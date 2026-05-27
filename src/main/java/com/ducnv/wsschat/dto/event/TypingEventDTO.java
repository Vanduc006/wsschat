package com.ducnv.wsschat.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypingEventDTO {
    private Long sender_id;
    private String sender_username;
    private Long receiver_id;
    private String receiver_username;
    private Boolean is_typing;
}
