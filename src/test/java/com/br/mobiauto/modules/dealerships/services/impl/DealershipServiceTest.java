package com.br.mobiauto.modules.dealerships.services.impl;

import com.br.mobiauto.exceptions.ConflictException;
import com.br.mobiauto.exceptions.NotFoundException;
import com.br.mobiauto.modules.dealerships.dtos.DealershipRequestDTO;
import com.br.mobiauto.modules.dealerships.dtos.DealershipResponseDTO;
import com.br.mobiauto.modules.dealerships.mappers.DealershipMapper;
import com.br.mobiauto.modules.dealerships.models.Dealership;
import com.br.mobiauto.modules.dealerships.repositories.DealershipRepository;
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
public class DealershipServiceTest {

    @Mock
    private DealershipRepository dealershipRepository;

    @InjectMocks
    private DealershipService dealershipService;

    private DealershipRequestDTO dealershipRequestDTO;
    private Dealership dealership;

    @BeforeEach
    void setUp() {
        dealershipRequestDTO = DealershipRequestDTO.builder()
                .cnpj("12345678000190")
                .corporateName("Dealership Name")
                .build();

        dealership = Dealership.builder()
                .id("1")
                .cnpj("12345678000190")
                .corporateName("Dealership Name")
                .build();
    }

    @Test
    void testCreateDealership_Success() {
        when(dealershipRepository.findByCnpj("12345678000190")).thenReturn(Optional.empty());
        when(dealershipRepository.save(any(Dealership.class))).thenReturn(dealership);

        DealershipResponseDTO result = dealershipService.createDealership(dealershipRequestDTO);

        assertNotNull(result);
        assertEquals("12345678000190", result.getCnpj());
        assertEquals("Dealership Name", result.getCorporateName());
        verify(dealershipRepository, times(1)).save(any(Dealership.class));
    }

    @Test
    void testCreateDealership_Conflict() {
        when(dealershipRepository.findByCnpj("12345678000190")).thenReturn(Optional.of(dealership));

        assertThrows(ConflictException.class, () -> dealershipService.createDealership(dealershipRequestDTO));
        verify(dealershipRepository, never()).save(any(Dealership.class));
    }

    @Test
    void testGetDealershipById_Success() {
        when(dealershipRepository.findById("1")).thenReturn(Optional.of(dealership));

        DealershipResponseDTO result = dealershipService.getDealershipById("1");

        assertNotNull(result);
        assertEquals("12345678000190", result.getCnpj());
        assertEquals("Dealership Name", result.getCorporateName());
    }

    @Test
    void testGetDealershipById_NotFound() {
        when(dealershipRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> dealershipService.getDealershipById("1"));
    }

    @Test
    void testGetAllDealerships() {
        Dealership anotherDealership = Dealership.builder()
                .id("2")
                .cnpj("09876543210987")
                .corporateName("Another Dealership")
                .build();

        when(dealershipRepository.findAll()).thenReturn(List.of(dealership, anotherDealership));

        List<DealershipResponseDTO> result = dealershipService.getAllDealerships();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("12345678000190", result.get(0).getCnpj());
        assertEquals("09876543210987", result.get(1).getCnpj());
    }

    @Test
    void testUpdateDealership_Success() {
        when(dealershipRepository.findById("1")).thenReturn(Optional.of(dealership));
        when(dealershipRepository.save(any(Dealership.class))).thenReturn(dealership);

        dealershipRequestDTO.setCorporateName("Updated Dealership Name");

        DealershipResponseDTO result = dealershipService.updateDealership("1", dealershipRequestDTO);

        assertNotNull(result);
        assertEquals("12345678000190", result.getCnpj());
        assertEquals("Updated Dealership Name", result.getCorporateName());
    }

    @Test
    void testUpdateDealership_NotFound() {
        when(dealershipRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> dealershipService.updateDealership("1", dealershipRequestDTO));
    }

    @Test
    void testUpdateDealership_Conflict() {
        Dealership anotherDealership = Dealership.builder()
                .id("2")
                .cnpj("09876543210987")
                .corporateName("Another Dealership")
                .build();

        when(dealershipRepository.findById("1")).thenReturn(Optional.of(dealership));
        when(dealershipRepository.findByCnpj("09876543210987")).thenReturn(Optional.of(anotherDealership));

        dealershipRequestDTO.setCnpj("09876543210987");

        assertThrows(ConflictException.class, () -> dealershipService.updateDealership("1", dealershipRequestDTO));
    }

    @Test
    void testDeleteDealership_Success() {
        when(dealershipRepository.findById("1")).thenReturn(Optional.of(dealership));
        doNothing().when(dealershipRepository).delete(dealership);

        assertDoesNotThrow(() -> dealershipService.deleteDealership("1"));
        verify(dealershipRepository, times(1)).delete(dealership);
    }

    @Test
    void testDeleteDealership_NotFound() {
        when(dealershipRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> dealershipService.deleteDealership("1"));
        verify(dealershipRepository, never()).delete(any(Dealership.class));
    }
}
