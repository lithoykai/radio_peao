package com.lithoykai.radio_peao.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YouTubeService {
    
    public VideoInfo extractVideoInfo(String videoUrl) {
        try {
            // Comando yt-dlp para extrair informações do vídeo
            ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp", 
                "--print", "title,duration,id",
                "--no-playlist",
                videoUrl
            );
            
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String title = reader.readLine();
            String durationStr = reader.readLine();
            String videoId = reader.readLine();
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && title != null && durationStr != null) {
                double duration = parseDuration(durationStr);
                return new VideoInfo(videoId, title, duration, videoUrl);
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private double parseDuration(String durationStr) {
        try {
            // yt-dlp retorna duração em segundos
            return Double.parseDouble(durationStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public static class VideoInfo {
        private String videoId;
        private String title;
        private double duration;
        private String url;
        
        public VideoInfo(String videoId, String title, double duration, String url) {
            this.videoId = videoId;
            this.title = title;
            this.duration = duration;
            this.url = url;
        }
        
        // Getters
        public String getVideoId() { return videoId; }
        public String getTitle() { return title; }
        public double getDuration() { return duration; }
        public String getUrl() { return url; }
    }
} 