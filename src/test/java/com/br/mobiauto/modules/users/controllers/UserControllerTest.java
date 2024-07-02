package com.br.mobiauto.modules.users.controllers;

import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.services.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.flapdoodle.embed.mongo.commands.MongoImportArguments;
import de.flapdoodle.embed.mongo.commands.ServerAddress;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.ExecutedMongoImportProcess;
import de.flapdoodle.embed.mongo.transitions.MongoImport;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.Transitions;
import de.flapdoodle.reverse.transitions.Start;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "spring.config.location=classpath:application-test.yml"
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;

    private TransitionWalker.ReachedState<RunningMongodProcess> mongoDProcess;
    private TransitionWalker.ReachedState<ExecutedMongoImportProcess> mongoImportProcess;

    @BeforeAll
    public void setUp() {
        String os = System.getProperty("os.name");
        String path = "src/test/resources/users.json";

        if (os != null && os.toLowerCase().contains("windows")) {
            path = path.substring(1);
        }

        MongoImportArguments arguments = MongoImportArguments.builder()
                .databaseName("mobiauto-test")
                .collectionName("users")
                .importFile(path)
                .isJsonArray(true)
                .upsertDocuments(true)
                .build();

        mongoDProcess = Mongod.builder()
                .net(Start.to(Net.class).initializedWith(Net.defaults().withPort(27017)))
                .build()
                .start(Version.Main.V6_0);

        Transitions mongoImportTransitions = MongoImport.instance()
                .transitions(Version.Main.V6_0)
                .replace(Start.to(MongoImportArguments.class).initializedWith(arguments))
                .addAll(Start.to(ServerAddress.class).initializedWith(mongoDProcess.current().getServerAddress()));

        mongoImportProcess = mongoImportTransitions.walker().initState(StateID.of(ExecutedMongoImportProcess.class));

        userResponseDTO = UserResponseDTO.builder()
                .id("1")
                .email("john.doe@example.com")
                .name("John Doe")
                .role(Role.ASSISTANT)
                .build();
        userRequestDTO = UserRequestDTO.builder()
                .email("john.doe@example.com")
                .name("John Doe")
                .password("password")
                .role(Role.ASSISTANT)
                .dealershipId("1")
                .build();
    }

    @AfterAll
    public void tearDownAfterAll() {
        mongoImportProcess.close();
        mongoDProcess.close();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByEmail() throws Exception {
        Mockito.when(userService.getUserByEmail("john.doe@example.com")).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser() throws Exception {
        Mockito.when(userService.saveUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser() throws Exception {
        Mockito.when(userService.updateUser(Mockito.eq("john.doe@example.com"), any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/users/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/john.doe@example.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole() throws Exception {
        Mockito.when(userService.updateUserRole("john.doe@example.com", Role.MANAGER)).thenReturn(userResponseDTO);

        mockMvc.perform(patch("/api/users/john.doe@example.com/role")
                        .param("role", "MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("ASSISTANT")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers() throws Exception {
        Mockito.when(userService.getUsers()).thenReturn(List.of(userResponseDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersByDealership() throws Exception {
        Mockito.when(userService.getUsersByDealership("1")).thenReturn(List.of(userResponseDTO));

        mockMvc.perform(get("/api/users/dealership/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")));
    }
}
