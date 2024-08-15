package com.msb.bpm.approval.appr.mqtt.data;

import com.msb.bpm.approval.appr.mqtt.message.Module;
import com.msb.bpm.approval.appr.mqtt.message.NotificationType;
import com.msb.bpm.approval.appr.mqtt.content.ContentType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class CICRatioMessageData extends MessageData {

  private String processingUserName;

  public CICRatioMessageData(String applicationId) {
    super(applicationId,
        NotificationType.BIZ,
        Module.mercury_rb,
        TITLE_DEFAULT,
        null,
        60L,
        ContentType.CIC_RATIO,
        "1",
        "Lấy dữ liệu chỉ tiêu CIC thành công.");
  }

  public CICRatioMessageData(String applicationId, String processingUserName) {
    this(applicationId);
    this.processingUserName = processingUserName;
  }

  @Override
  protected Object buildContentData() {
    return new CICRatioContent(applicationId, processingUserName);
  }
}
