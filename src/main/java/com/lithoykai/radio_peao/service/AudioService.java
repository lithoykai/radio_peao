package com.lithoykai.radio_peao.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AudioService {
    
    private static final String AUDIO_DIR = "audio_cache";
    
    public AudioService() {
        // Criar diretório de cache se não existir
        File audioDir = new File(AUDIO_DIR);
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
    }
    
    public String extractAudio(String videoUrl) {
        try {
            // Extrair ID do vídeo da URL
            String videoId = extractVideoId(videoUrl);
            if (videoId == null) {
                return null;
            }
            
            String outputPath = AUDIO_DIR + File.separator + videoId + ".mp3";
            File outputFile = new File(outputPath);
            
            // Se o arquivo já existe, retornar o caminho
            if (outputFile.exists()) {
                return outputPath;
            }
            
            // Extrair áudio usando yt-dlp
            ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "-x", // Extrair áudio
                "--audio-format", "mp3",
                "--audio-quality", "0", // Melhor qualidade
                "-o", outputPath,
                videoUrl
            );
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && outputFile.exists()) {
                return outputPath;
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public String convertToStreamableFormat(String audioPath) {
        try {
            String streamablePath = audioPath.replace(".mp3", "_stream.mp3");
            File streamableFile = new File(streamablePath);
            
            // Se o arquivo já existe, retornar o caminho
            if (streamableFile.exists()) {
                return streamablePath;
            }
            
            // Converter para formato otimizado para streaming
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", audioPath,
                "-c:a", "libmp3lame",
                "-b:a", "128k", // Bitrate otimizado para streaming
                "-ar", "44100", // Sample rate
                "-ac", "2", // Stereo
                streamablePath
            );
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && streamableFile.exists()) {
                return streamablePath;
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private String extractVideoId(String videoUrl) {
        // Padrões comuns de URLs do YouTube
        String[] patterns = {
            "(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/)([a-zA-Z0-9_-]{11})",
            "youtube\\.com/watch\\?.*v=([a-zA-Z0-9_-]{11})"
        };
        
        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(videoUrl);
            if (m.find()) {
                return m.group(1);
            }
        }
        
        return null;
    }
    
    public void cleanupOldFiles() {
        try {
            File audioDir = new File(AUDIO_DIR);
            File[] files = audioDir.listFiles();
            
            if (files != null) {
                long currentTime = System.currentTimeMillis();
                long maxAge = 24 * 60 * 60 * 1000; // 24 horas
                
                for (File file : files) {
                    if (currentTime - file.lastModified() > maxAge) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 