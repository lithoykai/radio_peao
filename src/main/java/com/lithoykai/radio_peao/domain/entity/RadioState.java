package com.lithoykai.radio_peao.domain.entity;

import java.util.ArrayList;
import java.util.List;

public class RadioState {
    private List<String> queue = new ArrayList<>();
    private String currentUrl;
    private double currentTime;
    private boolean isPlaying;


    public List<String> getQueue() {
        return queue;
    }

    public void setQueue(List<String> queue) {
        this.queue = queue;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}

