package com.lithoykai.radio_peao.controller;

import com.lithoykai.radio_peao.domain.entity.PlayerMessage;
import com.lithoykai.radio_peao.domain.entity.PlayerState;
import com.lithoykai.radio_peao.domain.entity.QueueItem;
import com.lithoykai.radio_peao.service.PlayerService;
import com.lithoykai.radio_peao.service.QueueService;
import com.lithoykai.radio_peao.service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private QueueService queueService;
    
    @Autowired
    private YouTubeService youTubeService;

    @MessageMapping("/player")
    @SendTo("/topic/player")
    public PlayerMessage handleControl(PlayerMessage message) {
        if ("CONTROL".equals(message.getType())) {
            playerService.handleControl(message.getCommand(), message.getTimestamp(), message.getUserId());
        }
        return message;
    }

    @MessageMapping("/state")
    @SendTo("/topic/player")
    public PlayerMessage syncState(PlayerMessage state) {
        playerService.handleControl(state.getCommand(), state.getTimestamp(), state.getUserId());
        return state;
    }

    @MessageMapping("/join")
    @SendTo("/topic/player")
    public PlayerMessage join(PlayerMessage join) {
        PlayerState currentState = playerService.getPlayerState();
        join.setType("STATE_SYNC");
        join.setCommand(currentState.getCommand());
        join.setTimestamp(currentState.getTimestamp());
        join.setVideoUrl(currentState.getVideoUrl());
        return join;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getPlayerStatus() {
        PlayerState state = playerService.getPlayerState();
        
        Map<String, Object> response = new HashMap<>();
        response.put("command", state.getCommand());
        response.put("timestamp", state.getTimestamp());
        response.put("videoUrl", state.getVideoUrl());
        response.put("isPlaying", state.isPlaying());
        response.put("duration", state.getDuration());
        response.put("currentVideoTitle", state.getCurrentVideoTitle());
        response.put("lastUpdateTime", state.getLastUpdateTime());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/queue")
    public ResponseEntity<Map<String, Object>> getQueue() {
        List<QueueItem> queue = queueService.getQueue();
        int currentIndex = queueService.getCurrentIndex();
        
        Map<String, Object> response = new HashMap<>();
        response.put("queue", queue);
        response.put("currentIndex", currentIndex);
        response.put("queueSize", queueService.getQueueSize());
        response.put("hasNext", queueService.hasNext());
        response.put("hasPrevious", queueService.hasPrevious());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/control")
    public ResponseEntity<Map<String, String>> controlPlayer(@RequestBody Map<String, Object> request) {
        String command = (String) request.get("command");
        Double timestamp = (Double) request.get("timestamp");
        String userId = (String) request.get("userId");
        
        if (command != null && timestamp != null) {
            playerService.handleControl(command, timestamp, userId != null ? userId : "api");
            return ResponseEntity.ok(Map.of("status", "success", "message", "Command executed"));
        }
        
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid parameters"));
    }
    
    @PostMapping("/queue/add")
    public ResponseEntity<Map<String, String>> addToQueue(@RequestBody Map<String, Object> request) {
        String videoUrl = (String) request.get("videoUrl");
        String videoTitle = (String) request.get("videoTitle");
        String userId = (String) request.get("userId");
        Double duration = (Double) request.get("duration");
        
        if (videoUrl != null && videoTitle != null && duration != null) {
            playerService.handleQueueUpdate("ADD_TO_QUEUE", videoUrl, videoTitle, 
                userId != null ? userId : "api", duration);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Added to queue"));
        }
        
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid parameters"));
    }
    
    @DeleteMapping("/queue/{index}")
    public ResponseEntity<Map<String, String>> removeFromQueue(@PathVariable int index) {
        queueService.removeFromQueue(index);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Removed from queue"));
    }
    
    @PostMapping("/queue/add-url")
    public ResponseEntity<Map<String, Object>> addToQueueByUrl(@RequestBody Map<String, Object> request) {
        String videoUrl = (String) request.get("videoUrl");
        String userId = (String) request.get("userId");
        
        if (videoUrl != null) {
            // Extrair informações do vídeo automaticamente
            YouTubeService.VideoInfo videoInfo = youTubeService.extractVideoInfo(videoUrl);
            if (videoInfo != null) {
                playerService.handleQueueUpdate("ADD_TO_QUEUE", videoUrl, videoInfo.getTitle(), 
                    userId != null ? userId : "api", videoInfo.getDuration());
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Added to queue");
                response.put("videoInfo", videoInfo);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Could not extract video info"));
            }
        }
        
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid video URL"));
    }
}

