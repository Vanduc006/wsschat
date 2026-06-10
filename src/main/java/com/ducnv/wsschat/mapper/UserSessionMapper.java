package com.ducnv.wsschat.mapper;

import org.mapstruct.Mapper;

import com.ducnv.wsschat.dto.usersession.CreateUserSessionDTO;
import com.ducnv.wsschat.dto.usersession.UserSessionDTO;
import com.ducnv.wsschat.entity.UserSession;

@Mapper(componentModel = "spring")
public interface UserSessionMapper {
    public UserSessionDTO toResponseDTO(UserSession userSession);

    public UserSession toEntity(CreateUserSessionDTO createUserSessionDTO);
}
