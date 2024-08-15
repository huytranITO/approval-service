package com.msb.bpm.approval.appr.service.common;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.APPLICATION_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INDIVIDUAL_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CUSTOMER_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.EB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.INCOME_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSET_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.ASSET_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.Constant.AUTO_GENERATED;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.UNDER_SCORE;
import static com.msb.bpm.approval.appr.enums.application.LoanLimit.RECOMMEND;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.MAKE_PROPOSAL;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_DN_HGD_HKD;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_ENTERPRISE;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_PROPERTY_BUSINESS;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_RENTAL_ASSET;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_FROM_SALARY;
import static com.msb.bpm.approval.appr.enums.checklist.Group.INCOME_OTHER;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.DOCUMENT_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.GENDER;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.ISSUE_BY;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.MARTIAL_STATUS;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.NATIONAL;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RELATIONSHIP;
import static com.msb.bpm.approval.appr.util.DateUtils.formatLocalDate;
import static com.msb.bpm.approval.appr.util.Util.categoryCodes;
import static com.msb.bpm.approval.appr.util.Util.getNamePlaceByCodeFromCacheMercury;
import static com.msb.bpm.approval.appr.util.Util.splitFirstName;
import static com.msb.bpm.approval.appr.util.Util.splitLastName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.chat.events.CreateGroupEvent;
import com.msb.bpm.approval.appr.client.asset.AssetClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralConfigProperties;
import com.msb.bpm.approval.appr.client.creditconditions.CreditConditionClient;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.customer.request.SearchCusRelationshipRequest;
import com.msb.bpm.approval.appr.client.customer.response.CusRelationForSearchResponse;
import com.msb.bpm.approval.appr.client.customer.response.CusRelationForSearchResponse.Relationship;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.UserManagementClient;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.ApprovalResult;
import com.msb.bpm.approval.appr.enums.application.AssetBusinessType;
import com.msb.bpm.approval.appr.enums.asset.ActionType;
import com.msb.bpm.approval.appr.enums.asset.CollateralEndpoint;
import com.msb.bpm.approval.appr.enums.checklist.ChecklistEnum;
import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.ApplicationAppraisalContentMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditConditionMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationFieldInformationMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationLimitCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import com.msb.bpm.approval.appr.mapper.collateral.AssetAllocationMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditConditionsDTO;
import com.msb.bpm.approval.appr.model.dto.AssetCommonInfoDTO;
import com.msb.bpm.approval.appr.model.dto.DebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.GetApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditConditionsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.EnterpriseCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.request.asset.BPMAssetRequest;
import com.msb.bpm.approval.appr.model.request.checklist.CreateChecklistRequest;
import com.msb.bpm.approval.appr.model.request.collateral.AssetResponse;
import com.msb.bpm.approval.appr.model.request.collateral.CreditAssetRequest;
import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;
import com.msb.bpm.approval.appr.model.response.asset.AssetDataResponse;
import com.msb.bpm.approval.appr.model.response.asset.AssetInfoResponse;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.collateral.AssetAllocationResponse;
import com.msb.bpm.approval.appr.model.response.collateral.CreditAssetAllocationResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionResponse;
import com.msb.bpm.approval.appr.model.response.customer.AddressDetailResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomerDetailResponse;
import com.msb.bpm.approval.appr.model.response.customer.IdentityDocumentResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV2Response;
import com.msb.bpm.approval.appr.model.response.usermanager.OrganizationTreeDetail;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea.DataResponse;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRelationshipRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.application.impl.PostCreateApplicationServiceImpl;
import com.msb.bpm.approval.appr.service.application.impl.PostDebtInfoServiceImpl;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.cache.MercuryConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.service.cms.impl.CmsCreateApplicationServiceImpl;
import com.msb.bpm.approval.appr.service.idgenerate.IDSequenceService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@Slf4j
@RequiredArgsConstructor
public class CopyApplicationService extends AbstractBaseService {

  public static final String MORTGAGE_STATUS_DEFAULT = "V002";
  private final CommonService commonService;
  private final PostDebtInfoServiceImpl postDebtInfoService;
  private final PostCreateApplicationServiceImpl postCreateApplicationService;
  private final CmsCreateApplicationServiceImpl cmsCreateApplicationService;
  private final ApplicationRepository applicationRepository;
  private final CustomerRepository customerRepository;
  private final ApplicationMapper applicationMapper;
  private final CustomerMapper customerMapper;
  private final CustomerRelationshipRepository customerRelationshipRepository;
  private final ApplicationIncomeMapper applicationIncomeMapper;
  private final ApplicationCreditMapper applicationCreditMapper;
  private final ApplicationFieldInformationMapper applicationFieldInformationMapper;
  private final ApplicationAppraisalContentMapper applicationAppraisalContentMapper;
  private final ApplicationLimitCreditMapper applicationLimitCreditMapper;
  private final ChecklistService checklistService;
  @Qualifier("objectMapperWithNull")
  private final ObjectMapper objectMapper;
  private final UserManagerClient userManagerClient;
  private final UserManagementClient userManagementClient;
  private final CreditConditionClient creditConditionClient;
  private final AssetClient assetClient;
  private final CollateralClient collateralClient;
  private final MessageSource messageSource;
  private final IDSequenceService idSequenceService;
  private final CustomerClient customerClient;
  private final MercuryConfigurationServiceCache mercuryCache;
  private final ConfigurationServiceCache configurationServiceCache;
  private final CollateralConfigProperties configProperties;
  private final ApplicationDraftRepository applicationDraftRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  MercuryDataResponse city;
  Map<String, List<GetListResponse.Detail>> categoryMap;

  @Transactional
  public String copyApplication(String bpmIdOld) {

    log.info("execute START copyApplication from oldBpmId={}", bpmIdOld);
    // Map <Old refCustomerId, CustomerEntity New> only customer inactive in order to get (fullName + refCustomerId new + identity) for income + asset
    categoryMap = configurationServiceCache.getCategoryDataByCodes(categoryCodes);

    Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive = new HashMap<>();
    ApplicationEntity applicationEntity = createApplicationEntity(bpmIdOld,
        mapRefCustomerIdAndCustomerInactive);

    // Save data application
    ApplicationEntity applicationEntityNew = applicationRepository.saveAndFlush(applicationEntity);

    // Generate application draft
    commonService.saveDraft(applicationEntity.getBpmId());

    // Mapping asset data
    Object assetResponse = mappingAssetInfo(applicationEntityNew, bpmIdOld,
        mapRefCustomerIdAndCustomerInactive);

    // Add asset info to draft
    addAssetInfo2Draft(applicationEntityNew, assetResponse);
    // Mapping asset allocation
    mappingAssetAllocationInfo(applicationEntityNew, bpmIdOld);

    //Checklist
    copyCheckList(applicationEntityNew, bpmIdOld);

    addDeftInfoToDraft(applicationEntityNew);

    log.info("execute END copyApplication newBpmId={} from oldBpmId={}",
        applicationEntityNew.getBpmId(), bpmIdOld);

    log.info("START SmartChat CreateGroup Event with applicationId={}",
            applicationEntityNew.getBpmId());
    applicationEventPublisher.publishEvent(new CreateGroupEvent(this,applicationEntityNew.getBpmId()));

    return applicationEntityNew.getBpmId();
  }

  private void addDeftInfoToDraft(ApplicationEntity applicationEntityNew) {
    // Add allocation info to draft
    GetApplicationDTO getApplicationDTO = getApplicationInfo(applicationEntityNew);
    DebtInfoDTO debtInfoDTO = getApplicationDTO.getDebtInfo();
    debtInfoDTO.setType(ApplicationConstant.PostBaseRequest.POST_DEBT_INFO);

    // Mapping credits
    Map<Long, List<Long>> creditMaps = applicationEntityNew.getCredits().stream()
        .collect(HashMap::new, (m, v) -> m.put(v.getId(), v.getAssets()), HashMap::putAll);

    debtInfoDTO.getCredits().forEach(cr -> cr.setAssets(creditMaps.get(cr.getId())));

    debtInfoDTO.setAssetAllocations(applicationEntityNew.getAssetAllocations());
    persistApplicationDraft(applicationEntityNew.getBpmId(), DEBT_INFO, UNFINISHED, debtInfoDTO);
  }

  private ApplicationEntity createApplicationEntity(String bpmIdOld,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    log.info("START createApplicationEntity in copy application...");
    ApplicationEntity applicationEntityOld = applicationRepository.findByBpmId(bpmIdOld)
        .orElseThrow(
            () -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION, new Object[]{bpmIdOld}));

    // Copy Application Data
    ApplicationEntity applicationEntityNew = applicationMapper.copyApplicationEntity(
        applicationEntityOld);
    applicationEntityNew.setStatus(ApplicationStatus.AS0000.getValue());
    String bpmId = idSequenceService.generateBpmId();
    applicationEntityNew.setBpmId(bpmId);
    applicationEntityNew.setRefId(bpmId);
    //Application Contact
    applicationEntityNew.setContact(
        applicationMapper.copyApplicationContactEntities(applicationEntityOld.getContact()));
    addApplicationForContact(applicationEntityNew);
    // Customer
    Map<Long, Long> mapCustomerId = new HashMap<>();
    //Save customer
    applicationEntityNew.setCustomer(
        saveCustomer(applicationEntityOld, mapCustomerId, mapRefCustomerIdAndCustomerInactive));
    // Related Person
    addRelatedCustomer(bpmIdOld, applicationEntityNew.getCustomer(), mapCustomerId,
        mapRefCustomerIdAndCustomerInactive);
    // Income
    applicationEntityNew.setIncomes(
        applicationIncomeMapper.copyApplicationIncomeEntities(applicationEntityOld.getIncomes()));
    addApplicationForIncomes(applicationEntityNew, mapCustomerId,
        mapRefCustomerIdAndCustomerInactive);
    //Credit
    applicationEntityNew.setCredits(
        applicationCreditMapper.copyApplicationCreditEntities(applicationEntityOld.getCredits()));
    addApplicationForCredits(applicationEntityNew);
    //Limit Credit
    applicationEntityNew.setLimitCredits(
        applicationLimitCreditMapper.copyApplicationLimitCreditEntities(
            applicationEntityOld.getLimitCredits()));

    // caculate credit
    reCaculateLimitCredit(applicationEntityNew);

    addApplicationForLimitCredits(applicationEntityNew);
    //Application Appraisal Content
    applicationEntityNew.setAppraisalContents(
        applicationAppraisalContentMapper.copyApplicationAppraisalContentEntities(
            applicationEntityOld.getAppraisalContents()));
    addApplicationForAppraisalContent(applicationEntityNew);
    //Application Field
    applicationEntityNew.setFieldInformations(
        applicationFieldInformationMapper.copyApplicationFieldInformationEntities(
            applicationEntityOld.getFieldInformations()));
    addApplicationForFieldInfo(applicationEntityNew);

    // Add user RM info
    addUserRMInfo(applicationEntityNew);

    // Credit condition
    addCreditConditions(applicationEntityNew, applicationEntityOld.getCreditConditions());

    // Save history
    cmsCreateApplicationService.createFirstHistory(applicationEntityNew);

    // Start process instance camunda
    postCreateApplicationService.startCamundaInstance(applicationEntityNew);

    return applicationEntityNew;
  }

  private void reCaculateLimitCredit(ApplicationEntity applicationEntityNew) {
    // List credit
    applicationEntityNew.getLimitCredits().forEach(lm -> {
      if (RECOMMEND.name().equalsIgnoreCase(lm.getLoanLimit())) {
        calculateLimitCreditEntities(applicationEntityNew.getCredits(), lm);
      }
    });
  }

  /**
   * Save customer
   *
   * @param applicationEntityOld
   * @param mapCustomerId
   * @param mapRefCustomerIdAndCustomerInactive (Map<Old RefCustomerId, CustomerInactive New>)
   * @return
   */
  private CustomerEntity saveCustomer(ApplicationEntity applicationEntityOld,
      Map<Long, Long> mapCustomerId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    //check KH active or inactive
    CustomerBaseResponse<SearchCustomerV2Response> responseDetail = customerClient.searchCustomerDetail(
        applicationEntityOld.getCustomer().getRefCustomerId(), null);
    if (Objects.isNull(responseDetail) || Objects.isNull(responseDetail.getData())
        || Objects.isNull(responseDetail.getData().getCustomer())) {
      throw new ApprovalException(DomainCode.NOT_FOUND_CUSTOMER_FROM_AI);
    }
    SearchCustomerV2Response searchCustomerV2Response = responseDetail.getData();
    //Active Customer
    if (searchCustomerV2Response.getCustomer().getId()
        .equals(applicationEntityOld.getCustomer().getRefCustomerId())) {
      return saveCustomerActive(applicationEntityOld, mapCustomerId);
    } else {    //InActive Customer
      CustomerEntity customerEntityNew = saveCustomerInActive(applicationEntityOld.getCustomer(),
          searchCustomerV2Response, mapCustomerId);
      mapRefCustomerIdAndCustomerInactive.put(applicationEntityOld.getCustomer().getRefCustomerId(),
          customerEntityNew);
      return customerEntityNew;
    }
  }

  private CustomerEntity saveCustomerActive(ApplicationEntity applicationEntityOld,
      Map<Long, Long> mapCustomerId) {
    CustomerEntity customerEntity = customerMapper.copyCustomerEntity(
        applicationEntityOld.getCustomer());
    addLinkForCustomer(customerEntity);
    customerEntity = customerRepository.saveAndFlush(customerEntity);
    mapCustomerId.put(customerEntity.getOldId(), customerEntity.getId());
    return customerEntity;
  }

  private CustomerEntity saveCustomerInActive(CustomerEntity oldCustomerEntity,
      SearchCustomerV2Response searchCustomerV2Response, Map<Long, Long> mapCustomerId) {
    CustomerEntity customerEntity = createCustomerEntityInactive(oldCustomerEntity,
        searchCustomerV2Response);
    customerEntity = customerRepository.saveAndFlush(customerEntity);
    mapCustomerId.put(oldCustomerEntity.getId(), customerEntity.getId());
    return customerEntity;
  }

  private CustomerEntity createCustomerEntityInactive(CustomerEntity oldCustomerEntity,
      SearchCustomerV2Response searchCustomerV2Response) {
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setCustomerType(RB);
    customerEntity.setRefCustomerId(searchCustomerV2Response.getCustomer().getId());
    customerEntity.setBpmCif(searchCustomerV2Response.getCustomer().getBpmCif());
    customerEntity.setRefCusId(customerMapper.generateRefCusId(customerEntity));
    customerEntity.setVersion(searchCustomerV2Response.getCustomer().getVersion());
    if (Objects.nonNull(searchCustomerV2Response.getCoreCustomer())) {
      customerEntity.setCif(searchCustomerV2Response.getCoreCustomer().getCifNumber());
    }
    List<CustomerAddressEntity> customerAddressEntities = createCustomerAddressEntities(
        customerEntity, searchCustomerV2Response.getAddresses());
    List<CustomerIdentityEntity> customerIdentityEntities = createCustomerIdentityEntities(
        customerEntity, searchCustomerV2Response.getIdentityDocuments());
    IndividualCustomerEntity individualCustomer = createIndividualCustomerEntity(
        searchCustomerV2Response.getCustomer());
    EnterpriseCustomerEntity enterpriseCustomerEntity = customerMapper.copyEnterpriseCustomerEntity(
        oldCustomerEntity.getEnterpriseCustomer());

    customerEntity.setIndividualCustomer(individualCustomer);
    customerEntity.setEnterpriseCustomer(enterpriseCustomerEntity);
    customerEntity.setCustomerIdentitys(new HashSet<>(customerIdentityEntities));
    customerEntity.setCustomerAddresses(new HashSet<>(customerAddressEntities));
    return customerEntity;
  }

  private IndividualCustomerEntity createIndividualCustomerEntity(
      CustomerDetailResponse customerDetailResponse) {

    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity().withFirstName(
            splitFirstName(customerDetailResponse.getName()))
        .withLastName(splitLastName(customerDetailResponse.getName()))
        .withGender(customerDetailResponse.getGender()).withGenderValue(
            configurationServiceCache.getCategoryData(categoryMap, GENDER)
                .get(customerDetailResponse.getGender()))
        .withDateOfBirth(formatLocalDate(customerDetailResponse.getBirthday(), DD_MM_YYYY_FORMAT))
        .withMartialStatus(customerDetailResponse.getMaritalStatus()).withMartialStatusValue(
            configurationServiceCache.getCategoryData(categoryMap, MARTIAL_STATUS)
                .get(customerDetailResponse.getMaritalStatus()))
        .withNation(customerDetailResponse.getNational()).withNationValue(
            configurationServiceCache.getCategoryData(categoryMap, NATIONAL)
                .get(customerDetailResponse.getNational()))
        .withEmail(customerDetailResponse.getEmail())
        .withPhoneNumber(customerDetailResponse.getPhoneNumber())
        .withSubject(customerDetailResponse.getCustomerSegment())
        .withEmployeeCode(customerDetailResponse.getStaffId());
    if (StringUtils.isNotEmpty(customerDetailResponse.getStaffId())) {
      individualCustomer.setMsbMember(true);
    }
    return individualCustomer;
  }

  private List<CustomerAddressEntity> createCustomerAddressEntities(CustomerEntity customerEntity,
      List<AddressDetailResponse> addressResponses) {
    // Customer address
    city = mercuryCache.searchPlace("");
    List<CustomerAddressEntity> customerAddressEntities = new ArrayList<>();
    for (AddressDetailResponse item : addressResponses) {
      CustomerAddressEntity entity = new CustomerAddressEntity().withAddressType(
          item.getAddressType()).withAddressTypeValue(
          commonService.getAddressTypeValue(customerEntity.getCustomerType(),
              item.getAddressType())).withCanDelete(item.isHktt());
      entity.setCustomer(customerEntity);
      entity.setCityCode(item.getCityCode());
      entity.setCityValue(getNamePlaceByCodeFromCacheMercury(city, item.getCityCode()));
      entity.setDistrictCode(item.getDistrictCode());
      entity.setDistrictValue(
          commonService.getDistrictValue(item.getCityCode(), item.getDistrictCode()));
      entity.setWardCode(item.getWardCode());
      entity.setWardValue(commonService.getWardValue(item.getDistrictCode(), item.getWardCode()));
      entity.setAddressLine(item.getAddressLine());
      customerAddressEntities.add(entity);
    }
    return customerAddressEntities;
  }

  private List<CustomerIdentityEntity> createCustomerIdentityEntities(CustomerEntity customerEntity,
      List<IdentityDocumentResponse> identityDocumentResponses) {
    List<CustomerIdentityEntity> customerIdentityEntities = new ArrayList<>();

    for (IdentityDocumentResponse item : identityDocumentResponses) {
      CustomerIdentityEntity entity = new CustomerIdentityEntity().withDocumentType(item.getType())
          .withDocumentTypeValue(
              configurationServiceCache.getCategoryData(categoryMap, DOCUMENT_TYPE)
                  .get(item.getType())).withIdentifierCode(item.getIdentityNumber())
          .withIssuedAt(item.getIssuedDate()).withIssuedBy(item.getIssuedBy()).withIssuedByValue(
              configurationServiceCache.getCategoryData(categoryMap, ISSUE_BY)
                  .get(item.getIssuedBy())).withIssuedPlace(item.getIssuedPlace())
          .withIssuedPlaceValue(getNamePlaceByCodeFromCacheMercury(city, item.getIssuedPlace()))
          .withPriority(item.isPrimary()).withCustomer(customerEntity);
      customerIdentityEntities.add(entity);
    }
    return customerIdentityEntities;
  }

  /**
   * Add link for customer (address, identity, contact)
   *
   * @param customerEntity
   */
  private void addLinkForCustomer(CustomerEntity customerEntity) {
    if (CollectionUtils.isNotEmpty(customerEntity.getCustomerAddresses())) {
      customerEntity.getCustomerAddresses().forEach(addr -> addr.setCustomer(customerEntity));
    }
    if (CollectionUtils.isNotEmpty(customerEntity.getCustomerIdentitys())) {
      customerEntity.getCustomerIdentitys()
          .forEach(identity -> identity.setCustomer(customerEntity));
    }
    if (CollectionUtils.isNotEmpty(customerEntity.getCustomerContacts())) {
      customerEntity.getCustomerContacts().forEach(contact -> contact.setCustomer(customerEntity));
    }
  }

  /**
   * Create related person and add customer id in mapCustomerId
   *
   * @param bpmIdOld
   * @param customerEntity
   * @param mapCustomerId
   * @param mapRefCustomerIdAndCustomerInactive (Map<Old RefCustomerId, CustomerInactive New>)
   */
  private void addRelatedCustomer(String bpmIdOld, CustomerEntity customerEntity,
      Map<Long, Long> mapCustomerId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    //Ordered by id
    List<CustomerEntity> relatedCustomerNewList = new ArrayList<>();
    List<CustomerEntity> relatedPersonOld = customerRepository.getAllCustomerRelationByBpmId(
        bpmIdOld);
    relatedPersonOld.forEach(oldRelatedCustomer -> {
      if (RB.equals(oldRelatedCustomer.getCustomerType())) {//RB mới có customer detail
        //check KH active or inactive
        CustomerBaseResponse<SearchCustomerV2Response> responseDetail = customerClient.searchCustomerDetail(
            oldRelatedCustomer.getRefCustomerId(), null);
        if (Objects.isNull(responseDetail) || Objects.isNull(responseDetail.getData())
            || Objects.isNull(responseDetail.getData().getCustomer())) {
          throw new ApprovalException(DomainCode.NOT_FOUND_CUSTOMER_FROM_AI);
        }
        SearchCustomerV2Response searchCustomerV2Response = responseDetail.getData();
        //Active
        if (searchCustomerV2Response.getCustomer().getId()
            .equals(oldRelatedCustomer.getRefCustomerId())) {
          CustomerEntity relatedCustomerCopy = customerMapper.copyCustomerEntity(
              oldRelatedCustomer);
          addLinkForCustomer(relatedCustomerCopy);
          relatedCustomerNewList.add(relatedCustomerCopy);
        } else {    //InActive
          CustomerEntity customerEntityNew = createCustomerEntityInactive(oldRelatedCustomer,
              searchCustomerV2Response);
          customerEntityNew.setPaymentGuarantee(oldRelatedCustomer.getPaymentGuarantee());
          relatedCustomerNewList.add(customerEntityNew);
          mapRefCustomerIdAndCustomerInactive.put(oldRelatedCustomer.getRefCustomerId(),
              customerEntityNew);
        }
      } else {
        CustomerEntity relatedCustomerCopy = customerMapper.copyCustomerEntity(oldRelatedCustomer);
        addLinkForCustomer(relatedCustomerCopy);
        relatedCustomerNewList.add(relatedCustomerCopy);
      }
    });
    //Ordered by customer_id
    List<CustomerRelationShipEntity> customerRelationShipListOld = customerRelationshipRepository.getCustomerRelationByBmpId(
        bpmIdOld);

    //create related customer and have id
    List<CustomerEntity> relatedCustomerCreated = customerRepository.saveAllAndFlush(
        relatedCustomerNewList);

    addRelationship(customerEntity, customerRelationShipListOld, relatedCustomerCreated,
        mapCustomerId);
  }

  private void addRelationship(CustomerEntity customerEntity,
      List<CustomerRelationShipEntity> customerRelationShipListOld,
      List<CustomerEntity> relatedCustomerCreated, Map<Long, Long> mapCustomerId) {
    //Call customer info in order to get relationship
    CustomerBaseResponse<CusRelationForSearchResponse> responseDetail = customerClient.searchCustomerRelationship(
        SearchCusRelationshipRequest.builder().customerId(customerEntity.getRefCustomerId())
            .build());
    Map<Long, Relationship> mapRelatedCusIdAndRelationship = new HashMap<>();
    if (ObjectUtils.isNotEmpty(responseDetail) && ObjectUtils.isNotEmpty(responseDetail.getData())
        && ObjectUtils.isNotEmpty(responseDetail.getData().getRelationships())) {
      //Get map relatedCusId and relationship
      mapRelatedCusIdAndRelationship = responseDetail.getData()
          .getRelationships()
          .stream()
          .collect(Collectors.toMap(Relationship::getRelatedCustomerId, Function.identity()));
    }
    // xử lý relationship
    int customerRelationShipListOldLength = customerRelationShipListOld.size();
    for (int i = 0; i < customerRelationShipListOldLength; i++) {
      //create relationship
      CustomerRelationShipEntity customerRelationShipEntity = new CustomerRelationShipEntity();
      if (RB.equals(relatedCustomerCreated.get(i).getCustomerType())) {
        customerRelationShipEntity = createRelationshipByRB(customerEntity,
            mapRelatedCusIdAndRelationship, relatedCustomerCreated.get(i));
        customerRelationShipEntity.setPaymentGuarantee(customerRelationShipListOld.get(i).getPaymentGuarantee());
      } else if (EB.equals(relatedCustomerCreated.get(i).getCustomerType())) {
        customerRelationShipEntity = createRelationshipByEB(customerEntity,
            mapRelatedCusIdAndRelationship, relatedCustomerCreated.get(i));
        if(customerRelationShipEntity != null) {
          customerRelationShipEntity.setPaymentGuarantee(customerRelationShipListOld.get(i).getPaymentGuarantee());
        }
      }
      if (ObjectUtils.isNotEmpty(customerRelationShipEntity)) {
        customerEntity.getCustomerRelationShips().add(customerRelationShipEntity);
        // add value for customer mapping
        mapCustomerId.put(customerRelationShipListOld.get(i).getCustomerRefId(),
            relatedCustomerCreated.get(i).getId());
      }
    }
  }

  private CustomerRelationShipEntity createRelationshipByRB(CustomerEntity customerEntity,
      Map<Long, Relationship> mapRelatedCusIdAndRelationship,
      CustomerEntity relatedCustomerCreated) {
    CustomerRelationShipEntity customerRelationShipEntity = new CustomerRelationShipEntity();
    Relationship relationshipOfRelation = mapRelatedCusIdAndRelationship.get(
        relatedCustomerCreated.getRefCustomerId());
    //CusIdOfAccountInfo exist và relationship = 1 thì update, không thì để trống.
    if (ObjectUtils.isNotEmpty(relationshipOfRelation) && CollectionUtils.isNotEmpty(
        relationshipOfRelation.getRelationshipDetails())
        && relationshipOfRelation.getRelationshipDetails().size() == 1) {
      customerRelationShipEntity.setRelationship(
          relationshipOfRelation.getRelationshipDetails().get(0).getRelatedDetail());
      customerRelationShipEntity.setRelationshipValue(
          configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP)
              .get(customerRelationShipEntity.getRelationship()));
      customerRelationShipEntity.setRelationshipRefId(
          relationshipOfRelation.getRelationshipDetails().get(0).getId());
    }
    customerRelationShipEntity.setCustomerRefId(relatedCustomerCreated.getId());
    customerRelationShipEntity.setCustomer(customerEntity);

    return customerRelationShipEntity;
  }

  private CustomerRelationShipEntity createRelationshipByEB(CustomerEntity customerEntity,
      Map<Long, Relationship> mapRelatedCusIdAndRelationship,
      CustomerEntity relatedCustomerCreated) {
    CustomerRelationShipEntity customerRelationShipEntity = null;
    Relationship relationshipOfRelation = mapRelatedCusIdAndRelationship.get(
        relatedCustomerCreated.getRefCustomerId());
    //CusIdOfAccountInfo exist and relationship > 0 thì lấy phần tử đầu tiên (Toàn R5)
    if (ObjectUtils.isNotEmpty(relationshipOfRelation) && CollectionUtils.isNotEmpty(
        relationshipOfRelation.getRelationshipDetails())
        && relationshipOfRelation.getRelationshipDetails().size() > 0) {
      customerRelationShipEntity = new CustomerRelationShipEntity();
      customerRelationShipEntity.setRelationship(
          relationshipOfRelation.getRelationshipDetails().get(0).getRelatedDetail());
      customerRelationShipEntity.setRelationshipValue(
          configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP)
              .get(customerRelationShipEntity.getRelationship()));
      customerRelationShipEntity.setRelationshipRefId(
          relationshipOfRelation.getRelationshipDetails().get(0).getId());
      customerRelationShipEntity.setCustomerRefId(relatedCustomerCreated.getId());
      customerRelationShipEntity.setCustomer(customerEntity);
    }
    return customerRelationShipEntity;
  }

  /**
   * Add application for contact
   *
   * @param applicationEntity
   */
  private void addApplicationForContact(ApplicationEntity applicationEntity) {
    if (Objects.nonNull(applicationEntity.getContact())) {
      applicationEntity.getContact().forEach(contact -> contact.setApplication(applicationEntity));
    }
  }

  /**
   * Add application for income (salary, individual enterprise, other, rental, property business)
   * Add link customer transaction income Add customer id in incomes
   *
   * @param applicationEntity
   * @param mapCustomerId
   * @param mapRefCustomerIdAndCustomerInactive
   */
  private void addApplicationForIncomes(ApplicationEntity applicationEntity,
      Map<Long, Long> mapCustomerId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    if (Objects.nonNull(applicationEntity.getIncomes())) {
      applicationEntity.getIncomes().forEach(applicationIncome -> {
        applicationIncome.setApplication(applicationEntity);
        addCustomerIdForSalary(applicationIncome, mapCustomerId,
            mapRefCustomerIdAndCustomerInactive);
        addCustomerIdForIndividualEnterprise(applicationIncome, mapCustomerId,
            mapRefCustomerIdAndCustomerInactive);
        addCustomerIdForOtherIncome(applicationIncome, mapCustomerId,
            mapRefCustomerIdAndCustomerInactive);
        addCustomerIdForRentalIncome(applicationIncome, mapCustomerId,
            mapRefCustomerIdAndCustomerInactive);
        addCustomerIdForPropertyBusinessIncome(applicationIncome, mapCustomerId,
            mapRefCustomerIdAndCustomerInactive);
        addLinkDataForIncomeEvaluation(applicationIncome);
      });
    }
  }

  private void addLinkDataForIncomeEvaluation(ApplicationIncomeEntity applicationIncome) {
    if (Objects.nonNull(applicationIncome.getIncomeEvaluation()) && CollectionUtils.isNotEmpty(
        applicationIncome.getIncomeEvaluation().getTotalAssetIncomes())) {
      applicationIncome.getIncomeEvaluation().getTotalAssetIncomes()
          .forEach(totalAssetIncomeEntity ->
              totalAssetIncomeEntity.setIncomeEvaluation(applicationIncome.getIncomeEvaluation()));
    }
  }

  private void addCustomerIdForSalary(ApplicationIncomeEntity applicationIncome,
      Map<Long, Long> mapCustomerId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    if (Objects.nonNull(applicationIncome.getSalaryIncomes())) {
      applicationIncome.getSalaryIncomes().forEach(salaryIncome -> {
        //Set customer_id
        salaryIncome.setCustomerId(mapCustomerId.get(salaryIncome.getCustomerId()));
        if (mapRefCustomerIdAndCustomerInactive.containsKey(
            salaryIncome.getRefCustomerId())) { //Customer InActive
          salaryIncome.setIncomeOwnerName(
              getIncomeOwnerName(mapRefCustomerIdAndCustomerInactive.get(
                  salaryIncome.getRefCustomerId())));
          salaryIncome.setRefCustomerId(
              mapRefCustomerIdAndCustomerInactive.get(salaryIncome.getRefCustomerId())
                  .getRefCustomerId());
        }
      });
    }
  }

  private void addCustomerIdForIndividualEnterprise(ApplicationIncomeEntity applicationIncome,
      Map<Long, Long> mapCustomerId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    if (Objects.nonNull(applicationIncome.getIndividualEnterpriseIncomes())) {
      applicationIncome.getIndividualEnterpriseIncomes().forEach(individualEnterpriseIncome -> {
        //Set customer_id
        individualEnterpriseIncome.setCustomerId(
            mapCustomerId.get(individualEnterpriseIncome.getCustomerId()));
        if (mapRefCustomerIdAndCustomerInactive.containsKey(
            individualEnterpriseIncome.getRefCustomerId())) {//Customer InActive
          individualEnterpriseIncome.setIncomeOwnerName(
              getIncomeOwnerName(mapRefCustomerIdAndCustomerInactive.get(
                  individualEnterpriseIncome.getRefCustomerId())));
          individualEnterpriseIncome.setRefCustomerId(
              mapRefCustomerIdAndCustomerInactive.get(individualEnterpriseIncome.getRefCustomerId())
                  .getRefCustomerId());
        }
      });
    }
  }

  private void addCustomerIdForOtherIncome(ApplicationIncomeEntity applicationIncome,
      Map<Long, Long> mapCustomerId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    if (Objects.nonNull(applicationIncome.getOtherIncomes())) {
      applicationIncome.getOtherIncomes().forEach(otherIncome -> {
        //Set customer_id
        otherIncome.setCustomerId(mapCustomerId.get(otherIncome.getCustomerId()));
        if (mapRefCustomerIdAndCustomerInactive.containsKey(
            otherIncome.getRefCustomerId())) {//Customer InActive
          otherIncome.setIncomeOwnerName(getIncomeOwnerName(mapRefCustomerIdAndCustomerInactive.get(
              otherIncome.getRefCustomerId())));
          otherIncome.setRefCustomerId(
              mapRefCustomerIdAndCustomerInactive.get(otherIncome.getRefCustomerId())
                  .getRefCustomerId());
        }
      });
    }
  }

  private void addCustomerIdForRentalIncome(ApplicationIncomeEntity applicationIncome,
      Map<Long, Long> mapCustomerId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    if (Objects.nonNull(applicationIncome.getRentalIncomes())) {
      applicationIncome.getRentalIncomes().forEach(rentalIncome -> {
        //Set customer_id
        rentalIncome.setCustomerId(mapCustomerId.get(rentalIncome.getCustomerId()));
        if (mapRefCustomerIdAndCustomerInactive.containsKey(
            rentalIncome.getRefCustomerId())) {//Customer InActive
          rentalIncome.setIncomeOwnerName(
              getIncomeOwnerName(mapRefCustomerIdAndCustomerInactive.get(
                  rentalIncome.getRefCustomerId())));
          rentalIncome.setRefCustomerId(
              mapRefCustomerIdAndCustomerInactive.get(rentalIncome.getRefCustomerId())
                  .getRefCustomerId());
        }
      });
    }
  }

  private void addCustomerIdForPropertyBusinessIncome(ApplicationIncomeEntity applicationIncome,
      Map<Long, Long> mapCustomerId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    if (Objects.nonNull(applicationIncome.getPropertyBusinessIncomes())) {
      applicationIncome.getPropertyBusinessIncomes().forEach(propertyBusinessIncome -> {
        //Set customer_id
        propertyBusinessIncome.setCustomerId(
            mapCustomerId.get(propertyBusinessIncome.getCustomerId()));
        if (mapRefCustomerIdAndCustomerInactive.containsKey(
            propertyBusinessIncome.getRefCustomerId())) {//Customer InActive
          propertyBusinessIncome.setIncomeOwnerName(
              getIncomeOwnerName(mapRefCustomerIdAndCustomerInactive.get(
                  propertyBusinessIncome.getRefCustomerId())));
          propertyBusinessIncome.setRefCustomerId(
              mapRefCustomerIdAndCustomerInactive.get(propertyBusinessIncome.getRefCustomerId())
                  .getRefCustomerId());
        }
        if (Objects.nonNull(propertyBusinessIncome.getCustomerTransactionIncomes())) {
          propertyBusinessIncome.getCustomerTransactionIncomes().forEach(
              customerTransactionIncome -> customerTransactionIncome.setPropertyBusinessIncome(
                  propertyBusinessIncome));
        }
      });
    }
  }

  private String getIncomeOwnerName(CustomerEntity customerEntity) {
    return customerEntity.getIndividualCustomer()
        .fullName() + " " + customerEntity.getCustomerIdentitys().stream()
        .filter(identity -> identity.isPriority())
        .map(CustomerIdentityEntity::getIdentifierCode).findFirst().orElse(null);
  }

  /**
   * Add application for credits
   *
   * @param applicationEntity
   */
  private void addApplicationForCredits(ApplicationEntity applicationEntity) {
    if (Objects.nonNull(applicationEntity.getCredits())) {
      applicationEntity.getCredits().forEach(applicationCreditEntity -> {
        //add link for credit entity
        applicationCreditEntity.setApplication(applicationEntity);
        applicationCreditEntity.setApproveResult(ApprovalResult.Y.name());
        applicationCreditEntity.setApproveResultValue(ApprovalResult.Y.getValue());
        //add link for SubCard
        if (Objects.nonNull(applicationCreditEntity.getCreditCard()) && Objects.nonNull(
            applicationCreditEntity.getCreditCard().getSubCreditCards())) {
          applicationCreditEntity.getCreditCard().getSubCreditCards().forEach(
              subCreditCard -> subCreditCard.setApplicationCreditCard(
                  applicationCreditEntity.getCreditCard()));
        }
      });
    }
  }

  /**
   * Add application entity for appraisal content
   *
   * @param applicationEntity
   */
  private void addApplicationForAppraisalContent(ApplicationEntity applicationEntity) {
    if (Objects.nonNull(applicationEntity.getAppraisalContents())) {
      applicationEntity.getAppraisalContents().forEach(
          applicationIncomeEntity -> applicationIncomeEntity.setApplication(applicationEntity));
    }
  }

  /**
   * Add application entity for field info
   *
   * @param applicationEntity
   */
  private void addApplicationForFieldInfo(ApplicationEntity applicationEntity) {
    if (Objects.nonNull(applicationEntity.getFieldInformations())) {
      applicationEntity.getFieldInformations()
          .forEach(fieldInfo -> fieldInfo.setApplication(applicationEntity));
    }
  }

  /**
   * Add application entity for limit credit
   *
   * @param applicationEntity
   */
  private void addApplicationForLimitCredits(ApplicationEntity applicationEntity) {
    if (Objects.nonNull(applicationEntity.getLimitCredits())) {
      applicationEntity.getLimitCredits()
          .forEach(limitCredit -> limitCredit.setApplication(applicationEntity));
    }
  }

  /**
   * Copy checklist
   *
   * @param applicationEntity
   * @param oldBpmId
   */
  private void copyCheckList(ApplicationEntity applicationEntity, String oldBpmId) {
    log.info("Start Copy checklist from oldBpmId {} for new application {}", oldBpmId,
        applicationEntity.getBpmId());
    // Get check list for old application
    ChecklistBaseResponse<GroupChecklistDto> oldResponse = checklistService.getChecklistByRequestCode(
        oldBpmId);
    // Create new checklist for new application
    ChecklistBaseResponse<GroupChecklistDto> newResponse =
        (ChecklistBaseResponse<GroupChecklistDto>) checklistService.reloadChecklist(
            applicationEntity.getBpmId(), true);

    Map<String, List<FileDto>> mapCheckListAndFilesOfOldApplication = createMapCheckListAndFiles(
        oldResponse.getData().getListChecklist());
    Map<String, Long> mapCheckListAndOldId = new HashMap<>();
    createMapBetweenChecklistKeyAndOldId(mapCheckListAndOldId, applicationEntity);
    CreateChecklistRequest createChecklistRequest = buildUploadFileChecklistRequest(
        applicationEntity, newResponse.getData().getListChecklist(), mapCheckListAndOldId,
        mapCheckListAndFilesOfOldApplication);
    //Call API CHECKLIST
    checklistService.uploadFileChecklist(createChecklistRequest);

    log.info("End Copy checklist from oldBpmId {} for new application {}", oldBpmId,
        applicationEntity.getBpmId());
  }

  /**
   * Build upload file checklist request
   *
   * @param applicationEntity
   * @param listChecklist
   * @param mapCheckListAndOldId
   * @param mapCheckListAndFiles
   * @return
   */
  private CreateChecklistRequest buildUploadFileChecklistRequest(
      ApplicationEntity applicationEntity, List<ChecklistDto> listChecklist,
      Map<String, Long> mapCheckListAndOldId, Map<String, List<FileDto>> mapCheckListAndFiles) {
    CreateChecklistRequest request = new CreateChecklistRequest();
    request.setBusinessFlow(applicationEntity.getProcessFlow());
    request.setPhaseCode(PhaseCode.RM.name());
    request.setRequestCode(applicationEntity.getBpmId());
    request.setListChecklist(
        addFileInChecklist(listChecklist, mapCheckListAndOldId, mapCheckListAndFiles));
    return request;
  }

  /**
   * Add file for new check list of new application (only add list file is not null)
   *
   * @param listChecklist
   * @param mapCheckListAndNewId
   * @param listFileChecklistMap
   * @return
   */
  private List<ChecklistDto> addFileInChecklist(List<ChecklistDto> listChecklist,
      Map<String, Long> mapCheckListAndNewId, Map<String, List<FileDto>> listFileChecklistMap) {
    List<ChecklistDto> result = new ArrayList<>();
    if (Objects.nonNull(listChecklist)) {
      listChecklist.forEach(checklistDto -> {
        if (Boolean.FALSE.equals(checklistDto.getIsGenerated())
            && !checkCreditWorthinessForCustomer(checklistDto)) {
          String key = "";
          if (APPLICATION_TAG.equals(checklistDto.getDomainType())) {
            key = checklistDto.getGroupCode() + UNDER_SCORE + checklistDto.getCode() + UNDER_SCORE
                + mapCheckListAndNewId.get(
                APPLICATION_TAG + UNDER_SCORE + checklistDto.getDomainObjectId());
          } else if (INCOME_TAG.equals(checklistDto.getDomainType())) {
            //INCOME
            key = checklistDto.getGroupCode() + UNDER_SCORE + checklistDto.getCode() + UNDER_SCORE
                + mapCheckListAndNewId.get(
                checklistDto.getGroupCode() + UNDER_SCORE + checklistDto.getDomainObjectId());
          } else {
            key = checklistDto.getGroupCode() + UNDER_SCORE + checklistDto.getCode() + UNDER_SCORE
                + checklistDto.getDomainObjectId();
          }
          List<FileDto> listFile = listFileChecklistMap.get(key);
          if (Objects.nonNull(listFile)) {
            checklistDto.setListFile(listFile);
            result.add(checklistDto);
          }
        }
      });
    }
    return result;
  }

  /**
   * Check credit worthiness of customer
   *
   * @param checklistDto
   * @return
   */
  private boolean checkCreditWorthinessForCustomer(ChecklistDto checklistDto) {
    boolean result = false;
    if (CUSTOMER_TAG.equals(checklistDto.getDomainType())
        && ChecklistEnum.CREDITWORTHINESS.getCode().equals(checklistDto.getCode())) {
      result = true;
    }
    return result;
  }

  /**
   * Create map checklist and files. Ex(groupCode_code_domainObjectId, listFile) Only add list file
   * is not null and file created_by is not auto_generated
   *
   * @param listChecklist
   * @return
   */
  private Map<String, List<FileDto>> createMapCheckListAndFiles(List<ChecklistDto> listChecklist) {
    Map<String, List<FileDto>> result = new HashMap<>();
    if (Objects.nonNull(listChecklist)) {
      listChecklist.forEach(checklistDto -> {
        if (Boolean.FALSE.equals(checklistDto.getIsGenerated()) && Objects.nonNull(
            checklistDto.getListFile())) {
          List<FileDto> files = checklistDto.getListFile().stream()
              .filter(fileDto -> !AUTO_GENERATED.equalsIgnoreCase(fileDto.getCreatedBy()))
              .collect(Collectors.toList());
          result.put(
              checklistDto.getGroupCode() + UNDER_SCORE + checklistDto.getCode() + UNDER_SCORE
                  + checklistDto.getDomainObjectId(), files);
        }
      });
    }
    return result;
  }

  /**
   * Create a map checklist key and oldId for checklist mapping Note (application, income có id thay
   * đổi với mỗi hò sơ. Còn customer, asset, ... thì sẽ giữ nguyên id) Dùng để mapping ngược lại khi
   * tìm danh sách file
   *
   * @param mapCheckListKeyAndOldId
   * @param applicationEntity
   */
  private void createMapBetweenChecklistKeyAndOldId(Map<String, Long> mapCheckListKeyAndOldId,
      ApplicationEntity applicationEntity) {
    //Application format (APPLICATION_newId, oldId)
    mapCheckListKeyAndOldId.put(APPLICATION_TAG + UNDER_SCORE + applicationEntity.getId(),
        applicationEntity.getOldId());
    //Income
    addValueMapForIncome(mapCheckListKeyAndOldId, applicationEntity);
  }

  /**
   * Add value map for incomes. Example format (groupCode_newId, oldId)
   *
   * @param mapCheckListKeyAndOldId
   * @param applicationEntity
   */
  private void addValueMapForIncome(Map<String, Long> mapCheckListKeyAndOldId,
      ApplicationEntity applicationEntity) {
    if (Objects.nonNull(applicationEntity.getIncomes())) {
      applicationEntity.getIncomes().forEach(income -> {
        //Salary
        addValueMapForSalaryIncome(income, mapCheckListKeyAndOldId);
        //Individual enterprise
        addValueMapForIndividualOrEnterpriseIncome(income, mapCheckListKeyAndOldId);
        //other
        addValueMapForOtherIncome(income, mapCheckListKeyAndOldId);
        //Rental
        addValueMapForRentalIncome(income, mapCheckListKeyAndOldId);
        //Property Business
        addValueMapForPropertyBusinessIncome(income, mapCheckListKeyAndOldId);
      });
    }
  }

  private void addValueMapForSalaryIncome(ApplicationIncomeEntity income,
      Map<String, Long> mapCheckListKeyAndOldId) {
    if (Objects.nonNull(income.getSalaryIncomes())) {
      income.getSalaryIncomes().forEach(salary -> mapCheckListKeyAndOldId.put(
          INCOME_FROM_SALARY.getValue() + UNDER_SCORE + salary.getId(), salary.getOldId()));
    }
  }

  private void addValueMapForIndividualOrEnterpriseIncome(ApplicationIncomeEntity income,
      Map<String, Long> mapCheckListKeyAndOldId) {
    if (Objects.nonNull(income.getIndividualEnterpriseIncomes())) {
      income.getIndividualEnterpriseIncomes().forEach(individualEnterpriseIncome -> {
        String groupId =
            INDIVIDUAL_BUSINESS.equalsIgnoreCase(individualEnterpriseIncome.getIncomeType())
                ? INCOME_FROM_DN_HGD_HKD.getValue() : INCOME_FROM_ENTERPRISE.getValue();
        mapCheckListKeyAndOldId.put(groupId + UNDER_SCORE + individualEnterpriseIncome.getId(),
            individualEnterpriseIncome.getOldId());
      });
    }
  }

  private void addValueMapForOtherIncome(ApplicationIncomeEntity income,
      Map<String, Long> mapCheckListKeyAndOldId) {
    if (Objects.nonNull(income.getOtherIncomes())) {
      income.getOtherIncomes().forEach(otherIncome -> mapCheckListKeyAndOldId.put(
          INCOME_OTHER.getValue() + UNDER_SCORE + otherIncome.getId(), otherIncome.getOldId()));
    }
  }

  private void addValueMapForRentalIncome(ApplicationIncomeEntity income,
      Map<String, Long> mapCheckListKeyAndOldId) {
    if (Objects.nonNull(income.getRentalIncomes())) {
      income.getRentalIncomes().forEach(rentalIncome -> mapCheckListKeyAndOldId.put(
          INCOME_FROM_RENTAL_ASSET.getValue() + UNDER_SCORE + rentalIncome.getId(),
          rentalIncome.getOldId()));
    }
  }

  private void addValueMapForPropertyBusinessIncome(ApplicationIncomeEntity income,
      Map<String, Long> mapCheckListKeyAndOldId) {
    if (Objects.nonNull(income.getPropertyBusinessIncomes())) {
      income.getPropertyBusinessIncomes().forEach(
          propertyBusinessIncome -> mapCheckListKeyAndOldId.put(
              INCOME_FROM_PROPERTY_BUSINESS.getValue() + UNDER_SCORE
                  + propertyBusinessIncome.getId(), propertyBusinessIncome.getOldId()));
    }
  }

  /**
   * Set data branch of current user RM
   *
   * @param applicationEntity
   */
  private void addUserRMInfo(ApplicationEntity applicationEntity) {
    log.info("Copy application, add user RM {} for new application {}",
        applicationEntity.getAssignee(), applicationEntity.getBpmId());
    DataResponse regionAreaResp = userManagerClient.getRegionAreaByUserName(
        applicationEntity.getAssignee());

    if (Objects.isNull(regionAreaResp)) {
      log.error("Copy application, user RM {} have region & area invalid...",
          applicationEntity.getAssignee());
      throw new ApprovalException(DomainCode.ERR_GET_ORGANIZATION_BY_USER,
          new Object[]{applicationEntity.getAssignee()});
    }

    applicationEntity.setProcessingStep(
        messageSource.getMessage(MAKE_PROPOSAL.getValue(), null, Util.locale()));
    applicationEntity.setBusinessCode(regionAreaResp.getBusinessUnitDetail().getCode());
    applicationEntity.setBusinessUnit(regionAreaResp.getBusinessUnitDetail().getName());
    applicationEntity.setCreatedFullName(regionAreaResp.getCreatedFullName());
    applicationEntity.setCreatedPhoneNumber(regionAreaResp.getCreatedPhoneNumber());
    applicationEntity.setSaleCode(userManagementClient.getSaleCode());
    applicationEntity.setAreaCode(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ? regionAreaResp.getBusinessUnitDetail().getBusinessAreaCode() : "");
    applicationEntity.setArea(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ? regionAreaResp.getBusinessUnitDetail().getBusinessAreaFullName() : "");

    OrganizationTreeDetail region = regionAreaResp.getRegionDetail();
    log.info("Copy application, region : {}", JsonUtil.convertObject2String(region, objectMapper));
    if (region != null) {
      applicationEntity.setRegionCode(region.getCode());
      applicationEntity.setRegion(region.getFullName());
    }

    OrganizationTreeDetail branchDetail = Util.getTreeDetailByType("CN",
        regionAreaResp.getRegionDetail());
    log.info("Copy application, branch : {}",
        JsonUtil.convertObject2String(branchDetail, objectMapper));
    if (branchDetail != null) {
      applicationEntity.setBranchCode(branchDetail.getCode());
      applicationEntity.setBranchName(branchDetail.getFullName());
    }
  }

  /**
   * Set data credit conditions
   *
   * @param applicationEntity
   * @param copyCreditConditionEntities
   */
  public void addCreditConditions(ApplicationEntity applicationEntity,
      Set<ApplicationCreditConditionsEntity> copyCreditConditionEntities) {
    log.info("Copy credit conditions...");
    if (CollectionUtils.isEmpty(copyCreditConditionEntities)) {
      log.warn("Not found any credit condition with old application");
      return;
    }

    List<Long> ids = copyCreditConditionEntities.stream()
        .map(ApplicationCreditConditionsEntity::getCreditConditionId).collect(Collectors.toList());

    List<CreditConditionResponse> creditConditionResponses = creditConditionClient.getCreditConditionByListId(
        ids).getValue();

    if (CollectionUtils.isEmpty(creditConditionResponses)) {
      return;
    }

    Set<ApplicationCreditConditionsDTO> creditConditionsDTOs = new HashSet<>(
        ApplicationCreditConditionMapper.INSTANCE.toCreditConditions(creditConditionResponses));

    creditConditionsDTOs.forEach(item -> {
      item.setId(null);
      item.setCreditConditionId(null);
    });
    log.info("Copy credit condition request {}",
        JsonUtil.convertObject2String(creditConditionsDTOs, objectMapper));

    postDebtInfoService.referenceCreditConditionsEntities(applicationEntity, creditConditionsDTOs);
  }

  /**
   * Mapping asset data
   *
   * @param applicationEntity
   * @param oldBpmId
   * @param mapRefCustomerIdAndCustomerInactive
   */
  private Object mappingAssetInfo(ApplicationEntity applicationEntity, String oldBpmId,
      Map<Long, CustomerEntity> mapRefCustomerIdAndCustomerInactive) {
    log.info("Copy asset data...");
    AssetInfoResponse response = collateralClient.getCommonAssetInfo(oldBpmId,
        AssetBusinessType.BPM.getValue());

    if (response == null || response.getData() == null || CollectionUtils.isEmpty(
        response.getData().getAssetData())) {
      log.warn("Not found any asset data with old application {}", oldBpmId);
      return null;
    }

    List<AssetDataResponse> assetDataResponses = response.getData().getAssetData();
    assetDataResponses.forEach(item -> {
      if(StringUtils.isEmpty(item.getMortgageStatus())) {
        item.setMortgageStatus(MORTGAGE_STATUS_DEFAULT);
      }
      item.setApplicationId(applicationEntity.getBpmId());
      if (Objects.nonNull(item.getAssetAdditionalInfo()) && CollectionUtils.isNotEmpty(
          item.getAssetAdditionalInfo().getOwnerInfo())) {
        item.getAssetAdditionalInfo().getOwnerInfo().forEach(ownerInfoDTO -> {
          if (mapRefCustomerIdAndCustomerInactive.containsKey(ownerInfoDTO.getCustomerId())) {
            CustomerEntity customer = mapRefCustomerIdAndCustomerInactive.get(
                ownerInfoDTO.getCustomerId());
            ownerInfoDTO.setCustomerId(customer.getRefCustomerId());
            ownerInfoDTO.setCustomerRefCode(customer.getCustomerIdentitys().stream()
                .filter(identity -> identity.isPriority())
                .map(CustomerIdentityEntity::getIdentifierCode).findFirst().orElse(null));
            ownerInfoDTO.setCustomerName(customer.getIndividualCustomer().fullName());
            ownerInfoDTO.setCustomerType(customer.getCustomerType());
          }
        });
      }
    });

    BPMAssetRequest request = new BPMAssetRequest();
    request.setApplicationId(applicationEntity.getBpmId());
    request.setBusinessType(AssetBusinessType.BPM.getValue());
    request.setActionType(ActionType.COPY.getValue());
    request.setAssetData(assetDataResponses);

    PostAssetInfoRequest infoRequest = new PostAssetInfoRequest();
    infoRequest.setBpmId(applicationEntity.getBpmId());
    infoRequest.setCollateral(request);

    log.info("Copy asset data request {}", JsonUtil.convertObject2String(request, objectMapper));
    try {
      return callUpsertAssetCommon(infoRequest).getData();
    } catch (Exception e) {
      return infoRequest;
    }
  }

  /**
   * Mapping asset allocation
   *
   * @param applicationEntity
   * @param oldBpmId
   */
  public void mappingAssetAllocationInfo(ApplicationEntity applicationEntity, String oldBpmId) {
    log.info("Copy asset allocation...");
    CreditAssetAllocationResponse data = collateralClient.getAssetAllocationInfo(oldBpmId);
    if (data == null || CollectionUtils.isEmpty(data.getAssetAllocations())) {
      log.warn("Not found any asset allocation with old application {}", oldBpmId);
      return;
    }

    Map<Long, Long> creditMapId = applicationEntity.getCredits().stream().collect(
        Collectors.toMap(ApplicationCreditEntity::getOldId, ApplicationCreditEntity::getId));

    data.getAssetAllocations()
        .forEach(item -> item.getCreditAllocations().forEach(creditAllocation -> {
          creditAllocation.setId(null);
          creditAllocation.setCreditId(creditMapId.get(creditAllocation.getCreditId()));
        }));

    Map<Long, List<Long>> creditMapAssets = data.getAssets().stream()
        .collect(HashMap::new, (m, v) -> m.put(v.getCredit(), v.getAssets()), HashMap::putAll);

    // Set Asset for credit
    applicationEntity.getCredits().forEach(cr -> cr.setAssets(creditMapAssets.get(cr.getOldId())));

    Set<AssetAllocationResponse> assetAllocationResponses = new HashSet<>(
        data.getAssetAllocations());

    log.info("Copy asset allocation request {}",
        JsonUtil.convertObject2String(assetAllocationResponses, objectMapper));

    postDebtInfoService.referenceAssetAllocationInfo(applicationEntity,
        AssetAllocationMapper.INSTANCE.toAssetAllocationDTO(assetAllocationResponses));

    if (CollectionUtils.isNotEmpty(data.getAssets())) {
      List<CreditAssetRequest> creditAssetRequests = new ArrayList<>();
      data.getAssets().forEach(item -> {
        CreditAssetRequest req = new CreditAssetRequest();
        req.setCredit(creditMapId.get(item.getCredit()));
        req.setAssets(item.getAssets());
        creditAssetRequests.add(req);
      });

      log.info("Copy asset allocation request {}",
          JsonUtil.convertObject2String(creditAssetRequests, objectMapper));
      collateralClient.mappingCreditAssetInfo(applicationEntity.getBpmId(), creditAssetRequests);
    }
  }

  private AssetResponse callUpsertAssetCommon(PostAssetInfoRequest req) {
    String endpointUpsert = UriComponentsBuilder
        .fromUriString(configProperties.getBaseUrl() + configProperties.getEndpoint()
            .get(CollateralEndpoint.UPSERT_ASSET.getValue())
            .getUrl())
        .toUriString();
    log.info("START COPY call api upsert with uri: {}", endpointUpsert);
    AssetResponse response = collateralClient.callApiCommonAsset(req.getCollateral(),
        HttpMethod.PUT, endpointUpsert, AssetResponse.class);

    log.info("END COPY call api upsert asset info: {}",
        JsonUtil.convertObject2String(response, objectMapper));
    if (ObjectUtils.isEmpty(response) || ObjectUtils.isEmpty(response.getData())) {
      throw new ApprovalException(DomainCode.CMS_CREATE_ASSET_ERROR);
    }
    return response;
  }

  private void addAssetInfo2Draft(ApplicationEntity applicationEntityNew,
      Object assetResponse) {
    Set<ApplicationDraftEntity> draftEntities = applicationDraftRepository.findByBpmId(
            applicationEntityNew.getBpmId())
        .orElse(null);

    if (CollectionUtils.isNotEmpty(draftEntities)) {
      for (ApplicationDraftEntity draftEntity : draftEntities) {
        if (ASSET_INFO.equals(draftEntity.getTabCode())) {
          draftEntity.setTabCode(ASSET_INFO);
          draftEntity.setStatus(UNFINISHED);
          draftEntity.setBpmId(applicationEntityNew.getBpmId());
          if (ObjectUtils.isNotEmpty(assetResponse)) {
            AssetCommonInfoDTO assetInfo = objectMapper.convertValue(assetResponse,
                AssetCommonInfoDTO.class);
            assetInfo.setType(POST_ASSET_INFO);
            assetInfo.setBpmId(applicationEntityNew.getBpmId());
            draftEntity.setData(JsonUtil.convertObject2Bytes(assetInfo, objectMapper));
          }
          applicationDraftRepository.save(draftEntity);
          break;
        }
      }
    }
  }
}
