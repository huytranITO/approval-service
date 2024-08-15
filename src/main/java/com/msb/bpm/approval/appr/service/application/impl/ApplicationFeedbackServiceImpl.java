package com.msb.bpm.approval.appr.service.application.impl;


import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.exception.DomainCode.CAN_NOT_SEND_FEEDBACK;
import static com.msb.bpm.approval.appr.exception.DomainCode.INVALID_LOAN_PURPOSE;
import static com.msb.bpm.approval.appr.exception.DomainCode.INVALID_REGULATORY;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION_DRAFT;
import static com.msb.bpm.approval.appr.exception.DomainCode.REGULATORY_CODE_EMPTY;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.general.GeneralInfoClient;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer;
import com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode;
import com.msb.bpm.approval.appr.constant.MessageCode;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.BusinessType;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.common.LdpStatus;
import com.msb.bpm.approval.appr.enums.common.SourceApplication;
import com.msb.bpm.approval.appr.enums.regulatoryCode.RegulatoryCode;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.kafka.producer.DataKafkaProducer;
import com.msb.bpm.approval.appr.mapper.ApplicationContactMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationFeedbackMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import com.msb.bpm.approval.appr.mapper.FeedbackHistoryMapper;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationContactDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseIncomeItemDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsOtherDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsPropertyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsRentalIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsSalaryDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.ApplicationFbDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.AssetInfoCommentFbDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.CommentFbDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.CreditCommentFbDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.CustomerCommentFbDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.FeedbackDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.FeedbackDataDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.IncomeCommentFbDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationContactEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import com.msb.bpm.approval.appr.model.request.feeback.PostApplicationFeedbackRequest;
import com.msb.bpm.approval.appr.model.response.MessageResponse;
import com.msb.bpm.approval.appr.model.response.general.GeneralInfoResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRelationshipRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.repository.FeedbackHistoryRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.application.ApplicationFeedbackService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationFeedbackServiceImpl extends AbstractBaseService implements
    ApplicationFeedbackService {

  private final ApplicationRepository applicationRepository;
  private final DataKafkaProducer dataKafkaProducer;
  private final CommonService commonService;
  private final FeedbackHistoryRepository feedbackHistoryRepository;
  private final VerifyService verifyService;
  private final CustomerRepository customerRepository;
  private final ApplicationMapper applicationMapper;
  private final CustomerMapper customerMapper;
  private final ApplicationIncomeMapper applicationIncomeMapper;
  private final ApplicationCreditMapper applicationCreditMapper;
  private final ApplicationFeedbackMapper applicationFeedbackMapper;
  private final ApplicationContactMapper applicationContactMapper;
  private final GeneralInfoClient generalInfoClient;
  private final CustomerRelationshipRepository customerRelationshipRepository;
  private final ApplicationDraftRepository applicationDraftRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public MessageResponse applicationFeedbackCustomer(PostApplicationFeedbackRequest request) {
    ApplicationEntity applicationEntity = applicationRepository.findByBpmIdCustomQuery(
        request.getBpmId()).orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
    log.info("execute START applicationFeedbackCustomer with request={}", request);
    if (!SecurityContextUtil.getCurrentUser().equalsIgnoreCase(applicationEntity.getAssignee())) {
      throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
    }

    //Check LDP status and processingRole
    if (checkFeedbackForCustomerEditing(applicationEntity)) {
      // Verify RegulatoryCode
      List<String> regulatoryCode = verifyRegulatoryCode(applicationEntity);
      // Verify LoanPurpose
      verifyLoanPurpose(applicationEntity);
      //case xac nhan DNVV
      if (!verifyService.compareWithApplicationLDP(applicationEntity)) {
        return feedbackForCustomerConfirm(request, applicationEntity, regulatoryCode);
      } else {//case phan hoi DNVV
        return feedbackForCustomerEditing(request, applicationEntity, regulatoryCode);
      }
    } else {
      throw new ApprovalException(CAN_NOT_SEND_FEEDBACK);
    }
  }
  private void verifyLoanPurpose(ApplicationEntity applicationEntity) {
    if (CollectionUtils.isEmpty(applicationEntity.getCredits())) {
      return;
    }
    // Mục đích vay phải khác V020 mới cho phản hồi hồ sơ
    applicationEntity.getCredits().forEach(credit -> {
      if ((ObjectUtils.isNotEmpty(credit.getCreditLoan()) && ("V020".equalsIgnoreCase(credit.getCreditLoan().getLoanPurpose()))) ||
          (ObjectUtils.isNotEmpty(credit.getCreditOverdraft()) && ("V020".equalsIgnoreCase(credit.getCreditOverdraft().getLoanPurpose())))) {
          throw new ApprovalException(INVALID_LOAN_PURPOSE);
      }
    });
  }
  private List<String> verifyRegulatoryCode(ApplicationEntity applicationEntity) {
    List<String> regulatoryCode = getRegulatoryCode(applicationEntity);
    if (CollectionUtils.isEmpty(regulatoryCode)) {
      throw new ApprovalException(REGULATORY_CODE_EMPTY);
    }
    // Check RegulatoryCode:
    // 1. QD.DF.006 -> CJMHome
    // 2. QD.DF.006 -> BPM
    // 3. QD.RB.076 -> CJBO
    regulatoryCode.forEach(rc -> {
      if (!((SourceApplication.CJMHOME.name().equalsIgnoreCase(applicationEntity.getSource()) && RegulatoryCode.QD_DF_006.getValue().equalsIgnoreCase(rc))
          || (SourceApplication.CJBO.name().equalsIgnoreCase(applicationEntity.getSource()) && RegulatoryCode.QD_RB_076.getValue().equalsIgnoreCase(rc))
          || (SourceApplication.BPM.name().equalsIgnoreCase(applicationEntity.getSource()) && RegulatoryCode.QD_DF_006.getValue().equalsIgnoreCase(rc)))) {
        throw new ApprovalException(INVALID_REGULATORY);
      }
    });
    return regulatoryCode;
  }

  private MessageResponse feedbackForCustomerEditing(PostApplicationFeedbackRequest request,
      ApplicationEntity applicationEntity, List<String> regulatoryCode) {
    log.info("execute START feedbackForCustomerEditing with bmpId={}", request.getBpmId());
    if (checkHavingComment(request)) {
      //update application status
      applicationRepository.updateLdpStatus(LdpStatus.WAIT_CUSTOMER_EDITING.getValue(),
          request.getBpmId());
      //store history comment
      storeApplicationHistoryFeedback(request, applicationEntity);
      //call kafka
      FeedbackDTO feedbackDTO = buildFeedbackDTO(request, applicationEntity,
          ApplicationStatus.AS4001.getValue(), LdpStatus.WAIT_CUSTOMER_EDITING.getValue(),
          regulatoryCode);
      dataKafkaProducer.publishMessage(feedbackDTO);

      log.info("execute END feedbackForCustomerEditing have result={}", feedbackDTO);
      return new MessageResponse(MessageCode.FEEDBACK_EDITING_SUCCESS.getCode(),
          MessageCode.FEEDBACK_EDITING_SUCCESS.getValue());
    }
    return new MessageResponse(MessageCode.FEEDBACK_ERROR.getCode(),
        MessageCode.FEEDBACK_ERROR.getValue());
  }

  private void storeApplicationHistoryFeedback(PostApplicationFeedbackRequest request,
      ApplicationEntity applicationEntity) {
    GetUserProfileResponse userProfileResponse = commonService.getUserDetail(
        SecurityContextUtil.getUsername());
    ApplicationHistoryFeedbackEntity applicationHistoryFeedback = FeedbackHistoryMapper.INSTANCE.convertAppCommentToApplicationFeedback(
        request.getApplicationCommentData(), applicationEntity, userProfileResponse);
    feedbackHistoryRepository.save(applicationHistoryFeedback);
  }

  private MessageResponse feedbackForCustomerConfirm(PostApplicationFeedbackRequest request,
      ApplicationEntity applicationEntity, List<String> regulatoryCode) {
    log.info("execute START feedbackForCustomerConfirm with bpmId={}", request.getBpmId());
    //Block feedback have no green tick
    if (!checkGreenTickOfApplication(applicationEntity.getBpmId())) {
      return new MessageResponse(MessageCode.FEEDBACK_ERROR_WITH_NO_GREEN_TICK.getCode(),
          MessageCode.FEEDBACK_ERROR_WITH_NO_GREEN_TICK.getValue());
    }
    //call kafka
    FeedbackDTO feedbackDTO = buildFeedbackDTO(request, applicationEntity,
        ApplicationStatus.AS4003.getValue(), LdpStatus.WAIT_CUSTOMER_CONFIRM.getValue(),
        regulatoryCode);
    //Store comment
    if (checkHavingComment(request)) {
      storeApplicationHistoryFeedback(request, applicationEntity);
    }
    //update application status
    applicationRepository.updateLdpStatus(LdpStatus.WAIT_CUSTOMER_CONFIRM.getValue(),
        request.getBpmId());
    dataKafkaProducer.publishMessage(feedbackDTO);
    log.info("execute END feedbackForCustomerConfirm with have result={}", feedbackDTO);
    return new MessageResponse(MessageCode.FEEDBACK_CONFIRM_SUCCESS.getCode(),
        MessageCode.FEEDBACK_CONFIRM_SUCCESS.getValue());
  }

  private boolean checkFeedbackForCustomerEditing(ApplicationEntity applicationEntity) {
    String applicationStatus = applicationEntity.getStatus();
    boolean result = false;
    if ((applicationStatus != null && !ApplicationStatus.AS0099.getValue().equals(applicationStatus)
        && !ApplicationStatus.AS9999.getValue().equals(applicationStatus)) && (
        applicationEntity.getLdpStatus() == null || LdpStatus.CUSTOMER_EDITED.getValue()
            .equals(applicationEntity.getLdpStatus())) && ProcessingRole.PD_RB_RM.toString()
        .equals(applicationEntity.getProcessingRole())) {
      result = true;
    }
    return result;
  }

  private boolean checkHavingComment(PostApplicationFeedbackRequest request) {
    boolean result = false;
    if (Objects.nonNull(request.getApplicationCommentData()) && (
        StringUtils.hasText(request.getApplicationCommentData().getCustomer())
            || StringUtils.hasText(request.getApplicationCommentData().getApplicationIncome())
            || StringUtils.hasText(request.getApplicationCommentData().getApplicationCredit())
            || StringUtils.hasText(request.getApplicationCommentData().getAssetInfo())
            || StringUtils.hasText(
            request.getApplicationCommentData().getDocumentCommentData().getComment()))) {
      result = true;
    }
    return result;
  }

  private FeedbackDTO buildFeedbackDTO(PostApplicationFeedbackRequest applicationFeedbackRequest,
      ApplicationEntity applicationEntity, String status, String type,
      List<String> regulatoryCode) {
    // Lấy tên của trạng thái.
    String statusName = mapToStatusName(status);
    GetUserProfileResponse userProfile = commonService.getUserDetail(
        applicationEntity.getAssignee());

    FeedbackDTO feedbackDTO = new FeedbackDTO();
    feedbackDTO.setRefId(applicationEntity.getRefId());
    feedbackDTO.setBpmId(applicationEntity.getBpmId());
    feedbackDTO.setTransactionID(applicationEntity.getTransactionId());
    feedbackDTO.setSource(applicationEntity.getSource());
    feedbackDTO.setStatus(status);
    feedbackDTO.setName(statusName);
    feedbackDTO.setRegulatoryCode(regulatoryCode);
    feedbackDTO.setUsername(getUserName(applicationEntity));
    if (Objects.nonNull(userProfile)) {
      feedbackDTO.setFullnameRM(
          Objects.nonNull(userProfile.getPersonal()) ? userProfile.getPersonal().getFullName()
              : "");
      if (Objects.nonNull(userProfile.getUser())) {
        feedbackDTO.setEmail(
            Objects.nonNull(userProfile.getUser().getEmail()) ? userProfile.getUser().getEmail()
                : "");
        feedbackDTO.setPhone(
            Objects.nonNull(userProfile.getUser().getPhoneNumber()) ? userProfile.getUser()
                .getPhoneNumber() : "");
      }
    }

    feedbackDTO.setResponseDate(LocalDateTime.now());
    FeedbackDataDTO feedbackDataDTO = new FeedbackDataDTO();
    //Data Application
    if (LdpStatus.WAIT_CUSTOMER_CONFIRM.getValue().equals(type)) {
      feedbackDataDTO.setDataApplication(
          convertApplicationEntityToApplicationFbDTO(applicationEntity));
    }
    //Data Comment
    feedbackDataDTO.setDataComment(buildComment(applicationFeedbackRequest));
    feedbackDTO.setData(feedbackDataDTO);
    return feedbackDTO;
  }

  private List<String> getRegulatoryCode(ApplicationEntity applicationEntity) {
    ApplicationDraftEntity applicationDraftEntity = applicationDraftRepository.findByBpmIdAndTabCode(
            applicationEntity.getBpmId(), TabCode.DEBT_INFO)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION_DRAFT));
    if (applicationDraftEntity.getStatus() != null
        && applicationDraftEntity.getStatus() == ApplicationDraftStatus.UNFINISHED
        && applicationDraftEntity.getData() != null) {
      return getRegulatoryCodeWithNoTick(applicationDraftEntity);
    } else {
      return getRegulatoryCodeWithGreenTick(applicationEntity);
    }
  }

  private boolean checkGreenTickOfApplication(String bpmId) {
    Set<ApplicationDraftEntity> applicationDraftEntities = applicationDraftRepository.findByBpmId(
            bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION_DRAFT));
    for (ApplicationDraftEntity applicationDraftEntity : applicationDraftEntities) {
      if (applicationDraftEntity.getStatus() != null
          && applicationDraftEntity.getStatus() == ApplicationDraftStatus.UNFINISHED) {
        return false;
      }
    }
    return true;
  }

  private List<String> getRegulatoryCodeWithGreenTick(ApplicationEntity applicationEntity) {
    // Lấy danh sách mã văn bản
    List<String> regulatoryCode = new ArrayList<>();
    if (org.apache.commons.lang3.StringUtils.isNotBlank(applicationEntity.getRegulatoryCode())) {
      regulatoryCode = Arrays.asList(applicationEntity.getRegulatoryCode().split(";"));
    }
    // Kiểm tra nếu chưa nhập đủ mã văn bản
    if (CollectionUtils.isNotEmpty(applicationEntity.getCredits())
        && (applicationEntity.getCredits().size() != regulatoryCode.size())) {
      throw new ApprovalException(REGULATORY_CODE_EMPTY);
    }
    return regulatoryCode;
  }

  private List<String> getRegulatoryCodeWithNoTick(ApplicationDraftEntity applicationDraftEntity) {
    List<String> result = new ArrayList<>();
    PostDebtInfoRequest postDebtInfoRequest = JsonUtil.convertBytes2Object(
        applicationDraftEntity.getData(), PostDebtInfoRequest.class, objectMapper);
    if (CollectionUtils.isNotEmpty(postDebtInfoRequest.getCredits())) {
      postDebtInfoRequest.getCredits().forEach(applicationCreditDTO -> {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(
            applicationCreditDTO.getDocumentCode())) {
          result.add(applicationCreditDTO.getDocumentCode());
        }
      });
      // Kiểm tra nếu chưa nhập đủ mã văn bản
      if (postDebtInfoRequest.getCredits().size() != result.size()) {
        throw new ApprovalException(REGULATORY_CODE_EMPTY);
      }
    }
    return result;
  }

  private String getUserName(ApplicationEntity applicationEntity) {
    // Lấy thông tin User của RM xử lý hồ sơ.
    List<ApplicationHistoryApprovalEntity> applicationHistoryApprovalEntities = new ArrayList<>(
        applicationEntity.getHistoryApprovals());
    List<ApplicationHistoryApprovalEntity> appHistoryApprovals = getPreviousHistoryApprovalByRole(
        applicationHistoryApprovalEntities, PD_RB_RM);
    String userName = "";
    if (CollectionUtils.isNotEmpty(appHistoryApprovals)) {
      ApplicationHistoryApprovalEntity applicationHistoryApprovalEntity = appHistoryApprovals.get(
          0);
      userName = applicationHistoryApprovalEntity.getUsername();
    }
    return userName;
  }

  private CommentFbDTO buildComment(PostApplicationFeedbackRequest applicationFeedbackRequest) {
    CommentFbDTO commentFbDTO = new CommentFbDTO();
    commentFbDTO.setCustomer(new CustomerCommentFbDTO(org.apache.commons.lang3.StringUtils.trim(
        applicationFeedbackRequest.getApplicationCommentData().getCustomer())));
    commentFbDTO.setApplicationCredits(new CreditCommentFbDTO(
        org.apache.commons.lang3.StringUtils.trim(
            applicationFeedbackRequest.getApplicationCommentData().getApplicationCredit())));
    commentFbDTO.setApplicationIncome(new IncomeCommentFbDTO(
        org.apache.commons.lang3.StringUtils.trim(
            applicationFeedbackRequest.getApplicationCommentData().getApplicationIncome())));
    commentFbDTO.setAssetInfo(new AssetInfoCommentFbDTO(org.apache.commons.lang3.StringUtils.trim(
        applicationFeedbackRequest.getApplicationCommentData().getAssetInfo())));
    if (Objects.nonNull(applicationFeedbackRequest.getApplicationCommentData())) {
      commentFbDTO.setDocuments(applicationFeedbackMapper.convertDocumentCommentToDocumentFeedback(
          applicationFeedbackRequest.getApplicationCommentData().getDocumentCommentData()));
    }
    return commentFbDTO;
  }

  public ApplicationFbDTO convertApplicationEntityToApplicationFbDTO(
      ApplicationEntity applicationEntity) {
    ApplicationFbDTO applicationFbDTO = new ApplicationFbDTO();
    //application
    applicationFbDTO.setApplication(applicationMapper.entityToCmsApplicationDto(applicationEntity));

    if (CustomerType.RB.toString().equals(applicationEntity.getCustomer().getCustomerType())) {
      //customer Info
      CustomerEntity customerEntity = applicationEntity.getCustomer();
      applicationFbDTO.setCustomer(
          customerMapper.toCmsCustomerDto(customerEntity.getIndividualCustomer(), customerEntity));
      //customer relationship
      Map<Long, String> mapCustomerIdAndRelationship = getMapCustomerIdAndRelationship(
          applicationEntity);
      //customer relations
      List<CmsCustomerRelationDTO> customerRelationDTOList = new ArrayList<>();
      List<CustomerEntity> relationCustomer = customerRepository.getAllCustomerRelation(
          applicationEntity.getBpmId(), Customer.RB);
      if (Objects.nonNull(relationCustomer)) {
        relationCustomer.forEach(customer -> {
          CmsCustomerRelationDTO cmsCustomerRelationDTO = customerMapper.toCmsCustomerRelationDto(
              customer.getIndividualCustomer(), customer,
              mapCustomerIdAndRelationship);
          cmsCustomerRelationDTO.setRelationship(
              commonService.convertRelationshipTypeBPMToSource(applicationEntity.getSource(),
                  cmsCustomerRelationDTO.getRelationship()));
          customerRelationDTOList.add(cmsCustomerRelationDTO);
        });
      }
      applicationFbDTO.setCustomerRelations(customerRelationDTOList);
      //enterprise relations
      List<CmsEnterpriseRelationDTO> enterpriseRelationDTOList = new ArrayList<>();
      List<CustomerEntity> enterpriseRelations = customerRepository.getAllCustomerRelation(
          applicationEntity.getBpmId(), Customer.EB);
      if (Objects.nonNull(enterpriseRelations)) {
        enterpriseRelations.forEach(customer -> enterpriseRelationDTOList.add(
            customerMapper.toCmsEnterpriseRelationDto(customer.getEnterpriseCustomer(), customer)));
      }
      applicationFbDTO.setEnterpriseRelations(enterpriseRelationDTOList);
      //Income
      //get All Ids of customer Relation
      Map<Long, String> mapRefCustomerIdAndRefCusId = mapRefCustomerIdAndRefCusId(relationCustomer,
          customerEntity);
      applicationFbDTO.setApplicationIncomes(
          getIncomeList(applicationEntity, mapRefCustomerIdAndRefCusId));
      //Credit
      applicationFbDTO.setApplicationCredits(getCreditList(applicationEntity));
      //Contact
      applicationFbDTO.setApplicationContact(getCmsApplicationContactDTOList(applicationEntity));
      //Asset Info
      applicationFbDTO.setAssetInfo(buildAssetProposal(applicationEntity.getBpmId()));
      //desCreditAvailable
      if (Objects.nonNull(applicationEntity.getRepayment())) {
        applicationFbDTO.setDesCreditAvailable(applicationEntity.getRepayment().getEvaluate());
      }
      // Set lai ten chu nguon thu
      remapIncomeOwnerName(applicationFbDTO.getCustomer(), applicationFbDTO.getCustomerRelations(),
          applicationFbDTO.getApplicationIncomes());
    }
    return applicationFbDTO;
  }

  public void remapIncomeOwnerName(CmsCustomerDTO customer, List<CmsCustomerRelationDTO> customerRelations,
      List<CmsIncomeDTO> incomes) {
    List<String> fullNames = new ArrayList<>();
    fullNames.add(((CmsIndividualCustomerDTO)customer).getFullName());

    if (CollectionUtils.isNotEmpty(customerRelations)) {
      customerRelations.forEach(
          customerRelation -> fullNames.add(customerRelation.getFullName()));
    }

    incomes.forEach(income -> income.getIncomeItems().forEach(incomeItem -> {
      for (String fullName : fullNames) {
        if (incomeItem.getIncomeOwnerName().contains(fullName)) {
          incomeItem.setIncomeOwnerName(fullName);
          break;
        }
      }
    }));
  }

  private List<CmsApplicationContactDTO> getCmsApplicationContactDTOList(
      ApplicationEntity applicationEntity) {
    List<CmsApplicationContactDTO> result = new ArrayList<>();
    if (Objects.nonNull(applicationEntity.getContact())) {
      List<ApplicationContactEntity> applicationContactEntities = new ArrayList<>(
          applicationEntity.getContact());
      applicationContactEntities.forEach(
          applicationContactEntity -> applicationContactEntity.setRelationship(
              commonService.convertRelationshipTypeBPMToSource(applicationEntity.getSource(),
                  applicationContactEntity.getRelationship()))
      );
      applicationContactEntities = applicationContactEntities.stream().sorted(
              Comparator.comparing(ApplicationContactEntity::getOrderDisplay,
                  Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toCollection(ArrayList::new));
      result = applicationContactMapper.entityToCmsApplicationContactDtoList(
          applicationContactEntities);
    }
    return result;
  }

  private Map<Long, String> getMapCustomerIdAndRelationship(ApplicationEntity applicationEntity) {
    Map<Long, String> result = new HashMap<>();
    List<CustomerRelationShipEntity> customerRelationShipEntities = customerRelationshipRepository.getCustomerRelationByBmpId(
        applicationEntity.getBpmId());
    if (Objects.nonNull(customerRelationShipEntities)) {
      customerRelationShipEntities.forEach(
          customerRelationShipEntity -> result.put(customerRelationShipEntity.getCustomerRefId(),
              customerRelationShipEntity.getRelationship()));
    }
    return result;
  }

  private List<CmsBaseCreditDTO> getCreditList(ApplicationEntity applicationEntity) {
    List<CmsBaseCreditDTO> creditDTOList = new ArrayList<>();
    if (Objects.nonNull(applicationEntity.getCredits())) {
      applicationEntity.getCredits().forEach(applicationCreditEntity -> {
        if (Objects.nonNull(applicationCreditEntity.getCreditLoan())) {
          creditDTOList.add(
              applicationCreditMapper.entityToCmsCreditLoan(applicationCreditEntity.getCreditLoan(),
                  applicationCreditEntity));
        }
        if (Objects.nonNull(applicationCreditEntity.getCreditOverdraft())) {
          creditDTOList.add(applicationCreditMapper.entityToCmsCreditOverdraft(
              applicationCreditEntity.getCreditOverdraft(), applicationCreditEntity));
        }
        if (Objects.nonNull(applicationCreditEntity.getCreditCard())) {
          creditDTOList.add(
              applicationCreditMapper.entityToCmsCreditCard(applicationCreditEntity.getCreditCard(),
                  applicationCreditEntity));
        }
      });
    }
    return creditDTOList;
  }

  private List<CmsIncomeDTO> getIncomeList(ApplicationEntity applicationEntity,
      Map<Long, String> mapRefCustomerIdAndRefCusId) {
    List<CmsIncomeDTO> incomeDTOList = new ArrayList<>();
    if (Objects.nonNull(applicationEntity.getIncomes())) {
      applicationEntity.getIncomes().forEach(applicationIncomeEntity -> {
        CmsIncomeDTO incomeDTO = applicationIncomeMapper.entityToCmsIncome(applicationIncomeEntity);

        List<CmsBaseIncomeItemDTO> incomeItemDTOList = new ArrayList<>();
        //salary income
        addSalaryIncomeToList(applicationEntity, applicationIncomeEntity, incomeItemDTOList,
            mapRefCustomerIdAndRefCusId);
        //rental income
        addRentalIncomeToList(applicationEntity, applicationIncomeEntity, incomeItemDTOList,
            mapRefCustomerIdAndRefCusId);
        //Chứa 2 loại nguồn thu (ca nhan kinh doanh va doanh nghiep) của khach hang
        addIndividualEnterpriseIncomesToList(applicationEntity, applicationIncomeEntity, incomeItemDTOList,
            mapRefCustomerIdAndRefCusId);
        //Other income
        addOtherIncomesToList(applicationEntity, applicationIncomeEntity, incomeItemDTOList,
            mapRefCustomerIdAndRefCusId);
        //PropertyBusiness income
        addPropertyBusinessIncomesToList(applicationEntity, applicationIncomeEntity, incomeItemDTOList,
            mapRefCustomerIdAndRefCusId);
        incomeDTO.setIncomeItems(incomeItemDTOList);
        incomeDTOList.add(incomeDTO);
      });
    }
    return incomeDTOList;
  }

  private void addSalaryIncomeToList(ApplicationEntity applicationEntity,
      ApplicationIncomeEntity applicationIncomeEntity,
      List<CmsBaseIncomeItemDTO> incomeItemDTOList, Map<Long, String> mapRefCustomerIdAndRefCusId) {
    if (Objects.nonNull(applicationIncomeEntity.getSalaryIncomes())) {
      applicationIncomeEntity.getSalaryIncomes().forEach(salaryIncome -> {
        CmsSalaryDTO cmsSalaryDTO = applicationIncomeMapper.entityToCmsSalaryIncome(salaryIncome,
            mapRefCustomerIdAndRefCusId);
        cmsSalaryDTO.setIncomeOwner(
            commonService.convertRelationshipTypeBPMToSource(applicationEntity.getSource(),
                cmsSalaryDTO.getIncomeOwner()));
        incomeItemDTOList.add(cmsSalaryDTO);
      });
    }
  }

  private void addRentalIncomeToList(ApplicationEntity applicationEntity,
      ApplicationIncomeEntity applicationIncomeEntity,
      List<CmsBaseIncomeItemDTO> incomeItemDTOList, Map<Long, String> mapRefCustomerIdAndRefCusId) {
    if (Objects.nonNull(applicationIncomeEntity.getRentalIncomes())) {
      applicationIncomeEntity.getRentalIncomes().forEach(rentalIncome -> {
        CmsRentalIncomeDTO cmsRentalIncomeDTO = applicationIncomeMapper.entitiesToCmsRentalIncome(
            rentalIncome,
            mapRefCustomerIdAndRefCusId);
        //change relationship mapping
        cmsRentalIncomeDTO.setIncomeOwner(
            commonService.convertRelationshipTypeBPMToSource(applicationEntity.getSource(),
                cmsRentalIncomeDTO.getIncomeOwner()));
        incomeItemDTOList.add(cmsRentalIncomeDTO);
      });

    }
  }

  private void addIndividualEnterpriseIncomesToList(ApplicationEntity applicationEntity,
      ApplicationIncomeEntity applicationIncomeEntity,
      List<CmsBaseIncomeItemDTO> incomeItemDTOList, Map<Long, String> mapRefCustomerIdAndRefCusId) {
    if (Objects.nonNull(applicationIncomeEntity.getIndividualEnterpriseIncomes())) {
      applicationIncomeEntity.getIndividualEnterpriseIncomes()
          .forEach(individualEnterpriseIncome -> {
            if (ActuallyIncomeDTO.INDIVIDUAL_BUSINESS.equals(
                individualEnterpriseIncome.getIncomeType())) {
              CmsIndividualBusinessDTO cmsIndividualBusinessDTO = applicationIncomeMapper.entityToCmsIndividualIncome(
                  individualEnterpriseIncome,
                  mapRefCustomerIdAndRefCusId);
              cmsIndividualBusinessDTO.setIncomeOwner(
                  commonService.convertRelationshipTypeBPMToSource(applicationEntity.getSource(),
                      cmsIndividualBusinessDTO.getIncomeOwner()));
              incomeItemDTOList.add(cmsIndividualBusinessDTO);

            } else if (ActuallyIncomeDTO.ENTERPRISE_BUSINESS.equals(
                individualEnterpriseIncome.getIncomeType())) {
              CmsEnterpriseBusinessDTO cmsEnterpriseBusinessDTO = applicationIncomeMapper.entiCmsEnterpriseBusinessDto(
                  individualEnterpriseIncome,
                  mapRefCustomerIdAndRefCusId);
              cmsEnterpriseBusinessDTO.setIncomeOwner(
                  commonService.convertRelationshipTypeBPMToSource(applicationEntity.getSource(),
                      cmsEnterpriseBusinessDTO.getIncomeOwner()));
              incomeItemDTOList.add(cmsEnterpriseBusinessDTO);
            }
          });
    }
  }

  private void addOtherIncomesToList(ApplicationEntity applicationEntity,
      ApplicationIncomeEntity applicationIncomeEntity,
      List<CmsBaseIncomeItemDTO> incomeItemDTOList, Map<Long, String> mapRefCustomerIdAndRefCusId) {
    if (Objects.nonNull(applicationIncomeEntity.getOtherIncomes())) {
      applicationIncomeEntity.getOtherIncomes().forEach(otherIncome -> {
        CmsOtherDTO cmsOtherDTO = applicationIncomeMapper.entityToCmsOtherIncome(otherIncome,
            mapRefCustomerIdAndRefCusId);
        cmsOtherDTO.setIncomeOwner(
            commonService.convertRelationshipTypeBPMToSource(applicationEntity.getSource(),
                cmsOtherDTO.getIncomeOwner()));
        incomeItemDTOList.add(cmsOtherDTO);
      });
    }
  }

  private void addPropertyBusinessIncomesToList(ApplicationEntity applicationEntity,
      ApplicationIncomeEntity applicationIncomeEntity,
      List<CmsBaseIncomeItemDTO> incomeItemDTOList, Map<Long, String> mapRefCustomerIdAndRefCusId) {
    if (Objects.nonNull(applicationIncomeEntity.getPropertyBusinessIncomes())) {
      applicationIncomeEntity.getPropertyBusinessIncomes().forEach(
          propertyBusinessIncomeEntity -> {
            CmsPropertyIncomeDTO cmsPropertyIncomeDTO = applicationIncomeMapper.entityToCmsPropertyIncome(
                propertyBusinessIncomeEntity,
                mapRefCustomerIdAndRefCusId);
            cmsPropertyIncomeDTO.setIncomeOwner(
                commonService.convertRelationshipTypeBPMToSource(applicationEntity.getSource(),
                    cmsPropertyIncomeDTO.getIncomeOwner()));
            incomeItemDTOList.add(cmsPropertyIncomeDTO);
          });
    }
  }

  private Map<Long, String> mapRefCustomerIdAndRefCusId(List<CustomerEntity> relationCustomer,
      CustomerEntity customerEntity) {
    Map<Long, String> result = new HashMap<>();
    if (Objects.nonNull(relationCustomer)) {
      relationCustomer.add(customerEntity);
      relationCustomer.forEach(
          customerEntityElement -> result.put(customerEntityElement.getRefCustomerId(),
              customerEntityElement.getRefCusId()));
    }
    return result;
  }

  private String mapToStatusName(String status) {
    log.info("mapToStatusName start with status={}", status);
    try {
      List<GeneralInfoResponse> generalInfos = generalInfoClient.getGeneralInfo();
      if (CollectionUtils.isEmpty(generalInfos)) {
        return null;
      }
      String name = generalInfos.stream()
          .filter(info -> info.getBusinessType().equals(BusinessType.PD_RB.getValue()))
          .filter(info -> info.getCode().equals(status)).findFirst()
          .map(GeneralInfoResponse::getName).orElse(null);
      log.info("mapToStatusName end with status={}", status);
      return name;
    } catch (Exception e) {
      log.error("mapToStatusName failure : ", e);
      return null;
    }
  }

}
