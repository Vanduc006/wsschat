package com.ducnv.wsschat.dto.event;

import com.ducnv.wsschat.utils.constant.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEventDTO {
    // private RoomType roomType;
    // private Long roomId;
    private Long chat_id;
    private String sender_username;
    private Long sender_id;
    private String receiver_username;
    private Long receiver_id;
    private String message_content;
}
