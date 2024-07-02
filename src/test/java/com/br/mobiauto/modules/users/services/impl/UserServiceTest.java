package com.br.mobiauto.modules.users.services.impl;

import com.br.mobiauto.exceptions.ConflictException;
import com.br.mobiauto.exceptions.NotFoundException;
import com.br.mobiauto.modules.dealerships.models.Dealership;
import com.br.mobiauto.modules.dealerships.repositories.DealershipRepository;
import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.mappers.UserMapper;
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
    private DealershipRepository dealershipRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequestDTO;
    private User user;
    private Dealership dealership;

    @BeforeEach
    void setUp() {
        dealership = Dealership.builder()
                .id("dealership-id")
                .cnpj("12.345.678/0001-90")
                .corporateName("Dealership Name")
                .build();

        userRequestDTO = UserRequestDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.MANAGER)
                .dealershipId(dealership.getId())
                .build();

        user = User.builder()
                .id("1")
                .name("John Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .dealership(dealership)
                .build();
    }

    @Test
    void testGetUserByEmail_UserExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserByEmail("john.doe@example.com"));
    }

    @Test
    void testSaveUser_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(dealershipRepository.findById("dealership-id")).thenReturn(Optional.of(dealership));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User mappedUser = UserMapper.toUserEntity(userRequestDTO);
        mappedUser.setDealership(dealership);
        when(userRepository.save(any(User.class))).thenReturn(mappedUser);

        UserResponseDTO result = userService.saveUser(userRequestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testSaveUser_EmailConflict() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.saveUser(userRequestDTO));
    }

    @Test
    void testUpdateUserRole_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        user.setRole(Role.MANAGER);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.updateUserRole("john.doe@example.com", Role.MANAGER);

        assertNotNull(result);
        assertEquals(Role.MANAGER, result.getRole());
    }

    @Test
    void testUpdateUserRole_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUserRole("john.doe@example.com", Role.MANAGER));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser("john.doe@example.com"));
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser("john.doe@example.com"));
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(dealershipRepository.findById("dealership-id")).thenReturn(Optional.of(dealership));
        when(userRepository.save(any(User.class))).thenReturn(user);
        userRequestDTO.setName("Updated Name");

        UserResponseDTO result = userService.updateUser("john.doe@example.com", userRequestDTO);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser("john.doe@example.com", userRequestDTO));
    }

    @Test
    void testUpdateUser_EmailConflict() {
        User anotherUser = User.builder()
                .id("2")
                .email("new.email@example.com")
                .build();
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new.email@example.com")).thenReturn(Optional.of(anotherUser));

        userRequestDTO.setEmail("new.email@example.com");

        assertThrows(ConflictException.class, () -> userService.updateUser("john.doe@example.com", userRequestDTO));
    }

    @Test
    void testUpdateUser_PartialUpdate() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(dealershipRepository.findById("dealership-id")).thenReturn(Optional.of(dealership));
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

        List<UserResponseDTO> users = userService.getUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("Jane Doe", users.get(1).getName());
    }

    @Test
    void testGetUsersByDealership() {
        User anotherUser = User.builder()
                .id("2")
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .password("encodedPassword")
                .role(Role.ASSISTANT)
                .dealership(dealership)
                .build();
        when(userRepository.findAllByDealershipId("dealership-id")).thenReturn(List.of(user, anotherUser));

        List<UserResponseDTO> users = userService.getUsersByDealership("dealership-id");

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("Jane Doe", users.get(1).getName());
    }
}
