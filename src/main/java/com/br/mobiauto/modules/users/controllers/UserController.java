package com.br.mobiauto.modules.users.controllers;

import com.br.mobiauto.exceptions.dtos.ErrorResponseDTO;
import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "Operations related to users")
public class UserController {

    private final IUserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @GetMapping("/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user details by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\":\"1\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"role\":\"MANAGER\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"User not found\",\"timestamp\":\"2023-07-02T12:00:00\"}")))
    })
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\":\"1\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"role\":\"MANAGER\"}"))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":422,\"message\":\"Validation Error\",\"timestamp\":\"2023-07-02T12:00:00\",\"errors\":{\"email\":\"Email should be valid\",\"password\":\"Password is required\"}}")))
    })
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.saveUser(userRequestDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PutMapping("/{email}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\":\"1\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"role\":\"MANAGER\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"User not found\",\"timestamp\":\"2023-07-02T12:00:00\"}"))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":422,\"message\":\"Validation Error\",\"timestamp\":\"2023-07-02T12:00:00\",\"errors\":{\"email\":\"Email should be valid\",\"password\":\"Password is required\"}}")))
    })
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String email, @RequestBody @Valid UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.updateUser(email, userRequestDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{email}")
    @Operation(summary = "Delete user", description = "Delete an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"User not found\",\"timestamp\":\"2023-07-02T12:00:00\"}")))
    })
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PatchMapping("/{email}/role")
    @Operation(summary = "Update user role", description = "Update the role of an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User role updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\":\"1\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"role\":\"MANAGER\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"User not found\",\"timestamp\":\"2023-07-02T12:00:00\"}")))
    })
    public UserResponseDTO updateUserRole(@PathVariable String email, @RequestParam Role role) {
        return userService.updateUserRole(email, role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)),
                            examples = @ExampleObject(value = "[{\"id\":\"1\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"role\":\"MANAGER\"}]")))
    })
    public List<UserResponseDTO> getUsers() {
        return userService.getUsers();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('MANAGER')")
    @GetMapping("/dealership/{dealershipId}")
    @Operation(summary = "Get users by dealership", description = "Retrieve users by dealership ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)),
                            examples = @ExampleObject(value = "[{\"id\":\"1\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"role\":\"MANAGER\"}]"))),
            @ApiResponse(responseCode = "404", description = "Dealership not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"Dealership not found\",\"timestamp\":\"2023-07-02T12:00:00\"}")))
    })
    public List<UserResponseDTO> getUsersByDealership(@PathVariable String dealershipId) {
        return userService.getUsersByDealership(dealershipId);
    }
}
