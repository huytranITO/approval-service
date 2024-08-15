package com.msb.bpm.approval.appr.service.applicationListenerEvent.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class SentCompleteGenerateFormTemplateEvent extends ApplicationEvent {

    private String applicationId;
    private String userName;

    public SentCompleteGenerateFormTemplateEvent(Object source, String applicationId, String userName) {
        super(source);
        this.applicationId = applicationId;
        this.userName = userName;
    }
}
