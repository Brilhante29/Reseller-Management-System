package com.br.mobiauto.modules.opportunities.services.impl;

import com.br.mobiauto.exceptions.NotFoundException;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityRequestDTO;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityResponseDTO;
import com.br.mobiauto.modules.opportunities.mappers.OpportunityMapper;
import com.br.mobiauto.modules.opportunities.models.Opportunity;
import com.br.mobiauto.modules.opportunities.models.enums.OpportunityStatus;
import com.br.mobiauto.modules.opportunities.repositories.OpportunityRepository;
import com.br.mobiauto.modules.opportunities.services.IOpportunityService;
import com.br.mobiauto.modules.users.models.User;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpportunityService implements IOpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    @Override
    public List<OpportunityResponseDTO> getAllOpportunities() {
        return opportunityRepository.findAll().stream()
                .map(OpportunityMapper::toOpportunityResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OpportunityResponseDTO getOpportunityById(String id) {
        return opportunityRepository.findById(id)
                .map(OpportunityMapper::toOpportunityResponseDTO)
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));
    }

    @Override
    public OpportunityResponseDTO createOpportunity(OpportunityRequestDTO opportunityRequestDTO) {
        Opportunity opportunity = OpportunityMapper.toOpportunityEntity(opportunityRequestDTO);
        opportunity.setStatus(OpportunityStatus.NEW);
        Opportunity savedOpportunity = opportunityRepository.save(opportunity);
        return OpportunityMapper.toOpportunityResponseDTO(savedOpportunity);
    }

    @Override
    public OpportunityResponseDTO updateOpportunity(String id, OpportunityRequestDTO opportunityRequestDTO) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));

        OpportunityMapper.updateOpportunityEntity(opportunityRequestDTO, opportunity);
        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);
        return OpportunityMapper.toOpportunityResponseDTO(updatedOpportunity);
    }

    @Override
    public void deleteOpportunity(String id) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));
        opportunityRepository.delete(opportunity);
    }

    @Override
    public OpportunityResponseDTO assignOpportunity(String id, String assistantId) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));

        User assistant = userRepository.findById(assistantId)
                .orElseThrow(() -> new NotFoundException("Assistant not found"));

        opportunity.setUser(assistant);
        opportunity.setAssignedDate(new Date());
        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);
        return OpportunityMapper.toOpportunityResponseDTO(updatedOpportunity);
    }

    @Override
    public OpportunityResponseDTO updateOpportunityStatus(String id, String status, String conclusionReason) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));

        OpportunityStatus newStatus = OpportunityStatus.valueOf(status.toUpperCase());
        opportunity.setStatus(newStatus);
        opportunity.setConclusionReason(conclusionReason);
        if (newStatus == OpportunityStatus.COMPLETED) {
            opportunity.setConclusionDate(new Date());
        }
        Opportunity updatedOpportunity = opportunityRepository.save(opportunity);
        return OpportunityMapper.toOpportunityResponseDTO(updatedOpportunity);
    }

    @Override
    public List<OpportunityResponseDTO> distributeOpportunities() {
        List<Opportunity> unassignedOpportunities = opportunityRepository.findAllByUserIsNull();
        List<User> assistants = userRepository.findAllByRole(Role.ASSISTANT);

        for (Opportunity opportunity : unassignedOpportunities) {
            User leastBusyAssistant = assistants.stream()
                    .sorted(Comparator.comparingInt(u -> getUserOpportunities(u.getId()).size()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("No assistants available"));

            opportunity.setUser(leastBusyAssistant);
            opportunity.setAssignedDate(new Date());
            opportunityRepository.save(opportunity);
        }

        return opportunityRepository.findAll().stream()
                .map(OpportunityMapper::toOpportunityResponseDTO)
                .collect(Collectors.toList());
    }

    private List<Opportunity> getUserOpportunities(String userId) {
        return opportunityRepository.findAllByUserId(userId);
    }
}
