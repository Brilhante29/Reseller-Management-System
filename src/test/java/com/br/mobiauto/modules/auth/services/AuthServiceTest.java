package com.br.mobiauto.modules.auth.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.br.mobiauto.exceptions.NotFoundException;
import com.br.mobiauto.modules.auth.dtos.AuthRequestDTO;
import com.br.mobiauto.modules.auth.dtos.AuthResponseDTO;
import com.br.mobiauto.modules.auth.dtos.RegisterRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.User;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.services.IUserService;
import com.br.mobiauto.security.providers.JWTProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private IUserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequestDTO;
    private AuthRequestDTO authRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        registerRequestDTO = RegisterRequestDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.MANAGER)
                .dealershipId("dealership-id")
                .build();

        authRequestDTO = AuthRequestDTO.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id("1")
                .name("John Doe")
                .email("john.doe@example.com")
                .role(Role.MANAGER)
                .build();
    }

    @Test
    void testRegister_Success() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.saveUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);
        UserResponseDTO result = authService.register(registerRequestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userService, times(1)).saveUser(any(UserRequestDTO.class));
    }

    @Test
    void testLogin_Success() {
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(userResponseDTO);
        when(jwtProvider.generateToken("john.doe@example.com", "MANAGER")).thenReturn("jwtToken");

        AuthResponseDTO result = authService.login(authRequestDTO);

        assertNotNull(result);
        assertEquals("jwtToken", result.getToken());

        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        verify(jwtProvider, times(1)).generateToken("john.doe@example.com", "MANAGER");
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(userService.getUserByEmail("john.doe@example.com")).thenThrow(new NotFoundException("Invalid email or password"));

        assertThrows(NotFoundException.class, () -> authService.login(authRequestDTO));

        verify(userService, times(1)).getUserByEmail("john.doe@example.com");
        verify(jwtProvider, never()).generateToken(anyString(), anyString());
    }

    @Test
    void testValidateToken() {
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(jwtProvider.validateToken("validToken")).thenReturn(decodedJWT);

        DecodedJWT result = authService.validateToken("validToken");

        assertNotNull(result);

        verify(jwtProvider, times(1)).validateToken("validToken");
    }
}
