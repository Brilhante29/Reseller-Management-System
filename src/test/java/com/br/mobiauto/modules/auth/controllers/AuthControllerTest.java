package com.br.mobiauto.modules.auth.controllers;

import com.br.mobiauto.modules.auth.dtos.AuthRequestDTO;
import com.br.mobiauto.modules.auth.dtos.AuthResponseDTO;
import com.br.mobiauto.modules.auth.dtos.RegisterRequestDTO;
import com.br.mobiauto.modules.auth.services.AuthService;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.utils.TestUtils;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "spring.config.location=classpath:application-test.yml"
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDTO registerRequestDTO;
    private AuthRequestDTO authRequestDTO;
    private UserResponseDTO userResponseDTO;
    private AuthResponseDTO authResponseDTO;

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

        registerRequestDTO = RegisterRequestDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.MANAGER)
                .dealershipId("dealership-id")
                .build();

        authRequestDTO = AuthRequestDTO.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id("1")
                .name("John Doe")
                .email("john.doe@example.com")
                .role(Role.MANAGER)
                .build();

        authResponseDTO = AuthResponseDTO.builder()
                .token("jwtToken")
                .build();
    }

    @AfterAll
    public void tearDownAfterAll() {
        mongoImportProcess.close();
        mongoDProcess.close();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerUser_Success() throws Exception {
        Mockito.when(authService.register(Mockito.any(RegisterRequestDTO.class))).thenReturn(userResponseDTO);

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDTO))
                        .header("Authorization", TestUtils.generateToken(String.valueOf(UUID.randomUUID()), "secret")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void loginUser_Success() throws Exception {
        Mockito.when(authService.login(Mockito.any(AuthRequestDTO.class))).thenReturn(authResponseDTO);

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO))
                        .header("Authorization", TestUtils.generateToken(String.valueOf(UUID.randomUUID()), "secret")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerUser_Error() throws Exception {
        Mockito.when(authService.register(Mockito.any(RegisterRequestDTO.class))).thenThrow(new RuntimeException("User registration failed"));

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDTO))
                        .header("Authorization", TestUtils.generateToken(String.valueOf(UUID.randomUUID()), "secret")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User registration failed"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void loginUser_Error() throws Exception {
        Mockito.when(authService.login(Mockito.any(AuthRequestDTO.class))).thenThrow(new RuntimeException("Invalid credentials"));

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDTO))
                        .header("Authorization", TestUtils.generateToken(String.valueOf(UUID.randomUUID()), "secret")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
