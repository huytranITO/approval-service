package com.msb.bpm.approval.appr.service.cms.impl;

import static com.msb.bpm.approval.appr.config.LandingPageConfig.BPM_SOURCE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.checklist.ChecklistClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.config.properties.MinioProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.cms.BucketType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.OtherIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.PropertyBusinessIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RentalIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest.Document;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.checklist.SearchResponse;
import com.msb.bpm.approval.appr.model.response.checklist.SearchResponse.Content;
import com.msb.bpm.approval.appr.model.response.collateral.AssetRuleChecklistResponse;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.minio.MinIOService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CmsBaseIntegrationService {
  private final MinioProperties minioProperties;
  private final ObjectMapper objectMapper;
  private final MinIOService minIOService;
  private static final String FILE_SIZE_DEFAULT = "0.323323";
  private final CustomerRepository customerRepository;
  private final CollateralClient collateralClient;
  private final ChecklistClient checklistClient;

  public String getSizeFileFromMap(String filePath, Map<String, Object> mapSizeFile) {
    try {

      String minIoPathFile = filePath
          .replace(minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue()).getBucket() + ApplicationConstant.SEPARATOR, "");
      if (mapSizeFile.containsKey(minIoPathFile)) {
        Long size = Long.valueOf(mapSizeFile.get(minIoPathFile).toString());
        return convertSizeFile(size);
      }
    } catch (Exception ex) {
      log.error("CmsBaseIntegrationService getSizeFileFromMap with filePath={}, Error: ", filePath, ex);
    }
    return FILE_SIZE_DEFAULT;
  }

  public String convertSizeFile(Long size) {
    return String.valueOf((float)size/1024/1024);
  }

  public Map<String, Object> getSizeFileAll(String bpmId, List<Document> documents) {
    log.info("CmsBaseIntegrationService getSizeFileAll method START with bpmId={}, documents={}", bpmId, JsonUtil.convertObject2String(documents, objectMapper));
    Map<String, Object> response = new HashMap<>();
    if (CollectionUtils.isNotEmpty(documents)) {
      Iterator<Document> it = documents.iterator();
      while (it.hasNext()) {
        Document document = it.next();
        if (CollectionUtils.isNotEmpty(document.getFiles())) {
          String pathFile = document.getFiles().get(0).getMinioPath();
          Path path = Paths.get(pathFile);
          log.info("getSizeFileAll with bpmId={}, path={}", bpmId, JsonUtil.convertObject2String(path, objectMapper));
          if (!path.getParent().toString().endsWith(bpmId)) {
            log.error("getSizeFileAll with bpmId={}, path={} INVALID", bpmId, path.getParent());
            throw new ApprovalException(DomainCode.INVALID_PATH_FILES_MINIO, new Object[]{pathFile});
          }
          String pathFileMinIO = path.getParent().toString().replace("\\", ApplicationConstant.SEPARATOR);
          log.info("getSizeFileAll with bpmId={}, pathFileMinIO={}", bpmId, pathFileMinIO);
          String bucketName = minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue()).getBucket();
          if (!pathFileMinIO.startsWith(bucketName + ApplicationConstant.SEPARATOR)) {
            log.error("getSizeFileAll with bpmId={}, pathFileMinIO={} INVALID", bpmId, pathFileMinIO);
            throw new ApprovalException(DomainCode.INVALID_PATH_FILES_MINIO, new Object[]{pathFile});
          }
          pathFileMinIO = pathFileMinIO.replace(bucketName + ApplicationConstant.SEPARATOR, "") + ApplicationConstant.SEPARATOR;
          response.putAll(minIOService.getSizeFileMinIOByPathFile(pathFileMinIO));
        }
      }
    }

   /* if(CollectionUtils.isNotEmpty(documents) && CollectionUtils.isNotEmpty(documents.get(0).getFiles())) {
      String pathFile = documents.get(0).getFiles().get(0).getMinioPath();
      Path path = Paths.get(pathFile);
      log.info("getSizeFileAll with bpmId={}, path={}", bpmId, JsonUtil.convertObject2String(path, objectMapper));
      if (!path.getParent().toString().endsWith(bpmId)) {
        log.error("getSizeFileAll with bpmId={}, path={} INVALID", bpmId, path.getParent());
        throw new ApprovalException(DomainCode.INVALID_PATH_FILES_MINIO, new Object[]{pathFile});
      }
      String pathFileMinIO = path.getParent().toString().replace("\\", ApplicationConstant.SEPARATOR);
      log.info("getSizeFileAll with bpmId={}, pathFileMinIO={}", bpmId, pathFileMinIO);
      String bucketName = minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue()).getBucket();
      if (!pathFileMinIO.startsWith(bucketName + ApplicationConstant.SEPARATOR)) {
        log.error("getSizeFileAll with bpmId={}, pathFileMinIO={} INVALID", bpmId, pathFileMinIO);
        throw new ApprovalException(DomainCode.INVALID_PATH_FILES_MINIO, new Object[]{pathFile});
      }
      pathFileMinIO = pathFileMinIO.replace(bucketName + ApplicationConstant.SEPARATOR, "") + ApplicationConstant.SEPARATOR;
      response = minIOService.getSizeFileMinIOByPathFile(pathFileMinIO);
    }*/

    log.info("CmsBaseIntegrationService getSizeFileAll END with bpmId={}, documents={}, result={}", bpmId, JsonUtil.convertObject2String(documents, objectMapper), response);
    return response;
  }


  public List<String> checkIfNotExistFile(List<Document> documents, Map<String, Object> mapSizeFile, String bpmId) {
    log.info("CmsBaseIntegrationService getSizeFileAll START with bpmId={}, documents={}, mapSizeFile={}", bpmId, JsonUtil.convertObject2String(documents, objectMapper), mapSizeFile);
    List<String> lstFileNotExist = new ArrayList<>();
    for (Document document : documents) {
      if (document.getFiles()!= null &&  document.getFiles().size() > 0 ) {
        document.getFiles().stream().forEach(file -> {
          if (!mapSizeFile.containsKey(file.getMinioPath()
              .replace(minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue()).getBucket() + ApplicationConstant.SEPARATOR, ""))) {
            lstFileNotExist.add(file.getMinioPath());
          }
        });
      }
    }
    log.info("CmsBaseIntegrationService getSizeFileAll END with bpmId={}, documents={}, mapSizeFile={}, lstFileNotExist={}", bpmId, JsonUtil.convertObject2String(documents, objectMapper), mapSizeFile, lstFileNotExist);
    return lstFileNotExist;
  }


  public boolean setAdditionIdAndChecklistMappingId(ChecklistBaseResponse<GroupChecklistDto> checklistBaseResponse, ChecklistDto checklistDto, String cmsDomainReferenceId, ApplicationEntity entityApp) {
    log.info("setAdditionIdAndChecklistMappingId START={}, " + "checklistDto={}", JsonUtil.convertObject2String(checklistBaseResponse, objectMapper), JsonUtil.convertObject2String(checklistDto, objectMapper));
    AtomicReference<Boolean> status = new AtomicReference<>(false);

    SearchResponse response = checklistClient.search();
    if (response ==null) return false;

    List<String> resCodeList = response.getData().getContent().stream().map(Content::getCode).collect(Collectors.toList());

    if (Objects.nonNull(checklistBaseResponse) && CollectionUtils.isNotEmpty(resCodeList)) {

      List<ChecklistDto> checklistResp = checklistBaseResponse.getData().getListChecklist().stream().filter(
          e -> checklistDto.getCode().equalsIgnoreCase(e.getCode()) && checklistDto.getGroupCode().equalsIgnoreCase(e.getGroupCode())).collect(Collectors.toList());

      Iterator<ChecklistDto> it = checklistResp.iterator();
      while (it.hasNext()) {
        ChecklistDto checklist = it.next();

        checklistDto.setAdditionalDataChecklistId(checklist.getAdditionalDataChecklistId());
        checklistDto.setChecklistMappingId(checklist.getChecklistMappingId());
        checklistDto.setDomainType(checklist.getDomainType());

        // customer
        if (entityApp.getCustomer() != null
            && (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equalsIgnoreCase(entityApp.getCustomer().getRefCusId()))
            && checklist.getDomainObjectId().equals(entityApp.getCustomer().getRefCustomerId())
            && checklistDto.getCode().equals(checklist.getCode())
            && resCodeList.contains(checklistDto.getCode())) {
          status.set(true);
        }

        // loan + approval + bank + other (id_cms)
        if (entityApp != null
            && (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equals(entityApp.getRefId()))
            && entityApp.getId().equals(checklist.getDomainObjectId())
            && resCodeList.contains(checklistDto.getCode())) {
          status.set(true);
        }

        // relationship
        if (CollectionUtils.isNotEmpty(entityApp.getCustomer().getCustomerRelationShips())) {
          List<Long> ids =  entityApp.getCustomer().getCustomerRelationShips().stream().filter(relationItem -> Objects.nonNull(relationItem.getRelationship())).map(CustomerRelationShipEntity::getCustomerRefId).collect(Collectors.toList());
          if (CollectionUtils.isNotEmpty(ids)) {
            Optional<Set<CustomerEntity>> customerSet = customerRepository.findByIdIn(new HashSet<>(ids));
            if (customerSet.isPresent()) {
              customerSet.get().forEach(customerItem -> {
                if (customerItem.getCustomerType().equals("RB")
                    && (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equalsIgnoreCase(customerItem.getRefCusId()))
                    && customerItem.getRefCustomerId().equals(checklist.getDomainObjectId())
                    && resCodeList.contains(checklistDto.getCode())) {
                  status.set(true);
                }
              });
            }
          }
        }

        // income
        if (CollectionUtils.isNotEmpty(entityApp.getIncomes())
            && resCodeList.contains(checklistDto.getCode())
            && checklistDto.getCode().equals(checklist.getCode())) {

          entityApp.getIncomes().forEach(incomeItem -> {
            //salary income
            if (CollectionUtils.isNotEmpty(incomeItem.getSalaryIncomes())) {
              Optional<SalaryIncomeEntity> salaryIncomeEntity = incomeItem.getSalaryIncomes().stream().filter(salaryItem ->
                  (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equalsIgnoreCase(salaryItem.getLdpSalaryId()))
                      && salaryItem.getId().equals(checklist.getDomainObjectId())).findFirst();
              if (salaryIncomeEntity.isPresent()) {
                status.set(true);
              }
            }

            // retail income
            if (CollectionUtils.isNotEmpty(incomeItem.getRentalIncomes())) {
              Optional<RentalIncomeEntity> rentalIncomeEntity = incomeItem.getRentalIncomes().stream().filter(retailItem ->
                  (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equalsIgnoreCase(retailItem.getLdpRentalId()))
                      && retailItem.getId().equals(checklist.getDomainObjectId())).findFirst();
              if (rentalIncomeEntity.isPresent()) {
                status.set(true);
              }
            }

            // individual_enterprise_income
            if (CollectionUtils.isNotEmpty(incomeItem.getIndividualEnterpriseIncomes())) {
              Optional<IndividualEnterpriseIncomeEntity> enterpriseIncomeEntity = incomeItem.getIndividualEnterpriseIncomes().stream().filter(enterpriseItem ->
                  (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equalsIgnoreCase(enterpriseItem.getLdpBusinessId()))
                      && enterpriseItem.getId().equals(checklist.getDomainObjectId())).findFirst();
              if (enterpriseIncomeEntity.isPresent()) {
                status.set(true);
              }
            }

            // other_income
            if (CollectionUtils.isNotEmpty(incomeItem.getOtherIncomes())) {
              log.info("Step check other_income start: ");
              Optional<OtherIncomeEntity> otherIncomeEntity = incomeItem.getOtherIncomes().stream().filter(otherItem ->
                  (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equalsIgnoreCase(otherItem.getLdpOtherId()))
                      && otherItem.getId().equals(checklist.getDomainObjectId())).findFirst();
              if (otherIncomeEntity.isPresent()) {
                status.set(true);
              }
            }

            // property_business_income
            if (CollectionUtils.isNotEmpty(incomeItem.getPropertyBusinessIncomes())) {
              Optional<PropertyBusinessIncomeEntity> businessIncomeEntity = incomeItem.getPropertyBusinessIncomes().stream().filter(businessItem ->
                  (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equalsIgnoreCase(businessItem.getLdpPropertyBusinessId()))
                      && businessItem.getId().equals(checklist.getDomainObjectId())).findFirst();
              if (businessIncomeEntity.isPresent()) {
                status.set(true);
              }
            }
          });
        }

        // collateral_asset
        List<AssetRuleChecklistResponse> assetRuleItems = collateralClient.getAssetRuleChecklist(entityApp.getBpmId());
        if(ObjectUtils.isNotEmpty(assetRuleItems)  && resCodeList.contains(checklistDto.getCode())){
          Optional<AssetRuleChecklistResponse> assetIt = assetRuleItems.stream().filter(assetRule ->
              ObjectUtils.isNotEmpty(assetRule.getObjectId())
                  &&  (BPM_SOURCE.equals(entityApp.getSource()) || cmsDomainReferenceId.equalsIgnoreCase(String.valueOf(assetRule.getObjectId())))
                  && assetRule.getAssetId().equals(checklist.getDomainObjectId())).findFirst();
          if (assetIt.isPresent()) {
            status.set(true);
          }
        }

        if (status.get() == true) {
          checklistDto.setDomainObjectId(checklist.getDomainObjectId());
          checklistDto.setListFile(checklist.getListFile());
          return status.get();
        }
      }
    }
    return status.get();
  }

}
