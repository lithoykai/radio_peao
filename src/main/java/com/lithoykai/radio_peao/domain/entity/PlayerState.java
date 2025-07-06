package com.lithoykai.radio_peao.domain.entity;

import lombok.Data;

@Data
public class PlayerState {
    private String command = "PAUSE";
    private double timestamp = 0.0;
    private String videoUrl = null;
    private boolean isPlaying = false;
    private double duration = 0.0;
    private String currentVideoTitle = "";
    private long lastUpdateTime = System.currentTimeMillis();
}

