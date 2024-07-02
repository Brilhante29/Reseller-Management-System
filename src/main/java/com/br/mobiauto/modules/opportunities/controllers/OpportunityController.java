package com.br.mobiauto.modules.opportunities.controllers;

import com.br.mobiauto.modules.opportunities.dtos.OpportunityRequestDTO;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityResponseDTO;
import com.br.mobiauto.modules.opportunities.services.IOpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
@RequiredArgsConstructor
@Tag(name = "Opportunities", description = "Management of Opportunities")
public class OpportunityController {

    private final IOpportunityService opportunityService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Get all opportunities")
    @GetMapping
    public ResponseEntity<List<OpportunityResponseDTO>> getAllOpportunities() {
        List<OpportunityResponseDTO> opportunities = opportunityService.getAllOpportunities();
        return new ResponseEntity<>(opportunities, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Get an opportunity by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponseDTO> getOpportunityById(@PathVariable String id) {
        OpportunityResponseDTO opportunity = opportunityService.getOpportunityById(id);
        return new ResponseEntity<>(opportunity, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Create a new opportunity")
    @PostMapping
    public ResponseEntity<OpportunityResponseDTO> createOpportunity(
            @Valid @RequestBody OpportunityRequestDTO opportunityRequestDTO) {
        OpportunityResponseDTO opportunity = opportunityService.createOpportunity(opportunityRequestDTO);
        return new ResponseEntity<>(opportunity, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Update an existing opportunity")
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponseDTO> updateOpportunity(
            @PathVariable String id,
            @Valid @RequestBody OpportunityRequestDTO opportunityRequestDTO) {
        OpportunityResponseDTO opportunity = opportunityService.updateOpportunity(id, opportunityRequestDTO);
        return new ResponseEntity<>(opportunity, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(summary = "Delete an opportunity by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable String id) {
        opportunityService.deleteOpportunity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Assign an opportunity to an assistant")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<OpportunityResponseDTO> assignOpportunity(
            @PathVariable String id,
            @RequestParam String assistantId) {
        OpportunityResponseDTO opportunity = opportunityService.assignOpportunity(id, assistantId);
        return new ResponseEntity<>(opportunity, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Update the status of an opportunity")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OpportunityResponseDTO> updateOpportunityStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam String conclusionReason) {
        OpportunityResponseDTO opportunity = opportunityService.updateOpportunityStatus(id, status, conclusionReason);
        return new ResponseEntity<>(opportunity, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Distribute unassigned opportunities to assistants")
    @PostMapping("/distribute")
    public ResponseEntity<List<OpportunityResponseDTO>> distributeOpportunities() {
        List<OpportunityResponseDTO> opportunities = opportunityService.distributeOpportunities();
        return new ResponseEntity<>(opportunities, HttpStatus.OK);
    }
}
