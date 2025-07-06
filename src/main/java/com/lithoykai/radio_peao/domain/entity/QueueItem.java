package com.lithoykai.radio_peao.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueItem {
    private String videoUrl;
    private String videoTitle;
    private String addedBy;
    private long addedAt;
    private double duration;
} 