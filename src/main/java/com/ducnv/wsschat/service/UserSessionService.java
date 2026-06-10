package com.ducnv.wsschat.service;

import java.util.List;

import com.ducnv.wsschat.dto.usersession.CreateUserSessionDTO;
import com.ducnv.wsschat.dto.usersession.UserSessionDTO;
import com.ducnv.wsschat.entity.User;

public interface UserSessionService {
    public UserSessionDTO hanldeCreateSession(CreateUserSessionDTO createUserSessionDTO);

    public UserSessionDTO handleValidRefreshToken(String refreshToken);

    public void hanldeRevokeUserSessionByRefreshToken(String refreshToken);

    public void handleRevokeAllUserSessionsByUserId(Long userId);

    public List<UserSessionDTO> handleGetAllUserSessionsByUserId(Long userId);
}
