package com.msb.bpm.approval.appr.aop;

import java.util.List;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PushKafkaEvent extends ApplicationEvent {

  String eventType;
  List<String> bpmIds;

  public PushKafkaEvent(Object source, String eventType, List<String> bpmIds) {
    super(source);
    this.eventType = eventType;
    this.bpmIds = bpmIds;
  }
}
