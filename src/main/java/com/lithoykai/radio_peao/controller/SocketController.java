package com.lithoykai.radio_peao.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class SocketController {

    @MessageMapping("/socket/control")
    @SendTo("/topic/control")
    public Map<String, Object> handleControl(Map<String, Object> message) {
        System.out.println("[WebSocket] Mensagem recebida: " + message);
        return message;
    }
}
