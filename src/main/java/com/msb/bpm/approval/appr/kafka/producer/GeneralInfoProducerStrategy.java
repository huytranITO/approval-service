package com.msb.bpm.approval.appr.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.camunda.CamundaClient;
import com.msb.bpm.approval.appr.client.camunda.CamundaProperties;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.constant.MessageCode;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.application.DistributionForm;
import com.msb.bpm.approval.appr.enums.camunda.CamundaTopic;
import com.msb.bpm.approval.appr.enums.camunda.CamundaVariable;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.factory.ApplicationCreditReverseFactory;
import com.msb.bpm.approval.appr.kafka.ProducerStrategy;
import com.msb.bpm.approval.appr.kafka.sender.GeneralKafkaSender;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.DebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.InitializeInfoDTO;
import com.msb.bpm.approval.appr.model.dto.kafka.GeneralInfoDTO;
import com.msb.bpm.approval.appr.model.dto.kafka.ProcessPropsDTO;
import com.msb.bpm.approval.appr.model.dto.kafka.ReasonDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.ProcessInstanceEntity;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.StringUtil;
import com.msb.bpm.approval.appr.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.FINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_ID;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_IDS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.POST_BASE_REQUEST;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.GENERAL_INFO_KEY;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.CLOSE_APPLICATION;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.FLOW_COMPLETE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.DOCUMENT_CODE;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.DISTRIBUTION_FORM;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.PROCESSING_ROLE;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.PROCESSING_STEP;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.PROCESS_FLOW;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.RECEPTION_AT;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.REGION;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.REGULATORY_CODE;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.SUBMISSION_PURPOSE;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 4/5/2023, Thursday
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class GeneralInfoProducerStrategy extends AbstractBaseService implements ProducerStrategy {
  private static final String PROCESSING_ROLE_SEARCH_KEY = "processRole";
  private static final String BU_NAME = "buName";
  private static final String GUARANTEE_FORM_CODE = "guaranteeForm.code";
  private static final String GUARANTEE_FORM_VALUE = "guaranteeForm.value";
  private static final String DOCUMENT_CODE_CODE = "documentCode.code";
  private static final String DOCUMENT_CODE_VALUE = "documentCode.value";
  private static final String LOAN_PURPOSE_CODE = "loanPurpose.code";
  private static final String LOAN_PURPOSE_VALUE = "loanPurpose.value";
  private final MessageSource messageSource;

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

  @Qualifier("objectMapperWithNull")
  private final ObjectMapper objectMapper;
  private final GeneralKafkaSender generalKafkaSender;
  private final ApplicationRepository applicationRepository;
  private final CamundaClient camundaClient;
  private final CamundaProperties properties;
  private final ConfigurationServiceCache configurationServiceCache;
  private final ApplicationDraftRepository applicationDraftRepository;
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

    String topicName = applicationConfig.getKafka().getTopic().get(GENERAL_INFO_KEY).getTopicName();

    log.info(">>> Integrate general-info, topic : {} , metadata : {}", topicName, metadata);

    if (StringUtils.isNotBlank(applicationBpmId)) {
      send(applicationBpmId, topicName, metadata);
    } else if (CollectionUtils.isNotEmpty(applicationBpmIds)) {
      for (String bpmId : applicationBpmIds) {
        send(bpmId, topicName, metadata);
      }
    }

    metadata.clear();
  }

  /**
   * @deprecated since 06/11/2023
   */
  @Deprecated
  @Override
  @Transactional(readOnly = true)
  public void publishMessage(String topicName) {
    String applicationBpmId = (String) metadata.get(APPLICATION_BPM_ID);
    List<String> applicationBpmIds = (List<String>) metadata.get(APPLICATION_BPM_IDS);

    log.info(">>> Integrate general-info, topic : {} , metadata : {}", topicName, metadata);

    if (StringUtils.isNotBlank(applicationBpmId)) {
      send(applicationBpmId, topicName, metadata);
    } else if (CollectionUtils.isNotEmpty(applicationBpmIds)) {
      for (String bpmId : applicationBpmIds) {
        send(bpmId, topicName, metadata);
      }
    }

    metadata.clear();
  }

  public void send(String applicationBpmId, String topicName, ConcurrentMap<String, Object> metadata) {
    try {
      ApplicationEntity applicationEntity = Objects.nonNull(applicationBpmId)
          ? applicationRepository.findByBpmId(applicationBpmId).orElse(null)
          : (ApplicationEntity) metadata.get(APPLICATION);

      if (Objects.isNull(applicationEntity)) {
        log.warn("Not found application data by {}, can not send data to general-info service!!!!", applicationBpmId);
        return;
      }

      PostBaseRequest postBaseRequest = (PostBaseRequest) metadata.get(POST_BASE_REQUEST);

      setDraftData(postBaseRequest, applicationEntity);

      Map<String, Object> props = buildProps(applicationEntity);

      GeneralInfoDTO dataSend = GeneralInfoDTO.builder()
          .requestCode(applicationEntity.getBpmId())
          .customerName(applicationEntity.getCustomer().getIndividualCustomer().getLastName() + " " +
              applicationEntity.getCustomer().getIndividualCustomer().getFirstName())
          .customerType(applicationEntity.getCustomer().getCustomerType())
          .businessName(ApplicationConstant.BUSINESS_NAME)
          .identityId(getIdentifierCode(applicationEntity))
          .createdBy(applicationEntity.getCreatedBy())
          .createdAt(
              Timestamp.valueOf(applicationEntity.getCreatedAt()))
          .status((applicationEntity.getLdpStatus() != null &&  !applicationEntity.getLdpStatus().equals(ApplicationStatus.AS4000.getValue())) ? applicationEntity.getLdpStatus(): applicationEntity.getStatus())
          .modifiedAt(
              Timestamp.valueOf(applicationEntity.getUpdatedAt()))
          .cif(applicationEntity.getCustomer().getCif())
          .loanAmount(String.valueOf(null == applicationEntity.getSuggestedAmount() ? "0" :
              applicationEntity.getSuggestedAmount()))
          .businessType("PD_RB_SBO_AUTO")
          .modifiedBy(applicationEntity.getUpdatedBy())
          .assignee(StringUtils.isNotBlank(applicationEntity.getAssignee()) ? applicationEntity.getAssignee() : null)
          .source(applicationEntity.getSource())
          .refId(applicationEntity.getRefId())
          .customerPhoneNumber(applicationEntity.getCustomer().getIndividualCustomer().getPhoneNumber())
          .phoneNumber(applicationEntity.getCustomer().getIndividualCustomer().getPhoneNumber())
          .customerSourceCode(applicationEntity.getRefId())
          .branch(applicationEntity.getBranchCode())
          .businessUnitCode(applicationEntity.getBusinessCode())
          .specializedBank(CustomerType.RB.name())
          .segment(applicationEntity.getSegment())
          .creditType(getCreditType(applicationEntity))
          .productCode(getProductCode(applicationEntity))
          .props(JsonUtil.convertObject2String(props, objectMapper))
          .taskDefinitionKeyName(ApplicationStatus.AS9999.getValue().equalsIgnoreCase(applicationEntity.getStatus()) ? messageSource.getMessage(FLOW_COMPLETE.getValue(), null, Util.locale()) : applicationEntity.getProcessingStep())
          .processProps(getProcessProps(applicationEntity))
          .build();

      if (dataSend.getAssignee() == null) {
        dataSend.setFieldAllowNull("username");
      }

      setProcessInstance(applicationEntity, dataSend);

      generalKafkaSender.sendMessage(dataSend, dataSend.getRequestCode(), topicName);
    } catch (Exception e) {
      log.error("GeneralInfoProducerStrategy failure when push data to kafka : {}, ", applicationBpmId, e);
    }
  }

  private String getProductCode(ApplicationEntity applicationEntity) {
    log.info("START method: GeneralInfoProducerStrategy.getProductCode with bpmId={}", applicationEntity.getBpmId());
    String productCode = "";
    List<String> codes = new ArrayList<>();
    if(CollectionUtils.isNotEmpty(applicationEntity.getCredits())) {
      applicationEntity.getCredits().forEach(cr -> {
        if (Objects.nonNull(cr.getCreditCard()) && StringUtils.isNotBlank(cr.getCreditCard().getCardPolicyCode())) {
          codes.add(cr.getCreditCard().getCardPolicyCode());
        }
        if (Objects.nonNull(cr.getCreditLoan()) && StringUtils.isNotBlank(cr.getCreditLoan().getProductCode())) {
          codes.add(cr.getCreditLoan().getProductCode());
        }
        if (Objects.nonNull(cr.getCreditOverdraft()) && StringUtils.isNotBlank(cr.getCreditOverdraft().getInterestRateCode())) {
          codes.add(cr.getCreditOverdraft().getInterestRateCode());
        }
      });
      if (CollectionUtils.isNotEmpty(codes)) {
        productCode = String.join(",", codes);
      }
    }
    log.info("END method: GeneralInfoProducerStrategy.getProductCode with response = {}", productCode);
    return productCode;
  }
  private String getCreditType(ApplicationEntity applicationEntity) {
    log.info("START method: GeneralInfoProducerStrategy.getCreditType with bpmId={}", applicationEntity.getBpmId());
    String creditType = "";
    List<String> codes = new ArrayList<>();
    Set<ApplicationCreditEntity> credits = applicationEntity.getCredits();
    if(CollectionUtils.isNotEmpty(credits)) {
      credits.forEach(x -> codes.add(x.getCreditType()));
      if (CollectionUtils.isNotEmpty(codes)) {
        creditType = String.join(",", codes);
      }
    }
    log.info("END method: GeneralInfoProducerStrategy.getCreditType with response = {}", creditType);
    return creditType;
  }

  private void setDraftData(PostBaseRequest postBaseRequest, ApplicationEntity applicationEntity) {
    if (postBaseRequest == null) {
      return;
    }

    if (postBaseRequest instanceof PostInitializeInfoRequest) {
      PostInitializeInfoRequest postInitializeInfoRequest = (PostInitializeInfoRequest) postBaseRequest;
      setInitializeInfoData(applicationEntity, postInitializeInfoRequest.getApplication(),
          postInitializeInfoRequest.getCustomerAndRelationPerson());
    } else {
      InitializeInfoDTO initializeInfo = JsonUtil.convertBytes2Object(
          getDraftData(applicationEntity.getBpmId()), InitializeInfoDTO.class,
          objectMapper);
      if (initializeInfo != null) {
        setInitializeInfoData(applicationEntity, initializeInfo.getApplication(),
            initializeInfo.getCustomerAndRelationPerson());
      }
    }

    if (postBaseRequest instanceof PostDebtInfoRequest) {
      PostDebtInfoRequest postDebtInfoRequest = (PostDebtInfoRequest) postBaseRequest;
      setDebtInfoData(applicationEntity, postDebtInfoRequest.getCredits());
    } else {
      DebtInfoDTO debtInfo = JsonUtil.convertBytes2Object(
          getDraftData(applicationEntity.getBpmId()), DebtInfoDTO.class, objectMapper);
      if (debtInfo != null) {
        setDebtInfoData(applicationEntity, debtInfo.getCredits());
      }
    }
  }

  private byte[] getDraftData(String bpmId) {
    ApplicationDraftEntity initializeDraftEntity = applicationDraftRepository.findByBpmIdAndTabCode(
            bpmId, INITIALIZE_INFO)
        .orElse(null);
    if (initializeDraftEntity == null || FINISHED == initializeDraftEntity.getStatus()) {
      return new byte[0];
    }
    return initializeDraftEntity.getData();
  }

  private void setInitializeInfoData(ApplicationEntity applicationEntity,
      ApplicationDTO application, CustomerAndRelationPersonDTO customerAndRelationPerson) {
    applicationEntity.setSubmissionPurpose(application.getSubmissionPurpose());
    applicationEntity.setRegionCode(application.getRegionCode());
    applicationEntity.setProcessFlow(application.getProcessFlow());
    IndividualCustomerDTO individualCustomerDTO = (IndividualCustomerDTO) customerAndRelationPerson.getCustomer();
    applicationEntity.getCustomer().getIndividualCustomer()
        .setLastName(Util.splitLastName(individualCustomerDTO.getFullName()));
    applicationEntity.getCustomer().getIndividualCustomer()
        .setFirstName(Util.splitFirstName(individualCustomerDTO.getFullName()));
    applicationEntity.getCustomer().setCif(individualCustomerDTO.getCif());
  }

  private void setDebtInfoData(ApplicationEntity applicationEntity, Set<ApplicationCreditDTO> credits) {
    DistributionForm distributionForm = getDistributionForm(credits);
    applicationEntity.setDistributionFormCode(distributionForm != null ? distributionForm.getCode() : null);
    applicationEntity.setRegulatoryCode(buildRegulationCode(credits));
    applicationEntity.setSuggestedAmount(calculateSuggestedAmount(credits));

    if (CollectionUtils.isNotEmpty(credits)) {
      Set<ApplicationCreditEntity> creditEntities = new HashSet<>();
      credits.forEach(credit -> {
        if (StringUtils.isNotBlank(credit.getCreditType())) {
          creditEntities.add(
              ApplicationCreditReverseFactory.getApplicationCredit(credit.getCreditType())
                  .build(credit));
        }
      });
      applicationEntity.setCredits(creditEntities);
    }
  }

  private String getIdentifierCode(ApplicationEntity response) {
    return response.getCustomer().getCustomerIdentitys()
        .stream()
        .filter(CustomerIdentityEntity::isPriority)
        .findFirst()
        .map(CustomerIdentityEntity::getIdentifierCode)
        .orElse(null);
  }

  private Map<String, Object> buildProps(ApplicationEntity applicationEntity) {
    Map<String, Object> props = new HashMap<>();
    String documentCodes = getDocumentCode(applicationEntity);
    props.put(SUBMISSION_PURPOSE, StringUtils.isNotBlank(applicationEntity.getSubmissionPurpose())
        ? applicationEntity.getSubmissionPurpose() : "_");
    props.put(REGION, StringUtils.isNotBlank(applicationEntity.getRegionCode())
        ? applicationEntity.getRegionCode() : "_");
    props.put(DISTRIBUTION_FORM, StringUtils.isNotBlank(applicationEntity.getDistributionFormCode())
        ? applicationEntity.getDistributionFormCode() : "_");
    props.put(REGULATORY_CODE, StringUtils.isNotBlank(applicationEntity.getRegulatoryCode())
        ? applicationEntity.getRegulatoryCode() : "_");
    props.put(PROCESSING_ROLE_SEARCH_KEY,
        ApplicationStatus.isComplete(applicationEntity.getStatus())
            ? "_" : applicationEntity.getProcessingRole());
    props.put(PROCESSING_ROLE, ApplicationStatus.isComplete(applicationEntity.getStatus())
        ? "_" : applicationEntity.getProcessingRole());
    props.put(PROCESS_FLOW, StringUtils.isNotBlank(applicationEntity.getProcessFlow())
        ? applicationEntity.getProcessFlow() : "_");
    props.put(PROCESSING_STEP, ApplicationStatus.isComplete(applicationEntity.getStatus())
        ? "_" : applicationEntity.getProcessingStep());
    props.put(RECEPTION_AT, applicationEntity.getReceptionAt());
    props.put(BU_NAME, applicationEntity.getBusinessUnit());
    props.put(GUARANTEE_FORM_CODE, getGuaranteeFormCode(applicationEntity));
    props.put(GUARANTEE_FORM_VALUE, getGuaranteeFormValue(applicationEntity));
    props.put(DOCUMENT_CODE_CODE, documentCodes);
    props.put(DOCUMENT_CODE_VALUE, getDocumentValue(documentCodes));
    props.put(LOAN_PURPOSE_CODE, getLoanPurposeCode(applicationEntity));
    props.put(LOAN_PURPOSE_VALUE, getLoanPurposeValue(applicationEntity));
    return props;
  }

  private String getDocumentValue(String documentCodes) {
    log.info("START method: GeneralInfoProducerStrategy.getDocumentValue with documentCodes={}", documentCodes);

    String documentValue = "";
    if (StringUtils.isBlank(documentCodes)) {
      return documentValue;
    }
    List<CategoryDataResponse> categoryDataResponses = getCategories();
    if (CollectionUtils.isEmpty(categoryDataResponses)) {
      return documentValue;
    }
    List<String> values = new ArrayList<>();
    String[] codes = documentCodes.split(",");
    for (String code: codes) {
      for (CategoryDataResponse response : categoryDataResponses) {
        if (code.equalsIgnoreCase(response.getCode())) {
          values.add(response.getValue());
          break;
        }
      }
    }

    if (CollectionUtils.isNotEmpty(values)) {
      documentValue = String.join(",", values);
    }
    log.info("END method: GeneralInfoProducerStrategy.getDocumentValue with documentValue={}", documentValue);
    return documentValue;
  }

  private List<CategoryDataResponse> getCategories() {
    try {
      log.info("START method: GeneralInfoProducerStrategy.getCategories");
      return configurationServiceCache.getCategoryDataByCode(DOCUMENT_CODE);
    } catch (Exception e) {
      log.error("ERROR: method: GeneralInfoProducerStrategy.getCategories with error message:{}", e.getMessage());
      return Collections.emptyList();
    }
  }

  private String getDocumentCode(ApplicationEntity applicationEntity) {
    log.info("START method: GeneralInfoProducerStrategy.getDocumentCode with bpmId={}", applicationEntity.getBpmId());
    String documentCode = "";
    List<String> codes = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(applicationEntity.getCredits())) {
      applicationEntity.getCredits().forEach(cr -> {
        if (StringUtils.isNotBlank(cr.getDocumentCode())) {
          codes.add(cr.getDocumentCode());
        }
      });
    }
    if (CollectionUtils.isNotEmpty(codes)) {
      documentCode = String.join(",", codes);
    }
    log.info("END method: GeneralInfoProducerStrategy.getDocumentCode with documentCode={}", documentCode);
    return documentCode;
  }

  private String getLoanPurposeValue(ApplicationEntity applicationEntity) {
    log.info("START method: GeneralInfoProducerStrategy.getLoanPurposeValue with bpmId={}", applicationEntity.getBpmId());
    String loanPurposeValue = "";
    List<String> values = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(applicationEntity.getCredits())) {
      applicationEntity.getCredits().forEach(cr -> {
        if (Objects.nonNull(cr.getCreditLoan()) && StringUtils.isNotBlank(cr.getCreditLoan().getLoanPurpose())) {
          values.add(cr.getCreditLoan().getLoanPurposeValue());
        }
        if (Objects.nonNull(cr.getCreditOverdraft()) && StringUtils.isNotBlank(cr.getCreditOverdraft().getLoanPurpose())) {
          values.add(cr.getCreditOverdraft().getLoanPurposeValue());
        }
      });
    }
    if (CollectionUtils.isNotEmpty(values)) {
      loanPurposeValue = String.join(",", values);
    }
    log.info("END method: GeneralInfoProducerStrategy.getLoanPurposeValue with loanPurposeValue={}", loanPurposeValue);
    return loanPurposeValue;
  }

  private String getLoanPurposeCode(ApplicationEntity applicationEntity) {
    log.info("START method: GeneralInfoProducerStrategy.getLoanPurposeCode with bpmId={}", applicationEntity.getBpmId());
    String loanPurposeCode = "";
    List<String> codes = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(applicationEntity.getCredits())) {
      applicationEntity.getCredits().forEach(cr -> {
        if (Objects.nonNull(cr.getCreditLoan()) && StringUtils.isNotBlank(cr.getCreditLoan().getLoanPurpose())) {
          codes.add(cr.getCreditLoan().getLoanPurpose());
        }
        if (Objects.nonNull(cr.getCreditOverdraft()) && StringUtils.isNotBlank(cr.getCreditOverdraft().getLoanPurpose())) {
          codes.add(cr.getCreditOverdraft().getLoanPurpose());
        }
      });
    }
    if (CollectionUtils.isNotEmpty(codes)) {
      loanPurposeCode = String.join(",", codes);
    }
    log.info("END method: GeneralInfoProducerStrategy.getLoanPurposeCode with loanPurposeCode={}", loanPurposeCode);
    return loanPurposeCode;
  }

  private String getGuaranteeFormCode(ApplicationEntity applicationEntity) {
    log.info("START method: GeneralInfoProducerStrategy.getGuaranteeFormCode with bpmId={}", applicationEntity.getBpmId());
    String guaranteeFormCode = "";
    List<String> codes = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(applicationEntity.getCredits())) {
      applicationEntity.getCredits().forEach(cr -> codes.add(cr.getGuaranteeForm()));
    }
    if (CollectionUtils.isNotEmpty(codes)) {
      guaranteeFormCode = String.join(",", codes);
    }
    log.info("END method: GeneralInfoProducerStrategy.getGuaranteeFormCode with guaranteeFormCode={}", guaranteeFormCode);
    return guaranteeFormCode;
  }
  private String getGuaranteeFormValue(ApplicationEntity applicationEntity) {
    log.info("START method: GeneralInfoProducerStrategy.getGuaranteeFormValue with bpmId={}", applicationEntity.getBpmId());
    String guaranteeFormValue = "";
    List<String> values = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(applicationEntity.getCredits())) {
      applicationEntity.getCredits().forEach(cr -> values.add(cr.getGuaranteeFormValue()));
    }
    if (CollectionUtils.isNotEmpty(values)) {
      guaranteeFormValue = String.join(",", values);
    }
    log.info("END method: GeneralInfoProducerStrategy.getGuaranteeFormValue with guaranteeFormValue={}", guaranteeFormValue);
    return guaranteeFormValue;
  }

  @SneakyThrows
  private void setProcessInstance(ApplicationEntity applicationEntity, GeneralInfoDTO generalInfoDTO) {
    ProcessInstanceEntity processInstanceEntity = applicationEntity.getProcessInstance();

    boolean isOldVersion = 1 == processInstanceEntity.getWorkflowVersion();
    if (isOldVersion) {
      setProcessInstanceOldVersion(applicationEntity, generalInfoDTO);
      return;
    }

    String processInstanceId;
    String processDefinitionId;

    if (StringUtils.isNotBlank(processInstanceEntity.getSubProcessInstanceId())) {
      processInstanceId = processInstanceEntity.getSubProcessInstanceId();
      processDefinitionId = processInstanceEntity.getSubProcessDefinitionId();
    } else {
      processInstanceId = processInstanceEntity.getProcessInstanceId();
      processDefinitionId = processInstanceEntity.getProcessDefinitionId();
    }

    String taskId = processInstanceEntity.getTaskId();
    String taskDefinitionKey = processInstanceEntity.getTaskDefinitionKey();

    generalInfoDTO.setProcessInstanceId(processInstanceId);
    generalInfoDTO.setProcessDefinitionId(processDefinitionId);
    generalInfoDTO.setTaskDefinitionKey(taskDefinitionKey);
    generalInfoDTO.setBpmTaskId(taskId);
  }

  @SneakyThrows
  private void setProcessInstanceOldVersion(ApplicationEntity applicationEntity, GeneralInfoDTO generalInfoDTO) {
    ProcessInstanceEntity processInstanceEntity = applicationEntity.getProcessInstance();

    String processInstanceId = null;
    String processDefinitionId = null;
    List<String> processInstanceIds = new ArrayList<>(
        Collections.singleton(processInstanceEntity.getProcessInstanceId()));
    String processBusinessKey = "";

    processInstanceId = StringUtils.isNotBlank(processInstanceEntity.getSubProcessInstanceId())
        ? processInstanceEntity.getSubProcessInstanceId()
        : processInstanceEntity.getProcessInstanceId();

    processDefinitionId = StringUtils.isNotBlank(processInstanceEntity.getSubProcessDefinitionId())
        ? processInstanceEntity.getSubProcessDefinitionId()
        : processInstanceEntity.getProcessDefinitionId();

    processBusinessKey = processInstanceEntity.getProcessBusinessKey();

    TaskDto nextTask;
    if (!ApplicationStatus.isComplete(applicationEntity.getStatus())) {
      String processKey = properties.getSubscriptions().get(
          CamundaTopic.INITIALIZE_VARIABLE.name()).getProcessDefinitionKey();
      ProcessInstanceDto processInstanceDto = camundaClient.getProcessInstanceByProcessKey(processBusinessKey,
          processKey);
      if (processInstanceDto != null) {
        processInstanceIds.add(processInstanceDto.getId());
      }
      try {
        nextTask = camundaClient.getTask(processInstanceIds, processBusinessKey);
      } catch (Throwable e) {
        log.error("Not found any task with processInstanceIds {}", processInstanceIds);
        nextTask = new TaskDto().id("-").taskDefinitionKey("activity_process_bm");
      }
    } else {
      nextTask = new TaskDto().id(CamundaVariable.END_TASK.getValue()).taskDefinitionKey(CamundaVariable.END_TASK.getValue());
    }

    generalInfoDTO.setProcessInstanceId(processInstanceId);
    generalInfoDTO.setProcessDefinitionId(processDefinitionId);
    generalInfoDTO.setTaskDefinitionKey(nextTask.getTaskDefinitionKey());
    generalInfoDTO.setBpmTaskId(nextTask.getId());
  }

  private ProcessPropsDTO getProcessProps(ApplicationEntity applicationEntity) {
    log.info("START method: GeneralInfoProducerStrategy.getProcessProps with bpmId={}", applicationEntity.getBpmId());
    ProcessPropsDTO processPropsDTO = new ProcessPropsDTO();
    processPropsDTO.setAuthority(applicationEntity.getProcessingRole());
    if (Objects.nonNull(applicationEntity.getHistoryApprovals())) {
      ApplicationHistoryApprovalEntity applicationHistoryApprovalEntity = applicationEntity.getHistoryApprovals().stream().max(Comparator.comparing(ApplicationHistoryApprovalEntity::getId)).orElse(null);
      log.info("START generate approval history for bpmId = [{}]", applicationEntity.getBpmId());
      if (Objects.nonNull(applicationHistoryApprovalEntity) && StringUtils.isNotBlank(applicationHistoryApprovalEntity.getReason())) {
        ConfigurationCategory configurationCategory = ApplicationStatus.AS0099.getValue().equalsIgnoreCase(applicationEntity.getStatus()) ? ConfigurationCategory.CLOSE_REASON_CODE : ConfigurationCategory.RETURN_REASON_CODE;
        Map<String, String> reasonMap = configurationServiceCache.getCategoryDataByCode(configurationCategory).stream().collect(HashMap::new, (m, v) -> m.put(v.getCode(), v.getValue()), HashMap::putAll);
        List<ReasonDTO> reasons = Arrays.stream(applicationHistoryApprovalEntity.getReason().split(StringUtil.COMMA)).filter(reason -> Objects.nonNull(reason) && reasonMap.containsKey(reason)).map(code -> {
          ReasonDTO reasonDTO = new ReasonDTO();
          reasonDTO.setReasonCode(code);
          reasonDTO.setReasonContent(reasonMap.get(code));
          return reasonDTO;
        }).collect(Collectors.toList());
        processPropsDTO.setReasons(reasons);
      }
    }
    if (ApplicationStatus.AS4001.getValue().equalsIgnoreCase(applicationEntity.getLdpStatus()) || ApplicationStatus.AS4003.getValue().equalsIgnoreCase(applicationEntity.getLdpStatus())) {
      processPropsDTO.setDetail(MessageCode.FEEDBACK_EDITING_SUCCESS.getValue());
    } else if (ApplicationStatus.AS0099.getValue().equalsIgnoreCase(applicationEntity.getStatus())) {
      processPropsDTO.setDetail(messageSource.getMessage(CLOSE_APPLICATION.getValue(), null, Util.locale()));
    }
    log.info("END method: GeneralInfoProducerStrategy.getProcessProps with response = [{}]", JsonUtil.convertObject2String(processPropsDTO, objectMapper));
    return processPropsDTO;
  }
}
