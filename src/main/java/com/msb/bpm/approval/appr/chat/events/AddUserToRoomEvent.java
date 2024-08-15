package com.msb.bpm.approval.appr.chat.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class AddUserToRoomEvent extends ApplicationEvent {

    private String assignee;
    private String roomName;

    public AddUserToRoomEvent(Object source, String roomName, String assignee) {
        super(source);
        this.assignee = assignee;
        this.roomName = roomName;
    }
}
