package com.ducnv.wsschat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Message {
    private String sender_username;
    private Long sender_id;
    private String receiver_username;
    private String receiver_id;
    private String message_content;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class Typing {
        private String sender_username;
        private Long sender_id;
        private Boolean is_typing;
    }
}
