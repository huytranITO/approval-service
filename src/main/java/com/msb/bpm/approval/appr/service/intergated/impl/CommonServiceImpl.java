package com.msb.bpm.approval.appr.service.intergated.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_ID;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.EB_ADDRESS_TYPE_V001;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RB_ADDRESS_TYPE_V001;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RB_ADDRESS_TYPE_V002;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.util.Util.categoryCodes;
import static com.msb.bpm.approval.appr.util.Util.getNamePlaceByCodeFromCacheMercury;

import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.config.RelationshipTypeConfig;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.application.AddressType;
import com.msb.bpm.approval.appr.enums.application.CustomerExtType;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.common.SourceApplication;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.response.cic.AmlOprDataResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.cache.MercuryConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.RelationshipMappingUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

  private final ApplicationRepository applicationRepository;
  private final ApplicationDraftRepository applicationDraftRepository;
  private final UserManagerClient userManagerClient;

  private final ConfigurationServiceCache configurationServiceCache;
  Map<String, List<GetListResponse.Detail>> categoryMap;
  private final MercuryConfigurationServiceCache mercuryCache;
  private final RelationshipTypeConfig relationshipTypeConfig;

  @Override
  public void saveAmlOprInfo(ApplicationEntity entityApp,
      List<AmlOprDataResponse> lstAmlOprData) {
    log.info("saveAmlOprInfo START with entityApp={} lstAmlOprData={}", entityApp, lstAmlOprData);
    // Save to DB
    Set<AmlOprEntity> lstAmlOpr = entityApp.getAmlOprs();
    String type = null;
    for (AmlOprDataResponse item : lstAmlOprData) {
      AmlOprEntity amlOprEntity;
      // check if exist then update, others is insert
      Optional<AmlOprEntity> amlOprEntityOptional = lstAmlOpr.stream().filter( e ->
         Objects.equals(item.getIdentifierCode(), e.getIdentifierCode()) &&
             Objects.equals(item.getQueryType(), e.getQueryType())).findFirst();
      if (amlOprEntityOptional.isPresent()) {
        amlOprEntity = amlOprEntityOptional.get();
        amlOprEntity.setUpdatedAt(LocalDateTime.now());
      } else {
        amlOprEntity = new AmlOprEntity().withApplication(entityApp);
        lstAmlOpr.add(amlOprEntity);
      }
      amlOprEntity.setCustomerId(item.getCustomerId());
      amlOprEntity.setIdentifierCode(item.getIdentifierCode());
      amlOprEntity.setQueryType(item.getQueryType());
      amlOprEntity.setResultCode(item.getResultCode());
      amlOprEntity.setResultDescription(item.getResultDescription());
      amlOprEntity.setSubject(item.getSubject());
      amlOprEntity.setResultOnBlackList(item.getReasonInList());
      amlOprEntity.setStartDate(item.getStartDate());
      amlOprEntity.setEndDate(item.getEndDate());
      amlOprEntity.setPriority(item.isPriority());
      amlOprEntity.setRefCustomerId(item.getRefCustomerId());
      if (null == type) {
        type = item.getQueryType();
      }
    }
    final String finalType = type;
    List<String> lstNewId = lstAmlOprData.stream().filter(e -> Objects.equals(
            finalType, e.getQueryType())).map(AmlOprDataResponse::getIdentifierCode)
        .collect(Collectors.toList());

    lstAmlOpr.removeAll(lstAmlOpr.stream().filter(e -> Objects.equals(e.getQueryType(), finalType) && !lstNewId.contains(e.getIdentifierCode())).collect(Collectors.toList()));

    log.info("saveAmlOprInfo END with entityApp={} lstAmlOprData={}", entityApp, lstAmlOprData);
  }

  public ApplicationEntity findAppByBpmId(String bpmId) {
    return applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));
  }

  @Override
  public String getCustomerType(Long customerId, String customerType, Long cusId) {
    if (CustomerType.EB.getTypeNumber().equals(customerType)){
      return CustomerExtType.ENTERPRISE.getValue();
    }
    if (Objects.isNull(customerId)) {
      return CustomerExtType.CUSTOMER_RELATIONSHIP.getValue();
    }
    return customerId.equals(cusId) ? CustomerExtType.CUSTOMER.getValue()
        : CustomerExtType.CUSTOMER_RELATIONSHIP.getValue();
  }

  @Override
  public ApplicationEntity getAppEntity(ConcurrentMap<String, Object> metadata) {
    return Objects.nonNull(metadata.get(APPLICATION_BPM_ID))
        ? findAppByBpmId((String) metadata.get(APPLICATION_BPM_ID))
        : (ApplicationEntity) metadata.get(APPLICATION);
  }

  @Override
  public String genBpmId() {
    Long maxIdApp = applicationRepository.getMaxId();
    String maxIdAppStr = String.valueOf((null == maxIdApp ? 0 : maxIdApp) + 1);
    String requestCode = StringUtils.leftPad(maxIdAppStr, 8, '0');
    return String.format(Util.BPM_ID_FORMAT, requestCode);
  }

  @Override
  public void saveDraft(String bpmId) {
    List<ApplicationDraftEntity> lstAppDraft = new ArrayList<>();
    Util.LIST_TABS.forEach(item -> {
      ApplicationDraftEntity entity = new ApplicationDraftEntity().withBpmId(bpmId)
          .withStatus(0)
          .withTabCode(item);
      lstAppDraft.add(entity);

    });
    applicationDraftRepository.saveAllAndFlush(lstAppDraft);
  }

  public GetUserProfileResponse getUserDetail(String username) {
    if (StringUtils.isBlank(username)) {
      return null;
    }

    try {
      return userManagerClient.getUserByUsername(username);
    } catch (Exception e) {
      log.error("Get user by username {} failure : ", username, e);
      return null;
    }
  }

  public String getAddressTypeValue(String customerType, String addressType) {

    categoryMap = configurationServiceCache.getCategoryDataByCodes(categoryCodes);

    String result = "";
    try {
      if (ApplicationConstant.Customer.EB.equalsIgnoreCase(customerType)) {
        result = configurationServiceCache.getCategoryData(categoryMap, EB_ADDRESS_TYPE_V001)
                .get(addressType);
      }
      if (ApplicationConstant.Customer.RB.equalsIgnoreCase(customerType)) {
        if (AddressType.HK_THUONG_TRU.getValue().equalsIgnoreCase(addressType)) {
          result = configurationServiceCache.getCategoryData(categoryMap, RB_ADDRESS_TYPE_V001)
                  .get(addressType);
        }
        if (AddressType.DIA_CHI_TSC.getValue().equalsIgnoreCase(addressType)) {
          result = configurationServiceCache.getCategoryData(categoryMap, RB_ADDRESS_TYPE_V002)
                  .get(addressType);
        }
      }
    } catch (Exception ex) {
      log.error("getAddressTypeValue End with error: {}", ex.getMessage());
    }
    return result;
  }

  public String getDistrictValue(String cityCode, String districtCode) {
    MercuryDataResponse district = null;
    if (StringUtils.isNotBlank(cityCode)) {
      district = mercuryCache.searchPlace(cityCode);
    }
    return getNamePlaceByCodeFromCacheMercury(district, districtCode);
  }

  public String getWardValue(String districtCode, String wardCode) {
    MercuryDataResponse ward = null;
    if (StringUtils.isNotBlank(districtCode)) {
      ward = mercuryCache.searchPlace(districtCode);
    }
    return getNamePlaceByCodeFromCacheMercury(ward, wardCode);
  }

  @Override
  public String convertRelationshipTypeSourceToBPM(String sourceApplication,
      String relationshipType) {
    if (relationshipTypeConfig.isEnable()) {
      if (SourceApplication.CJBO.name().equalsIgnoreCase(sourceApplication)) {
        String relationshipNew = RelationshipMappingUtil.CJBO_MAPPING_BPM.get(relationshipType);
        return Objects.nonNull(relationshipNew) ? relationshipNew : relationshipType;
      }
      if (SourceApplication.CJMHOME.name().equalsIgnoreCase(sourceApplication)) {
        String relationshipNew = RelationshipMappingUtil.CJ5_MAPPING_BPM.get(relationshipType);
        return Objects.nonNull(relationshipNew) ? relationshipNew : relationshipType;
      }
    }
    return relationshipType;
  }

  @Override
  public String convertRelationshipTypeBPMToSource(String sourceApplication,
      String relationshipType) {
    if (relationshipTypeConfig.isEnable()) {
      if (SourceApplication.CJBO.name().equalsIgnoreCase(sourceApplication)) {
        String relationshipNew = RelationshipMappingUtil.BPM_MAPPING_CJBO.get(relationshipType);
        return Objects.nonNull(relationshipNew) ? relationshipNew : relationshipType;
      }
      if (SourceApplication.CJMHOME.name().equalsIgnoreCase(sourceApplication)
          || SourceApplication.BPM.name()
          .equalsIgnoreCase(sourceApplication)) {
        String relationshipNew = RelationshipMappingUtil.BPM_MAPPING_CJ5.get(relationshipType);
        return Objects.nonNull(relationshipNew) ? relationshipNew : relationshipType;
      }
    }
    return relationshipType;
  }
}
