package com.br.mobiauto.modules.users.services;

import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.enums.Role;

import java.util.List;

public interface IUserService {
    UserResponseDTO getUserByEmail(String email);
    UserResponseDTO saveUser(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUser(String email, UserRequestDTO userRequestDTO);
    void deleteUser(String email);
    List<UserResponseDTO> getUsersByDealership(String dealershipId);
    UserResponseDTO updateUserRole(String email, Role role);
    List<UserResponseDTO> getUsers();
}
