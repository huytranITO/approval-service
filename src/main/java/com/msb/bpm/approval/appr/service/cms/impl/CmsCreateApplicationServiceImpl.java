package com.msb.bpm.approval.appr.service.cms.impl;

import static com.msb.bpm.approval.appr.config.LandingPageConfig.BPM_SOURCE;
import static com.msb.bpm.approval.appr.config.LandingPageConfig.CJBO_SOURCE;
import static com.msb.bpm.approval.appr.config.LandingPageConfig.CJMHOME_SOURCE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.ENTERPRISE_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INDIVIDUAL_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.SALARY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.CARD;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LOAN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.OVERDRAFT;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.FINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.UNFINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.EXCHANGE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.*;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.*;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0099;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS9999;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.MAKE_PROPOSAL;
import static com.msb.bpm.approval.appr.enums.application.SubmissionPurpose.NEW_LEVEL;
import static com.msb.bpm.approval.appr.enums.cms.StatusAppType.NOT_READY;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.ASSET_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.AUTO_DEDUCT_RATE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CARD_FORM;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CARD_RECEIVE_ADDRESS;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CARD_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CONVERSION_METHOD;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CREDIT_FORM;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.CREDIT_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.DETAIL_INCOME_OTHER;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.DOCUMENT_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.EB_ADDRESS_TYPE_V001;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.GENDER;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.GUARANTEE_FORM;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.INCOME_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.ISSUE_BY;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.LABOR_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.LOAN_PURPOSE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.MARTIAL_STATUS;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.NATIONAL;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.PAY_TYPE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.PILOT_SOURCE_MAPPING;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.PROCESSING_STEP;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.PROCESS_FLOW;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RB_ADDRESS_TYPE_V001;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RB_ADDRESS_TYPE_V002;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RELATIONSHIP;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.RENTAL_PURPOSE;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.SUBMISSION_PURPOSE;
import static com.msb.bpm.approval.appr.exception.DomainCode.ERROR_CREATE_ENTERPRISE_CUSTOMER_RELATIONSHIP;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_RELATION_GROUP;
import static com.msb.bpm.approval.appr.exception.DomainCode.NUMBER_OF_CONTACT_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.chat.events.CreateGroupEvent;
import com.msb.bpm.approval.appr.client.asset.AssetClient;
import com.msb.bpm.approval.appr.client.configuration.ConfigurationListClient;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.customer.request.AddOrUpdateCusRelationshipRequest;
import com.msb.bpm.approval.appr.client.customer.request.AddOrUpdateCusRelationshipRequest.RelationshipDetail;
import com.msb.bpm.approval.appr.client.customer.request.SearchCusRelationshipRequest;
import com.msb.bpm.approval.appr.client.customer.response.CusRelationForSearchResponse;
import com.msb.bpm.approval.appr.client.customer.response.CusRelationForSearchResponse.Relationship;
import com.msb.bpm.approval.appr.client.customer.response.CustomerRelationResponse;
import com.msb.bpm.approval.appr.client.customer.response.CustomerRelationResponse.RelationshipDetailInfo;
import com.msb.bpm.approval.appr.client.customer.response.CustomerRelationResponse.RelationshipInfo;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.application.AddressType;
import com.msb.bpm.approval.appr.enums.application.ApprovalResult;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.application.GeneratorStatusEnums;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.common.LdpStatus;
import com.msb.bpm.approval.appr.enums.common.SourceApplication;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.factory.CmsApplicationCreditFactory;
import com.msb.bpm.approval.appr.factory.CmsApplicationIncomeFactory;
import com.msb.bpm.approval.appr.mapper.ApplicationFieldInformationMapper;
import com.msb.bpm.approval.appr.mapper.draft.EntityDraftMapper;
import com.msb.bpm.approval.appr.model.dto.*;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationContactDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseIncomeItemDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsSalaryDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationContactEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditOverdraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationRepaymentEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.EnterpriseCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.OtherIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.PropertyBusinessIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RentalIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SubCreditCardEntity;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateAssetRequest;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateAssetRequest.AssetInfo;
import com.msb.bpm.approval.appr.model.request.collateral.AssetResponse;
import com.msb.bpm.approval.appr.model.request.configuration.GetCategoryConditionRequest;
import com.msb.bpm.approval.appr.model.request.customer.Address;
import com.msb.bpm.approval.appr.model.request.customer.CommonCustomerRequest;
import com.msb.bpm.approval.appr.model.request.customer.Customer;
import com.msb.bpm.approval.appr.model.request.customer.IdentityDocument;
import com.msb.bpm.approval.appr.model.request.customer.SearchByListCustomerRequest;
import com.msb.bpm.approval.appr.model.request.customereb.CreateCustomerEbRequest;
import com.msb.bpm.approval.appr.model.request.customereb.CreateCustomerEbRequest.CustomerEb;
import com.msb.bpm.approval.appr.model.request.customereb.CreateCustomerEbRequest.IdentityDocumentEbRequest;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import com.msb.bpm.approval.appr.model.request.data.PostFieldInformationRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import com.msb.bpm.approval.appr.model.response.cms.CmsBaseResponse;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryConditionResponse;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryConditionResponse.CategoryResp;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryConditionResponse.ConditionDataResponse;
import com.msb.bpm.approval.appr.model.response.configuration.ConfigurationBaseResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse.Detail;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.model.response.customer.AddressResponse;
import com.msb.bpm.approval.appr.model.response.customer.CreateRBCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse.CustomersEBResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse.CustomersRBResponse.CustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse.IdentityDocumentsEbRes;
import com.msb.bpm.approval.appr.model.response.customer.IdentityDocumentResponse;
import com.msb.bpm.approval.appr.model.response.customer.UpdateRBCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customereb.CustomerEbResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.OrganizationTreeDetail;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea.DataResponse;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRelationshipRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.cache.MercuryConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.service.idgenerate.IDSequenceService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.util.BeanUtil;
import com.msb.bpm.approval.appr.util.DateUtils;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.StringUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 19/8/2023, Saturday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class CmsCreateApplicationServiceImpl extends AbstractBaseService implements BaseService<CmsBaseResponse, PostCmsV2CreateApplicationRequest> {

  private final ApplicationRepository applicationRepository;
  private final CustomerRepository customerRepository;
  private final CustomerRelationshipRepository customerRelationshipRepository;
  private final CommonService commonService;
  private final UserManagerClient userManagerClient;
  private final CustomerClient customerClient;
  private final ConfigurationServiceCache configurationServiceCache;
  private final MercuryConfigurationServiceCache mercuryCache;
  private final ChecklistService checklistService;
  private final ObjectMapper objectMapper;
  private final MessageSource messageSource;
  private final VerifyService verifyService;
  private final AssetClient assetClient;
  private final IDSequenceService idSequenceService;
  Map<String, List<Detail>> categoryMap;
  MercuryDataResponse city;
  private final ConfigurationListClient configurationListClient;
  private final ApplicationDraftRepository applicationDraftRepository;
  private final Validator validator;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public String getType() {
    return CMS_CREATE_APPLICATION;
  }

  @Override
  @Transactional
  public CmsBaseResponse execute(PostCmsV2CreateApplicationRequest request, Object... obj) {
    List<String> codes = Arrays.asList(GENDER.getCode(), MARTIAL_STATUS.getCode(),
        DOCUMENT_TYPE.getCode(), RB_ADDRESS_TYPE_V001.getCode(), RB_ADDRESS_TYPE_V002.getCode(),
        EB_ADDRESS_TYPE_V001.getCode(), ISSUE_BY.getCode(), PROCESSING_STEP.getCode(),
        PROCESS_FLOW.getCode(), SUBMISSION_PURPOSE.getCode(), NATIONAL.getCode(),
        RELATIONSHIP.getCode(), CARD_TYPE.getCode(), AUTO_DEDUCT_RATE.getCode(),
        CARD_FORM.getCode(), CARD_RECEIVE_ADDRESS.getCode(),
        LOAN_PURPOSE.getCode(), CONVERSION_METHOD.getCode(),
        PAY_TYPE.getCode(), RENTAL_PURPOSE.getCode(), ASSET_TYPE.getCode(),
        LABOR_TYPE.getCode(), DETAIL_INCOME_OTHER.getCode(), INCOME_TYPE.getCode(),
        CREDIT_TYPE.getCode(), GUARANTEE_FORM.getCode(), CREDIT_FORM.getCode());

    categoryMap = configurationServiceCache.getCategoryDataByCodes(codes);

    city = mercuryCache.searchPlace("");

    log.info("CmsCreateApplicationServiceImpl.execute start with refId : {} , request : {}",
        request.getApplication().getRefId(), JsonUtil.convertObject2String(request, objectMapper));

    CmsBaseResponse response = new CmsBaseResponse();
    ApplicationEntity entityApp = saveApplication(request, response);

    response.setBpmId(entityApp.getBpmId());
    response.setStatus(NOT_READY.getValue());

    applicationRepository.updateCreateBy(entityApp.getAssignee(), entityApp.getId());

    log.info("CmsCreateApplicationServiceImpl.execute end with refId : {} , response : {}",
        request.getApplication().getRefId(), JsonUtil.convertObject2String(response, objectMapper));

    log.info("START SmartChat CreateGroup Event with applicationId={}",
            entityApp.getBpmId());
    applicationEventPublisher.publishEvent(new CreateGroupEvent(this,entityApp.getBpmId()));

    return response;
  }

  public ApplicationEntity saveApplication(PostCmsV2CreateApplicationRequest request, CmsBaseResponse response){
    String refId = request.getApplication().getRefId();
    Map<String, CustomerEntity> customerEntityMap = new HashMap<>();

    try {
      verifyService.verifyUserReception(request.getApplication().getCreatedBy().toLowerCase(), null);
    } catch (ApprovalException e) {
      if (e.getCode().getCode().equals(DomainCode.NOT_FOUND_USER.getCode())) {
        response.getNoted().put(DomainCode.RM_IS_NOT_EXIST.getCode(), DomainCode.RM_IS_NOT_EXIST.getMessage());
      } else if (e.getCode().getCode().equals(DomainCode.USER_RECEPTION_NOT_ACTIVE.getCode())) {
        response.getNoted().put(DomainCode.RM_IS_INACTIVE.getCode(), DomainCode.RM_IS_INACTIVE.getMessage());
      }
    }

    DataResponse regionAreaResp = userManagerClient.getRegionAreaByUserName(request.getApplication().getCreatedBy(), true);
    if (Objects.isNull(regionAreaResp) || Objects.isNull(regionAreaResp.getBusinessUnitDetails())) {
      response.getNoted().put(DomainCode.RM_DOESNT_HAVE_PERMISSION.getCode(),DomainCode.RM_DOESNT_HAVE_PERMISSION.getMessage());
    } else {
      if (regionAreaResp.getBusinessUnitDetails().size() > 1) {
        response.getNoted().put(DomainCode.RM_HAVE_A_LOT_OF_BUSINESS_UNIT.getCode(),DomainCode.RM_HAVE_A_LOT_OF_BUSINESS_UNIT.getMessage());
      }
    }

    // Kiểm tra hồ sơ thuộc phạm vi pilot
    String mode = null;
    String source = request.getApplication().getSource();
    Map<String, String> mapGroupValue = getSourcePilotList();
    if (!mapGroupValue.isEmpty()) {
      mode =  mapGroupValue.get("MODE");
    }
    if ("ENABLE".equalsIgnoreCase(mode)) {
      checkPilotScope(source, response, mapGroupValue, regionAreaResp);
    }
    ApplicationEntity entityApp = checkCmsApplication(request.getApplication().getRefId(), request.getApplication().getSource());
    // Kiem tra refId cua LDP/CMS
    if (entityApp != null) {
      verifyStatus(refId, entityApp);

      // Tìm kiếm thông tin chi nhánh/phòng giao dịch của user RM (nếu có)
      if (StringUtils.isNotBlank(request.getApplication().getCreatedBy())) {
        setBranchUserRM(entityApp, response, regionAreaResp);
        entityApp.setAssignee(request.getApplication().getCreatedBy());
      }

      entityApp.setSegment(request.getApplication().getSegment());
      entityApp.setTransactionId(request.getApplication().getTransactionId());
      entityApp.setInsurance(request.isInsurance());

      // update Đánh giá nghĩa vụ trả nợ của khách hàng
      if (request.getDesCreditAvailable() != null) {
        ApplicationRepaymentEntity repayment = new ApplicationRepaymentEntity();
        repayment.setEvaluate(request.getDesCreditAvailable());
        entityApp.setRepayment(repayment);
      } else {
        entityApp.setRepayment(null);
      }

      // update customer
      addOrUpdateCustomer(request, entityApp, customerEntityMap);

      // update relationship
      addOrUpdateRelationShip(request, entityApp, customerEntityMap);

      setCustomerGuaranteeIncome(request.getApplicationIncomes(), customerEntityMap);

      // Lưu update thông tin nguồn thu
      saveOrUpdateIncomes(request.getApplicationIncomes(), entityApp);

      // Lưu update thông tin khoản vay
      saveOrUpdateCredit(request.getApplicationCredits(), entityApp);

      saveOrUpdateApplicationContact(request.getApplicationContact(), entityApp);

      // Luu cap nhat thong tin thuc dia DVKD
      saveOrUpdateFieldInformation(entityApp, request);

      applicationRepository.saveAndFlush(entityApp);

      // Luu cap nhat thong tin tai san
      AssetResponse assetResponse = createOrUpdateAsset(entityApp.getBpmId(), request.getAssetInfo());

      // Tạo/cập nhật checklist
      reloadChecklist(entityApp.getBpmId());

      validateStateDraftData(entityApp, assetResponse);

      //Kiểm tra và lấy id mqh từ common
      checkCustomerRelationship(entityApp);

      return entityApp;
    } else {

      // Save Application
      ApplicationEntity appInfo = new ApplicationEntity();

      // Tìm kiếm thông tin chi nhánh/phòng giao dịch của user RM (nếu có)
      if (StringUtils.isNotBlank(request.getApplication().getCreatedBy())) {
        setBranchUserRM(appInfo, response, regionAreaResp);
        appInfo.setAssignee(request.getApplication().getCreatedBy());
      }

      // update Đánh giá nghĩa vụ trả nợ của khách hàng
      if (request.getDesCreditAvailable() != null) {
        ApplicationRepaymentEntity repayment = new ApplicationRepaymentEntity();
        repayment.setEvaluate(request.getDesCreditAvailable());
        appInfo.setRepayment(repayment);
      } else {
        appInfo.setRepayment(null);
      }

      appInfo.setBpmId(idSequenceService.generateBpmId());
      appInfo.setRefId(request.getApplication().getRefId());
      appInfo.setSource(request.getApplication().getSource());
      appInfo.setSegment(request.getApplication().getSegment());
      appInfo.setProcessFlow("V003");
      appInfo.setProcessFlowValue(configurationServiceCache.getCategoryData(categoryMap, PROCESS_FLOW).get("V003"));
      appInfo.setSubmissionPurpose(NEW_LEVEL.getCode());
      appInfo.setSubmissionPurposeValue(configurationServiceCache.getCategoryData(categoryMap, SUBMISSION_PURPOSE).get(NEW_LEVEL.getCode()));
      appInfo.setGeneratorStatus(GeneratorStatusEnums.DEFAULT.getValue());
      appInfo.setProcessingRole(PD_RB_RM.name());
      appInfo.setProcessingStepCode(MAKE_PROPOSAL.getCode());
      appInfo.setProcessingStep(messageSource.getMessage(MAKE_PROPOSAL.getValue(), null, Util.locale()));
      appInfo.setAssignee(request.getApplication().getCreatedBy());
      appInfo.setInsurance(request.isInsurance());
      appInfo.setTransactionId(request.getApplication().getTransactionId());

      // Customer
      CustomerEntity entityCus = persistCustomer((CmsIndividualCustomerDTO) request.getCustomer());
      customerEntityMap.put(entityCus.getRefCusId(), entityCus);
      appInfo.setCustomer(entityCus);

      // save customer relationship
      createCustomerRelationShip(request, appInfo, customerEntityMap);

      saveOrUpdateEnterpriseRelations(request.getEnterpriseRelations(), null, appInfo);

      setCustomerGuaranteeIncome(request.getApplicationIncomes(), customerEntityMap);

      // Lưu/update thông tin nguồn thu
      saveOrUpdateIncomes(request.getApplicationIncomes(), appInfo);

      // Lưu/ update thông tin khoản vay
      saveOrUpdateCredit(request.getApplicationCredits(), appInfo);

      saveOrUpdateApplicationContact(request.getApplicationContact(), appInfo);

      // Luu cap nhat thong tin thuc dia DVKD
      saveOrUpdateFieldInformation(appInfo, request);

      createFirstHistory(appInfo);

      //Kiểm tra và lấy id mqh từ common
      checkCustomerRelationship(appInfo);

      applicationRepository.saveAndFlush(appInfo);
      commonService.saveDraft(appInfo.getBpmId());

      // Luu cập nhật thông tin tài sản
      AssetResponse assetResponse = createOrUpdateAsset(appInfo.getBpmId(), request.getAssetInfo());

      // save asset to draft
      updateAssetInfo2Draft(appInfo, assetResponse);

      // Tạo/ cập nhật checklist
      reloadChecklist(appInfo.getBpmId());

      return appInfo;
    }

  }

  public void addOrUpdateCustomer(PostCmsV2CreateApplicationRequest request, ApplicationEntity entity, Map<String, CustomerEntity> customerEntityMap) {
    log.info("syncCustomer START with request={}", JsonUtil.convertObject2String(request, objectMapper));
    CustomerEntity currentCustomer = entity.getCustomer();
    if (currentCustomer == null) { throw new ApprovalException(DomainCode.NOT_FOUND_CUSTOMER_BY_REFCUSID, new Object[]{request}); }

    // update customer
    currentCustomer.getIndividualCustomer().setFirstName(Util.splitFirstName(((CmsIndividualCustomerDTO) request.getCustomer()).getFullName()));
    currentCustomer.getIndividualCustomer().setLastName(Util.splitLastName(((CmsIndividualCustomerDTO) request.getCustomer()).getFullName()));
    currentCustomer.getIndividualCustomer().setGender(((CmsIndividualCustomerDTO) request.getCustomer()).getGender());
    currentCustomer.getIndividualCustomer().setDateOfBirth(((CmsIndividualCustomerDTO) request.getCustomer()).getDateOfBirth());
    currentCustomer.getIndividualCustomer().setMartialStatus(((CmsIndividualCustomerDTO) request.getCustomer()).getMartialStatus().toString());
    currentCustomer.getIndividualCustomer().setMartialStatusValue(
        configurationServiceCache.getCategoryData(categoryMap, MARTIAL_STATUS)
            .get(((CmsIndividualCustomerDTO) request.getCustomer()).getMartialStatus().toString()));
    currentCustomer.getIndividualCustomer().setNation(((CmsIndividualCustomerDTO) request.getCustomer()).getNation());
    currentCustomer.getIndividualCustomer().setPhoneNumber(((CmsIndividualCustomerDTO) request.getCustomer()).getPhoneNumber());
    currentCustomer.getIndividualCustomer().setEmail(((CmsIndividualCustomerDTO) request.getCustomer()).getEmail());
    currentCustomer.getIndividualCustomer().setLiteracy(((CmsIndividualCustomerDTO) request.getCustomer()).getLiteracy());
    currentCustomer.getIndividualCustomer().setMsbMember(request.getCustomer().isMsbMember());
    currentCustomer.getIndividualCustomer().setEmployeeCode(request.getCustomer().getStaffId());

    // update address & identity
    updateAddressAndIdentity(currentCustomer, request);

    customerEntityMap.put(currentCustomer.getRefCusId(), currentCustomer);

    updateCustomer(currentCustomer, (CmsIndividualCustomerDTO) request.getCustomer());

    currentCustomer.getIndividualCustomer().setGenderValue(
        configurationServiceCache.getCategoryData(categoryMap, GENDER)
            .get(currentCustomer.getIndividualCustomer().getGender()));
    currentCustomer.getIndividualCustomer().setNationValue(
        configurationServiceCache.getCategoryData(categoryMap, NATIONAL)
            .get(((CmsIndividualCustomerDTO) request.getCustomer()).getNation()));
  }

  public void updateAddressAndIdentity(CustomerEntity entity, PostCmsV2CreateApplicationRequest request) {
    log.info("updateAddress START with request={}", JsonUtil.convertObject2String(request, objectMapper));
    // Identity
    Map<String, CustomerAddressEntity> mapAddress = new HashMap<>();
    Map<String, CustomerIdentityEntity> mapIdentities = new HashMap<>();
    Set<CustomerIdentityEntity> currentIdentityListForAdd = new HashSet<>();
    Set<CustomerIdentityEntity> currentIdentityListForUpdate = new HashSet<>();
    request.getCustomer().getIdentities().forEach(itemIdentity -> {
      Optional<CustomerIdentityEntity> currentIdentity = entity.getCustomerIdentitys().stream().filter(objIdentity -> itemIdentity.getLdpIdentityId().equals(objIdentity.getLdpIdentityId())).findFirst();
      if (currentIdentity.isPresent()) {
        currentIdentity.get().setDocumentType(itemIdentity.getDocumentType());
        currentIdentity.get().setDocumentTypeValue(configurationServiceCache.getCategoryData(categoryMap, DOCUMENT_TYPE).get(itemIdentity.getDocumentType()));
        currentIdentity.get().setLdpIdentityId(itemIdentity.getLdpIdentityId());
        currentIdentity.get().setIssuedAt(itemIdentity.getIssuedAt());
        currentIdentity.get().setIssuedBy(itemIdentity.getIssuedBy());
        currentIdentity.get().setIssuedByValue(configurationServiceCache.getCategoryData(categoryMap, ISSUE_BY).get(itemIdentity.getIssuedBy()));
        currentIdentity.get().setIssuedPlace(itemIdentity.getIssuedPlace());
        currentIdentity.get().setIssuedPlaceValue(getNamePlaceByCodeFromCacheMercury(city, itemIdentity.getIssuedPlace()));
        currentIdentity.get().setPriority(itemIdentity.isPriority());
        currentIdentity.get().setIdentifierCode(itemIdentity.getIdentifierNumber());
        currentIdentityListForUpdate.add(currentIdentity.get());
        mapIdentities.put(currentIdentity.get().getLdpIdentityId(), currentIdentity.get());
      } else {
        CustomerIdentityEntity identityEntity = BeanUtil.copyBean(itemIdentity, CustomerIdentityEntity.class);
        identityEntity.setDocumentTypeValue(configurationServiceCache.getCategoryData(categoryMap, DOCUMENT_TYPE).get(itemIdentity.getDocumentType()));
        identityEntity.setIssuedByValue(configurationServiceCache.getCategoryData(categoryMap, ISSUE_BY).get(itemIdentity.getIssuedBy()));
        identityEntity.setIssuedPlaceValue(getNamePlaceByCodeFromCacheMercury(city, itemIdentity.getIssuedPlace()));
        identityEntity.setIdentifierCode(itemIdentity.getIdentifierNumber());
        currentIdentityListForAdd.add(identityEntity);
        mapIdentities.put(identityEntity.getLdpIdentityId(),identityEntity);
      }
    });

    // Address
    Set<CustomerAddressEntity> currentAddressListForAdd = new HashSet<>();
    Set<CustomerAddressEntity> currentAddressListForUpdate = new HashSet<>();
    request.getCustomer().getAddresses().forEach(itemAddress -> {
      Optional<CustomerAddressEntity> currentAddress = entity.getCustomerAddresses().stream().filter(objAddress -> itemAddress.getLdpAddressId().equals(objAddress.getLdpAddressId())).findFirst();
      if (currentAddress.isPresent()) {
        currentAddress.get().setAddressType(itemAddress.getAddressType());
        currentAddress.get().setAddressTypeValue(getAddressTypeValue(entity.getCustomerType(), itemAddress.getAddressType()));
        currentAddress.get().setCityCode(itemAddress.getCityCode());
        currentAddress.get().setCityValue(getNamePlaceByCodeFromCacheMercury(city, itemAddress.getCityCode()));
        currentAddress.get().setDistrictCode(itemAddress.getDistrictCode());
        currentAddress.get().setDistrictValue(getDistrictValue(itemAddress.getCityCode(), itemAddress.getDistrictCode()));
        currentAddress.get().setWardCode(itemAddress.getWardCode());
        currentAddress.get().setWardValue(getWardValue(itemAddress.getDistrictCode(), itemAddress.getWardCode()));
        currentAddress.get().setAddressLine(itemAddress.getAddressLine());
        currentAddress.get().setLdpAddressId(itemAddress.getLdpAddressId());
        currentAddress.get().setAddressLinkId(SourceApplication.LDP.name() + "-" + itemAddress.getLdpAddressId());
        currentAddressListForUpdate.add(currentAddress.get());
        mapAddress.put(currentAddress.get().getLdpAddressId(), currentAddress.get());
      } else {
        CustomerAddressEntity addressEntity = BeanUtil.copyBean(itemAddress, CustomerAddressEntity.class);
        addressEntity.setCityValue(getNamePlaceByCodeFromCacheMercury(city, itemAddress.getCityCode()));
        addressEntity.setWardValue(getWardValue(itemAddress.getDistrictCode(), itemAddress.getWardCode()));
        addressEntity.setDistrictValue(getDistrictValue(itemAddress.getCityCode(), itemAddress.getDistrictCode()));
        addressEntity.setAddressTypeValue(getAddressTypeValue(entity.getCustomerType(), itemAddress.getAddressType()));
        addressEntity.setAddressLinkId(SourceApplication.LDP.name() + "-" + itemAddress.getLdpAddressId());
        currentAddressListForAdd.add(addressEntity);
        mapAddress.put(addressEntity.getLdpAddressId(),addressEntity);
      }
    });


    Set<CustomerAddressEntity> addressToRemove = new HashSet<>();
    if (mapAddress.size() > 0) {
      entity.getCustomerAddresses().forEach(item -> {
        if (!mapAddress.containsKey(item.getLdpAddressId())) addressToRemove.add(item);
      });
    }
    if(CollectionUtils.isNotEmpty(addressToRemove)) {
      entity.cmsRemoveCustomerAddresses(addressToRemove);
    }


    Set<CustomerIdentityEntity> identitiesToRemove = new HashSet<>();
    if (mapIdentities.size() > 0) {
      entity.getCustomerIdentitys().forEach(item -> {
        if (!mapIdentities.containsKey(item.getLdpIdentityId())) identitiesToRemove.add(item);
      });
    }
    if (CollectionUtils.isNotEmpty(identitiesToRemove)) {
      entity.cmsRemoveCustomerIdentities(identitiesToRemove);
    }

    if (CollectionUtils.isNotEmpty(currentAddressListForUpdate)) {
      entity.updateCustomerAddresses(currentAddressListForUpdate);
    }
    if (CollectionUtils.isNotEmpty(currentAddressListForAdd)) {
      entity.addCustomerAddresses(currentAddressListForAdd);
    }

    if (CollectionUtils.isNotEmpty(currentIdentityListForUpdate)) {
      entity.updateCustomerIdentities(currentIdentityListForUpdate);
    }
    if (CollectionUtils.isNotEmpty(currentIdentityListForAdd)) {
      entity.addCustomerIdentities(currentIdentityListForAdd);
    }
  }

  public void addOrUpdateRelationShip(PostCmsV2CreateApplicationRequest request, ApplicationEntity entity, Map<String, CustomerEntity> customerEntityMap) {
    log.info("syncRelationShip START with request={}", JsonUtil.convertObject2String(request, objectMapper));
    Set<CustomerEntity> currentCusList = new HashSet<>();
    Set<CustomerEntity> removeList = new HashSet<>();

    Set<Long> set = entity.getCustomer().getCustomerRelationShips().stream()
        .map(CustomerRelationShipEntity::getCustomerRefId)
        .collect(Collectors.toSet());

    Optional<Set<CustomerEntity>> entitySet = customerRepository.findByIdIn(set);
    entitySet.ifPresent(currentCusList::addAll);

    if (CollectionUtils.isEmpty(request.getCustomerRelations()) && CollectionUtils.isEmpty(
        request.getEnterpriseRelations())) {
      entity.getCustomer().removeAllCustomerRelationshipEntities();
      customerRepository.deleteAllById(set);
      return;
    }

    Set<CustomerEntity> customerRelationEntities = currentCusList.stream()
        .filter(customer -> CustomerType.RB.name().equals(customer.getCustomerType()))
        .collect(Collectors.toSet());

    // Người liên quan
    if (CollectionUtils.isNotEmpty(customerRelationEntities)) {
      request.getCustomerRelations().forEach(itemCustomer -> {
        Optional<CustomerEntity> currentCustomer = customerRelationEntities.stream().filter(objCustomer -> itemCustomer.getRefCusId().equals(objCustomer.getRefCusId())).findFirst();
        if (currentCustomer.isPresent()) {
          removeList.add(currentCustomer.get());
          currentCustomer.get().getIndividualCustomer().setFirstName(Util.splitFirstName(itemCustomer.getFullName()));
          currentCustomer.get().getIndividualCustomer().setLastName(Util.splitLastName(itemCustomer.getFullName()));
          currentCustomer.get().getIndividualCustomer().setGender(itemCustomer.getGender());
          currentCustomer.get().getIndividualCustomer().setDateOfBirth(itemCustomer.getDateOfBirth());
          currentCustomer.get().getIndividualCustomer().setMartialStatus(itemCustomer.getMartialStatus().toString());
          currentCustomer.get().getIndividualCustomer().setMartialStatusValue(configurationServiceCache.getCategoryData(categoryMap, MARTIAL_STATUS).get(itemCustomer.getMartialStatus().toString()));
          currentCustomer.get().getIndividualCustomer().setNation(itemCustomer.getNation());
          currentCustomer.get().getIndividualCustomer().setPhoneNumber(itemCustomer.getPhoneNumber());
          currentCustomer.get().getIndividualCustomer().setEmail(itemCustomer.getEmail());
          currentCustomer.get().setCustomerType(itemCustomer.getCustomerType());
          currentCustomer.get().setRefCusId(itemCustomer.getRefCusId());
          currentCustomer.get().getIndividualCustomer().setMsbMember(itemCustomer.isMsbMember());
          currentCustomer.get().getIndividualCustomer().setEmployeeCode(itemCustomer.getStaffId());

          // update relationship info
          Optional<CustomerRelationShipEntity> relationShipEntity = entity.getCustomer().getCustomerRelationShips().stream().filter(relationItem -> relationItem.getCustomerRefId().equals(currentCustomer.get().getId())).findFirst();
          if (relationShipEntity.isPresent()) {
            relationShipEntity.get().setRelationship(itemCustomer.getRelationship());
            relationShipEntity.get().setRelationshipValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(itemCustomer.getRelationship()));
          }

          Map<String, CustomerAddressEntity> mapAddress = new HashMap<>();
          Map<String, CustomerIdentityEntity> mapIdentities = new HashMap<>();
          Set<CustomerIdentityEntity> currentIdentityListForAdd = new HashSet<>();
          Set<CustomerIdentityEntity> currentIdentityListForUpdate = new HashSet<>();
          Set<CustomerAddressEntity> currentAddressListForAdd = new HashSet<>();
          Set<CustomerAddressEntity> currentAddressListForUpdate = new HashSet<>();

          // Identity
          itemCustomer.getIdentities().forEach(itemIdentity -> {
            Optional<CustomerIdentityEntity> currentIdentity = currentCustomer.get().getCustomerIdentitys().stream().filter(objIdentity -> itemIdentity.getLdpIdentityId().equals(objIdentity.getLdpIdentityId())).findFirst();
            if (currentIdentity.isPresent()) {
              currentIdentity.get().setDocumentType(itemIdentity.getDocumentType());
              currentIdentity.get().setDocumentTypeValue(configurationServiceCache.getCategoryData(categoryMap, DOCUMENT_TYPE).get(itemIdentity.getDocumentType()));
              currentIdentity.get().setLdpIdentityId(itemIdentity.getLdpIdentityId());
              currentIdentity.get().setIssuedAt(itemIdentity.getIssuedAt());
              currentIdentity.get().setIssuedBy(itemIdentity.getIssuedBy());
              currentIdentity.get().setIssuedByValue(configurationServiceCache.getCategoryData(categoryMap, ISSUE_BY).get(itemIdentity.getIssuedBy()));
              currentIdentity.get().setIssuedPlace(itemIdentity.getIssuedPlace());
              currentIdentity.get().setIssuedPlaceValue(getNamePlaceByCodeFromCacheMercury(city, itemIdentity.getIssuedPlace()));
              currentIdentity.get().setPriority(itemIdentity.isPriority());
              currentIdentity.get().setIdentifierCode(itemIdentity.getIdentifierNumber());
              currentIdentityListForUpdate.add(currentIdentity.get());
              mapIdentities.put(currentIdentity.get().getLdpIdentityId(), currentIdentity.get());
            } else {
              CustomerIdentityEntity identityEntity = BeanUtil.copyBean(itemIdentity, CustomerIdentityEntity.class);
              identityEntity.setDocumentTypeValue(configurationServiceCache.getCategoryData(categoryMap, DOCUMENT_TYPE).get(itemIdentity.getDocumentType()));
              identityEntity.setIssuedByValue(configurationServiceCache.getCategoryData(categoryMap, ISSUE_BY).get(itemIdentity.getIssuedBy()));
              identityEntity.setIssuedPlaceValue(getNamePlaceByCodeFromCacheMercury(city, itemIdentity.getIssuedPlace()));
              currentIdentityListForAdd.add(identityEntity);
              identityEntity.setIdentifierCode(itemIdentity.getIdentifierNumber());
              mapIdentities.put(identityEntity.getLdpIdentityId(), identityEntity);
            }
          });

          // Address
          itemCustomer.getAddresses().forEach(itemAddress -> {
            Optional<CustomerAddressEntity> currentAddress = currentCustomer.get().getCustomerAddresses().stream().filter(objAddress -> itemAddress.getLdpAddressId().equals(objAddress.getLdpAddressId())).findFirst();
            if (currentAddress.isPresent()) {
              currentAddress.get().setAddressType(itemAddress.getAddressType());
              currentAddress.get().setAddressTypeValue(getAddressTypeValue(currentCustomer.get().getCustomerType(), itemAddress.getAddressType()));
              currentAddress.get().setCityCode(itemAddress.getCityCode());
              currentAddress.get().setCityValue(getNamePlaceByCodeFromCacheMercury(city, itemAddress.getCityCode()));
              currentAddress.get().setDistrictCode(itemAddress.getDistrictCode());
              currentAddress.get().setDistrictValue(getDistrictValue(itemAddress.getCityCode(), itemAddress.getDistrictCode()));
              currentAddress.get().setWardCode(itemAddress.getWardCode());
              currentAddress.get().setWardValue(getWardValue(itemAddress.getDistrictCode(), itemAddress.getWardCode()));
              currentAddress.get().setAddressLine(itemAddress.getAddressLine());
              currentAddress.get().setLdpAddressId(itemAddress.getLdpAddressId());
              currentAddress.get().setAddressLinkId(SourceApplication.LDP.name() + "-" + itemAddress.getLdpAddressId());
              currentAddressListForUpdate.add(currentAddress.get());
              mapAddress.put(currentAddress.get().getLdpAddressId(), currentAddress.get());

            } else {
              CustomerAddressEntity addressEntity = BeanUtil.copyBean(itemAddress, CustomerAddressEntity.class);
              addressEntity.setCityValue(getNamePlaceByCodeFromCacheMercury(city, itemAddress.getCityCode()));
              addressEntity.setWardValue(getWardValue(itemAddress.getDistrictCode(), itemAddress.getWardCode()));
              addressEntity.setDistrictValue(getDistrictValue(itemAddress.getCityCode(), itemAddress.getDistrictCode()));
              addressEntity.setAddressTypeValue(getAddressTypeValue(currentCustomer.get().getCustomerType(), itemAddress.getAddressType()));
              addressEntity.setAddressLinkId(SourceApplication.LDP.name() + "-" + itemAddress.getLdpAddressId());
              currentAddressListForAdd.add(addressEntity);
              mapAddress.put(addressEntity.getLdpAddressId(), addressEntity);
            }
          });

          Set<CustomerAddressEntity> addressToRemove = new HashSet<>();
          if (mapAddress.size() > 0) {
            currentCustomer.get().getCustomerAddresses().forEach(item -> {
              if (!mapAddress.containsKey(item.getLdpAddressId())) addressToRemove.add(item);
            });
          }
          if(CollectionUtils.isNotEmpty(addressToRemove)) {
            currentCustomer.get().cmsRemoveCustomerAddresses(addressToRemove);
          }

          Set<CustomerIdentityEntity> identitiesToRemove = new HashSet<>();
          if (mapIdentities.size() > 0) {
            currentCustomer.get().getCustomerIdentitys().forEach(item -> {
              if (!mapIdentities.containsKey(item.getLdpIdentityId())) identitiesToRemove.add(item);
            });
          }
          if (CollectionUtils.isNotEmpty(identitiesToRemove)) {
            currentCustomer.get().cmsRemoveCustomerIdentities(identitiesToRemove);
          }

          if (CollectionUtils.isNotEmpty(currentAddressListForUpdate)) {
            currentCustomer.get().updateCustomerAddresses(currentAddressListForUpdate);
          }
          if (CollectionUtils.isNotEmpty(currentAddressListForAdd)) {
            currentCustomer.get().addCustomerAddresses(currentAddressListForAdd);
          }

          if (CollectionUtils.isNotEmpty(currentIdentityListForUpdate)) {
            currentCustomer.get().updateCustomerIdentities(currentIdentityListForUpdate);
          }
          if (CollectionUtils.isNotEmpty(currentIdentityListForAdd)) {
            currentCustomer.get().addCustomerIdentities(currentIdentityListForAdd);
          }

          updateCustomer(currentCustomer.get(), BeanUtil.copyBean(itemCustomer, CmsIndividualCustomerDTO.class));
          currentCustomer.get().getIndividualCustomer().setGenderValue(configurationServiceCache.getCategoryData(categoryMap, GENDER).get(itemCustomer.getGender()));
          currentCustomer.get().getIndividualCustomer().setNationValue(configurationServiceCache.getCategoryData(categoryMap, NATIONAL).get(itemCustomer.getNation()));

          customerRepository.saveAndFlush(currentCustomer.get());
          customerEntityMap.put(currentCustomer.get().getRefCusId(), currentCustomer.get());
        } else {
          CmsIndividualCustomerDTO object = BeanUtil.copyBean(itemCustomer, CmsIndividualCustomerDTO.class);
          CustomerEntity customer =  persistCustomer(object);
          CustomerEntity customerEntity = customerRepository.save(customer);
          CustomerRelationShipEntity customerRelationShipEntity = new CustomerRelationShipEntity()
              .withCustomer(entity.getCustomer())
              .withCustomerRefId(customerEntity.getId())
              .withRelationship(itemCustomer.getRelationship())
              .withRelationshipValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(itemCustomer.getRelationship()));
          entity.getCustomer().getCustomerRelationShips().add(customerRelationShipEntity);

          customerEntityMap.put(customerEntity.getRefCusId(), customerEntity);
          removeList.add(customerEntity);
        }
      });

      // delete
      if (CollectionUtils.isNotEmpty(removeList)) {
        removeList.forEach(i -> customerRelationEntities.removeAll(customerRelationEntities.stream().filter(e -> e.getRefCusId().equals(i.getRefCusId())).collect(Collectors.toList())));
        if (CollectionUtils.isNotEmpty(customerRelationEntities)) {
          List<Long> relationShipIds = customerRelationEntities.stream().map(CustomerEntity::getId).collect(Collectors.toList());
          Set<CustomerRelationShipEntity> entities = customerRelationshipRepository.findByCustomerRefIdIn(relationShipIds);

          entity.getCustomer().removeCustomerRelationshipEntities(entities);
          customerRepository.deleteAllById(relationShipIds);
        }
      }

    } else {
      createCustomerRelationShip(request, entity, customerEntityMap);
    }

    Set<CustomerEntity> enterpriseRelationEntities = currentCusList.stream()
        .filter(customer -> CustomerType.EB.name().equals(customer.getCustomerType()))
        .collect(Collectors.toSet());

    saveOrUpdateEnterpriseRelations(request.getEnterpriseRelations(), enterpriseRelationEntities,
        entity);
  }

  public void saveOrUpdateEnterpriseRelations(List<CmsEnterpriseRelationDTO> cmsEnterpriseRelations,
      Set<CustomerEntity> enterpriseRelationEntities, ApplicationEntity applicationEntity) {

    if (CollectionUtils.isEmpty(cmsEnterpriseRelations)) {
      if (CollectionUtils.isNotEmpty(enterpriseRelationEntities)) {
        List<Long> customerIds = enterpriseRelationEntities.stream().map(CustomerEntity::getId)
            .collect(
                Collectors.toList());
        customerRepository.deleteAllById(customerIds);
        applicationEntity.getCustomer().getCustomerRelationShips()
            .removeIf(customerRef -> customerIds.contains(customerRef.getCustomerRefId()));
      }
      return;
    }

    if (CollectionUtils.isEmpty(enterpriseRelationEntities)) {
      enterpriseRelationEntities = new HashSet<>();
    }

    Map<String, CustomerEntity> customerEntityMap = enterpriseRelationEntities.stream()
        .collect(Collectors.toMap(CustomerEntity::getRefCusId, Function.identity()));

    List<Long> detachIds = new ArrayList<>();

    Set<CustomerEntity> finalEnterpriseRelationEntities = enterpriseRelationEntities;
    // Không lưu enterprise nếu businessType # V001
    cmsEnterpriseRelations.stream()
        .filter(c -> "V001".equalsIgnoreCase(c.getBusinessType()))
        .forEach(cmsEnterpriseRelation -> {
      CustomerEntity customerEntity;
      if (customerEntityMap.containsKey(cmsEnterpriseRelation.getRefEnterpriseId())) {
        customerEntity = customerEntityMap.get(cmsEnterpriseRelation.getRefEnterpriseId());
        customerEntity.setRefCusId(cmsEnterpriseRelation.getRefEnterpriseId());
        customerEntity.getEnterpriseCustomer()
            .setCompanyName(cmsEnterpriseRelation.getCompanyName());
        customerEntity.getEnterpriseCustomer().setBusinessType(
            cmsEnterpriseRelation.getBusinessType());
        customerEntity.getEnterpriseCustomer().setBusinessRegistrationNumber(
            cmsEnterpriseRelation.getBusinessRegistrationNumber());
        detachIds.add(customerEntity.getId());
      } else {
        customerEntity = new CustomerEntity();
        customerEntity.setRefCusId(cmsEnterpriseRelation.getRefEnterpriseId());
        customerEntity.setCustomerType(CustomerType.EB.name());
        customerEntity.setEnterpriseCustomer(new EnterpriseCustomerEntity());
        customerEntity.getEnterpriseCustomer()
            .setCompanyName(cmsEnterpriseRelation.getCompanyName());
        customerEntity.getEnterpriseCustomer().setBusinessType(
            cmsEnterpriseRelation.getBusinessType());
        customerEntity.getEnterpriseCustomer().setBusinessRegistrationNumber(
            cmsEnterpriseRelation.getBusinessRegistrationNumber());
        finalEnterpriseRelationEntities.add(customerEntity);
      }
    });

    Set<CustomerEntity> persistEnterpriseRelations = enterpriseRelationEntities.stream()
        .filter(enterprise -> Objects.isNull(enterprise.getId()) || detachIds.contains(
            enterprise.getId()))
        .collect(Collectors.toSet());

    Set<CustomerEntity> removeEnterpriseRelations = enterpriseRelationEntities.stream()
        .filter(enterprise -> Objects.nonNull(enterprise.getId()) && !detachIds.contains(
            enterprise.getId()))
        .collect(Collectors.toSet());
    // Đồng bộ customer id và version từ common
    syncCustomerEbFromCommon(persistEnterpriseRelations);
    customerRepository.saveAllAndFlush(persistEnterpriseRelations);

    List<Long> newCustomerRefIds = persistEnterpriseRelations.stream().map(CustomerEntity::getId)
        .filter(customerRefId -> !detachIds.contains(customerRefId))
        .collect(
            Collectors.toList());
    newCustomerRefIds.forEach(
        customerRefId -> applicationEntity.getCustomer().getCustomerRelationShips()
            .add(new CustomerRelationShipEntity().withCustomer(applicationEntity.getCustomer())
                .withCustomerRefId(customerRefId)));

    if (CollectionUtils.isNotEmpty(removeEnterpriseRelations)) {
      List<Long> customerIds = removeEnterpriseRelations.stream().map(CustomerEntity::getId)
          .collect(
              Collectors.toList());
      customerRepository.deleteAllById(customerIds);
      applicationEntity.getCustomer().getCustomerRelationShips()
          .removeIf(customerRef -> customerIds.contains(customerRef.getCustomerRefId()));
    }
    // Lấy id mối quan hệ KH doanh nghiệp
    getEnterpriseRelationsId(applicationEntity);
  }

  private void getEnterpriseRelationsId(ApplicationEntity applicationEntity) {
    log.info("START getEnterpriseRelationsId with bpmId=[{}]", applicationEntity.getBpmId());
    // Lấy danh sách KH liên quan type = EB
    Set<CustomerRelationShipEntity> relationShipEntities =
        applicationEntity.getCustomer().getCustomerRelationShips()
            .stream()
            .filter(c -> CustomerType.EB.name().equalsIgnoreCase(
                Objects.requireNonNull(customerRepository.findById(c.getCustomerRefId()).orElse(null)).getCustomerType()))
            .filter(c -> Objects.nonNull(Objects.requireNonNull(customerRepository.findById(c.getCustomerRefId()).orElse(null)).getRefCustomerId()))
            .collect(Collectors.toSet());
    relationShipEntities.forEach(crs -> {
      // Gọi sang common lấy thông tin mối quan hệ doanh nghiệp liên quuan
      Long customerId = crs.getCustomer().getRefCustomerId();
      Long relatedCustomerId = customerRepository.searchRefCustomerIdById(crs.getCustomerRefId()).orElse(null);
      CusRelationForSearchResponse response = getCustomerRelationsFromCommon(customerId, relatedCustomerId);

      if (ObjectUtils.isNotEmpty(response) || CollectionUtils.isNotEmpty(response.getRelationships())) {
        Relationship relationship = response.getRelationships().stream()
            .filter(r -> relatedCustomerId.equals(r.getRelatedCustomerId()))
            .findFirst()
            .orElse(null);
        if (CollectionUtils.isNotEmpty(relationship.getRelationshipDetails())) {
          // Nếu có mối quan hệ
          // Lấy giá trị đầu tiên
          CusRelationForSearchResponse.RelationshipDetail relationshipDetail =
              response.getRelationships().get(0).getRelationshipDetails().get(0);
          crs.setRelationship(relationshipDetail.getRelatedDetail());
          crs.setRelationshipRefId(relationshipDetail.getId());
          crs.setRelationshipValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(relationshipDetail.getRelatedDetail()));
        } else {
          // Nếu không có mối quan hệ
          // Gọi api tạo mối quan hệ với giá trị = R5
          RelationshipDetailInfo relationshipDetailInfo =
              createRelationshipForEnterprise(customerId, relatedCustomerId);
          // Lưu id mối quan hệ doanh nghiệp liên quan
          crs.setRelationship(relationshipDetailInfo.getRelatedDetail());
          crs.setRelationshipRefId(relationshipDetailInfo.getId());
          crs.setRelationshipValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(relationshipDetailInfo.getRelatedDetail()));
        }
      } else {
        // Nếu không có mối quan hệ
        // Gọi api tạo mối quan hệ với giá trị = R5
        RelationshipDetailInfo relationshipDetailInfo =
            createRelationshipForEnterprise(customerId, relatedCustomerId);
        // Lưu id mối quan hệ doanh nghiệp liên quan
        crs.setRelationship(relationshipDetailInfo.getRelatedDetail());
        crs.setRelationshipRefId(relationshipDetailInfo.getId());
        crs.setRelationshipValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(relationshipDetailInfo.getRelatedDetail()));
      }
    });
    log.info("END getEnterpriseRelationsId with bpmId=[{}]", applicationEntity.getBpmId());
  }
  private RelationshipDetailInfo createRelationshipForEnterprise(Long customerId,
      Long relatedCustomerId) {
    CustomerBaseResponse<CustomerRelationResponse> createResponse =
        customerClient.createCustomerRelationship(AddOrUpdateCusRelationshipRequest.builder()
            .customerId(customerId)
            .relatedCustomerId(relatedCustomerId)
            .relationships(Collections.singletonList(RelationshipDetail.builder()
                .relatedDetail("R5")
                .build()))
            .build());

    if (ObjectUtils.isEmpty(createResponse) || ObjectUtils.isEmpty(createResponse.getData())
    || CollectionUtils.isEmpty(createResponse.getData().getRelationships())) {
      // Thông báo lỗi tạo mối liên hệ doanh nghiệp liên quan
      throw new ApprovalException(ERROR_CREATE_ENTERPRISE_CUSTOMER_RELATIONSHIP);
    }
    return createResponse.getData().getRelationships().get(0)
        .getRelationshipDetails().stream()
        .filter(r -> r.getRelatedDetail().equals("R5"))
        .findFirst()
        .orElse(null);
  }

  private void syncCustomerEbFromCommon(Set<CustomerEntity> persistEnterpriseRelations) {
    log.info("START syncCustomerIdFromCommon");
    // Map ĐKKD với từng KH doanh nghiệp
    Map<String, CustomerEntity> enterpriseCustomerMaps = persistEnterpriseRelations.stream()
        .filter(e -> "V001".equalsIgnoreCase(e.getEnterpriseCustomer().getBusinessType()))
        .collect(Collectors.toMap(
            enterpriseCus -> enterpriseCus.getEnterpriseCustomer().getBusinessRegistrationNumber(),
            Function.identity()
        ));
    // Lấy danh sách ĐKKD
    List<String> businessRegistrationNumbers = persistEnterpriseRelations.stream()
        .filter(e -> "V001".equalsIgnoreCase(e.getEnterpriseCustomer().getBusinessType()))
        .map(enterpriseCus -> enterpriseCus.getEnterpriseCustomer().getBusinessRegistrationNumber())
        .collect(Collectors.toList());
    // Kiểm tra tồn tại KH doanh nghiệp tại common
    CustomerBaseResponse<CustomersResponse> cusInfo = customerClient.searchCustomerByList(
        new SearchByListCustomerRequest().withIdentityNumbers(businessRegistrationNumbers));
    if (ObjectUtils.isEmpty(cusInfo) || ObjectUtils.isEmpty(cusInfo.getData()) ||
    ObjectUtils.isEmpty(cusInfo.getData().getCustomersEb())) {
      // Tạo mới KH cho toàn bộ danh sách businessRegistrationNumbers
      createEnterpriseCustomers(persistEnterpriseRelations);
    } else {
      // Map ĐKKD với KH trả ra từ common
      Map<String, CustomersEBResponse> customersEBResponseMaps = new HashMap<>();
      businessRegistrationNumbers.forEach(number -> {
        if (ObjectUtils.isNotEmpty(findCustomersEBResponse(cusInfo, number))) {
          customersEBResponseMaps.put(number, findCustomersEBResponse(cusInfo, number));
        }
      });
      businessRegistrationNumbers.forEach(number -> {
        CustomersEBResponse res = customersEBResponseMaps.get(number);
        CustomerEntity customerEntity = enterpriseCustomerMaps.get(number);
        if (ObjectUtils.isNotEmpty(res)) {
          customerEntity.setRefCustomerId(res.getCustomer().getId());
          customerEntity.setVersion(res.getCustomer().getVersion());
        } else {
          // Gọi API tạo mới KH doanh nghiệp từ common
          callApiCreateCustomerEb(customerEntity);
        }
      });
    }
    log.info("END syncCustomerIdFromCommon");
  }
  private CustomersEBResponse findCustomersEBResponse(CustomerBaseResponse<CustomersResponse> cusInfo, String number) {
    return cusInfo.getData().getCustomersEb().stream()
        .filter(res -> CollectionUtils.isNotEmpty(res.getIdentityDocuments()))
        .filter(res -> res.getIdentityDocuments().stream()
            .map(IdentityDocumentsEbRes::getIdentityNumber)
            .anyMatch(identityNumber -> Objects.equals(identityNumber, number)))
        .findFirst()
        .orElse(null);
  }

  private void createEnterpriseCustomers(Set<CustomerEntity> persistEnterpriseRelations) {
    if (CollectionUtils.isNotEmpty(persistEnterpriseRelations)) {
      persistEnterpriseRelations.forEach(this::callApiCreateCustomerEb);
    }
  }
  private void callApiCreateCustomerEb (CustomerEntity customerEntity) {
    CustomerBaseResponse<CustomerEbResponse> customerEbResp =
        customerClient.createCustomerEb(
            new CreateCustomerEbRequest()
                .withCustomer(CustomerEb.builder()
                    .name(customerEntity.getEnterpriseCustomer().getCompanyName())
                    .build())
                .withIdentityDocuments(Collections.singletonList(IdentityDocumentEbRequest.builder()
                    .type("BR")
                    .identityNumber(customerEntity.getEnterpriseCustomer().getBusinessRegistrationNumber())
                    .primary(true)
                    .build()))
        );
    // Define lỗi nếu không tạo được KH Doanh nghiệp
    // Save refCustomerId and version
    customerEntity.setRefCustomerId(customerEbResp.getData().getCustomer().getId());
    customerEntity.setVersion(customerEbResp.getData().getCustomer().getVersion());
  }

  public ApplicationEntity createCustomerRelationShip(PostCmsV2CreateApplicationRequest request, ApplicationEntity entity, Map<String, CustomerEntity> customerEntityMap) {
    log.info("createCustomerRelationShip START with request={}", JsonUtil.convertObject2String(request, objectMapper));
    List<CustomerEntity> relationList = new ArrayList<>();
    Set<CustomerRelationShipEntity> cusRelationList = new HashSet<>();
    Iterator<CmsCustomerRelationDTO> it = request.getCustomerRelations().iterator();
    while (it.hasNext()) {
      CmsCustomerRelationDTO item = it.next();
      CmsIndividualCustomerDTO object = BeanUtil.copyBean(item, CmsIndividualCustomerDTO.class);
      CustomerEntity customer =  persistCustomer(object);
      customer.setRelationship(item.getRelationship());
      customer.setRelationshipValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(item.getRelationship()));
      customer.setRefCusId(item.getRefCusId());
      relationList.add(customer);

      customerEntityMap.put(customer.getRefCusId(), customer);
    }

    if (CollectionUtils.isNotEmpty(relationList)) {
      List<CustomerEntity> list =  customerRepository.saveAllAndFlush(relationList);
      Iterator<CustomerEntity> iterator = list.iterator();
      while (iterator.hasNext()) {
        CustomerEntity item = iterator.next();
        cusRelationList.add(new CustomerRelationShipEntity()
            .withCustomer(entity.getCustomer())
            .withCustomerRefId(item.getId())
            .withRelationship(item.getRelationship())
            .withRelationshipValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(item.getRelationship())));
      }
      if (cusRelationList != null) {
        entity.getCustomer().getCustomerRelationShips().addAll(cusRelationList);
      }
    }
    return entity;
  }

  public CustomerEntity persistCustomer(CmsIndividualCustomerDTO request) {
    // Customer
    CustomerEntity entityCus = new CustomerEntity().withRefCusId(request.getRefCusId()).withCustomerType(request.getCustomerType());

    // Individual Customer
    IndividualCustomerEntity indiCus = new IndividualCustomerEntity()
        .withFirstName(Util.splitFirstName(request.getFullName()))
        .withLastName(Util.splitLastName(request.getFullName()))
        .withGender(request.getGender())
        .withDateOfBirth(request.getDateOfBirth())
        .withMartialStatus(request.getMartialStatus().toString())
        .withMartialStatusValue(configurationServiceCache.getCategoryData(categoryMap, MARTIAL_STATUS).get(request.getMartialStatus().toString()))
        .withNation(request.getNation())
        .withSubject(request.getTypeOfCustomer())
        .withEmail(request.getEmail())
        .withPhoneNumber(request.getPhoneNumber())
        .withLiteracy(request.getLiteracy())
        .withMsbMember(request.isMsbMember())
        .withEmployeeCode(request.getStaffId());
    entityCus.setIndividualCustomer(indiCus);

    // Customer Identity
    List<CustomerIdentityEntity> lstEntityCusIdentity = new ArrayList<>();
    for (CmsCustomerIdentityDTO item : request.getIdentities()) {
      CustomerIdentityEntity entity = new CustomerIdentityEntity()
          .withDocumentType(item.getDocumentType())
          .withDocumentTypeValue(configurationServiceCache.getCategoryData(categoryMap, DOCUMENT_TYPE).get(item.getDocumentType()))
          .withIdentifierCode(item.getIdentifierNumber())
          .withIssuedAt(item.getIssuedAt())
          .withIssuedBy(item.getIssuedBy())
          .withIssuedByValue(configurationServiceCache.getCategoryData(categoryMap, ISSUE_BY).get(item.getIssuedBy()))
          .withIssuedPlace(item.getIssuedPlace())
          .withIssuedPlaceValue(getNamePlaceByCodeFromCacheMercury(city, item.getIssuedPlace()))
          .withPriority(item.isPriority())
          .withLdpIdentityId(item.getLdpIdentityId())
          .withCustomer(entityCus);
      lstEntityCusIdentity.add(entity);
    }

    // Customer address
    List<CustomerAddressEntity> lstEntityCusAddress = new ArrayList<>();
    for (CmsCustomerAddressDTO item : request.getAddresses()) {
      CustomerAddressEntity entity = new CustomerAddressEntity()
          .withAddressType(item.getAddressType())
          .withAddressTypeValue(getAddressTypeValue(request.getCustomerType(), item.getAddressType()))
          .withCanDelete(!AddressType.HK_THUONG_TRU.getValue().equals(item.getAddressType()));
      entity.setCustomer(entityCus);
      entity.setCityCode(item.getCityCode());
      entity.setCityValue(getNamePlaceByCodeFromCacheMercury(city, item.getCityCode()));
      entity.setDistrictCode(item.getDistrictCode());
      entity.setDistrictValue(getDistrictValue(item.getCityCode(), item.getDistrictCode()));
      entity.setWardCode(item.getWardCode());
      entity.setWardValue(getWardValue(item.getDistrictCode(), item.getWardCode()));
      entity.setAddressLine(item.getAddressLine());
      entity.setAddressLinkId(SourceApplication.LDP.name() + "-" + item.getLdpAddressId());
      entity.setLdpAddressId(item.getLdpAddressId());
      lstEntityCusAddress.add(entity);
    }
    entityCus.setCustomerIdentitys(new HashSet<>(lstEntityCusIdentity));
    entityCus.setCustomerAddresses(new HashSet<>(lstEntityCusAddress));

    updateCustomer(entityCus, request);

    entityCus.getIndividualCustomer().setGenderValue(
        configurationServiceCache.getCategoryData(categoryMap, GENDER)
            .get(entityCus.getIndividualCustomer().getGender()));
    entityCus.getIndividualCustomer().setNationValue(
        configurationServiceCache.getCategoryData(categoryMap, NATIONAL)
            .get(entityCus.getIndividualCustomer().getNation()));

    return entityCus;
  }

  public void saveOrUpdateIncomes(List<CmsIncomeDTO> incomes, ApplicationEntity applicationEntity) {

    // Danh sách nguồn thu hiện tại
    Set<ApplicationIncomeEntity> incomeEntities = applicationEntity.getIncomes();

    // Danh sách nguồn thu gửi lên từ LDP
    List<ApplicationIncomeEntity> cmsIncomeEntities = new ArrayList<>();
    incomes.forEach(income -> cmsIncomeEntities.add(CmsApplicationIncomeFactory.getApplicationIncomeReverse().build(income)));

    // Tạo map nguồn thu từ danh sách nguồn thu hiện tại theo key ldpIncomeId (id nguồn thu từ LDP)
    Map<String, ApplicationIncomeEntity> mapIncome = incomeEntities.stream()
        .filter(incomeEntity -> StringUtils.isNotBlank(incomeEntity.getLdpIncomeId()))
        .collect(Collectors.toMap(ApplicationIncomeEntity::getLdpIncomeId, Function.identity()));

    Set<ApplicationIncomeEntity> createIncomes = new HashSet<>();
    Set<ApplicationIncomeEntity> updateIncomes = new HashSet<>();

    cmsIncomeEntities.forEach(income -> {

      setIncomeMethodValue(income);

      // Kiểm tra
      // - Nếu ldpIncomeId gửi lên trùng với ldpIncomeId của danh sách nguồn thu hiện tại thì thực hiện cập nhật
      // - Nếu ldpIncomeId gửi lên không trùng với ldpIncomeId của danh sách nguồn thu hiện tại thì thực hiện tạo mới
      if (mapIncome.containsKey(income.getLdpIncomeId())) {
        ApplicationIncomeEntity incomeEntity = mapIncome.get(income.getLdpIncomeId());

        // Set data cho income item
        setIncomeItemsValue(income, incomeEntity);

        updateIncomes.add(income);
      } else {

        // Set data cho income item
        setIncomeItemsValue(income, null);

        createIncomes.add(income);
      }
    });

    // Gọi hàm cập nhật/tạo mới nguồn thu
    applicationEntity.cmsUpdateApplicationIncomes(updateIncomes);
    applicationEntity.addApplicationIncomes(createIncomes);
  }

  public void saveOrUpdateCredit(List<CmsBaseCreditDTO> credits, ApplicationEntity applicationEntity) {

    // Danh sách các khoản vay đề xuất hiện tại
    Set<ApplicationCreditEntity> creditEntities = applicationEntity.getCredits();

    // Danh sách các khoản vay đề xuất được gửi lên từ LDP
    List<ApplicationCreditEntity> cmsCreditEntities = new ArrayList<>();
    credits.forEach(credit -> cmsCreditEntities.add(
        CmsApplicationCreditFactory.getApplicationCreditReverse(credit.getCreditType()).build(credit)));

    // Tạo map khoản vay đề xuất từ danh sách khoản vay hiện tại theo key ldpCreditId (id khoản vay từ LDP)
    Map<String, ApplicationCreditEntity> mapCredit = creditEntities.stream()
        .filter(creditEntity -> StringUtils.isNotBlank(creditEntity.getLdpCreditId()))
        .collect(Collectors.toMap(ApplicationCreditEntity::getLdpCreditId, Function.identity()));

    Set<ApplicationCreditEntity> createCredits = new HashSet<>();
    Set<ApplicationCreditEntity> updateCredits = new HashSet<>();

    cmsCreditEntities.forEach(cmsCreditEntity -> {
      cmsCreditEntity.setCreditTypeValue(configurationServiceCache.getCategoryData(categoryMap, CREDIT_TYPE).get(cmsCreditEntity.getCreditType()));
      cmsCreditEntity.setGuaranteeFormValue(configurationServiceCache.getCategoryData(categoryMap, GUARANTEE_FORM).get(cmsCreditEntity.getGuaranteeForm()));
      cmsCreditEntity.setApplication(applicationEntity);

      // Kiểm tra
      // - Nếu ldpCreditId gửi lên trùng với ldpCreditId của danh sách khoản vay hiện tại thì thực hiện cập nhật
      // - Nếu ldpCreditId gửi lên không trùng với ldpCreditId của danh sách khoản vay hiện tại thì thực hiện tạo mới
      if (mapCredit.containsKey(cmsCreditEntity.getLdpCreditId())) {
        ApplicationCreditEntity existCreditEntity = mapCredit.get(cmsCreditEntity.getLdpCreditId());

        // Set credit value
        setCreditValue(cmsCreditEntity, existCreditEntity);

        updateCredits.add(cmsCreditEntity);
      } else {
        // Set credit value
        setCreditValue(cmsCreditEntity, null);

        createCredits.add(cmsCreditEntity);
      }
    });

    // Gọi hàm cập nhật/tạo mới khoản vay
    applicationEntity.cmsUpdateCreditEntities(updateCredits);
    applicationEntity.addCreditEntities(createCredits);
  }


  public void saveOrUpdateApplicationContact(List<CmsApplicationContactDTO> contactList, ApplicationEntity applicationEntity) {
    if (CollectionUtils.isEmpty(contactList)) {
      applicationEntity.removeAllContact();
    } else {
      if (contactList.size() > 2) {
        throw new ApprovalException(NUMBER_OF_CONTACT_ERROR);
      }

      Set<ApplicationContactEntity> collection = new HashSet<>();
      for(int i = 0; i < contactList.size(); i++){
        ApplicationContactEntity contact = new ApplicationContactEntity();
        CmsApplicationContactDTO item = contactList.get(i);
        contact.setApplication(applicationEntity);
        contact.setRelationship(item.getRelationship());
        contact.setFullName(item.getFullName());
        contact.setPhoneNumber(item.getPhoneNumber());
        contact.setOrderDisplay(i);
        collection.add(contact);
      }
      applicationEntity.addContact(collection);
    }
  }


  public void updateCustomer(CustomerEntity entityCus, CmsIndividualCustomerDTO request) {
    // Search Customer
    log.info("START method CmsCreateApplicationServiceImpl.checkCustomer with request: [{}]", JsonUtil.convertObject2String(request, objectMapper));
    CmsCustomerIdentityDTO identity = request.getIdentities().stream().filter(CmsCustomerIdentityDTO::isPriority).findFirst().orElse(null);
    // Kiểm tra thông tin định danh chính
    if (Objects.isNull(identity) || StringUtils.isBlank(identity.getIdentifierNumber())) {
      throw new ApprovalException(DomainCode.INVALID_INPUT_IDENTITY_NUMBER);
    }
    // Lấy danh sách định danh chính và phụ của KH/NLQ
    List<String> identitiesNumber = request.getIdentities().stream()
        .map(CmsCustomerIdentityDTO::getIdentifierNumber)
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.toList());
    // Kiểm tra KH/NLQ theo danh sách các định danh từ customer info
    CustomerBaseResponse<CustomersResponse> cusInfo = customerClient.searchCustomerByList(new SearchByListCustomerRequest().withIdentityNumbers(identitiesNumber));
    if (Objects.isNull(cusInfo) || Objects.isNull(cusInfo.getData()) || CollectionUtils.isEmpty(cusInfo.getData().getCustomersRb()) ||
         (cusInfo.getData().getCustomersRb().size() == 1 && Objects.isNull(cusInfo.getData().getCustomersRb().get(0).getCustomer()))) {
      // Thực hiện tạo mới khi không tìm thấy thông tin khách hàng
      CustomerBaseResponse<CreateRBCustomerResponse> cusInfoNew = customerClient.createCustomer(buildCustomerRequest(request, null));
      try {
        IdentityDocumentResponse identityDocument = checkIdentityDocument(cusInfoNew);

        entityCus.setCif(identityDocument.getCif());
        entityCus.setRefCustomerId(cusInfoNew.getData().getCustomer().getId());
        entityCus.setBpmCif(cusInfoNew.getData().getCustomer().getBpmCif());
        mappingCommonCustomerField(entityCus, cusInfoNew.getData(), identityDocument);
        log.info("End create customer with response: {}", JsonUtil.convertObject2String(cusInfoNew, objectMapper));
      } catch (Exception e) {
        throw e;
      }

    } else if (cusInfo.getData().getCustomersRb().size() == 1 && Objects.nonNull(cusInfo.getData().getCustomersRb().get(0).getCustomer())) {
      // Thực hiện cập nhật thông tin KH khi tìm thấy thông tin 1 KH duy nhất
      UpdateRBCustomerResponse updateRBCustomer = customerClient.updateCustomer(buildCustomerRequest(request, cusInfo.getData().getCustomersRb().get(0).getCustomer())).getData();
      try {
        IdentityDocumentResponse identityDocument = checkIdentityDocument(updateRBCustomer);

        entityCus.setCif(identityDocument.getCif());
        entityCus.setBpmCif(cusInfo.getData().getCustomersRb().get(0).getCustomer().getBpmCif());
        entityCus.setRefCustomerId(cusInfo.getData().getCustomersRb().get(0).getCustomer().getId());
        mappingCommonCustomerField(entityCus, updateRBCustomer, identityDocument);
        log.info("End update customer with response: {}", JsonUtil.convertObject2String(updateRBCustomer, objectMapper));
      } catch (Exception e) {
        throw e;
      }
    } else {
      log.error("ERROR: List identities [{}] has found multiple customers [{}] from customer info",
          identitiesNumber, JsonUtil.convertObject2String(cusInfo.getData().getCustomersRb(), objectMapper));
      // Thông báo lỗi khi tìm thấy nhiều hơn 1 KH trong customer info
      throw new ApprovalException(DomainCode.NUMBER_OF_CUSTOMER_IS_INVALID, new Object[] {entityCus.getIndividualCustomer().fullName()});
    }
  }

  private IdentityDocumentResponse checkIdentityDocument(CustomerBaseResponse<CreateRBCustomerResponse> cusInfoNew) throws ApprovalException {
    if (Objects.isNull(cusInfoNew) || Objects.isNull(cusInfoNew.getData())
        || Objects.isNull(cusInfoNew.getData().getCustomer())
        || !cusInfoNew.getData().getCustomer().isActive()) {
      log.info("checkIdentityDocument not found customer detail info document or inactive");
      throw new ApprovalException(DomainCode.ERROR_CREATE_CUSTOMER, new Object[]{cusInfoNew});
    }

    IdentityDocumentResponse identityDocument = cusInfoNew.getData().getIdentityDocuments().stream().filter(i -> i.isPrimary()).findFirst().orElse(null);

    if(Objects.isNull(identityDocument) ) {
      log.info("checkIdentityDocument not found primary identity document");
      throw new ApprovalException(DomainCode.ERROR_CREATE_CUSTOMER, new Object[]{cusInfoNew});
    }

    return identityDocument;
  }

  private IdentityDocumentResponse checkIdentityDocument(UpdateRBCustomerResponse updateRBCustomer) throws ApprovalException {
    if (Objects.isNull(updateRBCustomer)
        || Objects.isNull(updateRBCustomer.getCustomer())
        || !updateRBCustomer.getCustomer().isActive()) {
      log.info("checkIdentityDocument not found customer detail info document or inactive");
      throw new ApprovalException(DomainCode.ERROR_UPDATE_CUSTOMER, new Object[]{updateRBCustomer});
    }

    IdentityDocumentResponse identityDocument = updateRBCustomer.getIdentityDocuments().stream().filter(i -> i.isPrimary()).findFirst().orElse(null);

    if(Objects.isNull(identityDocument)) {
      log.info("checkIdentityDocument not found primary identity document");
      throw new ApprovalException(DomainCode.ERROR_UPDATE_CUSTOMER, new Object[]{updateRBCustomer});
    }

    return identityDocument;
  }

  public void mappingCommonCustomerField(CustomerEntity customerEntity, CreateRBCustomerResponse customer, IdentityDocumentResponse identityDocument) throws ApprovalException {

    // Set 3 trường dữ liệu từ Core không cho phép thay đổi
    // Ngày sinh, giới tính, quốc tịch
    customerEntity.getIndividualCustomer().setDateOfBirth(customer.getCustomer().getBirthday());
    customerEntity.getIndividualCustomer().setGender(customer.getCustomer().getGender());
    customerEntity.getIndividualCustomer().setNation(customer.getCustomer().getNational());
    if (customer.getCustomer().getBirthday() != null) {
      customerEntity.getIndividualCustomer()
          .setAge(LocalDate.now().getYear() - customer.getCustomer().getBirthday().getYear());
    }
    customerEntity.setVersion(customer.getCustomer().getVersion());

    Map<String, Long> currentIdentityIdMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(customer.getIdentityDocuments())) {
      Iterator<IdentityDocumentResponse> it = customer.getIdentityDocuments().iterator();
      while (it.hasNext()) {
        IdentityDocumentResponse identityDocumentResponse = it.next();
        currentIdentityIdMap.put(identityDocumentResponse.getIdentityNumber() + identityDocumentResponse.getType(), identityDocumentResponse.getId());
      }
    }

    Map<String, CustomerIdentityEntity> identityIdMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(customerEntity.getCustomerIdentitys())) {
      Iterator<CustomerIdentityEntity> it = customerEntity.getCustomerIdentitys().iterator();
      while (it.hasNext()) {
        CustomerIdentityEntity identityEntity = it.next();
        identityIdMap.put(identityEntity.getIdentifierCode() + identityEntity.getDocumentType(), identityEntity);
      }
    }

    customerEntity.getCustomerIdentitys().forEach(identity -> {
    // Set lại 3 trường dữ liệu từ Core không cho phép thay đổi
    // Mã định danh chính, loại định danh chính, ngày cấp
    if (currentIdentityIdMap.containsKey(identity.getIdentifierCode() + identity.getDocumentType())
        && identityDocument.getIdentityNumber().equalsIgnoreCase(identity.getIdentifierCode())
        && identityDocument.getType().equalsIgnoreCase(identity.getDocumentType())) {
      identity.setIdentifierCode(identityDocument.getIdentityNumber());
      identity.setDocumentType(identityDocument.getType());
      identity.setDocumentTypeValue(configurationServiceCache.getCategoryData(categoryMap, DOCUMENT_TYPE).get(identity.getDocumentType()));
      identity.setIssuedAt(identityDocument.getIssuedDate());
      identity.setRefIdentityId(identityDocument.getId());
      identity.setPriority(true);
    } else {
      // Set lại id common identity
      identity.setRefIdentityId(currentIdentityIdMap.get(identity.getIdentifierCode() + identity.getDocumentType()));
      identity.setPriority(false);
    }
  });

    Set<CustomerIdentityEntity> identities = new HashSet<>();
    customer.getIdentityDocuments().stream().forEach(currentIdentity -> {
      if (!identityIdMap.containsKey(currentIdentity.getIdentityNumber() + currentIdentity.getType())) {
        identities.add(new CustomerIdentityEntity()
            .withIdentifierCode(currentIdentity.getIdentityNumber())
            .withPriority(currentIdentity.isPrimary() ? true : false)
            .withDocumentType(currentIdentity.getType())
            .withDocumentTypeValue(configurationServiceCache.getCategoryData(categoryMap, DOCUMENT_TYPE).get(currentIdentity.getType()))
            .withIssuedAt(currentIdentity.getIssuedDate())
            .withRefIdentityId(currentIdentity.getId())
            .withIssuedPlace(currentIdentity.getIssuedPlace())
            .withIssuedPlaceValue(getNamePlaceByCodeFromCacheMercury(city, currentIdentity.getIssuedPlace()))
            .withIssuedBy(currentIdentity.getIssuedBy())
            .withIssuedByValue(configurationServiceCache.getCategoryData(categoryMap, ISSUE_BY).get(currentIdentity.getIssuedBy()))
        );
      }
    });

    if (CollectionUtils.isNotEmpty(identities)) {
      customerEntity.addCustomerIdentities(identities);
    }

    currentIdentityIdMap.clear();
    identityIdMap.clear();

    // Set lại id common address
    if (CollectionUtils.isEmpty(customer.getAddresses())) {
      return;
    }

    customerEntity.getCustomerAddresses().forEach(address -> {
      String key = address.getAddressType() + address.getCityCode() + address.getDistrictCode() + address.getWardCode();
      for (AddressResponse commonAddress : customer.getAddresses()) {
        String commonKey = commonAddress.getAddressType() + commonAddress.getCityCode() + commonAddress.getDistrictCode() + commonAddress.getWardCode();
        if (!key.equalsIgnoreCase(commonKey)) {
          continue;
        }
        address.setRefAddressId(commonAddress.getId());
      }
    });

  }

  private CommonCustomerRequest buildCustomerRequest(CmsIndividualCustomerDTO request, CustomerResponse customerResponse) {
    Customer customer = Customer.builder()
        .name(request.getFullName())
        .birthday(request.getDateOfBirth())
        .gender(request.getGender())
        .national(request.getNation())
        .maritalStatus(request.getMartialStatus())
        .phoneNumber(request.getPhoneNumber())
        .email(request.getEmail())
        .customerSegment(request.getTypeOfCustomer())
        .object(false)
        .build();
    if (Objects.nonNull(customerResponse)) {
      customer.setBpmCif(customerResponse.getBpmCif());
      customer.setId(customerResponse.getId());
    }

    List<Address> addresses = new ArrayList<>();
    request.getAddresses().stream().filter(e -> StringUtils.isNotBlank(
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

    List<IdentityDocument> identities = new ArrayList<>();

    CmsCustomerIdentityDTO identity = request.getIdentities().stream().filter(CmsCustomerIdentityDTO::isPriority).findFirst().orElse(null);
    if (Objects.nonNull(identity)) {
      identities.add(IdentityDocument.builder()
          .type(StringUtil.getValue(identity.getDocumentType()))
          .identityNumber( StringUtil.getValue(identity.getIdentifierNumber()))
          .issuedBy(StringUtil.getValue(identity.getIssuedBy()))
          .issuedDate(StringUtil.getValue(DateUtils.format(identity.getIssuedAt(), "dd/MM/yyyy")))
          .issuedPlace(StringUtil.getValue(identity.getIssuedPlace()))
          .edit(false)
          .primary(Boolean.TRUE)
          .build());
    }
    // add primary identity
    request.getIdentities().stream().filter(e -> !e.isPriority()).forEach(identityItem -> identities.add(IdentityDocument.builder()
        .type(identityItem.getDocumentType())
        .identityNumber(identityItem.getIdentifierNumber())
        .issuedBy(identityItem.getIssuedBy())
        .issuedDate(DateUtils.format(identityItem.getIssuedAt(), "dd/MM/yyyy"))
        .issuedPlace(identityItem.getIssuedPlace())
        .edit(false)
        .primary(Boolean.FALSE)
        .build()));

    CommonCustomerRequest result = new CommonCustomerRequest();
    result.setCustomer(customer);
    result.setAddresses(addresses);
    result.setIdentityDocuments(identities);
    return result;
  }

  private String getNamePlaceByCodeFromCacheMercury(MercuryDataResponse lstPlaces, String code) {
    try {
      if (StringUtils.isEmpty(code) || !code.chars().allMatch(Character::isDigit)) {
        return  "";
      }
      if (Objects.nonNull(lstPlaces) && CollectionUtils
          .isNotEmpty(lstPlaces.getValue())) {
        MercuryDataResponse.Value cityVal = lstPlaces.getValue().stream().filter(e -> StringUtils
            .isNotBlank(e.getId()) && code.equalsIgnoreCase(e.getId())).findFirst().orElse(null);
        if (Objects.nonNull(cityVal)) {
          return cityVal.getName();
        }
      }
    } catch (Exception ex) {
      log.error("getCityByCodeFromCacheMercury END with error: {}", ex.getMessage());
    }
    return "";
  }

  private String getAddressTypeValue(String customerType, String addressType) {
    String result = "";
    try {
      if (ApplicationConstant.Customer.EB.equalsIgnoreCase(customerType)) {
        result = configurationServiceCache.getCategoryData(categoryMap, EB_ADDRESS_TYPE_V001).get(addressType);
      }
      if (ApplicationConstant.Customer.RB.equalsIgnoreCase(customerType)) {
        if (AddressType.HK_THUONG_TRU.getValue().equalsIgnoreCase(addressType)) {
          result = configurationServiceCache.getCategoryData(categoryMap, RB_ADDRESS_TYPE_V001).get(addressType);
        }
        if (AddressType.DIA_CHI_TSC.getValue().equalsIgnoreCase(addressType)) {
          result = configurationServiceCache.getCategoryData(categoryMap, RB_ADDRESS_TYPE_V002).get(addressType);
        }
      }
    } catch (Exception ex) {
      log.error("getAddressTypeValue End with error: {}", ex.getMessage());
    }
    return result;
  }

  /*
   * update vung, mien
   */
  public void setBranchUserRM(ApplicationEntity entityApp, CmsBaseResponse response, DataResponse regionAreaResp) {
    if (Objects.isNull(regionAreaResp) || Objects.isNull(regionAreaResp.getBusinessUnitDetails())) {
      return;
    } else {
      if (regionAreaResp.getBusinessUnitDetails().size() > 1) {
        return;
      }
    }

    entityApp.setCreatedFullName(regionAreaResp.getCreatedFullName());
    entityApp.setCreatedPhoneNumber(regionAreaResp.getCreatedPhoneNumber());
    entityApp.setBusinessUnit(Objects.nonNull(regionAreaResp.getBusinessUnitDetails()) ? regionAreaResp.getBusinessUnitDetails().get(0).getName() : null);
    entityApp.setBusinessCode(Objects.nonNull(regionAreaResp.getBusinessUnitDetails()) ? regionAreaResp.getBusinessUnitDetails().get(0).getCode() : null);

    if (Objects.nonNull(regionAreaResp.getBusinessUnitDetails()) && CollectionUtils.isNotEmpty(regionAreaResp.getBusinessUnitDetails())) {
      entityApp.setAreaCode(regionAreaResp.getBusinessUnitDetails().get(0).getBusinessAreaCode());
      entityApp.setArea(regionAreaResp.getBusinessUnitDetails().get(0).getBusinessAreaFullName());
    }

    if (Objects.nonNull(regionAreaResp.getRegionDetails()) && regionAreaResp.getRegionDetails().size() > 0) {
      entityApp.setRegion(regionAreaResp.getRegionDetails().get(0).getFullName());
      entityApp.setRegionCode(regionAreaResp.getRegionDetails().get(0).getCode());
    }

    OrganizationTreeDetail branchDetail = Util.getTreeDetailByType("CN", regionAreaResp.getRegionDetails().get(0));
    if (Objects.nonNull(branchDetail)) {
      entityApp.setBranchCode(branchDetail.getCode());
      entityApp.setBranchName(branchDetail.getFullName());
    }
  }

  private String getDistrictValue(String cityCode, String districtCode) {
    MercuryDataResponse district = null;
    if (StringUtils.isNotBlank(cityCode)) {
      district = mercuryCache.searchPlace(cityCode);
    }
    return getNamePlaceByCodeFromCacheMercury(district, districtCode);
  }

  private String getWardValue(String districtCode, String wardCode) {
    MercuryDataResponse ward = null;
    if (StringUtils.isNotBlank(districtCode)) {
      ward = mercuryCache.searchPlace(districtCode);
    }
    return getNamePlaceByCodeFromCacheMercury(ward, wardCode);
  }

  private void setIncomeMethodValue(ApplicationIncomeEntity incomeEntity) {
    if (ACTUALLY.equals(incomeEntity.getIncomeRecognitionMethod())) {
      incomeEntity.setIncomeRecognitionMethodValue(messageSource.getMessage(ACTUALLY, null, Util.locale()));
    }

    if (EXCHANGE.equals(incomeEntity.getIncomeRecognitionMethod())) {
      incomeEntity.setIncomeRecognitionMethodValue(messageSource.getMessage(EXCHANGE, null, Util.locale()));
      incomeEntity.setConversionMethodValue(
          configurationServiceCache.getCategoryData(categoryMap, CONVERSION_METHOD)
              .get(incomeEntity.getConversionMethod()));
    }
  }

  private void setIncomeItemsValue(ApplicationIncomeEntity cmsIncomeEntity, ApplicationIncomeEntity currentIncomeEntity) {
    Map<String, SalaryIncomeEntity> salaryIncomeEntityMap;
    Map<String, RentalIncomeEntity> rentalIncomeEntityMap;
    Map<String, IndividualEnterpriseIncomeEntity> businessIncomeEntityMap;
    Map<String, OtherIncomeEntity> otherIncomeEntityMap;
    Map<String, PropertyBusinessIncomeEntity> propertyIncomeEntityMap;

    if (currentIncomeEntity != null) {
      salaryIncomeEntityMap = currentIncomeEntity.getSalaryIncomes()
          .stream().filter(salaryIncome -> StringUtils.isNotBlank(salaryIncome.getLdpSalaryId()))
          .collect(Collectors.toMap(SalaryIncomeEntity::getLdpSalaryId, Function.identity()));

      rentalIncomeEntityMap = currentIncomeEntity.getRentalIncomes()
          .stream().filter(rentalIncome -> StringUtils.isNotBlank(rentalIncome.getLdpRentalId()))
          .collect(Collectors.toMap(RentalIncomeEntity::getLdpRentalId, Function.identity()));

      businessIncomeEntityMap = currentIncomeEntity.getIndividualEnterpriseIncomes()
          .stream()
          .filter(salaryIncome -> StringUtils.isNotBlank(salaryIncome.getLdpBusinessId()))
          .collect(Collectors.toMap(IndividualEnterpriseIncomeEntity::getLdpBusinessId,
              Function.identity()));

      otherIncomeEntityMap = currentIncomeEntity.getOtherIncomes()
          .stream().filter(salaryIncome -> StringUtils.isNotBlank(salaryIncome.getLdpOtherId()))
          .collect(Collectors.toMap(OtherIncomeEntity::getLdpOtherId, Function.identity()));

      propertyIncomeEntityMap = currentIncomeEntity.getPropertyBusinessIncomes()
          .stream().filter(propertyIncome -> StringUtils.isNotBlank(propertyIncome.getLdpPropertyBusinessId()))
          .collect(Collectors.toMap(PropertyBusinessIncomeEntity::getLdpPropertyBusinessId, Function.identity()));
    } else {
      otherIncomeEntityMap = new HashMap<>();
      rentalIncomeEntityMap = new HashMap<>();
      businessIncomeEntityMap = new HashMap<>();
      salaryIncomeEntityMap = new HashMap<>();
      propertyIncomeEntityMap = new HashMap<>();
    }

    // Set value cho nguon thu tu luong
    setSalaryIncomeValue(cmsIncomeEntity, salaryIncomeEntityMap);

    // Set value cho nguon thu cho thue
    setRentalIncomeValue(cmsIncomeEntity, rentalIncomeEntityMap);

    // Set value cho nguồn thu cá nhân kinh doanh hoặc doan nghiệp
    setBusinessIncomeValue(cmsIncomeEntity, businessIncomeEntityMap);

    // Set value cho nguồn thu khác
    setOtherIncomeValue(cmsIncomeEntity, otherIncomeEntityMap);

    // Set value cho nguồn thu BDS gom xây
    setPropertyBusinessIncome(cmsIncomeEntity, propertyIncomeEntityMap);

    cmsIncomeEntity.setId(currentIncomeEntity != null ? currentIncomeEntity.getId() : null);
  }

  private void setCreditValue(ApplicationCreditEntity cmsCreditEntity, ApplicationCreditEntity currentCreditEntity) {
    if (currentCreditEntity == null) {
      cmsCreditEntity.setApproveResult(ApprovalResult.Y.name());
    } else {
      cmsCreditEntity.setId(currentCreditEntity.getId());
      cmsCreditEntity.setApproveResult(currentCreditEntity.getApproveResult());
      cmsCreditEntity.setApproveResultValue(currentCreditEntity.getApproveResultValue());
    }

    switch (cmsCreditEntity.getCreditType()) {
      case LOAN:
        setCreditLoan(cmsCreditEntity.getCreditLoan(), currentCreditEntity);
        break;
      case OVERDRAFT:
        setCreditOverdraft(cmsCreditEntity.getCreditOverdraft(), currentCreditEntity);
        break;
      case CARD:
        setCreditCardValue(cmsCreditEntity);
        setCreditCard(cmsCreditEntity.getCreditCard(), currentCreditEntity);
        break;
      default:
        break;
    }
  }

  public void setCreditLoan(ApplicationCreditLoanEntity creditLoanEntity, ApplicationCreditEntity currentCreditEntity) {
    if (currentCreditEntity != null && currentCreditEntity.getCreditLoan() != null) {
      creditLoanEntity.setId(currentCreditEntity.getCreditLoan().getId());
    }
    creditLoanEntity.setLoanPurposeValue(configurationServiceCache.getCategoryData(categoryMap, LOAN_PURPOSE).get(creditLoanEntity.getLoanPurpose()));
    creditLoanEntity.setCreditFormValue(configurationServiceCache.getCategoryData(categoryMap, CREDIT_FORM).get(creditLoanEntity.getCreditForm()));
  }

  public void setCreditOverdraft(ApplicationCreditOverdraftEntity creditOverdraftEntity, ApplicationCreditEntity currentCreditEntity) {
    if (currentCreditEntity != null && currentCreditEntity.getCreditOverdraft() != null) {
      creditOverdraftEntity.setId(currentCreditEntity.getCreditOverdraft().getId());
    }
    creditOverdraftEntity.setLoanPurposeValue(configurationServiceCache.getCategoryData(categoryMap, LOAN_PURPOSE).get(creditOverdraftEntity.getLoanPurpose()));
  }

  public void setCreditCard(ApplicationCreditCardEntity creditCardEntity, ApplicationCreditEntity currentCreditEntity) {
    if (currentCreditEntity != null && currentCreditEntity.getCreditCard() != null) {
      creditCardEntity.setId(currentCreditEntity.getCreditCard().getId());
    }

    if (CollectionUtils.isEmpty(creditCardEntity.getSubCreditCards())) {
      creditCardEntity.setSubCreditCards(Collections.emptySet());
    }

    if (currentCreditEntity != null) {
      Map<String, Long> mapSubCardIds = currentCreditEntity.getCreditCard().getSubCreditCards().stream()
          .collect(Collectors.toMap(SubCreditCardEntity::getLdpSubId, SubCreditCardEntity::getId));
      for (SubCreditCardEntity subCreditCard : creditCardEntity.getSubCreditCards()) {
        if (mapSubCardIds.containsKey(subCreditCard.getLdpSubId())) {
          subCreditCard.setId(mapSubCardIds.get(subCreditCard.getLdpSubId()));
        }
      }
    }
  }

  private void setCreditCardValue(ApplicationCreditEntity cmsCreditEntity) {
    cmsCreditEntity.getCreditCard().setCardTypeValue(StringUtils.isNotBlank(cmsCreditEntity.getCreditCard().getCardType())
        ? configurationServiceCache.getCategoryData(categoryMap, CARD_TYPE).get(cmsCreditEntity.getCreditCard().getCardType())
        : null);
    cmsCreditEntity.getCreditCard().setAutoDeductRateValue(StringUtils.isNotBlank(cmsCreditEntity.getCreditCard().getAutoDeductRate())
        ? configurationServiceCache.getCategoryData(categoryMap, AUTO_DEDUCT_RATE).get(cmsCreditEntity.getCreditCard().getAutoDeductRate())
        : null);
    cmsCreditEntity.getCreditCard().setCardFormValue(StringUtils.isNotBlank(cmsCreditEntity.getCreditCard().getCardForm())
        ? configurationServiceCache.getCategoryData(categoryMap, CARD_FORM).get(cmsCreditEntity.getCreditCard().getCardForm())
        : null);
    cmsCreditEntity.getCreditCard().setCardReceiveAddressValue(StringUtils.isNotBlank(cmsCreditEntity.getCreditCard().getCardReceiveAddress())
        ? configurationServiceCache.getCategoryData(categoryMap, CARD_RECEIVE_ADDRESS).get(cmsCreditEntity.getCreditCard().getCardReceiveAddress())
        : null);
    cmsCreditEntity.getCreditCard().setCityValue(StringUtils.isNotBlank(cmsCreditEntity.getCreditCard().getCityCode())
        ? getNamePlaceByCodeFromCacheMercury(city, cmsCreditEntity.getCreditCard().getCityCode())
        : null);
    cmsCreditEntity.getCreditCard().setDistrictValue(StringUtils.isNotBlank(cmsCreditEntity.getCreditCard().getDistrictCode())
        ? getDistrictValue(cmsCreditEntity.getCreditCard().getCityCode(), cmsCreditEntity.getCreditCard().getDistrictCode())
        : null);
    cmsCreditEntity.getCreditCard().setWardValue(StringUtils.isNotBlank(cmsCreditEntity.getCreditCard().getWardCode())
        ? getWardValue(cmsCreditEntity.getCreditCard().getDistrictCode(), cmsCreditEntity.getCreditCard().getWardCode())
        : null);
  }

  private void setSalaryIncomeValue(ApplicationIncomeEntity cmsIncomeEntity, Map<String, SalaryIncomeEntity> salaryIncomeEntityMap) {
    // Set lại ID cho nguồn thu từ lương
    cmsIncomeEntity.getSalaryIncomes().forEach(incomeItem -> {
      if (salaryIncomeEntityMap.containsKey(incomeItem.getLdpSalaryId())) {
        incomeItem.setId(salaryIncomeEntityMap.get(incomeItem.getLdpSalaryId()).getId());
      }
      incomeItem.setIncomeTypeValue(configurationServiceCache.getCategoryData(categoryMap, INCOME_TYPE).get(incomeItem.getIncomeType()));
      incomeItem.setIncomeOwnerValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(incomeItem.getIncomeOwner()));
      incomeItem.setPayTypeValue(configurationServiceCache.getCategoryData(categoryMap, PAY_TYPE).get(incomeItem.getPayType()));
      incomeItem.setLaborTypeValue(StringUtils.isNotBlank(incomeItem.getLaborType())
          ? configurationServiceCache.getCategoryData(categoryMap, LABOR_TYPE).get(incomeItem.getLaborType())
          : null);
      incomeItem.setCityValue(StringUtils.isNotBlank(incomeItem.getCityCode())
          ? getNamePlaceByCodeFromCacheMercury(city, incomeItem.getCityCode())
          : null);
      incomeItem.setDistrictValue(StringUtils.isNotBlank(incomeItem.getDistrictCode())
          ? getDistrictValue(incomeItem.getCityCode(), incomeItem.getDistrictCode())
          : null);
      incomeItem.setWardValue(StringUtils.isNotBlank(incomeItem.getWardCode())
          ? getWardValue(incomeItem.getDistrictCode(), incomeItem.getWardCode())
          : null);
      incomeItem.setAddressLinkId(SourceApplication.LDP.name() + "-" + incomeItem.getLdpSalaryId());
    });
  }

  private void setRentalIncomeValue(ApplicationIncomeEntity cmsIncomeEntity, Map<String, RentalIncomeEntity> rentalIncomeEntityMap) {
    cmsIncomeEntity.getRentalIncomes().forEach(incomeItem -> {
      if (rentalIncomeEntityMap.containsKey(incomeItem.getLdpRentalId())) {
        incomeItem.setId(rentalIncomeEntityMap.get(incomeItem.getLdpRentalId()).getId());
      }
      incomeItem.setIncomeTypeValue(configurationServiceCache.getCategoryData(categoryMap, INCOME_TYPE).get(incomeItem.getIncomeType()));
      incomeItem.setIncomeOwnerValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(incomeItem.getIncomeOwner()));
      incomeItem.setAssetTypeValue(configurationServiceCache.getCategoryData(categoryMap, ASSET_TYPE).get(incomeItem.getAssetType()));
      incomeItem.setRentalPurposeValue(configurationServiceCache.getCategoryData(categoryMap, RENTAL_PURPOSE).get(incomeItem.getRentalPurpose()));
    });
  }
  private void setBusinessIncomeValue(ApplicationIncomeEntity cmsIncomeEntity, Map<String, IndividualEnterpriseIncomeEntity> businessIncomeEntityMap) {
    // Set lại ID cho nguồn thu cá nhân kinh doanh hoặc doanh nghiệp
    cmsIncomeEntity.getIndividualEnterpriseIncomes().forEach(incomeItem -> {
      if (businessIncomeEntityMap.containsKey(incomeItem.getLdpBusinessId())) {
        incomeItem.setId(businessIncomeEntityMap.get(incomeItem.getLdpBusinessId()).getId());
      }
      incomeItem.setIncomeTypeValue(configurationServiceCache.getCategoryData(categoryMap, INCOME_TYPE).get(incomeItem.getIncomeType()));
      incomeItem.setIncomeOwnerValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(incomeItem.getIncomeOwner()));
      incomeItem.setCityValue(getNamePlaceByCodeFromCacheMercury(city, incomeItem.getCityCode()));
      incomeItem.setDistrictValue(getDistrictValue(incomeItem.getCityCode(), incomeItem.getDistrictCode()));
      incomeItem.setWardValue(getWardValue(incomeItem.getDistrictCode(), incomeItem.getWardCode()));
      incomeItem.setAddressLinkId(SourceApplication.LDP.name() + "-" + incomeItem.getLdpBusinessId());
    });
  }

  private void setOtherIncomeValue(ApplicationIncomeEntity cmsIncomeEntity, Map<String, OtherIncomeEntity> otherIncomeEntityMap) {
    // Set lại ID cho nguồn thu khác
    cmsIncomeEntity.getOtherIncomes().forEach(incomeItem -> {
      if (otherIncomeEntityMap.containsKey(incomeItem.getLdpOtherId())) {
        incomeItem.setId(otherIncomeEntityMap.get(incomeItem.getLdpOtherId()).getId());
      }
      incomeItem.setIncomeTypeValue(configurationServiceCache.getCategoryData(categoryMap, INCOME_TYPE).get(incomeItem.getIncomeType()));
      incomeItem.setIncomeOwnerValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(incomeItem.getIncomeOwner()));
      incomeItem.setIncomeDetailValue(configurationServiceCache.getCategoryData(categoryMap, DETAIL_INCOME_OTHER).get(incomeItem.getIncomeDetail()));
    });
  }

  private void setPropertyBusinessIncome(ApplicationIncomeEntity cmsIncomeEntity, Map<String, PropertyBusinessIncomeEntity> propertyIncomeEntityMap) {
    // Set lại ID cho nguồn thu khác
    cmsIncomeEntity.getPropertyBusinessIncomes().forEach(incomeItem -> {
      if (propertyIncomeEntityMap.containsKey(incomeItem.getLdpPropertyBusinessId())) {
        incomeItem.setId(propertyIncomeEntityMap.get(incomeItem.getLdpPropertyBusinessId()).getId());
      }
      incomeItem.setIncomeTypeValue(configurationServiceCache.getCategoryData(categoryMap, INCOME_TYPE).get(incomeItem.getIncomeType()));
      incomeItem.setIncomeOwnerValue(configurationServiceCache.getCategoryData(categoryMap, RELATIONSHIP).get(incomeItem.getIncomeOwner()));
    });
  }

  private void setCustomerGuaranteeIncome(List<CmsIncomeDTO> applicationIncomes, Map<String, CustomerEntity> customerEntityMap) {
    for (CmsIncomeDTO income : applicationIncomes) {
      for (CmsBaseIncomeItemDTO incomeItem : income.getIncomeItems()) {
        if (customerEntityMap.containsKey(incomeItem.getRefCusId())) {
          CustomerEntity guarantee = customerEntityMap.get(incomeItem.getRefCusId());
          incomeItem.setCustomerId(guarantee.getId());
          incomeItem.setCustomerAdditionalId(guarantee.getRefCustomerId());
          break;
        }
      }
    }
  }

  public void checkPilotScope(String source, CmsBaseResponse response, Map<String, String> mapGroupValue, DataResponse regionAreaResp) {
    log.info("checkPilotScope START with: {}, createdBy: {}", source);
    Boolean isValid = false;
    List<String> items = null;

    if (havingRegionArea(regionAreaResp)) {
      List<String> businessUnitCodeList = regionAreaResp.getBusinessUnitDetails().stream().map(OrganizationTreeDetail::getCode).collect(Collectors.toList());
      Iterator<String> it = businessUnitCodeList.iterator();
      while (it.hasNext() && isValid == false) {
        String businessUnitCode = it.next();
        switch (source) {
          case CJBO_SOURCE:
            items = new ArrayList<>(Arrays.asList(mapGroupValue.get(CJBO_SOURCE).split(",")));
            if (items.contains(businessUnitCode)) {
              isValid = true;
            }
            break;
          case CJMHOME_SOURCE:
            items = new ArrayList<>(Arrays.asList(mapGroupValue.get(CJMHOME_SOURCE).split(",")));
            if (items.contains(businessUnitCode)) {
              isValid = true;
            }
            break;
          case BPM_SOURCE:
            items = new ArrayList<>(Arrays.asList(mapGroupValue.get(BPM_SOURCE).split(",")));
            if (items.contains(businessUnitCode)) {
              isValid = true;
            }
            break;
          default:
            throw new ApprovalException(DomainCode.NOT_BELONG_TO_PILOT_SCOPE, new Object[]{source});
        }
      }
    }

    if (Boolean.FALSE.equals(isValid)) {
      throw new ApprovalException(DomainCode.NOT_BELONG_TO_PILOT_SCOPE, new Object[]{source});
    } else {
      updateNotedForResponse(response);
    }
    log.info("checkPilotScope END with response: {}", response);
  }


  private boolean havingRegionArea(DataResponse regionAreaResp) {
    if (Objects.nonNull(regionAreaResp) && Objects.nonNull(regionAreaResp.getBusinessUnitDetails())) {
      return true;
    }
    return false;
  }

  private Map<String, String> getSourcePilotList() {
    List<String> listCategory = new ArrayList<>();
    listCategory.add(PILOT_SOURCE_MAPPING.getCode());
    GetListResponse listData = configurationListClient.findByListCategoryDataCodes(listCategory, HeaderUtil.getToken());

    return listData.getValue().get(PILOT_SOURCE_MAPPING.getCode()).stream()
        .collect(Collectors.toMap(Detail::getCode, Detail::getValue, (first, second) -> second));
  }

  public void updateNotedForResponse(CmsBaseResponse response) {
    response.getNoted().put(DomainCode.BELONG_TO_PILOT_SCOPE.getCode(), DomainCode.BELONG_TO_PILOT_SCOPE.getMessage());
  }

  public ApplicationEntity checkCmsApplication(String refId, String source) {
   return applicationRepository.findAppByRefIdAndSource(refId, source)
        .orElse(new ArrayList<>())
        .stream()
        .filter(i -> !AS0099.getValue().equals(i.getStatus()))
        .max(Comparator.comparing(ApplicationEntity::getCreatedAt))
        .orElse(null);
  }

  private void verifyStatus(String refId, ApplicationEntity entityApp) {
    if (StringUtils.isNotBlank(entityApp.getStatus()) && AS0099.getValue().equals(entityApp.getStatus())) {
      log.error(
          "Application {} has been closed at Digi-Lending system, please check again...", refId);
      throw new ApprovalException(DomainCode.APPLICATION_CLOSED, new Object[]{refId});
    }
    if (StringUtils.isNotBlank(entityApp.getStatus()) && AS9999.getValue().equals(entityApp.getStatus())) {
      log.error(
          "Application {} has been completed at Digi-Lending system, please check again...",
          refId);
      throw new ApprovalException(DomainCode.APPLICATION_APPROVED,
          new Object[]{refId});
    }
    if (Arrays.asList(LdpStatus.CUSTOMER_EDITED.getValue(),
        LdpStatus.WAIT_CUSTOMER_CONFIRM.getValue()).contains(entityApp.getLdpStatus())) {
      log.error(
          "Application {} is being processed, status {} , ldp status {} , cannot update/add information...",
          refId, entityApp.getStatus(), entityApp.getLdpStatus());
      throw new ApprovalException(DomainCode.UPDATE_APPLICATION_ERROR_BY_LDP_STATUS,
          new Object[]{refId});
    }
  }

  private AssetResponse createOrUpdateAsset(String bpmId, List<AssetInfo> assetInfos) {
    if (CollectionUtils.isEmpty(assetInfos)) {
      return null;
    }

    try {
      PostCmsV2CreateAssetRequest request = new PostCmsV2CreateAssetRequest();
      request.setApplicationId(bpmId);
      request.setAssetInfo(assetInfos);
      return assetClient.cmsCreateAssetData(request);
    } catch (Exception e) {
      log.error("CmsCreateApplicationServiceImpl.createOrUpdateAsset failure , error : ", e);
    }
    return null;
  }

  private void reloadChecklist(String bpmId) {
    try {
      checklistService.reloadChecklist(bpmId, true);
    } catch (Exception e) {
      log.error("CmsCreateApplicationServiceImpl.reloadChecklist failure , error : ", e);
    }
  }

  public void createFirstHistory(ApplicationEntity applicationEntity) {
    ApplicationHistoryApprovalEntity historyApprovalEntity = new ApplicationHistoryApprovalEntity();
    historyApprovalEntity.setStatus(applicationEntity.getStatus());
    historyApprovalEntity.setApplication(applicationEntity);
    historyApprovalEntity.setUserRole(ProcessingRole.valueOf(applicationEntity.getProcessingRole()));
    historyApprovalEntity.setUsername(applicationEntity.getAssignee());
    historyApprovalEntity.setFullName(applicationEntity.getCreatedFullName());
    historyApprovalEntity.setStepDescription(applicationEntity.getProcessingStep());
    historyApprovalEntity.setExecutedAt(LocalDateTime.now());
    applicationEntity.getHistoryApprovals().add(historyApprovalEntity);
  }

  public void saveOrUpdateFieldInformation(ApplicationEntity applicationEntity, PostCmsV2CreateApplicationRequest request) {
    Set<ApplicationFieldInformationEntity> fieldInformationEntities = buildFieldInformation(applicationEntity, request);

    Set<ApplicationFieldInformationEntity> addFieldEntities = fieldInformationEntities
        .stream()
        .filter(item -> Objects.isNull(item.getId()))
        .collect(Collectors.toSet());

    Set<ApplicationFieldInformationEntity> updateFieldEntities = fieldInformationEntities
        .stream()
        .filter(item -> !Objects.isNull(item.getId()))
        .collect(Collectors.toSet());

    applicationEntity.updateFieldInformationEntities(updateFieldEntities);

    applicationEntity.addFieldInformationEntities(addFieldEntities);
  }

  public Set<ApplicationFieldInformationEntity> buildFieldInformation(
      ApplicationEntity applicationEntity,
      PostCmsV2CreateApplicationRequest request) {
    Set<ApplicationFieldInformationEntity> responses = new HashSet<>();

    String executor = request.getApplication().getCreatedBy();

    Map<String, Long> addressLinkIdMap = applicationEntity.getFieldInformations()
        .stream()
        .collect(Collectors.toMap(ApplicationFieldInformationEntity::getAddressLinkId,
            ApplicationFieldInformationEntity::getId));

    // Map dia chi khach hang -> thuc dia DVKD
    if (CollectionUtils.isNotEmpty(request.getCustomer().getAddresses())) {
      request.getCustomer().getAddresses().forEach(item -> {
        ApplicationFieldInformationEntity entity = ApplicationFieldInformationMapper.INSTANCE.v2CmsAddressToFieldInformation(
            "V017", executor, item);
        entity.setAddressLinkId(SourceApplication.LDP.name() + "-" + item.getLdpAddressId());
        entity.setId(addressLinkIdMap.get(entity.getAddressLinkId()));
        responses.add(entity);
      });
    }

    // Map dia chi nguoi lien quan -> thuc dia DVKD
    if (CollectionUtils.isNotEmpty(request.getCustomerRelations())) {
      request.getCustomerRelations().forEach(relation -> {
        if (CollectionUtils.isNotEmpty(relation.getAddresses())) {
          relation.getAddresses().forEach(item -> {
            ApplicationFieldInformationEntity entity = ApplicationFieldInformationMapper.INSTANCE.v2CmsAddressToFieldInformation(
                relation.getRelationship(), executor, item);
            entity.setAddressLinkId(SourceApplication.LDP.name() + "-" + item.getLdpAddressId());
            entity.setId(addressLinkIdMap.get(entity.getAddressLinkId()));
            responses.add(entity);
          });
        }
      });
    }

    // Map dia chi nguon thu -> thuc dia DVKD
    if (CollectionUtils.isNotEmpty(request.getApplicationIncomes())) {
      request.getApplicationIncomes().forEach(income -> income.getIncomeItems().forEach(item -> {
        ApplicationFieldInformationEntity entity = new ApplicationFieldInformationEntity();
        switch (item.getIncomeType()) {
          case SALARY:
            entity = ApplicationFieldInformationMapper.INSTANCE.v2CmsSalaryAddressToFieldInformation(
                item.getIncomeOwner(), executor, (CmsSalaryDTO) item);
            responses.add(entity);
            break;
          case INDIVIDUAL_BUSINESS:
            entity = ApplicationFieldInformationMapper.INSTANCE.v2CmsIndividualBusinessAddressToFieldInformation(
                item.getIncomeOwner(), executor, (CmsIndividualBusinessDTO) item);
            responses.add(entity);
            break;
          case ENTERPRISE_BUSINESS:
            entity = ApplicationFieldInformationMapper.INSTANCE.v2CmsEnterpriseBusinessAddressToFieldInformation(
                item.getIncomeOwner(), executor, (CmsEnterpriseBusinessDTO) item);
            responses.add(entity);
            break;
          default:
            break;
        }
        entity.setAddressLinkId(SourceApplication.LDP.name() + "-" + item.getRefIncomeItemId());
        entity.setId(addressLinkIdMap.get(entity.getAddressLinkId()));
      }));
    }

    return responses;
  }

  public void validateStateDraftData(ApplicationEntity entityApp, AssetResponse assetResponse) {
    GetApplicationDTO summaryApplication = getApplicationInfo(entityApp);

    Set<ApplicationDraftEntity> draftEntities = applicationDraftRepository.findByBpmId(entityApp.getBpmId())
        .orElse(null);

    if (CollectionUtils.isNotEmpty(draftEntities)) {
      for (ApplicationDraftEntity draftEntity : draftEntities) {
        if (draftEntity.getData() == null) {
          continue;
        }
        switch (draftEntity.getTabCode()) {
          case INITIALIZE_INFO:
            checkInitializeInfo(draftEntity, summaryApplication.getInitializeInfo());
            break;
          case FIELD_INFO:
            checkFieldInfo(draftEntity, summaryApplication.getFieldInfor());
            break;
          case DEBT_INFO:
            checkDebtInfo(draftEntity, summaryApplication.getDebtInfo());
            break;
          case ASSET_INFO:
            checkAssetInfo(draftEntity, assetResponse);
            draftEntity.setBpmId(entityApp.getBpmId());
            break;
          default:
            break;
        }
      }

      applicationDraftRepository.saveAll(draftEntities);
    }
  }

  public void checkInitializeInfo(ApplicationDraftEntity draftEntity, InitializeInfoDTO initializeInfo) {
    initializeInfo.setType(POST_INITIALIZE_INFO);
    draftEntity.setData(JsonUtil.convertObject2Bytes(initializeInfo, objectMapper));

    if (FINISHED == draftEntity.getStatus()) {
      PostInitializeInfoRequest request = EntityDraftMapper.INSTANCE.dtoToPostInitializeInfoRequest(initializeInfo);
      draftEntity.setStatus(getStateDraft(request));
    }
  }

  public void checkFieldInfo(ApplicationDraftEntity draftEntity, FieldInforDTO fieldInfo) {
    fieldInfo.setType(POST_FIELD_INFO);
    draftEntity.setData(JsonUtil.convertObject2Bytes(fieldInfo, objectMapper));

    if (FINISHED == draftEntity.getStatus()) {
      PostFieldInformationRequest request = EntityDraftMapper.INSTANCE.dtoToPostFieldInformationRequest(fieldInfo);
      draftEntity.setStatus(getStateDraft(request));
    }
  }

  public void checkDebtInfo(ApplicationDraftEntity draftEntity, DebtInfoDTO debtInfo) {
    debtInfo.setType(POST_DEBT_INFO);
    draftEntity.setData(JsonUtil.convertObject2Bytes(debtInfo, objectMapper));

    if (FINISHED == draftEntity.getStatus()) {
      PostDebtInfoRequest request = EntityDraftMapper.INSTANCE.dtoToPostDebtInfoRequest(debtInfo);
      draftEntity.setStatus(getStateDraft(request));
    }
  }

  private Integer getStateDraft(PostBaseRequest request) {
    Set<ConstraintViolation<Object>> violations = validator.validate(request);
    return CollectionUtils.isNotEmpty(violations) ? UNFINISHED : FINISHED;
  }

  public void updateAssetInfo2Draft(ApplicationEntity entityApp, AssetResponse assetResponse) {
    Set<ApplicationDraftEntity> draftEntities = applicationDraftRepository.findByBpmId(entityApp.getBpmId())
            .orElse(null);

    if (CollectionUtils.isNotEmpty(draftEntities)) {
      for (ApplicationDraftEntity draftEntity : draftEntities) {
        if(ASSET_INFO.equals(draftEntity.getTabCode())) {
          draftEntity.setBpmId(entityApp.getBpmId());
          checkAssetInfo(draftEntity, assetResponse);
          applicationDraftRepository.save(draftEntity);
          break;
        }
      }
    }
  }

  private void checkAssetInfo(ApplicationDraftEntity draftEntity, AssetResponse assetResponse) {
    draftEntity.setTabCode(ASSET_INFO);
    draftEntity.setStatus(UNFINISHED);
    if(ObjectUtils.isNotEmpty(assetResponse)) {
      AssetCommonInfoDTO assetInfo = objectMapper.convertValue(assetResponse.getData(), AssetCommonInfoDTO.class);
      assetInfo.setType(POST_ASSET_INFO);
      draftEntity.setData(JsonUtil.convertObject2Bytes(assetInfo, objectMapper));
    }
  }

  public void checkCustomerRelationship(ApplicationEntity entity) {
    log.info("START CmsCreateApplicationServiceImpl.checkCustomerRelationship with bpmId {}", entity.getBpmId());
    // Lấy người liên quan theo customerType = RB
    Set<CustomerRelationShipEntity> relationShipEntities =
      entity.getCustomer().getCustomerRelationShips()
        .stream()
        .filter(c -> CustomerType.RB.name().equalsIgnoreCase(
            Objects.requireNonNull(customerRepository.findById(c.getCustomerRefId()).orElse(null)).getCustomerType()))
        .collect(Collectors.toSet());
    relationShipEntities.forEach(crs -> {
      Long relationRefId = null;
      // Gọi sang common lấy thông tin mối quan hệ người liên quan
      CusRelationForSearchResponse response = getCustomerRelationsFromCommon(crs.getCustomer().getRefCustomerId(),
          customerRepository.searchRefCustomerIdById(crs.getCustomerRefId()).orElse(null));
      if (ObjectUtils.isNotEmpty(response) || CollectionUtils.isNotEmpty(response.getRelationships())) {
        // Kiểm tra giá trị mối quan hệ từ LDP gửi sang với mối quan hệ trên common
         relationRefId = checkRelationsBetweenLDPAndCommon(crs, response);
      }
      crs.setRelationshipRefId(relationRefId);
    });
    log.info("END CmsCreateApplicationServiceImpl.checkCustomerRelationship");
  }

  private CusRelationForSearchResponse getCustomerRelationsFromCommon(Long customerId, Long relatedCustomerId) {
      // Build request
      SearchCusRelationshipRequest request = SearchCusRelationshipRequest.builder()
          .customerId(customerId)
          .relatedCustomerId(relatedCustomerId)
          .build();
      // Call API search customer relationships
      CustomerBaseResponse<CusRelationForSearchResponse> response = customerClient.searchCustomerRelationship(request);
      if (ObjectUtils.isNotEmpty(response) || ObjectUtils.isNotEmpty(response.getData())) {
        return response.getData();
      }
      return null;
  }
  private Long checkRelationsBetweenLDPAndCommon(CustomerRelationShipEntity customerRelationShip,
      CusRelationForSearchResponse response) {
    Long refCustomerId = customerRepository.searchRefCustomerIdById(customerRelationShip.getCustomerRefId()).orElse(null);
    if (ObjectUtils.isEmpty(refCustomerId)) {
      return null;
    }
    // Lấy mối quan hệ theo KH và NLQ
    Relationship relationship = response.getRelationships().stream()
        .filter(r -> refCustomerId.equals(r.getRelatedCustomerId()))
        .findFirst()
        .orElse(null);
    if (ObjectUtils.isEmpty(relationship) || CollectionUtils.isEmpty(relationship.getRelationshipDetails())) {
      // Tạo mới mqh vào common
      CustomerBaseResponse<CustomerRelationResponse> createResponse =
          customerClient.createCustomerRelationship(buildRelationshipRequest(customerRelationShip, null, false, refCustomerId));
      return getRelationshipRefId(createResponse, customerRelationShip.getRelationship(), refCustomerId);
    }
    // Lấy mối quan hệ theo relationType
    CusRelationForSearchResponse.RelationshipDetail relationshipDetail =
        getDetailByRelatedType(relationship, customerRelationShip.getRelationship());
    if (ObjectUtils.isNotEmpty(relationshipDetail) &&
        relationshipDetail.getRelatedDetail().equalsIgnoreCase(customerRelationShip.getRelationship())) {
      return relationshipDetail.getId();
    }
    String relationshipGroup = getRelationGroup(customerRelationShip.getRelationship());
    // TH tồn tại mối quan hệ:
    // Giá trị relatedType giống nhau
    // Giá trị mqh relatedDetail khác nhau
    CustomerBaseResponse<CustomerRelationResponse> updateRes =
        customerClient.updateCustomerRelationship(buildRequestForUpdateRelation(customerRelationShip.getCustomer().getRefCustomerId(),
            refCustomerId, customerRelationShip.getRelationship(), relationship.getRelationshipDetails(), relationshipGroup));
    return getRelationshipRefId(updateRes, customerRelationShip.getRelationship(), refCustomerId);
  }

  private AddOrUpdateCusRelationshipRequest buildRequestForUpdateRelation(
      Long customerId, Long relatedCustomerId, String relationship,
      List<CusRelationForSearchResponse.RelationshipDetail> relationshipDetails, String relationshipGroup) {

    List<RelationshipDetail> relationshipDetailList = new ArrayList<>();
    RelationshipDetail detail = RelationshipDetail.builder()
        .relatedDetail(relationship)
        .build();
    relationshipDetailList.add(detail);
    if (CollectionUtils.isNotEmpty(relationshipDetails)) {
      relationshipDetails.forEach(r -> {
        if (!relationshipGroup.equalsIgnoreCase(r.getRelationshipType())) {
          RelationshipDetail rd = new RelationshipDetail();
          rd.setId(r.getId());
          rd.setRelatedDetail(r.getRelatedDetail());
          relationshipDetailList.add(rd);
        }
      });
    }
    return AddOrUpdateCusRelationshipRequest.builder()
        .customerId(customerId)
        .relatedCustomerId(relatedCustomerId)
        .relationships(relationshipDetailList)
        .build();
  }


  private CusRelationForSearchResponse.RelationshipDetail getDetailByRelatedType(
      Relationship relationship, String relationshipDetail) {
    // Lấy loại mối quan hệ theo relation_detail
    String relationGroup = getRelationGroup(relationshipDetail);
    if (StringUtils.isEmpty(relationGroup)) {
      throw new ApprovalException(NOT_FOUND_RELATION_GROUP, new Object[]{relationshipDetail});
    }
    return relationship.getRelationshipDetails().stream()
        .filter(r -> relationGroup.equalsIgnoreCase(r.getRelationshipType()))
        .findFirst()
        .orElse(null);
  }

  private String getRelationGroup(String relationshipDetail) {
    log.info("START getRelationGroup with relationshipDetail: [{}]", relationshipDetail);
    GetCategoryConditionRequest request = GetCategoryConditionRequest.builder()
        .categoryCode(RELATIONSHIP.getCode())
        .categoryDataCode(Collections.singletonList(relationshipDetail))
        .build();
    ConfigurationBaseResponse<CategoryConditionResponse> categories = configurationListClient.findCategoryCondition(request);
    if (ObjectUtils.isEmpty(categories) || ObjectUtils.isEmpty(categories.getData()) ||
        CollectionUtils.isEmpty(categories.getData().getCategory())) {
      return null;
    }
    CategoryResp categoryResp = categories.getData().getCategory().stream()
        .filter(c -> relationshipDetail.equals(c.getCategoryDataCode()))
        .filter(c -> RELATIONSHIP.getCode().equals(c.getCategoryCode()))
        .findFirst().orElse(null);
    if (ObjectUtils.isEmpty(categoryResp) || CollectionUtils.isEmpty(categoryResp.getConditionData())) {
      return null;
    }
    ConditionDataResponse conditionDataResponse = categoryResp.getConditionData().stream()
        .filter(c -> "RELATIONSHIP_GROUP".equals(c.getConditionCategoryCode()))
        .findFirst()
        .orElse(null);
    if (ObjectUtils.isEmpty(conditionDataResponse)) {
      return null;
    }
    log.info("END getRelationGroup with category condition [{}]",
        conditionDataResponse.getConditionCategoryDataCode());
    return conditionDataResponse.getConditionCategoryDataCode();
  }

  private Long getRelationshipRefId(CustomerBaseResponse<CustomerRelationResponse> response,
      String relationshipFromLdp, Long refCustomerId) {
    if (ObjectUtils.isEmpty(response)) {
      return null;
    }
    CustomerRelationResponse customerRelationResponse = response.getData();
    RelationshipInfo relationship = customerRelationResponse.getRelationships().stream()
        .filter(r -> refCustomerId.equals(r.getRelatedCustomerId()))
        .findFirst()
        .orElse(null);
    if (ObjectUtils.isEmpty(relationship) || CollectionUtils.isEmpty(relationship.getRelationshipDetails())) {
      return null;
    }
    RelationshipDetailInfo relationshipDetailInfo = relationship.getRelationshipDetails()
        .stream()
        .filter(rd -> relationshipFromLdp.equalsIgnoreCase(rd.getRelatedDetail()))
        .findFirst()
        .orElse(null);
    if (ObjectUtils.isEmpty(relationshipDetailInfo)) {
      return null;
    }
    return relationshipDetailInfo.getId();
  }

  private AddOrUpdateCusRelationshipRequest buildRelationshipRequest(CustomerRelationShipEntity customerRelationShip,
      CusRelationForSearchResponse.RelationshipDetail relationshipDetailResponse, boolean isUpdate, Long refCustomerId) {
    List<RelationshipDetail> relationships = new ArrayList<>();
    RelationshipDetail relationship = RelationshipDetail.builder()
        .relatedDetail(customerRelationShip.getRelationship())
        .build();
    if (isUpdate) {
      relationship.setId(relationshipDetailResponse.getId());
    }
    relationships.add(relationship);

    return AddOrUpdateCusRelationshipRequest.builder()
        .customerId(customerRelationShip.getCustomer().getRefCustomerId())
        .relatedCustomerId(refCustomerId)
        .relationships(relationships)
        .build();
  }
}
