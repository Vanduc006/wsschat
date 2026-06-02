package com.ducnv.wsschat.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PaginationDTO<T>(MetaDTO meta, List<T> result) {
    
}
