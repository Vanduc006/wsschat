package com.ducnv.wsschat.dto.user;

import lombok.Builder;

@Builder
public record UserDTO(Long id, String username, String fullname, String email, String bio, String avatarUrl) {}
