package com.msb.bpm.approval.appr.service.intergated.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.cache.CICCache;
import com.msb.bpm.approval.appr.client.cic.CICClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.config.cic.CICProperties;
import com.msb.bpm.approval.appr.config.kafka.CICKafkaConsumerConfig;
import com.msb.bpm.approval.appr.config.kafka.CICKafkaProperties;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.checklist.BusinessFlow;
import com.msb.bpm.approval.appr.enums.checklist.ChecklistEnum;
import com.msb.bpm.approval.appr.enums.checklist.CustomerRelationShip;
import com.msb.bpm.approval.appr.enums.checklist.DomainType;
import com.msb.bpm.approval.appr.enums.checklist.Group;
import com.msb.bpm.approval.appr.enums.cic.CICProductCode;
import com.msb.bpm.approval.appr.enums.cic.CICStatus;
import com.msb.bpm.approval.appr.enums.cic.SpecialMission;
import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.enums.report.CICReportType;
import com.msb.bpm.approval.appr.enums.sql.SQLCommand;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.kafka.producer.CICProducer;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.dto.cic.CICDataCache;
import com.msb.bpm.approval.appr.model.dto.cic.CicBranchData;
import com.msb.bpm.approval.appr.model.dto.cic.GeneratePDFInput;
import com.msb.bpm.approval.appr.model.dto.cic.GeneratedPDFResult;
import com.msb.bpm.approval.appr.model.dto.cic.kafka.in.*;
import com.msb.bpm.approval.appr.model.dto.cic.report.CICReportData;
import com.msb.bpm.approval.appr.model.dto.statisticfile.UploadFileResult;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CICKafkaHistory;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.cic.CICCustomerInfo;
import com.msb.bpm.approval.appr.model.request.cic.SearchCICIntegrationRequest;
import com.msb.bpm.approval.appr.model.request.cic.SearchCodeCICRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest.QueryCIC;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.cic.*;
import com.msb.bpm.approval.appr.repository.CICKafkaHistoryRepository;
import com.msb.bpm.approval.appr.repository.CICRepository;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.service.cic.BranchService;
import com.msb.bpm.approval.appr.service.cic.ReportService;
import com.msb.bpm.approval.appr.service.intergated.CICService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.minio.MinIOService;
import com.msb.bpm.approval.appr.service.notification.NotificationService;
import com.msb.bpm.approval.appr.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.*;
import static com.msb.bpm.approval.appr.constant.Constant.YYYY_MM_DD_FORMAT;
import static com.msb.bpm.approval.appr.exception.DomainCode.ERROR_SEARCH_CIC;
import static com.msb.bpm.approval.appr.util.Util.FORMATTER_YYYYMMDD;
import static com.msb.bpm.approval.appr.util.Util.distinctByKey;

@Service
@AllArgsConstructor
@Slf4j
public class CICServiceImpl implements CICService {

  private final CICClient client;
  private final ObjectMapper objectMapper;
  private final CommonService commonService;
  private final CICKafkaConsumerConfig cicKafkaConsumerConfig;
  private final CICKafkaProperties cicKafkaConfig;
  private final CICProducer cicProducer;
  private final CICClient cicClient;
  private final CICRepository cicRepository;
  private final ReportService reportService;
  private final MinIOService minIOService;
  private final CICProperties cicProperties;
  private final BranchService branchService;
  private final CICKafkaHistoryRepository cicKafkaHistoryRepository;
  private final ChecklistService checklistService;
  private final CICCache cicCache;
  private final ApplicationConfig applicationConfig;
  private final NotificationService notificationService;

  @Override
  public Set<CustomerCIC> searchCode(String customerUniqueId, String customerType, String token) {
    SearchCodeCICRequest request = new SearchCodeCICRequest(customerUniqueId, customerType);
    CICResponse response = client.searchCode(request, token);
    if (response == null
        || response.isInternalError()) {
      throw new ApprovalException(DomainCode.CIC_SERVICE_ERROR);
    }
    if (response.isInvalidResponse()) {
      throw new ApprovalException(DomainCode.INVALID_PARAMETER);
    }
    String data = JsonUtil.convertObject2String(response.getValue(), objectMapper);
    Set<CustomerCIC> result = JsonUtil.convertString2Set(data, CustomerCIC.class, objectMapper);
    result = groupCustomerCic(result);
    return result;
  }

  private Set<CustomerCIC> groupCustomerCic(Set<CustomerCIC> customerCICS) {
    Map<String, CustomerCIC> cicCodes = new HashMap<>();
    for (CustomerCIC customerCIC : customerCICS) {
      if (!cicCodes.containsKey(customerCIC.getCicCode())
          && StringUtils.hasText(customerCIC.getAddress())
          && StringUtils.hasText(customerCIC.getCustomerName())) {
        cicCodes.put(customerCIC.getCicCode(), customerCIC);
      }
    }
    return new HashSet<>(cicCodes.values());
  }

  @Override
  public CICData search(SearchCICIntegrationRequest request, String token) {
    CICIntegrationResponse response = client.search(request, token);
    if(ObjectUtils.isNotEmpty(response) && (CIC_17.equals(response.getCategory())
            || CIC_18.equals(response.getCategory())
            || CIC_19.equals(response.getCategory())
            || CIC_21.equals(response.getCategory())
            || CIC_24.equals(response.getCategory())
      )
    ) {
      String message = "Lỗi tài khoản tra cứu CIC";
      if(!StringUtil.isEmpty(response.getMessage())) {
        message = response.getMessage().replace("requestUser", "tài khoản CIC");
      }
      throw new ApprovalException(DomainCode.ERROR_CIC,
              new Object[]{org.apache.commons.lang3.StringUtils.capitalize(message)});
    }

    if (ObjectUtils.isNotEmpty(response) && response.isInvalidResponse()) {
      throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[]{response.getMessage()});
    }

    if (response == null
            || response.getValue() == null
            || response.getValue().get(0) == null
            || response.getValue().get(0).getResult() == null
            || response.getValue().get(0).getResult().getStatus() == null) {
      throw new ApprovalException(DomainCode.CIC_SERVICE_ERROR, new Object[]{response.getMessage()});
    }
    if(ObjectUtils.isNotEmpty(request)
            && ObjectUtils.isNotEmpty(request.getCustomerInfo())
            && ObjectUtils.isEmpty(response.getValue().get(0).getCicCode())) {
        request.getCustomerInfo().stream().findFirst().ifPresent(req -> {
        response.getValue().get(0).setCicCode(req.getCicCode());
      });
    }
    return response.getValue().get(0);
  }

  @Override
  @Async
  public CompletableFuture<List<CICDataResponse>> getDataFromCICAsync(QueryCIC request,
      String token,
      ApplicationEntity entityApp) {
    return CompletableFuture.completedFuture(getDataFromCIC(request, token, entityApp));
  }

  @Override
  public List<CICDataResponse> getDataFromCIC(QueryCIC request, String token,
      ApplicationEntity entityApp) {
    Long cusId = entityApp.getCustomer().getId();
    log.info("START with request={}", request);
    List<CICDataResponse> lstResult = new ArrayList<>();
    String customerType = request.getCustomerType().getTypeNumber();
    String subject = commonService.getCustomerType(request.getCustomerId(), customerType, cusId);
    try {

      Set<CustomerCIC> lstCusCic = searchCode(request.getIdentifierCode(),
          customerType, token);
      if (CollectionUtils.isEmpty(lstCusCic)) {
        throw new ApprovalException(ERROR_SEARCH_CIC, new Object[]{request.getIdentifierCode(),
            request.getCustomerType()});
      }

      CicBranchData cicBranchData = branchService.getCicBranchData(entityApp, token);
      for (CustomerCIC item : lstCusCic) {
        String customerUniqueId = StringUtils.hasText(item.getBusinessRegNum()) ?
            item.getBusinessRegNum() :
            item.getUniqueId();

        List<CICCustomerInfo> customerInfo = new ArrayList<>();
        CICCustomerInfo cicCustomerInfo = CICCustomerInfo.builder()
                .customerType(customerType)
                .productCode(request.getProductCode())
                .cicCode(item.getCicCode())
                .address(item.getAddress())
                .customerName(item.getCustomerName())
                .taxNo(item.getTaxCode())
                .cbusRegNumber(item.getBusinessRegNum())
                .cusIdNumber(customerUniqueId)
                .build();
        // Bổ-sung-logic-gửi-tin-API-lấy-bản-hỏi
        if(CustomerType.EB.getTypeNumber().equals(customerType)) {
          cicCustomerInfo.setCusIdNumber(null);
          if(StringUtil.isEmpty(item.getTaxCode()))
            cicCustomerInfo.setTaxNo(item.getBusinessRegNum());
        } else {
          cicCustomerInfo.setTaxNo(null);
          cicCustomerInfo.setCbusRegNumber(null);
          cicCustomerInfo.setCusIdNumber(request.getIdentifierCode());
        }

        customerInfo.add(cicCustomerInfo);
        CICData data = search(SearchCICIntegrationRequest.builder()
                .userNameDirectRq(cicProperties.getUsername())
                .requestUser(SecurityContextUtil.getUsername())
                .branchCode(cicBranchData.getCicBranchCode())
                .dealingRoomCode(cicBranchData.getCicDealingRoomCode())
                .customerInfo(customerInfo)
                .build(), token);
        CICDataResponse cicDataResponse = CICDataResponse.builder()
                .customerId(request.getCustomerId())
                .refCustomerId(request.getRefCustomerId())
                .relationship(request.getRelationship())
                .identifierCode(request.getIdentifierCode())
                .cicCode(data.getCicCode())
                .subject(subject)
                .customerType(request.getCustomerType().toString())
                .customerName(item.getCustomerName())
                .productCode(request.getProductCode())
                .build();
        if(ObjectUtils.isNotEmpty(data.getResult().getErrorCodeSBV())) {
          StringBuffer sbv = new StringBuffer("");
          String end = "";
          if(data.getResult().getErrorCodeSBV().size() > 1) {
            end = "<CIC_ERROR<";
          }
          for (Map.Entry<String, String> sbvData : data.getResult().getErrorCodeSBV().entrySet()) {
            sbv.append(sbvData.getKey() + " - ");
            if(!StringUtil.isEmpty(sbvData.getValue())) {
              sbv.append(sbvData.getValue()).append(end);
            }
          }
          cicDataResponse.setErrorCodeSBV(sbv.toString());
        }

        if(!StringUtil.isEmpty(data.getResult().getStatus())) {
          cicDataResponse.setStatus(Integer.valueOf(data.getResult().getStatus()));
        }

        if(!StringUtil.isEmpty(data.getResult().getClientQuestionId())) {
          cicDataResponse.setClientQuestionId(Long.valueOf(data.getResult().getClientQuestionId()));
        }

        if(!StringUtil.isEmpty(data.getResult().getH2hResponseTime())) {
          cicDataResponse.setQueryAt(LocalDateTime.of(LocalDate.parse(data.getResult().getH2hResponseTime(),
                  FORMATTER_YYYYMMDD), LocalTime.of(0, 0)));
        }
        lstResult.add(cicDataResponse);
      }
    } catch (Exception ex) {
      log.error("getDataFromCIC with request={}, Error:", request, ex);
      if (Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
          ((ApprovalException) ex).getCode())
              || Objects.equals(DomainCode.ERROR_CIC,
              ((ApprovalException) ex).getCode())
      ) {
        throw ex;
      }
      int status = -1;
      if (Objects.equals(DomainCode.ERROR_SEARCH_CIC,
          ((ApprovalException) ex).getCode())) {
        // trạng thái 0 - không có mã cic
        status = 0;
      }
      lstResult.add(CICDataResponse.builder()
          .customerId(request.getCustomerId())
          .refCustomerId(request.getRefCustomerId())
          .relationship(request.getRelationship())
          .status(status)
          .identifierCode(request.getIdentifierCode())
          .subject(subject)
          .customerType(request.getCustomerType().toString())
          .productCode(request.getProductCode())
          .build());
    }
    log.info("END with request={}, lstResult={}", request, lstResult);
    return lstResult;
  }

  private static final String WHERE_IN = " SO_MAINID IN (%s) ";


  private String getCICQueryIn(Collection<String> objects) {
    return new StringBuilder()
        .append(String.join(StringUtil.COMMA, objects))
        .toString();
  }

  @Async
  @Override
  public void publishMessage(List<CICDataResponse> lstResult, ApplicationEntity entityApp) {
    if (!cicProperties.isEnabled()) {
      return;
    }
    updateSyncingIndicatorCICFlag(entityApp.getBpmId(), true);
    List<String> clientQuestionIds = lstResult.stream()
        .filter(cicDataResponse -> cicDataResponse.getStatus() != null
            && CICStatus.isHaveData(cicDataResponse.getStatus()))
        .map(cicDataResponse -> cicDataResponse.getClientQuestionId() != null ?
            cicDataResponse.getClientQuestionId().toString() : null)
        .filter(distinctByKey(clientQuestionId -> clientQuestionId))
        .collect(Collectors.toList());
    publishCICMessage(clientQuestionIds, entityApp);
  }

  @Async
  public void publishMessage(ApplicationEntity entityApp) {
    if (!cicProperties.isEnabled()) {
      return;
    }
    updateSyncingIndicatorCICFlag(entityApp.getBpmId(), true);
    List<String> clientQuestionIds = entityApp.getCics().stream()
        .filter(cic -> cic.getStatusCode() != null
            && CICStatus.isHaveData(cic.getStatusCode())
            && !cic.isCicIndicatorStatus())
        .map(cicDataResponse -> cicDataResponse.getClientQuestionId() != null ?
            cicDataResponse.getClientQuestionId().toString() : null)
        .filter(distinctByKey(clientQuestionId -> clientQuestionId))
        .collect(Collectors.toList());
    publishCICMessage(clientQuestionIds, entityApp);
  }

  private void publishCICMessage(List<String> clientQuestionIds, ApplicationEntity entityApp) {
    if (CollectionUtils.isEmpty(clientQuestionIds)) {
      log.info("clientQuestionIds is empty! " + clientQuestionIds);
      updateSyncingIndicatorCICFlag(entityApp.getBpmId(), false);
      return;
    }
    UserHeader userHeader = UserHeader.builder()
        .user(cicKafkaConfig.getUserHeader().getUser())
        .kafkaTopicOut(cicKafkaConsumerConfig.getTopic())
        .specialMission(SpecialMission.ON_DEMAND_STREAM.toString())
        .tableName(cicKafkaConfig.getUserHeader().getTableName())
        .build();

    String requestId = UUID.randomUUID().toString();
    UserData userData = UserData.builder()
        .user(cicKafkaConfig.getUserHeader().getUser())
        .myAction(SQLCommand.SELECT.toString())
        .socketChannel("Data")
        .tableName(cicKafkaConfig.getUserHeader().getTableName())
        .specialMission(SpecialMission.ON_DEMAND_STREAM.toString())
        .requestJsonId(requestId)
        .specialMessage(SpecialMessage.builder()
            .sqlType(cicKafkaConfig.getUserHeader().getSqlType())
            .crtCode(cicKafkaConfig.getUserHeader().getCrtCode())
            .build())
        .build();

    List<Action> actions = new ArrayList<>();
    String where = String.format(WHERE_IN, getCICQueryIn(clientQuestionIds));

    Action action = Action.builder()
        .select("SELECT CRT_CODE,FORMULA FROM " + cicKafkaConfig.getUserHeader()
            .getTableName())
        .having("")
        .groupBy("")
        .orderBy("")
        .where(where)
        .paging(" OFFSET 0 ROWS FETCH NEXT 1000 ROWS ONLY ")
        .build();
    actions.add(action);

    RequestMessageData requestMessageData = RequestMessageData.builder()
        .userData(userData)
        .actionArray(actions)
        .build();

    CicRequestMessage cicRequestMessage = CicRequestMessage.builder()
        .userHeader(userHeader)
        .data(requestMessageData)
        .build();

    cicProducer.publish(cicRequestMessage);

    CICKafkaHistory cicKafkaHistory = new CICKafkaHistory()
        .withRequestId(requestId)
        .withTopic(cicProducer.getCicInTopic())
        .withMessage(JsonUtil.convertObject2String(cicRequestMessage, objectMapper))
        .withClientQuestionIds(JsonUtil.convertObject2String(clientQuestionIds, objectMapper))
        .withApplicationBpmId(entityApp.getBpmId())
        .withApplicationId(entityApp.getId());
    cicKafkaHistoryRepository.save(cicKafkaHistory);
  }

  @Async
  @Override
  public void getCicReportAsync(ApplicationEntity entityApp,
      List<CICDataResponse> lstResult,
      PostQueryCICRequest request,
      String currentUser) {
    if (CollectionUtils.isEmpty(lstResult)) {
      log.info("[getCicReportAsync] Hồ sơ {} có clientQuestionIds rỗng", entityApp.getId());
      return;
    }
//    updateSyncingPDFCICFlag(entityApp.getBpmId(), true);
    Map<String, QueryCIC> queryCICMap = new HashMap<>();
    request.getData().stream().forEach(queryCIC -> {
      queryCICMap.put(queryCIC.getIdentifierCode() + queryCIC.getRefCustomerId(), queryCIC);
    });
    log.info("[getCicReportAsync] queryCICMap {}", queryCICMap);
    // get report data from cic, generate and upload pdf to minio
    Map<String, GeneratedPDFResult> generatedPDFResults = generatePdf(
        entityApp,
        lstResult,
        queryCICMap
    );

    log.info("[getCicReportAsync] generatedPDFResults {}", generatedPDFResults);
    if (generatedPDFResults.isEmpty()) {
      return;
    }

    Long applicationId = entityApp.getId();
    String applicationBpmId = entityApp.getBpmId();
    List<CicEntity> cicEntities = cicRepository.findByApplicationId(applicationId).get();
    log.info("[getCicReportAsync] cicEntities {}", cicEntities);
    if (CollectionUtils.isEmpty(cicEntities)) {
      log.info("[getCicReportAsync] Hồ sơ {} không có mã truy vấn cic trong database",
          applicationBpmId);
      return;
    }

    updateChecklist(entityApp, generatedPDFResults);
    savePDFDataToDB(cicEntities, generatedPDFResults);
    updateSyncingPDFCICFlag(entityApp.getBpmId(), false);
    notificationService.noticePdfCic(currentUser, entityApp);
  }

  @Async
  @Override
  public void getCicReportAsync(ApplicationEntity application, String currentUser) {
    updateSyncingPDFCICFlag(application.getBpmId(), true);
    Map<String, GeneratedPDFResult> generatedPDFResults = generatePdf(application);
    log.info("[syncCIC] generatedPDFResults {}", generatedPDFResults);
    if (generatedPDFResults.isEmpty()) {
      updateSyncingPDFCICFlag(application.getBpmId(), false);
      return;
    }
    updateChecklistForSync(application, generatedPDFResults);
    savePDFDataToDB(application.getCics(), generatedPDFResults);
    updateSyncingPDFCICFlag(application.getBpmId(), false);
    notificationService.noticePdfCic(currentUser, application);
  }

  @Override
  public boolean isSyncingCIC(String applicationBpmId) {
    CICDataCache cicDataCache = cicCache.get(applicationBpmId);
    return cicDataCache == null ? false
        : cicDataCache.isSyncPdf() || cicDataCache.isSyncIndicator();
  }

  @Override
  public void updateSyncingIndicatorCICFlag(String applicationBpmId, boolean syncingFlag) {
    log.info("[syncCIC] updateSyncingIndicatorCICFlag: applicationBpmId {}, syncingFlag {}",
        applicationBpmId, syncingFlag);
    CICDataCache cicDataCache = cicCache.get(applicationBpmId);
    if (cicDataCache == null) {
      cicDataCache = new CICDataCache();
      cicDataCache.setApplicationBpmId(applicationBpmId);
    }
    cicDataCache.setSyncIndicator(syncingFlag);
    cicCache.set(applicationBpmId, cicDataCache);
  }

  @Override
  public void updateSyncingPDFCICFlag(String applicationBpmId, boolean syncingFlag) {
    log.info("[syncCIC] updateSyncingPDFCICFlag: applicationBpmId {}, syncingFlag {}",
        applicationBpmId, syncingFlag);
    CICDataCache cicDataCache = cicCache.get(applicationBpmId);
    if (cicDataCache == null) {
      cicDataCache = new CICDataCache();
      cicDataCache.setApplicationBpmId(applicationBpmId);
    }
    cicDataCache.setSyncPdf(syncingFlag);
    cicCache.set(applicationBpmId, cicDataCache);
  }


  private void updateChecklistForSync(ApplicationEntity application,
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
    log.info("[getCicReportAsync] checklistBaseResponse {}", checklistBaseResponse);

    if (checklistBaseResponse == null
        || CollectionUtils.isEmpty(checklistBaseResponse.getListChecklist())) {
      log.info("[getCicReportAsync] Hồ sơ {} chưa có list checklist", applicationBpmId);
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

    log.info("[getCicReportAsync] cicEntityMap {}", cicEntityMap);
    log.info("[getCicReportAsync] checklistBaseResponse.getListChecklist() {}",
        checklistBaseResponse.getListChecklist());

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
      log.info("[getCicReportAsync] checklistDto {}", checklistDto);
      List<CicEntity> cics = cicEntityMap.get(checklistDto.getDomainObjectId());
      if(!DomainType.isCustomer(checklistDto.getDomainType())) {
        cicEntities.forEach(c -> {
          if(CustomerRelationShip.R5.toString().equals(c.getRelationship())) {
            cics.add(c);
          }
        });
      }
      log.info("[getCicReportAsync] cics {}", cics);
      if (org.springframework.util.CollectionUtils.isEmpty(cics)) {
        continue;
      }
      List<FileDto> listFile = new ArrayList<>();

      Set<Long> clientQuestionIds = new HashSet<>();
      for (CicEntity cic : cics) {
        if (cic.getClientQuestionId() == null
            || clientQuestionIds.contains(cic.getClientQuestionId())
            || cic.isPdfStatus()) {
          continue;
        }
        clientQuestionIds.add(cic.getClientQuestionId());
        GeneratedPDFResult generatedPDFResult = generatedPDFResults.get(
            cic.getClientQuestionId().toString());

        if(ObjectUtils.isNotEmpty(generatedPDFResult)
                && org.apache.commons.lang3.StringUtils.equals(cic.getCicCode(), generatedPDFResult.getCicCode())
                && ObjectUtils.isNotEmpty(cic.getQueryAt())
                && ObjectUtils.isNotEmpty(generatedPDFResult.getQueryAt())
                && cic.getQueryAt().toLocalDate().equals(Util.parseString2LocalDate(generatedPDFResult.getQueryAt(), TIME_CIC_FORMAT))
                && ObjectUtils.isNotEmpty(cic.isPdfStatus())
                && cic.isPdfStatus()
        ) continue;

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
    log.info("[getCicReportAsync] createChecklistRequest {}", createChecklistRequest);
    try {
      checklistService.uploadFileChecklistInternal(
          createChecklistRequest, applicationConfig.getJwt().buildBase64());
    } catch (Exception ex) {
      log.error("[syncCIC] save_checklist_fail_" + application.getBpmId(), ex);
      updateAttackChecklistFail(generatedPDFResults);
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
    log.info("[getCicReportAsync] checklistBaseResponse {}", checklistBaseResponse);

    if (checklistBaseResponse == null
        || CollectionUtils.isEmpty(checklistBaseResponse.getListChecklist())) {
      log.info("[getCicReportAsync] Hồ sơ {} chưa có list checklist", applicationBpmId);
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

    log.info("[getCicReportAsync] cicEntityMap {}", cicEntityMap);
    log.info("[getCicReportAsync] checklistBaseResponse.getListChecklist() {}",
        checklistBaseResponse.getListChecklist());

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
      log.info("[getCicReportAsync] checklistDto {}", checklistDto);
      List<CicEntity> cics = cicEntityMap.get(checklistDto.getDomainObjectId());
      if(!DomainType.isCustomer(checklistDto.getDomainType())) {
        cicEntities.forEach(c -> {
          if(CustomerRelationShip.R5.toString().equals(c.getRelationship())) {
            cics.add(c);
          }
        });
      }
      log.info("[getCicReportAsync] cics {}", cics);
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

        // Check R5 set checklist to customer
        if(CustomerRelationShip.R5.toString().equals(cic.getRelationship())) {
          Long refCustomerId = application.getCustomer().getRefCustomerId();
          List<ChecklistDto> tmp = checklistBaseResponse.getListChecklist().stream().filter(cl -> cl.getDomainObjectId().equals(refCustomerId)).collect(Collectors.toList());
          if(ObjectUtils.isNotEmpty(tmp)) {
            checklistDto = tmp.stream().findFirst().get();
            log.info("Re-set file cic R5 to V017 with checklist: {}", JsonUtil.convertObject2String(checklistDto, objectMapper));
            listFile = checklistDto.getListFile();
          } else {
            log.info("Re-set file cic R5 to V017 fail");
            continue;
          }
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
      // Check exist object R5
      Long domainId = checklistDto.getDomainObjectId();
      if(ObjectUtils.isNotEmpty(checklistDtoCICs.stream().filter(cicDto -> cicDto.getDomainObjectId().equals(domainId)).collect(Collectors.toList()))) {
        log.info("Check exist cic checklist object cic R5 to V017");
        continue;
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
    log.info("[getCicReportAsync] createChecklistRequest {}", createChecklistRequest);
    try {
      checklistService.uploadFileChecklistInternal(
          createChecklistRequest, applicationConfig.getJwt().buildBase64());
    } catch (Exception ex) {
      log.error("[syncCIC] save_checklist_fail_" + application.getBpmId(), ex);
      updateAttackChecklistFail(generatedPDFResults);
    }
  }

  private void updateAttackChecklistFail(Map<String, GeneratedPDFResult> generatedPDFResults) {
    for (Map.Entry<String, GeneratedPDFResult> entry : generatedPDFResults.entrySet()) {
      entry.getValue().setAttackChecklist(false);
    }
  }

  private Map<String, GeneratedPDFResult> generatePdf(ApplicationEntity application) {
    Map<String, GeneratedPDFResult> generatedPDFResultMap = new HashMap<>();
    for (CicEntity cic : application.getCics()) {
      if (cic != null
          && !cic.isPdfStatus()
          && (CICStatus.isHaveData(cic.getStatusCode())
          || CICStatus.isDoNotHaveData(cic.getStatusCode())
          || CICStatus.isCICCodeNotExist(cic.getStatusCode()))) {
        GeneratePDFInput generatePDFInput = GeneratePDFInput.builder()
            .application(application)
            .cicStatus(Integer.valueOf(cic.getStatusCode()))
            .clientQuestionId(cic.getClientQuestionId().toString())
            .productCode(cic.getProductCode()).build();

        GeneratedPDFResult generatedPDFResult = generatePdf(generatePDFInput);
        if (generatedPDFResultMap.get(generatedPDFResult.getClientQuestionId()) != null) {
          log.info(
              "[getCicReportAsync] DUPLICATE_PDF_RESULT! CLIENT QUESTION ID {}, cic {}",
              generatedPDFResult.getClientQuestionId(), cic);
        }
        generatedPDFResultMap.put(generatedPDFResult.getClientQuestionId(), generatedPDFResult);
      }
    }

    return generatedPDFResultMap;
  }

  private Map<String, GeneratedPDFResult> generatePdf(ApplicationEntity entityApp,
      List<CICDataResponse> lstResult,
      Map<String, QueryCIC> queryCICMap) {
    Map<String, GeneratedPDFResult> generatedPDFResults = new HashMap<>();
    for (CICDataResponse cicDataResponse : lstResult) {
      if (cicDataResponse != null
          && (CICStatus.isHaveData(cicDataResponse.getStatus())
          || CICStatus.isDoNotHaveData(cicDataResponse.getStatus())
          || CICStatus.isCICCodeNotExist(cicDataResponse.getStatus()))) {
        QueryCIC queryCIC = queryCICMap.get(
            cicDataResponse.getIdentifierCode() + cicDataResponse.getRefCustomerId());
        GeneratedPDFResult generatedPDFResult = generatePdf(entityApp, queryCIC, cicDataResponse, null);
        if (generatedPDFResults.get(generatedPDFResult.getClientQuestionId()) != null) {
          log.info(
              "[getCicReportAsync] DUPLICATE_PDF_RESULT! CLIENT QUESTION ID {}, lstResult {}, queryCICMap {}, ",
              generatedPDFResult.getClientQuestionId(),
              lstResult,
              queryCICMap);
        }
        generatedPDFResults.put(generatedPDFResult.getClientQuestionId(), generatedPDFResult);
      }
    }
    return generatedPDFResults;
  }

  private void savePDFDataToDB(Collection<CicEntity> cicEntities,
      Map<String, GeneratedPDFResult> generatedPDFResults) {
    try {
      for (CicEntity cic : cicEntities) {
        if (cic.getClientQuestionId() == null) {
          continue;
        }
        GeneratedPDFResult generatedPDFResult = generatedPDFResults.get(cic.getClientQuestionId()
            .toString());
        if (generatedPDFResult != null) {
          cic.setPdfDate(JsonUtil.convertObject2String(generatedPDFResult, objectMapper));
          cic.setPdfStatus(generatedPDFResult.isAttackChecklist());
        }
      }
      cicRepository.saveAll(cicEntities);
    } catch (Exception ex) {
      log.error("[getCicReportAsync] savePDFDataToDB error! ", ex);
    }
  }

  private FileDto createFile(CicEntity cic, GeneratedPDFResult generatedPDFResult) {
    try {

      log.info("[getCicReportAsync] cic {}, generatedPDFResult {}", cic, generatedPDFResult);
      // tra  cứu cic bị lỗi, nên không có client question id
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

      log.info("[getCicReportAsync] generatedPDFResult {}", generatedPDFResult);
      log.info("[getCicReportAsync] ===============");

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
      log.error("[getCicReportAsync] createFile error!", ex);
      return null;
    }
  }

  @Override
  public GeneratedPDFResult generatePdf(ApplicationEntity entityApp,
      QueryCIC queryCIC,
      CICDataResponse cicDataResponse, String token) {
    GeneratedPDFResult generatedPDFResult = new GeneratedPDFResult();
    try {
      String clientQuestionId = cicDataResponse.getClientQuestionId().toString();
      generatedPDFResult.setClientQuestionId(clientQuestionId);

      GetPDFDataResponse response;
      if(StringUtil.isEmpty(token)) {
        response = cicClient.getPDFData(clientQuestionId);
      } else {
        response = cicClient.getPDFData(clientQuestionId, token);
      }
      String responseJson = JsonUtil.convertObject2String(response, objectMapper);
      UploadFileResult uploadFileResult = new UploadFileResult();
      uploadFileResult.setCicDataJson(responseJson);
      generatedPDFResult.setUploadFileResult(uploadFileResult);

      CICReportType CICReportType = getReportType(response, queryCIC, cicDataResponse);
      if (CICReportType == null) {
        log.info("[getCicReportAsync] Không lấy được report type");
        return generatedPDFResult;
      }
      byte[] reportFileData = reportService.getCicPdfReport(CICReportType,
          response.getValue());
      if (reportFileData == null) {
        log.info("[getCicReportAsync] Không lấy được file từ CIC. clientQuestionId: "
            + clientQuestionId);
        return generatedPDFResult;
      }

      String bpmApplicationId = entityApp.getBpmId();
      minIOService.uploadPdfFile(bpmApplicationId,
          reportFileData, uploadFileResult);

      generatedPDFResult.setUploadFileResult(uploadFileResult);
      generatedPDFResult.setSuccess(true);
      return generatedPDFResult;
    } catch (Exception ex) {
      log.error("[getCicReportAsync] generatePdf error! queryCIC : " + queryCIC, ex);
      generatedPDFResult.setSuccess(false);
      return generatedPDFResult;
    }
  }

  public GeneratedPDFResult generatePdf(GeneratePDFInput generatePDFInput) {
    GeneratedPDFResult generatedPDFResult = new GeneratedPDFResult();
    try {
      ApplicationEntity application = generatePDFInput.getApplication();
      String clientQuestionId = generatePDFInput.getClientQuestionId();

      generatedPDFResult.setClientQuestionId(clientQuestionId);

      GetPDFDataResponse response = cicClient.getPDFData(clientQuestionId);
      String responseJson = JsonUtil.convertObject2String(response, objectMapper);
      UploadFileResult uploadFileResult = new UploadFileResult();
      uploadFileResult.setCicDataJson(responseJson);
      generatedPDFResult.setUploadFileResult(uploadFileResult);

      CICReportType CICReportType = getReportType(response,
          generatePDFInput.getProductCode(),
          Integer.valueOf(generatePDFInput.getCicStatus()));

      if (CICReportType == null) {
        log.info("[getCicReportAsync] Không lấy được report type");
        return generatedPDFResult;
      }
      byte[] reportFileData = reportService.getCicPdfReport(CICReportType,
          response.getValue());
      if (reportFileData == null) {
        log.info("[getCicReportAsync] Không lấy được file từ CIC. clientQuestionId: "
            + clientQuestionId);
        return generatedPDFResult;
      }

      String bpmApplicationId = application.getBpmId();
      minIOService.uploadPdfFile(bpmApplicationId,
          reportFileData, uploadFileResult);

      generatedPDFResult.setUploadFileResult(uploadFileResult);
      generatedPDFResult.setSuccess(true);

      CICReportData cicReportData = (CICReportData) JsonUtil.convertObject2Object(response.getValue(),
              CICReportType.getDataClassType(), objectMapper);

      if(ObjectUtils.isNotEmpty(cicReportData) && ObjectUtils.isNotEmpty(cicReportData.getCus())) {
        generatedPDFResult.setQueryAt(cicReportData.getNgayTraTin());
        generatedPDFResult.setCicCode(cicReportData.getCus().getMaCIC());
      }
      return generatedPDFResult;
    } catch (Exception ex) {
      log.error("[getCicReportAsync] generatePdf error! generatePDFInput : " + generatePDFInput,
          ex);
      generatedPDFResult.setSuccess(false);
      return generatedPDFResult;
    }
  }

  private CICReportType getReportType(GetPDFDataResponse response,
      QueryCIC queryCIC,
      CICDataResponse cicDataResponse) {
    return getReportType(response, queryCIC.getProductCode(), cicDataResponse.getStatus());
  }

  private CICReportType getReportType(GetPDFDataResponse response,
      String productCode,
      Integer cicStatus) {
    log.info("[getCicReportAsync] GetPDFDataResponseCode: {}, productCode: {}, cicStatus: {}",
        response, productCode, cicStatus);
    if (response == null
        || response.getCode() == null
        || !response.getCode().equals(0)) {
      return null;
    }

    if (CICStatus.isDoNotHaveData(cicStatus)
        || CICStatus.isDoNotHaveData(cicStatus)) {
      return CICReportType.CIC_REPORT_BLANK;
    }

    if (CICStatus.isHaveData(cicStatus)) {
      return CICProductCode.isTSBD(productCode) ?
          CICReportType.CIC_REPORT_TSBD : CICReportType.CIC_REPORT_TNPN2;
    }
    return null;
  }
}
