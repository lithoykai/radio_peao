package com.lithoykai.radio_peao.service;

import com.lithoykai.radio_peao.domain.entity.PlayerMessage;
import com.lithoykai.radio_peao.domain.entity.PlayerState;
import com.lithoykai.radio_peao.domain.entity.QueueItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerService {
    
    private final PlayerState playerState = new PlayerState();
    private final ConcurrentHashMap<String, Double> clientProgress = new ConcurrentHashMap<>();
    
    @Autowired
    private QueueService queueService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private YouTubeService youTubeService;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public PlayerService() {
        // Iniciar sincronização automática a cada 5 segundos
        scheduler.scheduleAtFixedRate(this::syncProgress, 5, 5, TimeUnit.SECONDS);
    }
    
    public synchronized void handleControl(String command, double timestamp, String userId) {
        switch (command.toUpperCase()) {
            case "PLAY":
                playerState.setCommand("PLAY");
                playerState.setTimestamp(timestamp);
                playerState.setPlaying(true);
                playerState.setLastUpdateTime(System.currentTimeMillis());
                broadcastStateSync();
                break;
                
            case "PAUSE":
                playerState.setCommand("PAUSE");
                playerState.setPlaying(false);
                playerState.setLastUpdateTime(System.currentTimeMillis());
                broadcastStateSync();
                break;
                
            case "SEEK":
                playerState.setTimestamp(timestamp);
                playerState.setLastUpdateTime(System.currentTimeMillis());
                broadcastStateSync();
                break;
                
            case "NEXT":
                handleNext();
                break;
                
            case "PREVIOUS":
                handlePrevious();
                break;
        }
    }
    
    public synchronized void handleQueueUpdate(String command, String videoUrl, String videoTitle, String userId, double duration) {
        switch (command.toUpperCase()) {
            case "ADD_TO_QUEUE":
                // Se não temos informações do vídeo, extrair do YouTube
                if (videoTitle == null || duration == 0.0) {
                    YouTubeService.VideoInfo videoInfo = youTubeService.extractVideoInfo(videoUrl);
                    if (videoInfo != null) {
                        videoTitle = videoInfo.getTitle();
                        duration = videoInfo.getDuration();
                    }
                }
                
                queueService.addToQueue(videoUrl, videoTitle, userId, duration);
                broadcastQueueUpdate();
                break;
                
            case "REMOVE_FROM_QUEUE":
                // Assumindo que o data contém o índice
                // Esta implementação seria expandida conforme necessário
                break;
        }
    }
    
    public synchronized void updateProgress(String userId, double progress) {
        clientProgress.put(userId, progress);
    }
    
    private synchronized void handleNext() {
        QueueItem nextItem = queueService.next();
        if (nextItem != null) {
            playerState.setVideoUrl(nextItem.getVideoUrl());
            playerState.setTimestamp(0.0);
            playerState.setPlaying(true);
            playerState.setLastUpdateTime(System.currentTimeMillis());
            broadcastStateSync();
            broadcastQueueUpdate();
        }
    }
    
    private synchronized void handlePrevious() {
        QueueItem previousItem = queueService.previous();
        if (previousItem != null) {
            playerState.setVideoUrl(previousItem.getVideoUrl());
            playerState.setTimestamp(0.0);
            playerState.setPlaying(true);
            playerState.setLastUpdateTime(System.currentTimeMillis());
            broadcastStateSync();
            broadcastQueueUpdate();
        }
    }
    
    private void syncProgress() {
        if (!clientProgress.isEmpty()) {
            // Calcular progresso médio dos clientes
            double averageProgress = clientProgress.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            // Atualizar o estado do servidor se a diferença for significativa
            if (Math.abs(averageProgress - playerState.getTimestamp()) > 1.0) {
                playerState.setTimestamp(averageProgress);
                broadcastStateSync();
            }
        }
    }
    
    private void broadcastStateSync() {
        PlayerMessage message = new PlayerMessage();
        message.setType("STATE_SYNC");
        message.setCommand(playerState.getCommand());
        message.setTimestamp(playerState.getTimestamp());
        message.setVideoUrl(playerState.getVideoUrl());
        message.setData(playerState);
        
        messagingTemplate.convertAndSend("/topic/player", message);
    }
    
    private void broadcastQueueUpdate() {
        PlayerMessage message = new PlayerMessage();
        message.setType("QUEUE_UPDATE");
        message.setData(queueService.getQueue());
        
        messagingTemplate.convertAndSend("/topic/player", message);
    }
    
    public synchronized PlayerState getPlayerState() {
        return playerState;
    }
    
    public synchronized void setCurrentVideo(String videoUrl, String videoTitle, double duration) {
        playerState.setVideoUrl(videoUrl);
        playerState.setCurrentVideoTitle(videoTitle);
        playerState.setDuration(duration);
        playerState.setTimestamp(0.0);
        playerState.setLastUpdateTime(System.currentTimeMillis());
        broadcastStateSync();
    }
    
    public synchronized void checkAndAdvanceQueue() {
        // Verificar se a música atual terminou
        if (playerState.isPlaying() && playerState.getTimestamp() >= playerState.getDuration() - 1.0) {
            handleNext();
        }
    }
} 