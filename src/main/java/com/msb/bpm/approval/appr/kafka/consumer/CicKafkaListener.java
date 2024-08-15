package com.msb.bpm.approval.appr.kafka.consumer;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.DEBT_GROUP_CURRENT;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.DEBT_GROUP_LAST_12;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.DEBT_GROUP_LAST_24;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.TOTAL_CREDIT_CARD_LIMIT;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.TOTAL_DEBT_CARD_LIMIT;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.TOTAL_LOAN_COLLATERAL;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.TOTAL_LOAN_COLLATERAL_USD;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.TOTAL_UNSECURE_COLLATERAL;
import static com.msb.bpm.approval.appr.enums.cic.CICCriteria.TOTAL_UNSECURE_COLLATERAL_USD;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.config.cic.CICProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode;
import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO.CicDetail;
import com.msb.bpm.approval.appr.model.dto.cic.CICResultData;
import com.msb.bpm.approval.appr.model.dto.cic.FieldName;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.CicResponseMessage;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.ResponseMessageData;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CICKafkaHistory;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import com.msb.bpm.approval.appr.mqtt.MQTTService;
import com.msb.bpm.approval.appr.repository.CICKafkaHistoryRepository;
import com.msb.bpm.approval.appr.repository.CICRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.intergated.CICService;
import com.msb.bpm.approval.appr.service.notification.NotificationService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class CicKafkaListener extends AbstractBaseService {

  @Value("${cic.kafka.consumer.topic}")
  private String topic;

  private final ObjectMapper objectMapper;
  private final CICRepository cicRepository;
  private final CICProperties cicProperties;
  private final CICKafkaHistoryRepository cicKafkaHistoryRepository;
  private final CICService cicService;
  private final NotificationService notificationService;
  private final MQTTService mqttService;

  public CicKafkaListener(ObjectMapper objectMapper, CICRepository cicRepository,
      CICProperties cicProperties, CICKafkaHistoryRepository cicKafkaHistoryRepository,
      CICService cicService,
      NotificationService notificationService,
      MQTTService mqttService) {
    this.objectMapper = objectMapper;
    this.cicRepository = cicRepository;
    this.cicProperties = cicProperties;
    this.cicKafkaHistoryRepository = cicKafkaHistoryRepository;
    this.cicService = cicService;
    this.mqttService = mqttService;
    this.notificationService = notificationService;
  }

  @KafkaListener(topics = "${cic.kafka.consumer.topic}",
      groupId = "${cic.kafka.consumer.group-id}",
      containerFactory = "cicKafkaListenerContainerFactory")
  public void listenGroupCIC(byte[] message) {
    CICKafkaHistory cicKafkaHistory = null;
    try {
      String data = SerializationUtils.deserialize(message);
      log.info("[RECEIVE_MESSAGE_CIC] MESSAGE {}", data);
      CicResponseMessage cicResponseMessage = JsonUtil.convertString2Object(data,
          CicResponseMessage.class, objectMapper);
      if (cicResponseMessage == null) {
        return;
      }

      // Kiểm tra xem requestId có tồn tại trong hệ thống hay không
      String requestId = cicResponseMessage.getUserHeader().getRequestJsonId();
      cicKafkaHistory = cicKafkaHistoryRepository.findByRequestId(requestId);
      if (cicKafkaHistory == null) {
        log.info("[RECEIVE_MESSAGE_CIC] Id không tồn tại trong hệ thống! requestId {}", requestId);
        return;
      }
      // lưu lại message vào DB
      cicKafkaHistoryRepository.save(new CICKafkaHistory()
          .withRequestId(requestId)
          .withTopic(topic)
          .withMessage(data)
          .withApplicationBpmId(cicKafkaHistory.getApplicationBpmId())
          .withClientQuestionIds(cicKafkaHistory.getClientQuestionIds())
          .withApplicationId(cicKafkaHistory.getApplicationId()));

      List<ResponseMessageData> responseMessageData = cicResponseMessage.getData();
      log.info("[RECEIVE_MESSAGE_CIC] responseMessageData {}", responseMessageData);
      if (CollectionUtils.isEmpty(responseMessageData)
          || CollectionUtils.isEmpty(responseMessageData.get(0).getData())) {
        return;
      }
      List<CICResultData> cicResultData = getCicResultData(responseMessageData);
      log.info("[RECEIVE_MESSAGE_CIC] cicResultData {}", cicResultData);

      /**
       * key: client request id
       * value: map
       *        key: field code
       *        value: cic result data
       */
      Map<String, Map<String, CICResultData>> dataMap = new HashMap<>();
      for (CICResultData cicResult : cicResultData) {
        Map<String, CICResultData> dataMap1 = dataMap.get(cicResult.getMainId());
        if (dataMap1 == null) {
          dataMap1 = new HashMap<>();
          dataMap.put(cicResult.getMainId(), dataMap1);
        }
        if (!dataMap1.containsKey(cicResult.getFieldName())) {
          dataMap1.put(cicResult.getFieldName(), cicResult);
        }
      }
      log.info("[RECEIVE_MESSAGE_CIC] dataMap {}", dataMap);

      Long applicationId = cicKafkaHistory.getApplicationId();
      List<CicEntity> cicEntities = cicRepository.findByApplicationId(applicationId).get();
      log.info("[RECEIVE_MESSAGE_CIC] cicEntities {}", cicEntities);
      if (CollectionUtils.isEmpty(cicEntities)) {
        return;
      }

      cicEntities.forEach(cicEntity -> {
        setCICTarget(cicEntity, dataMap);
      });

      cicRepository.saveAll(cicEntities);
      updateApplicationDraft(cicKafkaHistory, cicEntities);
      ApplicationEntity application = cicEntities.get(0).getApplication();
      notice(application, cicKafkaHistory);
    } catch (Exception ex) {
      log.error(
          "[RECEIVE_MESSAGE_CIC]Handle CIC message obtain from kafka is error! message " + message,
          ex);
    } finally {
      if (cicKafkaHistory != null) {
        cicService.updateSyncingIndicatorCICFlag(cicKafkaHistory.getApplicationBpmId(), false);
      }
    }
  }

  private void notice(ApplicationEntity application, CICKafkaHistory cicKafkaHistory) {
    Set<String> notifiedUsers = new HashSet<>();
    if (application != null && application.getAssignee() != null) {
      notifiedUsers.add(application.getAssignee());
    }
    if (cicKafkaHistory != null && cicKafkaHistory.getCreatedBy() != null) {
      notifiedUsers.add(cicKafkaHistory.getCreatedBy());
    }
    notificationService.noticeRatioCIC(notifiedUsers, application.getBpmId());
    mqttService.sendCICMessage(notifiedUsers, application.getBpmId());
  }

  private void updateApplicationDraft(CICKafkaHistory cicKafkaHistory,
      List<CicEntity> cicEntities) {
    String bpmId = cicKafkaHistory.getApplicationBpmId();
    Map<String, ApplicationDraftEntity> draftEntityMap = draftMapByTabCode(bpmId);
    ApplicationDraftEntity entity = draftEntityMap.get(TabCode.INITIALIZE_INFO);
    log.info("[RECEIVE_MESSAGE_CIC] entity {}", entity);
    if (!Objects.equals(ApplicationDraftStatus.UNFINISHED, entity.getStatus())) {
      return;
    }
    PostBaseRequest postBaseRequest = getDraftData(entity, entity.getTabCode());
    log.info("[RECEIVE_MESSAGE_CIC] postBaseRequest {}", postBaseRequest);
    if (postBaseRequest == null) {
      return;
    }
    PostInitializeInfoRequest request = (PostInitializeInfoRequest) postBaseRequest;
    CicDTO cicDTO = buildCicData(new HashSet<>(cicEntities));
    CicDTO cicDTODraft = request.getCustomerAndRelationPerson().getCic();
    log.info("[RECEIVE_MESSAGE_CIC] cicDTO : " + cicDTO);
    log.info("[RECEIVE_MESSAGE_CIC] cicDTODraft : " + cicDTODraft);
    if (cicDTODraft == null
        || org.springframework.util.CollectionUtils.isEmpty(cicDTODraft.getCicDetails())) {
      return;
    }

    Map<Long, CicDetail> cicDetailMap = cicDTO.getCicDetails().stream()
        .collect(Collectors.toMap(CicDetail::getId, Function.identity()));
    log.info("[RECEIVE_MESSAGE_CIC] cicDetailMap {}", cicDetailMap);
    log.info("[RECEIVE_MESSAGE_CIC] cicDTODraft.getCicDetails() {}", cicDTODraft.getCicDetails());

    Set<CicDetail> cicDetailsDraft = cicDTODraft.getCicDetails().stream()
        .map(cicDetail -> cicDetail.getId() == null
            || cicDetailMap.get(cicDetail.getId()) == null ?
            cicDetail : cicDetailMap.get(cicDetail.getId())
        ).collect(Collectors.toSet());

    cicDTODraft.setCicDetails(cicDetailsDraft);
    cicDTODraft.setCicGroupDetails(cicGroupDetails(cicEntities));
    log.info("[RECEIVE_MESSAGE_CIC] updated cicDTODraft : " + cicDTODraft);
    persistApplicationDraft(bpmId, INITIALIZE_INFO, UNFINISHED, request);
  }

  private void setCICTarget(CicEntity cicEntity,
      Map<String, Map<String, CICResultData>> cicResultMap) {
    Long clientRequestId = cicEntity.getClientQuestionId();
    if (clientRequestId == null) {
      cicEntity.setCicIndicatorStatus(false);
      return;
    }
    Map<String, CICResultData> dataMap1 = cicResultMap.get(clientRequestId.toString());
    if (dataMap1 == null || dataMap1.isEmpty()) {
      cicEntity.setCicIndicatorStatus(false);
      return;
    }

    boolean success = false;
    String cicCriteriaCode = cicProperties.getCriteriaCodeMapping()
        .get(DEBT_GROUP_CURRENT.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setDeftGroupCurrent(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null) {
      cicEntity.setDeftGroupCurrent(dataMap1.get(cicCriteriaCode).getFieldValue());
      success = true;
    }

    cicCriteriaCode = cicProperties.getCriteriaCodeMapping().get(DEBT_GROUP_LAST_12.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setDeftGroupLast12(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null) {
      cicEntity.setDeftGroupLast12(dataMap1.get(cicCriteriaCode).getFieldValue());
      success = true;
    }

    cicCriteriaCode = cicProperties.getCriteriaCodeMapping().get(DEBT_GROUP_LAST_24.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setDeftGroupLast24(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null) {
      cicEntity.setDeftGroupLast24(dataMap1.get(cicCriteriaCode).getFieldValue());
      success = true;
    }

    cicCriteriaCode = cicProperties.getCriteriaCodeMapping().get(TOTAL_LOAN_COLLATERAL.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setTotalLoanCollateral(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null
        && dataMap1.get(cicCriteriaCode).getFieldValue() != null) {
      cicEntity.setTotalLoanCollateral(
          new BigDecimal(dataMap1.get(cicCriteriaCode).getFieldValue()));
      success = true;
    }

    cicCriteriaCode = cicProperties.getCriteriaCodeMapping()
        .get(TOTAL_LOAN_COLLATERAL_USD.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setTotalLoanCollateralUSD(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null
        && dataMap1.get(cicCriteriaCode).getFieldValue() != null) {
      cicEntity.setTotalLoanCollateralUSD(
          new BigDecimal(dataMap1.get(cicCriteriaCode).getFieldValue()));
      success = true;
    }

    cicCriteriaCode = cicProperties.getCriteriaCodeMapping()
        .get(TOTAL_CREDIT_CARD_LIMIT.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setTotalCreditCardLimit(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null
        && dataMap1.get(cicCriteriaCode).getFieldValue() != null) {
      cicEntity.setTotalCreditCardLimit(
          new BigDecimal(dataMap1.get(cicCriteriaCode).getFieldValue()));
      success = true;
    }

    cicCriteriaCode = cicProperties.getCriteriaCodeMapping()
        .get(TOTAL_DEBT_CARD_LIMIT.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setTotalDebtCardLimit(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null
        && dataMap1.get(cicCriteriaCode).getFieldValue() != null) {
      cicEntity.setTotalDebtCardLimit(
          new BigDecimal(dataMap1.get(cicCriteriaCode).getFieldValue()));
      success = true;
    }

    cicCriteriaCode = cicProperties.getCriteriaCodeMapping()
        .get(TOTAL_UNSECURE_COLLATERAL.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setTotalUnsecureLoan(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null
        && dataMap1.get(cicCriteriaCode).getFieldValue() != null) {
      cicEntity.setTotalUnsecureLoan(new BigDecimal(dataMap1.get(cicCriteriaCode).getFieldValue()));
      success = true;
    }

    cicCriteriaCode = cicProperties.getCriteriaCodeMapping()
        .get(TOTAL_UNSECURE_COLLATERAL_USD.getValue());
    if(StringUtils.isEmpty(cicCriteriaCode)) {
      cicEntity.setTotalUnsecureLoanUSD(null);
      success = true;
    } else if (StringUtils.hasText(cicCriteriaCode)
        && dataMap1.get(cicCriteriaCode) != null
        && dataMap1.get(cicCriteriaCode).getFieldValue() != null) {
      cicEntity.setTotalUnsecureLoanUSD(
          new BigDecimal(dataMap1.get(cicCriteriaCode).getFieldValue()));
      success = true;
    }

    cicEntity.setCicIndicatorStatus(success);
  }

  private List<CICResultData> getCicResultData(List<ResponseMessageData> responseMessageData) {
    if (CollectionUtils.isEmpty(responseMessageData)) {
      return new ArrayList<>();
    }

    // ấy danh sách các field ở response , đẩy vào map<indexField, fieldName>
    ResponseMessageData messageData = responseMessageData.get(0);
    Map<String, Integer> fieldNames = new HashMap<>();
    for (int i = 0; i < messageData.getFieldList().size(); i++) {
      fieldNames.put(messageData.getFieldList().get(i).getFieldName(), i);
    }

    return messageData.getData().stream()
        .map(fieldValues -> geCicResultData(fieldNames, fieldValues))
        .collect(Collectors.toList());
  }

  private CICResultData geCicResultData(Map<String, Integer> fieldNames,
      Map<String, String> fieldValues) {
    CICResultData result = new CICResultData();

    String fieldName;
    String fieldValue;
    Integer index;
    for (Field field : result.getClass().getDeclaredFields()) {
      try {
        FieldName fieldNameAnnotation = field.getAnnotation(FieldName.class);
        fieldName = fieldNameAnnotation == null ? field.getName() : fieldNameAnnotation.value();
        index = fieldNames.get(fieldName);
        if (index != null) {
          fieldValue = fieldValues.get(index.toString());
          if (fieldName != null) {
            field.setAccessible(true);
            field.set(result, fieldValue);
          }
        }
      } catch (Exception ex) {
      }
    }
    return result;
  }
}
