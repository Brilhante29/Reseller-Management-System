package com.br.mobiauto.modules.dealerships.controllers;

import com.br.mobiauto.modules.dealerships.dtos.DealershipRequestDTO;
import com.br.mobiauto.modules.dealerships.dtos.DealershipResponseDTO;
import com.br.mobiauto.modules.dealerships.services.IDealershipService;
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

@WebMvcTest(DealershipController.class)
class DealershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDealershipService dealershipService;

    @Autowired
    private ObjectMapper objectMapper;

    private DealershipResponseDTO dealershipResponseDTO;
    private DealershipRequestDTO dealershipRequestDTO;

    @BeforeEach
    void setUp() {
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDealerships() throws Exception {
        when(dealershipService.getAllDealerships()).thenReturn(List.of(dealershipResponseDTO));
        mockMvc.perform(get("/api/dealerships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].corporateName", is("Dealership Name")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDealershipById() throws Exception {
        when(dealershipService.getDealershipById("1")).thenReturn(dealershipResponseDTO);
        mockMvc.perform(get("/api/dealerships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.corporateName", is("Dealership Name")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDealership() throws Exception {
        when(dealershipService.createDealership(any(DealershipRequestDTO.class))).thenReturn(dealershipResponseDTO);
        mockMvc.perform(post("/api/dealerships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealershipRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.corporateName", is("Dealership Name")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDealership() throws Exception {
        when(dealershipService.updateDealership(Mockito.eq("1"), any(DealershipRequestDTO.class))).thenReturn(dealershipResponseDTO);
        mockMvc.perform(put("/api/dealerships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealershipRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.corporateName", is("Dealership Name")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDealership() throws Exception {
        mockMvc.perform(delete("/api/dealerships/1"))
                .andExpect(status().isNoContent());
    }
}
