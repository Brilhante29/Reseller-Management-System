package com.br.mobiauto.modules.dealerships.controllers;

import com.br.mobiauto.modules.dealerships.dtos.DealershipRequestDTO;
import com.br.mobiauto.modules.dealerships.dtos.DealershipResponseDTO;
import com.br.mobiauto.modules.dealerships.services.IDealershipService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "spring.config.location=classpath:application-test.yml"
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DealershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDealershipService dealershipService;

    @Autowired
    private ObjectMapper objectMapper;

    private DealershipResponseDTO dealershipResponseDTO;
    private DealershipRequestDTO dealershipRequestDTO;

    private TransitionWalker.ReachedState<RunningMongodProcess> mongoDProcess;
    private TransitionWalker.ReachedState<ExecutedMongoImportProcess> mongoImportProcess;

    @BeforeAll
    public void setUp() {
        String os = System.getProperty("os.name");
        String path = "src/test/resources/dealerships.json";

        if (os != null && os.toLowerCase().contains("windows")) {
            path = path.substring(1);
        }

        MongoImportArguments arguments = MongoImportArguments.builder()
                .databaseName("mobiauto-test")
                .collectionName("dealerships")
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

        dealershipResponseDTO = DealershipResponseDTO.builder()
                .id("1")
                .cnpj("12345678000190")
                .corporateName("Dealership Name")
                .build();
        dealershipRequestDTO = DealershipRequestDTO.builder()
                .cnpj("12345678000190")
                .corporateName("Dealership Name")
                .build();
    }

    @AfterAll
    public void tearDownAfterAll() {
        mongoImportProcess.close();
        mongoDProcess.close();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDealerships() throws Exception {
        Mockito.when(dealershipService.getAllDealerships()).thenReturn(List.of(dealershipResponseDTO));

        mockMvc.perform(get("/api/dealerships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].corporateName", is("Dealership Name")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDealershipById() throws Exception {
        Mockito.when(dealershipService.getDealershipById("1")).thenReturn(dealershipResponseDTO);

        mockMvc.perform(get("/api/dealerships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.corporateName", is("Dealership Name")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDealership() throws Exception {
        Mockito.when(dealershipService.createDealership(any(DealershipRequestDTO.class))).thenReturn(dealershipResponseDTO);

        mockMvc.perform(post("/api/dealerships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealershipRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.corporateName", is("Dealership Name")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDealership() throws Exception {
        Mockito.when(dealershipService.updateDealership(Mockito.eq("1"), any(DealershipRequestDTO.class))).thenReturn(dealershipResponseDTO);

        mockMvc.perform(put("/api/dealerships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealershipRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.corporateName", is("Dealership Name")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDealership() throws Exception {
        mockMvc.perform(delete("/api/dealerships/1"))
                .andExpect(status().isNoContent());
    }
}
