package com.msb.bpm.approval.appr.chat.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
@Setter
public class AddUserToListRoomEvent extends ApplicationEvent {

    private String assignee;
    private List<String> roomNames;

    public AddUserToListRoomEvent(Object source, List<String> roomNames, String assignee) {
        super(source);
        this.assignee = assignee;
        this.roomNames = roomNames;
    }
}
