package com.ducnv.wsschat.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Builder;

@Builder
public record ResponseStatusDTO(HttpStatus statusCode, String message) {
    
}
