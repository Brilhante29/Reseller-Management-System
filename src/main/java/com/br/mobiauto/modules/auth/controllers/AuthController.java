package com.br.mobiauto.modules.auth.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.br.mobiauto.modules.auth.dtos.AuthRequestDTO;
import com.br.mobiauto.modules.auth.dtos.AuthResponseDTO;
import com.br.mobiauto.modules.auth.dtos.RegisterRequestDTO;
import com.br.mobiauto.modules.auth.services.AuthService;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "User Login")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.login(authRequestDTO);
        return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "User Registration")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        UserResponseDTO userResponseDTO = authService.register(registerRequestDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<DecodedJWT> validateToken(@RequestParam String token) {
        DecodedJWT decodedJWT = authService.validateToken(token);
        return new ResponseEntity<>(decodedJWT, HttpStatus.OK);
    }
}
