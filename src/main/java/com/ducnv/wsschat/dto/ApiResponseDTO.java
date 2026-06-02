package com.ducnv.wsschat.dto;

import org.springframework.http.HttpStatus;

import lombok.Builder;

@Builder
public record ApiResponseDTO<T>(HttpStatus status, T data) {
    
}
