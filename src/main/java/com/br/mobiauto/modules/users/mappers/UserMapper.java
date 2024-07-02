package com.br.mobiauto.modules.users.mappers;

import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.User;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static UserResponseDTO toUserResponseDTO(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public static User toUserEntity(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, User.class);
    }
}
