package com.msb.bpm.approval.appr.mqtt.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.mqtt.message.EventMessage;
import com.msb.bpm.approval.appr.mqtt.message.EventMessageData;
import com.msb.bpm.approval.appr.mqtt.message.EventMessageType;
import com.msb.bpm.approval.appr.mqtt.message.Module;
import com.msb.bpm.approval.appr.mqtt.message.NotificationType;
import com.msb.bpm.approval.appr.mqtt.content.Content;
import com.msb.bpm.approval.appr.mqtt.content.ContentDetail;
import com.msb.bpm.approval.appr.mqtt.content.ContentType;
import com.msb.bpm.approval.appr.mqtt.content.Response;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class MessageData {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static final String TITLE_DEFAULT = "Thông báo";

  protected String applicationId;

  protected NotificationType notificationType;

  protected Module module;

  protected String title;

  protected String link;
  protected Long timeToLive;

  /**
   * properties of content
   */
  protected ContentType contentType;

  protected String contentCode;

  protected String contentMessage;

  public String buildMessage() {
    EventMessageData eventMessageData = EventMessageData.builder()
        .id(applicationId)
        .notificationType(notificationType)
        .module(module)
        .title(title == null ? TITLE_DEFAULT : title)
        .link(link)
        .timeToLive(timeToLive)
        .content(buildContent())
        .createdAt(new Date())
        .build();

    return JsonUtil.convertObject2String(EventMessage.builder()
            .type(EventMessageType.WEBSOCKET)
            .data(eventMessageData)
            .build(),
        OBJECT_MAPPER);
  }

  protected String buildContent() {
    Content content = Content.builder()
        .type(contentType)
        .detail(ContentDetail.builder()
            .response(Response.builder()
                .code(contentCode)
                .message(contentMessage)
                .data(buildContentData())
                .build())
            .build())
        .build();
    return JsonUtil.convertObject2String(content, OBJECT_MAPPER);
  }

  abstract protected Object buildContentData();

}
