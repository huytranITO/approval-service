package com.msb.bpm.approval.appr.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.constant.Constant;
import com.msb.bpm.approval.appr.enums.rating.RatingSystemType;
import com.msb.bpm.approval.appr.model.dto.cas.CASResultData;
import com.msb.bpm.approval.appr.model.dto.cic.FieldName;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.CicResponseMessage;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.ResponseMessageData;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsDtlEntity;
import com.msb.bpm.approval.appr.notication.NoticeType;
import com.msb.bpm.approval.appr.notication.NotificationFactory;
import com.msb.bpm.approval.appr.notication.NotificationInterface;
import com.msb.bpm.approval.appr.repository.ApParamRepository;
import com.msb.bpm.approval.appr.repository.ApplicationCreditRatingsDtlRepository;
import com.msb.bpm.approval.appr.repository.ApplicationCreditRatingsRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.util.DateUtils;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
@Transactional
public class CasKafkaListener extends AbstractBaseService {
    private final ObjectMapper objectMapper;
    private final ApplicationCreditRatingsRepository creditRatingsRepository;
    private final ApplicationCreditRatingsDtlRepository creditRatingsDtlRepository;
    private final ApParamRepository apParamRepository;
    private final NotificationFactory factory;

    @KafkaListener(
            topics = "${cas.kafka.consumer.topic}",
            groupId = "${cas.kafka.consumer.group-id}",
            containerFactory = "casListenerContainerFactory"
    )
    public void listenGroupCAS(byte[] message) {
        AtomicReference<String> bpmId = new AtomicReference<>("");
        AtomicReference<String> assignee = new AtomicReference<>("");
        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        String data = SerializationUtils.deserialize(message);
        log.info("[RECEIVE_MESSAGE_CAS] MESSAGE {}", data);
        CicResponseMessage casResponseMessage = JsonUtil.convertString2Object(data, CicResponseMessage.class, objectMapper);
        if (casResponseMessage == null) {
            return;
        }
        // Kiểm tra xem requestId có tồn tại trong hệ thống hay không
        String requestId = casResponseMessage.getUserHeader().getRequestJsonId();
        creditRatingsRepository.findByRequestId(requestId).ifPresent(ratingsEntity -> {
            if (ratingsEntity.getApplication() != null) {
                bpmId.set(ratingsEntity.getApplication().getBpmId());
                assignee.set(ratingsEntity.getApplication().getAssignee());
                blockingQueue.add(ratingsEntity.getRatingId());
            }
            creditRatingsDtlRepository.findAllByApplicationCreditRatingId(ratingsEntity.getId()).ifPresent(oldRatingsDtlEntities -> {
                // Lưu kết quả xếp hạng mới
                List<ResponseMessageData> responseMessageData = casResponseMessage.getData();
                log.info("[RECEIVE_MESSAGE_CAS] responseMessageData {}", responseMessageData);
                if (CollectionUtils.isEmpty(responseMessageData) || CollectionUtils.isEmpty(responseMessageData.get(0).getData())) {
                    return;
                }
                List<CASResultData> casResultData = getCasResultData(responseMessageData);
                List<ApplicationCreditRatingsDtlEntity> newRatingsDtlEntities = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(casResultData)) {
                    casResultData.forEach(resultData -> {
                        ApplicationCreditRatingsDtlEntity ratingsDtl = new ApplicationCreditRatingsDtlEntity();
                        ratingsDtl.setRank(resultData.getRank1());
                        ratingsDtl.setScore(Double.valueOf(resultData.getScore1()));
                        ratingsDtl.setIdentityCard(resultData.getLegalDocNo());
                        ratingsDtl.setRecommendation(resultData.getRecommendation());
                        ratingsDtl.setExecutor(resultData.getCreatedBy());
                        ratingsDtl.setApplicationCreditRating(ratingsEntity);
                        ratingsDtl.setScoringTime(DateUtils.formatDate(resultData.getDateCreated(), Constant.YYYY_MM_DD_HH_MM_SS_FORMAT , Constant.DD_MM_YYYY_HH_MM_SS_FORMAT));
                        if (ObjectUtils.isNotEmpty(resultData.getScoringAction())) {
                            ratingsDtl.setRole(mappingRole(resultData.getScoringAction().toLowerCase()));
                        }
                        if (resultData.getStatus() != null) {
                            ratingsDtl.setStatusDescription(mappingStatus(resultData.getStatus()));
                        }

                        newRatingsDtlEntities.add(ratingsDtl);

                    });
                }
                if (CollectionUtils.isNotEmpty(newRatingsDtlEntities)) {
                    creditRatingsDtlRepository.saveAll(newRatingsDtlEntities);
                    // Xóa kết quả xếp hạng đã lưu
                    creditRatingsDtlRepository.deleteAll(oldRatingsDtlEntities);
                }

            });
        });


        NotificationInterface notificationInterface = factory.getNotification(NoticeType.CAS);
        notificationInterface.notice(bpmId.get(), assignee.get(), String.join(" and ", new ArrayList(blockingQueue)));
    }

    private List<CASResultData> getCasResultData(List<ResponseMessageData> responseMessageData) {
        if (CollectionUtils.isEmpty(responseMessageData)) {
            return new ArrayList<>();
        }

        // ấy danh sách các field ở response , đẩy vào map<indexField, fieldName>
        ResponseMessageData messageData = responseMessageData.get(0);
        Map<String, Integer> fieldNames = new HashMap<>();
        for (int i = 0; i < messageData.getFieldList().size(); i++) {
            fieldNames.put(messageData.getFieldList().get(i).getFieldName(), i);
        }

        return messageData.getData().stream().map(fieldValues -> geCasResultData(fieldNames, fieldValues)).collect(Collectors.toList());
    }

    private CASResultData geCasResultData(Map<String, Integer> fieldNames, Map<String, String> fieldValues) {
        CASResultData result = new CASResultData();

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
                log.error("Method geCasResultData error {}", ex.getMessage());
            }
        }
        return result;
    }

    private String mappingRole(String scoringAction) {
        AtomicReference<String> role = new AtomicReference<>("");
        apParamRepository.findByCodeAndType(scoringAction, "SCORING_ACTION")
                .ifPresent(apParamEntity -> role.set(apParamEntity.getMessage()));
        return role.get();
    }

    private String mappingStatus(String statusCode) {
        AtomicReference<String> message = new AtomicReference<>("");
        apParamRepository.findByCodeAndType(statusCode, RatingSystemType.CAS.name())
                .ifPresent(apParamEntity -> message.set(apParamEntity.getMessage()));
        return message.get();
    }

}

