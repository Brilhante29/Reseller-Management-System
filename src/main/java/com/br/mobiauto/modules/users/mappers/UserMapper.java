package com.br.mobiauto.modules.users.mappers;

import com.br.mobiauto.modules.users.dtos.UserRequestDTO;
import com.br.mobiauto.modules.users.dtos.UserResponseDTO;
import com.br.mobiauto.modules.users.models.User;
import org.modelmapper.ModelMapper;

public class UserMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public static User toUserEntity(UserRequestDTO userRequestDTO) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userRequestDTO, User.class);
    }
}
