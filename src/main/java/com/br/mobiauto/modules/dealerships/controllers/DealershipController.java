package com.br.mobiauto.modules.dealerships.controllers;

import com.br.mobiauto.modules.dealerships.dtos.DealershipRequestDTO;
import com.br.mobiauto.modules.dealerships.dtos.DealershipResponseDTO;
import com.br.mobiauto.modules.dealerships.services.IDealershipService;
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
@RequestMapping("/api/dealerships")
@RequiredArgsConstructor
@Tag(name = "Dealerships", description = "Management of Dealerships")
public class DealershipController {

    private final IDealershipService dealershipService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(summary = "Get all dealerships", description = "Retrieve all dealerships")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved dealerships",
                    content = @Content(schema = @Schema(implementation = DealershipResponseDTO.class),
                            examples = @ExampleObject(value = "[{\"id\": \"1\", \"cnpj\": \"12345678000190\", \"corporateName\": \"Dealership Name\"}]")))
    })
    @GetMapping
    public ResponseEntity<List<DealershipResponseDTO>> getAllDealerships() {
        List<DealershipResponseDTO> dealerships = dealershipService.getAllDealerships();
        return new ResponseEntity<>(dealerships, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(summary = "Get a dealership by ID", description = "Retrieve a dealership by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved dealership",
                    content = @Content(schema = @Schema(implementation = DealershipResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"cnpj\": \"12345678000190\", \"corporateName\": \"Dealership Name\"}"))),
            @ApiResponse(responseCode = "404", description = "Dealership not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DealershipResponseDTO> getDealershipById(@PathVariable String id) {
        DealershipResponseDTO dealership = dealershipService.getDealershipById(id);
        return new ResponseEntity<>(dealership, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new dealership", description = "Create a new dealership")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dealership successfully created",
                    content = @Content(schema = @Schema(implementation = DealershipResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"cnpj\": \"12345678000190\", \"corporateName\": \"Dealership Name\"}"))),
            @ApiResponse(responseCode = "409", description = "Conflict: CNPJ already in use"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<DealershipResponseDTO> createDealership(
            @Valid @RequestBody DealershipRequestDTO dealershipRequestDTO) {
        DealershipResponseDTO dealership = dealershipService.createDealership(dealershipRequestDTO);
        return new ResponseEntity<>(dealership, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing dealership", description = "Update an existing dealership")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dealership successfully updated",
                    content = @Content(schema = @Schema(implementation = DealershipResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"cnpj\": \"12345678000190\", \"corporateName\": \"Updated Dealership Name\"}"))),
            @ApiResponse(responseCode = "404", description = "Dealership not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: CNPJ already in use"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DealershipResponseDTO> updateDealership(
            @PathVariable String id,
            @Valid @RequestBody DealershipRequestDTO dealershipRequestDTO) {
        DealershipResponseDTO dealership = dealershipService.updateDealership(id, dealershipRequestDTO);
        return new ResponseEntity<>(dealership, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a dealership by ID", description = "Delete a dealership by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dealership successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Dealership not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealership(@PathVariable String id) {
        dealershipService.deleteDealership(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
