package com.lithoykai.radio_peao.controller;

import com.lithoykai.radio_peao.domain.entity.PlayerMessage;
import com.lithoykai.radio_peao.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class PlayerWebSocketController {
    
    @Autowired
    private PlayerService playerService;
    
    @MessageMapping("/player/control")
    public void handlePlayerControl(@Payload PlayerMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String userId = getUserId(headerAccessor);
        
        switch (message.getType()) {
            case "CONTROL":
                playerService.handleControl(message.getCommand(), message.getTimestamp(), userId);
                break;
                
            case "QUEUE_UPDATE":
                if (message.getData() instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) message.getData();
                    String videoUrl = (String) data.get("videoUrl");
                    String videoTitle = (String) data.get("videoTitle");
                    Double duration = (Double) data.get("duration");
                    
                    if (videoUrl != null && videoTitle != null && duration != null) {
                        playerService.handleQueueUpdate(message.getCommand(), videoUrl, videoTitle, userId, duration);
                    }
                }
                break;
                
            case "PROGRESS_UPDATE":
                playerService.updateProgress(userId, message.getTimestamp());
                break;
        }
    }
    
    @MessageMapping("/player/status")
    @SendToUser("/queue/player/status")
    public PlayerMessage getPlayerStatus() {
        PlayerMessage response = new PlayerMessage();
        response.setType("STATE_SYNC");
        response.setData(playerService.getPlayerState());
        return response;
    }
    
    private String getUserId(SimpMessageHeaderAccessor headerAccessor) {
        // Gerar um ID único para o usuário baseado na sessão
        String sessionId = headerAccessor.getSessionId();
        return sessionId != null ? sessionId : "anonymous";
    }
} 