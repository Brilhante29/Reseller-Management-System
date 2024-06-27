package com.br.mobiauto.modules.dealerships.mappers;

import com.br.mobiauto.modules.dealerships.dtos.DealershipRequestDTO;
import com.br.mobiauto.modules.dealerships.dtos.DealershipResponseDTO;
import com.br.mobiauto.modules.dealerships.models.Dealership;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DealershipMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static DealershipResponseDTO toDealershipResponseDTO(Dealership dealership) {
        return modelMapper.map(dealership, DealershipResponseDTO.class);
    }

    public static Dealership toDealershipEntity(DealershipRequestDTO dealershipRequestDTO) {
        return modelMapper.map(dealershipRequestDTO, Dealership.class);
    }
}
