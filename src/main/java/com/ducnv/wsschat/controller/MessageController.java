package com.ducnv.wsschat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    // @MessageMapping("/hello")
    // @SendTo("/topic/messages")
    // public String getMessage(String username) {
    //     return "Hello" + username;
    // }

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/hello")
    public void send(SimpMessageHeaderAccessor accessor, @Payload String username) {
        simpMessagingTemplate.convertAndSend(
            "/queue/messages-user" + username,
            "Hello from " + accessor.getUser().getName()
        );
    }

}
