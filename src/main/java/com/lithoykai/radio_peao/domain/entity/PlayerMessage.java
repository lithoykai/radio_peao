package com.lithoykai.radio_peao.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerMessage {
    private String type; // STATE_SYNC, CONTROL, QUEUE_UPDATE, PROGRESS_UPDATE
    private String command; // PLAY, PAUSE, SEEK, NEXT, PREVIOUS, ADD_TO_QUEUE, REMOVE_FROM_QUEUE
    private double timestamp;
    private String videoUrl;
    private String userId; // Identificador do usuário que enviou o comando
    private Object data; // Dados adicionais para comandos específicos
}
