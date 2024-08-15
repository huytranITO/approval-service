package com.msb.bpm.approval.appr.service;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.AML_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.CARD;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LOAN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.OVERDRAFT;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.FINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.EXCHANGE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CUSTOMER_RELATIONS_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CUSTOMER_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.EB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ENTERPRISE_RELATIONS_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.NOT_ON_THE_LIST;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ON_THE_LIST;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.OPR_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.QUERY_ERROR_CODE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.RESULT_QUERY_ERROR;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.ASSET_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.BlockKey.OTHER_REVIEW;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.FIELD_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.OTHER_ADDITIONAL;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.SPECIAL_RISK;
import static com.msb.bpm.approval.appr.enums.application.ApprovalResult.Y;
import static com.msb.bpm.approval.appr.enums.application.DistributionForm.DF1;
import static com.msb.bpm.approval.appr.enums.application.DistributionForm.DF2;
import static com.msb.bpm.approval.appr.enums.application.GuaranteeForm.COLLATERAL;
import static com.msb.bpm.approval.appr.enums.application.GuaranteeForm.UNSECURE;
import static com.msb.bpm.approval.appr.enums.application.HDLDDocumentCode.hdldDocumentCodes;
import static com.msb.bpm.approval.appr.enums.application.HDLDProductCode.hdldProductCodes;
import static com.msb.bpm.approval.appr.enums.application.HDLDProductInfoCode.hdldProductInfoCodes;
import static com.msb.bpm.approval.appr.enums.application.TNRProductCode.tnrProductCodes;
import static com.msb.bpm.approval.appr.enums.application.TNRProductInfoCode.tnrProductInfoCodes;
import static com.msb.bpm.approval.appr.exception.DomainCode.REQUEST_TYPE_NOT_ACCEPTED;
import static java.util.stream.Collectors.groupingBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.asset.AssetClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.creditconditions.CreditConditionClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.config.cic.CICProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.email.EmailSender;
import com.msb.bpm.approval.appr.enums.application.AssetBusinessType;
import com.msb.bpm.approval.appr.enums.application.CustomerExtType;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.application.DistributionForm;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.cic.CICStatus;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.enums.email.ReasonCloseType;
import com.msb.bpm.approval.appr.enums.email.ReasonReturnType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.factory.ApplicationCreditFactory;
import com.msb.bpm.approval.appr.factory.ApplicationIncomeFactory;
import com.msb.bpm.approval.appr.factory.CustomerFactory;
import com.msb.bpm.approval.appr.mapper.AmlOprMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationAppraisalContentMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationContactMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditConditionMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditRatingsMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationFieldInformationMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationLimitCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationPhoneExpertiseMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationRepaymentMapper;
import com.msb.bpm.approval.appr.mapper.AssetInfoMapper;
import com.msb.bpm.approval.appr.mapper.CicMapper;
import com.msb.bpm.approval.appr.mapper.collateral.AssetAllocationMapper;
import com.msb.bpm.approval.appr.model.dto.ActuallyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO.AmlOprDtl;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO.AmlOprGeneral;
import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditConditionsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditLoanDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditOverdraftDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationLimitCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationPhoneExpertiseDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationPhoneExpertiseDTO.ApplicationPhoneExpertiseDtlDTO;
import com.msb.bpm.approval.appr.model.dto.AssetCommonInfoDTO;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO.CicDetail;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.DebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.ExchangeIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.FieldInforDTO;
import com.msb.bpm.approval.appr.model.dto.GetApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.InitializeInfoDTO;
import com.msb.bpm.approval.appr.model.dto.OtherReviewDTO;
import com.msb.bpm.approval.appr.model.dto.TotalLoanAmountDTO;
import com.msb.bpm.approval.appr.model.dto.cbt.ApplicationDataDTO;
import com.msb.bpm.approval.appr.model.dto.cbt.AssetDataDTO;
import com.msb.bpm.approval.appr.model.dto.cbt.CustomerDataDTO;
import com.msb.bpm.approval.appr.model.dto.cbt.EnterpriseDataDTO;
import com.msb.bpm.approval.appr.model.dto.cbt.GetApplicationDataDTO;
import com.msb.bpm.approval.appr.model.dto.cic.CicGroupDetail;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationAppraisalContentEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditConditionsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationExtraDataEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationPhoneExpertiseEntity;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.asset.AssetInforRequest;
import com.msb.bpm.approval.appr.model.request.cbt.SearchByCustomerRelations.CustomerInfo;
import com.msb.bpm.approval.appr.model.request.cbt.SearchByCustomerRelations.EnterpriseInfo;
import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import com.msb.bpm.approval.appr.model.request.data.PostFieldInformationRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import com.msb.bpm.approval.appr.model.response.asset.AssetInfoResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionClientResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionResponse;
import com.msb.bpm.approval.appr.repository.ApplicationCreditConditionRepository;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CICRepository;
import com.msb.bpm.approval.appr.repository.CustomerRelationshipRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.MathUtil;
import com.msb.bpm.approval.appr.util.StringUtil;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Getter
public abstract class AbstractBaseService {

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private ApplicationDraftRepository applicationDraftRepository;

  @Autowired
  private ApplicationCreditConditionRepository applicationCreditConditionRepository;

  @Autowired
  private CreditConditionClient creditConditionClient;

  @Autowired
  @Qualifier("objectMapperWithNull")
  private ObjectMapper objectMapper;

  @Autowired
  private EmailSender emailSender;
  @Autowired
  private CommonService commonService;
  @Autowired
  private UserManagerClient userManagerClient;
  @Autowired
  private ApplicationRepository applicationRepository;
  @Autowired
  private CICRepository cicRepository;

  @Autowired
  private CICProperties cicProperties;

  @Autowired
  private CollateralClient collateralClient;

  @Autowired
  private AssetAllocationMapper assetAllocationMapper;

  @Autowired
  private AssetClient assetClient;

  @Autowired
  private AssetInfoMapper assetInfoMapper;

  @Autowired
  private CustomerRelationshipRepository customerRelationshipRepository;

  /**
   * Get application info
   *
   * @param application ApplicationEntity
   * @return GetApplicationDTO
   */
  @Transactional(readOnly = true)
  public GetApplicationDTO getApplicationInfo(ApplicationEntity application) {
    Map<String, Object> customerData = buildCustomerAndRelationData(application.getCustomer());

    CicDTO cic = buildCicData(application.getCics());

    AmlOprDTO amlOpr = buildAmlOpr(application.getAmlOprs());

    Set<ApplicationIncomeDTO> incomes = buildIncomes(application.getIncomes());

    Map<String, Set<ApplicationAppraisalContentDTO>> appraisalContent = buildAppraisalContent(
        application.getAppraisalContents());

    Set<ApplicationCreditDTO> credits = buildCredits(application.getCredits());

    Set<OtherReviewDTO> otherReviews = buildExtraData(application.getExtraDatas(),
        new ImmutablePair<>(DEBT_INFO, OTHER_REVIEW), OtherReviewDTO.class);

    List<ApplicationCreditConditionsEntity> applicationCreditConditionExist = applicationCreditConditionRepository.findByApplicationIdOrderByCreditConditionId(
        application.getId());
    List<Long> listCreditConditionId = applicationCreditConditionExist.stream()
        .map(ApplicationCreditConditionsEntity::getCreditConditionId).filter(s -> s != null)
        .collect(Collectors.toList());
    List<ApplicationCreditConditionsDTO> creditConditions = new ArrayList<>();
    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(listCreditConditionId)) {
      CreditConditionClientResponse<List<CreditConditionResponse>> clientCreditConditionReponse = creditConditionClient.getCreditConditionByListId(
          listCreditConditionId);
      List<ApplicationCreditConditionsDTO> listApplicationCreditConditionDto = ApplicationCreditConditionMapper.INSTANCE.toCreditConditions(
          clientCreditConditionReponse.getValue());
      creditConditions = listApplicationCreditConditionDto.stream()
          .map(s -> mappingIdApplicationCreditCondition(s, applicationCreditConditionExist))
          .sorted(Comparator.comparing(ApplicationCreditConditionsDTO::getCreditConditionId))
          .collect(Collectors.toList());
    }
    AssetCommonInfoDTO assetData = new AssetCommonInfoDTO();
    assetData.setCompleted(Boolean.TRUE);
    return new GetApplicationDTO()
        .withInitializeInfo(new InitializeInfoDTO()
            .withApplication(ApplicationMapper.INSTANCE.toDTO(application))
            .withCustomerAndRelationPerson(new CustomerAndRelationPersonDTO()
                .withCustomer((CustomerDTO) customerData.get(CUSTOMER_TAG))
                .withCustomerRelations((Set<CustomerDTO>) customerData.get(CUSTOMER_RELATIONS_TAG))
                .withEnterpriseRelations(
                    (Set<CustomerDTO>) customerData.get(ENTERPRISE_RELATIONS_TAG))
                .withApplicationContact(
                    ApplicationContactMapper.INSTANCE.entityToDTO(application.getContact()))
                .withCic(cic)
                .withAmlOpr(amlOpr))
            .withIncomes(incomes))
        .withFieldInfor(new FieldInforDTO()
            .withFieldInformations(ApplicationFieldInformationMapper.INSTANCE.toFieldInformations(
                application.getFieldInformations())))
        .withDebtInfo(new DebtInfoDTO()
            .withCredits(credits)
            .withCreditRatings(buildCreditRatings(application.getCreditRatings()))
            .withLimitCredits(
                ApplicationLimitCreditMapper.INSTANCE.toLimitCredits(application.getLimitCredits()))
            .withRepayment(ApplicationRepaymentMapper.INSTANCE.toRepayment(application))
            .withSpecialRiskContents(appraisalContent.get(SPECIAL_RISK.name()))
            .withAdditionalContents(appraisalContent.get(OTHER_ADDITIONAL.name()))
            .withCreditConditions(creditConditions)
            .withOtherReviews(otherReviews)
            .withPhoneExpertise(buildPhoneExpertise(application.getPhoneExpertises()))
            .withEffectivePeriod(application.getEffectivePeriod())
            .withEffectivePeriodUnit(application.getEffectivePeriodUnit())
            .withProposalApprovalPosition(application.getProposalApprovalPosition())
            .withLoanApprovalPosition(application.getLoanApprovalPosition())
            .withLoanApprovalPositionValue(application.getLoanApprovalPositionValue())
            .withApplicationAuthorityLevel(application.getApplicationAuthorityLevel())
            .withPriorityAuthority(application.getPriorityAuthority()))
        .withAssetInfo(assetData);
  }

  @Transactional
  public GetApplicationDataDTO getCusIdAndVersionList(ApplicationEntity application) {
    GetApplicationDataDTO applicationDTO = new GetApplicationDataDTO();
    buildCustomerInfo(applicationDTO, application);
    buildCustomerRelationship(applicationDTO, application);
    buildAssetInfo(applicationDTO, application);
    buildApplicationInfo(applicationDTO, application);
    return applicationDTO;
  }

  private void buildApplicationInfo(GetApplicationDataDTO applicationDTO, ApplicationEntity application) {
    ApplicationDataDTO applicationDataDTO = new ApplicationDataDTO();
    applicationDataDTO.setProcessFlow(StringUtil.getValue(application.getProcessFlow()));
    applicationDataDTO.setProcessFlowValue(StringUtil.getValue(application.getProcessFlowValue()));
    applicationDataDTO.setSubmissionPurpose(StringUtil.getValue(application.getSubmissionPurpose()));
    applicationDataDTO.setSubmissionPurposeValue(StringUtil.getValue(application.getSubmissionPurposeValue()));
    applicationDataDTO.setRm(StringUtil.getValue(application.getCreatedBy()));
    applicationDataDTO.setArea(StringUtil.getValue(application.getAreaCode()));
    applicationDataDTO.setAreaValue(StringUtil.getValue(application.getArea()));
    applicationDataDTO.setRegion(StringUtil.getValue(application.getRegionCode()));
    applicationDataDTO.setRegionValue(StringUtil.getValue(application.getRegion()));
    applicationDataDTO.setSegment(application.getSegment());
    applicationDataDTO.setBusinessUnit(application.getBusinessCode());
    applicationDataDTO.setBusinessUnitValue(application.getBusinessUnit());
    if (!CollectionUtils.isEmpty(application.getCredits())) {
      List<String> documentCode = application.getCredits().stream().map(ApplicationCreditEntity::getDocumentCode).collect(
          Collectors.toList());
      applicationDataDTO.setDocumentCode(documentCode);
    }
    applicationDataDTO.setPartnerCode(application.getPartnerCode());
    applicationDTO.setApplicationInfo(applicationDataDTO);
  }


  private void buildCustomerInfo(GetApplicationDataDTO applicationDTO, ApplicationEntity application) {
    CustomerEntity customer = application.getCustomer();
    applicationDTO.setCustomerInfo(CustomerDataDTO.builder()
        .id(customer.getRefCustomerId())
        .version(customer.getVersion())
        .build());
  }

  private void buildCustomerRelationship(GetApplicationDataDTO applicationDTO, ApplicationEntity application) {
    Set<CustomerRelationShipEntity> relations = application.getCustomer().getCustomerRelationShips();

    List<CustomerDataDTO> relationshipList = new ArrayList<>();
    List<EnterpriseDataDTO> enterpriseList = new ArrayList<>();
    Set<Long> ids = relations.stream().map(CustomerRelationShipEntity::getCustomerRefId).collect(
        Collectors.toSet());

    if (!CollectionUtils.isEmpty(ids)) {
      Optional<Set<CustomerEntity>> collection = customerRepository.findByIdIn(ids);
      if (collection.isPresent() && collection.get().size() > 0) {
        collection.get().stream().forEach(item -> {
          if (item.getCustomerType().equalsIgnoreCase(RB)) {
            relationshipList.add(CustomerDataDTO.builder()
                .id(item.getRefCustomerId())
                .idSrc(item.getId())
                .version(item.getVersion())
                .build());
          } else {
            enterpriseList.add(EnterpriseDataDTO.builder()
                .id(item.getRefCustomerId())
                .idSrc(item.getId())
                .version(item.getVersion())
                .build());
          }
        });
      }

      relationshipList.forEach(relation -> {
        relation.setRelationIds(application.getCustomer().getCustomerRelationShips().stream().filter(
            object-> relation.getIdSrc().equals(object.getCustomerRefId())).map(CustomerRelationShipEntity::getRelationshipRefId).collect(
            Collectors.toList()));
      });

      enterpriseList.forEach(relation -> {
        relation.setRelationIds(application.getCustomer().getCustomerRelationShips().stream().filter(
            object-> relation.getIdSrc().equals(object.getCustomerRefId())).map(CustomerRelationShipEntity::getRelationshipRefId).collect(
            Collectors.toList()));
      });


    }

    applicationDTO.setRelationshipInfo(relationshipList);
    applicationDTO.setEnterpriseInfo(enterpriseList);
  }

  private void buildAssetInfo(GetApplicationDataDTO applicationDTO, ApplicationEntity application) {

    AssetInfoResponse commonAssetInfo = collateralClient.getCommonAssetInfo(application.getBpmId(), "BPM");

    List<AssetDataDTO> assetInfo = new ArrayList<>();
      if (!CollectionUtils.isEmpty(commonAssetInfo.getData().getAssetData())) {
          commonAssetInfo.getData().getAssetData().stream().forEach(it -> {
              AssetDataDTO assetDataDTO = AssetDataDTO.builder()
                      .id(it.getId())
                      .build();
              if (it.getAssetVersion() != null) {
                  assetDataDTO.setVersion(Integer.parseInt(it.getAssetVersion()));
              }
              assetInfo.add(assetDataDTO);
          });
      }

    if (assetInfo.size() > 0) {
      applicationDTO.setAssetInfo(assetInfo);
    }

  }


  private ApplicationCreditConditionsDTO mappingIdApplicationCreditCondition(
      ApplicationCreditConditionsDTO dto,
      List<ApplicationCreditConditionsEntity> applicationCreditConditionExist) {
    ApplicationCreditConditionsEntity entity = applicationCreditConditionExist.stream()
        .filter(e -> Objects.equals(
            e.getCreditConditionId(), dto.getCreditConditionId())).findFirst().orElse(null);
    if (entity != null) {
      dto.setId(entity.getId());
    }
    return dto;
  }


  /**
   * Build customer data (include main customer + relations)
   *
   * @param customerEntity CustomerEntity
   * @return Map<String, Object>
   */
  @Transactional(readOnly = true)
  public Map<String, Object> buildCustomerAndRelationData(CustomerEntity customerEntity) {
    CustomerDTO customer = CustomerFactory.getCustomer(customerEntity.getCustomerType())
        .build(customerEntity);
    Map<String, Set<CustomerDTO>> customerRelationMap = getCustomerRelations(
        customerEntity.getCustomerRelationShips());

    Map<String, Object> response = new HashMap<>();
    response.put(CUSTOMER_TAG, customer);
    response.put(CUSTOMER_RELATIONS_TAG, customerRelationMap.get(RB));
    response.put(ENTERPRISE_RELATIONS_TAG, customerRelationMap.get(EB));

    return response;
  }

  @Transactional(readOnly = true)
  public Map<String, Object> buildCustomerDataForCBT(CustomerEntity customerEntity, Long id, Integer version, Set<Long> ids) {
    CustomerDTO customer = CustomerFactory.getCustomer(customerEntity.getCustomerType())
        .build(customerEntity);

    if (!customer.getRefCustomerId().equals(id) || !customer.getVersion().equals(version)) {
      throw new ApprovalException(DomainCode.ERROR_CUSTOMER_VERSION_NOT_FOUND);
    }

    Map<String, Object> response = new HashMap<>();
    response.put(CUSTOMER_TAG, customer);
    ids.add(customer.getId());
    return response;
  }

  @Transactional
  public Map<String, Object> buildRelationDataForCBT(List<CustomerInfo> relations, List<EnterpriseInfo> enterpriseInfos, ApplicationEntity application, Set<Long> relationIds) {
    Map<String, Object> responses = new HashMap<>();
    Set<CustomerRelationShipEntity> customerRelationships = application.getCustomer().getCustomerRelationShips();

    if (CollectionUtils.isEmpty(customerRelationships)) {
      return null;
    }

    Map<Long, Pair<String, String>> relationshipMap = customerRelationships
        .stream()
        .collect(Collectors.toMap(CustomerRelationShipEntity::getCustomerRefId, relation ->
            new ImmutablePair<>(relation.getRelationship(), relation.getRelationshipValue())));

    Map<Long, Pair<String, Long>> relationshipIdMap = customerRelationships
        .stream()
        .collect(Collectors.toMap(CustomerRelationShipEntity::getCustomerRefId, relation ->
            new ImmutablePair<>(relation.getRelationship(), relation.getRelationshipRefId())));

    Set<CustomerEntity> customerRelations = customerRepository.findByIdIn(relationshipMap.keySet())
        .orElse(Collections.emptySet());

    Set<CustomerDTO> customerRelationMapping = new HashSet<>();
    customerRelations.forEach(cus -> {
      CustomerDTO customerMapping = CustomerFactory.getCustomer(cus.getCustomerType()).build(cus);
      customerMapping.setRelationship(relationshipMap.get(customerMapping.getId()).getKey());
      customerMapping.setRelationshipValue(relationshipMap.get(customerMapping.getId()).getValue());
      customerMapping.setRelationshipRefId(relationshipIdMap.get(customerMapping.getId()).getValue());
      relationIds.add(cus.getId());
      customerRelationMapping.add(customerMapping);
    });

    Map<String, Set<CustomerDTO>> customerRelationMap = customerRelationMapping
        .stream()
        .collect(groupingBy(CustomerDTO::getCustomerType, HashMap::new,
            Collectors.toCollection(HashSet::new)));


    Set<CustomerDTO> mappingEnterpriseMap = new HashSet<>();
    if (Objects.nonNull(enterpriseInfos)) {
      Map<Long, Integer> enterpriseCollection = enterpriseInfos.stream().collect(Collectors.toMap(EnterpriseInfo::getId, EnterpriseInfo::getVersion));
        Set<CustomerDTO> enterpriseCustomerDTO = customerRelationMap.get(EB);
        enterpriseCustomerDTO.stream().forEach(enterprise -> {
          if (enterpriseCollection.containsKey(enterprise.getRefCustomerId()) && enterpriseCollection.get(enterprise.getRefCustomerId()).equals(enterprise.getVersion())) {
            mappingEnterpriseMap.add(enterprise);
          }
        });
    }

    Map<Long, Integer> relationCollection = relations.stream().collect(Collectors.toMap(CustomerInfo::getId, CustomerInfo::getVersion));
    Set<CustomerDTO> mappingRelationMap = new HashSet<>();
    Set<CustomerDTO> rbCustomerDTO = customerRelationMap.get(RB);
    if (relations.size() > 0 && !rbCustomerDTO.isEmpty()) {
      rbCustomerDTO.stream().forEach(customer -> {
        if (relationCollection.containsKey(customer.getRefCustomerId()) && relationCollection.get(customer.getRefCustomerId()).equals(customer.getVersion())) {
          mappingRelationMap.add(customer);
        }
      });
    }

    responses.put(CUSTOMER_TAG, mappingRelationMap);
    responses.put(ENTERPRISE_RELATIONS_TAG, mappingEnterpriseMap);
    return responses;
  }


  /**
   * Get customer relations
   *
   * @param customerRelationships Set<CustomerRelationShipEntity>
   * @return Map<String, Set < CustomerDTO>
   */
  @Transactional(readOnly = true)
  public Map<String, Set<CustomerDTO>> getCustomerRelations(
      Set<CustomerRelationShipEntity> customerRelationships) {
    if (CollectionUtils.isEmpty(customerRelationships)) {
      return Collections.emptyMap();
    }

    Map<Long, Pair<String, String>> relationshipMap = customerRelationships
        .stream()
        .collect(Collectors.toMap(CustomerRelationShipEntity::getCustomerRefId, relation ->
            new ImmutablePair<>(relation.getRelationship(), relation.getRelationshipValue())));
    Set<CustomerEntity> customerRelations = customerRepository.findByIdIn(relationshipMap.keySet())
        .orElse(Collections.emptySet());

    Set<CustomerDTO> customerRelationMapping = new HashSet<>();
    customerRelations.forEach(cus -> {
      CustomerDTO customerMapping = CustomerFactory.getCustomer(cus.getCustomerType()).build(cus);
      customerMapping.setRelationship(relationshipMap.get(customerMapping.getId()).getKey());
      customerMapping.setRelationshipValue(relationshipMap.get(customerMapping.getId()).getValue());
      List<CustomerRelationShipEntity> tmp = customerRelationships.stream().filter(rl -> rl.getCustomerRefId().equals(cus.getId()))
              .collect(Collectors.toList());
      if(ObjectUtils.isNotEmpty(tmp)) {
        customerMapping.setRelationshipRefId(tmp.get(0).getRelationshipRefId());
        customerMapping.setPaymentGuarantee(tmp.get(0).getPaymentGuarantee());
      }
      if(!ObjectUtils.isEmpty(cus.getCustomerIdentitys())
              && EB.equals(cus.getCustomerType())) {
        List<CustomerIdentityEntity> identityDTOS = cus.getCustomerIdentitys().stream().filter(id -> id.isPriority()).collect(Collectors.toList());
        if(ObjectUtils.isNotEmpty(identityDTOS)) {
          customerMapping.setIssuedAt(identityDTOS.stream().findFirst().get().getIssuedAt());
          customerMapping.setIssuedBy(identityDTOS.stream().findFirst().get().getIssuedBy());
          customerMapping.setDocumentType(identityDTOS.stream().findFirst().get().getDocumentType());
          customerMapping.setIssuedPlace(identityDTOS.stream().findFirst().get().getIssuedPlace());
          customerMapping.setIssuedPlaceValue(identityDTOS.stream().findFirst().get().getIssuedPlaceValue());
        }
      }
      customerRelationMapping.add(customerMapping);
    });

    return customerRelationMapping
        .stream()
        .collect(groupingBy(CustomerDTO::getCustomerType, HashMap::new,
            Collectors.toCollection(HashSet::new)));
  }




  /**
   * Build CIC data
   *
   * @param cics Set<CicEntity>
   * @return CicDTO
   */
  public CicDTO buildCicData(Set<CicEntity> cics) {
    if (CollectionUtils.isEmpty(cics)) {
      return null;
    }

    CicDTO cic = new CicDTO();
    cics.stream().findFirst().ifPresent(c -> cic.setExplanation(null == c.getExplanation() ? ""
        : c.getExplanation()));
    cic.setCicDetails(CicMapper.INSTANCE.toCicDetails(cics));
    cic.setCicGroupDetails(cicGroupDetails(cics));
    return cic;
  }

  protected Set<CicGroupDetail> cicGroupDetails(Collection<CicEntity> cics) {
    Set<CicGroupDetail> result = new HashSet<>();

    Map<Long, CicGroupDetail> cicDetailMap = new HashMap<>();
    Map<Long, Set<String>> customerMap = new HashMap<>();
    Map<String, CicGroupDetail> cicEBDetailMap = new HashMap<>();

    for (CicEntity cicEntity : cics) {
      if (CustomerType.isEB(cicEntity.getCustomerType())) {
        CicGroupDetail tmp = CicMapper.INSTANCE.toCicDetail(cicEntity);
        if(!cicEBDetailMap.containsKey(cicEntity.getIdentifierCode())){
          cicEBDetailMap.put(cicEntity.getIdentifierCode(), tmp);
        } else {
          tmp = cicEBDetailMap.get(cicEntity.getIdentifierCode());
          statisticCicData(tmp, cicEntity);
        }
        continue;
      }
      Long refCustomerId = cicEntity.getRefCustomerId();
      String cicCode = cicEntity.getCicCode() + cicEntity.getProductCode();

      CicGroupDetail cicDetail = cicDetailMap.get(refCustomerId);
      if (cicDetail == null) {
        cicDetail = CicMapper.INSTANCE.toCicDetail(cicEntity);
        cicDetailMap.put(refCustomerId, cicDetail);
        customerMap.put(refCustomerId, new HashSet<>(Arrays.asList(cicCode)));
      } else if (!customerMap.get(refCustomerId).contains(cicCode)) {
        statisticCicData(cicDetail, cicEntity);
        customerMap.get(refCustomerId).add(cicCode);
      }
    }
    result.addAll(cicDetailMap.values());
    if(ObjectUtils.isNotEmpty(cicEBDetailMap)) result.addAll(cicEBDetailMap.values());
    return result;
  }

  protected Set<CicGroupDetail> cicGroupDetailsFromApplicationDraft(
      Collection<CicDetail> cicDetails) {
    Set<CicGroupDetail> result = new HashSet<>();

    Map<Long, CicGroupDetail> cicDetailMap = new HashMap<>();
    Map<Long, Set<String>> customerMap = new HashMap<>();
    Map<String, CicGroupDetail> cicEBDetailMap = new HashMap<>();

    for (CicDetail cicDetail : cicDetails) {
      if (CustomerType.isEB(cicDetail.getCustomerType())) {
        CicGroupDetail tmp = CicMapper.INSTANCE.toCicDetail(cicDetail);
        if(!cicEBDetailMap.containsKey(cicDetail.getIdentifierCode())){
          cicEBDetailMap.put(cicDetail.getIdentifierCode(), tmp);
        } else {
          tmp = cicEBDetailMap.get(cicDetail.getIdentifierCode());
          statisticCicData(tmp, cicDetail);
        }
        continue;
      }
      Long refCustomerId = cicDetail.getRefCustomerId();
      String cicCode = cicDetail.getCicCode() + cicDetail.getProductCode();

      CicGroupDetail cicGroupDetail = cicDetailMap.get(refCustomerId);
      if (cicGroupDetail == null) {
        cicGroupDetail = CicMapper.INSTANCE.toCicDetail(cicDetail);
        cicDetailMap.put(refCustomerId, cicGroupDetail);
        customerMap.put(refCustomerId, new HashSet<>(Arrays.asList(cicCode)));
      } else if (!customerMap.get(refCustomerId).contains(cicCode)) {
        statisticCicData(cicGroupDetail, cicDetail);
        customerMap.get(refCustomerId).add(cicCode);
      }
    }
    result.addAll(cicDetailMap.values());
    if(ObjectUtils.isNotEmpty(cicEBDetailMap)) result.addAll(cicEBDetailMap.values());
    return result;
  }

  private void statisticCicData(CicGroupDetail cicGroupDetail, CicDetail cicDetail) {
    cicGroupDetail.setStatusCode(
        CICStatus.comparePriority(cicGroupDetail.getStatusCode(), cicDetail.getStatusCode()) == -1 ?
            cicDetail.getStatusCode() : cicGroupDetail.getStatusCode());
    if (!CICStatus.isHaveData(cicDetail.getStatusCode())) {
      return;
    }
    if (StringUtil.isLessThan(cicGroupDetail.getDeftGroupCurrent(),
        cicDetail.getDeftGroupCurrent())) {
      cicGroupDetail.setDeftGroupCurrent(cicDetail.getDeftGroupCurrent());
    }

    if (StringUtil.isLessThan(cicGroupDetail.getDeftGroupLast12(),
        cicDetail.getDeftGroupLast12())) {
      cicGroupDetail.setDeftGroupLast12(cicDetail.getDeftGroupLast12());
    }

    if (StringUtil.isLessThan(cicGroupDetail.getDeftGroupLast24(),
        cicDetail.getDeftGroupLast24())) {
      cicGroupDetail.setDeftGroupLast24(cicDetail.getDeftGroupLast24());
    }

    cicGroupDetail.setTotalLoanCollateral(MathUtil.add(cicGroupDetail.getTotalLoanCollateral(),
        cicDetail.getTotalLoanCollateral()));

    cicGroupDetail.setTotalLoanCollateralUSD(
        MathUtil.add(cicGroupDetail.getTotalLoanCollateralUSD(),
            cicDetail.getTotalLoanCollateralUSD()));

    cicGroupDetail.setTotalUnsecureLoan(MathUtil.add(cicGroupDetail.getTotalUnsecureLoan(),
        cicDetail.getTotalUnsecureLoan()));

    cicGroupDetail.setTotalUnsecureLoanUSD(MathUtil.add(cicGroupDetail.getTotalUnsecureLoanUSD(),
        cicDetail.getTotalUnsecureLoanUSD()));

    cicGroupDetail.setTotalCreditCardLimit(MathUtil.add(cicGroupDetail.getTotalCreditCardLimit(),
        cicDetail.getTotalCreditCardLimit()));

    cicGroupDetail.setTotalDebtCardLimit(MathUtil.add(cicGroupDetail.getTotalDebtCardLimit(),
        cicDetail.getTotalDebtCardLimit()));
  }


  private void statisticCicData(CicGroupDetail cicGroupDetail, CicEntity cicEntity) {
    cicGroupDetail.setStatusCode(
        CICStatus.comparePriority(cicGroupDetail.getStatusCode(), cicEntity.getStatusCode()) == -1 ?
            cicEntity.getStatusCode() : cicGroupDetail.getStatusCode());
    if (!CICStatus.isHaveData(cicEntity.getStatusCode())) {
      return;
    }
    if (StringUtil.isLessThan(cicGroupDetail.getDeftGroupCurrent(),
        cicEntity.getDeftGroupCurrent())) {
      cicGroupDetail.setDeftGroupCurrent(cicEntity.getDeftGroupCurrent());
    }

    if (StringUtil.isLessThan(cicGroupDetail.getDeftGroupLast12(),
        cicEntity.getDeftGroupLast12())) {
      cicGroupDetail.setDeftGroupLast12(cicEntity.getDeftGroupLast12());
    }

    if (StringUtil.isLessThan(cicGroupDetail.getDeftGroupLast24(),
        cicEntity.getDeftGroupLast24())) {
      cicGroupDetail.setDeftGroupLast24(cicEntity.getDeftGroupLast24());
    }

    cicGroupDetail.setTotalLoanCollateral(MathUtil.add(cicGroupDetail.getTotalLoanCollateral(),
        cicEntity.getTotalLoanCollateral()));

    cicGroupDetail.setTotalLoanCollateralUSD(
        MathUtil.add(cicGroupDetail.getTotalLoanCollateralUSD(),
            cicEntity.getTotalLoanCollateralUSD()));

    cicGroupDetail.setTotalUnsecureLoan(MathUtil.add(cicGroupDetail.getTotalUnsecureLoan(),
        cicEntity.getTotalUnsecureLoan()));

    cicGroupDetail.setTotalUnsecureLoanUSD(MathUtil.add(cicGroupDetail.getTotalUnsecureLoanUSD(),
        cicEntity.getTotalUnsecureLoanUSD()));

    cicGroupDetail.setTotalCreditCardLimit(MathUtil.add(cicGroupDetail.getTotalCreditCardLimit(),
        cicEntity.getTotalCreditCardLimit()));

    cicGroupDetail.setTotalDebtCardLimit(MathUtil.add(cicGroupDetail.getTotalDebtCardLimit(),
        cicEntity.getTotalDebtCardLimit()));
  }

  /**
   * Build AML, OPRISK Data
   *
   * @param amlOprs Set<AmlOprEntity>
   * @return AmlOprDTO
   */
  @Transactional(readOnly = true)
  public AmlOprDTO buildAmlOpr(Set<AmlOprEntity> amlOprs) {
    if (CollectionUtils.isEmpty(amlOprs)) {
      return null;
    }

    Set<AmlOprDtl> amlOprMappings = AmlOprMapper.INSTANCE.toAmlOprs(amlOprs);
    Map<Pair<String, String>, List<AmlOprDtl>> amlOprGroupByIdentifierCode = amlOprMappings.stream()
            .filter(amlOprDtl -> !Objects.equals(amlOprDtl.getQueryType(), ApplicationConstant.OPR_ASSET_TAG))
        .collect(groupingBy(
            amlOprDtl -> new ImmutablePair<>(amlOprDtl.getSubject(), amlOprDtl.getIdentifierCode()),
            HashMap::new, Collectors.toCollection(ArrayList::new)));

    Set<AmlOprGeneral> generals = new HashSet<>();
    Set<AmlOprGeneral> groupGenerals = new HashSet<>();

    amlOprGroupByIdentifierCode.forEach((k, v) -> {
      AmlOprGeneral general = new AmlOprGeneral().withSubject(k.getKey())
          .withIdentifierCode(k.getValue());
      v.forEach(data -> {
        if (AML_TAG.equalsIgnoreCase(data.getQueryType())) {
          general.setAmlId(data.getId());
          general.setAmlResult(data.getResultDescription());
        } else if (OPR_TAG.equalsIgnoreCase(data.getQueryType())) {
          general.setOprId(data.getId());
          general.setOprResult(data.getResultDescription());
        }
        general.setCustomerId(data.getCustomerId());
        general.setOrderDisplay(data.getOrderDisplay());
        general.setRefCustomerId(data.getRefCustomerId());
        general.setPriority(data.isPriority());
      });
      generals.add(general);

      buildGroupAmlOprGeneral(groupGenerals, amlOprMappings, general);
    });

    Map<String, Set<AmlOprDtl>> amlOprDtlMap = amlOprMappings
        .stream()
        .collect(groupingBy(AmlOprDtl::getQueryType, HashMap::new,
            Collectors.toCollection(HashSet::new)));

    LocalDateTime firstTimeQuery = amlOprMappings.stream().map(AmlOprDtl::getUpdatedAt)
        .max(LocalDateTime::compareTo).get();

    return new AmlOprDTO()
        .withGroupGenerals(groupGenerals)
        .withGenerals(generals)
        .withFirstTimeQuery(firstTimeQuery)
        .withAmls(amlOprDtlMap.get(AML_TAG))
        .withOprs(amlOprDtlMap.get(OPR_TAG));
  }

  private void buildGroupAmlOprGeneral(Set<AmlOprGeneral> groupGenerals, Set<AmlOprDtl> amlOprDtls,
      AmlOprGeneral general) {
    if (!general.isPriority() && !CustomerExtType.ENTERPRISE.getValue()
        .equals(general.getSubject())) {
      return;
    }

    AmlOprGeneral groupGeneral = new AmlOprGeneral();
    BeanUtils.copyProperties(general, groupGeneral);

    setAmlResult(amlOprDtls, groupGeneral);
    setOprResult(amlOprDtls, groupGeneral);

    groupGenerals.add(groupGeneral);
  }

  private void setAmlResult(Set<AmlOprDtl> amlOprDtls, AmlOprGeneral groupGeneral) {
    if (CustomerExtType.ENTERPRISE.getValue().equals(groupGeneral.getSubject())) {
      return;
    }

    Set<AmlOprDtl> amls = amlOprDtls.stream()
        .filter(
            filterData -> groupGeneral.getRefCustomerId().equals(filterData.getRefCustomerId())
                && AML_TAG.equalsIgnoreCase(filterData.getQueryType()))
        .collect(Collectors.toSet());

    if (amls.stream().anyMatch(matching -> "500".equals(matching.getResultCode()))) {
      groupGeneral.setAmlResult(ON_THE_LIST);
    } else if (amls.stream().anyMatch(matching -> !"-1".equals(matching.getResultCode()))) {
      groupGeneral.setAmlResult(NOT_ON_THE_LIST);
    } else {
      groupGeneral.setAmlResult(
          amls.stream().findAny().map(AmlOprDtl::getResultDescription).orElse(null));
    }
  }

  private void setOprResult(Set<AmlOprDtl> amlOprDtls, AmlOprGeneral groupGeneral) {
    if (CustomerExtType.ENTERPRISE.getValue().equals(groupGeneral.getSubject())) {
      AmlOprDtl enterpriseAmlOpr = amlOprDtls.stream()
          .filter(filterData -> groupGeneral.getIdentifierCode().equalsIgnoreCase(filterData.getIdentifierCode())
              && OPR_TAG.equalsIgnoreCase(filterData.getQueryType()))
          .findFirst()
          .orElse(new AmlOprDtl());

      if (QUERY_ERROR_CODE.equals(enterpriseAmlOpr.getResultCode())) {
        groupGeneral.setOprResult(RESULT_QUERY_ERROR);
      } else if (StringUtils.isNotBlank(enterpriseAmlOpr.getResultOnBlackList())) {
        groupGeneral.setOprResult(ON_THE_LIST);
      } else {
        groupGeneral.setOprResult(NOT_ON_THE_LIST);
      }

      return;
    }

    Set<AmlOprDtl> oprs = amlOprDtls.stream()
        .filter(
            filterData -> groupGeneral.getRefCustomerId().equals(filterData.getRefCustomerId())
                && OPR_TAG.equalsIgnoreCase(filterData.getQueryType()))
        .collect(Collectors.toSet());

    if (oprs.stream().anyMatch(matching -> QUERY_ERROR_CODE.equalsIgnoreCase(
        matching.getResultCode()))) {
      groupGeneral.setOprResult(RESULT_QUERY_ERROR);
    } else if (oprs.stream()
        .allMatch(matching -> NOT_ON_THE_LIST.equalsIgnoreCase(matching.getResultDescription()))) {
      groupGeneral.setOprResult(NOT_ON_THE_LIST);
    } else if (oprs.stream()
        .anyMatch(matching -> StringUtils.isNotBlank(matching.getResultOnBlackList()))) {
      groupGeneral.setOprResult(ON_THE_LIST);
    } else {
      groupGeneral.setOprResult(
          oprs.stream().findAny().map(AmlOprDtl::getResultDescription).orElse(null));
    }

  }

  /**
   * Build Incomes Data
   *
   * @param incomes Set<ApplicationIncomeEntity>
   * @return Set<ApplicationIncomeDTO>
   */
  @Transactional(readOnly = true)
  public Set<ApplicationIncomeDTO> buildIncomes(Set<ApplicationIncomeEntity> incomes) {
    Set<ApplicationIncomeDTO> responses = new HashSet<>();
    incomes.forEach(income -> responses.add(ApplicationIncomeFactory
        .getApplicationIncome(income.getIncomeRecognitionMethod()).build(income)));
    return responses;
  }

  /**
   * Build appraisal content (include special appraisal content & additional appraisal content)
   *
   * @param appraisalContents Set<ApplicationAppraisalContentEntity>
   * @return Map<String, Set < ApplicationAppraisalContentDTO>>
   */
  @Transactional(readOnly = true)
  public Map<String, Set<ApplicationAppraisalContentDTO>> buildAppraisalContent(
      Set<ApplicationAppraisalContentEntity> appraisalContents) {
    if (CollectionUtils.isEmpty(appraisalContents)) {
      return Collections.emptyMap();
    }

    Set<ApplicationAppraisalContentDTO> appraisalMapping = ApplicationAppraisalContentMapper.INSTANCE.toAppraisalContentDTO(
        appraisalContents);
    return appraisalMapping
        .stream()
        .collect(groupingBy(ApplicationAppraisalContentDTO::getContentType, HashMap::new,
            Collectors.toCollection(HashSet::new)));
  }

  /**
   * Build credit loan
   *
   * @param credits Set<ApplicationCreditEntity>
   * @return Set<ApplicationCreditDTO>
   */
  @Transactional(readOnly = true)
  public Set<ApplicationCreditDTO> buildCredits(Set<ApplicationCreditEntity> credits) {
    Set<ApplicationCreditDTO> responses = new HashSet<>();
    credits.forEach(credit -> responses.add(ApplicationCreditFactory
        .getApplicationCredit(credit.getCreditType()).build(credit)));
    return responses;
  }

  /**
   * Generic function build extra data Convert String to List
   *
   * @param extraDatas  Set<ApplicationExtraDataEntity>
   * @param comparePair Pair<String, String>              Compare value
   * @param tClass      Class<T> tClass                   Class of object
   * @param <T>         Generic class
   * @return Set<T>
   */
  public <T> Set<T> buildExtraData(Set<ApplicationExtraDataEntity> extraDatas,
      Pair<String, String> comparePair,
      Class<T> tClass) {
    if (CollectionUtils.isEmpty(extraDatas)) {
      return Collections.emptySet();
    }

    Map<Pair<String, String>, String> extraDataMap = convert(extraDatas);

    AtomicReference<String> json = new AtomicReference<>("");
    extraDataMap.forEach((k, v) -> {
      if (k.getKey().equalsIgnoreCase(comparePair.getKey())
          && k.getValue().equalsIgnoreCase(comparePair.getValue())) {
        json.set(v);
      }
    });

    if (StringUtils.isNotBlank(json.get())) {
      return JsonUtil.convertString2Set(json.get(), tClass, objectMapper);
    }
    return Collections.emptySet();
  }

  /**
   * Convert List extra data to Map key - value
   *
   * @param extraDatas Set<ApplicationExtraDataEntity>
   * @return Map<Pair < String, String>, String>
   */
  public Map<Pair<String, String>, String> convert(Set<ApplicationExtraDataEntity> extraDatas) {
    return extraDatas
        .stream()
        .collect(Collectors.toMap(
            ext -> new ImmutablePair<>(ext.getCategory(), ext.getKey()),
            ext -> Objects.nonNull(ext.getValue())
                ? new String(ext.getValue(), StandardCharsets.UTF_8) : "", (existingValue, newValue) -> newValue));
  }

  /**
   * Finding application draft data by BPM_ID
   *
   * @param bpmId String
   * @return Set<ApplicationDraftEntity>
   */
  private Set<ApplicationDraftEntity> getApplicationDraft(String bpmId) {
    return applicationDraftRepository.findByBpmId(bpmId)
        .orElse(Collections.emptySet());
  }

  /**
   * Convert List draft data to Map by tab code
   *
   * @param bpmId String
   * @return Map<String, ApplicationDraftEntity>
   */
  public Map<String, ApplicationDraftEntity> draftMapByTabCode(String bpmId) {
    Set<ApplicationDraftEntity> drafts = getApplicationDraft(bpmId);
    if (CollectionUtils.isEmpty(drafts)) {
      return Collections.emptyMap();
    }

    return drafts.stream()
        .collect(Collectors.toMap(ApplicationDraftEntity::getTabCode, Function.identity()));
  }

  /**
   * Convert List draft data to Map by status
   *
   * @param bpmId String
   * @return Map<Integer, Set < ApplicationDraftEntity>>
   */
  public Map<Integer, Set<ApplicationDraftEntity>> draftMapByStatus(String bpmId) {
    Set<ApplicationDraftEntity> drafts = getApplicationDraft(bpmId);
    if (CollectionUtils.isEmpty(drafts)) {
      return Collections.emptyMap();
    }

    return drafts.stream().collect(groupingBy(ApplicationDraftEntity::getStatus,
        HashMap::new, Collectors.toCollection(HashSet::new)));
  }

  /**
   * Replace data to draft data before return
   *
   * @param response GetApplicationDTO
   * @param draft    ApplicationDraftEntity
   */
  public void replaceApplicationDraftData(GetApplicationDTO response,
      ApplicationDraftEntity draft) {
    if (draft.getData() == null) {
      if(ASSET_INFO.equals(draft.getTabCode())) {
        response.getAssetInfo().setCompleted(Boolean.FALSE);
        response.getAssetInfo().setCollateral(null);
      }
      return;
    }
    PostBaseRequest postBaseRequest = getDraftData(draft, draft.getTabCode());
    if (postBaseRequest == null) {
      return;
    }
    switch (draft.getTabCode()) {
      case INITIALIZE_INFO:
        PostInitializeInfoRequest postInitializeInfoRequest = (PostInitializeInfoRequest) postBaseRequest;

        postInitializeInfoRequest.getApplication()
            .setStatus(response.getInitializeInfo().getApplication().getStatus());
        postInitializeInfoRequest.getApplication()
            .setRmCommitStatus(response.getInitializeInfo().getApplication().isRmCommitStatus());
        postInitializeInfoRequest.getApplication()
            .setRmStatus(response.getInitializeInfo().getApplication().isRmStatus());

        response.getInitializeInfo().setApplication(postInitializeInfoRequest.getApplication());
        response.getInitializeInfo().getCustomerAndRelationPerson()
            .setCustomer(postInitializeInfoRequest.getCustomerAndRelationPerson().getCustomer());
        response.getInitializeInfo().getCustomerAndRelationPerson().setCustomerRelations(
            postInitializeInfoRequest.getCustomerAndRelationPerson().getCustomerRelations());
        response.getInitializeInfo().getCustomerAndRelationPerson().setEnterpriseRelations(
            postInitializeInfoRequest.getCustomerAndRelationPerson().getEnterpriseRelations());

        setCICDraft(response, postInitializeInfoRequest);

        response.getInitializeInfo().getCustomerAndRelationPerson()
            .setAmlOpr(postInitializeInfoRequest.getCustomerAndRelationPerson().getAmlOpr());
        response.getInitializeInfo().setIncomes(postInitializeInfoRequest.getIncomes());
        response.getInitializeInfo().getCustomerAndRelationPerson().setApplicationContact(
            postInitializeInfoRequest.getCustomerAndRelationPerson().getApplicationContact());
        break;
      case FIELD_INFO:
        PostFieldInformationRequest postFieldInformationRequest = (PostFieldInformationRequest) postBaseRequest;
        response.getFieldInfor()
            .setFieldInformations(postFieldInformationRequest.getFieldInformations());
        break;
      case DEBT_INFO:
        PostDebtInfoRequest postDebtInfoRequest = (PostDebtInfoRequest) postBaseRequest;
        response.getDebtInfo().setLimitCredits(postDebtInfoRequest.getLimitCredits());
        response.getDebtInfo().setCreditRatings(postDebtInfoRequest.getCreditRatings());
        response.getDebtInfo().setCredits(postDebtInfoRequest.getCredits());
        response.getDebtInfo().setRepayment(postDebtInfoRequest.getRepayment());
        response.getDebtInfo().getRepayment()
            .setTotalIncome(calculateTotalIncome(response.getInitializeInfo()
                .getIncomes()));
        response.getDebtInfo().setSpecialRiskContents(postDebtInfoRequest.getSpecialRiskContents());
        response.getDebtInfo().setAdditionalContents(postDebtInfoRequest.getAdditionalContents());
        response.getDebtInfo().setCreditConditions(
            postDebtInfoRequest.getCreditConditions().stream().collect(Collectors.toList()));
        response.getDebtInfo().setOtherReviews(postDebtInfoRequest.getOtherReviews());
        response.getDebtInfo().setPhoneExpertise(postDebtInfoRequest.getPhoneExpertise());
        response.getDebtInfo().setEffectivePeriod(postDebtInfoRequest.getEffectivePeriod());
        response.getDebtInfo().setEffectivePeriodUnit(postDebtInfoRequest.getEffectivePeriodUnit());
        response.getDebtInfo()
            .setProposalApprovalPosition(postDebtInfoRequest.getProposalApprovalPosition());
        response.getDebtInfo()
            .setLoanApprovalPosition(postDebtInfoRequest.getLoanApprovalPosition());
        response.getDebtInfo()
            .setLoanApprovalPositionValue(postDebtInfoRequest.getLoanApprovalPositionValue());
        response.getDebtInfo()
            .setApplicationAuthorityLevel(postDebtInfoRequest.getApplicationAuthorityLevel());
        response.getDebtInfo().setPriorityAuthority(postDebtInfoRequest.getPriorityAuthority());
        response.getDebtInfo().setAssetAllocations(postDebtInfoRequest.getAssetAllocations());
        break;
      case ASSET_INFO:
        PostAssetInfoRequest postAssetInfoInfoRequest = (PostAssetInfoRequest) postBaseRequest;
        response.getAssetInfo().setCompleted(Boolean.FALSE);
        response.getAssetInfo().setBpmId(postAssetInfoInfoRequest.getBpmId());
        response.getAssetInfo().setType(postAssetInfoInfoRequest.getType());
        if(ObjectUtils.isNotEmpty(postAssetInfoInfoRequest.getCollateral())) {
          response.getAssetInfo().setCollateral(postAssetInfoInfoRequest.getCollateral());
        } else {
          response.getAssetInfo().setCollateral(postAssetInfoInfoRequest);
        }
        break;
      default:
        break;
    }
  }

  private void setCICDraft(GetApplicationDTO response,
      PostInitializeInfoRequest postInitializeInfoRequest) {
    CicDTO cicDTO = postInitializeInfoRequest.getCustomerAndRelationPerson().getCic();
    if (cicDTO != null
        && !CollectionUtils.isEmpty(cicDTO.getCicDetails())) {
      cicDTO.setCicGroupDetails(cicGroupDetailsFromApplicationDraft(
          postInitializeInfoRequest.getCustomerAndRelationPerson().getCic()
              .getCicDetails()));
    }
    response.getInitializeInfo().getCustomerAndRelationPerson().setCic(cicDTO);
  }

  /**
   * Set tab state
   *
   * @param response Object
   */
  public void setTabCompleted(Object response) {
    if (Objects.isNull(response)) {
      return;
    }

    if (response instanceof InitializeInfoDTO) {
      ((InitializeInfoDTO) response).setCompleted(Boolean.TRUE);
    } else if (response instanceof FieldInforDTO) {
      ((FieldInforDTO) response).setCompleted(Boolean.TRUE);
    } else if (response instanceof DebtInfoDTO) {
      ((DebtInfoDTO) response).setCompleted(Boolean.TRUE);
    } else if (response instanceof AssetCommonInfoDTO) {
      ((AssetCommonInfoDTO) response).setCompleted(Boolean.TRUE);
    }
  }

  /**
   * Get draft data by tab code
   *
   * @param draft   ApplicationDraftEntity
   * @param tabCode String
   * @return PostBaseRequest
   */
  public PostBaseRequest getDraftData(ApplicationDraftEntity draft, String tabCode) {
    if (Objects.isNull(draft)
        || Objects.isNull(draft.getData())
        || Objects.equals(FINISHED, draft.getStatus())) {
      return null;
    }

    try {
      switch (tabCode) {
        case INITIALIZE_INFO:
          return objectMapper.readValue(new String(draft.getData(), StandardCharsets.UTF_8),
              PostInitializeInfoRequest.class);
        case FIELD_INFO:
          return objectMapper.readValue(new String(draft.getData(), StandardCharsets.UTF_8),
              PostFieldInformationRequest.class);
        case DEBT_INFO:
          return objectMapper.readValue(new String(draft.getData(), StandardCharsets.UTF_8),
              PostDebtInfoRequest.class);
        case ASSET_INFO:
          return objectMapper.readValue(new String(draft.getData(), StandardCharsets.UTF_8),
                  PostAssetInfoRequest.class);
        default:
          return null;
      }
    } catch (JsonProcessingException e) {
      log.error("Parse string to json fail: ", e);
    }
    return null;
  }

  /**
   * Persist/ Merge draft data by BPM_ID
   *
   * @param bpmId   String
   * @param tabCode String
   * @param status  int
   * @param t       T
   * @param <T>     T
   */
  public <T> void persistApplicationDraft(String bpmId, String tabCode, int status, T t) {
    Map<String, ApplicationDraftEntity> map = draftMapByTabCode(bpmId);
    ApplicationDraftEntity applicationDraft = map.get(tabCode);
    if (applicationDraft == null) {
      applicationDraft = new ApplicationDraftEntity();
      applicationDraft.setBpmId(bpmId);
      applicationDraft.setTabCode(tabCode);
    }
    applicationDraft.setStatus(status);
    if (INITIALIZE_INFO.equalsIgnoreCase(tabCode)
        && UNFINISHED == status) {
      PostInitializeInfoRequest postInitializeInfoRequest = (PostInitializeInfoRequest) t;

      ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId).orElse(null);

      assert applicationEntity != null;
      Set<CicEntity> cicEntities = new HashSet<>(
          cicRepository.findByApplicationId(applicationEntity.getId())
              .orElse(Collections.emptyList()));

      CicDTO cicDTO = buildCicData(cicEntities);

      mapCicDraftWithCicDB(cicDTO, postInitializeInfoRequest);

    }
    applicationDraft.setData(
        JsonUtil.convertObject2String(t, objectMapper).getBytes(StandardCharsets.UTF_8));
    applicationDraftRepository.save(applicationDraft);
  }

  private void mapCicDraftWithCicDB(CicDTO cicDbDTO,
      PostInitializeInfoRequest postInitializeInfoRequest) {
    if (cicDbDTO == null || CollectionUtils.isEmpty(cicDbDTO.getCicDetails())) {
      return;
    }

    CicDTO cicDraftDTO = postInitializeInfoRequest.getCustomerAndRelationPerson().getCic();

    if (CollectionUtils.isEmpty(cicDraftDTO.getCicDetails())) {
      cicDbDTO.setExplanation(cicDraftDTO.getExplanation());
      postInitializeInfoRequest.getCustomerAndRelationPerson().setCic(cicDbDTO);
      return;
    }

    Map<Long, CicDetail> cicDetailMap = cicDbDTO.getCicDetails()
        .stream()
        .collect(Collectors.toMap(CicDetail::getId, Function.identity()));

    for (CicDetail cicDetail : cicDraftDTO.getCicDetails()) {
      if (Objects.nonNull(cicDetail.getId()) && cicDetailMap.containsKey(cicDetail.getId())) {
        CicMapper.INSTANCE.referenceCicDetail(cicDetail, cicDetailMap.get(cicDetail.getId()));
      }
    }
  }

  /**
   * Verify request type
   *
   * @param requestType         String
   * @param acceptedRequestType List<String>
   */
  public void validateRequestType(String requestType, List<String> acceptedRequestType) {
    if (!acceptedRequestType.contains(requestType)) {
      throw new ApprovalException(REQUEST_TYPE_NOT_ACCEPTED);
    }
  }

  /**
   * Calculate suggested amount = SUM(loan_amount of credit loan)
   *
   * @param credits Set<ApplicationCreditDTO>
   * @return BigDecimal
   */
  public BigDecimal calculateSuggestedAmount(Set<ApplicationCreditDTO> credits) {
    if (CollectionUtils.isEmpty(credits)) {
      return BigDecimal.ZERO;
    }

    return credits.stream()
        .map(ApplicationCreditDTO::getLoanAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Build regulation code
   *
   * @param credits Set<ApplicationCreditDTO>
   * @return String
   */
  public String buildRegulationCode(Set<ApplicationCreditDTO> credits) {
    if (CollectionUtils.isEmpty(credits)) {
      return null;
    }

    return credits.stream().map(ApplicationCreditDTO::getDocumentCode)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.joining(";"));
  }

  public DistributionForm getDistributionForm(Set<ApplicationCreditDTO> credits) {
    if (CollectionUtils.isEmpty(credits)) {
      return null;
    }

    credits = credits.stream()
        .filter(filter -> StringUtils.isNotBlank(filter.getGuaranteeForm()))
        .collect(Collectors.toSet());

    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(credits)) {
      return credits
          .stream()
          .anyMatch(x -> COLLATERAL.getCode().equalsIgnoreCase(x.getGuaranteeForm()))
          ? DF1 : DF2;
    }

    return null;
  }

  public ApplicationPhoneExpertiseDTO buildPhoneExpertise(
      Set<ApplicationPhoneExpertiseEntity> phoneExpertiseEntities) {
    if (CollectionUtils.isEmpty(phoneExpertiseEntities)) {
      return null;
    }

    Set<ApplicationPhoneExpertiseDtlDTO> phoneExpertiseDtls = ApplicationPhoneExpertiseMapper.INSTANCE.toApplicationPhoneExpertises(
        phoneExpertiseEntities);

    ApplicationPhoneExpertiseDTO response = new ApplicationPhoneExpertiseDTO();

    String ext = phoneExpertiseDtls.stream()
        .findAny()
        .map(ApplicationPhoneExpertiseDtlDTO::getExt)
        .orElse(null);

    response.setExt(ext);
    response.setPhoneExpertiseDtls(phoneExpertiseDtls);

    return response;
  }

  public Set<ApplicationCreditRatingsDTO> buildCreditRatings(
      Set<ApplicationCreditRatingsEntity> creditRatingsEntities) {
    if (CollectionUtils.isEmpty(creditRatingsEntities)) {
      return Collections.emptySet();
    }

    return ApplicationCreditRatingsMapper.INSTANCE.toApplicationCreditRatings(
        creditRatingsEntities);
  }

  public ApplicationLimitCreditDTO calculateLimitCredit(Set<ApplicationCreditDTO> credits) {
    Set<ApplicationCreditDTO> collateralCredits = credits
        .stream()
        .filter(filter -> COLLATERAL.getCode().equalsIgnoreCase(filter.getGuaranteeForm())
            && !checkDocumentCodeAndProductCodeAndProductInfoCodeHDLD(filter)
            && Y.name().equalsIgnoreCase(filter.getApproveResult()))
        .collect(Collectors.toSet());

    Set<ApplicationCreditDTO> unsecureCredits = credits
        .stream()
        .filter(filter -> (UNSECURE.getCode().equalsIgnoreCase(filter.getGuaranteeForm())
            || checkDocumentCodeAndProductCodeAndProductInfoCodeHDLD(filter))
            && Y.name().equalsIgnoreCase(filter.getApproveResult()))
        .collect(Collectors.toSet());

    TotalLoanAmountDTO collateralCalc = calculateEachLimitCredit(collateralCredits);
    TotalLoanAmountDTO unsecureCalc = calculateEachLimitCredit(unsecureCredits);

    return new ApplicationLimitCreditDTO()
        .withLoanProductCollateral(collateralCalc.getProductAmount())
        .withOtherLoanProductCollateral(collateralCalc.getOtherProductAmount())
        .withUnsecureProduct(unsecureCalc.getProductAmount())
        .withOtherUnsecureProduct(unsecureCalc.getOtherProductAmount());
  }

  private TotalLoanAmountDTO calculateEachLimitCredit(Set<ApplicationCreditDTO> credits) {
    final BigDecimal[] loanProduct = {BigDecimal.ZERO};
    final BigDecimal[] otherProduct = {BigDecimal.ZERO};
    credits.forEach(collateral -> {
      String productCode;
      String productInfoCode = "";
      switch (collateral.getCreditType()) {
        case LOAN:
          ApplicationCreditLoanDTO loanDTO = (ApplicationCreditLoanDTO) collateral;
          productCode = loanDTO.getProductCode();
          productInfoCode = loanDTO.getProductInfoCode();
          break;
        case OVERDRAFT:
          ApplicationCreditOverdraftDTO overdraftDTO = (ApplicationCreditOverdraftDTO) collateral;
          productCode = overdraftDTO.getInterestRateCode();
          productInfoCode = overdraftDTO.getProductInfoCode();
          break;
        case CARD:
          ApplicationCreditCardDTO cardDTO = (ApplicationCreditCardDTO) collateral;
          productCode = cardDTO.getCardPolicyCode();
          break;
        default:
          productCode = "";
          break;
      }

      BigDecimal loanAmount =
          Objects.nonNull(collateral.getLoanAmount()) ? collateral.getLoanAmount()
              : BigDecimal.ZERO;
      if (tnrProductCodes().contains(productCode) || tnrProductInfoCodes().contains(productInfoCode)) {
        loanProduct[0] = loanProduct[0].add(loanAmount);
      } else {
        otherProduct[0] = otherProduct[0].add(loanAmount);
      }
    });

    return new TotalLoanAmountDTO()
        .withProductAmount(loanProduct[0])
        .withOtherProductAmount(otherProduct[0]);
  }

  private boolean checkDocumentCodeAndProductCodeAndProductInfoCodeHDLD(ApplicationCreditDTO credits) {
    // check thông tin mã văn bản, mã sản phẩm, mã sản phẩm chi tiết của HDLD
    String documentCode = credits.getDocumentCode();
    String productCode;
    String productInfoCode = "";
    switch (credits.getCreditType()) {
      case LOAN:
        ApplicationCreditLoanDTO loanDTO = (ApplicationCreditLoanDTO) credits;
        productCode = loanDTO.getProductCode();
        productInfoCode = loanDTO.getProductInfoCode();
        break;
      case OVERDRAFT:
        ApplicationCreditOverdraftDTO overdraftDTO = (ApplicationCreditOverdraftDTO) credits;
        productCode = overdraftDTO.getInterestRateCode();
        productInfoCode = overdraftDTO.getProductInfoCode();
        break;
      case CARD:
        ApplicationCreditCardDTO cardDTO = (ApplicationCreditCardDTO) credits;
        productCode = cardDTO.getCardPolicyCode();
        break;
      default:
        productCode = "";
        break;
    };

    return (hdldDocumentCodes().contains(documentCode) &&
            hdldProductCodes().contains(productCode)) ||
            hdldProductInfoCodes().contains(productInfoCode);
  }

  /**
   * Get previous history approval
   *
   * @param approvalHistories List<ApplicationHistoryApprovalEntity>
   * @param processingRole    ProcessingRole
   * @return
   */
  public List<ApplicationHistoryApprovalEntity> getPreviousHistoryApprovalByRole(
      List<ApplicationHistoryApprovalEntity> approvalHistories, ProcessingRole processingRole) {
    if (org.apache.commons.collections4.CollectionUtils.isEmpty(approvalHistories)) {
      return Collections.emptyList();
    }
    return approvalHistories.stream()
        .filter(approvalHistory -> processingRole.equals(approvalHistory.getUserRole()))
        .sorted(Comparator.comparing(ApplicationHistoryApprovalEntity::getExecutedAt).reversed())
        .collect(Collectors.toList());
  }

  /**
   * Tính tổng nguồn thu
   *
   * @param incomes Danh sách nguồn thu
   * @return BigDecimal
   */
  public BigDecimal calculateTotalIncome(Set<ApplicationIncomeDTO> incomes) {
    if (org.apache.commons.collections4.CollectionUtils.isEmpty(incomes)) {
      return BigDecimal.ZERO;
    }

    return incomes
        .stream()
        .map(income -> {
          if (ACTUALLY.equalsIgnoreCase(income.getIncomeRecognitionMethod())) {
            return ((ActuallyIncomeDTO) income).getIncomeItems()
                .stream()
                .map(BaseIncomeDTO::getRecognizedIncome)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
          }

          if (EXCHANGE.equalsIgnoreCase(income.getIncomeRecognitionMethod())) {
            ExchangeIncomeDTO exchangeIncomeDTO = (ExchangeIncomeDTO) income;
            return exchangeIncomeDTO.getRecognizedIncome() == null ? BigDecimal.ZERO
                : exchangeIncomeDTO.getRecognizedIncome();
          }

          return BigDecimal.ZERO;
        })
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Refresh tất cả dữ liệu lưu nháp theo hồ sơ
   *
   * @param bpmId String
   */
  public void refreshApplicationDataDraft(String bpmId) {
    Set<ApplicationDraftEntity> applicationDraftEntities = getApplicationDraft(bpmId);

    applicationDraftEntities.forEach(applicationDraftEntity -> {
      if (INITIALIZE_INFO.equalsIgnoreCase(applicationDraftEntity.getTabCode())) {
        applicationDraftEntity.setData(null);
      }
      applicationDraftEntity.setStatus(UNFINISHED);
    });

    applicationDraftRepository.saveAllAndFlush(applicationDraftEntities);
  }

  public String getCustomerName(String eventCode, ApplicationEntity entityApp) {
    log.info("getCustomerName with eventCode={}, bpmId={}", eventCode, entityApp.getBpmId());
    String customerName = "";
    try {
      if (Objects.nonNull(entityApp.getCustomer().getIndividualCustomer())) {
        customerName = entityApp.getCustomer().getIndividualCustomer().getLastName() + " " +
            entityApp.getCustomer().getIndividualCustomer().getFirstName();
      }
    } catch (Exception ex) {
      log.info("getCustomerName with eventCode={}, bpmId={}, Error: {}",
          eventCode, entityApp.getBpmId(), ex.getMessage());
    }
    return customerName;
  }

  public String getReasonReturn(String eventCode, String bpmId, Set<String> reasons) {
    log.info("getReasonReturn START with eventCode={}, bpmId={}, reasons={}", eventCode,
        bpmId, JsonUtil.convertObject2String(reasons, objectMapper));
    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(reasons)) {
      List<String> lstReasons = new ArrayList<>(reasons);
      if (EventCodeEmailType.CLOSE.name().equalsIgnoreCase(eventCode)) {
        return ReasonCloseType.getListReasonByCode(lstReasons);
      }
      return ReasonReturnType.getListReasonByCode(lstReasons);
    }
    return "";
  }

  public List<AssetInforRequest> buildAssetProposal(String bpmId) {
    try {
      AssetInfoResponse response = assetClient.getAssetData(bpmId, AssetBusinessType.LDP);

      if (response == null || response.getData() == null
          || CollectionUtils.isEmpty(response.getData()
          .getAssetData())) {
        return Collections.emptyList();
      }

      return assetInfoMapper.convertAssetDataToAssetInfo(response.getData()
          .getAssetData());
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  // Caculate for entities
  public ApplicationLimitCreditEntity calculateLimitCreditEntities(Set<ApplicationCreditEntity> credits, ApplicationLimitCreditEntity limitCreditEntity) {
    Set<ApplicationCreditEntity> collateralCredits = credits
            .stream()
            .filter(filter -> COLLATERAL.getCode().equalsIgnoreCase(filter.getGuaranteeForm())
                    && !checkDocumentCodeAndProductCodeAndProductInfoHDLDEntites(filter)
                    && Y.name().equalsIgnoreCase(filter.getApproveResult()))
            .collect(Collectors.toSet());

    Set<ApplicationCreditEntity> unsecureCredits = credits
            .stream()
            .filter(filter -> (UNSECURE.getCode().equalsIgnoreCase(filter.getGuaranteeForm())
                    || checkDocumentCodeAndProductCodeAndProductInfoHDLDEntites(filter))
                    && Y.name().equalsIgnoreCase(filter.getApproveResult()))
            .collect(Collectors.toSet());

    TotalLoanAmountDTO collateralCalc = calculateEachLimitCreditEntities(collateralCredits);
    TotalLoanAmountDTO unsecureCalc = calculateEachLimitCreditEntities(unsecureCredits);

    BigDecimal totalNew = BigDecimal.ZERO;
    if(ObjectUtils.isNotEmpty(collateralCalc)) {
      limitCreditEntity.setLoanProductCollateral(collateralCalc.getProductAmount());
      limitCreditEntity.setOtherLoanProductCollateral(collateralCalc.getOtherProductAmount());
      totalNew = totalNew.add(collateralCalc.getProductAmount());
      totalNew = totalNew.add(collateralCalc.getOtherProductAmount());
    }
    if(ObjectUtils.isNotEmpty(unsecureCalc)) {
      limitCreditEntity.setUnsecureProduct(unsecureCalc.getProductAmount());
      limitCreditEntity.setOtherUnsecureProduct(unsecureCalc.getOtherProductAmount());
      totalNew = totalNew.add(unsecureCalc.getProductAmount());
      totalNew = totalNew.add(unsecureCalc.getOtherProductAmount());
    }
    if(ObjectUtils.isEmpty(limitCreditEntity.getTotal())
            || totalNew.compareTo(limitCreditEntity.getTotal()) >= 0) {
      limitCreditEntity.setTotal(totalNew);
    }
    return limitCreditEntity;
  }

  private TotalLoanAmountDTO calculateEachLimitCreditEntities(Set<ApplicationCreditEntity> credits) {
    final BigDecimal[] loanProduct = {BigDecimal.ZERO};
    final BigDecimal[] otherProduct = {BigDecimal.ZERO};
    credits.forEach(collateral -> {
      String productCode = getProductCode(collateral);
      String productInfoCode = getProductInfoCode(collateral);

      BigDecimal loanAmount =
              Objects.nonNull(collateral.getLoanAmount()) ? collateral.getLoanAmount()
                      : BigDecimal.ZERO;
      if (tnrProductCodes().contains(productCode) || tnrProductInfoCodes().contains(productInfoCode)) {
        loanProduct[0] = loanProduct[0].add(loanAmount);
      } else {
        otherProduct[0] = otherProduct[0].add(loanAmount);
      }
    });

    return new TotalLoanAmountDTO()
            .withProductAmount(loanProduct[0])
            .withOtherProductAmount(otherProduct[0]);
  }

  private boolean checkDocumentCodeAndProductCodeAndProductInfoHDLDEntites(ApplicationCreditEntity credits) {
    // check thông tin mã văn bản, mã sản phẩm, mã sản phẩm chi tiết của HDLD
    String documentCode = credits.getDocumentCode();
    String productCode = getProductCode(credits);
    String productInfoCode = getProductInfoCode(credits);
    return (hdldDocumentCodes().contains(documentCode) &&
            hdldProductCodes().contains(productCode)) ||
            hdldProductInfoCodes().contains(productInfoCode);
  }

  private String getProductCode(ApplicationCreditEntity credit) {
    if(ObjectUtils.isNotEmpty(credit.getCreditLoan()))
      return credit.getCreditLoan().getProductCode();

    if(ObjectUtils.isNotEmpty(credit.getCreditOverdraft()))
      return credit.getCreditOverdraft().getInterestRateCode();

    if(ObjectUtils.isNotEmpty(credit.getCreditCard()))
      return credit.getCreditCard().getCardPolicyCode();

    return null;
  }

  private String getProductInfoCode(ApplicationCreditEntity credit) {
    if(ObjectUtils.isNotEmpty(credit.getCreditLoan()))
      return credit.getCreditLoan().getProductInfoCode();

    if(ObjectUtils.isNotEmpty(credit.getCreditOverdraft()))
      return credit.getCreditOverdraft().getProductInfoCode();

    return null;
  }
}
