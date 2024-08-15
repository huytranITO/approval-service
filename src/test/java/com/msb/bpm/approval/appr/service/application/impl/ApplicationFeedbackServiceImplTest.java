package com.msb.bpm.approval.appr.service.application.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.asset.AssetClient;
import com.msb.bpm.approval.appr.client.general.GeneralInfoClient;
import com.msb.bpm.approval.appr.constant.MessageCode;
import com.msb.bpm.approval.appr.kafka.producer.DataKafkaProducer;
import com.msb.bpm.approval.appr.mapper.ApplicationContactMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationFeedbackMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.mapper.AssetInfoMapper;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import com.msb.bpm.approval.appr.model.entity.ApplicationAppraisalContentEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationContactEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.request.feeback.PostApplicationFeedbackRequest;
import com.msb.bpm.approval.appr.model.response.MessageResponse;
import com.msb.bpm.approval.appr.model.response.asset.AssetInfoResponse;
import com.msb.bpm.approval.appr.model.response.general.GeneralInfoResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRelationshipRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.repository.FeedbackHistoryRepository;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class ApplicationFeedbackServiceImplTest {

  private String pathSourceFile = "src/test/resources/feedback_application/";

  private ObjectMapper objectMapperTest;
  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private CustomerRelationshipRepository customerRelationshipRepository;
  @Mock
  private VerifyService verifyService;
  @Mock
  private CommonService commonService;
  @Mock
  private FeedbackHistoryRepository feedbackHistoryRepository;
  @Mock
  private DataKafkaProducer dataKafkaProducer;
  @Mock
  private SecurityContextUtil securityContextUtil;
  @Mock
  private GeneralInfoClient generalInfoClient;
  @Mock
  private AssetClient assetClient;
  @Mock
  private ApplicationDraftRepository applicationDraftRepository;

  @Spy
  private ApplicationFeedbackMapper applicationFeedbackMapper = Mappers.getMapper(
      ApplicationFeedbackMapper.class);

  @Spy
  private ApplicationMapper applicationMapper = Mappers.getMapper(
      ApplicationMapper.class);
  @Spy
  private CustomerMapper customerMapper = Mappers.getMapper(
      CustomerMapper.class);

  @Spy
  private ApplicationIncomeMapper applicationIncomeMapper = Mappers.getMapper(
      ApplicationIncomeMapper.class);

  @Spy
  private ApplicationCreditMapper applicationCreditMapper = Mappers.getMapper(
      ApplicationCreditMapper.class);

  @Spy
  private ApplicationContactMapper applicationContactMapper = Mappers.getMapper(
      ApplicationContactMapper.class);

  @Spy
  private AssetInfoMapper assetInfoMapper = Mappers.getMapper(
      AssetInfoMapper.class);

  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks
  private ApplicationFeedbackServiceImpl applicationFeedbackService;

  private PostApplicationFeedbackRequest postApplicationFeedbackRequest;

  private ApplicationEntity applicationEntity;
  private List<CustomerEntity> relatedPerson = new ArrayList<>();

  private List<CustomerRelationShipEntity> customerRelationShipList = new ArrayList<>();

  private GetUserProfileResponse getUserProfileResponse;

  private List<GeneralInfoResponse> generalInfoResponses;

  private AssetInfoResponse assetInfoResponse;

  private ApplicationDraftEntity applicationDraftEntity;

  private Set<ApplicationDraftEntity> applicationDraftEntitiesNoGreenTick;

  private Set<ApplicationDraftEntity> applicationDraftEntitiesGreenTick;

  @BeforeEach
  public void setUp() throws IOException {
    System.out.println("SETUP");
    MockitoAnnotations.initMocks(this);
    objectMapperTest = new ObjectMapper();
    objectMapperTest.registerModule(ObjectMapperUtil.javaTimeModule());
    applicationEntity = createApplicationEntity();
    initData();
  }

  @Test
  void feedbackForCustomerEditingTest() {
    //Application
    String bpmId = "151-00007879";
    when(applicationRepository.findByBpmIdCustomQuery(bpmId)).thenReturn(
        Optional.of(applicationEntity));
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getName()).thenReturn("thietpt");
    SecurityContextHolder securityContextHolder = Mockito.mock(SecurityContextHolder.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    Mockito.when(securityContextUtil.getAuthentication()).thenReturn(authentication);
    //Verify
    when(verifyService.compareWithApplicationLDP(any())).thenReturn(true);
    doNothing().when(applicationRepository).updateLdpStatus(any(), any());
    when(commonService.getUserDetail(any())).thenReturn(getUserProfileResponse);
    //store feedback
    when(feedbackHistoryRepository.save(any())).thenReturn(new ApplicationHistoryFeedbackEntity());
    when(generalInfoClient.getGeneralInfo()).thenReturn(generalInfoResponses);
    when(applicationDraftRepository.findByBpmIdAndTabCode(any(), any())).thenReturn(
        Optional.of(applicationDraftEntity));
    doNothing().when(dataKafkaProducer).publishMessage(any());

    MessageResponse messageResponse = applicationFeedbackService.applicationFeedbackCustomer(
        postApplicationFeedbackRequest);
    Assert.assertEquals(messageResponse.getCode(), MessageCode.FEEDBACK_EDITING_SUCCESS.getCode());
  }

  @Test
  void feedbackForCustomerConfirmTest() {
    //Application
    String bpmId = "151-00007879";
    when(applicationRepository.findByBpmIdCustomQuery(bpmId)).thenReturn(
        Optional.of(applicationEntity));
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getName()).thenReturn("thietpt");
    SecurityContextHolder securityContextHolder = Mockito.mock(SecurityContextHolder.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    Mockito.when(securityContextUtil.getAuthentication()).thenReturn(authentication);
    //Verify
    when(verifyService.compareWithApplicationLDP(any())).thenReturn(false);
    doNothing().when(applicationRepository).updateLdpStatus(any(), any());
    when(commonService.getUserDetail(any())).thenReturn(getUserProfileResponse);
    //store feedback
    when(feedbackHistoryRepository.save(any())).thenReturn(new ApplicationHistoryFeedbackEntity());
    when(customerRepository.getAllCustomerRelation(any(), any())).thenReturn(relatedPerson);
    when(customerRelationshipRepository.getCustomerRelationByBmpId(any())).thenReturn(
        customerRelationShipList);
    when(assetClient.getAssetData(any(), any())).thenReturn(assetInfoResponse);
    when(generalInfoClient.getGeneralInfo()).thenReturn(generalInfoResponses);
    when(applicationDraftRepository.findByBpmIdAndTabCode(any(), any())).thenReturn(
        Optional.of(applicationDraftEntity));
    when(applicationDraftRepository.findByBpmId(any())).thenReturn(
        Optional.of(applicationDraftEntitiesGreenTick));
    doNothing().when(dataKafkaProducer).publishMessage(any());

    MessageResponse messageResponse = applicationFeedbackService.applicationFeedbackCustomer(
        postApplicationFeedbackRequest);
    Assert.assertEquals(messageResponse.getCode(), MessageCode.FEEDBACK_CONFIRM_SUCCESS.getCode());
  }

  @Test
  void feedbackForCustomerConfirmNoGreenTickTest() {
    //Application
    String bpmId = "151-00007879";
    when(applicationRepository.findByBpmIdCustomQuery(bpmId)).thenReturn(
        Optional.of(applicationEntity));
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getName()).thenReturn("thietpt");
    SecurityContextHolder securityContextHolder = Mockito.mock(SecurityContextHolder.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    Mockito.when(securityContextUtil.getAuthentication()).thenReturn(authentication);
    //Verify
    when(verifyService.compareWithApplicationLDP(any())).thenReturn(false);
    doNothing().when(applicationRepository).updateLdpStatus(any(), any());
    when(commonService.getUserDetail(any())).thenReturn(getUserProfileResponse);
    //store feedback
    when(feedbackHistoryRepository.save(any())).thenReturn(new ApplicationHistoryFeedbackEntity());
    when(customerRepository.getAllCustomerRelation(any(), any())).thenReturn(relatedPerson);
    when(customerRelationshipRepository.getCustomerRelationByBmpId(any())).thenReturn(
        customerRelationShipList);
    when(assetClient.getAssetData(any(), any())).thenReturn(assetInfoResponse);
    when(generalInfoClient.getGeneralInfo()).thenReturn(generalInfoResponses);
    when(applicationDraftRepository.findByBpmIdAndTabCode(any(), any())).thenReturn(
        Optional.of(applicationDraftEntity));
    when(applicationDraftRepository.findByBpmId(any())).thenReturn(
        Optional.of(applicationDraftEntitiesNoGreenTick));
    doNothing().when(dataKafkaProducer).publishMessage(any());

    MessageResponse messageResponse = applicationFeedbackService.applicationFeedbackCustomer(
        postApplicationFeedbackRequest);
    Assert.assertEquals(messageResponse.getCode(), MessageCode.FEEDBACK_ERROR_WITH_NO_GREEN_TICK.getCode());
  }

  private void initData() throws IOException {
    postApplicationFeedbackRequest = objectMapperTest.readValue(
        new File(pathSourceFile + "post_feedback.json"), PostApplicationFeedbackRequest.class);
    getUserProfileResponse = objectMapperTest.readValue(
        new File(pathSourceFile + "get_user_profile_response.json"), GetUserProfileResponse.class);
    generalInfoResponses = objectMapperTest.readValue(
        new File(pathSourceFile + "general_info.json"),
        new TypeReference<List<GeneralInfoResponse>>() {
        });
    assetInfoResponse = objectMapperTest.readValue(new File(pathSourceFile + "asset_info.json"),
        AssetInfoResponse.class);
    //Application Draft
    applicationDraftEntity = objectMapperTest.readValue(
        new File(pathSourceFile + "debt_info_draft.json"),
        ApplicationDraftEntity.class);
    applicationDraftEntitiesGreenTick = objectMapperTest.readValue(
        new File(pathSourceFile + "application_draft_green_tick.json"),
        new TypeReference<Set<ApplicationDraftEntity>>() {
        });
    applicationDraftEntitiesNoGreenTick = objectMapperTest.readValue(
        new File(pathSourceFile + "application_draft_no_green_tick.json"),
        new TypeReference<Set<ApplicationDraftEntity>>() {
        });
  }

  private ApplicationEntity createApplicationEntity() throws IOException {
    ApplicationEntity applicationEntity = objectMapperTest.readValue(
        new File(pathSourceFile + "application.json"), ApplicationEntity.class);
    CustomerEntity customerEntity = objectMapperTest.readValue(
        new File(pathSourceFile + "customer.json"), CustomerEntity.class);
    relatedPerson = objectMapperTest.readValue(
        new File(pathSourceFile + "related_customer.json"),
        new TypeReference<List<CustomerEntity>>() {
        });
    customerRelationShipList = objectMapperTest.readValue(
        new File(pathSourceFile + "customer_relationship.json"),
        new TypeReference<List<CustomerRelationShipEntity>>() {
        });
    Set<CustomerRelationShipEntity> customerRelationShipEntities = objectMapperTest.readValue(
        new File(pathSourceFile + "customer_relationship.json"),
        new TypeReference<Set<CustomerRelationShipEntity>>() {
        });
    //Set customerRelationships
    customerEntity.setCustomerRelationShips(customerRelationShipEntities);
    //Set customer
    applicationEntity.setCustomer(customerEntity);
    //Application Contact
    Set<ApplicationContactEntity> applicationContactEntities = objectMapperTest.readValue(
        new File(pathSourceFile + "application_contact.json"),
        new TypeReference<Set<ApplicationContactEntity>>() {
        });
    applicationEntity.setContact(applicationContactEntities);
    //Income
    Set<ApplicationIncomeEntity> applicationIncomeEntities = objectMapperTest.readValue(
        new File(pathSourceFile + "income.json"),
        new TypeReference<Set<ApplicationIncomeEntity>>() {
        });
    applicationEntity.setIncomes(applicationIncomeEntities);
    //Credit
    Set<ApplicationCreditEntity> applicationCreditEntities = objectMapperTest.readValue(
        new File(pathSourceFile + "application_credit.json"),
        new TypeReference<Set<ApplicationCreditEntity>>() {
        });
    applicationEntity.setCredits(applicationCreditEntities);
    //Field
    Set<ApplicationFieldInformationEntity> applicationFieldInformationEntities = objectMapperTest.readValue(
        new File(pathSourceFile + "application_field.json"),
        new TypeReference<Set<ApplicationFieldInformationEntity>>() {
        });
    applicationEntity.setFieldInformations(applicationFieldInformationEntities);
    //Limit Credit
    Set<ApplicationLimitCreditEntity> applicationLimitCreditEntities = objectMapperTest.readValue(
        new File(pathSourceFile + "application_limit_credit.json"),
        new TypeReference<Set<ApplicationLimitCreditEntity>>() {
        });
    applicationEntity.setLimitCredits(applicationLimitCreditEntities);
    //Appraisal Content
    Set<ApplicationAppraisalContentEntity> applicationAppraisalContentEntities = objectMapperTest.readValue(
        new File(pathSourceFile + "application_appraisal_content.json"),
        new TypeReference<Set<ApplicationAppraisalContentEntity>>() {
        });
    applicationEntity.setAppraisalContents(applicationAppraisalContentEntities);

    return applicationEntity;
  }

}
