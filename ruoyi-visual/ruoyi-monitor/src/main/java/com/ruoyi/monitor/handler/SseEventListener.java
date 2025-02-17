package com.ruoyi.monitor.handler;

import com.ruoyi.monitor.entities.events.CpuThresholdEvent;
import com.ruoyi.monitor.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SseEventListener {

    @Autowired
    private SseService sseService;


    @EventListener
    public void handleDataChangeEvent(CpuThresholdEvent event) {
        sseService.sendEvent(event.getMsg());
    }
}
