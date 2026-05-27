package com.ducnv.wsschat.dto.event;

import com.ducnv.wsschat.utils.constant.SocketEventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocketEventDTO {
    private SocketEventType eventType;
    private Object payload;
}
