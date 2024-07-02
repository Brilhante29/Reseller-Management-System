package com.br.mobiauto.modules.opportunities.services;

import com.br.mobiauto.modules.opportunities.dtos.OpportunityRequestDTO;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityResponseDTO;

import java.util.List;

public interface IOpportunityService {
    List<OpportunityResponseDTO> getAllOpportunities();

    OpportunityResponseDTO getOpportunityById(String id);

    OpportunityResponseDTO createOpportunity(OpportunityRequestDTO opportunityRequestDTO);

    OpportunityResponseDTO updateOpportunity(String id, OpportunityRequestDTO opportunityRequestDTO);

    void deleteOpportunity(String id);

    List<OpportunityResponseDTO> distributeOpportunities();

    OpportunityResponseDTO assignOpportunity(String id, String assistantId);

    OpportunityResponseDTO updateOpportunityStatus(String id, String status, String conclusionReason);
}
