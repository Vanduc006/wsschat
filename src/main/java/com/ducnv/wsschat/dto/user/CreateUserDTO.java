package com.ducnv.wsschat.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
public record CreateUserDTO(String username, String fullname, String email, String password, String bio, String avatarUrl) {
    
}
