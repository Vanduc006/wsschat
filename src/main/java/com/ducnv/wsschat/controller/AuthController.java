package com.ducnv.wsschat.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ducnv.wsschat.dto.ApiResponseDTO;
import com.ducnv.wsschat.dto.ResponseStatusDTO;
import com.ducnv.wsschat.dto.auth.SignInDTO;
import com.ducnv.wsschat.dto.auth.SignUpDTO;
import com.ducnv.wsschat.dto.auth.UserLoginDTO;
import com.ducnv.wsschat.dto.user.CreateUserDTO;
import com.ducnv.wsschat.dto.user.UserDTO;
import com.ducnv.wsschat.dto.usersession.CreateUserSessionDTO;
import com.ducnv.wsschat.entity.User;
import com.ducnv.wsschat.entity.UserSession;
import com.ducnv.wsschat.mapper.UserMapper;
import com.ducnv.wsschat.service.UserService;
import com.ducnv.wsschat.service.UserSessionService;
import com.ducnv.wsschat.utils.SecurityUtil;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1")
public class AuthController {
    // SignIn, SignUp, SignOut, ... 

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private UserSessionService userSessionService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService, PasswordEncoder passwordEncoder, UserSessionService userSessionService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userSessionService = userSessionService;
    }

    @Value("${wsschat.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpriration;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<?>> signUp(
        @Valid @RequestBody SignUpDTO signUpDTO
    ) {
        String hashPassword = this.passwordEncoder.encode(signUpDTO.password());
        // CreateUserDTO createUserDTO = this.userService.handleCreateUser(
        //     User.builder()
        //         .username(signUpDTO.getUsername())
        //         .
        // )
        UserDTO userDTO = this.userService.handleCreateUser(CreateUserDTO.builder()
            .username(signUpDTO.username())
            .fullname(signUpDTO.fullname())
            .email(signUpDTO.email())
            .password(hashPassword)
            .avatarUrl(signUpDTO.avatarUrl())
            .bio(signUpDTO.bio())
            .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.builder()
            .status(ResponseStatusDTO.builder()
                .statusCode(HttpStatus.CREATED)
                .message("SignUp success")
                .build()
            )
            .data(userDTO)
            .timeStamp(Instant.now())
            .build()
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponseDTO<?>> signIn(
        @Valid @RequestBody SignInDTO signInDTO
    ) {
        
        User currentUser;
        if (signInDTO.email() != "") {
            currentUser = this.userService.handleGetUserByEmail(signInDTO.email());
        } else if (signInDTO.username() != "") {
            currentUser = this.userService.handleGetUserByUsername(signInDTO.username());
        } else {
            throw new IllegalArgumentException("Invalid email/username or password");
        }

        // user can signin by username or email
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser.getEmail(), signInDTO.password());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
            .id(currentUser.getId())
            .username(currentUser.getUsername())
            .fullname(currentUser.getFullname())
            .build();
        
        String refreshToken = this.securityUtil.createRefreshToken(currentUser.getEmail(), userLoginDTO);
        String accessToken = this.securityUtil.createAccessToken(currentUser.getEmail(), userLoginDTO);

        this.userSessionService.hanldeCreateSession(CreateUserSessionDTO.builder()
            .user(currentUser)
            .refreshToken(refreshToken)
            .deviceName(signInDTO.deviceName())
            .ipAddress(signInDTO.ipAddress())
            .build()
        );

        ResponseCookie responseCookie = ResponseCookie
        .from("refreshToken", refreshToken)
        .secure(false)
        .path("/")
        .maxAge(refreshTokenExpriration)
        .httpOnly(true)
        .sameSite("Strict")
        .build();

        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(ApiResponseDTO.builder()
            .status(ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Login success")
                .build()
            )
            .data(accessToken) // just return access token
            .timeStamp(Instant.now())
            .build()
        ); 

    }
    
}
