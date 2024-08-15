package com.msb.bpm.approval.appr.email.sender;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.REASONS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.TOKEN;
import static com.msb.bpm.approval.appr.constant.Constant.EMAIL_ADDRESS_MSB;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.CONTACT_RETURN;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.ParamsEmailCommon;
import com.msb.bpm.approval.appr.email.SenderStrategy;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FinishGenerateTemplateSenderStrategy extends AbstractBaseService
    implements SenderStrategy {

  private ConcurrentMap<String, Object> metadata;

  @Override
  public void publishEmail(String eventCode) {
    log.info("send FINISH_GENERATE_TEMPLATE");
    Map<String, Object> params = new HashMap<>();
    ApplicationEntity entityApp = (ApplicationEntity) metadata.get(APPLICATION);
    params.put(
        ParamsEmailCommon.CUSTOMER_NAME,
        getCustomerName(eventCode, (ApplicationEntity) metadata.get(APPLICATION)));
    params.put(ParamsEmailCommon.BPM_ID, entityApp.getBpmId());
    params.put(ParamsEmailCommon.DOCUMENT_CODE, metadata.get("DOCUMENT_CODE").toString());

    EmailRequest dataSend =
        EmailRequest.builder()
            .to(Arrays.asList(entityApp.getAssignee() + EMAIL_ADDRESS_MSB))
            .eventCode(EventCodeEmailType.FINISH_GENERATE_TEMPLATE.name())
            .params(params)
            .build();

    getEmailSender().sendInternalEmail(dataSend, eventCode);

    metadata.clear();
  }

  @Override
  public EventCodeEmailType getType() {
    return EventCodeEmailType.FINISH_GENERATE_TEMPLATE;
  }

  @Override
  public void setMetadata(ConcurrentMap<String, Object> metadata) {
    this.metadata = metadata;
  }
}
