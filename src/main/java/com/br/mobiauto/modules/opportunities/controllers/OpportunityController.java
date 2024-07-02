package com.br.mobiauto.modules.opportunities.controllers;

import com.br.mobiauto.modules.opportunities.dtos.OpportunityRequestDTO;
import com.br.mobiauto.modules.opportunities.dtos.OpportunityResponseDTO;
import com.br.mobiauto.modules.opportunities.services.IOpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get all opportunities", description = "Retrieve all opportunities")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved opportunities",
                    content = @Content(schema = @Schema(implementation = OpportunityResponseDTO.class),
                            examples = @ExampleObject(value = "[{\"id\": \"1\", \"status\": \"NEW\", \"name\": \"Opportunity 1\", \"email\": \"client@example.com\", \"phone\": \"123456789\", \"brand\": \"Brand\", \"model\": \"Model\", \"version\": \"Version\", \"yearModel\": 2022}]")))
    })
    @GetMapping
    public ResponseEntity<List<OpportunityResponseDTO>> getAllOpportunities() {
        List<OpportunityResponseDTO> opportunities = opportunityService.getAllOpportunities();
        return new ResponseEntity<>(opportunities, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Get an opportunity by ID", description = "Retrieve an opportunity by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved opportunity",
                    content = @Content(schema = @Schema(implementation = OpportunityResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"status\": \"NEW\", \"name\": \"Opportunity 1\", \"email\": \"client@example.com\", \"phone\": \"123456789\", \"brand\": \"Brand\", \"model\": \"Model\", \"version\": \"Version\", \"yearModel\": 2022}"))),
            @ApiResponse(responseCode = "404", description = "Opportunity not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponseDTO> getOpportunityById(@PathVariable String id) {
        OpportunityResponseDTO opportunity = opportunityService.getOpportunityById(id);
        return new ResponseEntity<>(opportunity, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Create a new opportunity", description = "Create a new opportunity")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Opportunity successfully created",
                    content = @Content(schema = @Schema(implementation = OpportunityResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"status\": \"NEW\", \"name\": \"Opportunity 1\", \"email\": \"client@example.com\", \"phone\": \"123456789\", \"brand\": \"Brand\", \"model\": \"Model\", \"version\": \"Version\", \"yearModel\": 2022}"))),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<OpportunityResponseDTO> createOpportunity(
            @Valid @RequestBody OpportunityRequestDTO opportunityRequestDTO) {
        OpportunityResponseDTO opportunity = opportunityService.createOpportunity(opportunityRequestDTO);
        return new ResponseEntity<>(opportunity, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Update an existing opportunity", description = "Update an existing opportunity")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Opportunity successfully updated",
                    content = @Content(schema = @Schema(implementation = OpportunityResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"status\": \"NEW\", \"name\": \"Opportunity 1\", \"email\": \"client@example.com\", \"phone\": \"123456789\", \"brand\": \"Brand\", \"model\": \"Model\", \"version\": \"Version\", \"yearModel\": 2022}"))),
            @ApiResponse(responseCode = "404", description = "Opportunity not found"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponseDTO> updateOpportunity(
            @PathVariable String id,
            @Valid @RequestBody OpportunityRequestDTO opportunityRequestDTO) {
        OpportunityResponseDTO opportunity = opportunityService.updateOpportunity(id, opportunityRequestDTO);
        return new ResponseEntity<>(opportunity, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(summary = "Delete an opportunity by ID", description = "Delete an opportunity by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Opportunity successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Opportunity not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable String id) {
        opportunityService.deleteOpportunity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Assign an opportunity to an assistant", description = "Assign an opportunity to an assistant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Opportunity successfully assigned",
                    content = @Content(schema = @Schema(implementation = OpportunityResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"status\": \"NEW\", \"name\": \"Opportunity 1\", \"email\": \"client@example.com\", \"phone\": \"123456789\", \"brand\": \"Brand\", \"model\": \"Model\", \"version\": \"Version\", \"yearModel\": 2022}"))),
            @ApiResponse(responseCode = "404", description = "Opportunity or Assistant not found")
    })
    @PatchMapping("/{id}/assign")
    public ResponseEntity<OpportunityResponseDTO> assignOpportunity(
            @PathVariable String id,
            @RequestParam String assistantId) {
        OpportunityResponseDTO opportunity = opportunityService.assignOpportunity(id, assistantId);
        return new ResponseEntity<>(opportunity, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(summary = "Update the status of an opportunity", description = "Update the status of an opportunity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Opportunity status successfully updated",
                    content = @Content(schema = @Schema(implementation = OpportunityResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"status\": \"COMPLETED\", \"name\": \"Opportunity 1\", \"email\": \"client@example.com\", \"phone\": \"123456789\", \"brand\": \"Brand\", \"model\": \"Model\", \"version\": \"Version\", \"yearModel\": 2022, \"conclusionReason\": \"Client bought another car\"}"))),
            @ApiResponse(responseCode = "404", description = "Opportunity not found"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OpportunityResponseDTO> updateOpportunityStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam String conclusionReason) {
        OpportunityResponseDTO opportunity = opportunityService.updateOpportunityStatus(id, status, conclusionReason);
        return new ResponseEntity<>(opportunity, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Distribute unassigned opportunities to assistants", description = "Distribute unassigned opportunities to assistants")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Opportunities successfully distributed",
                    content = @Content(schema = @Schema(implementation = OpportunityResponseDTO.class),
                            examples = @ExampleObject(value = "[{\"id\": \"1\", \"status\": \"NEW\", \"name\": \"Opportunity 1\", \"email\": \"client@example.com\", \"phone\": \"123456789\", \"brand\": \"Brand\", \"model\": \"Model\", \"version\": \"Version\", \"yearModel\": 2022}]")))
    })
    @PostMapping("/distribute")
    public ResponseEntity<List<OpportunityResponseDTO>> distributeOpportunities() {
        List<OpportunityResponseDTO> opportunities = opportunityService.distributeOpportunities();
        return new ResponseEntity<>(opportunities, HttpStatus.OK);
    }
}
