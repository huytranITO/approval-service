package com.msb.bpm.approval.appr.email.sender;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.LDP_CONFIRM_STATUS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.LDP_REASON;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.TOKEN;
import static com.msb.bpm.approval.appr.constant.Constant.EMAIL_ADDRESS_MSB;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.LDP_SEND_CONFIRM_CUSTOMER;

import com.msb.bpm.approval.appr.constant.ApplicationConstant.ParamsEmailCommon;
import com.msb.bpm.approval.appr.email.SenderStrategy;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.common.LdpConfirmStatus;
import com.msb.bpm.approval.appr.enums.email.ConfirmStatus;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class LdpSendConfirmCustomerSenderStrategy extends AbstractBaseService implements
    SenderStrategy {
  private ConcurrentMap<String, Object> metadata;
  @Override
  public EventCodeEmailType getType() {
    return LDP_SEND_CONFIRM_CUSTOMER;
  }
  @Override
  public void setMetadata(ConcurrentMap<String, Object> metadata) {
    this.metadata = metadata;
  }
  @Override
  @Transactional(readOnly = true)
  public void publishEmail(String eventCode) {
    log.info("publishEmail START send email with eventCode={}", eventCode);
    ApplicationEntity entityApp = getCommonService().getAppEntity(metadata);
    Map<String, Object> params = new HashMap<>();
    params.put(ParamsEmailCommon.NAME_OF_CUSTOMER, getCustomerName(eventCode, entityApp));
    params.put(ParamsEmailCommon.APPLICATION_SOURCE, entityApp.getSource());
    params.put(ParamsEmailCommon.APPLICATION_BPM_ID, entityApp.getBpmId());
    params.put(ParamsEmailCommon.LDP_CONFIRM_STATUS, getConfirmStatus(metadata.get(LDP_CONFIRM_STATUS).toString()));
    params.put(ParamsEmailCommon.LDP_REASON, metadata.get(LDP_REASON).toString());

    // Get cc email addresses
    List<String> lstEmailCc = new ArrayList<>();
    List<ApplicationHistoryApprovalEntity> approvalHistories = getPreviousHistoryApprovalByRole(
        new ArrayList<>(entityApp.getHistoryApprovals()), ProcessingRole.PD_RB_BM);
    if(CollectionUtils.isNotEmpty(approvalHistories)
        && Objects.nonNull(approvalHistories.get(0).getUsername())) {
      lstEmailCc.add(approvalHistories.get(0).getUsername() + EMAIL_ADDRESS_MSB);
    }
    // Create email request for evenCode LDP send confirm customer
    EmailRequest dataSend = EmailRequest.builder()
        .to(Collections.singletonList(entityApp.getAssignee() + EMAIL_ADDRESS_MSB))
        .cc(lstEmailCc)
        .eventCode(LDP_SEND_CONFIRM_CUSTOMER.name())
        .params(params)
        .build();

    getEmailSender().sendEmail(dataSend, eventCode, metadata.get(TOKEN).toString());

    metadata.clear();
  }

  private String getConfirmStatus(String confirmStatus) {
    String resp = confirmStatus;
    if (LdpConfirmStatus.CONFIRM.name().equalsIgnoreCase(confirmStatus)) {
      resp =  ConfirmStatus.CONFIRM_STATUS.getValue();
    }
    if (LdpConfirmStatus.REJECT.name().equalsIgnoreCase(confirmStatus)) {
      resp = ConfirmStatus.REJECT_STATUS.getValue();
    }
    if (LdpConfirmStatus.EXPIRED.name().equalsIgnoreCase(confirmStatus)) {
      resp = ConfirmStatus.EXPIRED_STATUS.getValue();
    }
    return resp;
  }
}
