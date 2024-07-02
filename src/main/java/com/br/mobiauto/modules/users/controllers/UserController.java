package com.br.mobiauto.modules.users.controllers;

import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "Operations related to users")
public class UserController {

    private final IUserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @GetMapping("/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user details by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = UserResponseDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = UserResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public UserResponseDTO createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        return userService.saveUser(userRequestDTO);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PutMapping("/{email}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = UserResponseDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public UserResponseDTO updateUser(@PathVariable String email, @RequestBody @Valid UserRequestDTO userRequestDTO) {
        return userService.updateUser(email, userRequestDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{email}")
    @Operation(summary = "Delete user", description = "Delete an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PatchMapping("/{email}/role")
    @Operation(summary = "Update user role", description = "Update the role of an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = UserResponseDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponseDTO updateUserRole(@PathVariable String email, @RequestParam Role role) {
        return userService.updateUserRole(email, role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))
            })
    })
    public List<UserResponseDTO> getUsers() {
        return userService.getUsers();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @GetMapping("/dealership/{dealershipId}")
    @Operation(summary = "Get users by dealership", description = "Retrieve users by dealership ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))
            }),
            @ApiResponse(responseCode = "404", description = "Dealership not found")
    })
    public List<UserResponseDTO> getUsersByDealership(@PathVariable String dealershipId) {
        return userService.getUsersByDealership(dealershipId);
    }
}
