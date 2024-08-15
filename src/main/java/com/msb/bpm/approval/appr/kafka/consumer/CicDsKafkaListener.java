package com.msb.bpm.approval.appr.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode;
import com.msb.bpm.approval.appr.enums.checklist.BusinessFlow;
import com.msb.bpm.approval.appr.enums.checklist.ChecklistEnum;
import com.msb.bpm.approval.appr.enums.checklist.DomainType;
import com.msb.bpm.approval.appr.enums.checklist.Group;
import com.msb.bpm.approval.appr.enums.cic.CICStatus;
import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.kafka.CICDSTable;
import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO.CicDetail;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.dto.cic.FieldName;
import com.msb.bpm.approval.appr.model.dto.cic.GeneratedPDFResult;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.CICClientData;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.CICClientQuestionData;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.CicDataTable;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.CicDsData;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.out.RootCicDsMessage;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.cic.CICDataResponse;
import com.msb.bpm.approval.appr.mqtt.MQTTService;
import com.msb.bpm.approval.appr.repository.CICRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.service.intergated.CICService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.notification.NotificationService;
import com.msb.bpm.approval.appr.util.DateUtils;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.StringUtil;
import com.msb.bpm.approval.appr.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TIME_LOCAL_FORMAT;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.constant.Constant.YYYY_MM_DD_FORMAT;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.CIC_RATIO_CONTENT;
import static com.msb.bpm.approval.appr.notication.NoticeConstant.contents;

@Slf4j
@Component
@RequiredArgsConstructor
public class CicDsKafkaListener extends AbstractBaseService {

    @Value("${cic.kafka-update-cic.consumer.topic}")
    private String topic;

    private final ObjectMapper objectMapper;
    private final CICRepository cicRepository;
    private final CICService cicService;
    private final NotificationService notificationService;
    private final MQTTService mqttService;
    private final ChecklistService checklistService;
    private final ApplicationConfig applicationConfig;
    private final CommonService commonService;


    @KafkaListener(
            topics = "${cic.kafka-update-cic.consumer.topic}",
            groupId = "${cic.kafka-update-cic.consumer.group-id}",
            containerFactory = "cicDsListenerContainerFactory")
    public void listenDataCIC(String message) {
        log.info("RB [KAFKA_listenDataCIC] get from topic {} and MESSAGE {}", topic, message);
        RootCicDsMessage cicResponseMessage = JsonUtil.convertString2Object(message,
                RootCicDsMessage.class, objectMapper);
        if (cicResponseMessage == null || !cicResponseMessage.hasData()) {
            log.error("RB could not found data in cic root message!");
            return;
        }

        CicDataTable cicdsData = getCicData(cicResponseMessage);
        log.info("RB cic result: ciccdsData = {}", JsonUtil.convertObject2String(cicdsData, objectMapper));
        if (cicdsData == null
                || cicdsData.getCicClientQuestionData() == null
                || !cicdsData.getCicClientQuestionData().hasData()) {
            return;
        }

        CICClientQuestionData cicClientQuestionData = cicdsData.getCicClientQuestionData();
        int statusAsInt = cicClientQuestionData.statusAsInt();
        if (CICStatus.WAITING_RESPONSE.getStatus().equals(statusAsInt)) {
            log.info("RB receiver CIC status = 5, not need to process message !");
            return;
        }

        Long clientQuestionIdAsLong = Long.valueOf(cicClientQuestionData.getClientQuestionId());
        String productCode = cicClientQuestionData.getProductCode();
        // Check requestId exist in db
        List<CicEntity> cicDb = cicRepository.findAllByClientQuestionIdAndProductCodeAndStatusCode(clientQuestionIdAsLong,
                productCode,
                CICStatus.WAITING_RESPONSE.getStatus().toString());

        if (CollectionUtils.isEmpty(cicDb)) {
            log.info("RB there is no cic with status = 5");
            return;
        }

        Map<String, GeneratedPDFResult> generatedPDFResults = new HashMap<>();
        for (CicEntity cic : cicDb) {
            try {
                // update entity & generate pdf
                updateInquiryAndGenerateFile(cic, cicdsData, generatedPDFResults);
            } catch (Exception ex) {
                log.error("RB updateInquiryAndGenerateFile with cause {}", ex);
            }
            CICClientData cicClientData = cicdsData.getCicClientData();
            Integer status = cicClientQuestionData.statusAsInt();
            cic.setStatusCode(String.valueOf(status));
            cic.setClientQuestionId(cicClientQuestionData.clientQuestionIdAsLong());
            if (CICStatus.equal(status, CICStatus.VIOLATE_CIC_RULE)) {
                cic.setErrorCodeSBV(cicClientQuestionData.getErrorCodeList());
            }
            if (!org.apache.commons.lang3.StringUtils.isBlank(cicClientData.getResponseDate())) {
                cic.setQueryAt(Util.parseLocalDate(cicClientData.getResponseDate(),
                        TIME_LOCAL_FORMAT));
            }
        }

        // Save DB
        cicRepository.saveAllAndFlush(cicDb);

        publicMessageCicAndUpdateChecklist(statusAsInt, cicDb, generatedPDFResults);

        sendNotice2User(cicDb);
    }

    private void publicMessageCicAndUpdateChecklist(int statusAsInt, List<CicEntity> cicDb, Map<String, GeneratedPDFResult> generatedPDFResults) {
        try {
            for (CicEntity cic: cicDb) {
                // case 6,7 status
                if (CICStatus.HAVE_DATA.getStatus().equals(statusAsInt)
                        || CICStatus.DO_NOT_HAVE_INFORMATION.getStatus().equals(statusAsInt) ) {
                    ApplicationEntity application = commonService.findAppByBpmId(cic.getApplication().getBpmId());
                    // publish message lên kafka lấy chỉ tiêu cic
                    log.info("RB publishMessage cic");
                    cicService.publishMessage(application);

                    // update checklist
                    if (ObjectUtils.isNotEmpty(generatedPDFResults)) {
                        log.info("RB updateChecklist cic");
                        updateChecklist(application, generatedPDFResults);
                    }
                }
            }
        } catch (Exception e) {
            log.error("RB publicMessageCicAndUpdateChecklist cic {}", e);
        }
    }

    private void sendNotice2User(List<CicEntity> cicDb) {
        try {
            for (CicEntity cic: cicDb) {
                ApplicationEntity application = cic.getApplication();

                // update draft
                updateApplicationDraft(application.getBpmId(), cic);

                // send notice and mqtt to FE
                Set<String> users = cicDb.stream().map(CicEntity::getCreatedBy)
                        .distinct().collect(Collectors.toSet());
                if(!users.contains(application.getAssignee())) {
                    users.add(application.getAssignee());
                }
                notificationService.noticeUpdateRatioCIC(users, application.getBpmId());
                mqttService.sendCICMessageUpdate(users, application.getBpmId(), contents.get(CIC_RATIO_CONTENT));
            }
        }catch (Exception e) {
            log.error("RB sendNotice2User fail with cause {}", e);
        }
    }

    private void updateApplicationDraft(String bpmId,
                                        CicEntity cic) {
        List<CicEntity> cicEntities = new ArrayList<>();
        cicEntities.add(cic);
        Map<String, ApplicationDraftEntity> draftEntityMap = draftMapByTabCode(bpmId);
        ApplicationDraftEntity entity = draftEntityMap.get(TabCode.INITIALIZE_INFO);
        log.info("RB [RECEIVE_MESSAGE_CIC] entity {}", entity);
        if (!Objects.equals(ApplicationDraftStatus.UNFINISHED, entity.getStatus())) {
            return;
        }
        PostBaseRequest postBaseRequest = getDraftData(entity, entity.getTabCode());
        log.info("RB [RECEIVE_MESSAGE_CIC] postBaseRequest {}", postBaseRequest);
        if (postBaseRequest == null) {
            return;
        }
        PostInitializeInfoRequest request = (PostInitializeInfoRequest) postBaseRequest;
        CicDTO cicDTO = buildCicData(new HashSet<>(cicEntities));
        CicDTO cicDTODraft = request.getCustomerAndRelationPerson().getCic();
        log.info("RB [RECEIVE_MESSAGE_CIC] cicDTO : " + cicDTO);
        log.info("RB [RECEIVE_MESSAGE_CIC] cicDTODraft : " + cicDTODraft);
        if (cicDTODraft == null
                || org.springframework.util.CollectionUtils.isEmpty(cicDTODraft.getCicDetails())) {
            return;
        }

        Map<Long, CicDetail> cicDetailMap = cicDTO.getCicDetails().stream()
                .collect(Collectors.toMap(CicDetail::getId, Function.identity()));
        log.info("RB [RECEIVE_MESSAGE_CIC] cicDetailMap {}", cicDetailMap);
        log.info("RB [RECEIVE_MESSAGE_CIC] cicDTODraft.getCicDetails() {}", cicDTODraft.getCicDetails());

        Set<CicDetail> cicDetailsDraft = cicDTODraft.getCicDetails().stream()
                .map(cicDetail -> cicDetail.getId() == null
                        || cicDetailMap.get(cicDetail.getId()) == null ?
                        cicDetail : cicDetailMap.get(cicDetail.getId())
                ).collect(Collectors.toSet());

        cicDTODraft.setCicDetails(cicDetailsDraft);
        cicDTODraft.setCicGroupDetails(cicGroupDetails(cicEntities));
        log.info("RB [RECEIVE_MESSAGE_CIC] updated cicDTODraft : " + cicDTODraft);
        persistApplicationDraft(bpmId, INITIALIZE_INFO, UNFINISHED, request);
    }

    private CicDataTable getCicData(RootCicDsMessage rootMessage) {
        CicDsData data = rootMessage.getData();
        if (data == null
                || data.getUserData() == null
                || data.getUserData().getSpecialMessage() == null
                || data.getUserData().getSpecialMessage().getTableName() == null) {
            log.debug("RB CICDS don't return table list");
            return null;
        }
        List<String> tableNames = data.getUserData().getSpecialMessage().getTableName();
        Map<String, Integer> cicTableMap = new HashMap<>();
        for (int index = 0; index < tableNames.size(); index++) {
            String tableName = tableNames.get(index);
            if (StringUtils.hasText(tableName)) {
                cicTableMap.put(tableName, index);
            }
        }
        CicDataTable result = new CicDataTable();

        // duyệt các object data tương ứng với các table
        Field[] fields = CicDataTable.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = getTableData(field, cicTableMap, data.getActionArray());
                if (value != null) {
                    field.set(result, value);
                }
            } catch (Exception ex) {
                log.error("RB getCicData error during field access: {}", ex);
            }
        }

        return result;
    }

    private Object getTableData(Field field, Map<String, Integer> cicTableMap, List<List<List<String>>> actionArray) {
        CICDSTable annotations = field.getAnnotation(CICDSTable.class);
        if (annotations == null || !StringUtils.hasText(annotations.name())) {
            return null;
        }
        String tableName = annotations.name();
        if (StringUtils.hasText(tableName)) {
            Integer tableIndex = cicTableMap.get(tableName);
            if (tableIndex != null && tableIndex < actionArray.size()) {
                List<List<String>> values = actionArray.get(tableIndex);
                Class<?> dataClass = field.getType();
                return getData(values, dataClass);
            }
        }
        return null;
    }

    private <T> T getData(List<List<String>> cicData, Class<T> dataClassType) {
        if (CollectionUtils.isEmpty(cicData)
                || cicData.size() < 2
                || CollectionUtils.isEmpty(cicData.get(0))
                || CollectionUtils.isEmpty(cicData.get(1))
                || dataClassType == null) {
            log.debug("RB CICDS don't return data! cicData: {}", JsonUtil.convertObject2String(cicData, objectMapper));
            return null;
        }

        try {
            List<String> columns = cicData.get(0);
            List<String> values = cicData.get(1);

            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int index = 0; index < columns.size(); index++) {
                String columnName = columns.get(index);
                if (StringUtils.hasText(columnName)) {
                    columnIndexMap.put(columnName, index);
                }
            }

            Constructor<T> constructor = dataClassType.getDeclaredConstructor();
            T result = constructor.newInstance();

            Field[] fields = dataClassType.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String value = getValue(field, columnIndexMap, values);
                if (StringUtils.hasText(value)) {
                    field.set(result, value);
                }
            }

            return result;
        } catch (Exception ex) {
            log.error("Error during field access: {}", ex);
            return null;
        }
    }

    private String getValue(Field field,
                            Map<String, Integer> columnIndexMap, List<String> values) {
        field.setAccessible(true);
        FieldName annotations = field.getAnnotation(FieldName.class);
        if (annotations == null
                || !StringUtils.hasText(annotations.value())) {
            return null;
        }
        String fieldNameValue = annotations.value();
        Integer columnIndex = columnIndexMap.get(fieldNameValue);
        if (columnIndex != null && columnIndex < values.size()) {
            return values.get(columnIndex);
        }
        return null;
    }

    private void updateInquiryAndGenerateFile(CicEntity cic, CicDataTable cicdsData, Map<String, GeneratedPDFResult> generatedPDFResults) {
        log.info("RB search and update inquiry {}. start", cic.getId());
        if (cic.isPdfStatus()) {
            log.info("RB search and update inquiry {}. pdf status is true", cic.getId());
            return;
        }

        // Update cic data
        CICClientQuestionData cicClientQuestionData = cicdsData.getCicClientQuestionData();
        ApplicationEntity application = cic.getApplication();

        Integer status = cicClientQuestionData.statusAsInt();
        // gen cic pdf
        PostQueryCICRequest.QueryCIC queryCIC = new PostQueryCICRequest.QueryCIC();
        queryCIC.setProductCode(cic.getProductCode());

        CICDataResponse cicDataResponse = new CICDataResponse();
        cicDataResponse.setClientQuestionId(cicClientQuestionData.clientQuestionIdAsLong());
        cicDataResponse.setStatus(status);
        if (CICStatus.equal(status, CICStatus.HAVE_DATA)
                || CICStatus.equal(status, CICStatus.DO_NOT_HAVE_INFORMATION)) {
            GeneratedPDFResult generatedPDFResult = cicService.generatePdf(application, queryCIC, cicDataResponse, applicationConfig.getJwt().buildBase64());
            if(ObjectUtils.isNotEmpty(generatedPDFResult)) {
                cic.setPdfDate(JsonUtil.convertObject2String(generatedPDFResult, objectMapper));
                cic.setPdfStatus(generatedPDFResult.isAttackChecklist());
                generatedPDFResults.put(cicClientQuestionData.getClientQuestionId(), generatedPDFResult);
            }
        }
    }

    private void updateChecklist(ApplicationEntity application,
                                 Map<String, GeneratedPDFResult> generatedPDFResults) {
        if (org.springframework.util.CollectionUtils.isEmpty(application.getCics())) {
            return;
        }

        String applicationBpmId = application.getBpmId();
        List<CicEntity> cicEntities = new ArrayList<>(application.getCics());
        // get checklist list from checklist service
        GroupChecklistDto checklistBaseResponse
                = ((ChecklistBaseResponse<GroupChecklistDto>) checklistService.reloadChecklistCIC(
                applicationBpmId, cicEntities)).getData();
        log.info("RB [getCicReportAsync] checklistBaseResponse {}", JsonUtil.convertObject2String(checklistBaseResponse, objectMapper));

        if (checklistBaseResponse == null
                || CollectionUtils.isEmpty(checklistBaseResponse.getListChecklist())) {
            log.info("RB [getCicReportAsync] Hồ sơ {} chưa có list checklist", applicationBpmId);
            return;
        }

        Map<Long, List<CicEntity>> cicEntityMap = new HashMap<>();
        for (CicEntity cicEntity : cicEntities) {
            List<CicEntity> cicEntityList = cicEntityMap.get(cicEntity.getRefCustomerId());
            if (CollectionUtils.isEmpty(cicEntityList)) {
                cicEntityList = new ArrayList<>();
                cicEntityMap.put(cicEntity.getRefCustomerId(), cicEntityList);
            }
            cicEntityList.add(cicEntity);
        }

        log.info("RB [getCicReportAsync] cicEntityMap {}", JsonUtil.convertObject2String(cicEntityMap, objectMapper));
        log.info("RB [getCicReportAsync] checklistBaseResponse.getListChecklist() {}",
                JsonUtil.convertObject2String(checklistBaseResponse.getListChecklist(), objectMapper));

        List<ChecklistDto> checklistDtoCICs = new ArrayList<>();
        // find and add file into checklist
        for (ChecklistDto checklistDto : checklistBaseResponse.getListChecklist()) {
            if (!Group.isCicGroup(checklistDto.getGroupCode())
                    || !ChecklistEnum.isCreditworthiness(checklistDto.getCode())
                    || !DomainType.isCustomer(checklistDto.getDomainType())) {
                if (CollectionUtils.isEmpty(checklistDto.getListFile())) {
                    checklistDto.setListFile(new ArrayList<>());
                }
                continue;
            }
            log.info("RB [getCicReportAsync] checklistDto {}", checklistDto);
            List<CicEntity> cics = cicEntityMap.get(checklistDto.getDomainObjectId());
            log.info("RB [getCicReportAsync] cics {}", cics);
            if (org.springframework.util.CollectionUtils.isEmpty(cics)) {
                continue;
            }
            List<FileDto> listFile = new ArrayList<>();

            Set<Long> clientQuestionIds = new HashSet<>();
            for (CicEntity cic : cics) {
                if (cic.getClientQuestionId() == null
                        || clientQuestionIds.contains(cic.getClientQuestionId())) {
                    continue;
                }
                clientQuestionIds.add(cic.getClientQuestionId());
                GeneratedPDFResult generatedPDFResult = generatedPDFResults.get(
                        cic.getClientQuestionId().toString());
                FileDto fileDto = createFile(cic, generatedPDFResult);
                if (fileDto != null) {
                    listFile.add(fileDto);
                    generatedPDFResult.setAttackChecklist(true);
                }
            }

            if (!CollectionUtils.isEmpty(listFile)) {
                checklistDto.setListFile(listFile);
                checklistDtoCICs.add(checklistDto);
            }
        }
        // save checklist into checklist service
        CreateChecklistRequest createChecklistRequest = CreateChecklistRequest.builder()
                .requestCode(checklistBaseResponse.getRequestCode())
                .businessFlow(BusinessFlow.BO_USL_NRM.name())
                .phaseCode(PhaseCode.NRM_ALL_S.name())
                .listChecklist(checklistDtoCICs)
                .build();
        log.info("RB [getCicReportAsync] createChecklistRequest {}", createChecklistRequest);
        try {
            checklistService.uploadFileChecklistInternal(
                    createChecklistRequest, applicationConfig.getJwt().buildBase64());
        } catch (Exception ex) {
            log.error("RB [syncCIC] save_checklist_fail_ {} and cause {}", application.getBpmId(), ex);
        }
    }

    private FileDto createFile(CicEntity cic, GeneratedPDFResult generatedPDFResult) {
        try {

            log.info("RB [getCicReportAsync] cic {}, generatedPDFResult {}", cic, generatedPDFResult);
            if (cic.getClientQuestionId() == null
                    || generatedPDFResult == null
                    || !generatedPDFResult.isSuccess()) {
                return null;
            }
            String customerName = cic != null
                    && cic.getCicCustomerName() != null ?
                    cic.getCicCustomerName() : StringUtil.EMPTY;
            String time = cic != null
                    && cic.getQueryAt() != null ?
                    DateUtils.format(cic.getQueryAt(), YYYY_MM_DD_FORMAT) :
                    Util.getCurrDate(YYYY_MM_DD_FORMAT);

            log.info("RB [getCicReportAsync] generatedPDFResult {}", generatedPDFResult);
            log.info("RB [getCicReportAsync] ===============");

            StringBuilder fileNameBuilder = new StringBuilder();
            if (cic.getCicCode() != null) {
                fileNameBuilder.append(cic.getCicCode())
                        .append(StringUtil.UNDERSCORE);
            }

            String fileName = fileNameBuilder
                    .append(customerName)
                    .append(StringUtil.UNDERSCORE)
                    .append(cic.getProductCode())
                    .append(StringUtil.UNDERSCORE)
                    .append(time)
                    .toString();

            return new FileDto()
                    .withFileName(fileName)
                    .withFileSize(generatedPDFResult.getUploadFileResult().getFileSize().toString())
                    .withFileType(generatedPDFResult.getUploadFileResult().getFileType())
                    .withMinioPath(generatedPDFResult.getUploadFileResult().getPath())
                    .withBucket(generatedPDFResult.getUploadFileResult().getBucketName());
        } catch (Exception ex) {
            log.error("RB [getCicReportAsync] createFile error! {}", ex);
            return null;
        }
    }
}