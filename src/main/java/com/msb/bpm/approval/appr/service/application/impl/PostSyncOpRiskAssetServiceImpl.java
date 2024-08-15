package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SYNC_OPR_ASSET;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.legacy.OpRiskLegacyClient;
import com.msb.bpm.approval.appr.config.OpriskPersonConfig;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.oprisk.AssetGroupEnums;
import com.msb.bpm.approval.appr.enums.oprisk.AssetTypeEnums;
import com.msb.bpm.approval.appr.enums.oprisk.CollateralTypeOpRiskEnums;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.AmlOprMapper;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.AuthenInfo;
import com.msb.bpm.approval.appr.model.request.oprisk.CheckBlackListPLegalData;
import com.msb.bpm.approval.appr.model.request.oprisk.CheckBlackListPLegalRequest;
import com.msb.bpm.approval.appr.model.request.oprisk.OpRiskRequest;
import com.msb.bpm.approval.appr.model.request.oprisk.OpRiskRequest.Item;
import com.msb.bpm.approval.appr.model.response.oprisk.OpRiskResponse;
import com.msb.bpm.approval.appr.model.response.oprisk.SyncOpRiskAssetResponse;
import com.msb.bpm.approval.appr.repository.AmlOprRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostSyncOpRiskAssetServiceImpl extends AbstractBaseService implements
    BaseService<Object, OpRiskRequest> {
  private final OpRiskLegacyClient opRiskLegacyClient;
  private final OpriskPersonConfig opriskPersonConfig;
  private final AmlOprRepository opRiskRepository;
  private final AmlOprMapper opRiskMapper;
  private final ApplicationRepository applicationRepository;
  private final ObjectMapper objectMapper;

  @Override
  public String getType() {
    return POST_SYNC_OPR_ASSET;
  }

  @Override
  @Transactional
  public List<OpRiskResponse> execute(OpRiskRequest opRiskRequest, Object... obj) {
    log.info("START PostSyncOpRiskServiceImpl.execute with request: [{}]",
        JsonUtil.convertObject2String(opRiskRequest, objectMapper));
    List<OpRiskResponse> lstAmlResult = new ArrayList<>();
    List<AmlOprEntity> savedEntities = new ArrayList<>();
    opRiskRequest.getAssetData().stream().collect(Collectors.toMap(Item::getKey, Function.identity(), (existing, replacement) -> replacement)).values().forEach(reqDTO -> {
      verifyOpRiskAsset(opRiskRequest, reqDTO);
      OpRiskResponse response = OpRiskResponse.builder()
          .identifierCode(reqDTO.getCertificate())
          .applicationId(opRiskRequest.getApplicationId())
          .queryType(ApplicationConstant.OPR_ASSET_TAG)
          .build();

      SyncOpRiskAssetResponse data = callApiOpRisk(reqDTO.getCertificate(),
          CollateralTypeOpRiskEnums.getCollateralType(reqDTO.getAssetGroup()));
      try {
        if (data != null
            && data.getCheckBlackListC() != null
            && data.getCheckBlackListC().getRespDomain().getBlackList4LosC() != null) {
          response.setResultCode(data.getCheckBlackListC().getRespMessage().getRespCode().toString());
          response.setResultDescription(
              getResultDesc(data.getCheckBlackListC().getRespMessage().getRespCode(),
                  data.getCheckBlackListC().getRespDomain().getBlackList4LosC()
                      .getClassification()
                  , data.getCheckBlackListC().getRespDomain().getBlackList4LosC().getResult()));
          response.setReasonInList(
              data.getCheckBlackListC().getRespDomain().getBlackList4LosC().getListedReasons());
          response.setStartDate(
              data.getCheckBlackListC().getRespDomain().getBlackList4LosC().getDateAdded());
          response.setEndDate(
              data.getCheckBlackListC().getRespDomain().getBlackList4LosC().getEndDate());
        } else {
          response.setResultDescription(ApplicationConstant.RESULT_QUERY_ERROR);
          response.setResultCode(ApplicationConstant.QUERY_ERROR_CODE);
        }
      } catch (Exception e) {
        log.error("PostSyncOpRiskServiceImpl error {}: ", e.getMessage());
        response.setResultDescription(ApplicationConstant.RESULT_QUERY_ERROR);
        response.setResultCode(ApplicationConstant.QUERY_ERROR_CODE);
      }
      ApplicationEntity applicationEntity = applicationRepository.findByBpmId(opRiskRequest.getApplicationId())
          .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{opRiskRequest.getApplicationId()}));
      // Kiểm tra đã tồn tại chưa?
      AmlOprEntity oprEntitySaved = opRiskRepository.findByApplicationIdAndIdentifierCodeAndAssetGroupAndAssetType(
          applicationEntity.getId()
          , reqDTO.getCertificate()
          , reqDTO.getAssetGroup(),
          reqDTO.getAssetType()
      );
      AmlOprEntity oprEntityReq = opRiskMapper.toEntity(response);
      if (oprEntitySaved != null) {
        oprEntityReq.setId(oprEntitySaved.getId());
        oprEntityReq.setCreatedAt(oprEntitySaved.getCreatedAt());
        oprEntityReq.setCreatedBy(oprEntitySaved.getCreatedBy());
        oprEntityReq.setUpdatedAt(LocalDateTime.now());
        oprEntityReq.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
      }
      oprEntityReq.setApplication(applicationEntity);
      oprEntityReq.setAssetGroup(reqDTO.getAssetGroup());
      oprEntityReq.setAssetType(reqDTO.getAssetType());
      savedEntities.add(oprEntityReq);

      response.setAssetGroup(reqDTO.getAssetGroup());
      response.setAssetType(reqDTO.getAssetType());
      response.setCreatedAt(LocalDateTime.now());
      lstAmlResult.add(response);
    });
    opRiskRepository.saveAll(savedEntities);
    log.info("END PostSyncOpRiskServiceImpl.execute with response: [{}]",
        JsonUtil.convertObject2String(lstAmlResult, objectMapper));
    removeOpRiskAssetPostSync(savedEntities);
    return lstAmlResult;
  }

  private void verifyOpRiskAsset(OpRiskRequest opRiskRequest, Item reqDTO) {
    if (opRiskRequest.getApplicationId().trim().isEmpty()
        || reqDTO.getCertificate().trim().isEmpty()
        || reqDTO.getAssetGroup().trim().isEmpty()
        || reqDTO.getAssetType().trim().isEmpty()) {
      throw new ApprovalException(DomainCode.INVALID_PARAMETER);
    }
    if (!AssetGroupEnums.validCode(reqDTO.getAssetGroup())) {
      throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[] {"Nhóm tài sản không đúng"});
    }
    if (!AssetTypeEnums.validCode(reqDTO.getAssetType())) {
      throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[] {"Loại tài sản không đúng"});
    }
    if (AssetGroupEnums.invalidChildByParentCode(reqDTO.getAssetGroup(), reqDTO.getAssetType())) {
      throw new ApprovalException(DomainCode.INVALID_PARAMETER, new Object[] {"Loại tài sản không khớp với nhóm tài sản"});
    }
  }

  private String getResultDesc(Long respCode, String valueRespCode, Boolean result) {
    if (Objects.isNull(respCode)) {
      return "";
    }

    String statusResult;
    if (0L != respCode) {
      statusResult = ApplicationConstant.RESULT_QUERY_ERROR;
    } else if (Objects.equals(Boolean.TRUE, result)) {
      statusResult = (valueRespCode == null || valueRespCode.trim().isEmpty())
          ? ApplicationConstant.ON_THE_LIST
          : valueRespCode;
    } else {
      statusResult = ApplicationConstant.NOT_ON_THE_LIST;
    }
    return statusResult;
  }
  private SyncOpRiskAssetResponse callApiOpRisk(String identifierCode, String collateralType) {
    // Build authentication information
    AuthenInfo authenInfo = AuthenInfo.builder()
        .reqTime(new Date(System.currentTimeMillis()))
        .authorizer(opriskPersonConfig.getAsset().getAuthorizerOprAsset())
        .password(opriskPersonConfig.getAsset().getPasswordOprAsset())
        .srv(opriskPersonConfig.getAsset().getSrvLegalOprAsset())
        .reqId(UUID.randomUUID().toString())
        .reqApp(opriskPersonConfig.getAsset().getRegAppOprAsset())
        .build();
    // Build data details
    CheckBlackListPLegalData dataCheckBlackListPLegalRequest = CheckBlackListPLegalData.builder()
        .authenInfo(authenInfo)
        .identifiesInfo(identifierCode)
        .collateralType(collateralType)
        .build();
    // Build request
    CheckBlackListPLegalRequest checkBlackListPLegalRequest = CheckBlackListPLegalRequest.builder()
        .checkBlackListC(dataCheckBlackListPLegalRequest)
        .build();

    return opRiskLegacyClient.syncOpriskLegal(checkBlackListPLegalRequest);
  }

  private void removeOpRiskAssetPostSync(List<AmlOprEntity> savedEntities) {
    log.info("START PostSyncOpRiskServiceImpl.removeOpRiskAssetPostSync with bpm id: [{}]",
            JsonUtil.convertObject2String(savedEntities.get(0).getApplication().getBpmId(), objectMapper));
    List<Long> savedEntityIds = savedEntities.stream().map(AmlOprEntity::getId).collect(Collectors.toList());
    Long applicationId = savedEntities.get(0).getApplication().getId();
    List<AmlOprEntity> currentEntities = opRiskRepository.findByApplicationIdAndQueryType(applicationId, ApplicationConstant.OPR_ASSET_TAG).orElse(new ArrayList<>());
    if (!CollectionUtils.isEmpty(currentEntities)) {
      List<AmlOprEntity> deletedEntities = currentEntities.stream().filter(entity -> !savedEntityIds.contains(entity.getId())).collect(Collectors.toList());
      log.info("List of op_risk entities need removing: {}", JsonUtil.convertObject2String(deletedEntities.stream().map(AmlOprEntity::getId).collect(Collectors.toList()), objectMapper));
      opRiskRepository.deleteAll(deletedEntities);
    }
    log.info("END PostSyncOpRiskServiceImpl.removeOpRiskAssetPostSync with bpm id: [{}]",
            JsonUtil.convertObject2String(savedEntities.get(0).getApplication().getBpmId(), objectMapper));
  }
}
