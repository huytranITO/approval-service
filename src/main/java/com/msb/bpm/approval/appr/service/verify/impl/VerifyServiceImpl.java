package com.msb.bpm.approval.appr.service.verify.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.ASSET_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DOCUMENTS_INFO;
import static com.msb.bpm.approval.appr.enums.application.LoanLimit.CURRENT;
import static com.msb.bpm.approval.appr.enums.application.LoanLimit.RECOMMEND;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_CA1;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_CONTACT_LEAD;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.isRequiredCicResult;
import static com.msb.bpm.approval.appr.enums.css.CSSCreditRatingsDtlStatusCode.cssCreditRatingsDtlStatusCodes;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_NOT_COMPLETE;
import static com.msb.bpm.approval.appr.exception.DomainCode.AUTHORITY_NOT_MATCHING_WITH_LOAN;
import static com.msb.bpm.approval.appr.exception.DomainCode.EXIST_CIC_NO_RESULT;
import static com.msb.bpm.approval.appr.exception.DomainCode.FORM_TEMPLATE_GENERATE_NOT_DONE;
import static com.msb.bpm.approval.appr.exception.DomainCode.LEAST_ONE_CIC_LOOKUP;
import static com.msb.bpm.approval.appr.exception.DomainCode.LIMIT_TOTAL_LOAN_UN_MATCH;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_AUTHORITY;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_CHECKLIST_ERROR;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_USER;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_RECEPTION_NOT_ACTIVE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.CommonClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.config.CIFIConfigProperties;
import com.msb.bpm.approval.appr.client.loanproposal.LoanProposalClient;
import com.msb.bpm.approval.appr.client.otp.OtpClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.config.cic.CICProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.application.AutoDeductRate;
import com.msb.bpm.approval.appr.enums.application.GuaranteeForm;
import com.msb.bpm.approval.appr.enums.application.LoanLimit;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.application.ProcessingStep;
import com.msb.bpm.approval.appr.enums.rating.RatingSystemType;
import com.msb.bpm.approval.appr.enums.rule.RuleCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.exception.ValidationRequestException;
import com.msb.bpm.approval.appr.mapper.DecisionRuleMapping;
import com.msb.bpm.approval.appr.model.dto.*;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityRequestMapDTO;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsDtlEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.request.authority.AuthorityCheckerRequest;
import com.msb.bpm.approval.appr.model.request.authority.LimitLoanInfoRequest;
import com.msb.bpm.approval.appr.model.request.cifi.CIFISearchRequest;
import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCompleteRequest;
import com.msb.bpm.approval.appr.model.request.loanproposal.ComparatorApplicationRequest;
import com.msb.bpm.approval.appr.model.request.otp.VerifyOtpRequest;
import com.msb.bpm.approval.appr.model.response.MessageResponse;
import com.msb.bpm.approval.appr.model.response.asset.ListAssetResponse;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.cifi.CIFIInfoResponse;
import com.msb.bpm.approval.appr.model.response.collateral.CollateralClientResponse;
import com.msb.bpm.approval.appr.model.response.esb.AccountResponse;
import com.msb.bpm.approval.appr.model.response.esb.EsbResponse;
import com.msb.bpm.approval.appr.model.response.loanproposal.ComparatorApplicationResponse;
import com.msb.bpm.approval.appr.model.response.otp.VerifyOtpResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.model.response.t24.T24AccountResponse.Account;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.repository.AmlOprRepository;
import com.msb.bpm.approval.appr.repository.ApplicationCreditRepository;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.cache.EsbServiceCache;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.service.intergated.impl.T24ServiceImpl;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/5/2023, Sunday
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class VerifyServiceImpl extends AbstractBaseService implements VerifyService {

  public static final String DIGI_LENDING = "DIGI_LENDING";
  private final DecisionRuleIntegrateService decisionRuleIntegrateService;
  private final OtpClient otpClient;
  private final UserManagerClient userManagerClient;
  private final ChecklistService checklistService;
  private final Validator validator;
  private final MessageSource messageSource;
  private final ObjectMapper objectMapper;
  private final CICProperties cicProperties;
  private final LoanProposalClient loanProposalClient;
  private final ApplicationRepository applicationRepository;
  private final EsbServiceCache esbServiceCache;
  private final T24ServiceImpl t24ServiceImpl;
  private final ApplicationConfig applicationConfig;
  private final CollateralClient collateralClient;
  private final AmlOprRepository amlOprRepository;
  private final ApplicationCreditRepository applicationCreditRepository;
  private final CommonClient commonClient;
  private final CIFIConfigProperties cifiConfigProperties;
  private final CommonService commonService;
  private final ApplicationDraftRepository applicationDraftRepository;
  /**
   * Verify dữ liệu hồ sơ
   *
   * @param bpmId String
   */
  @Override
  public void verifyApplicationData(String bpmId) {
    Map<String, ApplicationDraftEntity> draftMap = draftMapByTabCode(bpmId);

    log.info("Verify data detail with bpmId {} ...", bpmId);

    draftMap.forEach((k, v) -> {
      if (!(DOCUMENTS_INFO.equalsIgnoreCase(k) || ASSET_INFO.equalsIgnoreCase(k))
          && Objects.equals(UNFINISHED, v.getStatus())) {
        log.error(
            "Verify data detail with bpmId {} is failure, because {} missing detail info or detail not complete",
            bpmId,
            v.getTabCode());
        throw new ApprovalException(APPLICATION_NOT_COMPLETE,
            new Object[]{messageSource.getMessage(v.getTabCode(), null, Util.locale())});
      }
    });

    log.info("Verify data detail with bpmId {} is successful!!!...", bpmId);
  }

  /**
   * Verify thông tin hạn mức tín dụng vs tất cả các khoản vay có trong hồ sơ
   *
   * @param limitCredits Set<ApplicationLimitCreditDTO>
   * @param credits      Set<ApplicationCreditDTO>
   */
  @Override
  public void verifyLimitCredit(Set<ApplicationLimitCreditDTO> limitCredits,
      Set<ApplicationCreditDTO> credits) {
    log.info("Verify credit limit {} , credits {}", limitCredits, credits);

    limitCredits.forEach(limitCredit -> {
      if (RECOMMEND.name().equalsIgnoreCase(limitCredit.getLoanLimit())) {
        verifyRecommendLimitCredit(limitCredit, calculateLimitCredit(credits));
      } else {
        BigDecimal total = limitCredit.getTotal();
        BigDecimal totalCompare = limitCredit.calculateTotal();
        log.info("Total {} , total compare {}", total, totalCompare);
        if (total != null && totalCompare != null) {
          verifyTotalLoan(total, totalCompare);
        }
      }
    });

    log.info("Verify credit limit is successful...");
  }

  /**
   * Verify thẩm quyền phê duyệt khoản vay hiện tại vs thẩm quyền phê duyệt khoản vay trả về từ
   * desicion rule
   *
   * @param authorityRequestMapDTO AuthorityRequestMapDTO
   */
  @Override
  public void verifyApplicationAuthority(AuthorityRequestMapDTO authorityRequestMapDTO) {

    AuthorityCheckerRequest req = DecisionRuleMapping.INSTANCE.toAuthorityCheckerRequest(
        authorityRequestMapDTO);

    authorityRequestMapDTO.getLimitCredits().forEach(limit -> {
      LimitLoanInfoRequest info = new LimitLoanInfoRequest();
      info.setLoanProductCollateral(
          Objects.nonNull(limit.getLoanProductCollateral()) ? limit.getLoanProductCollateral()
              .doubleValue() : 0);
      info.setUnsecureProduct(
          Objects.nonNull(limit.getUnsecureProduct()) ? limit.getUnsecureProduct().doubleValue()
              : 0);
      info.setOtherLoanProductCollateral(Objects.nonNull(limit.getOtherLoanProductCollateral())
          ? limit.getOtherLoanProductCollateral().doubleValue() : 0);
      info.setOtherUnsecureProduct(
          Objects.nonNull(limit.getOtherUnsecureProduct()) ? limit.getOtherUnsecureProduct()
              .doubleValue() : 0);

      if (CURRENT.name().equalsIgnoreCase(limit.getLoanLimit())) {
        req.setCurrentLimit(info);
      } else if (LoanLimit.PRE_APPROVE.name().equalsIgnoreCase(limit.getLoanLimit())) {
        req.setPreApproveLimit(info);
      } else {
        req.setRecommendLimit(info);
      }
    });

    log.info("Verify authority, request : {}", req);

    AuthorityDetailDTO authorityDetail = (AuthorityDetailDTO) decisionRuleIntegrateService.getDecisionRule(
        authorityRequestMapDTO.getApplicationId(), RuleCode.R002, req);
    if (authorityDetail == null || StringUtils.isBlank(authorityDetail.getCode())) {
      throw new ApprovalException(NOT_FOUND_AUTHORITY);
    }

    log.info("Authority detail : {}", authorityDetail);

    if (!authorityDetail.getCode()
        .equalsIgnoreCase(authorityRequestMapDTO.getLoanApprovalPosition())) {
      log.error("Verify authority is failure...");
      throw new ApprovalException(AUTHORITY_NOT_MATCHING_WITH_LOAN);
    }

    log.info("Verify authority is successful...");
  }

  /**
   * Verify mã OTP
   *
   * @param request PostCompleteRequest
   */
  @Override
  public void verifyOTP(PostCompleteRequest request) {
    log.info("Verify OTP : {}", request);
    VerifyOtpRequest otpRequest = VerifyOtpRequest.builder()
        .transactionId(request.getTransactionId())
        .ivalueTime(120)
        .otp(request.getOtp())
        .otpId(request.getOtpId())
        .build();

    VerifyOtpResponse otpResponse = otpClient.verifyOtp(otpRequest);
    if (!otpResponse.isSuccess()) {
      log.error("Verify OTP is failure...");
      throw new ApprovalException(DomainCode.VERIFY_OTP_ERROR,
          new Object[]{otpRequest});
    }

    log.info("Verify OTP is successful...");
  }

  /**
   * Kiểm tra thông tin CB tiếp nhận
   *
   * @param userReception  String
   * @param transitionRule RuleResponse
   */
  @Override
  public void verifyUserReception(String userReception, RuleResponse transitionRule) {
    // If     : nextStep = Team lead
    // Then   : Không verify thông tin CB tiếp nhận
    if (transitionRule != null) {
      String stepCode = transitionRule.getRuleDataItem().getData().getStepCode();
      if (ProcessingStep.TL_COORDINATOR.getCode().equalsIgnoreCase(stepCode)) {
        return;
      }
    }

    // Với nextStep là các step còn lại thì verify thông tin CB tiếp nhận
    log.info("Verify user reception : {}", userReception);
    if (StringUtils.isBlank(userReception)) {
      log.error("Verify user reception failure, user reception is {}", userReception);
      throw new ApprovalException(NOT_FOUND_USER, new Object[]{userReception});
    }

    GetUserProfileResponse response = null;
    try {
       response = userManagerClient.getUserByUsername(userReception);

    } catch (Exception e) {
      String httpBody = String.valueOf(((ApprovalException) e).getArgs()[1]);
      MessageResponse msg = JsonUtil.convertString2Object(httpBody, MessageResponse.class, objectMapper);
      if ("UMS-053".equals(msg.getCode())) {
        log.error("Verify user reception failure, not found user information : {}", userReception);
        throw new ApprovalException(NOT_FOUND_USER, new Object[]{userReception});
      }
    }

    if (response == null || response.getUser() == null) {
      log.error("Verify user reception failure, not found user information : {}", userReception);
      throw new ApprovalException(NOT_FOUND_USER, new Object[]{userReception});
    }

    if (Objects.equals(Boolean.FALSE, response.getUser().getEnabled())) {
      log.error("Verify user reception failure, user : {} is inactive", userReception);
      throw new ApprovalException(USER_RECEPTION_NOT_ACTIVE, new Object[]{userReception});
    }
    log.info("Verify user reception : {} is successful!!!...", userReception);
  }

  /**
   * Kiểm tra thông tin danh mục hồ sơ
   *
   * @param bpmId            String
   * @param isCheckGenerator Boolean
   */
  @Override
  public void verifyDocumentsInfo(String bpmId, boolean isCheckGenerator) {
    log.info(
        "VerifyServiceImpl.verifyDocumentsInfo() start with bpmId {} and isCheckGenerator : {}",
        bpmId, isCheckGenerator);
    ChecklistBaseResponse<GroupChecklistDto> response = checklistService.getChecklistByRequestCode(
        bpmId);
    if (response == null || response.getData() == null) {
      log.error(
          "Verify documents checklist failure, because not found checklist with bpmId : {}, verify data : {}",
          bpmId, JsonUtil.convertObject2String(response, objectMapper));
      throw new ApprovalException(NOT_FOUND_CHECKLIST_ERROR, new Object[]{bpmId});
    }

    List<ChecklistDto> validateManualChecklist = response.getData().getListChecklist()
        .stream()
        .filter(filterChecklist -> Objects.equals(Boolean.FALSE, filterChecklist.getIsGenerated()))
        .collect(Collectors.toList());

    verifyDocumentsInfo(bpmId, validateManualChecklist, false);

    List<ChecklistDto> validateGeneratorChecklist = response.getData().getListChecklist()
        .stream()
        .filter(filterChecklist -> Objects.equals(Boolean.TRUE, filterChecklist.getIsGenerated()))
        .collect(Collectors.toList());

    if (CollectionUtils.isNotEmpty(validateGeneratorChecklist)) {
      try {
        verifyDocumentsInfo(bpmId, validateGeneratorChecklist, true);
      } catch (Exception e) {
        if (isCheckGenerator) {
          throw e;
        }
      }
    }

    log.info("VerifyServiceImpl.verifyDocumentsInfo() end with bpmId : {}", bpmId);
  }

  @Override
  @Transactional
  public void verifyUserCanView(ApplicationEntity applicationEntity) {

    List<String> userNames = applicationEntity.getHistoryApprovals().stream()
        .map(ApplicationHistoryApprovalEntity::getUsername)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());

    List<String> contactLeadUserNames = applicationEntity.getHistoryApprovals().stream()
        .filter(filterUser -> PD_RB_CONTACT_LEAD.equals(filterUser.getFromUserRole()))
        .map(ApplicationHistoryApprovalEntity::getCreatedBy)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());

    if (CollectionUtils.isNotEmpty(contactLeadUserNames)) {
      userNames.addAll(contactLeadUserNames);
    }

    if (!userNames.contains(SecurityContextUtil.getCurrentUser())) {
      throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
    }
  }

  /**
   * Kiểm tra lại kết qua tra cứu CIC khi đệ trình
   * Chỉ áp dụng với các cấp CA/CM
   *
   * @param applicationEntity
   */
  @Override
  public void verifyCicWhenSubmit(ApplicationEntity applicationEntity) {
    log.info("Verify CIC result with bpmId : {} , processing role : {} when submit",
        applicationEntity.getBpmId(), applicationEntity.getProcessingRole());

    if (!isRequiredCicResult(applicationEntity.getProcessingRole())) {
      return;
    }

    if (CollectionUtils.isEmpty(applicationEntity.getCics())) {
      log.error(
          "Verify CIC result with bpmId : {} , processing role : {} when submit failure, because at least one CIC lookup message is required",
          applicationEntity.getBpmId(), applicationEntity.getProcessingRole());
      throw new ApprovalException(LEAST_ONE_CIC_LOOKUP);
    }

    applicationEntity.getCics().forEach(cic -> {
      if (!cicProperties.getAcceptedSubmitStatus().contains(cic.getStatusCode())) {
        throw new ApprovalException(EXIST_CIC_NO_RESULT);
      }
    });
  }

  public Object verifyLdpStatus(ApplicationEntity applicationEntity) {
    log.info("Verify LDP with ldpStatus : {} when submit",
        applicationEntity.getLdpStatus());
    // Check user role RM
    if (!PD_RB_RM.name().equals(applicationEntity.getProcessingRole())) {
      return ComparatorApplicationResponse.builder()
          .valid(true)
          .build();
    }
    // Compare with application from LDP
    boolean result = compareWithApplicationLDP(applicationEntity);

    return ComparatorApplicationResponse.builder()
        .valid(result)
        .build();
  }

  @Override
  @Transactional
  public Object verifyLdpStatusWhenSubmit(String bpmId) {
    ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));
    return verifyLdpStatus(applicationEntity);
  }
  @Override
  public boolean compareWithApplicationLDP(ApplicationEntity applicationEntity) {
    log.info("Comparator application from loan proposal service with bpmId : {}", applicationEntity.getBpmId());

    // Build request
    ComparatorApplicationRequest request = buildRequest(applicationEntity);

    //Call Loan proposal service
    ComparatorApplicationResponse response = loanProposalClient.callLoanProposalService(applicationEntity.getBpmId(), request);

    //response.getValid() = true : Không có sự thay đổi từ LDP
    //response.getValid() = false : Có sự thay đổi từ LDP
    return response.getValid();
  }

  public ComparatorApplicationRequest buildRequest(ApplicationEntity applicationEntity) {
    ComparatorApplicationRequest request = new ComparatorApplicationRequest();
    GetApplicationDTO applicationDTO = super.getApplicationInfo(applicationEntity);

    CustomerDTO customer = applicationDTO.getInitializeInfo().getCustomerAndRelationPerson().getCustomer();
    Set<CustomerDTO> customerRelations = applicationDTO.getInitializeInfo().getCustomerAndRelationPerson().getCustomerRelations();

    Map<Long, String> mapCusFullName = buildMapFullName(customer, customerRelations);
    Set<ApplicationIncomeDTO> incomes = applicationDTO.getInitializeInfo().getIncomes();
    remapIncomeOwnerName(incomes, mapCusFullName);
    request.getData().put("application", applicationDTO.getInitializeInfo().getApplication());
    request.getData().put("customer", customer);
    request.getData().put("customerRelations", customerRelations);
    request.getData().put("enterpriseRelations", applicationDTO.getInitializeInfo().getCustomerAndRelationPerson().getEnterpriseRelations());
    request.getData().put("cic", applicationDTO.getInitializeInfo().getCustomerAndRelationPerson().getCic());
    request.getData().put("amlOpr", applicationDTO.getInitializeInfo().getCustomerAndRelationPerson().getAmlOpr());
    request.getData().put("applicationIncomes", incomes);
    request.getData().put("limitCredits", applicationDTO.getDebtInfo().getLimitCredits());
    request.getData().put("applicationCredits", applicationDTO.getDebtInfo().getCredits());
    request.getData().put("creditRatings", applicationDTO.getDebtInfo().getCreditRatings());
    request.getData().put("repayment", applicationDTO.getDebtInfo().getRepayment());
    request.getData().put("specialRiskContents", applicationDTO.getDebtInfo().getSpecialRiskContents());
    request.getData().put("additionalContents", applicationDTO.getDebtInfo().getAdditionalContents());
    request.getData().put("otherReviews", applicationDTO.getDebtInfo().getOtherReviews());
    request.getData().put("creditConditions", applicationDTO.getDebtInfo().getCreditConditions());
    request.getData().put("phoneExpertise", applicationDTO.getDebtInfo().getPhoneExpertise());
    request.getData().put("effectivePeriod", applicationDTO.getDebtInfo().getEffectivePeriod());
    request.getData().put("effectivePeriodUnit", applicationDTO.getDebtInfo().getEffectivePeriodUnit());
    request.getData().put("proposalApprovalPosition", applicationDTO.getDebtInfo().getProposalApprovalPosition());
    request.getData().put("loanApprovalPosition", applicationDTO.getDebtInfo().getLoanApprovalPosition());
    request.getData().put("loanApprovalPositionValue", applicationDTO.getDebtInfo().getLoanApprovalPositionValue());
    request.getData().put("applicationAuthorityLevel", applicationDTO.getDebtInfo().getApplicationAuthorityLevel());
    request.getData().put("priorityAuthority", applicationDTO.getDebtInfo().getPriorityAuthority());
    request.getData().put("assetInfo", buildAssetProposal(applicationEntity.getBpmId()));
    request.getData().put("insurance", applicationEntity.isInsurance());
    request.getData().put("applicationContact", applicationDTO.getInitializeInfo().getCustomerAndRelationPerson().getApplicationContact());
    if (applicationDTO.getDebtInfo().getRepayment() != null) {
      request.getData().put("desCreditAvailable", applicationDTO.getDebtInfo().getRepayment().getEvaluate());
    }

    return request;

  }

  public Map<Long, String> buildMapFullName(CustomerDTO customer, Set<CustomerDTO> customerRelations) {
    Map<Long, String> mapCusFullName = new HashMap<>();
    mapCusFullName.put(customer.getRefCustomerId(),
        ((IndividualCustomerDTO) customer).getFullName());

    if (CollectionUtils.isNotEmpty(customerRelations)) {
      customerRelations.forEach(
          customerRelation -> mapCusFullName.put(customerRelation.getRefCustomerId(),
              ((IndividualCustomerDTO) customerRelation).getFullName()));
    }

    return mapCusFullName;
  }

  public void remapIncomeOwnerName(Set<ApplicationIncomeDTO> incomes, Map<Long, String> mapCusFullName) {
    if (CollectionUtils.isNotEmpty(incomes)) {
      incomes.forEach(income -> {
        if (ACTUALLY.equals(income.getIncomeRecognitionMethod())) {
          ((ActuallyIncomeDTO)income).getIncomeItems().forEach(incomeItem -> {
            if (mapCusFullName.containsKey(incomeItem.getRefCustomerId())) {
              incomeItem.setIncomeOwnerName(mapCusFullName.get(incomeItem.getRefCustomerId()));
            }
          });
        } else {
          ((ExchangeIncomeDTO)income).getIncomeItems().forEach(incomeItem -> {
            if (mapCusFullName.containsKey(incomeItem.getRefCustomerId())) {
              incomeItem.setIncomeOwnerName(mapCusFullName.get(incomeItem.getRefCustomerId()));
            }
          });
        }
      });
    }
  }

  private void verifyDocumentsInfo(String bpmId, List<ChecklistDto> validateChecklist,
      boolean isGenerator) {
    GroupChecklistDto groupChecklistDto = new GroupChecklistDto();
    groupChecklistDto.setListChecklist(
        validateChecklist.stream().sorted(Comparator.comparing(ChecklistDto::getOrderDisplay))
            .collect(
                Collectors.toList()));
    Set<ConstraintViolation<Object>> violations = validator.validate(groupChecklistDto);
    if (CollectionUtils.isNotEmpty(violations)) {
      log.error(
          "Verify documents checklist with bpmId : {} failure, because checklist missing required file, verify data : {}",
          bpmId, JsonUtil.convertObject2String(validateChecklist, objectMapper));

      throw new ValidationRequestException(
          isGenerator ? FORM_TEMPLATE_GENERATE_NOT_DONE : APPLICATION_NOT_COMPLETE,
          new Object[]{messageSource.getMessage(DOCUMENTS_INFO, null, Util.locale())},
          new ConstraintViolationException(violations));
    }
  }

  private void verifyRecommendLimitCredit(ApplicationLimitCreditDTO limitCredit,
      ApplicationLimitCreditDTO compareLimitCredit) {
    verifyGreaterOrEqualTotalLoan(limitCredit.getLoanProductCollateral(),
        compareLimitCredit.getLoanProductCollateral());
    verifyGreaterOrEqualTotalLoan(limitCredit.getOtherLoanProductCollateral(),
        compareLimitCredit.getOtherLoanProductCollateral());
    verifyGreaterOrEqualTotalLoan(limitCredit.getUnsecureProduct(),
        compareLimitCredit.getUnsecureProduct());
    verifyGreaterOrEqualTotalLoan(limitCredit.getOtherUnsecureProduct(),
        compareLimitCredit.getOtherUnsecureProduct());
    verifyGreaterOrEqualTotalLoan(limitCredit.calculateTotal(),
        compareLimitCredit.calculateTotal());
  }

  private void verifyTotalLoan(BigDecimal limit, BigDecimal compare) {
    if (limit.compareTo(compare) != 0) {
      log.error("Verify credit limit is failure , limit amount {} <> compare amount {}", limit,
          compare);
      throw new ApprovalException(LIMIT_TOTAL_LOAN_UN_MATCH);
    }
  }

  private void verifyGreaterOrEqualTotalLoan(BigDecimal limit, BigDecimal compare) {
    if(limit == null) {
      limit = BigDecimal.ZERO;
    }
    if (compare == null) {
      return;
    }
    if (limit.compareTo(compare) < 0) {
      log.error("Verify credit limit is failure , limit amount {} < compare amount {}", limit,
          compare);
      throw new ApprovalException(LIMIT_TOTAL_LOAN_UN_MATCH);
    }
  }

  @Override
  public void verifyAccountNumber(ApplicationEntity entity) {
    log.info("START verifyAccountNumber with bpmId={}", entity.getBpmId());
    String cifNumber = entity.getCustomer().getCif();
    List<String> accountNumbers = entity.getCredits().stream()
        .filter(acc -> Objects.nonNull(acc.getCreditCard()))
        .filter(acc -> !AutoDeductRate.V001.name().equalsIgnoreCase(acc.getCreditCard().getAutoDeductRate()))
        .filter(acc -> Objects.nonNull(acc.getCreditCard().getDeductAccountNumber()))
        .map(c -> c.getCreditCard().getDeductAccountNumber())
        .collect(Collectors.toList());
    String errorMessage = "";
    if (CollectionUtils.isNotEmpty(accountNumbers)) {
      List<String> coreAccountNumbers;
      if (applicationConfig.getAccountSource().isT24Account()) {
        coreAccountNumbers = getT24AccountNumbers(cifNumber);
      } else {
        coreAccountNumbers = getEsbAccountNumbers(cifNumber);
      }
      errorMessage = accountNumbers.stream()
          .filter(item ->!coreAccountNumbers.contains(item))
          .map(item -> "'" + item + "'")
          .collect(Collectors.joining(", "));
    }
    log.info("END verifyAccountNumber with bpmId={}", entity.getBpmId());
    if (!errorMessage.isEmpty()) {
      throw new ApprovalException(DomainCode.ESB_ACCOUNT_INFO_ERROR, new Object[] {errorMessage});
    }
  }

  @Override
  public void verifyRmCommitStatus(ApplicationEntity entity) {
      log.info(String.format("isRmStatus: %b", entity.isRmStatus()));
      if (PD_RB_RM.name().equals(entity.getProcessingRole()) && !entity.isRmCommitStatus()) {
        throw new ApprovalException(DomainCode.RM_NOT_CHECK_ORIGINAL);
      }
  }

  @Override
  public void verifyOpRiskCollateral(ApplicationEntity applicationEntity) {
    if (ProcessingRole.isRequiredOpRiskCollateralResult(applicationEntity.getProcessingRole()) &&
        !validateOpRiskApprovalService(applicationEntity)) {
      log.error(
          "OpRisk asset collateral with bpmId {} is failure, because {} missing detail info or detail not complete",
          applicationEntity.getBpmId(),
          "COLLATERAL_ASSET_OPRISK_ASSET");
      throw new ApprovalException(APPLICATION_NOT_COMPLETE,
          new Object[]{"COLLATERAL_ASSET_OPRISK_ASSET"});
    }
  }

  @Override
  public void verifyAssetInfoStatus(String bpmId) {
    // data new
    Map<String, ApplicationDraftEntity> draftMap = draftMapByTabCode(bpmId);

    log.info("Verify data asset info with bpmId {} ...", bpmId);
    int countCollateral = applicationCreditRepository.countApplicationCreditByBpmId(bpmId, GuaranteeForm.COLLATERAL.getCode());
    if(ObjectUtils.isNotEmpty(draftMap) && ObjectUtils.isNotEmpty(draftMap.get(ASSET_INFO))) {
      draftMap.forEach((k, v) -> {
        if (ASSET_INFO.equals(k)) {
          PostAssetInfoRequest request = null;
          try {
            if(v.getData() != null) {
              request = objectMapper.readValue(new String(v.getData(), StandardCharsets.UTF_8),
                      PostAssetInfoRequest.class);
            }
          } catch (JsonProcessingException e) {
            log.error("Parse string to json fail: ", e);
            throw new ApprovalException(APPLICATION_NOT_COMPLETE,
                    new Object[]{messageSource.getMessage(v.getTabCode(), null, Util.locale())});
          }
          if( Objects.equals(UNFINISHED, v.getStatus())
                  && (countCollateral > 0
                      || (ObjectUtils.isNotEmpty(request)
                        && ((ObjectUtils.isNotEmpty(request.getCollateral()) && ObjectUtils.isNotEmpty(request.getAssetData()))
                            || ObjectUtils.isNotEmpty(request.getAssetData()))))
          ) {
            log.error(
                    "Verify data asset info with bpmId {} is failure, because {} missing detail info or detail not complete",
                    bpmId,
                    v.getTabCode());
            throw new ApprovalException(APPLICATION_NOT_COMPLETE,
                    new Object[]{"Tab tài sản"});
          }
        }
      });
    } else {
      log.info("Verify data asset info old with bpmId {} ...", bpmId);
      // data old
      CollateralClientResponse response = collateralClient.checkCollateralAssetStatus(bpmId, countCollateral);
      if (response != null && !((boolean) response.getData())) {
        log.error(
                "Collateral asset with bpmId {} is failure, because {} missing detail info or detail not complete",
                bpmId,
                "COLLATERAL_ASSET");
        throw new ApprovalException(APPLICATION_NOT_COMPLETE,
                new Object[]{"COLLATERAL_ASSET"});
      }
    }
  }

  @Override
  public void verifyCIFIStatus(String bpmId) {
    try {
      log.info("verifyCIFIStatus with bpmId {} ...", bpmId);
      CIFISearchRequest request = CIFISearchRequest.builder()
              .channel(DIGI_LENDING)
              .channelRequestId(bpmId)
              .build();
      String endpointSearchCIFI = UriComponentsBuilder
              .fromUriString(cifiConfigProperties.getBaseUrl() + cifiConfigProperties.getSearchUrl())
              .toUriString();
      log.info("Call CIFI with url {} and request {}", endpointSearchCIFI, JsonUtil.convertObject2String(request, objectMapper));

      CIFIInfoResponse cifiInfoResponse = commonClient.callApiCommon(request, HttpMethod.POST, endpointSearchCIFI, CIFIInfoResponse.class );
      log.info("Call CIFI with bpmId {} and response {}", bpmId, JsonUtil.convertObject2String(cifiInfoResponse, objectMapper));

      if(!ObjectUtils.isEmpty(cifiInfoResponse)
              && ObjectUtils.isNotEmpty(cifiInfoResponse.getData())
              && ObjectUtils.isNotEmpty(cifiInfoResponse.getData().getItems())) {
        if(!(cifiInfoResponse.getData().getItems().stream()
                .allMatch(cifi -> cifiConfigProperties.getCompleteStatus().contains(cifi.getStatus().getCode())))) {
          throw new ApprovalException(DomainCode.CIFI_NOT_COMPLETE);
        }
      }
    } catch (Exception exception) {
      log.error("verifyCIFIStatus with bpmId {} error {}", bpmId, exception);
      throw new ApprovalException(DomainCode.CIFI_NOT_COMPLETE);
    }
  }

  @Override
  public void verifyIncomeCreditRatingsCss(ApplicationEntity applicationEntity) {

    log.info("Verify data income credit ratings with bpmId {} ...", applicationEntity.getBpmId());
    Set<ApplicationCreditRatingsEntity> creditRatings = applicationEntity.getCreditRatings();
    if(!CollectionUtils.isEmpty(creditRatings)) {
      Set<ApplicationCreditRatingsEntity> creditRatingsCss = creditRatings
              .stream()
              .filter(filter -> RatingSystemType.CSS.name().equalsIgnoreCase(filter.getRatingSystem()))
              .collect(Collectors.toSet());
      if(!CollectionUtils.isEmpty(creditRatingsCss)) {
        creditRatingsCss.forEach(itemCreditRatingCss -> {
          if(!ObjectUtils.isEmpty(itemCreditRatingCss)) {
            Set<ApplicationCreditRatingsDtlEntity> creditRatingsDtlCss = itemCreditRatingCss.getCreditRatingsDtls();
            if(!CollectionUtils.isEmpty(creditRatingsDtlCss)) {
              creditRatingsDtlCss.forEach(itemCreditRatingDtlCss -> {
                if(!ObjectUtils.isEmpty(itemCreditRatingDtlCss) && !cssCreditRatingsDtlStatusCodes().contains(itemCreditRatingDtlCss.getStatus())) {
                  log.info("Verify data income credit ratings detail css with RatingId {} IdentityCard {} ...", itemCreditRatingCss.getRatingId(), itemCreditRatingDtlCss.getIdentityCard());
                  throw new ApprovalException(DomainCode.CSS_VERIFY_INVALID, new Object[]{itemCreditRatingCss.getRatingId()});
                }
              });
            }
          }
        });
      }
    } else  {
      log.info("Verify data income credit ratings is empty with bpmId {} ...", applicationEntity.getBpmId());
    }
  }

  private boolean validateOpRiskApprovalService(ApplicationEntity applicationEntity) {
    List<AmlOprEntity> amlOprEntities = amlOprRepository.findByApplicationIdAndQueryType(applicationEntity.getId(), ApplicationConstant.OPR_ASSET_TAG)
            .orElse(null);
    ApplicationDraftEntity draftEntity = applicationDraftRepository.findByBpmIdAndTabCodeAndStatus(applicationEntity.getBpmId(), ASSET_INFO, 1).orElse(null);
    if (Objects.nonNull(draftEntity) && Objects.nonNull(amlOprEntities)) {
      List<String> amlOpRiskCodes = amlOprEntities.stream().map(aml -> aml.getIdentifierCode().concat(aml.getAssetGroup()).concat(aml.getAssetType())).collect(Collectors.toList());
      ListAssetResponse response = JsonUtil.convertBytes2Object(draftEntity.getData(), ListAssetResponse.class, objectMapper);
      if (Objects.nonNull(response) && Objects.nonNull(response.getAssetData())) {
        List<String> result = response.getAssetData().stream().map(data -> data.getAssetCode().concat(data.getAssetGroup()).concat(data.getAssetType())).filter(code -> !amlOpRiskCodes.contains(code)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(result)) {
          return false;
        }
      }
    }
    if (CollectionUtils.isEmpty(amlOprEntities)) {
      return collateralClient.validOpr(applicationEntity.getBpmId(), applicationEntity.getProcessingRole());
    }
    if (PD_RB_RM.name().equals(applicationEntity.getProcessingRole())) {
      return amlOprEntities.stream()
          .allMatch(amlOpr -> amlOpr.getId() != null);
    } else if (PD_RB_CA1.name().equals(applicationEntity.getProcessingRole())) {
      return amlOprEntities.stream()
          .allMatch(amlOpr -> amlOpr.getId() != null
              && !ApplicationConstant.QUERY_ERROR_CODE.equals(amlOpr.getResultCode()));
    }
    return true;
  }
  public List<String> getEsbAccountNumbers(String cifNumber) {
    log.info("START getEsbAccountNumbers with cifNumber={}", cifNumber);
    //Call esb core to get account data
    if (StringUtils.isEmpty(cifNumber)) {
      return Collections.emptyList();
    }
    EsbResponse esbResponse = esbServiceCache.callGetEsbAccountInfo(cifNumber);
    if (Objects.isNull(esbResponse.getData())) {
      return Collections.emptyList();
    }
    List<AccountResponse> responses = new ArrayList<>();
    if (Objects.nonNull(esbResponse.getData().getListOfCAAccounts())) {
      responses.addAll(esbResponse.getData().getListOfCAAccounts().getCaAccounts());
    }
    if (Objects.nonNull(esbResponse.getData().getListOfSAAccounts())) {
      responses.addAll(esbResponse.getData().getListOfSAAccounts().getSaAccounts());
    }
    if (Objects.nonNull(esbResponse.getData().getListOfFDAccounts())) {
      responses.addAll(esbResponse.getData().getListOfFDAccounts().getFdAccounts());
    }
    if (Objects.nonNull(esbResponse.getData().getListOfLNAccounts())) {
      responses.addAll(esbResponse.getData().getListOfLNAccounts().getLnAccounts());
    }
    log.info("END getEsbAccountNumbers with cifNumber={}", cifNumber);
    return responses
        .stream()
        .map(AccountResponse::getAccountNumber)
        .collect(Collectors.toList());
  }
  public List<String> getT24AccountNumbers(String cifNumber) {
    log.info("START getT24AccountNumbers with cifNumber={}", cifNumber);
    if (StringUtils.isEmpty(cifNumber)) {
      return Collections.emptyList();
    }

    //Call T24 core to get account data
    List<Account> responses = t24ServiceImpl.getAccountsFromT24(cifNumber);
    log.info("END getT24AccountNumbers with cifNumber={}", cifNumber);
    return responses
        .stream()
        .map(Account::getAccountNumber)
        .collect(Collectors.toList());
  }
}
