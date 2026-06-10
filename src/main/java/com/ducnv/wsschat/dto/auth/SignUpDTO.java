package com.ducnv.wsschat.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignUpDTO(
    @NotBlank(message = "User is required") String username,
    @NotBlank(message = "Fullname is required") String fullname,
    @NotBlank(message = "Email is required") String email,
    @NotBlank(message = "Password is required") String password,
    String avatarUrl,
    String bio
) {}


// public record CreateUserDTO(String username, String fullname, String email, String password, String bio, String avatarUrl) {
    
// }