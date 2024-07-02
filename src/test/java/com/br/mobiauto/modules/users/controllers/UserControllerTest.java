package com.br.mobiauto.modules.users.controllers;

import com.br.mobiauto.modules.auth.services.AuthService;
import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.services.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "spring.data.mongodb.uri=mongodb://localhost:27017/test")
@AutoConfigureMockMvc
@Import(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
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
                .andExpect(status().isOk())
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
