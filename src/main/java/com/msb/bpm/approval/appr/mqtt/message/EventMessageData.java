package com.msb.bpm.approval.appr.mqtt.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMessageData {

  private String id;

  private NotificationType notificationType;

  private Module module;

  private String title;

  private String link;

  @JsonFormat(pattern = "dd/M/yyyy HH:mm:ss")
  private Date createdAt;

  private long timeToLive;

  private String content;

}
