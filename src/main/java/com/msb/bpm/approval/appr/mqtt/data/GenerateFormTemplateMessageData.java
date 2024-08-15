package com.msb.bpm.approval.appr.mqtt.data;

import com.msb.bpm.approval.appr.mqtt.content.ContentType;
import com.msb.bpm.approval.appr.mqtt.message.Module;
import com.msb.bpm.approval.appr.mqtt.message.NotificationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class GenerateFormTemplateMessageData extends MessageData {

  private String processingUserName;
  public GenerateFormTemplateMessageData(String applicationId, String processingUserName) {
    super(applicationId,
        NotificationType.BIZ,
        Module.mercury_rb,
        TITLE_DEFAULT,
        null,
        60L,
        ContentType.FORM_TEMPLATE_SUCCESS,
        "1",
        "Sinh mẫu biểu thành công.");
    this.processingUserName = processingUserName;
  }

  @Override
  protected Object buildContentData() {
    return new GenerateFormTemplateContent(applicationId, processingUserName);
  }
}
