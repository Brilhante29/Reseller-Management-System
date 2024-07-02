package com.br.mobiauto.modules.opportunities.controllers;

import com.br.mobiauto.modules.opportunities.dtos.OpportunityRequestDTO;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityResponseDTO;
import com.br.mobiauto.modules.opportunities.models.enums.OpportunityStatus;
import com.br.mobiauto.modules.opportunities.services.IOpportunityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OpportunityController.class)
class OpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IOpportunityService opportunityService;

    @Autowired
    private ObjectMapper objectMapper;

    private OpportunityResponseDTO opportunityResponseDTO;
    private OpportunityRequestDTO opportunityRequestDTO;

    @BeforeEach
    void setUp() {
        opportunityResponseDTO = OpportunityResponseDTO.builder()
                .id("1")
                .name("Opportunity 1")
                .email("client@example.com")
                .phone("123456789")
                .brand("Brand")
                .model("Model")
                .version("Version")
                .yearModel(2022)
                .status(OpportunityStatus.NEW)
                .build();
        opportunityRequestDTO = OpportunityRequestDTO.builder()
                .name("Opportunity 1")
                .email("client@example.com")
                .phone("123456789")
                .brand("Brand")
                .model("Model")
                .version("Version")
                .yearModel(2022)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOpportunities() throws Exception {
        when(opportunityService.getAllOpportunities()).thenReturn(List.of(opportunityResponseDTO));
        mockMvc.perform(get("/api/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Opportunity 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOpportunityById() throws Exception {
        when(opportunityService.getOpportunityById("1")).thenReturn(opportunityResponseDTO);
        mockMvc.perform(get("/api/opportunities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Opportunity 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOpportunity() throws Exception {
        when(opportunityService.createOpportunity(any(OpportunityRequestDTO.class))).thenReturn(opportunityResponseDTO);
        mockMvc.perform(post("/api/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opportunityRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Opportunity 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOpportunity() throws Exception {
        when(opportunityService.updateOpportunity(Mockito.eq("1"), any(OpportunityRequestDTO.class))).thenReturn(opportunityResponseDTO);
        mockMvc.perform(put("/api/opportunities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opportunityRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Opportunity 1")));
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
        when(opportunityService.assignOpportunity("1", "1")).thenReturn(opportunityResponseDTO);
        mockMvc.perform(patch("/api/opportunities/1/assign")
                        .param("assistantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Opportunity 1")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOpportunityStatus() throws Exception {
        when(opportunityService.updateOpportunityStatus("1", "COMPLETED", "Reason")).thenReturn(opportunityResponseDTO);
        mockMvc.perform(patch("/api/opportunities/1/status")
                        .param("status", "COMPLETED")
                        .param("conclusionReason", "Reason"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }
}
