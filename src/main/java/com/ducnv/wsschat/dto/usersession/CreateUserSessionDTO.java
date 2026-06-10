package com.ducnv.wsschat.dto.usersession;

import java.time.Instant;

import com.ducnv.wsschat.entity.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateUserSessionDTO(
    @NotBlank(message = "User is required") User user, 
    @NotBlank(message = "Refresh token is required") String refreshToken, 
    @NotBlank(message = "Device name is required") String deviceName, 
    String ipAddress, Instant expiresAt) {
    
}
