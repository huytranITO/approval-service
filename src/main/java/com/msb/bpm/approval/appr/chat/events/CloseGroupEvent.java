package com.msb.bpm.approval.appr.chat.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class CloseGroupEvent extends ApplicationEvent {

    private String roomName;

    public CloseGroupEvent(Object source, String roomName) {
        super(source);
        this.roomName = roomName;
    }
}
