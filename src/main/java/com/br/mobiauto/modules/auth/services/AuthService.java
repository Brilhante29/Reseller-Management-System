package com.br.mobiauto.modules.auth.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.br.mobiauto.modules.auth.dtos.AuthRequestDTO;
import com.br.mobiauto.modules.auth.dtos.AuthResponseDTO;
import com.br.mobiauto.modules.auth.dtos.RegisterRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.mappers.UserMapper;
import com.br.mobiauto.modules.users.models.User;
import com.br.mobiauto.modules.users.services.IUserService;
import com.br.mobiauto.security.providers.JWTProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUserService userService;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO) {
        UserResponseDTO user = this.userService.getUserByEmail(authRequestDTO.getEmail());
        String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDTO(token);
    }

    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name(registerRequestDTO.getName())
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .role(registerRequestDTO.getRole())
                .dealershipId(registerRequestDTO.getDealershipId())
                .build();
        User user = UserMapper.toUserEntity(userRequestDTO);

        UserResponseDTO savedUser = userService.saveUser(userRequestDTO);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public DecodedJWT validateToken(String token) {
        return jwtProvider.validateToken(token);
    }
}
