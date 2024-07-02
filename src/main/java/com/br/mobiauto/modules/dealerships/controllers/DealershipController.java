package com.br.mobiauto.modules.dealerships.controllers;

import com.br.mobiauto.modules.dealerships.dtos.DealershipRequestDTO;
import com.br.mobiauto.modules.dealerships.dtos.DealershipResponseDTO;
import com.br.mobiauto.modules.dealerships.services.IDealershipService;
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
@RequestMapping("/api/dealerships")
@RequiredArgsConstructor
@Tag(name = "Dealerships", description = "Management of Dealerships")
public class DealershipController {

    private final IDealershipService dealershipService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(summary = "Get all dealerships")
    @GetMapping
    public ResponseEntity<List<DealershipResponseDTO>> getAllDealerships() {
        List<DealershipResponseDTO> dealerships = dealershipService.getAllDealerships();
        return new ResponseEntity<>(dealerships, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(summary = "Get a dealership by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DealershipResponseDTO> getDealershipById(@PathVariable String id) {
        DealershipResponseDTO dealership = dealershipService.getDealershipById(id);
        return new ResponseEntity<>(dealership, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new dealership")
    @PostMapping
    public ResponseEntity<DealershipResponseDTO> createDealership(
            @Valid @RequestBody DealershipRequestDTO dealershipRequestDTO) {
        DealershipResponseDTO dealership = dealershipService.createDealership(dealershipRequestDTO);
        return new ResponseEntity<>(dealership, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing dealership")
    @PutMapping("/{id}")
    public ResponseEntity<DealershipResponseDTO> updateDealership(
            @PathVariable String id,
            @Valid @RequestBody DealershipRequestDTO dealershipRequestDTO) {
        DealershipResponseDTO dealership = dealershipService.updateDealership(
                id,
                dealershipRequestDTO);
        return new ResponseEntity<>(dealership, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a dealership by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealership(@PathVariable String id) {
        dealershipService.deleteDealership(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
