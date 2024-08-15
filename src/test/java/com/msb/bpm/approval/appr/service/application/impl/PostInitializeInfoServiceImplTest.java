package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.AML_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.ENTERPRISE_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INDIVIDUAL_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.OTHER;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.PROPERTY_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.RENTAL;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.SALARY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.OPR_TAG;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0000;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0099;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.UserManagementClient;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO.AmlOprDtl;
import com.msb.bpm.approval.appr.model.dto.ApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BusinessIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO.CicDetail;
import com.msb.bpm.approval.appr.model.dto.CustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.EnterpriseCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.ExchangeIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.OtherIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.PropertyBusinessIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.RentalIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.SalaryIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.application.ApplicationContactDTO;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.request.customer.FindByListCustomerRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV3Response;
import com.msb.bpm.approval.appr.model.response.usermanager.OrganizationTreeDetail;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 18/9/2023, Monday
 **/
@ExtendWith(MockitoExtension.class)
class PostInitializeInfoServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private UserManagementClient userManagementClient;

  @Mock
  private SecurityContextUtil securityContextUtil;

  @InjectMocks
  private PostInitializeInfoServiceImpl postInitializeInfoService;

  private PostInitializeInfoRequest request;

  private Map<Long, Long> customerRefCustomerIdMap;

  private ApplicationEntity applicationEntity;

  @Mock
  private UserManagerClient userManagerClient;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  CustomerClient customerClient;

  @BeforeEach
  public void setUp() {
    CustomerDTO customerDTO = new IndividualCustomerDTO();
    customerDTO.setId(1L);
    customerDTO.setCustomerType(CustomerType.RB.name());
    customerDTO.setIdentities(Collections.singleton(new CustomerIdentityDTO().withDocumentType("V001")));
    customerDTO.setAddresses(Collections.singleton(new CustomerAddressDTO()));

    Set<CustomerDTO> customerRelations = new HashSet<>();

    CustomerDTO customerRelationDTO = new IndividualCustomerDTO();
    customerRelationDTO.setId(2L);
    customerRelationDTO.setCustomerType(CustomerType.RB.name());
    customerRelationDTO.setIdentities(Collections.singleton(new CustomerIdentityDTO().withDocumentType("V001")));
    customerRelationDTO.setAddresses(Collections.singleton(new CustomerAddressDTO()));
    customerRelations.add(customerRelationDTO);

    customerRelationDTO = new IndividualCustomerDTO();
    customerRelationDTO.setId(1L);
    customerRelationDTO.setCustomerType(CustomerType.RB.name());
    customerRelationDTO.setIdentities(Collections.singleton(new CustomerIdentityDTO().withDocumentType("V001")));
    customerRelationDTO.setAddresses(Collections.singleton(new CustomerAddressDTO()));
    customerRelations.add(customerRelationDTO);

    Set<CustomerDTO> enterpriseRelations = new HashSet<>();
    CustomerDTO enterpriseRelationDTO = new EnterpriseCustomerDTO();
    enterpriseRelationDTO.setCustomerType(CustomerType.EB.name());
    enterpriseRelationDTO.setRefCustomerId(1L);
    enterpriseRelationDTO.setVersion(1);
    enterpriseRelationDTO.setIssuedPlace("HN");
    enterpriseRelationDTO.setIssuedAt(LocalDate.now());
    enterpriseRelationDTO.setIssuedBy("CA HN");
    enterpriseRelations.add(enterpriseRelationDTO);

    enterpriseRelationDTO = new EnterpriseCustomerDTO();
    enterpriseRelationDTO.setId(1L);
    enterpriseRelationDTO.setCustomerType(CustomerType.EB.name());
    enterpriseRelationDTO.setRefCustomerId(1L);
    enterpriseRelationDTO.setVersion(1);
    enterpriseRelationDTO.setIssuedPlace("HN");
    enterpriseRelationDTO.setIssuedAt(LocalDate.now());
    enterpriseRelationDTO.setIssuedBy("CA HN");
    enterpriseRelations.add(enterpriseRelationDTO);

    CicDTO cicDTO = new CicDTO();

    Set<CicDetail> cicDetails = new HashSet<>();
    CicDetail cicDetail = new CicDetail();
    cicDetail.setId(1L);
    cicDetail.setSubject("Khach hang");
    cicDetails.add(cicDetail);

    cicDetail = new CicDetail();
    cicDetail.setId(2L);
    cicDetail.setSubject("Nguoi lien quan");
    cicDetails.add(cicDetail);

    cicDTO.setCicDetails(cicDetails);
    cicDTO.setExplanation("ABCDEF");

    AmlOprDTO amlOpr = new AmlOprDTO();
    Set<AmlOprDtl> amls = new HashSet<>();
    AmlOprDtl aml = new AmlOprDtl();
    aml.setQueryType(AML_TAG);
    aml.setId(1L);
    amls.add(aml);
    amlOpr.setAmls(amls);

    Set<AmlOprDtl> oprs = new HashSet<>();
    AmlOprDtl opr = new AmlOprDtl();
    opr.setId(2L);
    opr.setQueryType(OPR_TAG);
    oprs.add(opr);
    amlOpr.setOprs(oprs);

    ApplicationContactDTO contactDTO = new ApplicationContactDTO();
    contactDTO.setFullName("Nguyen Van Manh");
    contactDTO.setRelationship("V001");
    contactDTO.setPhoneNumber("0123");

    Set<ApplicationIncomeDTO> incomes = new HashSet<>();
    ExchangeIncomeDTO income = new ExchangeIncomeDTO();
    income.setIncomeRecognitionMethod("EXCHANGE");
    income.setOrderDisplay(1);

    Set<BaseIncomeDTO> incomeItems = new HashSet<>();
    SalaryIncomeDTO salaryIncome = new SalaryIncomeDTO();
    salaryIncome.setIncomeType(SALARY);
    salaryIncome.setRefCustomerId(1L);
    incomeItems.add(salaryIncome);

    RentalIncomeDTO rentalIncome = new RentalIncomeDTO();
    rentalIncome.setIncomeType(RENTAL);
    rentalIncome.setRefCustomerId(1L);
    incomeItems.add(rentalIncome);

    BusinessIncomeDTO individualIncome = new BusinessIncomeDTO();
    individualIncome.setIncomeType(INDIVIDUAL_BUSINESS);
    individualIncome.setRefCustomerId(1L);
    incomeItems.add(individualIncome);

    BusinessIncomeDTO enterpriseIncome = new BusinessIncomeDTO();
    enterpriseIncome.setIncomeType(ENTERPRISE_BUSINESS);
    enterpriseIncome.setRefCustomerId(1L);
    incomeItems.add(enterpriseIncome);

    OtherIncomeDTO otherIncome = new OtherIncomeDTO();
    otherIncome.setIncomeType(OTHER);
    otherIncome.setRefCustomerId(1L);
    incomeItems.add(otherIncome);

    PropertyBusinessIncomeDTO propertyIncome = new PropertyBusinessIncomeDTO();
    propertyIncome.setIncomeType(PROPERTY_BUSINESS);
    propertyIncome.setRefCustomerId(1L);
    propertyIncome.setCustomerTransactionIncomes(new HashSet<>());
    incomeItems.add(propertyIncome);

    income.setIncomeItems(incomeItems);
    incomes.add(income);

    customerRefCustomerIdMap = new HashMap<>();
    customerRefCustomerIdMap.put(1L, 1L);

    request = new PostInitializeInfoRequest();

    request.setType(POST_INITIALIZE_INFO);
    request.setApplication(Mockito.mock(ApplicationDTO.class));
    request.setCustomerAndRelationPerson(new CustomerAndRelationPersonDTO()
        .withCustomer(customerDTO)
        .withCustomerRelations(customerRelations)
        .withEnterpriseRelations(enterpriseRelations)
        .withCic(cicDTO)
        .withAmlOpr(amlOpr)
        .withApplicationContact(Collections.singleton(contactDTO)));
    request.setIncomes(incomes);

    applicationEntity = new ApplicationEntity();
    applicationEntity.setStatus(AS0000.getValue());
    applicationEntity.setAssignee("unknown");
//    .withStatus(AS0000.getValue())
//        .withAssignee("unknown");

    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setCif("111");
    CustomerRelationShipEntity relationShipEntity = new CustomerRelationShipEntity();
    relationShipEntity.setCustomerRefId(2L);
    customerEntity.getCustomerRelationShips().add(relationShipEntity);

    applicationEntity.setCustomer(customerEntity);
  }

  @Test
  void test_getType_should_be_ok() {
    Assertions.assertEquals(POST_INITIALIZE_INFO, postInitializeInfoService.getType());
  }

  @ParameterizedTest
  @CsvSource({"false,0","true,3","true,1"})
  void test_referenceApplicationContact_should_success_contactNull(boolean hasContact, int numOfContact) {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    List<ApplicationContactDTO> contactDTOs = new ArrayList<>(
        request.getCustomerAndRelationPerson().getApplicationContact());

    if (!hasContact) {
      Assertions.assertDoesNotThrow(
          () -> postInitializeInfoService.referenceApplicationContact(applicationEntity, null));
    } else if (numOfContact > 2) {

      ApplicationContactDTO applicationContactDTO = new ApplicationContactDTO();
      applicationContactDTO.setPhoneNumber("1");
      applicationContactDTO.setRelationship("2");
      applicationContactDTO.setFullName("3");
      contactDTOs.add(applicationContactDTO);

      applicationContactDTO = new ApplicationContactDTO();
      applicationContactDTO.setPhoneNumber("4");
      applicationContactDTO.setRelationship("5");
      applicationContactDTO.setFullName("6");
      contactDTOs.add(new ApplicationContactDTO());

      Set<ApplicationContactDTO> contactDTOSet = new HashSet<>(contactDTOs);

      Assertions.assertThrows(ApprovalException.class,
          () -> postInitializeInfoService.referenceApplicationContact(applicationEntity, contactDTOSet));
    } else {

      Assertions.assertDoesNotThrow(
          () -> postInitializeInfoService.referenceApplicationContact(applicationEntity, new HashSet<>(contactDTOs)));
    }
  }

  @Test
  void test_getInitializeInfoDTO_should_success() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setCustomer(new CustomerEntity().withCustomerType(CustomerType.RB.name()));


    Assertions.assertDoesNotThrow(() -> postInitializeInfoService.getInitializeInfoDTO(applicationEntity, null, null));
  }

  @ParameterizedTest
  @CsvSource({"false","true"})
  void test_persistCustomerRelationsFlushAll_should_return_success(boolean hasRelations) {
    if (hasRelations) {
      CustomerEntity customerEntity = new CustomerEntity();
      customerEntity.setCustomerType(CustomerType.RB.name());
      customerEntity.setId(1L);

      Mockito.when(customerRepository.findById(Mockito.anyLong()))
          .thenReturn(Optional.of(customerEntity));

      Set<CustomerDTO> responses = postInitializeInfoService.persistCustomerRelationsFlushAll(
          request.getCustomerAndRelationPerson()
              .getCustomerRelations(),
          request.getCustomerAndRelationPerson().getEnterpriseRelations());

      Assertions.assertNotNull(responses);
    } else {
      Set<CustomerDTO> responses = postInitializeInfoService.persistCustomerRelationsFlushAll(null, null);
      Assertions.assertEquals(0, responses.size());
    }
  }

  @ParameterizedTest
  @CsvSource({"false","true"})
  void test_persistCustomerWithRelation_should_return_success(boolean hasRelation) {
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setCustomerType(CustomerType.RB.name());
    customerEntity.setId(1L);

    test_persistCustomerRelationsFlushAll_should_return_success(true);

    CustomerEntity response;
    if (hasRelation) {
      response = postInitializeInfoService.persistCustomerWithRelation(
          request.getCustomerAndRelationPerson().getCustomer(),
          request.getCustomerAndRelationPerson().getCustomerRelations());
    } else {
      response = postInitializeInfoService.persistCustomerWithRelation(
          request.getCustomerAndRelationPerson().getCustomer(), null);
    }

    Assertions.assertNotNull(response);
  }

  @ParameterizedTest
  @CsvSource({"false,false","true,false","true,true"})
  void test_referenceAmlOprData_should_return_success(boolean hasAmlOpr, boolean hasAmlOprDetail) {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    AmlOprEntity amlOprEntity = new AmlOprEntity();
    amlOprEntity.setId(1L);
    applicationEntity.getAmlOprs().add(amlOprEntity);

    amlOprEntity = new AmlOprEntity();
    amlOprEntity.setId(2L);
    applicationEntity.getAmlOprs().add(amlOprEntity);

    if (!hasAmlOpr) {
      Assertions.assertDoesNotThrow(() -> postInitializeInfoService.referenceAmlOprData(applicationEntity, null));
    } else if (!hasAmlOprDetail) {
      AmlOprDTO amlOpr = request.getCustomerAndRelationPerson().getAmlOpr();
      amlOpr.setOprs(null);
      amlOpr.setAmls(null);
      Assertions.assertDoesNotThrow(() -> postInitializeInfoService.referenceAmlOprData(applicationEntity, amlOpr));
    } else {
      Assertions.assertDoesNotThrow(
          () -> postInitializeInfoService.referenceAmlOprData(applicationEntity,
              request.getCustomerAndRelationPerson()
                  .getAmlOpr()));
    }
  }

  @ParameterizedTest
  @CsvSource({"false","true"})
  void test_referenceCicData_should_return_success(boolean hasCicData) {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    CicEntity cicEntity = new CicEntity();
    cicEntity.setId(1L);
    cicEntity.setSubject("Khach hang");
    applicationEntity.getCics().add(cicEntity);

    cicEntity = new CicEntity();
    cicEntity.setId(2L);
    cicEntity.setSubject("Nguoi lien quan");
    applicationEntity.getCics().add(cicEntity);

    cicEntity = new CicEntity();
    cicEntity.setId(3L);
    cicEntity.setSubject("Nguoi lien quan");
    applicationEntity.getCics().add(cicEntity);

    CicDTO cicDTO = new CicDTO();

    Set<CicDetail> cicDetails = new HashSet<>();
    CicDetail cicDetail = new CicDetail();
    cicDetail.setId(1L);
    cicDetail.setSubject("Khach hang");
    cicDetails.add(cicDetail);

    cicDetail = new CicDetail();
    cicDetail.setId(2L);
    cicDetail.setSubject("Nguoi lien quan");
    cicDetails.add(cicDetail);

    cicDTO.setCicDetails(cicDetails);
    cicDTO.setExplanation("ABCDEF");

    if (hasCicData) {
      Assertions.assertDoesNotThrow(
          () -> postInitializeInfoService.referenceCicData(applicationEntity, cicDTO));
    } else {
      Assertions.assertDoesNotThrow(
          () -> postInitializeInfoService.referenceCicData(applicationEntity, null));
    }
  }

  @ParameterizedTest
  @CsvSource({"false","true"})
  void test_referenceApplicationIncome_should_return_success(boolean hasIncome) {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    Map<Long, Long> customerRefCustomerIdMap = new HashMap<>();
    customerRefCustomerIdMap.put(1L, 1L);

    if (hasIncome) {
      Assertions.assertDoesNotThrow(
          () -> postInitializeInfoService.referenceApplicationIncome(applicationEntity,
              request.getIncomes(), customerRefCustomerIdMap));
    } else {
      Assertions.assertDoesNotThrow(
          () -> postInitializeInfoService.referenceApplicationIncome(applicationEntity,
              null, customerRefCustomerIdMap));
    }
  }

  @Test
  void test_saveData_should_return_success() {
    Mockito.when(userManagementClient.getSaleCode()).thenReturn("041951");
    test_referenceApplicationIncome_should_return_success(true);
    test_referenceCicData_should_return_success(true);
    test_referenceAmlOprData_should_return_success(true, true);
    test_referenceApplicationContact_should_success_contactNull(true, 1);

    Assertions.assertDoesNotThrow(() -> postInitializeInfoService.saveData(applicationEntity,
        request, customerRefCustomerIdMap));
  }

/*
  @Test
  void test_execute_should_return_success() {
    Mockito.when(applicationRepository.findByBpmId(Mockito.anyString()))
        .thenReturn(Optional.of(applicationEntity));
    test_persistCustomerRelationsFlushAll_should_return_success(true);
    test_persistCustomerWithRelation_should_return_success(true);
    test_saveData_should_return_success();
    Assertions.assertDoesNotThrow(() -> postInitializeInfoService.execute(request, "151-00001111"));
  }
*/

  @Test
  void test_execute_should_throw_exception_1() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenReturn("");

    applicationEntity.setAssignee("nhant");
    Mockito.when(applicationRepository.findByBpmId(Mockito.anyString()))
        .thenReturn(Optional.of(applicationEntity));

    Assertions.assertThrows(ApprovalException.class, () -> postInitializeInfoService.execute(request, "151-00001111"));
  }

  @Test
  void test_execute_should_throw_exception_2() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenReturn("");
    applicationEntity.setStatus(AS0099.getValue());
    Mockito.when(applicationRepository.findByBpmId(Mockito.anyString()))
        .thenReturn(Optional.of(applicationEntity));

    Assertions.assertThrows(ApprovalException.class, () -> postInitializeInfoService.execute(request, "151-00001111"));
  }

  @Test
  void test_persistCustomerFlushAll_should_be_ok_if_inputEmpty() {
    Assertions.assertDoesNotThrow(() -> postInitializeInfoService.persistCustomerFlushAll(null));
  }

  @Test
  void  test_businessCode_isBlank() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenReturn("");
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getName()).thenReturn("nhant");
    SecurityContextHolder securityContextHolder = Mockito.mock(SecurityContextHolder.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    Mockito.when(securityContextUtil.getAuthentication()).thenReturn(authentication);


    PostInitializeInfoRequest request1 = new PostInitializeInfoRequest();
    ApplicationDTO applicationDTO = new ApplicationDTO();
    request1.setApplication(applicationDTO);
    applicationDTO.setBusinessCode("");
    applicationDTO.setProcessingRole(ProcessingRole.PD_RB_RM.name());
    applicationDTO.setBpmId("151-00001111");
    applicationDTO.setStatus(AS0000.getValue());
    applicationDTO.setAssignee("nhant");
    CustomerAndRelationPersonDTO customerAndRelationPersonDTO = new CustomerAndRelationPersonDTO();
    CustomerDTO customerDTO = new IndividualCustomerDTO();
    customerDTO.setRefCustomerId(Long.valueOf(123));
    customerDTO.setCustomerType(CustomerType.RB.name());
    customerAndRelationPersonDTO.setCustomer(customerDTO);
    Set<CustomerDTO> customers = new HashSet<>();
    customerAndRelationPersonDTO.setEnterpriseRelations(customers);
    request1.setCustomerAndRelationPerson(customerAndRelationPersonDTO);
    applicationEntity.setProcessingRole(ProcessingRole.PD_RB_RM.name());
    applicationEntity.setAssignee("nhant");

    UserManagerRegionArea.DataResponse regionAreaResp = new UserManagerRegionArea.DataResponse();
    regionAreaResp.setCreatedFullName("a");
    regionAreaResp.setRegionDetail(new OrganizationTreeDetail().withCode("1").withName("1"));
    regionAreaResp.setUserCode("a");
    regionAreaResp.setCreatedPhoneNumber("0");
    regionAreaResp.setSpecializedBank("a");

    when(applicationRepository.findByBpmId(any())).thenReturn(Optional.of(applicationEntity));
    when(userManagerClient.getRegionAreaByUserName("nhant")).thenReturn(regionAreaResp);
    // happy Case
    Assertions.assertDoesNotThrow(
            () -> postInitializeInfoService.execute(request1, "151-00001111"));

    // Case organization of user is invalid
    when(userManagerClient.getRegionAreaByUserName("nhant")).thenReturn(null);
    Assertions.assertThrows(ApprovalException.class,
            () -> postInitializeInfoService.execute(request1, "151-00001111"));
  }

  @Test
  void  test_mapping_enterprise() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenReturn("");
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getName()).thenReturn("nhant");
    SecurityContextHolder securityContextHolder = Mockito.mock(SecurityContextHolder.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    Mockito.when(securityContextUtil.getAuthentication()).thenReturn(authentication);


    PostInitializeInfoRequest request1 = new PostInitializeInfoRequest();
    ApplicationDTO applicationDTO = new ApplicationDTO();
    request1.setApplication(applicationDTO);
    applicationDTO.setBusinessCode("");
    applicationDTO.setProcessingRole(ProcessingRole.PD_RB_RM.name());
    applicationDTO.setBpmId("151-00001111");
    applicationDTO.setStatus(AS0000.getValue());
    applicationDTO.setAssignee("nhant");
    CustomerAndRelationPersonDTO customerAndRelationPersonDTO = new CustomerAndRelationPersonDTO();
    CustomerDTO customerDTO = new IndividualCustomerDTO();
    customerDTO.setRefCustomerId(Long.valueOf(123));
    customerDTO.setCustomerType(CustomerType.RB.name());
    customerAndRelationPersonDTO.setCustomer(customerDTO);
    Set<CustomerDTO> customers = new HashSet<>();
    customerAndRelationPersonDTO.setEnterpriseRelations(customers);
    request1.setCustomerAndRelationPerson(customerAndRelationPersonDTO);
    applicationEntity.setProcessingRole(ProcessingRole.PD_RB_RM.name());
    applicationEntity.setAssignee("nhant");

    UserManagerRegionArea.DataResponse regionAreaResp = new UserManagerRegionArea.DataResponse();
    regionAreaResp.setCreatedFullName("a");
    regionAreaResp.setRegionDetail(new OrganizationTreeDetail().withCode("1").withName("1"));
    regionAreaResp.setUserCode("a");
    regionAreaResp.setCreatedPhoneNumber("0");
    regionAreaResp.setSpecializedBank("a");

    when(applicationRepository.findByBpmId(any())).thenReturn(Optional.of(applicationEntity));
    when(userManagerClient.getRegionAreaByUserName("nhant")).thenReturn(regionAreaResp);
    // happy Case
    CustomerBaseResponse<SearchCustomerV3Response> response = new CustomerBaseResponse<>();
    response.setCode("200");
    SearchCustomerV3Response customerV3Response = new SearchCustomerV3Response();
    List<SearchCustomerV3Response.CustomersEBResponse> ebResponses = new ArrayList<>();
    SearchCustomerV3Response.CustomersEBResponse ebResponse = new SearchCustomerV3Response.CustomersEBResponse();
    SearchCustomerV3Response.CustomerEbRes cus = new SearchCustomerV3Response.CustomerEbRes();
    cus.setId(1L);
    cus.setVersion(1);
    cus.setIssuedBy("HN");
    cus.setIssuedDate(LocalDate.now());
    cus.setIssuedPlace("HN");
    cus.setCustomerType("EB");
    cus.setActive(Boolean.TRUE);
    ebResponse.setCustomer(cus);
    Set<SearchCustomerV3Response.IdentityDocumentsEbRes> identityDocumentsEbRes = new HashSet<>();
    SearchCustomerV3Response.IdentityDocumentsEbRes identityDocumentsEbRes1 = new SearchCustomerV3Response.IdentityDocumentsEbRes();
    identityDocumentsEbRes1.setId(1L);
    identityDocumentsEbRes1.setIdentityNumber("11111");
    identityDocumentsEbRes1.setPrimary(Boolean.TRUE);
    identityDocumentsEbRes1.setType("BR");
    identityDocumentsEbRes1.setIssuedDate(LocalDate.now());
    identityDocumentsEbRes1.setIssuedBy("HN");
    identityDocumentsEbRes.add(identityDocumentsEbRes1);
    ebResponse.setIdentityDocuments(identityDocumentsEbRes);
    ebResponse.setAddresses(new HashSet<>());
    ebResponses.add(ebResponse);
    customerV3Response.setCustomers(ebResponses);
    response.setData(customerV3Response);

    Set<CustomerDTO> enterpriseRelations = new HashSet<>();
    CustomerDTO enterpriseRelationDTO = new EnterpriseCustomerDTO();
    enterpriseRelationDTO.setCustomerType(CustomerType.EB.name());
    enterpriseRelationDTO.setRefCustomerId(1L);
    enterpriseRelationDTO.setVersion(1);
    enterpriseRelationDTO.setIssuedPlace("HN");
    enterpriseRelationDTO.setIssuedAt(LocalDate.now());
    enterpriseRelationDTO.setIssuedBy("CA HN");
    enterpriseRelationDTO.setRefCusId("1");
    enterpriseRelations.add(enterpriseRelationDTO);
    request1.getCustomerAndRelationPerson().setEnterpriseRelations(enterpriseRelations);
    when( customerClient.findByListCustomerCommon(any(FindByListCustomerRequest.class))).thenReturn(response);
    Assertions.assertThrows(NullPointerException.class,
            () -> postInitializeInfoService.execute(request1, "151-00001111"));

  }
}
