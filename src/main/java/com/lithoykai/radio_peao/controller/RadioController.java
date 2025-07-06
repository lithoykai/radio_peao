package com.lithoykai.radio_peao.controller;

import com.lithoykai.radio_peao.domain.entity.RadioState;
import com.lithoykai.radio_peao.service.RadioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RadioController {

    private final RadioService service;

    public RadioController(RadioService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public void addToQueue(@RequestParam String url) {
        service.addToQueue(url);
    }

    @PostMapping("/control")
    public void control(@RequestParam String action, @RequestParam(required = false) Double time) {
        service.updateState(action, time);
    }

    @GetMapping("/state")
    public RadioState getState() {
        return service.getState();
    }
}