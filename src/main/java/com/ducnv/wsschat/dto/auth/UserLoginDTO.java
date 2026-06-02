package com.ducnv.wsschat.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDTO {
    private Long id;
    private String username;
    private String fullname;
    private String avatarUrl;
    private String bio;
}
