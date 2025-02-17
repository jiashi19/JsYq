package com.ruoyi.monitor.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {

    public SseEmitter createEmitter();
    public void sendEvent(String message);
}
