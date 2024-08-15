package com.msb.bpm.approval.appr.email.sender;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.REASONS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.TOKEN;
import static com.msb.bpm.approval.appr.constant.Constant.EMAIL_ADDRESS_MSB;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.CLOSE;

import com.msb.bpm.approval.appr.constant.ApplicationConstant.ParamsEmailCommon;
import com.msb.bpm.approval.appr.email.SenderStrategy;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 22/5/2023, Monday
 **/
@Component
@Slf4j
public class CloseAppSenderStrategy extends AbstractBaseService implements SenderStrategy {
  private ConcurrentMap<String, Object> metadata;

  @Override
  public void setMetadata(ConcurrentMap<String, Object> metadata) {
    this.metadata = metadata;
  }

  @Override
  public EventCodeEmailType getType() {
    return CLOSE;
  }

  @Override
  @Transactional(readOnly = true)
  public void publishEmail(String eventCode) {
    log.info("publishEmail START send email with eventCode={}", eventCode);
    ApplicationEntity entityApp = getCommonService().getAppEntity(metadata);
    Map<String, Object> params = new HashMap<>();
    params.put(ParamsEmailCommon.CUSTOMER_NAME, getCustomerName(eventCode, entityApp));
    params.put(ParamsEmailCommon.BPM_ID, entityApp.getBpmId());
    params.put(ParamsEmailCommon.REASON, getReasonReturn(eventCode,
        entityApp.getBpmId(), (Set<String>) metadata.get(REASONS)));

    // get cc received mail
    String emailCc = null;
    List<ApplicationHistoryApprovalEntity> approvalHistories = getPreviousHistoryApprovalByRole(
        new ArrayList<>(entityApp.getHistoryApprovals()), ProcessingRole.PD_RB_BM);
    if(CollectionUtils.isNotEmpty(approvalHistories)
        && Objects.nonNull(approvalHistories.get(0).getUsername())) {
      emailCc = approvalHistories.get(0).getUsername();
    }

    String emailTo = null;
    approvalHistories = getPreviousHistoryApprovalByRole(
        new ArrayList<>(entityApp.getHistoryApprovals()), ProcessingRole.PD_RB_RM);
    if(CollectionUtils.isNotEmpty(approvalHistories)
        && Objects.nonNull(approvalHistories.get(0).getUsername())) {
      emailTo = approvalHistories.get(0).getUsername() + EMAIL_ADDRESS_MSB;
    }

    EmailRequest dataSend = EmailRequest.builder()
        .to(Arrays.asList(entityApp.getCreatedBy() + EMAIL_ADDRESS_MSB))
        .eventCode(CLOSE.name())
        .params(params)
        .build();
    if (StringUtils.isNotBlank(emailCc) && StringUtils.isNotBlank(entityApp.getAssignee())
        && !entityApp.getAssignee().equalsIgnoreCase(emailCc)) {
      dataSend.setCc(Arrays.asList(emailCc + EMAIL_ADDRESS_MSB));
    }
    if(StringUtils.isNotBlank(emailTo)) {
      dataSend.setTo(Arrays.asList(emailTo));
    }
    getEmailSender().sendEmail(dataSend, eventCode, metadata.get(TOKEN).toString());

    metadata.clear();
  }
}
