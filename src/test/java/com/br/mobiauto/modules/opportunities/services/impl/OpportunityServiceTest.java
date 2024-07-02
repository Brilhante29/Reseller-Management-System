package com.br.mobiauto.modules.opportunities.services.impl;

import com.br.mobiauto.exceptions.NotFoundException;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityRequestDTO;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityResponseDTO;
import com.br.mobiauto.modules.opportunities.models.Opportunity;
import com.br.mobiauto.modules.opportunities.models.enums.OpportunityStatus;
import com.br.mobiauto.modules.opportunities.repositories.OpportunityRepository;
import com.br.mobiauto.modules.users.models.User;
import com.br.mobiauto.modules.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpportunityServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OpportunityService opportunityService;

    private OpportunityRequestDTO opportunityRequestDTO;
    private Opportunity opportunity;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user-id")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password")
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

        opportunity = Opportunity.builder()
                .id("1")
                .name("Opportunity 1")
                .email("client@example.com")
                .phone("123456789")
                .brand("Brand")
                .model("Model")
                .version("Version")
                .yearModel(2022)
                .status(OpportunityStatus.NEW)
                .user(user)
                .build();
    }

    @Test
    void testGetAllOpportunities() {
        Opportunity anotherOpportunity = Opportunity.builder()
                .id("2")
                .name("Opportunity 2")
                .email("client2@example.com")
                .phone("987654321")
                .brand("Another Brand")
                .model("Another Model")
                .version("Another Version")
                .yearModel(2023)
                .status(OpportunityStatus.NEW)
                .build();

        when(opportunityRepository.findAll()).thenReturn(List.of(opportunity, anotherOpportunity));

        List<OpportunityResponseDTO> result = opportunityService.getAllOpportunities();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Opportunity 1", result.get(0).getName());
        assertEquals("Opportunity 2", result.get(1).getName());
    }

    @Test
    void testGetOpportunityById_Success() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.of(opportunity));

        OpportunityResponseDTO result = opportunityService.getOpportunityById("1");

        assertNotNull(result);
        assertEquals("Opportunity 1", result.getName());
    }

    @Test
    void testGetOpportunityById_NotFound() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> opportunityService.getOpportunityById("1"));
    }

    @Test
    void testCreateOpportunity_Success() {
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

        OpportunityResponseDTO result = opportunityService.createOpportunity(opportunityRequestDTO);

        assertNotNull(result);
        assertEquals("Opportunity 1", result.getName());
    }

    @Test
    void testUpdateOpportunity_Success() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.of(opportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

        opportunityRequestDTO.setName("Updated Opportunity");

        OpportunityResponseDTO result = opportunityService.updateOpportunity("1", opportunityRequestDTO);

        assertNotNull(result);
        assertEquals("Updated Opportunity", result.getName());
    }

    @Test
    void testUpdateOpportunity_NotFound() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> opportunityService.updateOpportunity("1", opportunityRequestDTO));
    }

    @Test
    void testDeleteOpportunity_Success() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.of(opportunity));
        doNothing().when(opportunityRepository).delete(opportunity);

        assertDoesNotThrow(() -> opportunityService.deleteOpportunity("1"));
        verify(opportunityRepository, times(1)).delete(opportunity);
    }

    @Test
    void testDeleteOpportunity_NotFound() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> opportunityService.deleteOpportunity("1"));
    }

    @Test
    void testAssignOpportunity_Success() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.of(opportunity));
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

        OpportunityResponseDTO result = opportunityService.assignOpportunity("1", "user-id");

        assertNotNull(result);
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testAssignOpportunity_OpportunityNotFound() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> opportunityService.assignOpportunity("1", "user-id"));
    }

    @Test
    void testAssignOpportunity_UserNotFound() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.of(opportunity));
        when(userRepository.findById("user-id")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> opportunityService.assignOpportunity("1", "user-id"));
    }

    @Test
    void testUpdateOpportunityStatus_Success() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.of(opportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

        OpportunityResponseDTO result = opportunityService.updateOpportunityStatus("1", "COMPLETED", "Reason");

        assertNotNull(result);
        assertEquals(OpportunityStatus.COMPLETED, result.getStatus());
    }

    @Test
    void testUpdateOpportunityStatus_NotFound() {
        when(opportunityRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> opportunityService.updateOpportunityStatus("1", "COMPLETED", "Reason"));
    }
}
