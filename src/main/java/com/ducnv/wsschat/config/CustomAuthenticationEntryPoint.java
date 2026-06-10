package com.ducnv.wsschat.config;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.ducnv.wsschat.dto.ApiResponseDTO;
import com.ducnv.wsschat.dto.ResponseStatusDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");
        
        objectMapper.writeValue(response.getWriter(), ApiResponseDTO.builder()
        .status(ResponseStatusDTO.builder()
            .statusCode(HttpStatus.UNAUTHORIZED)
            .message("Invalid Access Token")
            .build()
        )
        .timeStamp(Instant.now())
        .build()
        );
    }
}
