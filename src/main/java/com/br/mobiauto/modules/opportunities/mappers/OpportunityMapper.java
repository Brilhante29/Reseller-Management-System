package com.br.mobiauto.modules.opportunities.mappers;

import com.br.mobiauto.modules.opportunities.dtos.OpportunityRequestDTO;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityResponseDTO;
import com.br.mobiauto.modules.opportunities.models.Opportunity;
import org.modelmapper.ModelMapper;

public class OpportunityMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static OpportunityResponseDTO toOpportunityResponseDTO(Opportunity opportunity) {
        return modelMapper.map(opportunity, OpportunityResponseDTO.class);
    }

    public static Opportunity toOpportunityEntity(OpportunityRequestDTO opportunityRequestDTO) {
        return modelMapper.map(opportunityRequestDTO, Opportunity.class);
    }

    public static void updateOpportunityEntity(OpportunityRequestDTO dto, Opportunity opportunity) {
        modelMapper.map(dto, opportunity);
    }
}
