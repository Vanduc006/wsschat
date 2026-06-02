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
public class ChatController {
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
            "/queue/messages-user" + newChatEventDTO.getReceiverUsername(),
            // newMessage.getMessage_content()
            SocketEventDTO.builder()
                .eventType(SocketEventType.CHAT)
                .payload(
                    ChatEventDTO.builder()
                        .receiverUsername(newChatEventDTO.getReceiverUsername())
                        .senderUsername(accessor.getUser().getName())
                        .messageContent(newChatEventDTO.getMessageContent())
                        .build()
                )
                .build()
        );
    }

    @MessageMapping("/chat.typing")
    public void type(SimpMessageHeaderAccessor accessor, @Payload TypingEventDTO newTypingEventDTO) {
        // String username = accessor.getUser().getName();
        simpMessagingTemplate.convertAndSend(
            "/queue/messages-user" + newTypingEventDTO.getReceiverUsername(),
            SocketEventDTO.builder()
                .eventType(SocketEventType.TYPING)
                .payload(
                    TypingEventDTO.builder()
                        .senderUsername(accessor.getUser().getName())
                        .isTyping(newTypingEventDTO.getIsTyping())
                        .build()
                )
                .build()
        );
    }
}
