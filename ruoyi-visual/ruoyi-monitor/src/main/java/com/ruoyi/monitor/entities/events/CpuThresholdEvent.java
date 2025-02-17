package com.ruoyi.monitor.entities.events;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import org.springframework.context.ApplicationEvent;

public class CpuThresholdEvent extends BaseEvent {
    private String msg;
    private Instance instance;
    public CpuThresholdEvent(Object source, String message,Instance instance) {
        super(source, message);
        this.msg = message;
        this.instance= instance;
    }

    public CpuThresholdEvent(Object source, String message) {
        super(source,message);
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }


}
