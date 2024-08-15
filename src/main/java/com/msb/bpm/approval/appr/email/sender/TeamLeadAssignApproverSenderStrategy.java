package com.msb.bpm.approval.appr.email.sender;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.*;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_IDS;
import static com.msb.bpm.approval.appr.constant.Constant.EMAIL_ADDRESS_MSB;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.TEAM_LEAD_ASSIGN_APPROVER;

import com.msb.bpm.approval.appr.constant.ApplicationConstant.ParamsEmailCommon;
import com.msb.bpm.approval.appr.email.SenderStrategy;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 22/5/2023, Monday
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class TeamLeadAssignApproverSenderStrategy extends AbstractBaseService implements SenderStrategy {
  private ConcurrentMap<String, Object> metadata;

  private final CommonService commonService;

  @Override
  public void setMetadata(ConcurrentMap<String, Object> metadata) {
    this.metadata = metadata;
  }

  @Override
  public EventCodeEmailType getType() {
    return TEAM_LEAD_ASSIGN_APPROVER;
  }

  @Override
  @Transactional(readOnly = true)
  public void publishEmail(String eventCode) {
    log.info("publishEmail START send email with eventCode={}", eventCode);

    if (metadata.containsKey(APPLICATION_BPM_ID)){
      ApplicationEntity entityApp = getCommonService().getAppEntity(metadata);
      sendMail(entityApp,eventCode);
    }
    if (metadata.containsKey(APPLICATION_BPM_IDS)){
      List<String> bpmIs = (List<String>) metadata.get(APPLICATION_BPM_IDS);
      bpmIs.forEach(bpmId -> sendMail(commonService.findAppByBpmId(bpmId),eventCode));
    }

    metadata.clear();
  }

  private void sendMail(ApplicationEntity entityApp, String eventCode){
    Map<String, Object> params = new HashMap<>();
    params.put(ParamsEmailCommon.CUSTOMER_NAME, getCustomerName(eventCode, entityApp));
    params.put(ParamsEmailCommon.BPM_ID, entityApp.getBpmId());
    params.put(ParamsEmailCommon.ASSIGNEE, entityApp.getAssignee());
    params.put(ParamsEmailCommon.ASSIGNER, SecurityContextUtil.getUsername());

    // get cc received mail
    List<String> lstEmailCc = new ArrayList<>();
    List<ApplicationHistoryApprovalEntity> approvalHistories = getPreviousHistoryApprovalByRole(
            new ArrayList<>(entityApp.getHistoryApprovals()), ProcessingRole.PD_RB_BM);
    if(CollectionUtils.isNotEmpty(approvalHistories)
            && Objects.nonNull(approvalHistories.get(0).getUsername())) {
      lstEmailCc.add(approvalHistories.get(0).getUsername() + EMAIL_ADDRESS_MSB);
    }
    approvalHistories = getPreviousHistoryApprovalByRole(
            new ArrayList<>(entityApp.getHistoryApprovals()), ProcessingRole.PD_RB_RM);
    if(CollectionUtils.isNotEmpty(approvalHistories)
            && Objects.nonNull(approvalHistories.get(0).getUsername())) {
      lstEmailCc.add(approvalHistories.get(0).getUsername() + EMAIL_ADDRESS_MSB);
    }

    EmailRequest dataSend = EmailRequest.builder()
            .to(Arrays.asList(entityApp.getAssignee() + EMAIL_ADDRESS_MSB))
            .cc(lstEmailCc)
            .eventCode(TEAM_LEAD_ASSIGN_APPROVER.name())
            .params(params)
            .build();

    getEmailSender().sendEmail(dataSend, eventCode, metadata.get(TOKEN).toString());
  }
}
