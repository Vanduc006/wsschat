package com.ducnv.wsschat.dto;

import lombok.Builder;

@Builder
public record MetaDTO(int page, int pageSize, int pages, Long total) {
    
}
