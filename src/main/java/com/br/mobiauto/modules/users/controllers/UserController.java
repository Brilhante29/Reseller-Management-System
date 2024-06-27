package com.br.mobiauto.modules.users.controllers;

import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.enums.Role;
import com.br.mobiauto.modules.users.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{email}")
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping
    public UserResponseDTO createUser(
            @RequestBody @Valid UserRequestDTO userRequestDTO) {
        return userService.saveUser(userRequestDTO);
    }

    @PutMapping("/{email}")
    public UserResponseDTO updateUser(
            @PathVariable String email,
            @RequestBody @Valid UserRequestDTO userRequestDTO) {
        return userService.updateUser(email, userRequestDTO);
    }

    @DeleteMapping("/{email}")
    public void deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
    }

    @PatchMapping("/{email}/role")
    public UserResponseDTO updateUserRole(
            @PathVariable String email,
            @RequestParam Role role) {
        return userService.updateUserRole(email, role);
    }

    @GetMapping
    public List<UserResponseDTO> getUsers() {
        return userService.getUsers();
    }
}
