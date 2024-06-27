package com.br.mobiauto.modules.dealerships.services.impl;

import com.br.mobiauto.exceptions.ConflictException;
import com.br.mobiauto.exceptions.NotFoundException;
import com.br.mobiauto.modules.dealerships.dtos.DealershipRequestDTO;
import com.br.mobiauto.modules.dealerships.dtos.DealershipResponseDTO;
import com.br.mobiauto.modules.dealerships.mappers.DealershipMapper;
import com.br.mobiauto.modules.dealerships.models.Dealership;
import com.br.mobiauto.modules.dealerships.repositories.DealershipRepository;
import com.br.mobiauto.modules.dealerships.services.IDealershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealershipService implements IDealershipService {

    private final DealershipRepository dealershipRepository;

    @Override
    public DealershipResponseDTO createDealership(DealershipRequestDTO dealershipRequestDTO) {
        if (dealershipRepository.findByCnpj(dealershipRequestDTO.getCnpj()).isPresent()) {
            throw new ConflictException("CNPJ is already in use");
        }
        Dealership dealership = DealershipMapper.toDealershipEntity(dealershipRequestDTO);
        Dealership savedDealership = dealershipRepository.save(dealership);
        return DealershipMapper.toDealershipResponseDTO(savedDealership);
    }

    @Override
    public DealershipResponseDTO getDealershipById(String id) {
        Dealership dealership = dealershipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dealership not found"));
        return DealershipMapper.toDealershipResponseDTO(dealership);
    }

    @Override
    public List<DealershipResponseDTO> getAllDealerships() {
        return dealershipRepository.findAll().stream()
                .map(DealershipMapper::toDealershipResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DealershipResponseDTO updateDealership(String id, DealershipRequestDTO dealershipRequestDTO) {
        Dealership dealership = dealershipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dealership not found"));

        if (!dealership.getCnpj().equals(dealershipRequestDTO.getCnpj()) &&
                dealershipRepository.findByCnpj(dealershipRequestDTO.getCnpj()).isPresent()) {
            throw new ConflictException("CNPJ is already in use");
        }

        dealership.setCnpj(dealershipRequestDTO.getCnpj());
        dealership.setCorporateName(dealershipRequestDTO.getCorporateName());

        Dealership updatedDealership = dealershipRepository.save(dealership);
        return DealershipMapper.toDealershipResponseDTO(updatedDealership);
    }

    @Override
    public void deleteDealership(String id) {
        Dealership dealership = dealershipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dealership not found"));
        dealershipRepository.delete(dealership);
    }
}
