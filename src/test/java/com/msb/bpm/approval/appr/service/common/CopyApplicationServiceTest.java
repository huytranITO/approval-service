package com.msb.bpm.approval.appr.service.common;

import static org.hamcrest.Matchers.in;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.asset.AssetClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.creditconditions.CreditConditionClient;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.customer.response.CusRelationForSearchResponse;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.UserManagementClient;
import com.msb.bpm.approval.appr.mapper.ApplicationAppraisalContentMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationFieldInformationMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationLimitCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditLoanDTO;
import com.msb.bpm.approval.appr.model.dto.DebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.GetApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationAppraisalContentEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationContactEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.request.collateral.AssetResponse;
import com.msb.bpm.approval.appr.model.response.asset.AssetInfoResponse;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.collateral.CreditAssetAllocationResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionClientResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV2Response;
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
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;

class CopyApplicationServiceTest {

  private String pathSourceFile = "src/test/resources/copy_application/";

  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private CustomerRelationshipRepository customerRelationshipRepository;
  @Mock
  private CommonService commonService;
  @Mock
  private PostDebtInfoServiceImpl postDebtInfoService;
  @Mock
  private PostCreateApplicationServiceImpl postCreateApplicationService;
  @Mock
  private CmsCreateApplicationServiceImpl cmsCreateApplicationService;
  @Mock
  private AssetClient assetClient;
  @Mock
  private CollateralClient collateralClient;
  @Mock
  private UserManagerClient userManagerClient;
  @Mock
  private UserManagementClient userManagementClient;
  @Mock
  private CreditConditionClient creditConditionClient;
  @Mock
  private ChecklistService checklistService;
  @Mock
  private MessageSource messageSource;
  @Spy
  private ApplicationMapper applicationMapper = Mappers.getMapper(ApplicationMapper.class);
  @Spy
  private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
  @Spy
  private ApplicationIncomeMapper applicationIncomeMapper = Mappers.getMapper(
      ApplicationIncomeMapper.class);
  @Spy
  private ApplicationCreditMapper applicationCreditMapper = Mappers.getMapper(
      ApplicationCreditMapper.class);
  @Spy
  private ApplicationFieldInformationMapper applicationFieldInformationMapper = Mappers.getMapper(
      ApplicationFieldInformationMapper.class);
  @Spy
  private ApplicationAppraisalContentMapper applicationAppraisalContentMapper = Mappers.getMapper(
      ApplicationAppraisalContentMapper.class);
  @Spy
  private ApplicationLimitCreditMapper applicationLimitCreditMapper = Mappers.getMapper(
      ApplicationLimitCreditMapper.class);
  @Spy
  private ObjectMapper objectMapper;
  @Mock
  private IDSequenceService idSequenceService;
  @Mock
  private CustomerClient customerClient;
  @Mock
  MercuryConfigurationServiceCache mercuryCache;
  @Mock
  private ConfigurationServiceCache configurationServiceCache;

  @Mock
  private ApplicationDraftRepository applicationDraftRepository;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @Spy
  @InjectMocks
  private CopyApplicationService copyApplicationService;

  private AssetInfoResponse assetInfoResponse;

  private CreditAssetAllocationResponse creditAssetAllocationResponse;

  private ChecklistBaseResponse<GroupChecklistDto> oldChecklistResponse;

  private ChecklistBaseResponse<GroupChecklistDto> newChecklistResponse;

  private List<CustomerEntity> relatedPersonOld = new ArrayList<>();

  private List<CustomerRelationShipEntity> customerRelationShipListOld = new ArrayList<>();

  private ApplicationEntity applicationEntityOld;
  private ApplicationEntity applicationEntityNew;
  private CustomerEntity customerEntityNew;
  private List<CustomerEntity> relatedCustomerCreated = new ArrayList<>();
  private DataResponse regionAreaResp;
  private CreditConditionClientResponse<List<CreditConditionResponse>> creditConditionResponses;
  private Set<ApplicationCreditEntity> applicationCreditEntitiesNew;

  private CustomerBaseResponse<SearchCustomerV2Response> customerV2ResponseCustomerBaseResponseActive = new CustomerBaseResponse<>();

  private CustomerBaseResponse<SearchCustomerV2Response> customerV2ResponseCustomerBaseResponseInactive = new CustomerBaseResponse<>();

  private CustomerBaseResponse<CusRelationForSearchResponse> cusRelationForSearchResponse = new CustomerBaseResponse<>();

  private GetApplicationDTO getApplicationDTO;

  MercuryDataResponse city;

  Map<String, List<GetListResponse.Detail>> categoryMap;

  @Mock
  private AbstractBaseService abstractBaseService;

  private String oldBpmId = "OLD_BMP_ID";
  private String newBpmId = "NEW_BPM_ID";

  Set<ApplicationDraftEntity> applicationDraftEntities;
  AssetResponse assetInfoDTO;

  @BeforeEach
  void setUp() throws IOException {
    System.out.println("SETUP");
    MockitoAnnotations.initMocks(this);
    objectMapper.registerModule(ObjectMapperUtil.javaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    applicationEntityOld = createOldApplicationEntity();
    applicationEntityNew = createNewApplicationEntity(applicationEntityOld);
    getApplicationDTO = buildGetApplicationDTO();
    createCustomer(applicationEntityOld);
    initOtherData();
    initAssetInfo();
    initChecklist();
    // save draft
    doReturn(getApplicationDTO).when(copyApplicationService).getApplicationInfo(any());
    doNothing().when(copyApplicationService)
        .persistApplicationDraft(isA(String.class), isA(String.class), isA(Integer.class),
            isA(DebtInfoDTO.class));
    applicationDraftEntities = new HashSet<>();
    assetInfoDTO = JsonUtil.convertString2Object("{\"code\":\"HL-000\",\"message\":{\"vi\":\"Thành công.\",\"en\":\"Success.\"},\"data\":{\"assetData\":[{\"id\":1828,\"assetVersion\":3,\"objectId\":null,\"applicationId\":\"151-00004651\",\"assetCode\":\"Định danh tài sản\",\"assetType\":\"V401\",\"assetTypeName\":\"Đất ở\",\"assetGroup\":\"V001\",\"assetGroupName\":\"Bất động sản\",\"assetName\":\"Bất động sản là Thửa đất số Thửa đất/lô đất số, tờ bản đồ số Tờ bản đồ/vị trí lô đất số, tại Số nhà/Tên đường, Xã Văn Phú, Thành phố Yên Bái, Tỉnh Yên Bái theo GCN QSD đất số 12313123 do Nơi cấp ngày 14/11/2023, đứng tên\",\"ownerType\":null,\"ownerTypeName\":null,\"nextValuationDay\":null,\"addressLinkId\":null,\"status\":\"ACTIVE\",\"mortgageStatus\":\"V001\",\"hasComponent\":false,\"state\":null,\"assetAdditionalInfo\":{\"realEstateInfo\":{\"id\":2836,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-27T15:57:42\",\"updatedBy\":\"bpmtest1\",\"updatedAt\":\"2024-02-27T16:05:40\",\"provinceCode\":\"1\",\"provinceName\":\"Tỉnh Yên Bái\",\"districtCode\":\"657\",\"districtName\":\"Thành phố Yên Bái\",\"wardCode\":\"1467\",\"wardName\":\"Xã Văn Phú\",\"streetNumber\":\"Số nhà/Tên đường\",\"houseNumber\":null,\"floor\":null,\"landPlot\":\"Tờ bản đồ/vị trí lô đất số\",\"landOverValueStr\":null,\"landParcel\":\"Thửa đất/lô đất số\",\"mapLocation\":null,\"investorInformation\":null,\"description\":\"Mô tả tài sản (nếu có)\",\"payMethod\":null,\"buildingWork\":false,\"futureAsset\":false,\"expertiseValue\":null,\"landValue\":null,\"landValueStr\":null,\"landOverValue\":null,\"isNew\":false,\"propertyTypeDetail\":null,\"blockadeDisbursementPlan\":null,\"blocked\":null,\"soNguoiBenBan\":null,\"beneficiaryType\":null,\"certificateBookNo\":null,\"certificateVerified\":null,\"contractDetail\":null,\"landArea\":null,\"landAreaText\":null,\"landUsageForm\":null,\"landGeneralUse\":null,\"landUsePrivate\":null,\"landUseTarget\":null,\"landExpired\":null,\"landOriginOfUse\":null,\"landNote\":null,\"houseType\":null,\"houseAddress\":null,\"houseFloor\":null,\"houseName\":null,\"houseAreaUse\":null,\"houseArea\":null,\"houseStructure\":null,\"houseFloorArea\":null,\"houseOwnershipForm\":null,\"houseTypeDetail\":null,\"houseExpired\":null,\"completedConstructionYear\":null,\"houseAreaOutside\":null,\"houseNote\":null,\"houseDesc\":null,\"houseFloor1\":null,\"houseStructure1\":null,\"houseTypeDetail1\":null,\"houseArea1\":null,\"houseFloorArea1\":null,\"houseCreatedAt\":null,\"houseArchitectural\":null,\"houseStatus\":null,\"buyerName\":null,\"investorConfirmed\":null,\"projectId\":null,\"proposalCollateralValueStr\":null,\"sellers\":null,\"project\":null,\"notarizedInformationId\":null,\"moreAssetsOnLand\":null,\"mortgageContractNo\":null,\"houseUpdatedAt1\":null},\"transportationInfo\":null,\"stockInfo\":null,\"ownerInfo\":[{\"id\":29028,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-28T14:39:01\",\"updatedBy\":null,\"updatedAt\":null,\"customerId\":2772,\"customerRefCode\":\"9466\",\"customerName\":\"hant92 test 4\",\"relationshipCode\":\"V001\",\"relationshipName\":\"Khách hàng\",\"customerVersion\":null}],\"legalDocumentInfo\":{\"id\":5113,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-27T15:57:45\",\"updatedBy\":\"bpmtest1\",\"updatedAt\":\"2024-02-27T16:05:40\",\"docValue\":\"12313123\",\"docType\":\"V001\",\"docName\":\"GCN QSD đất\",\"issuedBy\":\"Nơi cấp\",\"dateOfIssue\":\"2023-11-14T00:00:00\",\"description\":\"\"},\"laborContractInfo\":null,\"otherInfo\":null,\"collateralAssetInfo\":{\"id\":6204,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-27T15:57:45\",\"updatedBy\":null,\"updatedAt\":null,\"proposalCollateralValue\":12312312,\"valuationGuaranteed\":null,\"ltv\":null,\"description\":null,\"isAdditional\":null,\"kvalue\":null},\"valuationInfo\":{\"id\":847,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-27T18:16:47\",\"updatedBy\":\"bpmtest1\",\"updatedAt\":\"2024-02-28T13:21:08.732\",\"mvlId\":\"18772\",\"status\":\"HS đã hoàn thành (đã đóng)\",\"valuationDate\":\"2023-10-18\",\"valuationAmount\":150000000,\"assetName\":null,\"valuationAssetId\":\"PTVT.TB.61\",\"assetCodeCore\":null,\"syncValuationDay\":\"2024-02-28T13:21:09.16\",\"assetType\":null,\"assetGroup\":null,\"assetCode\":null},\"components\":[],\"generalObligations\":[]},\"sourceType\":null}],\"opriskData\":null,\"searchAsset\":null,\"totalValuation\":0,\"complete\":true}}",
        AssetResponse.class, objectMapper);
  }

  @Test
  void copyApplicationTest() {
    //Application
    List<Long> listRefCustomerId = new ArrayList<>();
    listRefCustomerId.add(421L);
    listRefCustomerId.add(1235L);
    listRefCustomerId.add(1236L);
    when(applicationRepository.findByBpmId(oldBpmId)).thenReturn(Optional.of(applicationEntityOld));
    when(applicationRepository.saveAndFlush(any())).thenReturn(applicationEntityNew);
    when(idSequenceService.generateBpmId()).thenReturn(newBpmId);
    doNothing().when(commonService).saveDraft(newBpmId);
//    (eq(421L),eq(17677L),eq(17678L))
    when(customerClient.searchCustomerDetail(argThat(in(listRefCustomerId)), any())).thenReturn(
        customerV2ResponseCustomerBaseResponseInactive);
    when(customerClient.searchCustomerDetail(eq(1234L), any())).thenReturn(
        customerV2ResponseCustomerBaseResponseActive);
    when(mercuryCache.searchPlace(any())).thenReturn(city);
    when(commonService.getAddressTypeValue(any(), any())).thenReturn("addressTypeValue");
    when(commonService.getDistrictValue(any(), any())).thenReturn("districtValue");
    when(commonService.getWardValue(any(), any())).thenReturn("wardValue");
    when(configurationServiceCache.getCategoryDataByCodes(any())).thenReturn(categoryMap);
    when(configurationServiceCache.getCategoryData(any(), any())).thenReturn(new HashMap<>());
    //Customer and Related person
    when(customerRepository.getAllCustomerRelationByBpmId(oldBpmId)).thenReturn(relatedPersonOld);
    when(customerRelationshipRepository.getCustomerRelationByBmpId(oldBpmId)).thenReturn(
        customerRelationShipListOld);
    when(customerClient.searchCustomerRelationship(any())).thenReturn(cusRelationForSearchResponse);
    when(customerRepository.saveAndFlush(any())).thenReturn(customerEntityNew);
    when(customerRepository.saveAllAndFlush(any())).thenReturn(relatedCustomerCreated);
    //UserRMInfo
    when(userManagerClient.getRegionAreaByUserName(any())).thenReturn(regionAreaResp);
    when(userManagementClient.getSaleCode()).thenReturn("SALE_CODE");
    //Credit condition
    when(creditConditionClient.getCreditConditionByListId(any())).thenReturn(
        creditConditionResponses);
    doNothing().when(postDebtInfoService).referenceCreditConditionsEntities(any(), any());
    doNothing().when(cmsCreateApplicationService).createFirstHistory(any());
    doNothing().when(postCreateApplicationService).startCamundaInstance(any());
    //Asset
    when(assetClient.getAssetData(any(), any())).thenReturn(assetInfoResponse);
    when(collateralClient.getCommonAssetInfo(any(), any())).thenReturn(
        Mockito.mock(AssetInfoResponse.class));
    doReturn(assetInfoDTO).when(collateralClient)
        .callApiCommonAsset(isA(Object.class), isA(HttpMethod.class), isA(String.class),
            eq(AssetResponse.class));
    when(applicationDraftRepository.findByBpmId(any())).thenReturn(
        Optional.ofNullable(applicationDraftEntities));
    doNothing().when(assetClient).bpmCreateAssetData(any());
    doNothing().when(assetClient).bpmCreateAssetData(any());
    when(collateralClient.getAssetAllocationInfo(any())).thenReturn(creditAssetAllocationResponse);
    when(collateralClient.mappingCreditAssetInfo(any(), any())).thenReturn(new ArrayList<>());
    //Checklist
    when(checklistService.getChecklistByRequestCode(oldBpmId)).thenReturn(oldChecklistResponse);
    when(checklistService.reloadChecklist(newBpmId, true)).thenReturn(newChecklistResponse);
    when(checklistService.uploadFileChecklist(any())).thenReturn(any());
    //call copy application
    Assert.assertEquals(copyApplicationService.copyApplication(oldBpmId), newBpmId);
  }

  private void createCustomer(ApplicationEntity applicationEntityOld) {
    Long customerIdNew = 100l;
    customerEntityNew = customerMapper.copyCustomerEntity(applicationEntityOld.getCustomer());
    customerEntityNew.setId(customerIdNew);
    //create new customer
    relatedCustomerCreated = customerMapper.copyCustomerEntities(relatedPersonOld);
    for (int i = 0; i < relatedCustomerCreated.size(); i++) {
      relatedCustomerCreated.get(i).setId(customerIdNew + i + 1);
    }
  }

  private ApplicationEntity createNewApplicationEntity(ApplicationEntity applicationEntity)
      throws IOException {
    ApplicationEntity newApplicationEntity = SerializationUtils.clone(applicationEntity);
    applicationCreditEntitiesNew = objectMapper.readValue(
        new File(pathSourceFile + "application_credit_new.json"),
        new TypeReference<Set<ApplicationCreditEntity>>() {
        });
    newApplicationEntity.setBpmId(newBpmId);
    newApplicationEntity.setCredits(applicationCreditEntitiesNew);
    return newApplicationEntity;
  }

  private ApplicationEntity createOldApplicationEntity() throws IOException {
    ApplicationEntity applicationEntity = objectMapper.readValue(
        new File(pathSourceFile + "application.json"), ApplicationEntity.class);
    CustomerEntity customerEntity = objectMapper.readValue(
        new File(pathSourceFile + "customer.json"), CustomerEntity.class);
    relatedPersonOld = objectMapper.readValue(new File(pathSourceFile + "related_customer.json"),
        new TypeReference<List<CustomerEntity>>() {
        });
    customerRelationShipListOld = objectMapper.readValue(
        new File(pathSourceFile + "customer_relationship.json"),
        new TypeReference<List<CustomerRelationShipEntity>>() {
        });
    Set<CustomerRelationShipEntity> customerRelationShipEntities = objectMapper.readValue(
        new File(pathSourceFile + "customer_relationship.json"),
        new TypeReference<Set<CustomerRelationShipEntity>>() {
        });
    //Set customerRelationships
    customerEntity.setCustomerRelationShips(customerRelationShipEntities);
    //Set customer
    applicationEntity.setCustomer(customerEntity);
    //Application Contact
    Set<ApplicationContactEntity> applicationContactEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_contact.json"),
        new TypeReference<Set<ApplicationContactEntity>>() {
        });
    applicationEntity.setContact(applicationContactEntities);
    //Income
    Set<ApplicationIncomeEntity> applicationIncomeEntities = objectMapper.readValue(
        new File(pathSourceFile + "income.json"),
        new TypeReference<Set<ApplicationIncomeEntity>>() {
        });
    applicationEntity.setIncomes(applicationIncomeEntities);
    //Credit
    Set<ApplicationCreditEntity> applicationCreditEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_credit.json"),
        new TypeReference<Set<ApplicationCreditEntity>>() {
        });
    applicationEntity.setCredits(applicationCreditEntities);
    //Field
    Set<ApplicationFieldInformationEntity> applicationFieldInformationEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_field.json"),
        new TypeReference<Set<ApplicationFieldInformationEntity>>() {
        });
    applicationEntity.setFieldInformations(applicationFieldInformationEntities);
    //Limit Credit
    Set<ApplicationLimitCreditEntity> applicationLimitCreditEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_limit_credit.json"),
        new TypeReference<Set<ApplicationLimitCreditEntity>>() {
        });
    applicationEntity.setLimitCredits(applicationLimitCreditEntities);
    //Appraisal Content
    Set<ApplicationAppraisalContentEntity> applicationAppraisalContentEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_appraisal_content.json"),
        new TypeReference<Set<ApplicationAppraisalContentEntity>>() {
        });
    applicationEntity.setAppraisalContents(applicationAppraisalContentEntities);
    return applicationEntity;
  }

  private void initOtherData() throws IOException {
    //Region Area
    regionAreaResp = objectMapper.readValue(new File(pathSourceFile + "region_area.json"),
        DataResponse.class);
    //Credit condition
    creditConditionResponses = objectMapper.readValue(
        new File(pathSourceFile + "credit_condition.json"), CreditConditionClientResponse.class);
    //searchCustomerV2
    customerV2ResponseCustomerBaseResponseInactive.setData(objectMapper.readValue(
        new File(pathSourceFile + "customer_detail_inactive.json"),
        SearchCustomerV2Response.class));
    customerV2ResponseCustomerBaseResponseActive.setData(objectMapper.readValue(
        new File(pathSourceFile + "customer_detail_active_1234.json"),
        SearchCustomerV2Response.class));
    //Search customer relationship detail
    cusRelationForSearchResponse.setData(objectMapper.readValue(
        new File(pathSourceFile + "customer_relationship_detail.json"),
        CusRelationForSearchResponse.class));
    city = objectMapper.readValue(
        new File(pathSourceFile + "city.json"), MercuryDataResponse.class);
    categoryMap = objectMapper.readValue(new File(pathSourceFile + "category_map.json"),
        new TypeReference<Map<String, List<GetListResponse.Detail>>>() {
        });
  }

  private void initAssetInfo() throws IOException {
    assetInfoResponse = objectMapper.readValue(new File(pathSourceFile + "asset_info.json"),
        AssetInfoResponse.class);

    creditAssetAllocationResponse = objectMapper.readValue(
        new File(pathSourceFile + "asset_allocation.json"), CreditAssetAllocationResponse.class);
  }

  private void initChecklist() throws IOException {
    oldChecklistResponse = objectMapper.readValue(
        new File(pathSourceFile + "reload_checklist_old.json"),
        new TypeReference<ChecklistBaseResponse<GroupChecklistDto>>() {
        });

    newChecklistResponse = (ChecklistBaseResponse<GroupChecklistDto>) objectMapper.readValue(
        new File(pathSourceFile + "reload_checklist_new.json"),
        new TypeReference<ChecklistBaseResponse<GroupChecklistDto>>() {
        });
  }

  private GetApplicationDTO buildGetApplicationDTO() {
    GetApplicationDTO getApplicationDTO = new GetApplicationDTO();
    DebtInfoDTO debtInfoDTO = new DebtInfoDTO();
    debtInfoDTO.setAssetAllocations(null);
    Set<ApplicationCreditDTO> applicationCreditDTOS = new HashSet<>();
    ApplicationCreditDTO creditDTO1 = new ApplicationCreditLoanDTO();
    ApplicationCreditDTO creditDTO2 = new ApplicationCreditLoanDTO();
    ApplicationCreditDTO creditDTO3 = new ApplicationCreditLoanDTO();
    ApplicationCreditDTO creditDTO4 = new ApplicationCreditLoanDTO();
    ApplicationCreditDTO creditDTO5 = new ApplicationCreditLoanDTO();
    ApplicationCreditDTO creditDTO6 = new ApplicationCreditLoanDTO();
    creditDTO1.setId(401L);
    creditDTO1.setAssets(new ArrayList<Long>(Arrays.asList(1L, 2L, 3L)));
    creditDTO2.setId(402L);
    creditDTO2.setAssets(new ArrayList<Long>(Arrays.asList(1L, 2L, 3L)));
    creditDTO3.setId(403L);
    creditDTO3.setAssets(new ArrayList<Long>(Arrays.asList(1L, 2L, 3L)));
    creditDTO4.setId(404L);
    creditDTO4.setAssets(new ArrayList<Long>(Arrays.asList(1L, 2L, 3L)));
    creditDTO5.setId(405L);
    creditDTO5.setAssets(new ArrayList<Long>(Arrays.asList(1L, 2L, 3L)));
    creditDTO6.setId(406L);
    creditDTO6.setAssets(new ArrayList<Long>(Arrays.asList(1L, 2L, 3L)));

    applicationCreditDTOS.add(creditDTO1);
    applicationCreditDTOS.add(creditDTO2);
    applicationCreditDTOS.add(creditDTO3);
    applicationCreditDTOS.add(creditDTO4);
    applicationCreditDTOS.add(creditDTO5);
    applicationCreditDTOS.add(creditDTO6);
    debtInfoDTO.setCredits(applicationCreditDTOS);
    getApplicationDTO.setDebtInfo(debtInfoDTO);
    return getApplicationDTO;
  }
}