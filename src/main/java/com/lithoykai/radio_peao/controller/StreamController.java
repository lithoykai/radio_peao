package com.lithoykai.radio_peao.controller;

import com.lithoykai.radio_peao.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/stream")
@CrossOrigin(origins = "*")
public class StreamController {
    
    @Autowired
    private AudioService audioService;
    
    @GetMapping("/audio/{videoId}")
    public ResponseEntity<Resource> streamAudio(@PathVariable String videoId) {
        try {
            String audioPath = "audio_cache" + File.separator + videoId + ".mp3";
            File audioFile = new File(audioPath);
            
            if (!audioFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(audioFile);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.setContentLength(audioFile.length());
            headers.set("Accept-Ranges", "bytes");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/prepare")
    public ResponseEntity<Object> prepareAudio(@RequestBody Object request) {
        // Este endpoint seria usado para preparar o áudio antes do streaming
        // Implementação seria expandida conforme necessário
        return ResponseEntity.ok().build();
    }
}
