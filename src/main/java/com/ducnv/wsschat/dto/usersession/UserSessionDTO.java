package com.ducnv.wsschat.dto.usersession;

import com.ducnv.wsschat.entity.User;

import lombok.Builder;

@Builder
public record UserSessionDTO(User user, String refreshToken ,String deviceName, String ipAddress) {
    
}
