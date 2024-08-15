package com.msb.bpm.approval.appr.kafka.producer;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_ID;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_IDS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_FEEDBACK_APPLICATION_CMS;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.util.Util.NOTE_CMS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.general.GeneralInfoClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.constant.Constant;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.ApprovalResult;
import com.msb.bpm.approval.appr.enums.application.BusinessType;
import com.msb.bpm.approval.appr.enums.application.CreditType;
import com.msb.bpm.approval.appr.kafka.ProducerStrategy;
import com.msb.bpm.approval.appr.kafka.sender.DataKafkaSender;
import com.msb.bpm.approval.appr.model.dto.InitializeInfoDTO;
import com.msb.bpm.approval.appr.model.dto.kafka.cms.CmsLoanApplicationApprovedDTO;
import com.msb.bpm.approval.appr.model.dto.kafka.cms.CmsV2InfoBaseDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import com.msb.bpm.approval.appr.model.response.general.GeneralInfoResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.FeedbackHistoryRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CmsV2InfoProducerStrategy extends AbstractBaseService implements ProducerStrategy {

  /**
   * @deprecated since 06/12/2023
   */
  @Deprecated
  private ConcurrentMap<String, Object> metadata;

  /**
   * @deprecated since 06/12/2023
   * @param metadata
   */
  @Deprecated
  public void setMetadata(ConcurrentMap<String, Object> metadata) {
    this.metadata = metadata;
  }
  private final DataKafkaSender kafkaSender;
  private final ApplicationRepository applicationRepository;
  private final FeedbackHistoryRepository feedbackHistoryRepository;
  private final UserManagerClient userManagerClient;
  private final GeneralInfoClient generalInfoClient;
  private final ApplicationDraftRepository applicationDraftRepository;
  private final ObjectMapper objectMapper;
  private final ApplicationConfig applicationConfig;

  @Override
  @Transactional(readOnly = true)
  @Async
  public void publishMessage(ConcurrentMap<String, Object> metadata) {
    commonPublishMessage(metadata);
  }

  @Override
  @Transactional(readOnly = true)
  public void publishMessageEvent(ConcurrentMap<String, Object> metadata) {
    commonPublishMessage(metadata);
  }

  private void commonPublishMessage(ConcurrentMap<String, Object> metadata) {
    String applicationBpmId = (String) metadata.get(APPLICATION_BPM_ID);
    List<String> applicationBpmIds = (List<String>) metadata.get(APPLICATION_BPM_IDS);

    String topicName = applicationConfig.getKafka().getTopic().get(BPM_FEEDBACK_APPLICATION_CMS)
        .getTopicName();

    log.info(">>> Integrate broadcast, topic : {} , metadata : {}", topicName, metadata);

    if (StringUtils.isNotBlank(applicationBpmId)) {
      send(applicationBpmId, topicName, metadata);
    } else {
      for (String bpmId : applicationBpmIds) {
        send(bpmId, topicName, metadata);
      }
    }

    metadata.clear();
  }

  /**
   * @deprecated since 06/11/2023
   */
  @Override
  @Transactional(readOnly = true)
  @Deprecated
  public void publishMessage(String topicName) {
    log.info("publishMessage START with topicName={}", topicName);
    String applicationBpmId = (String) metadata.get(APPLICATION_BPM_ID);
    List<String> applicationBpmIds = (List<String>) metadata.get(APPLICATION_BPM_IDS);

    log.info(">>> Integrate broadcast, topic : {} , metadata : {}", topicName, metadata);

    if (StringUtils.isNotBlank(applicationBpmId)) {
      send(applicationBpmId, topicName, metadata);
    } else {
      for (String bpmId : applicationBpmIds) {
        send(bpmId, topicName, metadata);
      }
    }

    metadata.clear();
  }

  public void send(String applicationBpmId, String topicName, ConcurrentMap<String, Object> metadata) {
    ApplicationEntity applicationEntity = Objects.nonNull(applicationBpmId)
        ? applicationRepository.findByBpmId(applicationBpmId).orElse(null)
        : (ApplicationEntity) metadata.get(APPLICATION);

    if (Objects.isNull(applicationEntity)) {
      log.warn("Not found application data by {}, can not send data to general-info service!!!!", applicationBpmId);
      return;
    }

    // Lấy tên của trạng thái.
    String statusName = mapToStatusName(applicationEntity.getStatus());
    // Lấy danh sách mã văn bản
    List<String> regulatoryCode = setRegulatoryCodeFromDraftData(applicationEntity.getBpmId());
    if (org.apache.commons.lang3.StringUtils.isNotBlank(applicationEntity.getRegulatoryCode())) {
      regulatoryCode = Arrays.asList(applicationEntity.getRegulatoryCode().split(";"));
    }
    // Lấy thông tin User của RM xử lý hồ sơ.
    List<ApplicationHistoryApprovalEntity> applicationHistoryApprovalEntities =
        new ArrayList<>(applicationEntity.getHistoryApprovals());
    List<ApplicationHistoryApprovalEntity> appHistoryApprovals =
        getPreviousHistoryApprovalByRole(applicationHistoryApprovalEntities, PD_RB_RM);
    String userName = "";
    if (CollectionUtils.isNotEmpty(appHistoryApprovals)) {
      ApplicationHistoryApprovalEntity applicationHistoryApprovalEntity = appHistoryApprovals.get(0);
      userName = applicationHistoryApprovalEntity.getUsername();
    }
    GetUserProfileResponse userProfile = getUserProfile(userName);

    CmsV2InfoBaseDTO dataSendKafka =  CmsV2InfoBaseDTO.builder().build();
    dataSendKafka.setRefId(applicationEntity.getRefId());
    dataSendKafka.setBpmId(applicationEntity.getBpmId());
    dataSendKafka.setTransactionID(applicationEntity.getTransactionId());
    dataSendKafka.setSource(applicationEntity.getSource());
    //Set status
    if (ApplicationStatus.AS4003.getValue().equalsIgnoreCase(applicationEntity.getLdpStatus()) ||
        ApplicationStatus.AS4001.getValue().equalsIgnoreCase(applicationEntity.getLdpStatus())) {
      dataSendKafka.setStatus(applicationEntity.getLdpStatus());
    } else {
      dataSendKafka.setStatus(applicationEntity.getStatus());
    }
    dataSendKafka.setName(statusName);
    dataSendKafka.setRegulatoryCode(regulatoryCode);
    dataSendKafka.setUsername(userName);
    if (Objects.nonNull(userProfile)) {
       dataSendKafka.setFullnameRM(Objects.nonNull(userProfile.getPersonal()) ? userProfile.getPersonal().getFullName() : "");
      if (Objects.nonNull(userProfile.getUser())) {
        dataSendKafka.setEmail(userProfile.getUser().getEmail());
        dataSendKafka.setPhone(userProfile.getUser().getPhoneNumber());
      }
    }
    // Lấy thông tin loanApplicationApproved
    if (org.apache.commons.lang3.StringUtils.isNotBlank(applicationEntity.getStatus()) && (applicationEntity.getStatus()
        .equalsIgnoreCase(ApplicationStatus.AS9999.getValue()))) {
      List<CmsLoanApplicationApprovedDTO> loanApps = getLoanApplicationApproved(applicationEntity);
      dataSendKafka.setLoanApplicationApproved(loanApps);
    } else  {
      dataSendKafka.setLoanApplicationApproved(null);
    }
    dataSendKafka.setResponseDate(LocalDateTime.now());
    dataSendKafka.setNote(getNotes(applicationEntity));
    // Set data feedback null
    dataSendKafka.setCommentData(null);
    dataSendKafka.setApplicationData(null);
    dataSendKafka.setProcessingRole(applicationEntity.getProcessingRole());
    dataSendKafka.setPreviousRole(applicationEntity.getPreviousRole());
    log.info("START sending application [{}] with data: [{}] to topic [{}]", applicationEntity.getBpmId(), JsonUtil.convertObject2String(dataSendKafka, objectMapper), topicName);
    kafkaSender.sendMessage(dataSendKafka, topicName);
    log.info("FINISH sending application [{}] to topic [{}]", applicationEntity.getBpmId(), topicName);
  }

  private List<CmsLoanApplicationApprovedDTO> getLoanApplicationApproved(ApplicationEntity applicationEntity) {
    log.info("START method: getLoanApplicationApproved");
    List<CmsLoanApplicationApprovedDTO> loanApps = new ArrayList<>();
    applicationEntity.getCredits().stream().filter(e -> ApprovalResult.Y.name().equalsIgnoreCase(e.getApproveResult())).forEach(credit -> {
        CmsLoanApplicationApprovedDTO loanApplicationApproved = new CmsLoanApplicationApprovedDTO();
        loanApplicationApproved.setCreditType(credit.getCreditType());
        loanApplicationApproved.setGuaranteeForm(credit.getGuaranteeForm());
        loanApplicationApproved.setLoanPurpose(Objects.nonNull(credit.getCreditLoan()) ?
            credit.getCreditLoan().getLoanPurpose(): null);
        loanApplicationApproved.setCreditForm(Objects.nonNull(credit.getCreditLoan()) ?
            credit.getCreditLoan().getCreditForm(): null);
        loanApplicationApproved.setLoanAmount(credit.getLoanAmount());
        loanApplicationApproved.setLimitSustentivePeriod(Objects.nonNull(credit.getCreditCard()) ?
            credit.getCreditCard().getLimitSustentivePeriod(): null);
      if (CreditType.CARD.getCode().equals(loanApplicationApproved.getCreditType()) && Objects.nonNull(credit.getCreditCard())) {
        loanApplicationApproved.setLiabilityContract(credit.getCreditCard().getContractL());
        loanApplicationApproved.setIssueContract(credit.getCreditCard().getIssuingContract());
        loanApplicationApproved.setCreatedDate(credit.getCreditCard().getCreatedDate());
      }
        loanApps.add(loanApplicationApproved);
      });
    log.info("END method: getLoanApplicationApproved");
    return loanApps;
  }

  public String getNotes(ApplicationEntity applicationEntity) {
    log.info("getNotes START with bpmId={}, previousRole={}", applicationEntity.getBpmId(),
        applicationEntity.getPreviousRole());
    AtomicReference<String> notes = new AtomicReference<>("");
    List<ApplicationHistoryFeedbackEntity> feedbackEntities = feedbackHistoryRepository.
        findByApplicationBpmId(applicationEntity.getBpmId()).orElse(null);
    if (CollectionUtils.isNotEmpty(feedbackEntities)) {
      feedbackEntities = feedbackEntities.stream().filter(
              e -> StringUtils.isNotBlank(e.getUserRole()) &&
                  e.getUserRole().equalsIgnoreCase(String.valueOf(PD_RB_RM)))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(feedbackEntities)) {
        feedbackEntities.stream().forEach(note ->
            notes.set(notes + String.format(NOTE_CMS, note.getFeedbackAt().format(
                    DateTimeFormatter.ofPattern(Constant.DD_MM_YYYY_HH_MM_SS_FORMAT)), note.getUsername(),
                note.getUserRole(), note.getFeedbackContent()) + "\n"));
      }
    }
    log.info("getNotes END with bpmId={}, notes={}", applicationEntity.getBpmId(), notes);
    return notes.get();
  }

  public GetUserProfileResponse getUserProfile(String username) {
    if (StringUtils.isBlank(username)) {
      return null;
    }
    try {
      return userManagerClient.getUserByUsername(username);
    } catch (Exception e) {
      log.error("Get user profile by username failure : ", e);
      return null;
    }
  }

  public String mapToStatusName(String status) {
    log.info("mapToStatusName start with status={}", status);
    try {
      List<GeneralInfoResponse> generalInfos = generalInfoClient.getGeneralInfo();
      if (CollectionUtils.isEmpty(generalInfos)) {
        return null;
      }
      String name = generalInfos.stream()
          .filter(info -> info.getBusinessType().equals(BusinessType.PD_RB.getValue()))
          .filter(info -> info.getCode().equals(status))
          .findFirst()
          .map(GeneralInfoResponse::getName)
          .orElse(null);
      log.info("mapToStatusName end with status={}", status);
      return name;
    } catch (Exception e) {
      log.error("mapToStatusName failure : ", e);
      return null;
    }
  }
  private List<String> setRegulatoryCodeFromDraftData(String bpmId) {
    log.info("CmsV2InfoProducerStrategy.setRegulatoryCodeFromDraftData with bpmId={}", bpmId);
    List<String> regulatoryCode = new ArrayList<>();
    ApplicationDraftEntity applicationDraftEntity = applicationDraftRepository.findByBpmIdAndTabCode(bpmId, INITIALIZE_INFO).orElse(null);
    if (Objects.nonNull(applicationDraftEntity) && Objects.nonNull(applicationDraftEntity.getData())) {
      InitializeInfoDTO initializeInfo = JsonUtil.convertBytes2Object(applicationDraftEntity.getData(), InitializeInfoDTO.class, objectMapper);
      if (Objects.nonNull(initializeInfo) && Objects.nonNull(initializeInfo.getApplication()) &&
          UNFINISHED == applicationDraftEntity.getStatus() && StringUtils.isNotEmpty(initializeInfo.getApplication().getRegulatoryCode())) {
        return Arrays.asList(initializeInfo.getApplication().getRegulatoryCode().split(";"));
      }
    }
    return regulatoryCode;
  }

}
