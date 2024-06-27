package com.br.mobiauto.modules.users.services.impl;

import com.br.mobiauto.exceptions.ConflictException;
import com.br.mobiauto.exceptions.NotFoundException;
import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.User;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userRequestDTO = UserRequestDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.MANAGER)
                .build();

        user = User.builder()
                .id("1")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void testGetUserByEmail_UserExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user);

        UserResponseDTO result = userService.getUserByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.getUserByEmail("john.doe@example.com"));
    }

    @Test
    void testSaveUser_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.saveUser(userRequestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testSaveUser_EmailConflict() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user);

        assertThrows(ConflictException.class, () -> userService.saveUser(userRequestDTO));
    }

    @Test
    void testUpdateUserRole_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user);
        user.setRole(Role.MANAGER);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.updateUserRole("john.doe@example.com", Role.MANAGER);

        assertNotNull(result);
        assertEquals(Role.MANAGER, result.getRole());
    }

    @Test
    void testUpdateUserRole_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.updateUserRole("john.doe@example.com", Role.MANAGER));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user);
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser("john.doe@example.com"));
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.deleteUser("john.doe@example.com"));
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        userRequestDTO.setName("Updated Name");

        UserResponseDTO result = userService.updateUser("john.doe@example.com", userRequestDTO);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.updateUser("john.doe@example.com", userRequestDTO));
    }

    @Test
    void testUpdateUser_EmailConflict() {
        User anotherUser = User.builder()
                .id("2")
                .email("new.email@example.com")
                .build();
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user);
        when(userRepository.findByEmail("new.email@example.com")).thenReturn(anotherUser);

        userRequestDTO.setEmail("new.email@example.com");

        assertThrows(ConflictException.class, () -> userService.updateUser("john.doe@example.com", userRequestDTO));
    }

    @Test
    void testUpdateUser_PartialUpdate() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userRequestDTO.setName(null); // Simulate partial update with only email
        userRequestDTO.setEmail("updated.email@example.com");

        UserResponseDTO result = userService.updateUser("john.doe@example.com", userRequestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("updated.email@example.com", result.getEmail());
    }

    @Test
    void testGetUsers() {
        User anotherUser = User.builder()
                .id("2")
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .password("encodedPassword")
                .role(Role.ASSISTANT)
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user, anotherUser));

        var users = userService.getUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("Jane Doe", users.get(1).getName());
    }
}
