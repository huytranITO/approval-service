package com.msb.bpm.approval.appr.mapper.formtemplate;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CUSTOMER_RELATIONS_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CUSTOMER_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ENTERPRISE_RELATIONS_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.BlockKey.OTHER_REVIEW;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.enums.application.AddressType.HK_THUONG_TRU;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.OTHER_ADDITIONAL;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.SPECIAL_RISK;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CONTROL_CREDIT_UNIT;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CREDIT_CONDITION_GROUP;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.DOCUMENT_CODE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.TIME_CONTROL_CREDIT;
import static com.msb.bpm.approval.appr.util.Util.SIGN_OTP_TEXT;
import static com.msb.bpm.approval.appr.util.Util.VN_DATE_FORMAT;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.configuration.ConfigurationListClient;
import com.msb.bpm.approval.appr.client.creditconditions.CreditConditionClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditConditionMapper;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditConditionsDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.CreditConditionParamsDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.OtherReviewDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.ApplicationFormTemplateDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateAssetInfoDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateCreditAssetDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateCreditDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateDebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateIncomesDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateInitializeInfoDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateTotalAssetIncome;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateFieldInforDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateLimitCreditDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.RuleAssetResponse;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditConditionsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity;
import com.msb.bpm.approval.appr.model.response.collateral.CollateralClientResponse;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse.Detail;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionClientResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse.Personal;
import com.msb.bpm.approval.appr.repository.ApplicationCreditConditionRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
@Slf4j
public class FormTemplateApplicationMapper extends AbstractBaseService {

  private FormTemplateIncomeMapper incomeMapper;
  private FormTemplateCreditMapper creditMapper;
  private FormTemplateApplicationDtoMapper formTemplateApplicationDtoMapper;
  private FormTemplateLimitCreditMapper formTemplateLimitCreditMapper;
  private CreditConditionClient creditConditionClient;
  private ApplicationCreditConditionRepository applicationCreditConditionRepository;
  private ConfigurationListClient configurationListClient;
  private UserManagerClient userManagerClient;
  private CollateralClient collateralClient;
  private ObjectMapper objectMapper;
  private ConfigurationServiceCache serviceCache;

  @Transactional
  public ApplicationFormTemplateDTO buildApplicationFormTemplate(ApplicationEntity application) {

    Map<String, Object> customerData = buildCustomerAndRelationData(application.getCustomer());

    Map<Long, String> listCusmapping = new HashMap<>();
    CustomerDTO customer = (CustomerDTO) customerData.get(CUSTOMER_TAG);
    Set<CustomerDTO> customerRelationship = (Set<CustomerDTO>) customerData.get(CUSTOMER_RELATIONS_TAG);
    if(!CollectionUtils.isEmpty(customerRelationship)){
      customerRelationship.forEach(relationship -> listCusmapping.put(relationship.getRefCustomerId(), relationship.getMainIdentity().getIdentifierCode()));
    }
    listCusmapping.put(customer.getRefCustomerId(), customer.getMainIdentity().getIdentifierCode());

    CicDTO cic = buildCicData(application.getCics());

    cic.getCicGroupDetails().forEach(cicDetails -> {
      if(!CustomerType.isEB(cicDetails.getCustomerType())){
        if(listCusmapping.containsKey(cicDetails.getRefCustomerId())){
          cicDetails.setIdentifierCode(listCusmapping.get(cicDetails.getRefCustomerId()));
        }
      }
    });

    AmlOprDTO amlOpr = buildAmlOpr(application.getAmlOprs());

    Map<String, Set<ApplicationAppraisalContentDTO>> appraisalContent =
        buildAppraisalContent(application.getAppraisalContents());

    Map<String, OtherReviewDTO> otherReviews = buildOtherReviews(application);

    Set<FormTemplateIncomesDTO> incomes =
        incomeMapper.transformIncome(
            application.getIncomes().stream()
                .sorted(Comparator.comparing(ApplicationIncomeEntity::getOrderDisplay, Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList()));
    // Sắp xếp lại Total Asset Income
    incomes.forEach(income -> {
        if (ObjectUtils.isNotEmpty(income.getIncomeEvaluation()) &&
            CollectionUtils.isNotEmpty(income.getIncomeEvaluation().getTotalAssetIncomes())) {
            List<FormTemplateTotalAssetIncome> is =  income.getIncomeEvaluation().getTotalAssetIncomes().stream()
              .sorted(comparing(FormTemplateTotalAssetIncome::getOrderDisplay, nullsLast(naturalOrder())))
              .collect(Collectors.toList());
            income.getIncomeEvaluation().setTotalAssetIncomes(is);
        }
    });
    Set<FormTemplateCreditDTO> credits =
        creditMapper.transformCreditDTOs(
            application.getCredits().stream()
                .sorted(Comparator.comparing(ApplicationCreditEntity::getId))
                .collect(Collectors.toList()));
    List<CategoryDataResponse> dataResponses = serviceCache.getCategoryDataByCode(
            DOCUMENT_CODE);
    credits.forEach(formTemplateCreditDTO -> {
      formTemplateCreditDTO.setLoanDocumentName(
              Util.getValueByCategoryCode(formTemplateCreditDTO.getLoanDocumentName(), dataResponses));
      formTemplateCreditDTO.setOverdraftDocumentName(
              Util.getValueByCategoryCode(formTemplateCreditDTO.getOverdraftDocumentName(), dataResponses));
      formTemplateCreditDTO.setCardDocumentName(
              Util.getValueByCategoryCode(formTemplateCreditDTO.getCardDocumentName(), dataResponses));
    });

    Set<FormTemplateLimitCreditDTO> limitCredits = formTemplateLimitCreditMapper.toLimitCredits(
        application.getLimitCredits().stream()
            .sorted(Comparator.comparing(ApplicationLimitCreditEntity::getOrderDisplay, Comparator.nullsFirst(Comparator.naturalOrder())))
            .collect(Collectors.toList()));

    Set<ApplicationCreditConditionsDTO> creditsConditions = buildCreditConditions(application);
    Personal personal = getCurrentUser();
    ApplicationFormTemplateDTO applicationFormTemplateDTO =
        ApplicationFormTemplateDTO.builder()
            .createdFormFullname(personal.getFullName())
            .currentDateTime(
                DateTimeFormatter.ofPattern(VN_DATE_FORMAT).format(LocalDateTime.now()))
            .initializeInfo(
                FormTemplateInitializeInfoDTO.builder()
                    .application(
                        formTemplateApplicationDtoMapper.toFormTemplateApplicationDTO(application))
                    .customerAndRelationPerson(
                        new CustomerAndRelationPersonDTO()
                            .withCustomer(customer)
                            .withCustomerRelations(customerRelationship)
                            .withEnterpriseRelations(
                                (Set<CustomerDTO>) customerData.get(ENTERPRISE_RELATIONS_TAG))
                            .withCic(cic)
                            .withAmlOpr(amlOpr))
                    .incomes(incomes)
                    .totalIncomes(
                        formTemplateApplicationDtoMapper.toNumberFormat(
                            application.getTotalIncome()))
                    .build())
            .fieldInfor(
                FormTemplateFieldInforDTO.builder()
                    .fieldInformations(
                        formTemplateApplicationDtoMapper.toSetFieldInformations(
                            application.getFieldInformations()))
                    .build())
            .debtInfo(
                new FormTemplateDebtInfoDTO()
                    .withCredits(credits)
                    .withCreditRatings(buildCreditRatings(application.getCreditRatings()))
                    .withLimitCredits(limitCredits)
                    .withRepayment(
                        formTemplateApplicationDtoMapper.toApplicationRepaymentDTO(application))
                    .withSpecialRiskContents(
                        formTemplateApplicationDtoMapper.toListAppraisailForm(
                            appraisalContent.get(SPECIAL_RISK.name())))
                    .withAdditionalContents(
                        formTemplateApplicationDtoMapper.toListAppraisailForm(
                            appraisalContent.get(OTHER_ADDITIONAL.name())))
                    .withCreditConditions(creditsConditions)
                    .withOtherReviews(otherReviews)
                    .withPhoneExpertise(buildPhoneExpertise(application.getPhoneExpertises()))
                    .withEffectivePeriod(application.getEffectivePeriod())
                    .withEffectivePeriodUnit(application.getEffectivePeriodUnit())
                    .withProposalApprovalPosition(application.getProposalApprovalPosition())
                    .withLoanApprovalPosition(application.getLoanApprovalPosition())
                    .withApplicationAuthorityLevel(application.getApplicationAuthorityLevel())
                    .withPriorityAuthority(application.getPriorityAuthority()))
            .build();

    // fix some issues
    setSign(personal, applicationFormTemplateDTO, application);
    applicationFormTemplateDTO
        .getInitializeInfo()
        .getApplication()
        .setProposalApprovalFullName(personal.getFullName());
    applicationFormTemplateDTO
        .getInitializeInfo()
        .getApplication()
        .setProposalApprovalPhoneNumber(personal.getPhoneNumber());

    // set main identity for customers
    Set<CustomerIdentityDTO> listNormalIdentiy =
        applicationFormTemplateDTO
            .getInitializeInfo()
            .getCustomerAndRelationPerson()
            .getCustomer()
            .getIdentities()
            .stream()
            .filter(i -> !i.isPriority())
            .collect(Collectors.toSet());
    applicationFormTemplateDTO
        .getInitializeInfo()
        .getCustomerAndRelationPerson()
        .getCustomer()
        .setIdentities(listNormalIdentiy);

    if (ObjectUtils.isNotEmpty(
        applicationFormTemplateDTO
            .getInitializeInfo()
            .getCustomerAndRelationPerson()
            .getCustomerRelations())) {
      applicationFormTemplateDTO
          .getInitializeInfo()
          .getCustomerAndRelationPerson()
          .getCustomerRelations()
          .forEach(
              customerRelations -> {
                Set<CustomerIdentityDTO> setRelationCustomerIdentityDTOs =
                    customerRelations.getIdentities().stream()
                        .filter(i -> !i.isPriority())
                        .collect(Collectors.toSet());
                Set<CustomerAddressDTO> setRelationCustomerAddressesDTOs =
                    customerRelations.getAddresses().stream()
                        .filter(i -> !HK_THUONG_TRU.getValue().equalsIgnoreCase(i.getAddressType()))
                        .collect(Collectors.toSet());
                customerRelations.setIdentities(setRelationCustomerIdentityDTOs);
                customerRelations.setAddresses(setRelationCustomerAddressesDTOs);
              });
    }

    // set main addresses
    Set<CustomerAddressDTO> listNormalAddresses =
        applicationFormTemplateDTO
            .getInitializeInfo()
            .getCustomerAndRelationPerson()
            .getCustomer()
            .getAddresses()
            .stream()
            .filter(i -> !HK_THUONG_TRU.getValue().equalsIgnoreCase(i.getAddressType()))
            .collect(Collectors.toSet());

    applicationFormTemplateDTO
        .getInitializeInfo()
        .getCustomerAndRelationPerson()
        .getCustomer()
        .setAddresses(listNormalAddresses);

    //set data asset
    CollateralClientResponse response = collateralClient.getDataAssetFormTemplate(application.getBpmId());
    try {
      if (Objects.nonNull(response) && Objects.nonNull(response.getData())) {
        LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) response.getData();
        RuleAssetResponse ruleAssetResponse = objectMapper.convertValue(data, RuleAssetResponse.class);
        applicationFormTemplateDTO.setAssetInfo(FormTemplateAssetInfoDTO
                .builder()
                .assets(ruleAssetResponse.getAssets())
                .build());

        //map credit asset
        credits.forEach(c -> {
          Optional.ofNullable(ruleAssetResponse.getMapCreditAsset().get(Long.valueOf(c.getId())))
                  .filter(CollectionUtils::isNotEmpty)
                  .ifPresent(assetNames -> {
                    List<FormTemplateCreditAssetDTO> lstAssetName = assetNames.stream()
                            .map(assetName -> FormTemplateCreditAssetDTO.builder()
                                    .assetName(assetName)
                                    .build())
                            .collect(Collectors.toList());

                    if (!c.getLoanCreditTypeValue().equals("null")) {
                      c.setLoanAssetName(lstAssetName);
                    } else if (!c.getOverdraftCreditTypeValue().equals("null")) {
                      c.setOverAssetName(lstAssetName);
                    } else if (!c.getCardCreditTypeValue().equals("null")) {
                      c.setCardAssetName(lstAssetName);
                    }
                  });
        });
        applicationFormTemplateDTO.getDebtInfo().setCredits(credits);
      }
    } catch (Exception e) {
      log.error("Error getDataAssetFormTemplate : {}", e.getMessage());
    }

    return applicationFormTemplateDTO;
  }

  public Map<String, OtherReviewDTO> buildOtherReviews(ApplicationEntity application) {
    Map<String, OtherReviewDTO> result =
        buildExtraData(
            application.getExtraDatas(),
            new ImmutablePair<>(DEBT_INFO, OTHER_REVIEW),
            OtherReviewDTO.class)
            .stream()
            .collect(Collectors.toMap(OtherReviewDTO::getApprovalPosition, Function.identity()));
    return result;
  }
  private Set<ApplicationCreditConditionsDTO> buildCreditConditions(ApplicationEntity application) {
    Set<ApplicationCreditConditionsDTO> creditsConditions = Collections.emptySet();

    try {
      List<ApplicationCreditConditionsEntity> applicationCreditConditionExist =
          applicationCreditConditionRepository.findByApplicationIdOrderByCreditConditionId(
              application.getId());
      if (CollectionUtils.isNotEmpty(applicationCreditConditionExist)) {
        List<Long> conditionIds =
            applicationCreditConditionExist.stream()
                .map(entity -> entity.getCreditConditionId())
                .collect(Collectors.toList());

        CreditConditionClientResponse<List<CreditConditionResponse>> clientCreditConditionReponse =
            creditConditionClient.getCreditConditionByListId(conditionIds);
        if (CollectionUtils.isNotEmpty(clientCreditConditionReponse.getValue())) {
          creditsConditions =
              new HashSet<>(
                  ApplicationCreditConditionMapper.INSTANCE.toCreditConditions(
                      clientCreditConditionReponse.getValue()));

          List<String> listCategory = new ArrayList<>();
          listCategory.add(CREDIT_CONDITION_GROUP.getCode());
          listCategory.add(TIME_CONTROL_CREDIT.getCode());
          listCategory.add(TIME_CONTROL_CREDIT.getCode());
          listCategory.add(CONTROL_CREDIT_UNIT.getCode());
          GetListResponse listData =
              configurationListClient.findByListCategoryDataCodes(
                  listCategory, HeaderUtil.getToken());
          if (listData.getValue() != null && !listData.getValue().isEmpty()) {

            Map<String, String> mapGroupValue =
                listData.getValue().get(CREDIT_CONDITION_GROUP.getCode()).stream()
                    .collect(
                        Collectors.toMap(
                            Detail::getCode, Detail::getValue, (first, second) -> second));

            Map<String, String> mapTimeValue =
                listData.getValue().get(TIME_CONTROL_CREDIT.getCode()).stream()
                    .collect(
                        Collectors.toMap(
                            Detail::getCode, Detail::getValue, (first, second) -> second));
            Map<String, String> controlUnitValue =
                listData.getValue().get(CONTROL_CREDIT_UNIT.getCode()).stream()
                    .collect(
                        Collectors.toMap(
                            Detail::getCode, Detail::getValue, (first, second) -> second));

            creditsConditions.forEach(
                credit -> {
                  credit.setGroupValue(mapGroupValue.get(credit.getGroup()));
                  credit.setTimeOfControlValue(mapTimeValue.get(credit.getTimeOfControl()));
                  credit.setControlUnitValue(controlUnitValue.get(credit.getControlUnit()));
                });
          }
        }


        String format = "${%s}";
        try {
          if (CollectionUtils.isNotEmpty(creditsConditions)) {
            Iterator<ApplicationCreditConditionsDTO> it = creditsConditions.iterator();
            while (it.hasNext()) {
              ApplicationCreditConditionsDTO condition = it.next();
              List<CreditConditionParamsDTO> params = condition.getCreditConditionParams();
              String detail = condition.getDetail();

              if (CollectionUtils.isNotEmpty(params)) {
                Iterator<CreditConditionParamsDTO> innerIt =  params.iterator();
                while (innerIt.hasNext()) {
                  CreditConditionParamsDTO paramItem = innerIt.next();
                  detail = detail.replace(String.format(format, paramItem.getParameter()), paramItem.getValue());
                }
                condition.setDetail(detail);
              }
            }
          }

          log.info("buildCreditConditions end replace params in detail with : {}", creditsConditions);
        } catch (Exception e) {
          log.error("buildCreditConditions replace params in detail occurred exception: {}", e.getMessage());
        }

      }
    } catch (Exception e) {
      log.error(
          "error on get Credit Condition for BPM_ID = [{}], error message =[{}]",
          application.getBpmId(),
          e.getMessage());
    }
    return creditsConditions;
  }

  protected Personal getCurrentUser() {
    try {
      GetUserProfileResponse response =
          userManagerClient.getUserByUsername(SecurityContextUtil.getUsername());
      return response.getPersonal();
    } catch (Exception e) {
      log.error(e.getMessage());
      return Personal.builder().fullName("").phoneNumber("").build();
    }
  }

  protected void setSign(
      Personal personal, ApplicationFormTemplateDTO formTemplateDTO, ApplicationEntity entity) {
    try {
      formTemplateDTO.setProposalSign(
          String.format(
              SIGN_OTP_TEXT,
              Util.getCurrDate(DD_MM_YYYY_FORMAT),
              entity.getCreatedFullName(),
              SecurityContextUtil.getUsername()));
      formTemplateDTO.setApprovalSign(
          String.format(
              SIGN_OTP_TEXT,
              Util.getCurrDate(DD_MM_YYYY_FORMAT),
              personal.getFullName(),
              SecurityContextUtil.getUsername()));
    } catch (Exception e) {
      log.error("Error on get sign: [{}]", e.getMessage());
    }
  }
}
