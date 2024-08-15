package com.msb.bpm.approval.appr.mqtt.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage {

  private EventMessageType type;

  private EventMessageData data;

}
