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
    private String senderUsername;
    private String senderFullname;
    private Long senderId;

    private String receiverUsername;
    private String receiverFullname;
    private Long receiverId;

    private Boolean isTyping;
}
