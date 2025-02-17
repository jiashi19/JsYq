package com.ruoyi.monitor.entities.events;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import org.springframework.context.ApplicationEvent;

public class BaseEvent extends ApplicationEvent {
    private String msg;
    public BaseEvent(Object source, String message) {
        super(source);
        this.msg = message;
    }


    public String getMsg() {
        return msg;
    }

}
