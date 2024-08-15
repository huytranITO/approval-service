package com.msb.bpm.approval.appr.service.cms.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.CMS_CREATE_APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.CMS_PUSH_DOCUMENTS;
import static com.msb.bpm.approval.appr.enums.application.GuaranteeForm.COLLATERAL;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.MAKE_PROPOSAL;
import static com.msb.bpm.approval.appr.enums.application.SubmissionPurpose.NEW_LEVEL;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.AUTO_DEDUCT_RATE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CARD_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CREDIT_FORM;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CREDIT_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.DOCUMENT_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.EB_ADDRESS_TYPE_V001;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.GENDER;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.GUARANTEE_FORM;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.ISSUE_BY;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.LOAN_PURPOSE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.MARTIAL_STATUS;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RB_ADDRESS_TYPE_V001;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RB_ADDRESS_TYPE_V002;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.camunda.CamundaClient;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.UserManagementClient;
import com.msb.bpm.approval.appr.config.properties.MinioProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant;
import com.msb.bpm.approval.appr.enums.application.AddressType;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.DistributionForm;
import com.msb.bpm.approval.appr.enums.application.GeneratorStatusEnums;
import com.msb.bpm.approval.appr.enums.checklist.BusinessFlow;
import com.msb.bpm.approval.appr.enums.cms.BucketType;
import com.msb.bpm.approval.appr.enums.cms.StatusAppType;
import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.factory.BaseServiceFactory;
import com.msb.bpm.approval.appr.mapper.ApplicationFieldInformationMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationFieldInformationDTO;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.dto.cms.CustomerCmsAddressDTO;
import com.msb.bpm.approval.appr.model.dto.cms.CustomerContactDTO;
import com.msb.bpm.approval.appr.model.dto.cms.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.CustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.cms.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredLoanDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredOverdraftDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditOverdraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerContactEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RuleVersionMappingEntity;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsCreateApplicationRequest.ApplicationCredit;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsDocumentsRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsDocumentsRequest.Document;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest;
import com.msb.bpm.approval.appr.model.request.customer.Address;
import com.msb.bpm.approval.appr.model.request.customer.CommonCustomerRequest;
import com.msb.bpm.approval.appr.model.request.customer.Customer;
import com.msb.bpm.approval.appr.model.request.data.PostFieldInformationRequest;
import com.msb.bpm.approval.appr.model.request.organization.GetOrganizationRequest;
import com.msb.bpm.approval.appr.model.request.organization.GetOrganizationRequest.Filter;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.cms.CmsBaseResponse;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.model.response.customer.CreateRBCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.IdentityDocumentResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.UpdateRBCustomerResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetOrganizationResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.OrganizationTreeDetail;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea.DataResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.RuleVersionMappingRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.application.impl.PostCreateApplicationServiceImpl;
import com.msb.bpm.approval.appr.service.application.impl.PostFieldInformationServiceImpl;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.cache.MercuryConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.checklist.impl.ChecklistServiceImpl;
import com.msb.bpm.approval.appr.service.cms.CmsIntegrationService;
import com.msb.bpm.approval.appr.service.idgenerate.IDSequenceService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.minio.MinIOService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CmsIntegrationServiceImpl extends AbstractBaseService implements
    CmsIntegrationService {

  private final ApplicationRepository applicationRepository;
  private final MessageSource messageSource;
  private final ChecklistServiceImpl checklistService;
  private final CommonService commonService;
  private final MinioProperties minioProperties;
  private final CamundaClient camundaClient;
  private final ObjectMapper objectMapper;
  private final RuleVersionMappingRepository ruleVersionMappingRepository;
  private final MinIOService minIOService;
  private final CustomerClient customerClient;
  private final PostFieldInformationServiceImpl postFieldInformationService;
  private final ConfigurationServiceCache serviceCache;
  private final MercuryConfigurationServiceCache mercuryCache;
  private static final String FILE_SIZE_DEFAULT = "0.323323";
  private final UserManagementClient userManagementClient;
  private final BaseServiceFactory baseServiceFactory;
  private final UserManagerClient userManagerClient;
  private final PostCreateApplicationServiceImpl postCreateApplicationService;
  private final IDSequenceService idSequenceService;

  @Transactional
  @Override
  public Object postChecklistByBpmApplicationId(PostCmsDocumentsRequest request, String bpmApplicationId) {
    log.info("postChecklistByBpmApplicationId START with request={}, bpmApplicationId={}", JsonUtil.convertObject2String(request, objectMapper), bpmApplicationId);
    ApplicationEntity entityApp = commonService.findAppByBpmId(bpmApplicationId);
    // Xử lý call save checklist
    Map<String, Object> mapSizeFile = getSizeFileAll(entityApp.getBpmId(), request.getDocuments());
    List<String> lstFileNotExist = checkIfNotExistFile(request.getDocuments(), mapSizeFile, entityApp.getBpmId());
    if(CollectionUtils.isNotEmpty(lstFileNotExist)) {
        throw new ApprovalException(DomainCode.NOT_EXIST_FILES_IN_MINIO, new Object[]{lstFileNotExist});
    }
    saveChecklist(entityApp, request, mapSizeFile);
    boolean isStartCamunda = false;
    if (Objects.isNull(entityApp.getProcessInstance())
        || (StringUtils.isEmpty(entityApp.getProcessInstance().getProcessInstanceId()))) {
      isStartCamunda = true;
    } else {
      try {
        ProcessInstanceDto processInstance = camundaClient.getProcessInstanceById(
            entityApp.getProcessInstance().getProcessInstanceId());
        log.info("processInstance={}", processInstance);
      } catch (Exception ex) {
        log.error("Not found Process Instance with bpmApplicationId={}, Error: ", bpmApplicationId,
            ex);
        isStartCamunda = true;
      }
    }
    log.info("isStartCamunda={}", isStartCamunda);
    // Check Status Application & process_instance_id
    if (isStartCamunda) {
      // Camunda start instance workflow
      log.info("START CAMUNDA with isStartCamunda={}", isStartCamunda);

      postCreateApplicationService.startCamundaInstance(entityApp);

      applicationRepository.saveAndFlush(entityApp);
    }

    log.info("postChecklistByBpmApplicationId END with request={}, bpmApplicationId={}",
        JsonUtil.convertObject2String(request, objectMapper), bpmApplicationId);
    return CmsBaseResponse.builder().bpmId(bpmApplicationId)
        .status(entityApp.getStatus()).build();
  }

  @Override
  public CmsBaseResponse v2PostCreateApplication(PostCmsV2CreateApplicationRequest request) {
    return (CmsBaseResponse) baseServiceFactory.getBaseRequest(CMS_CREATE_APPLICATION).execute(request);
  }

  @Override
  public CmsBaseResponse v2PostDocuments(PostCmsV2DocumentsRequest request, String bpmApplicationId) {
    return (CmsBaseResponse) baseServiceFactory.getBaseRequest(CMS_PUSH_DOCUMENTS).execute(request, bpmApplicationId);
  }

  @Override
  public Object findOrganization() {
    log.info("Start findOrganization:");
    GetOrganizationResponse response = null;
    try {

      Filter filter = Filter.builder()
          .page(0)
          .pageSize(1000)
          .build();

      GetOrganizationRequest request = GetOrganizationRequest.builder()
          .specializedBank("RB")
          .name("MSB")
          .type("DVKD")
          .filter(filter)
          .build();

      response = userManagerClient.findOrganization(request);
      if (response == null || response.getData() == null) return null;

      log.info("findOrganization with data: {} ", response.getData());
      return response.getData().getOrganizations().stream()
          .filter(item -> !item.getCode().contains("DS")
              && !item.getCode().contains("LH")
              && !item.getCode().contains("T")
              && !item.getCode().contains("L")
              && !item.getCode().contains("SSE")
          )
          .collect(Collectors.toList());

    } catch (Exception e) {
      throw new ApprovalException(DomainCode.FIND_ORGANIZATION_ERROR, new Object[]{e.getMessage()});
    }
  }

  private List<String> checkIfNotExistFile(List<Document> documents,
      Map<String, Object> mapSizeFile,
      String bpmId) {
    log.info("getSizeFileAll START with bpmId={}, documents={}, mapSizeFile={}", bpmId,
        JsonUtil.convertObject2String(documents, objectMapper), mapSizeFile);
    List<String> lstFileNotExist = new ArrayList<>();
    for (Document document : documents) {
      document.getFiles().stream().forEach(file -> {
        if (!mapSizeFile.containsKey(file.getMinioPath()
            .replace(minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue())
                .getBucket() + ApplicationConstant.SEPARATOR, ""))) {
          lstFileNotExist.add(file.getMinioPath());
        }
      });
    }
    log.info("getSizeFileAll END with bpmId={}, documents={}, mapSizeFile={}, lstFileNotExist={}",
        bpmId, JsonUtil.convertObject2String(documents, objectMapper), mapSizeFile,
        lstFileNotExist);
    return lstFileNotExist;
  }

  private Map<String, Object> getSizeFileAll(String bpmId, List<Document> documents) {
    log.info("getSizeFileAll START with bpmId={}, documents={}", bpmId,
        JsonUtil.convertObject2String(documents, objectMapper));
    Map<String, Object> result = new HashMap<>();
    if(CollectionUtils.isNotEmpty(documents) &&
        CollectionUtils.isNotEmpty(documents.get(0).getFiles())) {
      String pathFile = documents.get(0).getFiles().get(0).getMinioPath();
      Path path = Paths.get(pathFile);
      log.info("getSizeFileAll with bpmId={}, path={}", bpmId,
          JsonUtil.convertObject2String(path, objectMapper));
      if (!path.getParent().toString().endsWith(bpmId)) {
        log.error("getSizeFileAll with bpmId={}, path={} INVALID", bpmId, path.getParent());
        throw new ApprovalException(DomainCode.INVALID_PATH_FILES_MINIO, new Object[]{pathFile});
      }
      String pathFileMinIO = path.getParent().toString()
          .replace("\\", ApplicationConstant.SEPARATOR);
      log.info("getSizeFileAll with bpmId={}, pathFileMinIO={}", bpmId, pathFileMinIO);
      String bucketName = minioProperties.getMinio().getBucketExt().get(
          BucketType.CMS.getValue()).getBucket();
      String prefix = minioProperties.getMinio().getBucketExt().get(
          BucketType.CMS.getValue()).getPrefix();
      if (!pathFileMinIO.startsWith(bucketName + ApplicationConstant.SEPARATOR + prefix)) {
        log.error("getSizeFileAll with bpmId={}, pathFileMinIO={} INVALID", bpmId, pathFileMinIO);
        throw new ApprovalException(DomainCode.INVALID_PATH_FILES_MINIO, new Object[]{pathFile});
      }
      pathFileMinIO = pathFileMinIO.replace(bucketName +
          ApplicationConstant.SEPARATOR, "") + ApplicationConstant.SEPARATOR;
      result = minIOService.getSizeFileMinIOByPathFile(pathFileMinIO);
    }
    log.info("getSizeFileAll END with bpmId={}, documents={}, result={}", bpmId,
        JsonUtil.convertObject2String(documents, objectMapper), result);
    return result;
  }

  private void saveChecklist(ApplicationEntity entityApp, PostCmsDocumentsRequest request,
      Map<String, Object> mapSizeFile) {
    log.info("saveChecklist START with bpmId={}, request={}, mapSizeFile={}", entityApp.getBpmId(),
        JsonUtil.convertObject2String(request, objectMapper), mapSizeFile);
    try {
      List<RuleVersionMappingEntity> ruleVersionMapping = ruleVersionMappingRepository
          .findByApplicationIdAndRuleCode(entityApp.getId(), RuleCode.R001)
          .orElse(null);
      List<ChecklistDto> listChecklistDto = new ArrayList<>();
      ChecklistBaseResponse<GroupChecklistDto> checklistBaseResponse = checklistService.getChecklistByRequestCode(entityApp.getBpmId());
      log.info("saveChecklist START with bpmId={}, checklistBaseResponse={}",
          entityApp.getBpmId(),  JsonUtil.convertObject2String(checklistBaseResponse, objectMapper));
      for (Document item : request.getDocuments()) {
        if(StringUtils.isNotBlank(item.getCmsDomainReferenceId()) &&
        !item.getCmsDomainReferenceId().equals(entityApp.getCustomer().getRefCusId()))
        {
          throw new ApprovalException(DomainCode.NOT_FOUND_CHECKLIST_ERROR, new Object[]{request});
        }
        Long ruleVersion = null;
        if (CollectionUtils.isNotEmpty(ruleVersionMapping)) {
          RuleVersionMappingEntity ruleVersionMappingEntity = ruleVersionMapping.stream().filter(e ->
              item.getGroup().equals(e.getGroupCode())).findFirst().orElse(null);
          ruleVersion = Objects.nonNull(ruleVersionMappingEntity) && Objects.nonNull(
              ruleVersionMappingEntity.getRuleVersion()) ?
              Long.valueOf(ruleVersionMappingEntity.getRuleVersion()) : null;
        }
        ChecklistDto checklistDto = ChecklistDto.builder()
            .code(item.getDocCode())
            .name(item.getDocName())
            .groupCode(item.getGroup())
            .isError(false)
            .isRequired(true)
            .isGenerated(true)
            .ruleVersion(ruleVersion)
            .build();
        List<FileDto> listFile = new ArrayList<>();
        item.getFiles().stream().forEach(file ->  {
          String minIOPath = file.getMinioPath()
              .replace(minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue())
                  .getBucket() + ApplicationConstant.SEPARATOR, "");
          minIOService.copyBucketByFilePathMinIO(minIOPath,
              minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue()).getPrefix(),
              minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue()).getBucket());
          listFile.add(new FileDto()
              .withBucket(minioProperties.getMinio().getBucket())
              .withMinioPath(minIOPath)
              .withFileName(file.getFileName())
              .withFileType(Util.getExtensionFile(file.getFileName()))
              .withFileSize(getSizeFileFromMap(file.getMinioPath(), mapSizeFile))
          );
        });
        checklistDto.setListFile(listFile);
        setAdditionIdAndChecklistMappingId(checklistBaseResponse, checklistDto);
        log.info("saveChecklist  with bpmId={}, checklistDto={}", entityApp.getBpmId(),
            JsonUtil.convertObject2String(checklistDto, objectMapper));
        if (Objects.nonNull(checklistDto.getChecklistMappingId()) &&
            Objects.nonNull(checklistDto.getAdditionalDataChecklistId())) {
          listChecklistDto.add(checklistDto);
        }
      }
      log.info("saveChecklist START with bpmId={}, listChecklistDto={}",
          entityApp.getBpmId(), JsonUtil.convertObject2String(listChecklistDto, objectMapper));
      CreateChecklistRequest createChecklistRequest =
          CreateChecklistRequest.builder()
              .requestCode(entityApp.getBpmId())
              .businessFlow(BusinessFlow.BO_USL_NRM.name())
              .phaseCode(PhaseCode.NRM_ALL_S.name())
              .listChecklist(listChecklistDto)
              .build();
      Object resultChecklist = checklistService.uploadFileChecklist(createChecklistRequest);
      log.info("saveChecklist END with request={}, resultChecklist={}, mapSizeFile={}",
          JsonUtil.convertObject2String(request, objectMapper),
          JsonUtil.convertObject2String(resultChecklist, objectMapper), mapSizeFile);
    } catch (Exception ex) {
      log.error("saveChecklist with request={}, Error: ",
          JsonUtil.convertObject2String(request, objectMapper), ex);
      throw ex;
    }
  }

  private void setAdditionIdAndChecklistMappingId(ChecklistBaseResponse<GroupChecklistDto>
      checklistBaseResponse, ChecklistDto checklistDto) {
    log.info("setAdditionIdAndChecklistMappingId START with  checklistBaseResponse={}, "
            + "checklistDto={}", JsonUtil.convertObject2String(checklistBaseResponse, objectMapper),
        JsonUtil.convertObject2String(checklistDto, objectMapper));
    if (Objects.nonNull(checklistBaseResponse)) {
      ChecklistDto checklist = checklistBaseResponse.getData().getListChecklist().stream().filter(
              e -> checklistDto.getCode().equalsIgnoreCase(e.getCode())
                  && checklistDto.getGroupCode().equalsIgnoreCase(e.getGroupCode())
          ).findFirst().orElse(null);
      if (Objects.nonNull(checklist)) {
        checklistDto.setAdditionalDataChecklistId(checklist.getAdditionalDataChecklistId());
        checklistDto.setChecklistMappingId(checklist.getChecklistMappingId());
        checklistDto.setDomainObjectId(checklist.getDomainObjectId());
        checklistDto.setDomainType(checklist.getDomainType());
      } else {
        log.info("setAdditionIdAndChecklistMappingId docCode={}, group={} not found in checklist",
            checklistDto.getCode(), checklistDto.getGroupCode());
      }
    }
  }

  /**
   * @Description: Lấy kích thước file theo filePath từ minIO, đợn vị: MB
   * @param size
   * @return
   */
  public String convertSizeFile(Long size) {
      return String.valueOf((float)size/1024/1024);
  }

  private String getSizeFileFromMap(String filePath, Map<String, Object> mapSizeFile) {
    try {
      String minIoPathFile = filePath
          .replace(minioProperties.getMinio().getBucketExt().get(BucketType.CMS.getValue())
              .getBucket() + ApplicationConstant.SEPARATOR, "");
      if (mapSizeFile.containsKey(minIoPathFile)) {
        Long size = Long.valueOf(mapSizeFile.get(minIoPathFile).toString());
        return convertSizeFile(size);
      }
    } catch (Exception ex) {
      log.error("getSizeFileFromMap with filePath={}, Error: ", filePath, ex);
    }
    return FILE_SIZE_DEFAULT;
  }

  @Transactional
  @Override
  public Object postCreateApplication(PostCmsCreateApplicationRequest request) {
    log.info("postCreateApplication START with request={}",
        JsonUtil.convertObject2String(request, objectMapper));
    // Check before create app
    List<ApplicationEntity> entityAppChecks = applicationRepository.findAppByRefId(
        request.getApplication().getRefId()).orElse(null);
    if (CollectionUtils.isNotEmpty(entityAppChecks)
        && entityAppChecks.stream().filter(e -> Objects.isNull(e.getStatus()) || !e.getStatus()
            .equalsIgnoreCase(ApplicationStatus.AS0099.getValue()))
        .anyMatch(e -> request.getApplication().getRefId().equalsIgnoreCase(e.getRefId()))) {
      throw new ApprovalException(DomainCode.EXIST_REFID_IN_APPLICATION_BUT_CLOSE,
          new Object[]{request.getApplication().getRefId()});
    }
    ApplicationEntity entityApp = ((CmsIntegrationServiceImpl) AopContext.currentProxy())
        .saveApplication(request);

    // Save thông tin địa chỉ vào thông tin thực địa
    if (CollectionUtils.isNotEmpty(request.getCustomer().getAddresses())) {
      postFieldInformationService.saveData(entityApp,
          buildFieldInformationRequest(request));
    }

    log.info("postCreateApplication END with request={}, bpmId={}",
        JsonUtil.convertObject2String(request, objectMapper), entityApp.getBpmId());
    return CmsBaseResponse.builder().bpmId(entityApp.getBpmId())
        .status(StatusAppType.NOT_READY.getValue()).build();
  }

  @Transactional
  public ApplicationEntity saveApplication(PostCmsCreateApplicationRequest request) {
    log.info("saveApplication START with request={}", JsonUtil.convertObject2String(request, objectMapper));
    // Customer
    CustomerEntity entityCus = persistCustomer(request);
    // Save Application
    // gen bpm_id
    String bpmId = idSequenceService.generateBpmId();
    ApplicationEntity entityApp = new ApplicationEntity().withBpmId(bpmId);
    entityApp.setRefId(request.getApplication().getRefId());
    entityApp.setSource(request.getApplication().getSource());
    entityApp.setCreatedBy(request.getApplication().getCreatedBy());
    entityApp.setSegment(request.getApplication().getSegment());
    entityApp.setProcessFlow("V003");
    entityApp.setProcessingStepCode(MAKE_PROPOSAL.getCode());
    entityApp.setCustomer(entityCus);
    entityApp.setSubmissionPurpose(NEW_LEVEL.getCode());
    entityApp.setSubmissionPurposeValue(messageSource.getMessage(NEW_LEVEL.getText(), null, Util.locale()));
    DataResponse regionAreaResp = getUserManagerClient().getRegionAreaByUserName(request
        .getApplication().getCreatedBy());
    if (Objects.isNull(regionAreaResp)) {
      throw new ApprovalException(DomainCode.ERR_GET_ORGANIZATION_BY_USER, new Object[]{request});
    }
    entityApp.setCreatedFullName(regionAreaResp.getCreatedFullName());
    entityApp.setCreatedPhoneNumber(regionAreaResp.getCreatedPhoneNumber());
    entityApp.setBusinessUnit(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ?
        regionAreaResp.getBusinessUnitDetail().getName() : null);
    entityApp.setBusinessCode(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ?
        regionAreaResp.getBusinessUnitDetail().getCode() : null);
    entityApp.setAreaCode(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ? regionAreaResp.getBusinessUnitDetail().getBusinessAreaCode() : "");
    entityApp.setArea(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ? regionAreaResp.getBusinessUnitDetail().getBusinessAreaFullName() : "");

    if (Objects.nonNull(regionAreaResp.getRegionDetail())) {
      entityApp.setRegion(regionAreaResp.getRegionDetail().getFullName());
      entityApp.setRegionCode(regionAreaResp.getRegionDetail().getCode());
    }

    OrganizationTreeDetail branchDetail = Util.getTreeDetailByType("CN",
        regionAreaResp.getRegionDetail());
    if (Objects.nonNull(branchDetail)) {
      entityApp.setBranchCode(branchDetail.getCode());
      entityApp.setBranchName(branchDetail.getFullName());
    }
    entityApp.setGeneratorStatus(GeneratorStatusEnums.DEFAULT.getValue());
    entityApp.setProcessingRole(PD_RB_RM.name());
    entityApp.setProcessingStep(messageSource.getMessage(MAKE_PROPOSAL.getValue(), null, Util.locale()));
    entityApp.setAssignee(request.getApplication().getCreatedBy());
    // ApplicationCredits
    persistAppCredits(request.getApplicationCredits(), entityApp, request.getApplication().getRefId());
    persistInComes(entityApp, request.getApplication().getRefId());

    // get sale code from user management
    entityApp.setSaleCode(userManagementClient.getSaleCode());

    applicationRepository.save(entityApp);
    applicationRepository.updateCreateBy(request.getApplication().getCreatedBy(), entityApp.getId());
    commonService.saveDraft(bpmId);
    checklistService.generateChecklist(entityApp);
    log.info("saveApplication END with request={}, bpmId={}",
        JsonUtil.convertObject2String(request, objectMapper), entityApp.getBpmId());
    return entityApp;
  }


  private void persistInComes(ApplicationEntity entityApp, String refId) {
    log.info("persistInComes START with refId={}", refId);
      ApplicationIncomeEntity entityIncome = new ApplicationIncomeEntity()
          .withApplication(entityApp)
          .withIncomeRecognitionMethod(ApplicationIncomeConstant.EXCHANGE)
          ;
    IndividualEnterpriseIncomeEntity individualIncomeEntity = new IndividualEnterpriseIncomeEntity()
        .withIncomeType(ActuallyIncomeDTO.INDIVIDUAL_BUSINESS);
      entityIncome.setIndividualEnterpriseIncomes(new HashSet<>(
          Arrays.asList(individualIncomeEntity)));
      entityApp.setIncomes(new HashSet<>(Arrays.asList(entityIncome)));
    log.info("persistInComes END with refId={}", refId);
  }

  private CustomerEntity persistCustomer(PostCmsCreateApplicationRequest request) {
    log.info("persistCustomer START with request={}, refId={}",
        JsonUtil.convertObject2String(request, objectMapper), request.getApplication().getRefId());
    List<CategoryDataResponse> lstGenders = serviceCache.getCategoryDataByCode(GENDER);
    List<CategoryDataResponse> lstMartialStatus = serviceCache.getCategoryDataByCode(MARTIAL_STATUS);
    List<CategoryDataResponse> lstDocumentTypes = serviceCache.getCategoryDataByCode(DOCUMENT_TYPE);
    List<CategoryDataResponse> lstRBV001AddressTypes = serviceCache.getCategoryDataByCode(RB_ADDRESS_TYPE_V001);
    List<CategoryDataResponse> lstRBV002AddressTypes = serviceCache.getCategoryDataByCode(RB_ADDRESS_TYPE_V002);
    List<CategoryDataResponse> lstEBV001AddressTypes = serviceCache.getCategoryDataByCode(EB_ADDRESS_TYPE_V001);
    // Customer
    CustomerEntity entityCus = new CustomerEntity()
        .withRefCusId(request.getCustomer().getRefCusId())
        .withCustomerType(request.getCustomer().getCustomerType());
    entityCus.setCreatedAt(LocalDateTime.now());
    checkCustomer(entityCus, request);
    // Individual Customer
    IndividualCustomerEntity indiCus = new IndividualCustomerEntity()
        .withFirstName(Util.splitFirstName(((IndividualCustomerDTO) request.getCustomer()).getFullName()))
        .withLastName(Util.splitLastName(((IndividualCustomerDTO) request.getCustomer()).getFullName()))
        .withGender(((IndividualCustomerDTO) request.getCustomer()).getGender())
        .withDateOfBirth(((IndividualCustomerDTO) request.getCustomer()).getDateOfBirth())
        .withMartialStatus(((IndividualCustomerDTO) request.getCustomer()).getMartialStatus().toString())
        .withMartialStatusValue(getCategoryValue(lstMartialStatus, ((IndividualCustomerDTO)
            request.getCustomer()).getMartialStatus().toString()))
        .withNation(((IndividualCustomerDTO) request.getCustomer()).getNation())
        .withSubject(request.getCustomer().getTypeOfCustomer())
        .withGenderValue(getCategoryValue(lstGenders, ((IndividualCustomerDTO) request.getCustomer()).getGender()))
        .withNationValue(((IndividualCustomerDTO) request.getCustomer()).getNation())
        .withEmail(((IndividualCustomerDTO) request.getCustomer()).getEmail())
        .withPhoneNumber(((IndividualCustomerDTO) request.getCustomer()).getPhoneNumber());
    entityCus.setIndividualCustomer(indiCus);
    // Customer Identity
    MercuryDataResponse lstCities = searchPlaceFromCacheMercury("");
    List<CustomerIdentityEntity> lstEntityCusIdentity = new ArrayList<>();
    List<CategoryDataResponse> lstIssueBy = serviceCache.getCategoryDataByCode(ISSUE_BY);
    for (CustomerIdentityDTO item : request.getCustomer().getIdentities()) {
      CustomerIdentityEntity entity = new CustomerIdentityEntity()
          .withDocumentType(item.getDocumentType())
          .withDocumentTypeValue(getCategoryValue(lstDocumentTypes, item.getDocumentType()))
          .withIdentifierCode(item.getIdentifierNumber())
          .withIssuedAt(item.getIssuedAt())
          .withIssuedBy(item.getIssuedBy())
          .withIssuedByValue(getCategoryValue(lstIssueBy, item.getIssuedBy()))
          .withIssuedPlace(item.getIssuedPlace())
          .withPriority(item.isPriority());
      entity.setIssuedPlaceValue(getNamePlaceByCodeFromCacheMercury(lstCities,
          item.getIssuedPlace()));
      entity.setCustomer(entityCus);
      entity.setCreatedAt(LocalDateTime.now());
      lstEntityCusIdentity.add(entity);
    }

    // Customer address

    List<CustomerAddressEntity> lstEntityCusAddress = new ArrayList<>();
    for (CustomerCmsAddressDTO item : request.getCustomer().getAddresses()) {
      CustomerAddressEntity entity = new CustomerAddressEntity()
          .withAddressType(item.getAddressType())
          .withAddressTypeValue(getAddressTypeValue(request, item.getAddressType(),
              lstRBV001AddressTypes, lstRBV002AddressTypes, lstEBV001AddressTypes))
          .withCanDelete(!AddressType.HK_THUONG_TRU.getValue().equals(item.getAddressType()));
      entity.setCustomer(entityCus);
      entity.setCreatedAt(LocalDateTime.now());
      entity.setCityCode(item.getCityCode());
      entity.setCityValue(getNamePlaceByCodeFromCacheMercury(lstCities,item.getCityCode()));
      MercuryDataResponse lstDistricts = searchPlaceFromCacheMercury(item.getCityCode());
      entity.setDistrictCode(item.getDistrictCode());
      entity.setDistrictValue(getNamePlaceByCodeFromCacheMercury(lstDistricts,item.getDistrictCode()));
      MercuryDataResponse lstWards = searchPlaceFromCacheMercury(item.getDistrictCode());
      entity.setWardCode(item.getWardCode());
      entity.setWardValue(getNamePlaceByCodeFromCacheMercury(lstWards,item.getWardCode()));
      entity.setAddressLine(item.getAddressLine());
      entity.setAddressLinkId(item.getAddressLinkId());
      lstEntityCusAddress.add(entity);
    }
    clearListCategories(lstGenders, lstMartialStatus , lstDocumentTypes,lstRBV001AddressTypes,
        lstRBV002AddressTypes, lstEBV001AddressTypes);
    List<CustomerContactEntity> lstEntityCusContacts = new ArrayList<>();
    for (CustomerContactDTO item : request.getCustomer().getContacts()) {
      CustomerContactEntity entity = new CustomerContactEntity()
          .withCustomer(entityCus)
          .withContactType(item.getContactType())
          .withValue(item.getValue())
          ;
      lstEntityCusContacts.add(entity);
    }
    entityCus.setCustomerIdentitys(new HashSet<>(lstEntityCusIdentity));
    entityCus.setCustomerAddresses(new HashSet<>(lstEntityCusAddress));
    entityCus.setCustomerContacts(new HashSet<>(lstEntityCusContacts));
    log.info("persistCustomer END with request={}, refId={}",
        JsonUtil.convertObject2String(request, objectMapper), request.getApplication().getRefId());
    return entityCus;
  }

  private String getAddressTypeValue(PostCmsCreateApplicationRequest request, String addressType,
      List<CategoryDataResponse> lstRBV001AddressTypes,
      List<CategoryDataResponse> lstRBV002AddressTypes,
      List<CategoryDataResponse> lstEBV001AddressTypes) {
    String result = "";
    log.info("getAddressTypeValue START with request={}, addressType={}, lstRBV001AddressTypes={},"
        + " lstRBV002AddressTypes={}, lstEBV001AddressTypes={}", JsonUtil.convertObject2String(request,
    objectMapper), addressType, JsonUtil.convertObject2String(lstRBV001AddressTypes,objectMapper),
        JsonUtil.convertObject2String(lstRBV002AddressTypes,objectMapper), JsonUtil.convertObject2String(lstEBV001AddressTypes,objectMapper));
    try {
      if (ApplicationConstant.Customer.EB.equalsIgnoreCase(request.getCustomer().getCustomerType())) {
        result = getCategoryValue(lstEBV001AddressTypes, addressType);
      }
      if (ApplicationConstant.Customer.RB.equalsIgnoreCase(request.getCustomer().getCustomerType())) {
        if (AddressType.HK_THUONG_TRU.getValue().equalsIgnoreCase(addressType)) {
          result = getCategoryValue(lstRBV002AddressTypes, addressType);
        }
        if (AddressType.DIA_CHI_TSC.getValue().equalsIgnoreCase(addressType)) {
          result = getCategoryValue(lstRBV002AddressTypes, addressType);
        }
      }
    } catch (Exception ex) {
      log.error("getAddressTypeValue End with refId={}, error: {}", request.getApplication()
          .getRefId(), ex.getMessage());
    }
    return result;
  }

  private void checkCustomer(CustomerEntity entityCus, PostCmsCreateApplicationRequest request) {
    log.info("checkCustomer START with request={}, refId={}",
        JsonUtil.convertObject2String(request, objectMapper), request.getApplication().getRefId());
    // Search Customer
    CustomerIdentityDTO identity = getIdentityPrimary(request.getCustomer());
    if (Objects.isNull(identity) || StringUtils.isBlank(identity.getIdentifierNumber())) {
      throw new ApprovalException(DomainCode.INVALID_INPUT_IDENTITY_NUMBER);
    }
    CustomerBaseResponse<SearchCustomerResponse> cusInfor =
        searchCustomer(identity.getIdentifierNumber());
    log.info("checkCustomer with refId={}, cusInfor={}", request.getApplication().getRefId(),
        JsonUtil.convertObject2String(cusInfor, objectMapper));
    if (Objects.isNull(cusInfor) || Objects.isNull(cusInfor.getData())
        || Objects.isNull(cusInfor.getData().getCustomers())) {
      log.error("Không tồn tại KH, cần tạo mới KH với identifierNumber={}",
          identity.getIdentifierNumber());
      CustomerBaseResponse<CreateRBCustomerResponse> cusInfoNew =  createCustomer(request);
      if(Objects.nonNull(cusInfoNew.getData().getCustomer().getId())) {
        log.info("checkCustomer new with refId={}, id={}", request.getApplication().getRefId(),
            cusInfoNew.getData().getCustomer().getId());
        entityCus.setRefCustomerId(cusInfoNew.getData().getCustomer().getId());
      }
      if (StringUtils.isNotBlank(cusInfoNew.getData().getCustomer().getBpmCif())) {
        log.info("checkCustomer new with refId={}, bpmCif={}", request.getApplication().getRefId(), cusInfoNew.getData().getCustomer().getBpmCif());
        entityCus.setBpmCif(cusInfoNew.getData().getCustomer().getBpmCif());
      }
      if (CollectionUtils.isNotEmpty(cusInfoNew.getData().getIdentityDocuments())) {
        IdentityDocumentResponse identityDocument = cusInfoNew.getData().getIdentityDocuments().stream().filter( i -> i.isPrimary()).findFirst().orElse(null);
        log.info("checkCustomer new with refId={}, cif={}", request.getApplication().getRefId(), identityDocument.getCif());
        entityCus.setBpmCif(identityDocument.getCif());
        entityCus.setCif(identityDocument.getCif());
      }
    } else {
      if (StringUtils.isNotBlank(cusInfor.getData().getCustomers().getBpmCif())) {
        log.info("checkCustomer with refId={}, bpmCif={}", request.getApplication().getRefId(),
            cusInfor.getData().getCustomers().getBpmCif());
        entityCus.setBpmCif(cusInfor.getData().getCustomers().getBpmCif());
      }
  /*    if (StringUtils.isNotBlank(cusInfor.getData().getCustomers().getCif())) {
        log.info("checkCustomer with refId={}, cif={}", request.getApplication().getRefId(), cusInfor.getData().getCustomers().getCif());
        entityCus.setCif(cusInfor.getData().getCustomers().getCif());
      }*/
      entityCus.setRefCustomerId(cusInfor.getData().getCustomers().getId());
      updateCustomer(request, cusInfor);
    }
    log.info("checkCustomer END with request={}, refId={}",
        JsonUtil.convertObject2String(request, objectMapper), request.getApplication().getRefId());
  }

  private void persistAppCredits(ApplicationCredit applicationCredits, ApplicationEntity entityApp,
      String refId) {
    log.info("persistAppCredits START with applicationCredits={}, refId={}",
        JsonUtil.convertObject2String(applicationCredits, objectMapper), refId);
    List<CategoryDataResponse> lstCardTypes = serviceCache.getCategoryDataByCode(CARD_TYPE);
    List<CategoryDataResponse> lstAutoDeductRates = serviceCache.getCategoryDataByCode(AUTO_DEDUCT_RATE);
    List<CategoryDataResponse> lstCreditForms = serviceCache.getCategoryDataByCode(CREDIT_FORM);
    // applicationCredits
    List<ApplicationCreditEntity> lstEntityAppCredits = new ArrayList<>();
    BigDecimal loanAmountTotal = BigDecimal.ZERO;
    DistributionForm distributionForm = DistributionForm.DF2;

    for (UnsecuredCreditCardDTO item : applicationCredits.getUnsecuredCreditCard()) {
      ApplicationCreditEntity entity = buildCommonCredit(entityApp, item.getCreditType(),
          item.getLoanAmount(), item.getGuaranteeForm());
      ApplicationCreditCardEntity creditCard = new ApplicationCreditCardEntity();
      creditCard.setCardType(item.getCardType());
      creditCard.setCardTypeValue(getCategoryValue(lstCardTypes, item.getCardType()));
      creditCard.setCardName(item.getCardName());
      creditCard.setDeductAccountNumber(item.getDeductAccountNumber());
      creditCard.setAutoDeductRate(item.getAutoDeductRate());
      creditCard.setAutoDeductRateValue(getCategoryValue(lstAutoDeductRates, item.getAutoDeductRate()));
      creditCard.setEmail(item.getEmail());
      entity.setCreditCard(creditCard);
      lstEntityAppCredits.add(entity);
      if (COLLATERAL.name().equalsIgnoreCase(item.getGuaranteeForm())) {
        distributionForm = DistributionForm.DF1;
      }
    }
    List<CategoryDataResponse> dataResponses = serviceCache.getCategoryDataByCode(LOAN_PURPOSE);
    if (CollectionUtils.isNotEmpty(applicationCredits.getUnsecuredCreditCard())) {
      loanAmountTotal = loanAmountTotal.add(applicationCredits.getUnsecuredCreditCard().stream()
          .map(UnsecuredCreditCardDTO::getLoanAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    for (UnsecuredLoanDTO item : applicationCredits.getUnsecuredLoan()) {
      ApplicationCreditEntity entity = buildCommonCredit(entityApp, item.getCreditType(),
          item.getLoanAmount(), item.getGuaranteeForm());
      ApplicationCreditLoanEntity creditLoan = new ApplicationCreditLoanEntity();
      creditLoan.setTotalCapital(item.getTotalCapital());
      creditLoan.setLoanPeriod(item.getLoanPeriod());
      creditLoan.setLoanPurpose(item.getLoanPurpose());
      creditLoan.setLoanPurposeValue(getCategoryValue(dataResponses, item.getLoanPurpose()));
      creditLoan.setCreditForm(item.getCreditForm());
      creditLoan.setCreditFormValue(getCategoryValue(lstCreditForms, item.getCreditForm()));
      entity.setCreditLoan(creditLoan);
      lstEntityAppCredits.add(entity);
      if (COLLATERAL.name().equalsIgnoreCase(item.getGuaranteeForm())) {
        distributionForm = DistributionForm.DF1;
      }
    }
    if (CollectionUtils.isNotEmpty(applicationCredits.getUnsecuredLoan())) {
      loanAmountTotal = loanAmountTotal.add(applicationCredits.getUnsecuredLoan().stream()
          .map(UnsecuredLoanDTO::getLoanAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    for (UnsecuredOverdraftDTO item : applicationCredits.getUnsecuredOverdraft()) {
      ApplicationCreditEntity entity = buildCommonCredit(entityApp, item.getCreditType(),
          item.getLoanAmount(), item.getGuaranteeForm());
      ApplicationCreditOverdraftEntity creditOverdraft = new ApplicationCreditOverdraftEntity();
      creditOverdraft.setLimitSustentivePeriod(item.getLimitSustentivePeriod());
      creditOverdraft.setLoanPurpose(item.getLoanPurpose());
      creditOverdraft.setLoanPurposeValue(getCategoryValue(dataResponses, item.getLoanPurpose()));
      entity.setCreditOverdraft(creditOverdraft);
      lstEntityAppCredits.add(entity);
      if (COLLATERAL.name().equalsIgnoreCase(item.getGuaranteeForm())) {
        distributionForm = DistributionForm.DF1;
      }
    }
    clearListCategories(lstCardTypes, lstAutoDeductRates, lstCreditForms);
    if (CollectionUtils.isNotEmpty(applicationCredits.getUnsecuredOverdraft())) {
      loanAmountTotal = loanAmountTotal.add(applicationCredits.getUnsecuredOverdraft().stream()
          .map(UnsecuredOverdraftDTO::getLoanAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }
    entityApp.setCredits(new HashSet<>(lstEntityAppCredits));
    entityApp.setSuggestedAmount(loanAmountTotal);
    entityApp.setDistributionFormCode(distributionForm.getCode());
    entityApp.setDistributionForm(messageSource.getMessage(distributionForm.getText(),
        null, Util.locale()));
    log.info("persistAppCredits END with applicationCredits={}, refId={}",
        JsonUtil.convertObject2String(applicationCredits, objectMapper), refId);
  }

  private void clearListCategories(Object... lst) {
    Arrays.stream(lst).collect(Collectors.toList()).forEach(e ->
        ((List<CategoryDataResponse>) e).clear());
  }

  private String getCategoryValue(List<CategoryDataResponse> lstCategories, String code) {
    log.info("getCategoryValue START with lstCategories={}, code={}",
        JsonUtil.convertObject2String(lstCategories, objectMapper), code);
    String result = "";
    try {
      if (CollectionUtils.isEmpty(lstCategories) || StringUtils.isEmpty(code)) {
        return "";
      }
      CategoryDataResponse category = lstCategories.stream().filter(e -> StringUtils.isNotBlank(
          e.getCode()) && code.equalsIgnoreCase(e.getCode())).findFirst().orElse(null);
      if (Objects.nonNull(category)) {
        result = category.getValue();
      }
    } catch (Exception ex) {
      log.error("getCategoryValue END with lstCategories={}, code={}, error={}",
          JsonUtil.convertObject2String(lstCategories, objectMapper), code, ex.getMessage());
    }

    log.info("getCategoryValue END with lstCategories={}, code={}, result={}",
        JsonUtil.convertObject2String(lstCategories, objectMapper), code, result);
    return result;
  }

  private ApplicationCreditEntity buildCommonCredit(ApplicationEntity entityApp, String creditType,
      BigDecimal loanAmount, String guaranteeForm) {
    List<CategoryDataResponse> lstCreditTypes = serviceCache.getCategoryDataByCode(CREDIT_TYPE);
    List<CategoryDataResponse> lstGuaranteeForms = serviceCache.getCategoryDataByCode(GUARANTEE_FORM);
    return new ApplicationCreditEntity()
        .withApplication(entityApp)
        .withCreditType(creditType)
        .withCreditTypeValue(getCategoryValue(lstCreditTypes, creditType))
        .withLoanAmount(loanAmount)
        .withGuaranteeForm(guaranteeForm)
        .withGuaranteeFormValue(getCategoryValue(lstGuaranteeForms, guaranteeForm))
        ;
  }

  private void updateCustomer(PostCmsCreateApplicationRequest request,
      CustomerBaseResponse<SearchCustomerResponse> cusInfor) {
    log.info("updateCustomer START with refId={} request={}, cusInfor={}",
        request.getApplication().getRefId(),
        JsonUtil.convertObject2String(request, objectMapper),
        JsonUtil.convertObject2String(cusInfor, objectMapper));

    CommonCustomerRequest updateCustomerRequest = buildCustomerRequest(request, cusInfor);
    CustomerBaseResponse<UpdateRBCustomerResponse> result =
        customerClient.updateCustomer(updateCustomerRequest);

    log.info("updateCustomer END with refId={} request={}, cusInfor={}, result={}",
        request.getApplication().getRefId(),
        JsonUtil.convertObject2String(request, objectMapper),
        JsonUtil.convertObject2String(cusInfor, objectMapper),
        JsonUtil.convertObject2String(result, objectMapper));
  }

  private CustomerBaseResponse<CreateRBCustomerResponse> createCustomer(
      PostCmsCreateApplicationRequest request) {
    log.info("createCustomer START with refId={}, request={}", request.getApplication().getRefId()
        ,JsonUtil.convertObject2String(request, objectMapper));

    CommonCustomerRequest commonCustomerRequest = buildCustomerRequest(request, null);
    CustomerBaseResponse<CreateRBCustomerResponse> result =
         customerClient.createCustomer(commonCustomerRequest);

    log.info("createCustomer END with refId={}, request={}, result={}",
        request.getApplication().getRefId(),
        JsonUtil.convertObject2String(request, objectMapper),
        JsonUtil.convertObject2String(result, objectMapper));
    return result;
  }

  private CommonCustomerRequest buildCustomerRequest(PostCmsCreateApplicationRequest request,
      CustomerBaseResponse<SearchCustomerResponse> cusInfor) {
    log.info("buildCustomerRequest START with refId={}, cusInfor={}",
        request.getApplication().getRefId(), JsonUtil.convertObject2String(cusInfor, objectMapper));
    CustomerIdentityDTO identity = getIdentityPrimary(request.getCustomer());

    Customer customer = Customer.builder()
        //.cifNo(request.getCustomer().getCifNo())
        .name(((IndividualCustomerDTO) request.getCustomer()).getFullName())
        //.identityNumber(Objects.nonNull(identity) ? identity.getIdentifierNumber() : null)
        //.issuedBy(Objects.nonNull(identity) ? identity.getIssuedBy() : null)
        //.issuedPlace(Objects.nonNull(identity) ? identity.getIssuedPlace() : null)
        //.issuedDate(Objects.nonNull(identity) ? identity.getIssuedAt() : null)
        .birthday(((IndividualCustomerDTO) request.getCustomer()).getDateOfBirth())
        //.identityType(Objects.nonNull(identity) ? identity.getDocumentType() : null)
        .gender(((IndividualCustomerDTO) request.getCustomer()).getGender())
        .national(((IndividualCustomerDTO) request.getCustomer()).getNation())
        .maritalStatus(((IndividualCustomerDTO) request.getCustomer()).getMartialStatus())
        .phoneNumber(((IndividualCustomerDTO) request.getCustomer()).getPhoneNumber())
        .email(((IndividualCustomerDTO) request.getCustomer()).getEmail())
        .customerSegment(request.getCustomer().getTypeOfCustomer())
        .object(false)
        .build();
    if (Objects.nonNull(cusInfor) && Objects.nonNull(cusInfor.getData())
        && Objects.nonNull(cusInfor.getData().getCustomers())) {
      customer.setBpmCif(cusInfor.getData().getCustomers().getBpmCif());
      //customer.setCifNo(cusInfor.getData().getCustomers().getCif());
      customer.setId(cusInfor.getData().getCustomers().getId());
    }

    List<Address> addresses = new ArrayList<>();
    request.getCustomer().getAddresses().stream().filter(e -> StringUtils.isNotBlank(
        e.getAddressType()) && AddressType.HK_THUONG_TRU.getValue()
            .equalsIgnoreCase(e.getAddressType())
        ).forEach(address -> addresses.add(Address.builder()
        .addressType(address.getAddressType())
        .cityCode(address.getCityCode())
        .edit(false)
        .districtCode(address.getDistrictCode())
        .wardCode(address.getWardCode())
        .addressLine(address.getAddressLine())
        .build()));
    CommonCustomerRequest result = new CommonCustomerRequest();
    result.setCustomer(customer);
    result.setAddresses(addresses);
    result.setIdentityDocuments(new ArrayList<>());
    log.info("buildCustomerRequest END with refId={}, cusInfor={}, result={}",
        request.getApplication().getRefId(),
        JsonUtil.convertObject2String(cusInfor, objectMapper),
        JsonUtil.convertObject2String(result, objectMapper));
    return result;
  }

  private CustomerIdentityDTO getIdentityPrimary(CustomerDTO customer) {
    return customer.getIdentities()
        .stream().filter(CustomerIdentityDTO::isPriority).findFirst().orElse(null);
  }

  private CustomerBaseResponse<SearchCustomerResponse> searchCustomer(String identityNumber) {
    log.info("searchCustomer START with identityNumber={}", identityNumber);
    CustomerBaseResponse<SearchCustomerResponse> result =
        customerClient.searchCustomer(identityNumber);
    log.info("searchCustomer END with identityNumber={}, result={}", identityNumber,
        JsonUtil.convertObject2String(result, objectMapper));
    return result;
  }

  private PostFieldInformationRequest buildFieldInformationRequest(
      PostCmsCreateApplicationRequest cmsRequest) {
    PostFieldInformationRequest request = new PostFieldInformationRequest();

    Set<ApplicationFieldInformationDTO> fieldInformations = new HashSet<>();

    List<CategoryDataResponse> relationshipCategories = serviceCache.getCategoryDataByCode(
        ConfigurationCategory.RELATIONSHIP);

    CategoryDataResponse customerRelationshipCategory = relationshipCategories.stream()
        .filter(filterCate -> "V017".equalsIgnoreCase(filterCate.getCode()))
        .findFirst()
        .orElse(null);

    cmsRequest.getCustomer().getAddresses().forEach(address -> {
      ApplicationFieldInformationDTO fieldInformationDTO = ApplicationFieldInformationMapper.INSTANCE.cmsAddressToFieldInformationDTO(
          address);

      if (customerRelationshipCategory != null) {
        fieldInformationDTO.setRelationship(customerRelationshipCategory.getCode());
        fieldInformationDTO.setRelationshipValue(customerRelationshipCategory.getValue());
      }

      fieldInformationDTO.setExecutor(cmsRequest.getApplication().getCreatedBy());
      fieldInformations.add(fieldInformationDTO);
    });

    request.setFieldInformations(fieldInformations);
    return request;
  }

  private MercuryDataResponse searchPlaceFromCacheMercury(String code) {
    return mercuryCache.searchPlace(code);
  }

  private String getNamePlaceByCodeFromCacheMercury(MercuryDataResponse lstPlaces, String code) {
    try {
      log.info("getCityByCodeFromCacheMercury START with lstPlaces={}, code={}",
          JsonUtil.convertObject2String(lstPlaces, objectMapper), code);
      if (StringUtils.isEmpty(code) || !code.chars().allMatch(Character::isDigit)) {
        return  "";
      }
      if (Objects.nonNull(lstPlaces) && CollectionUtils
          .isNotEmpty(lstPlaces.getValue())) {
        MercuryDataResponse.Value city = lstPlaces.getValue().stream().filter(e -> StringUtils
            .isNotBlank(e.getId()) && code.equalsIgnoreCase(e.getId())).findFirst().orElse(null);
        if (Objects.nonNull(city)) {
          return city.getName();
        }
      }
    } catch (Exception ex) {
      log.error("getCityByCodeFromCacheMercury END with error: {}", ex.getMessage());
    }
    return "";
  }
}
