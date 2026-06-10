package com.ducnv.wsschat.dto;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.Builder;

@Builder
public record ApiResponseDTO<T>(ResponseStatusDTO status, T data, Instant timeStamp) {
    
}
