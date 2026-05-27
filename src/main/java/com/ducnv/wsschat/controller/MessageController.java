package com.ducnv.wsschat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.ducnv.wsschat.dto.event.ChatEventDTO;
import com.ducnv.wsschat.dto.event.SocketEventDTO;
import com.ducnv.wsschat.dto.event.TypingEventDTO;
import com.ducnv.wsschat.model.Message;
import com.ducnv.wsschat.utils.constant.SocketEventType;

@Controller
public class MessageController {
    // @MessageMapping("/hello")
    // @SendTo("/topic/messages")
    // public String getMessage(String username) {
    //     return "Hello" + username;
    // }

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Payload is data send to target client
    @MessageMapping("/chat.send")
    public void send(SimpMessageHeaderAccessor accessor, @Payload ChatEventDTO newChatEventDTO) {
        simpMessagingTemplate.convertAndSend(
            "/queue/messages-user" + newChatEventDTO.getReceiver_username(),
            // newMessage.getMessage_content()
            SocketEventDTO.builder()
                .eventType(SocketEventType.CHAT)
                .payload(
                    ChatEventDTO.builder()
                        .receiver_username(newChatEventDTO.getReceiver_username())
                        .sender_username(accessor.getUser().getName())
                        .message_content(newChatEventDTO.getMessage_content())
                        .build()
                )
                .build()
        );
    }

    @MessageMapping("/chat.typing")
    public void type(SimpMessageHeaderAccessor accessor, @Payload TypingEventDTO newTypingEventDTO) {
        // String username = accessor.getUser().getName();
        simpMessagingTemplate.convertAndSend(
            "/queue/messages-user" + newTypingEventDTO.getReceiver_username(),
            SocketEventDTO.builder()
                .eventType(SocketEventType.TYPING)
                .payload(
                    TypingEventDTO.builder()
                        .sender_username(accessor.getUser().getName())
                        .is_typing(newTypingEventDTO.getIs_typing())
                        .build()
                )
                .build()
        );
    }
}
