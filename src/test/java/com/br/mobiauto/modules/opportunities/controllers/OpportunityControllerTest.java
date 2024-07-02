package com.br.mobiauto.modules.opportunities.controllers;

import com.br.mobiauto.modules.opportunities.dtos.OpportunityRequestDTO;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityResponseDTO;
import com.br.mobiauto.modules.opportunities.models.enums.OpportunityStatus;
import com.br.mobiauto.modules.opportunities.services.IOpportunityService;
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
class OpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IOpportunityService opportunityService;

    @Autowired
    private ObjectMapper objectMapper;

    private OpportunityResponseDTO opportunityResponseDTO;
    private OpportunityRequestDTO opportunityRequestDTO;

    private TransitionWalker.ReachedState<RunningMongodProcess> mongoDProcess;
    private TransitionWalker.ReachedState<ExecutedMongoImportProcess> mongoImportProcess;

    @BeforeAll
    public void setUp() {
        String os = System.getProperty("os.name");
        String usersPath = "src/test/resources/users.json";
        String dealershipsPath = "src/test/resources/dealerships.json";
        String opportunitiesPath = "src/test/resources/opportunities.json";

        if (os != null && os.toLowerCase().contains("windows")) {
            usersPath = usersPath.substring(1);
            dealershipsPath = dealershipsPath.substring(1);
            opportunitiesPath = opportunitiesPath.substring(1);
        }

        MongoImportArguments usersArguments = MongoImportArguments.builder()
                .databaseName("mobiauto-test")
                .collectionName("users")
                .importFile(usersPath)
                .isJsonArray(true)
                .upsertDocuments(true)
                .build();

        MongoImportArguments dealershipsArguments = MongoImportArguments.builder()
                .databaseName("mobiauto-test")
                .collectionName("dealerships")
                .importFile(dealershipsPath)
                .isJsonArray(true)
                .upsertDocuments(true)
                .build();

        MongoImportArguments opportunitiesArguments = MongoImportArguments.builder()
                .databaseName("mobiauto-test")
                .collectionName("opportunities")
                .importFile(opportunitiesPath)
                .isJsonArray(true)
                .upsertDocuments(true)
                .build();

        mongoDProcess = Mongod.builder()
                .net(Start.to(Net.class).initializedWith(Net.defaults().withPort(27017)))
                .build()
                .start(Version.Main.V6_0);

        Transitions mongoImportTransitions = MongoImport.instance()
                .transitions(Version.Main.V6_0)
                .replace(Start.to(MongoImportArguments.class).initializedWith(usersArguments))
                .addAll(Start.to(ServerAddress.class).initializedWith(mongoDProcess.current().getServerAddress()));

        mongoImportProcess = mongoImportTransitions.walker().initState(StateID.of(ExecutedMongoImportProcess.class));

        mongoImportTransitions = MongoImport.instance()
                .transitions(Version.Main.V6_0)
                .replace(Start.to(MongoImportArguments.class).initializedWith(dealershipsArguments))
                .addAll(Start.to(ServerAddress.class).initializedWith(mongoDProcess.current().getServerAddress()));

        mongoImportProcess = mongoImportTransitions.walker().initState(StateID.of(ExecutedMongoImportProcess.class));

        mongoImportTransitions = MongoImport.instance()
                .transitions(Version.Main.V6_0)
                .replace(Start.to(MongoImportArguments.class).initializedWith(opportunitiesArguments))
                .addAll(Start.to(ServerAddress.class).initializedWith(mongoDProcess.current().getServerAddress()));

        mongoImportProcess = mongoImportTransitions.walker().initState(StateID.of(ExecutedMongoImportProcess.class));

        opportunityRequestDTO = OpportunityRequestDTO.builder()
                .name("Opportunity One")
                .email("client1@example.com")
                .phone("1234567890")
                .brand("BrandA")
                .model("ModelA")
                .version("VersionA")
                .yearModel(2022)
                .build();

        opportunityResponseDTO = OpportunityResponseDTO.builder()
                .id("1")
                .name("Opportunity One")
                .email("client1@example.com")
                .phone("1234567890")
                .brand("BrandA")
                .model("ModelA")
                .version("VersionA")
                .yearModel(2022)
                .status(OpportunityStatus.NEW)
                .build();
    }

    @AfterAll
    public void tearDownAfterAll() {
        mongoImportProcess.close();
        mongoDProcess.close();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOpportunities() throws Exception {
        Mockito.when(opportunityService.getAllOpportunities()).thenReturn(List.of(opportunityResponseDTO));

        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Opportunity One")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOpportunityById() throws Exception {
        Mockito.when(opportunityService.getOpportunityById("1")).thenReturn(opportunityResponseDTO);

        mockMvc.perform(get("/api/opportunities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Opportunity One")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOpportunity() throws Exception {
        Mockito.when(opportunityService.createOpportunity(any(OpportunityRequestDTO.class))).thenReturn(opportunityResponseDTO);

        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opportunityRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Opportunity One")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOpportunity() throws Exception {
        Mockito.when(opportunityService.updateOpportunity(Mockito.eq("1"), any(OpportunityRequestDTO.class))).thenReturn(opportunityResponseDTO);

        mockMvc.perform(put("/api/opportunities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opportunityRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Opportunity One")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOpportunity() throws Exception {
        mockMvc.perform(delete("/api/opportunities/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignOpportunity() throws Exception {
        // Implement the test for assigning an opportunity
    }
}
