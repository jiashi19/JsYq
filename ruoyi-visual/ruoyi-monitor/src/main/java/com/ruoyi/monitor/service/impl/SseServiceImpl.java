package com.ruoyi.monitor.service.impl;


import com.ruoyi.monitor.service.SseService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseServiceImpl implements SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(60_000L); // 1分钟超时
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    /**
     * 遍历所有emitter，发送消息
     * @param message
     */
    public void sendEvent(String message) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .data(message)
                        .reconnectTime(5000));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
}
