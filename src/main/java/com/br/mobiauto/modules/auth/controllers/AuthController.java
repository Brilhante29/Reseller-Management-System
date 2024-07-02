package com.br.mobiauto.modules.auth.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.br.mobiauto.modules.auth.dtos.AuthRequestDTO;
import com.br.mobiauto.modules.auth.dtos.AuthResponseDTO;
import com.br.mobiauto.modules.auth.dtos.RegisterRequestDTO;
import com.br.mobiauto.modules.auth.services.AuthService;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and Authorization")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User Login", description = "Authenticate a user and return a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged in",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class),
                            examples = @ExampleObject(value = "{\"token\": \"jwtToken\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.login(authRequestDTO);
        return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "User Registration", description = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": \"1\", \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"role\": \"MANAGER\"}"))),
            @ApiResponse(responseCode = "409", description = "Conflict: Email already in use"),
            @ApiResponse(responseCode = "422", description = "Validation error")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        UserResponseDTO userResponseDTO = authService.register(registerRequestDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Validate Token", description = "Validate a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid",
                    content = @Content(schema = @Schema(implementation = DecodedJWT.class),
                            examples = @ExampleObject(value = "{\"sub\": \"user@example.com\", \"role\": \"ROLE_USER\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token")
    })
    @GetMapping("/validate")
    public ResponseEntity<DecodedJWT> validateToken(@RequestParam String token) {
        DecodedJWT decodedJWT = authService.validateToken(token);
        return new ResponseEntity<>(decodedJWT, HttpStatus.OK);
    }
}
