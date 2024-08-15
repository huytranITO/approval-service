package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CUSTOMER_RELATIONS_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.CUSTOMER_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ENTERPRISE_RELATIONS_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.MetaDataKey.APPLICATION_BPM_IDS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.ASSIGN_APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.GET_OPR_DETAIL;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.GET_PREVIEW_FORM;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSIGN_TO_TL;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CLOSE_APP;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREATE_APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREDIT_RATING;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREDIT_RATING_V2;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_QUERY_APPLICATION_BY_CUSTOMER;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_QUERY_CIC_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_REWORK_APPLICATION;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SUBMIT_APP;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SYNC_AML_OPR_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SYNC_OPR_ASSET;
import static com.msb.bpm.approval.appr.enums.camunda.CamundaAction.PD_APP_ALLOCATE;
import static com.msb.bpm.approval.appr.enums.camunda.CamundaAction.PD_APP_REASSIGN;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.TEAM_LEAD_ASSIGN_APPROVER;
import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.TEAM_LEAD_ASSIGN_CONTACT;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;

import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.factory.BaseServiceFactory;
import com.msb.bpm.approval.appr.factory.cbt.IncomeFactoryInterface;
import com.msb.bpm.approval.appr.kafka.BeanProducer;
import com.msb.bpm.approval.appr.kafka.producer.CmsInfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.CmsV2InfoProducerStrategy;
import com.msb.bpm.approval.appr.kafka.producer.GeneralInfoProducerStrategy;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.GetApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.InitializeInfoDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityDetailDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.model.request.cbt.SearchByCustomerRelations;
import com.msb.bpm.approval.appr.model.request.collateral.AssetInfo;
import com.msb.bpm.approval.appr.model.request.collateral.GetAssetInfoRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostAssignRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCloseApplicationRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostReworkApplicationRequest;
import com.msb.bpm.approval.appr.model.request.oprisk.OpRiskRequest;
import com.msb.bpm.approval.appr.model.request.organization.GetOrganizationRequest;
import com.msb.bpm.approval.appr.model.request.organization.GetOrganizationRequest.Filter;
import com.msb.bpm.approval.appr.model.request.query.PostQueryApplicationRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.model.response.advancedFunctionality.ApplicationPushKafkaResponse;
import com.msb.bpm.approval.appr.model.response.asset.AssetInfoResponse;
import com.msb.bpm.approval.appr.model.response.cbt.SearchByRelationsResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetOrganizationResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetOrganizationResponse.Organization;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.application.ApplicationService;
import com.msb.bpm.approval.appr.service.authority.reception.AuthorityReceptionService;
import com.msb.bpm.approval.appr.service.common.CopyApplicationService;
import com.msb.bpm.approval.appr.service.intergated.BpmOperationService;
import com.msb.bpm.approval.appr.service.intergated.EsbService;
import com.msb.bpm.approval.appr.service.intergated.IntegrationWay4Service;
import com.msb.bpm.approval.appr.service.intergated.T24Service;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.util.CommonUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl extends AbstractBaseService implements ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final BaseServiceFactory factory;
  private final AuthorityReceptionService authorityReceptionService;
  private final BpmOperationService bpmOperationService;

  private final IntegrationWay4Service integrationWay4Service;
  private final VerifyService verifyService;
  private final EsbService esbService;
  private final T24Service t24Service;
  private final ApplicationConfig applicationConfig;

  private final CopyApplicationService copyApplicationService;
  private final CollateralClient collateralClient;
  private final UserManagerClient userManagerClient;

  private final GeneralInfoProducerStrategy generalInfoProducerStrategy;
  private final CmsInfoProducerStrategy cmsInfoProducerStrategy;
  private final CmsV2InfoProducerStrategy cmsV2InfoProducerStrategy;
  private final ApiRespFactory apiRespFactory;

  /**
   * Khởi tạo hồ sơ
   *
   * @param request   PostInitApplicationRequest
   * @return          GetApplicationDTO
   */
  @Override
  @Transactional
  public Object postCreateApplication(PostCreateApplicationRequest request) {
    ApplicationEntity applicationEntity = (ApplicationEntity) factory.getBaseRequest(
            POST_CREATE_APPLICATION).execute(request);

    Map<String, Object> customerData = buildCustomerAndRelationData(applicationEntity.getCustomer());

    return new GetApplicationDTO()
            .withInitializeInfo(new InitializeInfoDTO()
                    .withApplication(ApplicationMapper.INSTANCE.toDTO(applicationEntity))
                    .withCustomerAndRelationPerson(new CustomerAndRelationPersonDTO()
                            .withCustomer((CustomerDTO) customerData.get(CUSTOMER_TAG))
                            .withCustomerRelations(
                                    (Set<CustomerDTO>) customerData.get(CUSTOMER_RELATIONS_TAG))));
  }

  /**
   * Lấy thông tin hồ sơ theo bpmId
   *
   * @param bpmId   String
   * @return        GetApplicationDTO
   */
  @Override
  @Transactional(readOnly = true)
  public Object getApplication(String bpmId) {
    ApplicationEntity application = applicationRepository.findByBpmId(bpmId)
            .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));
    application.setStatus(CommonUtil.getStatus(application));
    return getApplicationInfo(application);
  }

  @Override
  @Transactional(readOnly = true)
  public Object searchIdAndVersionList(String bpmId) {
    ApplicationEntity application = applicationRepository.findByBpmId(bpmId)
            .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));
    application.setStatus(CommonUtil.getStatus(application));
    return getCusIdAndVersionList(application);
  }

  /**
   * Lưu thông tin hồ sơ
   * Tab thông tin hồ sơ            PostInitializeInfoRequest
   * Tab thông tin thực địa         PostFieldInformationRequest
   * Tab thông tin khoản vay        PostDebtInfoRequest
   * Tab thông tin danh mục hồ sơ
   *
   * @param bpmId     String
   * @param request   PostBaseRequest
   * @return          Object
   */
  @Override
  public Object postSaveData(String bpmId, PostBaseRequest request) {
    validateRequestType(request.getType(), Util.getSaveDataAcceptedRequestType());
    return factory.getBaseRequest(request.getType()).execute(request, bpmId);
  }

  /**
   * Lấy lịch sử trình hồ sơ KH/NHP theo Customer
   *
   * @param request   PostQueryApplicationRequest
   * @return          QueryApplicationByCusResponse
   */
  @Override
  @Transactional(readOnly = true)
  public Object postQueryApplicationByCustomer(PostQueryApplicationRequest request) {
    return factory.getBaseRequest(POST_QUERY_APPLICATION_BY_CUSTOMER).execute(request);
  }

  /**
   * Chuyển hồ sơ tới CBXL Trạng thái hồ sơ được phép điều chuyển: 0002, 0004
   *
   * @param request PostAssignRequest
   */
  @Override
  public void postApplicationHandlingOfficer(PostAssignRequest request) {
    factory.getBaseRequest(ASSIGN_APPLICATION).execute(request, PD_APP_REASSIGN, TEAM_LEAD_ASSIGN_APPROVER);
  }

  /**
   * Chuyển hồ sơ lên CBĐP Trạng thái hồ sơ được phép điều chuyển: 0002
   *
   * @param request PostAssignRequest
   */
  @Override
  public void postApplicationCoordinator(PostAssignRequest request) {
    factory.getBaseRequest(ASSIGN_APPLICATION).execute(request, PD_APP_ALLOCATE, TEAM_LEAD_ASSIGN_CONTACT);
  }

  /**
   * Trả hồ sơ
   *
   * @param bpmId     String
   * @param request   PostReworkApplicationRequest
   */
  @Override
  public void postReworkApplication(String bpmId, PostReworkApplicationRequest request) {
    factory.getBaseRequest(POST_REWORK_APPLICATION).execute(request, bpmId);
  }

  /**
   * Đệ trình/hoàn thành hồ sơ
   *
   * @param bpmId     String
   * @param request   PostSubmitRequest
   */
  @Override
  public ApplicationEntity postSubmitApplication(String bpmId, PostBaseRequest request) {
    ApplicationEntity applicationEntity = (ApplicationEntity) factory.getBaseRequest(POST_SUBMIT_APP).execute(request, bpmId);

    bpmOperationService.syncBpmOperation(applicationEntity, false);

    // card-way4
    try {

      if (applicationEntity.getStatus().equals(ApplicationStatus.AS9999.getValue()))
      {
        log.info("Application status : {}", applicationEntity.getStatus());
        if (!integrationWay4Service.checkDuplicate(applicationEntity.getId(), applicationEntity.getBpmId())) {
          log.info("Application Id: {}", applicationEntity.getId());
          ApplicationHistoricIntegration applicationInfo = integrationWay4Service.init(applicationEntity.getId());
          integrationWay4Service.asyncForCard(applicationInfo);
        }
      }
    } catch (Exception e) {
      log.info("Trigger create card occurred exception: {}", e.getMessage());
    }
    // Call way4
    return applicationEntity;
  }

  /**
   * Chuyển hồ sơ về Team lead
   *
   * @param bpmId     String
   */
  @Override
  public void postCoordinatorAssignToTeamLead(String bpmId) {
    factory.getBaseRequest(POST_ASSIGN_TO_TL).execute(bpmId);
  }

  /**
   * Đóng hồ sơ
   *
   * @param bpmId     String
   * @param request   PostCloseApplicationRequest
   */
  @Override
  public void postCloseApplication(String bpmId, PostCloseApplicationRequest request) {
    factory.getBaseRequest(POST_CLOSE_APP).execute(request, bpmId);
  }

  /**
   * Tra cứu thông tin uy tín tín dụng
   *
   * @param bpmId     String
   * @param request   PostQueryCICRequest
   * @return          CicDTO
   */
  @Override
  @Transactional
  public Object queryCICInfo(String bpmId, PostQueryCICRequest request) {
    return factory.getBaseRequest(POST_QUERY_CIC_INFO).execute(request, bpmId);
  }

  /**
   * Đồng bộ thông tin từ AML|OPR
   *
   * @param bpmId     String
   * @param request   PostSyncAmlOprRequest
   * @return          AmlOprDTO
   */
  @Override
  @Transactional
  public Object syncAmlOprInfo(String bpmId, PostSyncAmlOprRequest request) {
    return factory.getBaseRequest(POST_SYNC_AML_OPR_INFO).execute(request, bpmId);
  }

  /**
   * Đồng bộ thông tin Xếp hạng tín dụng từ CSS
   *
   * @param bpmId     String
   * @param request   PostQueryCreditRatingRequest
   * @return          ApplicationCreditRatingsDTO
   */
  @Override
  public Object syncCreditRatingCSS(String bpmId, PostQueryCreditRatingRequest request) {
    return factory.getBaseRequest(POST_CREDIT_RATING).execute(request, bpmId);
  }

  @Override
  public Object syncCreditRatingCSSV2(String bpmId, PostQueryCreditRatingRequest request) {
    return factory.getBaseRequest(POST_CREDIT_RATING_V2).execute(request, bpmId);
  }

  /**
   * Lấy danh sách cấp thẩm quyền tiếp nhận khi đệ trình hồ sơ
   *
   * @param bpmId   String
   * @return        List<AuthorityDetailDTO>
   */
  @Override
  public List<AuthorityDetailDTO> getAuthoritiesByApplication(String bpmId) {
    return authorityReceptionService.getAuthoritiesByApplication(bpmId);
  }

  /**
   * Generate & preview biểu mẫu
   *
   * @param bpmId   String
   * @return        PreviewFormDTO
   */
  @Override
  public Object getGenerateAndPreviewForms(String bpmId) {
    return factory.getBaseRequest(GET_PREVIEW_FORM).execute(bpmId);
  }

  /**
   * Cập nhật trạng thái generate biên bản phê duyệt/tờ trình
   *
   * @param bpmId               String
   * @param generatorStatus     String
   */
  @Override
  @Transactional
  public void updateGeneratorStatus(String bpmId, String generatorStatus) {
    ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId)
            .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    applicationEntity.setGeneratorStatus(generatorStatus);
  }
  /**
   * Verify đệ trình hồ sơ
   *
   * @param bpmId               String
   */

  @Override
  public Object verifySubmitApplication(String bpmId) {
    return verifyService.verifyLdpStatusWhenSubmit(bpmId);
  }

  @Override
  public Object getAccountInfo(String cifNumber) {
    if (applicationConfig.getAccountSource().isT24Account()) {
      return t24Service.getAccountInfo(cifNumber);
    } else {
      return esbService.getAccountInfo(cifNumber);
    }
  }

  @Override
  public Object copyApplication(String bpmId) {
    return copyApplicationService.copyApplication(bpmId);
  }

  @Override
  @Transactional
  public Object getCicDetail(String bpmId) {
    ApplicationEntity applicationEntity = applicationRepository
            .findByBpmId(bpmId)
            .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));
    Set<CicEntity> cics = applicationEntity.getCics();
    return  buildCicData(cics);
  }

  @Override
  @Transactional
  public Object searchByRelations(SearchByCustomerRelations request) {
    ApplicationEntity application = applicationRepository.findByBpmId(request.getBpmId())
            .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{request.getBpmId()}));
    Set<Long> ids = new HashSet<>();

    Map<String, Object> customerData = buildCustomerDataForCBT(application.getCustomer(), request.getCustomerInfo().getId(), request.getCustomerInfo().getVersion(), ids);
    Map<String, Object> relations = buildRelationDataForCBT(request.getRelations(), request.getEnterpriseInfo(), application, ids);

    Set<ApplicationIncomeDTO> responses = new HashSet<>();
    Set<ApplicationIncomeEntity> applicationIncomes = application.getIncomes();

    if (!ids.isEmpty()) {
      applicationIncomes.forEach(
              income -> {
                responses.add(IncomeFactoryInterface.getApplicationIncome(income.getIncomeRecognitionMethod()).build(income, ids));
              });
    }


    List<AssetInfo> assets = new ArrayList<>();
    AssetInfoResponse assetInfoResponse = null;
    if (request.getAssetInfo() != null) {
      request.getAssetInfo().stream().forEach(asset -> {
        assets.add(new AssetInfo().withAssetId(asset.getId()).withVersion(asset.getVersion()));
      });
      assetInfoResponse = collateralClient.getAssetInfo(new GetAssetInfoRequest().withAssetData(assets));
    }

    responses.removeIf(Objects::isNull);
    return new SearchByRelationsResponse()
            .withCustomerAndRelation(new CustomerAndRelationPersonDTO()
                    .withCustomerRelations(relations != null ? (Set<CustomerDTO>) relations.get(CUSTOMER_TAG): null)
                    .withCustomer((CustomerDTO) customerData.get(CUSTOMER_TAG)))
            .withIncomes(responses)
            .withEnterpriseInfo(relations != null ? (Set<CustomerDTO>) relations.get(ENTERPRISE_RELATIONS_TAG) : null)
            .withAssetInfo(assetInfoResponse != null ? assetInfoResponse.getData() : null)

            ;
  }
  @Override
  public Object syncOpRisk(OpRiskRequest opRiskRequest) {
    return factory.getBaseRequest(POST_SYNC_OPR_ASSET).execute(opRiskRequest);
  }

  @Override
  public Object getOpRiskDetail(String bpmId) {
    return factory.getBaseRequest(GET_OPR_DETAIL).execute(bpmId);
  }

  @Override
  public Object findBranch() {
    log.info("Start findBranch:");
    GetOrganizationResponse response = null;
    try {

      Filter filter = Filter.builder()
          .page(0)
          .pageSize(1000)
          .build();

      GetOrganizationRequest request = GetOrganizationRequest.builder()
          .type("CN")
          .filter(filter)
          .build();

      response = userManagerClient.findOrganization(request);
      if (response == null || response.getData() == null) return null;

      log.info("findBranch with data: {} ", response.getData());
      return response.getData().getOrganizations();
    } catch (Exception e) {
      throw new ApprovalException(DomainCode.FIND_ORGANIZATION_ERROR, new Object[]{e.getMessage()});
    }
  }

  @Override
  @Transactional
  public ApplicationPushKafkaResponse getApplicationPushKafka(String bpmId) {
    ApplicationEntity applicationEntity = applicationRepository
            .findByBpmId(bpmId)
            .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));
    return new ApplicationPushKafkaResponse()
            .withBpmId(applicationEntity.getBpmId())
            .withStatus(CommonUtil.getStatus(applicationEntity))
            .withAssignee(applicationEntity.getAssignee())
            .withPreviousRole(applicationEntity.getPreviousRole())
            .withProcessingRole(applicationEntity.getProcessingRole())
            .withProcessingStepCode(applicationEntity.getProcessingStepCode())
            .withProcessingStep(applicationEntity.getProcessingStep());
  }

  @Override
  @Transactional
  public ResponseEntity<ApiResponse> pushKafkaApplication(String bpmId) {
    applicationRepository.findByBpmId(bpmId).orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    List<String> bpmIds = Collections.singletonList(bpmId);

    ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
    metadata.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(metadata, generalInfoProducerStrategy).execute();

    ConcurrentMap<String, Object> broadcastMetadata = new ConcurrentHashMap<>();
    broadcastMetadata.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(broadcastMetadata, cmsInfoProducerStrategy).execute();

    ConcurrentMap<String, Object> broadcastMetadataV2 = new ConcurrentHashMap<>();
    broadcastMetadataV2.put(APPLICATION_BPM_IDS, bpmIds);
    new BeanProducer(broadcastMetadataV2, cmsV2InfoProducerStrategy).execute();

    return apiRespFactory.success();
  }
}

