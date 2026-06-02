package com.ducnv.wsschat.dto.user;

import lombok.Builder;

@Builder
public record UpdateUserDTO(String fullname, String email, String bio, String avatarUrl) {
    // not change username, passsword in here, should be in auth dto
}
