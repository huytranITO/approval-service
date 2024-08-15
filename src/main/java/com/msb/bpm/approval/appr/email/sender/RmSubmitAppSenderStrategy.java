package com.msb.bpm.approval.appr.email.sender;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.TOKEN;
import static com.msb.bpm.approval.appr.constant.Constant.EMAIL_ADDRESS_MSB;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.RM_SUBMIT;

import com.msb.bpm.approval.appr.constant.ApplicationConstant.ParamsEmailCommon;
import com.msb.bpm.approval.appr.email.SenderStrategy;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/5/2023, Friday
 **/
@Component
@Slf4j
public class RmSubmitAppSenderStrategy extends AbstractBaseService implements SenderStrategy {
  private ConcurrentMap<String, Object> metadata;

  @Override
  public void setMetadata(ConcurrentMap<String, Object> metadata) {
    this.metadata = metadata;
  }

  @Override
  public EventCodeEmailType getType() {
    return RM_SUBMIT;
  }

  @Override
  @Transactional(readOnly = true)
  public void publishEmail(String eventCode) {
    log.info("publishEmail START send email with eventCode={}", eventCode);
    ApplicationEntity entityApp = getCommonService().getAppEntity(metadata);
    Map<String, Object> params = new HashMap<>();
    params.put(ParamsEmailCommon.CUSTOMER_NAME, getCustomerName(eventCode, entityApp));
    params.put(ParamsEmailCommon.BPM_ID, entityApp.getBpmId());
    params.put(ParamsEmailCommon.ASSIGNEE, entityApp.getAssignee());
    EmailRequest dataSend = EmailRequest.builder()
        .to(Arrays.asList(entityApp.getCreatedBy() + EMAIL_ADDRESS_MSB))
        .cc(Arrays.asList(entityApp.getAssignee() + EMAIL_ADDRESS_MSB))
        .eventCode(RM_SUBMIT.name())
        .params(params)
        .build();

    getEmailSender().sendEmail(dataSend, eventCode, metadata.get(TOKEN).toString());

    metadata.clear();
  }
}
