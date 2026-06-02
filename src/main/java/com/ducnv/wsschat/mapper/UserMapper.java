package com.ducnv.wsschat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.ducnv.wsschat.dto.user.CreateUserDTO;
import com.ducnv.wsschat.dto.user.UpdateUserDTO;
import com.ducnv.wsschat.dto.user.UserDTO;
import com.ducnv.wsschat.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public UserDTO toResponseDTO(User entity);

    public User toEntity(CreateUserDTO createDTO);

    public void updateEntityFromDTO(UpdateUserDTO updateDTO, @MappingTarget User entity);
}
