package com.lithoykai.radio_peao.controller;


import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class StreamController {
    @GetMapping("get-audio-url")
    public String getAudioUrl(@RequestParam String url) {
        try {
            Process process = new ProcessBuilder("yt-dlp", "-f", "bestaudio", "-g", url).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return Optional.ofNullable(reader.readLine())
                        .orElse("URL não encontrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao extrair URL de áudio: " + e.getMessage();
        }
    }
}
