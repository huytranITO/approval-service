package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.BlockKey.OTHER_REVIEW;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DEBT_INFO;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.OTHER_ADDITIONAL;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.SPECIAL_RISK;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.collateral.AssetAllocationConfigProperties;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralConfigProperties;
import com.msb.bpm.approval.appr.client.creditconditions.CreditConditionClient;
import com.msb.bpm.approval.appr.constant.CreditConditionSourceConstant;
import com.msb.bpm.approval.appr.constant.CreditConditionStateConstant;
import com.msb.bpm.approval.appr.enums.application.DistributionForm;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.factory.ApplicationCreditReverseFactory;
import com.msb.bpm.approval.appr.mapper.ApplicationAppraisalContentMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditConditionMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationLimitCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationPhoneExpertiseMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationRepaymentMapper;
import com.msb.bpm.approval.appr.mapper.collateral.AssetAllocationMapper;
import com.msb.bpm.approval.appr.mapper.creditconditions.ApplicationCreditConditionsMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditConditionsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationLimitCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationPhoneExpertiseDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationRepaymentDTO;
import com.msb.bpm.approval.appr.model.dto.DebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.OtherReviewDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.ApplicationAssetAllocationDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationAppraisalContentEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditConditionsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationExtraDataEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationPhoneExpertiseEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.request.collateral.AssetAllocationRequest;
import com.msb.bpm.approval.appr.model.request.collateral.CreditAssetRequest;
import com.msb.bpm.approval.appr.model.request.creditconditions.CreateCreditConditionRequest;
import com.msb.bpm.approval.appr.model.request.creditconditions.CreditConditionRequest;
import com.msb.bpm.approval.appr.model.request.creditconditions.DeleteCreditConditionRequest;
import com.msb.bpm.approval.appr.model.request.creditconditions.UpdateCreditConditionRequest;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import com.msb.bpm.approval.appr.model.response.collateral.AssetAllocationResponse;
import com.msb.bpm.approval.appr.model.response.collateral.CreditAssetAllocationResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionClientResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionResponse;
import com.msb.bpm.approval.appr.repository.ApplicationCreditConditionRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.msb.bpm.approval.appr.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostDebtInfoServiceImpl extends AbstractBaseService
    implements BaseService<Object, PostDebtInfoRequest> {

  private final ApplicationRepository applicationRepository;
  private final ObjectMapper objectMapper;
  private final MessageSource messageSource;
  private final CreditConditionClient creditConditionClient;
  private final ApplicationCreditConditionRepository applicationCreditConditionRepository;
  private final CollateralClient collateralClient;
  private final CollateralConfigProperties collateralConfigProperties;

  @Override
  public String getType() {
    return POST_DEBT_INFO;
  }

  @Override
  @Transactional
  public Object execute(PostDebtInfoRequest postCreditRequest, Object... obj) {

    log.info("PostDebtInfoServiceImpl.execute() start with bpmId : {} , request : [{}]", obj[0],
        JsonUtil.convertObject2String(postCreditRequest, objectMapper));

    return Optional.of(applicationRepository.findByBpmId(String.valueOf(obj[0])))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(applicationEntity -> {

          if (!SecurityContextUtil.getCurrentUser()
              .equalsIgnoreCase(applicationEntity.getAssignee())) {
            log.error("User {} don't have permission to execute application {}",
                SecurityContextUtil.getCurrentUser(), obj[0]);
            throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
          }

          if (isComplete(applicationEntity.getStatus())) {
            log.error("Application {} is {} , process interrupted", obj[0],
                applicationEntity.getStatus());
            throw new ApprovalException(APPLICATION_PREVIOUSLY_CLOSED);
          }

          saveData(applicationEntity, postCreditRequest);

          // Gan ts cho khoan vay
          addAssetForCredit(applicationEntity, postCreditRequest.getCredits());

          log.info("PostDebtInfoServiceImpl.execute() end with bpmId : {}", obj[0]);

          return getDebtInfoDTO(applicationEntity);

        })
        .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION));
  }

  @Transactional
  public void saveData(ApplicationEntity applicationEntity, PostDebtInfoRequest postCreditRequest) {
    ApplicationMapper.INSTANCE.referenceApplicationEntityByDebtInfoRequest(applicationEntity, postCreditRequest);
    applicationEntity.setSuggestedAmount(calculateSuggestedAmount(postCreditRequest.getCredits()));
    applicationEntity.setRegulatoryCode(buildRegulationCode(postCreditRequest.getCredits()));

    DistributionForm df = getDistributionForm(postCreditRequest.getCredits());
    if (df != null) {
      applicationEntity.setDistributionFormCode(df.getCode());
      applicationEntity.setDistributionForm(messageSource.getMessage(df.getText(), null, Util.locale()));
    }

    referenceLimitCreditEntities(applicationEntity, postCreditRequest.getLimitCredits());

    referenceRepaymentEntity(applicationEntity, postCreditRequest.getRepayment());

    Set<ApplicationAppraisalContentDTO> combineAppraisalContents = new HashSet<>();
    if (CollectionUtils.isNotEmpty(postCreditRequest.getSpecialRiskContents())) {
      combineAppraisalContents.addAll(postCreditRequest.getSpecialRiskContents());
    }
    if (CollectionUtils.isNotEmpty(postCreditRequest.getAdditionalContents())) {
      combineAppraisalContents.addAll(postCreditRequest.getAdditionalContents());
    }
    referenceAppraisalContentsEntities(applicationEntity, combineAppraisalContents);

    Map<Pair<String, String>, ApplicationExtraDataEntity> extraDataEntityMap = applicationEntity.getExtraDatas()
        .stream()
        .collect(Collectors.toMap(ext -> new ImmutablePair<>(ext.getCategory(), ext.getKey()), ext -> ext,(existing, replacement) -> existing));
    referenceExtraDataEntities(applicationEntity, postCreditRequest.getOtherReviews(), extraDataEntityMap, DEBT_INFO, OTHER_REVIEW);
    referencePhoneExpertises(applicationEntity, postCreditRequest.getPhoneExpertise());
    referenceCreditEntities(applicationEntity, postCreditRequest.getCredits());
    referenceCreditConditionsEntities(applicationEntity, postCreditRequest.getCreditConditions());

    referenceCreditRatingsEntities(applicationEntity, postCreditRequest.getCreditRatings());

    referenceAssetAllocationInfo(applicationEntity, postCreditRequest.getAssetAllocations());

    applicationRepository.saveAndFlush(applicationEntity);
  }

  @Transactional
  public void referenceLimitCreditEntities(ApplicationEntity applicationEntity, Set<ApplicationLimitCreditDTO> limitCredits) {
    if (CollectionUtils.isEmpty(limitCredits)) {
      applicationEntity.getLimitCredits().clear();
      return;
    }

    Set<ApplicationLimitCreditEntity> limitCreditEntities = ApplicationLimitCreditMapper.INSTANCE.toLimitCreditEntities(limitCredits);

    Set<ApplicationLimitCreditEntity> createLimitCreditEntities = limitCreditEntities
        .stream()
        .filter(limitCredit -> Objects.isNull(limitCredit.getId()))
        .collect(Collectors.toSet());

    Set<ApplicationLimitCreditEntity> updateLimitCreditEntities = limitCreditEntities
        .stream()
        .filter(limitCredit -> Objects.nonNull(limitCredit.getId()))
        .collect(Collectors.toSet());

    applicationEntity.updateLimitCreditEntities(updateLimitCreditEntities);

    applicationEntity.addLimitCreditEntities(createLimitCreditEntities);
  }

  @Transactional
  public void referenceRepaymentEntity(ApplicationEntity applicationEntity, ApplicationRepaymentDTO repaymentDTO) {
    applicationEntity.setRepayment(ApplicationRepaymentMapper.INSTANCE.toRepaymentEntity(repaymentDTO));
  }

  @Transactional
  public void referenceAppraisalContentsEntities(ApplicationEntity applicationEntity, Set<ApplicationAppraisalContentDTO> appraisalContents) {
    if (CollectionUtils.isEmpty(appraisalContents)) {
      applicationEntity.getAppraisalContents().clear();
      return;
    }

    Set<ApplicationAppraisalContentEntity> appraisalContentEntities = ApplicationAppraisalContentMapper.INSTANCE.toAppraisalContentEntities(appraisalContents);

    Set<ApplicationAppraisalContentEntity> createAppraisalContentEntities = appraisalContentEntities.stream()
            .filter(appraisalContentEntity -> Objects.isNull(appraisalContentEntity.getId()))
            .collect(Collectors.toSet());

    Set<ApplicationAppraisalContentEntity> updateAppraisalContentEntities = appraisalContentEntities.stream()
            .filter(appraisalContentEntity -> Objects.nonNull(appraisalContentEntity.getId()))
            .collect(Collectors.toSet());

    applicationEntity.updateApplicationAppraisalContents(updateAppraisalContentEntities);

    applicationEntity.addApplicationAppraisalContents(createAppraisalContentEntities);
  }

  @Transactional
  public void referenceCreditConditionsEntities(ApplicationEntity applicationEntity, Set<ApplicationCreditConditionsDTO> creditConditions) {
      Set<ApplicationCreditConditionsDTO> updateCreditConditionsEntities = creditConditions.stream()
              .filter(creditConditionsEntity -> Objects.nonNull(creditConditionsEntity.getId()))
              .collect(Collectors.toSet());

      UpdateCreditConditionRequest updateRequest = mappingUpdateOrDeleteCreditConditionRequest(applicationEntity,updateCreditConditionsEntities);
      if(CollectionUtils.isNotEmpty(updateRequest.getCreditConditions())) {
          creditConditionClient.updateCreditCondition(updateRequest);
      }
      List<ApplicationCreditConditionsDTO> createCreditConditionsEntities = creditConditions.stream()
            .filter(creditConditionsEntity -> Objects.isNull(creditConditionsEntity.getId()))
            .collect(Collectors.toList());

      if(CollectionUtils.isNotEmpty(createCreditConditionsEntities)) {
          CreateCreditConditionRequest createRequest = mappingCreateCreditConditionRequest(applicationEntity,createCreditConditionsEntities);
          CreditConditionClientResponse<List<CreditConditionResponse>> createResponse = creditConditionClient.createCreditConditions(createRequest);
          if(CollectionUtils.isNotEmpty(createResponse.getValue())) {
              createResponse.getValue().forEach(s -> {
                  ApplicationCreditConditionsEntity entity = new ApplicationCreditConditionsEntity();
                  entity.setApplication(applicationEntity);
                  entity.setCreditConditionId(s.getId());
                  applicationCreditConditionRepository.save(entity);
              });
        }
    }
  }
  
  private CreateCreditConditionRequest mappingCreateCreditConditionRequest(ApplicationEntity applicationEntity,
                                                                           List<ApplicationCreditConditionsDTO> createCreditConditionsEntities) {
      CreateCreditConditionRequest createCreditConditionRequest = new CreateCreditConditionRequest();
      IndividualCustomerEntity customerDetail = applicationEntity.getCustomer().getIndividualCustomer();
      createCreditConditionRequest.setCustomerCifNumber(applicationEntity.getCustomer().getCif());
      createCreditConditionRequest.setCustomerCifNumberBPM(applicationEntity.getCustomer().getBpmCif());
      createCreditConditionRequest.setCustomerName(customerDetail.getLastName() + customerDetail.getFirstName());
      List<CreditConditionRequest> createRequest = createCreditConditionsEntities.stream().map(s -> {
          CreditConditionRequest mapping = ApplicationCreditConditionsMapper.INSTANCE.toCreditConditionRequest(s);
          mapping.setSource(CreditConditionSourceConstant.APPROVAL);
          mapping.setState(CreditConditionStateConstant.NEW);
          return mapping;
      }).collect(Collectors.toList());
      createCreditConditionRequest.setCreditConditions(createRequest);
      return createCreditConditionRequest;
  }

  private UpdateCreditConditionRequest mappingUpdateOrDeleteCreditConditionRequest(
          ApplicationEntity applicationEntity, Set<ApplicationCreditConditionsDTO> updateCreditConditionsEntities) {
      List<ApplicationCreditConditionsEntity> creditConditionExists = applicationCreditConditionRepository
              .findByApplicationIdOrderByCreditConditionId(applicationEntity.getId());
      List<Long> creditConditionIdsExist = creditConditionExists.stream().map(
              ApplicationCreditConditionsEntity::getId)
              .collect(Collectors.toList());
      List<Long> updatedIds = updateCreditConditionsEntities.stream().map(
              ApplicationCreditConditionsDTO::getId)
              .collect(Collectors.toList());
      List<Long> listIdToDelete = creditConditionIdsExist.stream().filter(i -> i != null && !updatedIds.contains(i))
              .collect(Collectors.toList());
      List<ApplicationCreditConditionsEntity> creditConditionToDelete = creditConditionExists.stream()
              .filter(s -> listIdToDelete.contains(s.getId())).collect(Collectors.toList());
      List<Long> creditConditionListId = creditConditionToDelete.stream()
              .map(ApplicationCreditConditionsEntity::getCreditConditionId).filter(Objects::nonNull)
              .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(listIdToDelete) && CollectionUtils.isNotEmpty(creditConditionListId)) {
          applicationCreditConditionRepository.deleteAllByIdInBatch(listIdToDelete);
          DeleteCreditConditionRequest request = new DeleteCreditConditionRequest();
          request.setIds(creditConditionListId);
          creditConditionClient.deleteCreditConditionByListId(request);
      }
      UpdateCreditConditionRequest updateCreditConditionRequest = new UpdateCreditConditionRequest();
      IndividualCustomerEntity customerDetail = applicationEntity.getCustomer().getIndividualCustomer();
      updateCreditConditionRequest.setCustomerCifNumber(applicationEntity.getCustomer().getCif());
      updateCreditConditionRequest.setCustomerCifNumberBPM(applicationEntity.getCustomer().getBpmCif());
      updateCreditConditionRequest.setCustomerName(customerDetail.getLastName() + customerDetail.getFirstName());
      List<CreditConditionRequest> updateRequest = updateCreditConditionsEntities.stream().map(s -> {
          CreditConditionRequest mapping = ApplicationCreditConditionsMapper.INSTANCE.toCreditConditionRequest(s);
          mapping.setSource(CreditConditionSourceConstant.APPROVAL);
          mapping.setState(StringUtils.isNotBlank(s.getState()) ? s.getState() : CreditConditionStateConstant.NEW);
          return mapping;
      }).collect(Collectors.toList());
      updateCreditConditionRequest.setCreditConditions(updateRequest);
      return updateCreditConditionRequest;
  }

  @Transactional
  public void referenceExtraDataEntities(
      ApplicationEntity applicationEntity,
      Set<?> extraDataEntities,
      Map<Pair<String, String>, ApplicationExtraDataEntity> extraDataEntityMap,
      String category,
      String key) {

    ApplicationExtraDataEntity extraDataEntity = extraDataEntityMap.get(new ImmutablePair<>(category, key));
    if (extraDataEntity == null) {
      extraDataEntity = new ApplicationExtraDataEntity();
      extraDataEntity.setApplication(applicationEntity);
      extraDataEntity.setCategory(category);
      extraDataEntity.setKey(key);
    }

    byte[] parseValue = CollectionUtils.isEmpty(extraDataEntities)
            ? null
            : JsonUtil.convertObject2String(extraDataEntities, objectMapper).getBytes(StandardCharsets.UTF_8);

    extraDataEntity.setValue(parseValue);
    applicationEntity.getExtraDatas().add(extraDataEntity);
  }

  @Transactional
  public void referencePhoneExpertises(ApplicationEntity applicationEntity, ApplicationPhoneExpertiseDTO phoneExpertiseDTO) {
    if (Objects.isNull(phoneExpertiseDTO) || (CollectionUtils.isEmpty(phoneExpertiseDTO.getPhoneExpertiseDtls())
        && StringUtils.isBlank(phoneExpertiseDTO.getExt()))) {
      applicationEntity.getPhoneExpertises().clear();
      return;
    }

    Set<ApplicationPhoneExpertiseEntity> createExpertiseEntities;

    if (CollectionUtils.isEmpty(phoneExpertiseDTO.getPhoneExpertiseDtls())) {
      createExpertiseEntities = new HashSet<>();
      createExpertiseEntities.add(new ApplicationPhoneExpertiseEntity().withExt(phoneExpertiseDTO.getExt()));
    } else {
      phoneExpertiseDTO.getPhoneExpertiseDtls()
          .forEach(dtl -> dtl.setExt(phoneExpertiseDTO.getExt()));

      Set<ApplicationPhoneExpertiseEntity> phoneExpertiseEntities = ApplicationPhoneExpertiseMapper.INSTANCE.toApplicationPhoneExpertiseEntities(
          phoneExpertiseDTO.getPhoneExpertiseDtls());

      createExpertiseEntities = phoneExpertiseEntities
          .stream()
          .filter(phoneExpertise -> Objects.isNull(phoneExpertise.getId()))
          .collect(Collectors.toSet());

      Set<ApplicationPhoneExpertiseEntity> updateExpertiseEntities = phoneExpertiseEntities
          .stream()
          .filter(phoneExpertise -> Objects.nonNull(phoneExpertise.getId()))
          .collect(Collectors.toSet());

      applicationEntity.updatePhoneExpertiseEntities(updateExpertiseEntities);

    }
    applicationEntity.addPhoneExpertiseEntities(createExpertiseEntities);
  }

  @Transactional
  public void referenceCreditEntities(ApplicationEntity applicationEntity, Set<ApplicationCreditDTO> credits) {
    if (CollectionUtils.isEmpty(credits)) {
      applicationEntity.getCredits().clear();
      return;
    }

    Set<ApplicationCreditEntity> creditEntities = credits.stream()
            .map(creditDTO -> ApplicationCreditReverseFactory.getApplicationCredit(creditDTO.getCreditType()).build(creditDTO))
            .collect(Collectors.toSet());

    Set<ApplicationCreditEntity> createCreditEntities = creditEntities.stream()
            .filter(creditEntity -> Objects.isNull(creditEntity.getId()))
            .collect(Collectors.toSet());

    Set<ApplicationCreditEntity> updateCreditEntities = creditEntities.stream()
            .filter(creditEntity -> Objects.nonNull(creditEntity.getId()))
            .collect(Collectors.toSet());

    applicationEntity.updateCreditEntities(updateCreditEntities);

    applicationEntity.addCreditEntities(createCreditEntities);
  }

  @Transactional
  public void referenceCreditRatingsEntities(ApplicationEntity applicationEntity, Set<ApplicationCreditRatingsDTO> applicationCreditRatings) {
    if (CollectionUtils.isEmpty(applicationCreditRatings)) {
      applicationEntity.getCreditRatings().clear();
      return;
    }

    applicationEntity.removeCreditRatingsEntities(applicationCreditRatings);
  }

  @Transactional(readOnly = true)
  public DebtInfoDTO getDebtInfoDTO(ApplicationEntity applicationEntity) {
    Map<String, Set<ApplicationAppraisalContentDTO>> appraisalContent = buildAppraisalContent(applicationEntity.getAppraisalContents());
      List<ApplicationCreditConditionsEntity> applicationCreditConditionExist = applicationCreditConditionRepository
              .findByApplicationIdOrderByCreditConditionId(applicationEntity.getId());
      List<Long> listCreditConditionId = applicationCreditConditionExist.stream()
              .map(ApplicationCreditConditionsEntity::getCreditConditionId).filter(Objects::nonNull)
              .collect(Collectors.toList());
      List<ApplicationCreditConditionsDTO> creditConditions = new ArrayList<>();
      if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(listCreditConditionId)) {
          CreditConditionClientResponse<List<CreditConditionResponse>> clientCreditConditionReponse = creditConditionClient
                  .getCreditConditionByListId(listCreditConditionId);
          List<ApplicationCreditConditionsDTO> listApplicationCreditConditionDto = ApplicationCreditConditionMapper.INSTANCE
                  .toCreditConditions(clientCreditConditionReponse.getValue());
          creditConditions = listApplicationCreditConditionDto.stream().map(s -> mapIdApplicationCreditCondition(s,applicationCreditConditionExist)).sorted(Comparator.comparing(ApplicationCreditConditionsDTO::getCreditConditionId)).collect(Collectors.toList());
      }

      return new DebtInfoDTO()
        .withEffectivePeriod(applicationEntity.getEffectivePeriod())
        .withEffectivePeriodUnit(applicationEntity.getEffectivePeriodUnit())
        .withProposalApprovalPosition(applicationEntity.getProposalApprovalPosition())
        .withLoanApprovalPosition(applicationEntity.getLoanApprovalPosition())
        .withLoanApprovalPositionValue(applicationEntity.getLoanApprovalPositionValue())
        .withApplicationAuthorityLevel(applicationEntity.getApplicationAuthorityLevel())
        .withPriorityAuthority(applicationEntity.getPriorityAuthority())
        .withLimitCredits(ApplicationLimitCreditMapper.INSTANCE.toLimitCredits(applicationEntity.getLimitCredits()))
        .withRepayment(ApplicationRepaymentMapper.INSTANCE.toRepayment(applicationEntity))
        .withCreditConditions(creditConditions)
        .withCreditRatings(buildCreditRatings(applicationEntity.getCreditRatings()))
        .withCredits(buildCredits(applicationEntity.getCredits()))
        .withPhoneExpertise(buildPhoneExpertise(applicationEntity.getPhoneExpertises()))
        .withOtherReviews(buildExtraData(applicationEntity.getExtraDatas(), new ImmutablePair<>(DEBT_INFO, OTHER_REVIEW), OtherReviewDTO.class))
        .withSpecialRiskContents(appraisalContent.get(SPECIAL_RISK.name()))
        .withAdditionalContents(appraisalContent.get(OTHER_ADDITIONAL.name()))
        .withAssetAllocations(applicationEntity.getAssetAllocations());
  }

    private ApplicationCreditConditionsDTO mapIdApplicationCreditCondition(ApplicationCreditConditionsDTO dto,
                                                                               List<ApplicationCreditConditionsEntity> applicationCreditConditionExist) {
        ApplicationCreditConditionsEntity entity = applicationCreditConditionExist.stream()
                .filter(e -> e.getCreditConditionId().equals(dto.getCreditConditionId())).findFirst().orElse(null);
        if (entity != null) {
            dto.setId(entity.getId());
        }
        return dto;
    }

    @Transactional
    public void referenceAssetAllocationInfo(ApplicationEntity applicationEntity, Set<ApplicationAssetAllocationDTO> assetAllocations) {
      if(assetAllocations == null) {
          return;
      }

      // Check #role update
      List<String> listRole = Arrays.asList(collateralConfigProperties.getAllocationConfig().getRoleEdit().split(","));
      if (!StringUtils.isEmpty(applicationEntity.getProcessingRole())
              && !(SecurityContextUtil.getAuthorities().contains(applicationEntity.getProcessingRole())
                && listRole.contains(applicationEntity.getProcessingRole()))) {
          return;
      }

      if(!ValidationUtil.checkSumPercentAllocation(assetAllocations)
              && !(ProcessingRole.PD_RB_RM.name().equals(applicationEntity.getProcessingRole())
                      || ProcessingRole.PD_RB_BM.name().equals(applicationEntity.getProcessingRole())
               || ObjectUtils.isEmpty(applicationEntity.getProcessingRole()))
              ) {
          log.error("Invalid allocation");
          throw new ApprovalException(DomainCode.ALLOCATION_ASSET_ERROR);
      }

      Set<AssetAllocationRequest> data = AssetAllocationMapper.INSTANCE.toAssetAllocationRequests(assetAllocations);
      List<AssetAllocationRequest> request = new ArrayList<>();
      request.addAll(data);

      // call asset service
      CreditAssetAllocationResponse dataAsset = collateralClient.updateAssetAllocationInfo(applicationEntity.getBpmId(), request);

      if (ObjectUtils.isEmpty(dataAsset) || CollectionUtils.isEmpty(dataAsset.getAssetAllocations())) {
          return;
      }

      Set<AssetAllocationResponse> dataResponse = dataAsset.getAssetAllocations().stream().collect(Collectors.toSet());
      applicationEntity.setAssetAllocations(AssetAllocationMapper.INSTANCE.toAssetAllocationDTO(dataResponse));
    }

    @Transactional
    public void addAssetForCredit(ApplicationEntity applicationEntity, Set<ApplicationCreditDTO> credits) {
      List<CreditAssetRequest> request = new ArrayList<>();
      if(CollectionUtils.isEmpty(credits)) {
          return;
      }

      credits.forEach(credit-> {
          if(Boolean.TRUE.equals(credit.getIsAllocation()) && credit.getAssets() != null && !credit.getAssets().isEmpty()) {
              ApplicationCreditEntity creditEntity;
              if(!StringUtils.isEmpty(credit.getIdDraft())) {
                  creditEntity = applicationEntity.getCredits().stream().filter(cd -> cd.getIdDraft().equals(credit.getIdDraft())).findFirst().orElse(null);
                  if(!ObjectUtils.isEmpty(creditEntity)) {
                      creditEntity.setAssets(credit.getAssets());
                      request.add(CreditAssetRequest.builder()
                              .credit(creditEntity.getId())
                              .assets(credit.getAssets())
                              .build());
                  }
              }
          }
      });

      // call asset service
      List<CreditAssetRequest> dataAsset = collateralClient.mappingCreditAssetInfo(applicationEntity.getBpmId(), request);
      if(!CollectionUtils.isEmpty(dataAsset)) {
          credits.forEach(credit -> {
              List<CreditAssetRequest> assetTmp = dataAsset.stream().filter(as -> as.getCredit().equals(credit.getId())).collect(Collectors.toList());
              if(!CollectionUtils.isEmpty(assetTmp)) credit.setAssets(assetTmp.stream().findFirst().get().getAssets());
          });
      }

    }
}
