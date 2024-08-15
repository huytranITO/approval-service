package com.msb.bpm.approval.appr.service.intergated.impl;

import static com.msb.bpm.approval.appr.constant.Constant.EMAIL_ADDRESS_MSB;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.ADDRESS_OF_WORK_AGENCY_CODE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.ANOTHER_ADDRESS_CODE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.APPLICATIONBPMID;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.BPM_PD;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.CARD_FORM_PHYSICAL;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.CARD_FORM_VIRTUAL;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.CHANNEL;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.CREATE_CLIENT_DEFAULT_TYPE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.CURRENT_RESIDENTIAL_ADDRESS_CODE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.CUSTOMERNAME;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.DATEOBIRTH;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.DEFAULT_INSTITUTION_CODE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.DELIMITER;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.EMAIL;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.FULL;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.MAIN_CARD;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.MIN;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.OFFICE_ADDRESS_CODE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.PCD;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.PHONE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.PHYSICAL_ENG;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.PHYSICAL_VIE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.PISSL;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.SUB_CARD;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.TRANSACTION_OFFICE_CODE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.V001;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.V002;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.V003;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.VIRTUAL_ENG;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.VIRTUAL_VIE;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.WAY_4;
import static com.msb.bpm.approval.appr.enums.card.IntegrationResponseCode.BAD_REQUEST;
import static com.msb.bpm.approval.appr.enums.card.IntegrationResponseCode.TIMEOUT;
import static com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail.ERR_CARD;
import static com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail.ERR_CLIENT;
import static com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail.ERR_SUB_CARD;
import static com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail.INPROGESS_CARD;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CARD_BRANCH_MAPPING;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.CARD_SEND_FEEDBACK_CUSTOMER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.card.CardClient;
import com.msb.bpm.approval.appr.client.configuration.ConfigurationListClient;
import com.msb.bpm.approval.appr.client.core.CoreIntegrationClient;
import com.msb.bpm.approval.appr.client.core.response.CoreCustomer;
import com.msb.bpm.approval.appr.client.core.response.CoreCustomerInfo;
import com.msb.bpm.approval.appr.config.card.request.CheckCardCreatedRequest;
import com.msb.bpm.approval.appr.config.card.request.CreateCardRequest;
import com.msb.bpm.approval.appr.config.card.request.CreateClientRequest;
import com.msb.bpm.approval.appr.config.card.request.CreateSubCardRequest;
import com.msb.bpm.approval.appr.config.card.response.CheckCardCreatedResponse;
import com.msb.bpm.approval.appr.config.card.response.CheckCardCreatedResponse.Data.IssueContract.CardInfo;
import com.msb.bpm.approval.appr.config.card.response.CheckClientResponse;
import com.msb.bpm.approval.appr.config.card.response.CreateCardErrorResponse;
import com.msb.bpm.approval.appr.config.card.response.CreateCardResponse;
import com.msb.bpm.approval.appr.config.card.response.CreateClientResponse;
import com.msb.bpm.approval.appr.config.card.response.CreateSubCardResponse;
import com.msb.bpm.approval.appr.constant.IntegrationConstant;
import com.msb.bpm.approval.appr.email.EmailSender;
import com.msb.bpm.approval.appr.enums.application.CreditType;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.card.IntegrationResponseCode;
import com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.SubCreditCardEntity;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse.Detail;
import com.msb.bpm.approval.appr.repository.ApplicationCreditCardRepository;
import com.msb.bpm.approval.appr.repository.ApplicationCreditRepository;
import com.msb.bpm.approval.appr.repository.ApplicationHistoricIntegrationRepository;
import com.msb.bpm.approval.appr.repository.ApplicationHistoryApprovalRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.ApplicationSubCreditCardRepository;
import com.msb.bpm.approval.appr.service.intergated.BaseIntegrationService;
import com.msb.bpm.approval.appr.util.DateUtils;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.StringUtil;
import com.msb.bpm.approval.appr.util.VNCharacterUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/*
 * @author: Nguyễn Văn Bảo
 * @since: 8/6/2023
 * @email:  baonv2@msb.com.vn
 * */
@Slf4j
@Service
@AllArgsConstructor
public class BaseIntegrationServiceImpl implements BaseIntegrationService {
  private final CoreIntegrationClient coreIntegrationClient;
  private final CardClient cardClient;
  private final ApplicationHistoricIntegrationRepository integrationRepository;
  private final ApplicationCreditCardRepository creditCardRepository;
  private final ApplicationCreditRepository creditRepository;
  private final ApplicationSubCreditCardRepository subCreditCardRepository;
  private final ApplicationRepository applicationRepository;
  private final ObjectMapper objectMapper;
  @Value("${card.client.errors}")
  private  String[] clientErrors;
  @Value("${card.credit.errors}")
  private String[] creditErrors;
  private final EmailSender emailSender;
  private final ApplicationHistoryApprovalRepository historyApprovalRepository;
  private final ConfigurationListClient configurationListClient;

  @Override
  @Transactional
  public void createSubCard(ApplicationHistoricIntegration cardInfo) {
    log.info("createSubCard START with status {}, code {}", cardInfo.getIntegratedStatusDetail(), cardInfo.getErrorCode());
    Long applicationId = cardInfo.getApplicationId();
    ApplicationEntity application = applicationRepository.getReferenceById(applicationId);
    Optional<ApplicationCreditEntity> creditEntity = creditRepository.findByApplicationId(applicationId).stream().filter(item -> item.getCreditType().equals(CreditType.CARD.getCode())).findFirst();
    ApplicationCreditCardEntity creditCardEntity = creditEntity.get().getCreditCard();
    Long creditCardId = cardInfo.getApplicationCreditCardId() == null ? creditCardEntity.getId() : cardInfo.getApplicationCreditCardId();
    ApplicationCreditCardEntity creditCard = creditCardRepository.findById(creditCardId).get();
    Set<SubCreditCardEntity> subs = creditEntity.get().getCreditCard().getSubCreditCards();
    if (CollectionUtils.isEmpty(subs)) return;

    if (creditCardEntity.getCardForm().equals(CARD_FORM_PHYSICAL)) {
      String address = StringUtil.getValue(creditEntity.get().getCreditCard().getCardReceiveAddress());
      switch (address) {
        case ANOTHER_ADDRESS_CODE:
        case CURRENT_RESIDENTIAL_ADDRESS_CODE:
        case ADDRESS_OF_WORK_AGENCY_CODE:
          cardInfo.setAddress(VNCharacterUtils.removeAccent(StringUtil.getValue(creditCardEntity.getAddressLine())
              + DELIMITER + StringUtil.getValue(creditCardEntity.getWardValue())
              + DELIMITER + StringUtil.getValue(creditCardEntity.getDistrictValue())
              + DELIMITER + StringUtil.getValue(creditCardEntity.getCityValue())));
          break;
        case TRANSACTION_OFFICE_CODE:
          cardInfo.setAddress(application.getBusinessCode());
          break;
        case OFFICE_ADDRESS_CODE:
          cardInfo.setAddress(creditCardEntity.getWay4BranchCode());
          break;
        default:
          cardInfo.setAddress(StringUtil.EMPTY);
          break;
      }
    } else if (creditCardEntity.getCardForm().equals(CARD_FORM_VIRTUAL)) {
      cardInfo.setAddress(application.getBusinessCode());
    }

    if (cardInfo.getBranchCode() == null) {
      String branchResp = getBranch(application.getBusinessCode()) ;
      String branch = Objects.isNull(branchResp) ? application.getBusinessCode() : branchResp;
      cardInfo.setBranchCode(StringUtil.getValue(branch));
    }

    Iterator<SubCreditCardEntity> it = subs.iterator();
    while (it.hasNext()) {
      SubCreditCardEntity item = it.next();
      ApplicationHistoricIntegration subCard = ApplicationHistoricIntegration.builder()
          .applicationId(cardInfo.getApplicationId())
          .bpmId(cardInfo.getBpmId())
          .cif(cardInfo.getCif())
          .applicationCreditId(cardInfo.getApplicationCreditId())
          .fullName(item.getCardOwnerName().toUpperCase())
          .firstName(getFirstName(item.getCardOwnerName()))
          .lastName(getLastName(item.getCardOwnerName()))
          .integratedStatusDetail(IntegrationStatusDetail.INPROGESS_SUB_CARD.getValue())
          .integratedSystem(IntegrationConstant.WAY_4)
          .loanAmount(item.getCardLimitAmount())
          .guaranteeForm(cardInfo.getGuaranteeForm())
          .identifierCode(cardInfo.getIdentifierCode())
          .applicationSubCardCreditId(item.getId())
          .businessCode(cardInfo.getBusinessUnit())
          .documentCode(cardInfo.getDocumentCode())
          .businessUnit(cardInfo.getBusinessUnit())
          .nextIntegratedTime(new Date())
          .effectiveDate(cardInfo.getEffectiveDate())
          .creditType(cardInfo.getCreditType())
          .cardType(IntegrationConstant.SUB_CARD)
          .createdAt(cardInfo.getCreatedAt())
          .createdBy(cardInfo.getCreatedBy())
          .build();
      try {
        CreateSubCardResponse response = cardClient.createSubCard(CreateSubCardRequest.builder()
            .cifNumber(Integer.valueOf(cardInfo.getCif()))
            .issueContract(creditCard.getIssuingContract())
            .address(VNCharacterUtils.removeAccent(StringUtil.getValue(cardInfo.getAddress())))
            .fullName(item.getCardOwnerName().toUpperCase().trim())
            .firstName(getFirstName(item.getCardOwnerName()))
            .lastName(getLastName(item.getCardOwnerName()))
            .type(getCardForm(StringUtil.getValue(creditCardEntity.getCardFormValue())))
            .channel(BPM_PD)
            .amount(item.getCardLimitAmount())
            .branchCode(cardInfo.getBranchCode())
            .build());
        if (response != null) {
          item.setCreatedDate(DateUtils.toLdt(response.getData().getCreatedDate()));
          item.setContractNumber(response.getData().getCardNumber());
          subCreditCardRepository.save(item);

          subCard = updateIntegrationStatus(subCard, IntegrationStatusDetail.SUCESSFULL.getValue(), String.valueOf(response.getCode()), response.getMessage());
          subCard.setJsonRespone(JsonUtil.convertObject2String(response, objectMapper));
          log.info("createSubCard success with response:{}", JsonUtil.convertObject2String(response, objectMapper));
          integrationRepository.save(subCard);
        }
      } catch (Exception e) {
        log.error("[createSubCard] Error {}: ", e.getMessage());
        subCard = updateErrorStatus(e, subCard, ERR_SUB_CARD.getValue());
        integrationRepository.save(subCard);
      }
    }
  }

  @Override
  @Transactional
  public void retrySubCard(ApplicationHistoricIntegration cardInfo) {
    log.info("retrySubCard START with status {}, code {}", cardInfo.getIntegratedStatusDetail(), cardInfo.getErrorCode());
    try {
      Long applicationId = cardInfo.getApplicationId();
      Optional<SubCreditCardEntity> subCreditCard = subCreditCardRepository.findById(cardInfo.getApplicationSubCardCreditId());
      Optional<ApplicationCreditEntity> creditEntity = creditRepository.findByApplicationId(cardInfo.getApplicationId()).stream().filter(item -> item.getCreditType().equals(CreditType.CARD.getCode())).findFirst();
      ApplicationEntity application = applicationRepository.getReferenceById(applicationId);
      ApplicationCreditCardEntity creditCardEntity = creditEntity.get().getCreditCard();
      if (!creditEntity.isPresent() || !subCreditCard.isPresent()) return;
      cardInfo.setCardType(SUB_CARD);
      cardInfo.setFirstName(getFirstName(subCreditCard.get().getCardOwnerName()));
      cardInfo.setLastName(getLastName(subCreditCard.get().getCardOwnerName()));
      cardInfo.setLoanAmount(subCreditCard.get().getCardLimitAmount());
      cardInfo.setUpdatedAt(LocalDateTime.now());
      cardInfo.setUpdatedBy(SecurityContextUtil.getUsername());
      cardInfo.setJsonRespone(null);

      if (cardInfo.getCif() == null) {
        String cifFromCore = checkCifNo(cardInfo.getRegNumber(), cardInfo.getCif());
        log.info("retrySubCard with Cif From Core: {}", cifFromCore);
        if(cifFromCore != null) {
          cardInfo.setCif(cifFromCore);
        }
      }

      if (creditCardEntity.getCardForm().equals(CARD_FORM_PHYSICAL)) {
        String address = StringUtil.getValue(creditEntity.get().getCreditCard().getCardReceiveAddress());
        switch (address) {
          case ANOTHER_ADDRESS_CODE:
          case CURRENT_RESIDENTIAL_ADDRESS_CODE:
          case ADDRESS_OF_WORK_AGENCY_CODE:
            cardInfo.setAddress(VNCharacterUtils.removeAccent(StringUtil.getValue(creditCardEntity.getAddressLine())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getWardValue())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getDistrictValue())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getCityValue())));
            break;
          case TRANSACTION_OFFICE_CODE:
            cardInfo.setAddress(application.getBusinessCode());
            break;
          case OFFICE_ADDRESS_CODE:
            cardInfo.setAddress(creditCardEntity.getWay4BranchCode());
            break;
          default:
            cardInfo.setAddress(StringUtil.EMPTY);
            break;
        }
      } else if (creditCardEntity.getCardForm().equals(CARD_FORM_VIRTUAL)) {
        cardInfo.setAddress(application.getBusinessCode());
      }

      if (cardInfo.getBranchCode() == null) {
        String branchResp = getBranch(application.getBusinessCode()) ;
        String branch = Objects.isNull(branchResp) ? application.getBusinessCode() : branchResp;
        cardInfo.setBranchCode(StringUtil.getValue(branch));
      }

      CreateSubCardResponse response = cardClient.createSubCard(CreateSubCardRequest.builder()
          .cifNumber(Integer.valueOf(cardInfo.getCif()))
          .issueContract(creditEntity.get().getCreditCard().getIssuingContract())
          .address(VNCharacterUtils.removeAccent(cardInfo.getAddress()))
          .fullName(subCreditCard.get().getCardOwnerName())
          .firstName(getFirstName(subCreditCard.get().getCardOwnerName()))
          .lastName(getLastName(subCreditCard.get().getCardOwnerName()))
          .type(getCardForm(StringUtil.getValue(creditCardEntity.getCardFormValue())))
          .channel(BPM_PD)
          .amount(subCreditCard.get().getCardLimitAmount())
          .branchCode(cardInfo.getBranchCode())
          .build());
      if (!Objects.isNull(response)) {
        subCreditCard.get().setCreatedDate(DateUtils.toLdt(response.getData().getCreatedDate()));
        subCreditCard.get().setContractNumber(response.getData().getCardNumber());
        subCreditCardRepository.save(subCreditCard.get());

        cardInfo = updateIntegrationStatus(cardInfo, IntegrationStatusDetail.SUCESSFULL.getValue(), String.valueOf(response.getCode()), response.getMessage());
        cardInfo.setJsonRespone(JsonUtil.convertObject2String(response, objectMapper));
        log.info("retrySubCard success with response:{}", JsonUtil.convertObject2String(response, objectMapper));
        integrationRepository.save(cardInfo);
      }
    } catch (Exception e) {
      log.error("[retrySubCard] Error {}: ", e.getMessage());
      cardInfo = updateErrorStatus(e, cardInfo, ERR_SUB_CARD.getValue());
      integrationRepository.save(cardInfo);
    }
  }

  @Override
  @Transactional
  public ApplicationHistoricIntegration createOrRetryCreditCard(ApplicationHistoricIntegration cardInfo) throws Exception {
    log.info("createOrRetryCard START with status {}, code {}", cardInfo.getIntegratedStatusDetail(), cardInfo.getErrorCode());
    ApplicationEntity application = applicationRepository.getReferenceById(cardInfo.getApplicationId());
    Optional<ApplicationCreditEntity> creditEntity = creditRepository.findByApplicationId(application.getId()).stream().filter(item -> item.getCreditType().equals(CreditType.CARD.getCode())).findFirst();
    ApplicationCreditCardEntity creditCardEntity = creditEntity.get().getCreditCard();
    CustomerEntity customerEntity = application.getCustomer();
    Optional<CustomerIdentityEntity> customerIdentity = customerEntity.getCustomerIdentitys().stream().filter(item -> item.isPriority() == Boolean.TRUE).findFirst();
    try {
      if (cardInfo.getId()!= null && checkToRetryCard(cardInfo)) {
        cardInfo.setUpdatedAt(LocalDateTime.now());
        cardInfo.setUpdatedBy(SecurityContextUtil.getUsername());
      }

      setCardForm(cardInfo, StringUtil.getValue(creditCardEntity.getCardFormValue()));
      setAutoDeductRate(cardInfo, StringUtil.getValue(creditCardEntity.getAutoDeductRate()));
      setCif(cardInfo);

      if (creditCardEntity.getCardForm().equals(CARD_FORM_PHYSICAL)) {
        String address = StringUtil.getValue(creditEntity.get().getCreditCard().getCardReceiveAddress());
        switch (address) {
          case ANOTHER_ADDRESS_CODE:
          case CURRENT_RESIDENTIAL_ADDRESS_CODE:
          case ADDRESS_OF_WORK_AGENCY_CODE:
            cardInfo.setAddress(VNCharacterUtils.removeAccent(StringUtil.getValue(creditCardEntity.getAddressLine())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getWardValue())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getDistrictValue())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getCityValue())));
            break;
          case TRANSACTION_OFFICE_CODE:
            cardInfo.setAddress(application.getBusinessCode());
            break;
          case OFFICE_ADDRESS_CODE:
            cardInfo.setAddress(creditCardEntity.getWay4BranchCode());
            break;
          default:
            cardInfo.setAddress(StringUtil.EMPTY);
            break;
        }
      } else if (creditCardEntity.getCardForm().equals(CARD_FORM_VIRTUAL)) {
        cardInfo.setAddress(application.getBusinessCode());
      }

      String branchResp = getBranch(application.getBusinessCode()) ;
      String branch = Objects.isNull(branchResp) ? application.getBusinessCode() : branchResp;
      cardInfo.setBranchCode(StringUtil.getValue(branch));

      CreateCardResponse response = cardClient.createCard(CreateCardRequest.builder()
          .bpmId(cardInfo.getBpmId())
          .cifNumber(Integer.valueOf(cardInfo.getCif()))
          .fullName(VNCharacterUtils.removeAccent(creditCardEntity.getCardName().toUpperCase()))
          .firstName(VNCharacterUtils.removeAccent(cardInfo.getFirstName()))
          .lastName(VNCharacterUtils.removeAccent(cardInfo.getLastName()))
          .regNumber(customerIdentity.get().getIdentifierCode())
          .rbsNumber(creditCardEntity.getDeductAccountNumber())
          .payment(cardInfo.getAutoDeductRate())
          .secretQuestion(VNCharacterUtils.removeAccent(creditEntity.get().getCreditCard().getSecretQuestion()))
          .amount(creditEntity.get().getLoanAmount())
          .address(VNCharacterUtils.removeAccent(cardInfo.getAddress()))
          .productType(creditCardEntity.getCardType())
          .cardType(creditCardEntity.getWay4CardCode())
          .policyCode(creditEntity.get().getCreditCard().getCardPolicyCode())
          .liabilityType(PISSL)
          .branchCode(cardInfo.getBranchCode())
          .institutionCode(DEFAULT_INSTITUTION_CODE)
          .type(cardInfo.getCardForm())
          .channel(BPM_PD)
          .isChargeFee(0)
          .email(StringUtil.getValue(creditCardEntity.getEmail()))
          .saleCode(application.getSaleCode())
          .image(null)
          .build());

      log.info("createOrRetryCreditCard success with response : {}", response);
      if (response != null && response.getCode() == 0) {
        ApplicationCreditCardEntity creditCard = creditCardRepository.findById(creditCardEntity.getId()).get();
        if (creditCard != null) {
          Map<String, Object> map = new HashMap<>();
          map.put(PHONE, application.getCustomer().getIndividualCustomer().getPhoneNumber());
          map.put(EMAIL, application.getCustomer().getIndividualCustomer().getEmail());
          map.put(DATEOBIRTH, application.getCustomer().getIndividualCustomer().getDateOfBirth());
          map.put(CUSTOMERNAME,  application.getCustomer().getIndividualCustomer().getLastName() + " " + application.getCustomer().getIndividualCustomer().getFirstName());
          map.put(APPLICATIONBPMID, application.getBpmId());

          ApplicationHistoryApprovalEntity entity =  historyApprovalRepository.findByApplicationIdAndProcessingRole(application.getId(), ProcessingRole.PD_RB_RM)
              .orElse(new ArrayList<>())
              .stream()
              .max(Comparator.comparing(ApplicationHistoryApprovalEntity::getCreatedAt))
              .orElse(null);

          if (entity != null) {
            map.put("assignee", entity.getUsername());
          }

          checkClient(cardInfo.getCif(), map);

          creditCard.setContractL(response.getData().getLiabilityContract());
          creditCard.setIssuingContract(response.getData().getIssueContract());
          creditCard.setContractNumber(response.getData().getContractNumber());
          creditCard.setCreatedDate(DateUtils.toLdt(response.getData().getCreatedDate()));
          creditCardRepository.saveAndFlush(creditCard);

          cardInfo = updateIntegrationStatus(cardInfo, IntegrationStatusDetail.SUCESSFULL.getValue(), String.valueOf(response.getCode()), response.getMessage());
          cardInfo.setJsonRespone(JsonUtil.convertObject2String(response, objectMapper));
          cardInfo.setIssuingContract(creditCard.getIssuingContract());
          log.info("createOrRetryCreditCard success with response:{}", JsonUtil.convertObject2String(response, objectMapper));
          return integrationRepository.saveAndFlush(cardInfo);
        }
      }
    } catch (Exception e) {
      log.error("[createOrRetryCard] Error {}: ", e.getMessage());
      cardInfo = updateErrorStatus(e, cardInfo, ERR_CARD.getValue());
      return integrationRepository.saveAndFlush(cardInfo);
    }
    log.info("createOrRetryCreditCard with response invalid!");
    return null;
  }

  @Override
  @Async
  public void checkClient(String cifNo, Map<String, Object> data) {
    log.info("checkClient START with cifNo {}, data {}", cifNo, data);

    try {
      CheckClientResponse response = cardClient.checkClient(cifNo);

      if (response != null && MapUtils.isNotEmpty(data)) {
          Date dobFromResp = DateUtils.toDate(response.getData().getDateOBirth());
          String phoneFromResp = response.getData().getPhone();
          String emailFromResp = response.getData().getEmail();

          Date dob = DateUtils.asDate((LocalDate) data.get("dateOBirth"));
          String phone = StringUtil.getValue((String) data.get("phone"));
          String assignee = (String) data.get("assignee");
          String email = StringUtil.getValue((String) data.get("email"));
          String customerName = (String) data.get("customerName");
          String applicationBpmId = (String) data.get("applicationBpmId");

          ConcurrentMap<String, Object> errorMap = new ConcurrentHashMap<>();
          if(dobFromResp.after(dob) || dobFromResp.before(dob)) {
            errorMap.put("DateOfBirth", "Ngày sinh: " + DateUtils.format(dobFromResp, "dd/MM/yyyy"));
          }

          if (!phoneFromResp.equals(phone)) {
            errorMap.put("phoneNumber", "Số điện thoại: " + phoneFromResp);
          }

          if (!emailFromResp.equals(email)) {
            errorMap.put("email", "Email: " + emailFromResp);
          }

          if (!errorMap.isEmpty()) {
            StringBuilder buffer = new StringBuilder();
            ConcurrentMap<String, Object> metaMap = new ConcurrentHashMap<>();
            for (Map.Entry<String, Object> entry : errorMap.entrySet()) {
                buffer.append(entry.getValue());
                buffer.append(", ");
             String tmp = buffer.toString();
             tmp = tmp.substring(0, tmp.length() - 2);
             tmp = tmp + ".";
             metaMap.put("ErrorContent", tmp);
             metaMap.put("ApplicationBpmId", applicationBpmId);
             metaMap.put("CustomerName", customerName);
            }

            EmailRequest dataSend = EmailRequest.builder()
                .to(Arrays.asList(assignee + EMAIL_ADDRESS_MSB))
                .eventCode(CARD_SEND_FEEDBACK_CUSTOMER.name())
                .params(metaMap)
                .build();

            emailSender.sendEmail(dataSend, CARD_SEND_FEEDBACK_CUSTOMER.name(), SecurityContextUtil.getAuthorizationToken());
          }
      }

    } catch (Exception e) {
      log.error("[checkClient] Error {}: ", e.getMessage());
    }
  }

  @Override
  @Transactional
  public ApplicationHistoricIntegration createOrRetryClient(ApplicationHistoricIntegration clientInfo) throws Exception {
    log.info("createOrRetryClient START: ");
    String cifFromCore = checkCifNo(clientInfo.getRegNumber(), clientInfo.getCif());
    if (cifFromCore == null) {
      log.info("createOrRetryClient method: Cif not found");
      clientInfo = updateIntegrationStatus(clientInfo, IntegrationStatusDetail.NEW.getValue(), null, IntegrationStatusDetail.NEW.getValue());
      clientInfo.setJsonRespone(null);
      return integrationRepository.save(clientInfo);
    }

    try {
      if (checkToRetryClient(clientInfo) == Boolean.TRUE) {
        clientInfo.setUpdatedAt(LocalDateTime.now());
        clientInfo.setUpdatedBy(SecurityContextUtil.getUsername());
      }

      clientInfo.setCif(cifFromCore);
      CreateClientResponse response = cardClient.createClient(CreateClientRequest.builder()
          .cifNumber(Integer.valueOf(cifFromCore))
          .regNumber(clientInfo.getRegNumber())
          .type(CREATE_CLIENT_DEFAULT_TYPE)
          .build());

      if (response != null && response.getCode() == 0) {
        log.info("createOrRetryClient success with response:{}", JsonUtil.convertObject2String(response, objectMapper));
        clientInfo = updateIntegrationStatus(clientInfo, INPROGESS_CARD.getValue(), null, INPROGESS_CARD.getValue());
        clientInfo.setJsonRespone(JsonUtil.convertObject2String(response, objectMapper));
        return integrationRepository.save(clientInfo);
      }

    } catch (Exception e) {
      log.error("[createOrRetryClient] Error {}: ", e.getMessage());
      clientInfo = updateErrorStatus(e, clientInfo, ERR_CLIENT.getValue());
      return integrationRepository.save(clientInfo);
    }
    log.info("createOrRetryClient with response invalid!");
    return null;
  }

  @Override
  public ApplicationHistoricIntegration checkCreated(ApplicationHistoricIntegration cardInfo) throws Exception {
    log.info("checkCreated START with status {}, code {}", cardInfo.getIntegratedStatusDetail(), cardInfo.getErrorCode());
    try {
      cardInfo.setUpdatedAt(LocalDateTime.now());
      cardInfo.setUpdatedBy(SecurityContextUtil.getUsername());

      setCif(cardInfo);

      CheckCardCreatedResponse response = cardClient.checkCreated(CheckCardCreatedRequest.builder()
          .cifNumber(cardInfo.getCif())
          .policyCode(generatePolicyCode(cardInfo.getPolicyCode(), cardInfo.getBpmId()))
          .build());

      if (response != null && response.getCode() == 0 && response.getData() != null) {
        log.info("checkCreated success with data response : {}", response.getData());
        ApplicationCreditCardEntity creditCard = creditCardRepository.findById(cardInfo.getApplicationCreditCardId()).get();
        if (creditCard != null) {
          if (cardInfo.getCardType().equals(MAIN_CARD)) {
            creditCard.setContractL(response.getData().getLiabilityContract());
            creditCard.setIssuingContract(response.getData().getListIssueContract().get(0).getIssueContract());
            creditCard.setContractNumber(response.getData().getListIssueContract().get(0).getMainCard().get(0).getContractNumber());
            creditCard.setCreatedDate(DateUtils.toLdt(response.getData().getListIssueContract().get(0).getMainCard().get(0).getDateOpen()));
            creditCardRepository.save(creditCard);

            cardInfo = updateIntegrationStatus(cardInfo, IntegrationStatusDetail.SUCESSFULL.getValue(), String.valueOf(response.getCode()), response.getMessage());
            cardInfo.setJsonRespone(JsonUtil.convertObject2String(response.getData(), objectMapper));
            cardInfo.setIssuingContract(creditCard.getIssuingContract());
            return integrationRepository.save(cardInfo);
          } else {
            Optional<SubCreditCardEntity> subCreditCard = subCreditCardRepository.findById(cardInfo.getApplicationSubCardCreditId());
            if (subCreditCard.isPresent()) {
              Optional<CardInfo> subCard = response.getData().getListIssueContract().get(0).getSubCard().stream()
                  .filter(item -> item.getContractName().equals(VNCharacterUtils.removeAccent(subCreditCard.get().getCardOwnerName().toUpperCase()))).findFirst();

              if (subCard.isPresent() && subCreditCard.get().getContractNumber() != null) {
                subCreditCard.get().setCreatedDate(DateUtils.toLdt(subCard.get().getDateOpen()));
                subCreditCard.get().setContractNumber(subCard.get().getContractNumber());
                subCreditCardRepository.save(subCreditCard.get());
              }

              cardInfo = updateIntegrationStatus(cardInfo, IntegrationStatusDetail.SUCESSFULL.getValue(), String.valueOf(response.getCode()), response.getMessage());
              cardInfo.setJsonRespone(JsonUtil.convertObject2String(response.getData(), objectMapper));
              return integrationRepository.save(cardInfo);
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("[checkCreated] Error {}: ", e.getMessage());
      if (cardInfo.getCardType().equals(MAIN_CARD)) {
        cardInfo = updateErrorStatus(e, cardInfo, ERR_CARD.getValue());
      } else  {
        cardInfo = updateErrorStatus(e, cardInfo, ERR_SUB_CARD.getValue());
      }
      return integrationRepository.save(cardInfo);
    }
    return null;
  }

  private ApplicationHistoricIntegration updateErrorStatus(Exception e, ApplicationHistoricIntegration infoApp, String status) {
    log.info("updateErrorStatus with exception: {}",  String.valueOf(((ApprovalException) e).getArgs()[0]));
    infoApp = updateIntegrationStatus(infoApp, status, String.valueOf(TIMEOUT.getCode()), IntegrationStatusDetail.TIMEOUT.getValue());
    if (e instanceof ApprovalException && Objects.equals(DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR, ((ApprovalException) e).getCode())) {
      String httpBody = String.valueOf(((ApprovalException) e).getArgs()[0]);
      String httpCode = String.valueOf(((ApprovalException) e).getArgs()[1]);
      if (httpCode != null && httpCode.equals(String.valueOf(BAD_REQUEST.getCode()))) {
        CreateCardErrorResponse response = JsonUtil.convertString2Object(httpBody, CreateCardErrorResponse.class, objectMapper);
        if (response != null) {
          infoApp = updateIntegrationStatus(infoApp, status, String.valueOf(response.getCode()), response.getMessage() + ": " + StringUtil.getValue(
              (String) response.getData()));
          infoApp.setJsonRespone(JsonUtil.convertObject2String(response, objectMapper));
        }
      }
    } else if (e instanceof ApprovalException && Objects.equals(DomainCode.EXTERNAL_SERVICE_SERVER_ERROR, ((ApprovalException) e).getCode())) {
      infoApp = updateIntegrationStatus(infoApp, status, String.valueOf(TIMEOUT.getCode()), IntegrationStatusDetail.TIMEOUT.getValue());
      infoApp.setJsonRespone(String.valueOf(((ApprovalException) e).getArgs()[0]));
    }
    return infoApp;
  }

  @Override
  @Transactional
  public ApplicationHistoricIntegration composeCardInfo(ApplicationHistoricIntegration applicationInfo) throws Exception {
    log.info("composeCardInfo Start:");
    try {
      ApplicationEntity application = applicationRepository.getReferenceById(applicationInfo.getApplicationId());
      CustomerEntity customerEntity = application.getCustomer();
      Optional<ApplicationCreditEntity> creditEntity = creditRepository.findByApplicationId(application.getId()).stream().filter(item -> item.getCreditType().equals(CreditType.CARD.getCode())).findFirst();
      Optional<CustomerIdentityEntity> customerIdentity = customerEntity.getCustomerIdentitys().stream().filter(item -> item.isPriority() == Boolean.TRUE).findFirst();
      if (!customerIdentity.isPresent() || !creditEntity.isPresent()) {
        log.info("System not found the customer, credit info");
        throw new ApprovalException(DomainCode.COMPOSE_CARD_INFO_ERROR, new Object[]{"Customer identity, credit not found!"});
      }
      ApplicationCreditCardEntity creditCardEntity = creditEntity.get().getCreditCard();
      if (ObjectUtils.isEmpty(application.getId()) || creditCardEntity == null) {
        log.info("System not found the application, credit card info");
        throw new ApprovalException(DomainCode.COMPOSE_CARD_INFO_ERROR, new Object[]{"Application credit card not found!"});
      }

      if (applicationInfo.getId() == null) {
        applicationInfo.setFirstName(getFirstName(creditCardEntity.getCardName()).toUpperCase());
        applicationInfo.setLastName(getLastName(creditCardEntity.getCardName()).toUpperCase());
        applicationInfo.setLoanAmount(creditEntity.get().getLoanAmount());
        applicationInfo.setCardType(MAIN_CARD);
      }
      applicationInfo.setFullName(creditCardEntity.getCardName().toUpperCase());
      applicationInfo.setApplicationId(application.getId());
      applicationInfo.setBpmId(application.getBpmId());
      applicationInfo.setApplicationId(application.getId());
      applicationInfo.setCreditType(creditEntity.get().getCreditType());
      applicationInfo.setStatus(application.getStatus());
      applicationInfo.setCif(customerEntity.getCif());
      applicationInfo.setIdentifierCode(customerIdentity.get().getIdentifierCode());
      applicationInfo.setRegNumber(customerIdentity.get().getIdentifierCode());
      applicationInfo.setSecretQuestion(creditEntity.get().getCreditCard().getSecretQuestion());
      applicationInfo.setCardProductType(creditCardEntity.getWay4CardCode());
      applicationInfo.setProductCode(creditCardEntity.getCardPolicyCode());
      applicationInfo.setProductType(creditCardEntity.getCardType());
      applicationInfo.setPolicyCode(creditEntity.get().getCreditCard().getCardPolicyCode());
      applicationInfo.setLiabilityType(PISSL);
      applicationInfo.setBusinessUnitCode(application.getBusinessCode());
      applicationInfo.setBusinessUnit(application.getBusinessUnit());
      applicationInfo.setChannel(BPM_PD);
      applicationInfo.setGuaranteeForm(creditEntity.get().getGuaranteeFormValue());
      applicationInfo.setIntegratedSystem(WAY_4);
      applicationInfo.setEffectiveDate(DateUtils.getByPeriodTime(application.getUpdatedAt(), application.getEffectivePeriod(), application.getEffectivePeriodUnit()).toLocalDate());
      applicationInfo.setDocumentCode(creditEntity.get().getDocumentCode());
      applicationInfo.setDeductAccountNumber(creditCardEntity.getDeductAccountNumber());
      applicationInfo.setIsChargeFee(0);
      applicationInfo.setInstitutionCode(DEFAULT_INSTITUTION_CODE);
      applicationInfo.setNextIntegratedTime(new Date());
      applicationInfo.setApproveResult(creditEntity.get().getApproveResult());
      applicationInfo.setApplicationCreditId(creditEntity.get().getId());
      applicationInfo.setApplicationCreditCardId(creditCardEntity.getId());
      applicationInfo.setCreatedAt(LocalDateTime.now());
      applicationInfo.setCreatedBy(application.getCreatedBy());
      applicationInfo.setSaleCode(application.getSaleCode());
      applicationInfo.setEmail(StringUtil.getValue(creditCardEntity.getEmail()));

      String autoDeductRate = StringUtil.getValue(creditCardEntity.getAutoDeductRate());
      switch (autoDeductRate) {
        case V001:
          applicationInfo.setAutoDeductRate(null);
          break;
        case V002:
          applicationInfo.setAutoDeductRate(MIN);
          break;
        case V003:
          applicationInfo.setAutoDeductRate(FULL);
          break;
        default:
          applicationInfo.setAutoDeductRate(StringUtil.EMPTY);
          break;
      }

      String cardFormValue = StringUtil.getValue(creditCardEntity.getCardFormValue());
      switch (cardFormValue) {
        case VIRTUAL_VIE:
          applicationInfo.setType(VIRTUAL_ENG);
          applicationInfo.setCardForm(VIRTUAL_ENG);
          break;
        case PHYSICAL_VIE:
          applicationInfo.setCardForm(PHYSICAL_ENG);
          applicationInfo.setType(PHYSICAL_ENG);
          break;
        default:
          break;
      }

      if (creditCardEntity.getCardForm().equals(CARD_FORM_PHYSICAL)) {
        String address = StringUtil.getValue(creditEntity.get().getCreditCard().getCardReceiveAddress());
        switch (address) {
          case ANOTHER_ADDRESS_CODE:
          case CURRENT_RESIDENTIAL_ADDRESS_CODE:
          case ADDRESS_OF_WORK_AGENCY_CODE:
            applicationInfo.setAddress(VNCharacterUtils.removeAccent(StringUtil.getValue(creditCardEntity.getAddressLine())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getWardValue())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getDistrictValue())
                + DELIMITER + StringUtil.getValue(creditCardEntity.getCityValue())));
            break;
          case TRANSACTION_OFFICE_CODE:
            applicationInfo.setAddress(application.getBusinessCode());
            break;
          case OFFICE_ADDRESS_CODE:
            applicationInfo.setAddress(creditCardEntity.getWay4BranchCode());
            break;
          default:
            applicationInfo.setAddress(StringUtil.EMPTY);
            break;
        }
      } else if (creditCardEntity.getCardForm().equals(CARD_FORM_VIRTUAL)) {
        applicationInfo.setAddress(application.getBusinessCode());
      }

      String branchResp = getBranch(application.getBusinessCode()) ;
      String branch = Objects.isNull(branchResp) ? application.getBusinessCode() : branchResp;
      applicationInfo.setBranchCode(StringUtil.getValue(branch));

      log.info("composeCardInfo End with bpm Id: {}", applicationInfo.getBpmId());
      return applicationInfo;
    } catch (Exception e) {
      log.error("[composeCardInfo] Error {}: ", e.getMessage());
      throw new ApprovalException(DomainCode.COMPOSE_CARD_INFO_ERROR, new Object[]{e.getMessage()});
    }
  }

  private void setCif(ApplicationHistoricIntegration cardInfo) {
    if (cardInfo.getCif() == null) {
      String cifFromCore = checkCifNo(cardInfo.getRegNumber(), cardInfo.getCif());
      log.info("createOrRetryCreditCard with Cif From Core: {}", cifFromCore);
      if(cifFromCore != null) {
        cardInfo.setCif(cifFromCore);
      }
    }
  }

  private void setAutoDeductRate(ApplicationHistoricIntegration cardInfo, String autoDeductRate) {
    switch (autoDeductRate) {
      case V001:
        cardInfo.setAutoDeductRate(null);
        break;
      case V002:
        cardInfo.setAutoDeductRate(MIN);
        break;
      case V003:
        cardInfo.setAutoDeductRate(FULL);
        break;
      default:
        cardInfo.setAutoDeductRate(StringUtil.EMPTY);
        break;
    }
  }

  private void setCardForm(ApplicationHistoricIntegration cardInfo, String cardFormValue) {
    switch (cardFormValue) {
      case VIRTUAL_VIE:
        cardInfo.setType(VIRTUAL_ENG);
        cardInfo.setCardForm(VIRTUAL_ENG);
        break;
      case PHYSICAL_VIE:
        cardInfo.setCardForm(PHYSICAL_ENG);
        cardInfo.setType(PHYSICAL_ENG);
        break;
      default:
        break;
    }
  }


  private String getBranch(String businessCode) {
    List<String> listCategory = new ArrayList<>();
    listCategory.add(CARD_BRANCH_MAPPING.getCode());
    GetListResponse listData = configurationListClient.findByListCategoryDataCodes(listCategory, HeaderUtil.getToken());

    Map<String, String> mapGroupValue = listData.getValue().get(CARD_BRANCH_MAPPING.getCode()).stream()
        .collect(Collectors.toMap(Detail::getCode, Detail::getValue, (first, second) -> second));
    if (!mapGroupValue.isEmpty()) {
     return  mapGroupValue.get(businessCode);
    }
    return null;
  }

  @Override
  public Boolean checkToCreateCard(ApplicationHistoricIntegration cardInfo) {
    Boolean flag = Boolean.FALSE;
    if (cardInfo != null && cardInfo.getIntegratedStatusDetail().equals(INPROGESS_CARD.getValue()) && cardInfo.getCardType().equals(MAIN_CARD)) {
      flag = Boolean.TRUE;
    }

    if (cardInfo != null && cardInfo.getIntegratedStatusDetail().equals(ERR_CLIENT.getValue()) && Integer.valueOf(cardInfo.getErrorCode()).equals(IntegrationResponseCode.CLIENT_IS_EXIST.getCode())) {
      flag = Boolean.TRUE;
    }

    if (cardInfo != null && cardInfo.getId()!= null && checkToRetryCard(cardInfo)) {
      flag = Boolean.TRUE;
    }

    return flag;
  }


  public Boolean checkToRetryCard(ApplicationHistoricIntegration cardInfo) {
    if (cardInfo != null
        && cardInfo.getIntegratedStatusDetail().equals(ERR_CARD.getValue())
        && isErrorsRetryCardOrSubCard(Integer.valueOf(cardInfo.getErrorCode())) == Boolean.TRUE)
    {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  public Boolean checkToRetryOrCreateClient(ApplicationHistoricIntegration applicationInfo)
  {
    Boolean flag = Boolean.FALSE;
    if (applicationInfo.getId() == null)
    {
      flag =  Boolean.TRUE;
    }

    if (checkToRetryClient(applicationInfo) == Boolean.TRUE) {
      flag =  Boolean.TRUE;
    }
    return flag;
  }


  public Boolean checkToRetryClient(ApplicationHistoricIntegration applicationInfo)
  {
    if (applicationInfo.getId() != null
        && (applicationInfo.getIntegratedStatusDetail().equals(IntegrationStatusDetail.NEW.getValue()) || applicationInfo.getIntegratedStatusDetail().equals(INPROGESS_CARD.getValue())))
    {
      return Boolean.TRUE;
    }

    if (applicationInfo.getErrorCode() != null
        && (Arrays.asList(clientErrors).contains(applicationInfo.getErrorCode()))
        && applicationInfo.getIntegratedStatusDetail().equals(IntegrationStatusDetail.ERR_CLIENT.getValue())) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  public Boolean checkToCreateSubCard(ApplicationHistoricIntegration cardInfo)
  {
    if (cardInfo != null
        && cardInfo.getIntegratedStatusDetail().equals(IntegrationStatusDetail.SUCESSFULL.getValue())
        && cardInfo.getCardType().equals(IntegrationConstant.MAIN_CARD)
        && Integer.valueOf(cardInfo.getErrorCode()) == 0)
    {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }


  @Override
  public Boolean checkToRetrySubCard(ApplicationHistoricIntegration cardInfo)
  {
    if (cardInfo != null
        && cardInfo.getIntegratedStatusDetail().equals(ERR_SUB_CARD.getValue())
        && isErrorsRetryCardOrSubCard(Integer.valueOf(cardInfo.getErrorCode())) == Boolean.TRUE)
    {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  private String checkCifNo(String idNumber, String cifNumber)
  {
    log.info("checkCifNo START with idNumber: {}, cifNumber: {}", idNumber, cifNumber);
    try {
      CoreCustomerInfo response = coreIntegrationClient.getCustomerInfo(StringUtil.EMPTY, idNumber.trim(), HeaderUtil.getToken());
      log.info("CALL_CORE_CUSTOMER_INFO with request idNumber: {}, cifNumber: {}, response : {}", idNumber, cifNumber, JsonUtil.convertObject2String(response, objectMapper));
      if (response == null) {
        log.info("No records found.");
        return null;
      }
      CoreCustomer coreCustomer = JsonUtil.toObject(response, CoreCustomer.class);
      if (idNumber.equals(coreCustomer.getIdNo())) {
        return cifNumber == null ? coreCustomer.getCifNumber() : cifNumber;
      }
    } catch (Exception e) {
      log.error("checkCifNo Error: {}", e.getMessage());
      throw e;
    }
    return null;
  }


  private Boolean isErrorsRetryCardOrSubCard(Integer errorCode)
  {
    if(Arrays.asList(creditErrors).contains(String.valueOf(errorCode))) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  private ApplicationHistoricIntegration updateIntegrationStatus(ApplicationHistoricIntegration objectInfo, String detailStatus, String errorCode, String errorDetail)
  {
    objectInfo.setIntegratedStatusDetail(detailStatus);
    objectInfo.setIntegratedStatus(detailStatus);
    objectInfo.setErrorCode(errorCode);
    objectInfo.setErrorDescription(errorDetail);
    return objectInfo;
  }

  public String getFirstName(String fullName)
  {
    log.info("getFirstName start with: {}", fullName);
    try {
      return fullName.split(" (?!.* )")[0].toUpperCase();
    } catch (Exception e) {
      log.info("getFirstName error with: {}", e.getMessage());
      return StringUtil.EMPTY;
    }
  }

  public String getLastName(String fullName)
  {
    log.info("getLastName start with: {}", fullName);
    try {
      return fullName.split(" (?!.* )")[1].toUpperCase();
    } catch (Exception e) {
      log.info("getLastName error : {}", e.getMessage());
      return StringUtil.EMPTY;
    }
  }

  private String getCardForm(String cardFormValue){
    if (VIRTUAL_VIE.equals(cardFormValue)) {
      return VIRTUAL_ENG;
    } else if (PHYSICAL_VIE.equals(cardFormValue)) {
      return PHYSICAL_ENG;
    }
    return StringUtil.EMPTY;
  }

  private String generatePolicyCode(String cardPolicyCode, String idBpm) {
    String root = PCD + String.format("%03d", cardPolicyCode.length()) + cardPolicyCode + generateBpmIdPolicy(idBpm);
    String leftRoot = CHANNEL + String.format("%03d", root.length()) + root;
    return String.format("%03d", leftRoot.length()) + leftRoot;
  }

  private String generateBpmIdPolicy(String idBpm) {
    return "ACD" + String.format("%03d", idBpm.length()) + idBpm;
  }

}