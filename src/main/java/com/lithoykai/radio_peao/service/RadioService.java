package com.lithoykai.radio_peao.service;

import com.lithoykai.radio_peao.domain.entity.RadioState;
import org.springframework.stereotype.Service;

@Service
public class RadioService {

    private final RadioState state = new RadioState();

    public void addToQueue(String url) {
        state.getQueue().add(url);
        if (state.getCurrentUrl() == null) {
            state.setCurrentUrl(url);
        }
    }

    public void updateState(String action, Double time) {
        switch (action) {
            case "play" -> state.setPlaying(true);
            case "pause" -> state.setPlaying(false);
            case "seek" -> {
                if (time != null) {
                    state.setCurrentTime(time);
                }
            }
        }
    }

    public RadioState getState() {
        return state;
    }
}