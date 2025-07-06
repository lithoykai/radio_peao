package com.lithoykai.radio_peao.service;

import com.lithoykai.radio_peao.domain.entity.QueueItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class QueueService {
    
    private final List<QueueItem> queue = new CopyOnWriteArrayList<>();
    private int currentIndex = -1;
    
    public synchronized void addToQueue(String videoUrl, String videoTitle, String addedBy, double duration) {
        QueueItem item = new QueueItem(videoUrl, videoTitle, addedBy, System.currentTimeMillis(), duration);
        queue.add(item);
        
        // Se é o primeiro item, definir como atual
        if (currentIndex == -1) {
            currentIndex = 0;
        }
    }
    
    public synchronized void removeFromQueue(int index) {
        if (index >= 0 && index < queue.size()) {
            queue.remove(index);
            
            // Ajustar o índice atual se necessário
            if (currentIndex >= index && currentIndex > 0) {
                currentIndex--;
            }
            
            // Se a fila ficou vazia, resetar o índice
            if (queue.isEmpty()) {
                currentIndex = -1;
            }
        }
    }
    
    public synchronized QueueItem getCurrentItem() {
        if (currentIndex >= 0 && currentIndex < queue.size()) {
            return queue.get(currentIndex);
        }
        return null;
    }
    
    public synchronized QueueItem next() {
        if (currentIndex < queue.size() - 1) {
            currentIndex++;
            return getCurrentItem();
        }
        return null;
    }
    
    public synchronized QueueItem previous() {
        if (currentIndex > 0) {
            currentIndex--;
            return getCurrentItem();
        }
        return null;
    }
    
    public synchronized void setCurrentIndex(int index) {
        if (index >= 0 && index < queue.size()) {
            currentIndex = index;
        }
    }
    
    public synchronized List<QueueItem> getQueue() {
        return new ArrayList<>(queue);
    }
    
    public synchronized int getCurrentIndex() {
        return currentIndex;
    }
    
    public synchronized int getQueueSize() {
        return queue.size();
    }
    
    public synchronized boolean hasNext() {
        return currentIndex < queue.size() - 1;
    }
    
    public synchronized boolean hasPrevious() {
        return currentIndex > 0;
    }
    
    public synchronized void clearQueue() {
        queue.clear();
        currentIndex = -1;
    }
} 