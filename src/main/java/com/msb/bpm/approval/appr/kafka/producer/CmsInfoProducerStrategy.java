package com.msb.bpm.approval.appr.kafka.producer;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_ID;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_IDS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_APPROVAL_RB_INFO_KEY;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.util.Util.NOTE_CMS;

import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.constant.Constant;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.common.SourceApplication;
import com.msb.bpm.approval.appr.kafka.ProducerStrategy;
import com.msb.bpm.approval.appr.kafka.sender.ExternalKafkaSender;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredLoanDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredOverdraftDTO;
import com.msb.bpm.approval.appr.model.dto.kafka.cms.CmsInfoBaseDTO;
import com.msb.bpm.approval.appr.model.dto.kafka.cms.CmsInfoDTO;
import com.msb.bpm.approval.appr.model.dto.kafka.cms.PackageDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.FeedbackHistoryRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 20/6/2023, Tuesday
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class CmsInfoProducerStrategy implements ProducerStrategy {

  /**
   * @deprecated since 06/12/2023
   */
  @Deprecated
  private ConcurrentMap<String, Object> metadata;
  private static final String APPROVAL_Y = "Y";

  /**
   * @deprecated since 06/12/2023
   * @param metadata
   */
  @Deprecated
  public void setMetadata(ConcurrentMap<String, Object> metadata) {
    this.metadata = metadata;
  }
  private final ExternalKafkaSender kafkaSender;
  private final ApplicationRepository applicationRepository;
  private final FeedbackHistoryRepository feedbackHistoryRepository;
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

    String topicName = applicationConfig.getKafka().getTopic().get(BPM_APPROVAL_RB_INFO_KEY)
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
   * @deprecated since 06/12/2023
   *
   */
  @Override
  @Transactional(readOnly = true)
  @Deprecated
  public void publishMessage(String topicName) {
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
    CmsInfoBaseDTO dataSendKafka =  CmsInfoBaseDTO.builder().build();
    dataSendKafka.setTenantId(SourceApplication.CJBO.name());
    dataSendKafka.setTimestamp(LocalDateTime.now());
    dataSendKafka.setProcessId("9099090");
    CmsInfoDTO data = CmsInfoDTO.builder()
        .bpmId(applicationEntity.getBpmId())
        .status(applicationEntity.getStatus())
        .note(getNotes(applicationEntity))
        .landingPageId(applicationEntity.getRefId())
        .build();
    // Credits
    if (StringUtils.isNotBlank(applicationEntity.getStatus()) && applicationEntity.getStatus()
        .equalsIgnoreCase(ApplicationStatus.AS9999.getValue())) {
      List<UnsecuredCreditCardDTO> lstUnsecuredCreditCard = new ArrayList<>();
      applicationEntity.getCredits().stream().filter(e -> Objects.nonNull(e.getCreditCard())
          && APPROVAL_Y.equalsIgnoreCase(e.getApproveResult())).forEach(credit -> {
        UnsecuredCreditCardDTO unsecuredCreditCardDTO = UnsecuredCreditCardDTO.builder().build();
        unsecuredCreditCardDTO.setCreditType(credit.getCreditType());
        unsecuredCreditCardDTO.setLoanAmount(credit.getLoanAmount());
        unsecuredCreditCardDTO.setGuaranteeForm(credit.getGuaranteeForm());
        unsecuredCreditCardDTO.setLimitSustentivePeriod(Objects.nonNull(credit.getCreditCard()) ?
            credit.getCreditCard().getLimitSustentivePeriod(): null);
        lstUnsecuredCreditCard.add(unsecuredCreditCardDTO);
      });

      List<UnsecuredLoanDTO> lstUnsecuredLoanDTOS = new ArrayList<>();
      applicationEntity.getCredits().stream().filter(e -> Objects.nonNull(e.getCreditLoan())
          && APPROVAL_Y.equalsIgnoreCase(e.getApproveResult())).forEach(credit -> {
        UnsecuredLoanDTO unsecuredLoanDTO = UnsecuredLoanDTO.builder().build();
        unsecuredLoanDTO.setCreditType(credit.getCreditType());
        unsecuredLoanDTO.setLoanAmount(credit.getLoanAmount());
        unsecuredLoanDTO.setGuaranteeForm(credit.getGuaranteeForm());
        unsecuredLoanDTO.setLoanPeriod(credit.getCreditLoan().getLoanPeriod());
        unsecuredLoanDTO.setLoanPurpose(credit.getCreditLoan().getLoanPurpose());
        unsecuredLoanDTO.setCreditForm(credit.getCreditLoan().getCreditForm());
        lstUnsecuredLoanDTOS.add(unsecuredLoanDTO);
      });

      List<UnsecuredOverdraftDTO> lstUnsecuredOverdraftDTO = new ArrayList<>();
      applicationEntity.getCredits().stream().filter(e -> Objects.nonNull(e.getCreditOverdraft())
          && APPROVAL_Y.equalsIgnoreCase(e.getApproveResult())).forEach(credit -> {
        UnsecuredOverdraftDTO creditOverdraftDTO = UnsecuredOverdraftDTO.builder().build();
        creditOverdraftDTO.setCreditType(credit.getCreditType());
        creditOverdraftDTO.setLoanAmount(credit.getLoanAmount());
        creditOverdraftDTO.setGuaranteeForm(credit.getGuaranteeForm());
        creditOverdraftDTO.setLoanPurpose(credit.getCreditOverdraft().getLoanPurpose());
        creditOverdraftDTO.setLimitSustentivePeriod(Objects.nonNull(credit.getCreditOverdraft()) ?
            credit.getCreditOverdraft().getLimitSustentivePeriod(): null);
        lstUnsecuredOverdraftDTO.add(creditOverdraftDTO);
      });

      PackageDTO packageObj = PackageDTO.builder().build();
      packageObj.setUnsecuredCreditCard(lstUnsecuredCreditCard);
      packageObj.setUnsecuredLoan(lstUnsecuredLoanDTOS);
      packageObj.setUnsecuredOverdraft(lstUnsecuredOverdraftDTO);
      data.setPackageObj(packageObj);
    }
    dataSendKafka.setData(data);
    kafkaSender.sendMessage(dataSendKafka, topicName);
  }

  private String getNotes(ApplicationEntity applicationEntity) {
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
            notes.set(notes + String.format(NOTE_CMS, note.getFeedbackAt().format(DateTimeFormatter.ofPattern(Constant.DD_MM_YYYY_HH_MM_SS_FORMAT)), note.getUsername(),
            note.getUserRole(), note.getFeedbackContent()) + "\n"));
      }
    }
    log.info("getNotes END with bpmId={}, notes={}", applicationEntity.getBpmId(), notes);
    return notes.get();
  }
}
