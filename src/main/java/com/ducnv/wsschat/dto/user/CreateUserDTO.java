package com.ducnv.wsschat.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
public record CreateUserDTO(
    @NotBlank(message = "Username is required") String username, 
    @NotBlank(message = " Fullname is required") String fullname, 
    @NotBlank(message = "Email is required") String email, 
    @NotBlank(message = "Password is required") String password, 
    String bio, String avatarUrl) {
    
}
