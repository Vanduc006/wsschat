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
    private Long id;

    private String senderUsername;
    private String senderFullname;
    private Long senderId;

    private String receiverUsername;
    private String receiverFullname;
    private Long receiverId;

    private String messageContent;
}
