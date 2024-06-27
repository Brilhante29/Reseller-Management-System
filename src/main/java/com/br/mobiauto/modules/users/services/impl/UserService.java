package com.br.mobiauto.modules.users.services.impl;

import com.br.mobiauto.exceptions.ConflictException;
import com.br.mobiauto.exceptions.NotFoundException;
import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.User;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.mappers.UserMapper;
import com.br.mobiauto.modules.users.repositories.UserRepository;
import com.br.mobiauto.modules.users.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .map(UserMapper::toUserResponseDTO)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserResponseDTO saveUser(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.getEmail()) != null) {
            throw new ConflictException("Email is already in use");
        }

        User user = UserMapper.toUserEntity(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return UserMapper.toUserResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO updateUserRole(String email, Role role) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserResponseDTO(savedUser);
    }

    @Override
    public List<UserResponseDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(String email, UserRequestDTO userRequestDTO) {
        User user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new NotFoundException("User not found"));

        Optional.ofNullable(userRepository.findByEmail(userRequestDTO.getEmail()))
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    throw new ConflictException("Email is already in use");
                });

        Optional.ofNullable(userRequestDTO.getName()).ifPresent(user::setName);
        Optional.ofNullable(userRequestDTO.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(userRequestDTO.getPassword()).filter(password -> !password.isEmpty())
                .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
        Optional.ofNullable(userRequestDTO.getRole()).ifPresent(user::setRole);

        User updatedUser = userRepository.save(user);
        return UserMapper.toUserResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(String email) {
        User user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
    }
}
