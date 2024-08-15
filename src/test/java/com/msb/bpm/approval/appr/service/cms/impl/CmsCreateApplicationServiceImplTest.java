package com.msb.bpm.approval.appr.service.cms.impl;

import static com.msb.bpm.approval.appr.config.LandingPageConfig.CJMHOME_SOURCE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationDraftStatus.FINISHED;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.CMS_CREATE_APPLICATION;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_FORMAT;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS9999;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
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
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.SUBMISSION_PURPOSE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msb.bpm.approval.appr.client.asset.AssetClient;
import com.msb.bpm.approval.appr.client.configuration.ConfigurationListClient;
import com.msb.bpm.approval.appr.client.configuration.MercuryClient;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.customer.response.CusRelationForSearchResponse;
import com.msb.bpm.approval.appr.client.customer.response.CustomerRelationResponse;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.UserManagementClient;
import com.msb.bpm.approval.appr.enums.common.LdpStatus;
import com.msb.bpm.approval.appr.model.dto.DebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.FieldInforDTO;
import com.msb.bpm.approval.appr.model.dto.InitializeInfoDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationContactDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.EnterpriseCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.OtherIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.PropertyBusinessIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.customer.CommonCustomerRequest;
import com.msb.bpm.approval.appr.model.response.cms.CmsBaseResponse;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryConditionResponse;
import com.msb.bpm.approval.appr.model.response.configuration.ConfigurationBaseResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse;
import com.msb.bpm.approval.appr.model.response.configuration.GetListResponse.Detail;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse.Value;
import com.msb.bpm.approval.appr.model.response.customer.CreateRBCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse.CustomersRBResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse.CustomersRBResponse.CustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.IdentityDocumentResponse;
import com.msb.bpm.approval.appr.model.response.customer.RBCustomerDetailResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerResponse.Customer;
import com.msb.bpm.approval.appr.model.response.customer.UpdateRBCustomerResponse;
import com.msb.bpm.approval.appr.model.response.customereb.CustomerEbResponse;
import com.msb.bpm.approval.appr.model.response.customereb.CustomerEbResponse.CustomerEb;
import com.msb.bpm.approval.appr.model.response.customereb.CustomerEbResponse.IdentityDocumentEbResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.OrganizationTreeDetail;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea.DataResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRelationshipRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.cache.MercuryConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 20/8/2023, Sunday
 **/
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CmsCreateApplicationServiceImplTest {

  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private CustomerRelationshipRepository customerRelationshipRepository;
  @Mock
  private CommonService commonService;
  @Mock
  private UserManagerClient userManagerClient;
  @Mock
  private UserManagementClient userManagementClient;
  @Mock
  private CustomerClient customerClient;
  @Mock
  private ConfigurationServiceCache configurationServiceCache;
  @Mock
  private MercuryConfigurationServiceCache mercuryCache;
  @Mock
  private ChecklistService checklistService;

  @InjectMocks
  private CmsCreateApplicationServiceImpl cmsCreateApplicationService;

  @Mock
  MercuryClient mercuryClient;

  @Mock
  private AssetClient assetClient;

  private Map<String, List<Detail>> categoryMap;
  private MercuryDataResponse city;
  private MercuryDataResponse district;
  private MercuryDataResponse ward;

  private PostCmsV2CreateApplicationRequest request;
  private ApplicationEntity applicationEntity;
  private CustomerEntity customerEntity;
  private CustomerBaseResponse<CusRelationForSearchResponse> cusRelationForSearchResponse;
  private ConfigurationBaseResponse<CategoryConditionResponse> categoriesConditionResponse;
  private CustomerBaseResponse<CustomerRelationResponse> customerRelationResponse;
  private CmsBaseResponse response;
  private CustomerBaseResponse<CustomersResponse> cusInfo;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private MessageSource messageSource;
  @Mock
  private ConfigurationListClient configurationListClient;
  @Mock
  private VerifyService verifyService;
  @Mock
  private MercuryConfigurationServiceCache mercuryConfigurationServiceCache;
  @Mock
  private Validator validator;
  private static String pathSourceFile = "src/test/resources/cms_create_application/";

  @BeforeEach
  public void setup() throws IOException {
    categoryMap = new HashMap<>();
    categoryMap.put(GENDER.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(MARTIAL_STATUS.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(NATIONAL.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(DOCUMENT_TYPE.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(RB_ADDRESS_TYPE_V001.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(RB_ADDRESS_TYPE_V002.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(EB_ADDRESS_TYPE_V001.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(ISSUE_BY.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(PROCESSING_STEP.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(PROCESS_FLOW.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(SUBMISSION_PURPOSE.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(RELATIONSHIP.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(CARD_TYPE.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(AUTO_DEDUCT_RATE.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(CARD_FORM.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(CARD_RECEIVE_ADDRESS.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(LOAN_PURPOSE.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(CONVERSION_METHOD.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(PAY_TYPE.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(LABOR_TYPE.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(DETAIL_INCOME_OTHER.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(INCOME_TYPE.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(CREDIT_TYPE.getCode(), Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(GUARANTEE_FORM.getCode(),
        Collections.singletonList(new Detail("data", "data")));
    categoryMap.put(CREDIT_FORM.getCode(), Collections.singletonList(new Detail("data", "data")));

    city = new MercuryDataResponse();
    List<Value> values = new ArrayList<>();
    values.add(new Value("data", "data", "data"));
    city.setValue(values);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(javaTimeModule());

    request = JsonUtil.convertString2Object(
        "{\"application\":{\"refId\":\"0a36ce26-3f21-11ee-9faf-0242ac120002\",\"source\":\"CJMHome\",\"createdBy\":\"thietpt\",\"segment\":\"V003\",\"bpmId\":\"151-00005308\"},\"customer\":{\"cifNo\":\"CIF_MANHNV8\",\"refCusId\":\"0e58e013-3f21-11ee-9faf-0242ac120002\",\"customerType\":\"RB\",\"typeOfCustomer\":\"V001\",\"fullName\":\"Nguyễn Văn Mạnh\",\"gender\":\"M\",\"dateOfBirth\":\"06/03/1994\",\"martialStatus\":1,\"national\":\"VN\",\"phoneNumber\":\"0397203893\",\"email\":\"vanmanh6394@gmail.com\",\"identities\":[{\"refIdentityId\":\"ldpIdentityId_100\",\"priority\":true,\"documentType\":\"CMND\",\"identifierNumber\":\"163332485\",\"issuedAt\":\"29/03/2012\",\"issuedBy\":\"V001\",\"issuedPlace\":\"24\"}],\"addresses\":[{\"refAddressId\":\"ldpAddressId_100\",\"addressType\":\"V002\",\"cityCode\":\"40\",\"districtCode\":\"746\",\"wardCode\":\"11730\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"}]},\"customerRelations\":[{\"cifNo\":\"CIF_MANHNV8_REL\",\"refCusId\":\"4519bfd6-3f21-11ee-9faf-0242ac120002\",\"customerType\":\"RB\",\"typeOfCustomer\":\"V001\",\"fullName\":\"Nguyễn Văn A\",\"gender\":\"M\",\"dateOfBirth\":\"06/03/1994\",\"martialStatus\":1,\"national\":\"VN\",\"phoneNumber\":\"0463251236\",\"email\":\"a@gmail.com\",\"relationship\":\"V012\",\"identities\":[{\"refIdentityId\":\"656cfc78-3f21-11ee-9faf-0242ac120002\",\"priority\":true,\"documentType\":\"IDC\",\"identifierNumber\":\"1325123548654\",\"issuedAt\":\"29/03/2012\",\"issuedBy\":\"V001\",\"issuedPlace\":\"24\"}],\"addresses\":[{\"refAddressId\":\"6b995326-3f21-11ee-9faf-0242ac120002\",\"addressType\":\"V002\",\"cityCode\":\"40\",\"districtCode\":\"746\",\"wardCode\":\"11730\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình aaaaa\"}]}],\"applicationIncomes\":[{\"refIncomeId\":\"e7ba9dfa-3f4e-11ee-9faf-0242ac120002\",\"incomeMethod\":\"EXCHANGE\",\"recognizedIncome\":40000000,\"incomeItems\":[{\"refIncomeItemId\":\"ebab45f2-3f4e-11ee-9faf-0242ac120002\",\"refCusId\":\"0e58e013-3f21-11ee-9faf-0242ac120002\",\"incomeType\":\"V001\",\"incomeOwner\":\"V017\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"companyName\":\"MSB\",\"position\":\"Chuyen vien cao cap lap trinh\",\"payType\":\"V002\",\"laborType\":\"\",\"cityCode\":\"\",\"districtCode\":\"\",\"wardCode\":\"\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"}]},{\"refIncomeId\":\"88bb7abd-3f21-11ee-9faf-0242ac120002\",\"incomeMethod\":\"ACTUALLY\",\"recognizedIncome\":40000000,\"incomeItems\":[{\"refIncomeItemId\":\"af6bd22d-3f21-11ee-9faf-0242ac120002\",\"refCusId\":\"0e58e013-3f21-11ee-9faf-0242ac120002\",\"incomeType\":\"V001\",\"incomeOwner\":\"V017\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"companyName\":\"MSB\",\"position\":\"Chuyen vien cao cap lap trinh\",\"payType\":\"V002\",\"laborType\":\"V002\",\"cityCode\":\"40\",\"districtCode\":\"746\",\"wardCode\":\"11730\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"}]},{\"refIncomeId\":\"89fb718b-3f2e-11ee-9faf-0242ac120002\",\"incomeMethod\":\"ACTUALLY\",\"recognizedIncome\":50000000,\"incomeItems\":[{\"refIncomeItemId\":\"92f39280-3f2e-11ee-9faf-0242ac120002\",\"refCusId\":\"0e58e013-3f21-11ee-9faf-0242ac120002\",\"incomeType\":\"V003\",\"incomeOwner\":\"V017\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"businessRegistrationNumber\":\"827382781\",\"companyName\":\"SBDDD\",\"mainBusinessSector\":\"aksdjlkajskldjasd\",\"cityCode\":\"40\",\"districtCode\":\"746\",\"wardCode\":\"11730\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"}]},{\"refIncomeId\":\"b91ef0a7-3f2e-11ee-9faf-0242ac120002\",\"incomeMethod\":\"ACTUALLY\",\"recognizedIncome\":60000000,\"incomeItems\":[{\"refIncomeItemId\":\"bd1d6a3c-3f2e-11ee-9faf-0242ac120002\",\"refCusId\":\"0e58e013-3f21-11ee-9faf-0242ac120002\",\"incomeType\":\"V004\",\"incomeOwner\":\"V017\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"businessRegistrationNumber\":\"827382781\",\"companyName\":\"SBDDD\",\"mainBusinessSector\":\"aksdjlkajskldjasd\",\"capitalContributionRate\":0.5,\"cityCode\":\"40\",\"districtCode\":\"746\",\"wardCode\":\"11730\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"}]},{\"refIncomeId\":\"c790fcb9-3f2e-11ee-9faf-0242ac120002\",\"incomeMethod\":\"ACTUALLY\",\"recognizedIncome\":10000000,\"incomeItems\":[{\"refIncomeItemId\":\"cb14581d-3f2e-11ee-9faf-0242ac120002\",\"refCusId\":\"0e58e013-3f21-11ee-9faf-0242ac120002\",\"incomeType\":\"V005\",\"incomeOwner\":\"V017\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"incomeDetail\":\"V005\",\"incomeInfo\":\"ABCDEFGHJK\"}]},{\"refIncomeId\":\"659039ef-418e-11ee-9faf-0242ac120002\",\"incomeMethod\":\"ACTUALLY\",\"recognizedIncome\":10000000,\"incomeItems\":[{\"refIncomeItemId\":\"6a6f803e-418e-11ee-9faf-0242ac120002\",\"refCusId\":\"0e58e013-3f21-11ee-9faf-0242ac120002\",\"incomeType\":\"V006\",\"incomeOwner\":\"V017\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\"}]},{\"refIncomeId\":\"a169ad1d-45c8-11ee-9faf-0242ac120002\",\"incomeMethod\":\"ACTUALLY\",\"recognizedIncome\":10000000,\"incomeItems\":[{\"refIncomeItemId\":\"a70c3ba3-45c8-11ee-9faf-0242ac120002\",\"refCusId\":\"0e58e013-3f21-11ee-9faf-0242ac120002\",\"incomeType\":\"V002\",\"incomeOwner\":\"V017\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"assetType\":\"V001\",\"assetOwner\":\"Nguyen Van C\",\"rentalPurpose\":\"V001\",\"assetAddress\":\"\",\"renter\":\"\",\"renterPhone\":\"\",\"renterPhone\":10000000}]}],\"applicationCredits\":[{\"refCreditId\":\"f2cd385f-3f2e-11ee-9faf-0242ac120002\",\"creditType\":\"V001\",\"guaranteeForm\":\"V002\",\"loanAmount\":500000000,\"loanPurpose\":\"V001\",\"creditForm\":\"V001\",\"totalCapital\":600000000,\"loanPeriod\":12},{\"refCreditId\":\"263ccdbd-3f2f-11ee-9faf-0242ac120002\",\"creditType\":\"V002\",\"guaranteeForm\":\"V002\",\"loanAmount\":100000000,\"limitSustentivePeriod\":3,\"loanPurpose\":\"V001\"},{\"refCreditId\":\"4f659897-3f2f-11ee-9faf-0242ac120002\",\"creditType\":\"V003\",\"guaranteeForm\":\"V002\",\"loanAmount\":560000000,\"cardType\":\"MCDS\",\"cardName\":\"NGUYEN VAN MANH\",\"secretQuestion\":\"Who is Batman?\",\"autoDeductRate\":\"V002\",\"deductAccountNumber\":\"172638761278647\",\"email\":\"vanmanh6394@gmail.com\",\"cardForm\":\"V001\",\"cardReceiveAddress\":\"V002\",\"cityCode\":\"40\",\"districtCode\":\"746\",\"wardCode\":\"11730\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"},{\"refCreditId\":\"4525f6e2-3f4f-11ee-9faf-0242ac120002\",\"creditType\":\"V003\",\"guaranteeForm\":\"V002\",\"loanAmount\":560000000,\"cardType\":\"\",\"cardName\":\"NGUYEN VAN MANH\",\"secretQuestion\":\"Who is Batman?\",\"autoDeductRate\":\"\",\"deductAccountNumber\":\"172638761278647\",\"email\":\"vanmanh6394@gmail.com\",\"cardForm\":\"\",\"cardReceiveAddress\":\"\",\"cityCode\":\"\",\"districtCode\":\"\",\"wardCode\":\"\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"}]}",
        PostCmsV2CreateApplicationRequest.class, objectMapper);
    // Set CustomersResponse data
    cusInfo = new CustomerBaseResponse<>();
    CustomersResponse cusResponse = new CustomersResponse();
    List<CustomersRBResponse> customersRBResponses = new ArrayList<>();
    CustomersRBResponse customersRBResponse = new CustomersRBResponse();
    CustomerResponse customeResponse = new CustomerResponse();
    customeResponse.setId(1L);
    customeResponse.setBpmCif("test-bpm-cif");
    customersRBResponse.setCustomer(customeResponse);
    customersRBResponses.add(customersRBResponse);
    cusResponse.setCustomersRb(customersRBResponses);
    cusInfo.setData(cusResponse);
    init(objectMapper);
  }
  private void init(ObjectMapper objectMapper) throws IOException {
    applicationEntity = objectMapper.readValue(
        new File(pathSourceFile + "application.json"), ApplicationEntity.class);
    customerEntity = objectMapper.readValue(
        new File(pathSourceFile + "customer.json"), CustomerEntity.class);
    cusRelationForSearchResponse = objectMapper.readValue(
        new File(pathSourceFile + "cus_relation_for_search_response.json"),
        new TypeReference<CustomerBaseResponse<CusRelationForSearchResponse>>() {
        });
    categoriesConditionResponse = objectMapper.readValue(
        new File(pathSourceFile + "category_condition_response.json"),
        new TypeReference<ConfigurationBaseResponse<CategoryConditionResponse>>() {
        });
    customerRelationResponse = objectMapper.readValue(
        new File(pathSourceFile + "customer_relation.json"),
        new TypeReference<CustomerBaseResponse<CustomerRelationResponse>>() {
        });
  }

  @Test
  void test_execute_successful() {
    //Set CustomerBaseResponse data
    CustomerBaseResponse<CreateRBCustomerResponse> response = new CustomerBaseResponse<>();
    CreateRBCustomerResponse response2 = new CreateRBCustomerResponse();
    RBCustomerDetailResponse customerDetailResponse = new RBCustomerDetailResponse();
    customerDetailResponse.setId(1L);
    customerDetailResponse.setBpmCif("test-bm-cif");

    response.setData(response2);
    when(customerClient.createCustomer(any())).thenReturn(response);
    // Set GetListResponse data
    GetListResponse getListResponse = new GetListResponse();
    Map<String, List<Detail>> map = new HashMap<>();

    List<Detail> details = new ArrayList<>();
    Detail detail = new Detail();
    detail.setCode(CJMHOME_SOURCE);
    detail.setValue("thietpt");
    details.add(detail);

    detail = new Detail();
    detail.setCode("MODE");
    detail.setValue("ENABLE");
    details.add(detail);

    map.put(PILOT_SOURCE_MAPPING.name(), details);
    getListResponse.setValue(map);
    when(configurationListClient.findByListCategoryDataCodes(any(), any())).thenReturn(
        getListResponse);
    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.execute(request, "151-00006116"));
//    CmsBaseResponse cmsBaseResponse = cmsCreateApplicationService.execute(request, "151-00006116");
//    assertNotNull(cmsBaseResponse);
  }

  @Test
  void test_getType_return_expected_data() {
    assertEquals(CMS_CREATE_APPLICATION, cmsCreateApplicationService.getType());
  }

  @Test
  void test_saveOrUpdateIncomes_successful() {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    ApplicationIncomeEntity incomeEntity = new ApplicationIncomeEntity();
    incomeEntity.setId(1l);
    incomeEntity.setLdpIncomeId("88bb7abd-3f21-11ee-9faf-0242ac120002");
    SalaryIncomeEntity salaryIncomeEntity = new SalaryIncomeEntity();
    salaryIncomeEntity.setId(1l);
    salaryIncomeEntity.setLdpSalaryId("af6bd22d-3f21-11ee-9faf-0242ac120002");
    incomeEntity.getSalaryIncomes().add(salaryIncomeEntity);
    applicationEntity.getIncomes().add(incomeEntity);

    incomeEntity = new ApplicationIncomeEntity();
    incomeEntity.setId(2l);
    incomeEntity.setLdpIncomeId("89fb718b-3f2e-11ee-9faf-0242ac120002");
    IndividualEnterpriseIncomeEntity businessEntity = new IndividualEnterpriseIncomeEntity();
    businessEntity.setId(2l);
    businessEntity.setLdpBusinessId("92f39280-3f2e-11ee-9faf-0242ac120002");
    incomeEntity.getIndividualEnterpriseIncomes().add(businessEntity);
    applicationEntity.getIncomes().add(incomeEntity);

    incomeEntity = new ApplicationIncomeEntity();
    incomeEntity.setId(3l);
    incomeEntity.setLdpIncomeId("c790fcb9-3f2e-11ee-9faf-0242ac120002");
    OtherIncomeEntity otherIncomeEntity = new OtherIncomeEntity();
    otherIncomeEntity.setId(3l);
    otherIncomeEntity.setLdpOtherId("cb14581d-3f2e-11ee-9faf-0242ac120002");
    incomeEntity.getOtherIncomes().add(otherIncomeEntity);
    applicationEntity.getIncomes().add(incomeEntity);

    incomeEntity = new ApplicationIncomeEntity();
    incomeEntity.setId(4l);
    incomeEntity.setLdpIncomeId("659039ef-418e-11ee-9faf-0242ac120002");
    PropertyBusinessIncomeEntity propertyBusinessIncome = new PropertyBusinessIncomeEntity();
    propertyBusinessIncome.setId(3l);
    propertyBusinessIncome.setLdpPropertyBusinessId("6a6f803e-418e-11ee-9faf-0242ac120002");
    incomeEntity.getPropertyBusinessIncomes().add(propertyBusinessIncome);
    applicationEntity.getIncomes().add(incomeEntity);

    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.saveOrUpdateIncomes(request.getApplicationIncomes(),
            applicationEntity));
  }

  @Test
  void test_saveOrUpdateCredit_successful() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setCustomer(new CustomerEntity());
    applicationEntity.getCustomer()
        .setIndividualCustomer(new IndividualCustomerEntity().withEmail("email@gmail.com"));

    ApplicationCreditEntity creditEntity = new ApplicationCreditEntity();
    creditEntity.setId(1L);
    creditEntity.setLdpCreditId("f2cd385f-3f2e-11ee-9faf-0242ac120002");

    ApplicationCreditLoanEntity loanEntity = new ApplicationCreditLoanEntity();
    loanEntity.setId(2L);
    loanEntity.setLdpLoanId("f2cd385f-3f2e-11ee-9faf-0242ac120002");
    creditEntity.setCreditLoan(loanEntity);

    applicationEntity.getCredits().add(creditEntity);

    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.saveOrUpdateCredit(request.getApplicationCredits(),
            applicationEntity));
  }

  public JavaTimeModule javaTimeModule() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS_FORMAT);
    LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
    LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter);
    javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
    javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer);

    formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT);
    LocalDateDeserializer dateDeserializer = new LocalDateDeserializer(formatter);
    LocalDateSerializer dateSerializer = new LocalDateSerializer(formatter);
    javaTimeModule.addDeserializer(LocalDate.class, dateDeserializer);
    javaTimeModule.addSerializer(LocalDate.class, dateSerializer);

    return javaTimeModule;
  }

  @Test
  void test_addOrUpdateCustomer_successful() {
    Set<CustomerAddressEntity> setOfAddress = new HashSet<>();
    Set<CustomerIdentityEntity> setOfIdentities = new HashSet<>();
    ApplicationEntity applicationEntity = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    CustomerAddressEntity address = new CustomerAddressEntity();
    CustomerIdentityEntity identity = new CustomerIdentityEntity();

    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    customerEntity.setIndividualCustomer(individualCustomer);
    applicationEntity.setCustomer(customerEntity);
    applicationEntity.getCustomer().addCustomerAddresses(setOfAddress);
    applicationEntity.getCustomer().addCustomerIdentities(setOfIdentities);
    setOfIdentities.add(identity);
    setOfAddress.add(address);

    customerEntity.setRefCusId("refCusId_105");
    customerEntity.setId(1L);

    address.setLdpAddressId("refCusAddress_100");
    identity.setLdpIdentityId("refCusIdentity_100");

    // Set UpdateRBCustomerResponse data
    CustomerBaseResponse<UpdateRBCustomerResponse> res = new CustomerBaseResponse<>();
    UpdateRBCustomerResponse updateRBCustomerResponse = new UpdateRBCustomerResponse();
    RBCustomerDetailResponse rbCustomerDetailResponse = new RBCustomerDetailResponse();
    rbCustomerDetailResponse.setBpmCif("test-bpm-cif");
    rbCustomerDetailResponse.setId(1L);
    res.setData(updateRBCustomerResponse);
    when(customerClient.updateCustomer(any())).thenReturn(res);

    when(customerClient.searchCustomerByList(any())).thenReturn(cusInfo);

    test_updateAddressAndIdentity_successful();
    test_checkCustomer_successful();
    Map<String, CustomerEntity> customerEntityMap = new HashMap<>();
    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.addOrUpdateCustomer(request, applicationEntity,
            customerEntityMap));
  }

  @Test
  void test_addOrUpdateRelationShip_successful() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    Set<CustomerAddressEntity> setOfAddress = new HashSet<>();
    Set<CustomerIdentityEntity> setOfIdentities = new HashSet<>();
    MercuryDataResponse mercuryDataResponse = new MercuryDataResponse();
    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    CustomerRelationShipEntity customerRelationShipEntity = new CustomerRelationShipEntity();

    applicationEntity.setCustomer(customerEntity);
    customerEntity.setId(1L);

    customerEntity.setIndividualCustomer(individualCustomer);
    applicationEntity.setCustomer(customerEntity);
    applicationEntity.getCustomer().addCustomerAddresses(setOfAddress);
    applicationEntity.getCustomer().addCustomerIdentities(setOfIdentities);

    CustomerBaseResponse<SearchCustomerResponse> cusInfor = new CustomerBaseResponse<>();
    SearchCustomerResponse data = new SearchCustomerResponse();
    data.setCustomers(new Customer());
    cusInfor.setData(data);

    Set<CustomerEntity> currentCusList = new HashSet<>();
    CustomerEntity saveCustomer = new CustomerEntity();
    saveCustomer.setId(2L);
    saveCustomer.setRefCusId("4519bfd6-3f21-11ee-9faf-0242ac120002");
    saveCustomer.setCustomerType("RB");
    saveCustomer.setIndividualCustomer(individualCustomer);
    currentCusList.add(saveCustomer);

    CustomerAddressEntity address = new CustomerAddressEntity();
    CustomerIdentityEntity identity = new CustomerIdentityEntity();
    setOfAddress.add(address);
    address.setId(1L);
    address.setLdpAddressId("995326-3f21-11ee-9faf-0242ac120002");
    setOfIdentities.add(identity);
    identity.setId(1L);
    identity.setLdpIdentityId("656cfc78-3f21-11ee-9faf-0242ac120002");
    saveCustomer.setCustomerAddresses(setOfAddress);
    saveCustomer.setCustomerIdentitys(setOfIdentities);

    Set<CustomerEntity> customerRelationEntities = new HashSet<>();
    // Set UpdateRBCustomerResponse data
    CustomerBaseResponse<UpdateRBCustomerResponse> res = new CustomerBaseResponse<>();
    UpdateRBCustomerResponse updateRBCustomerResponse = new UpdateRBCustomerResponse();
    RBCustomerDetailResponse rbCustomerDetailResponse = new RBCustomerDetailResponse();
    rbCustomerDetailResponse.setBpmCif("test-bpm-cif");
    rbCustomerDetailResponse.setId(1L);
    res.setData(updateRBCustomerResponse);

    when(customerClient.searchCustomerByList(any())).thenReturn(cusInfo);
    when(customerClient.updateCustomer(any())).thenReturn(res);
//    when(customerRepository.save(any())).thenReturn(saveCustomer);
    when(customerRepository.findByIdIn(any())).thenReturn(Optional.of(currentCusList));
    when(customerClient.searchCustomer(any())).thenReturn(cusInfor);
//    when(mercuryClient.searchPlace(any())).thenReturn(mercuryDataResponse);
//    when(mercuryClient.searchPlace(any())).thenReturn(mercuryDataResponse);

    Map<String, CustomerEntity> customerEntityMap = new HashMap<>();

    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.addOrUpdateRelationShip(request, applicationEntity,
            customerEntityMap));
  }

  @Test
  void test_updateAddressAndIdentity_successful() {
    Set<CustomerAddressEntity> setOfAddress = new HashSet<>();
    Set<CustomerIdentityEntity> setOfIdentities = new HashSet<>();
    CustomerAddressEntity address = new CustomerAddressEntity();
    CustomerIdentityEntity identity = new CustomerIdentityEntity();
    setOfAddress.add(address);
    address.setId(1L);
    address.setLdpAddressId("ldpAddressId_100");
    setOfIdentities.add(identity);
    identity.setId(1L);
    identity.setLdpIdentityId("ldpIdentityId_100");
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setCustomerAddresses(setOfAddress);
    customerEntity.setCustomerIdentitys(setOfIdentities);
    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.updateAddressAndIdentity(customerEntity, request));
  }

  @Test
  void test_checkCustomer_successful() {
    CustomerEntity customerEntity = new CustomerEntity();
    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    customerEntity.setIndividualCustomer(individualCustomer);
    CustomerBaseResponse<SearchCustomerResponse> cusInfor = new CustomerBaseResponse<>();
    CmsIndividualCustomerDTO individualCustomerDTO = (CmsIndividualCustomerDTO) request.getCustomer();
    SearchCustomerResponse data = new SearchCustomerResponse();
    Customer customer = new Customer();
    customer.setId(1L);
    customer.setCif("test-cif");
    customer.setBpmCif("test-bpm-cif");
    data.setCustomers(customer);
    cusInfor.setData(data);
    // Set UpdateRBCustomerResponse data
    CustomerBaseResponse<UpdateRBCustomerResponse> res = new CustomerBaseResponse<>();
    UpdateRBCustomerResponse updateRBCustomerResponse = new UpdateRBCustomerResponse();
    RBCustomerDetailResponse rbCustomerDetailResponse = new RBCustomerDetailResponse();
    rbCustomerDetailResponse.setBpmCif("test-bpm-cif");
    rbCustomerDetailResponse.setId(1L);
    res.setData(updateRBCustomerResponse);

    when(customerClient.searchCustomerByList(any())).thenReturn(cusInfo);
    when(customerClient.updateCustomer(any())).thenReturn(res);
    when(customerClient.searchCustomer(any())).thenReturn(cusInfor);
    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.updateCustomer(customerEntity, individualCustomerDTO));
  }

  @Test
  void test_createCustomer_successful() {
    CustomerEntity customerEntity = new CustomerEntity();
    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();

    customerEntity.setIndividualCustomer(individualCustomer);
    CustomerIdentityEntity identity = new CustomerIdentityEntity();
    identity.setPriority(true);
    identity.setIdentifierCode("1");
    customerEntity.setCustomerIdentitys(Collections.singleton(identity));

    RBCustomerDetailResponse customer = new RBCustomerDetailResponse();
    CustomerBaseResponse<SearchCustomerResponse> cusInfor = new CustomerBaseResponse<>();
    CmsIndividualCustomerDTO individualCustomerDTO = (CmsIndividualCustomerDTO) request.getCustomer();
    CreateRBCustomerResponse data = new CreateRBCustomerResponse();
    CustomerBaseResponse<CreateRBCustomerResponse> cusInfoNew = new CustomerBaseResponse<>();
    customer.setBpmCif("cifNo");
    customer.setId(1L);
    cusInfor.setData(null);
    cusInfoNew.setData(data);
    cusInfo.getData().getCustomersRb().get(0).setCustomer(null);

    when(customerClient.searchCustomerByList(any())).thenReturn(cusInfo);
    when(customerClient.createCustomer(any(CommonCustomerRequest.class))).thenReturn(cusInfoNew);

    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.updateCustomer(customerEntity, individualCustomerDTO));
  }

  @Test
  void test_checkCustomer_should_throw_exception() {
    CustomerEntity customerEntity = new CustomerEntity();
    CustomerIdentityEntity identity = new CustomerIdentityEntity();
    identity.setPriority(true);
    identity.setIdentifierCode("1");
    customerEntity.setCustomerIdentitys(Collections.singleton(identity));

    RBCustomerDetailResponse customer = new RBCustomerDetailResponse();
    CustomerBaseResponse<SearchCustomerResponse> cusInfor = new CustomerBaseResponse<>();
    CmsIndividualCustomerDTO individualCustomerDTO = (CmsIndividualCustomerDTO) request.getCustomer();
    CreateRBCustomerResponse data = new CreateRBCustomerResponse();
    CustomerBaseResponse<CreateRBCustomerResponse> cusInfoNew = new CustomerBaseResponse<>();
    customer.setBpmCif("cifNo");
    customer.setId(1L);
    cusInfor.setData(null);
    cusInfoNew.setData(data);

    CustomersRBResponse customersRBResponse = new CustomersRBResponse();
    CustomerResponse customeResponse = new CustomerResponse();
    customeResponse.setId(2L);
    customeResponse.setBpmCif("test-bpm-cif-1");
    cusInfo.getData().getCustomersRb().add(customersRBResponse);
    customersRBResponse.setCustomer(customeResponse);

    when(customerClient.searchCustomerByList(any())).thenReturn(cusInfo);
    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.updateCustomer(customerEntity, individualCustomerDTO));
  }

  @Test
  void test_checkCustomer_throw_exception() {
    CustomerEntity customerEntity = new CustomerEntity();
    CmsIndividualCustomerDTO individualCustomerDTO = new CmsIndividualCustomerDTO();
    individualCustomerDTO.setIdentities(new ArrayList<>());

    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.updateCustomer(customerEntity, individualCustomerDTO));
  }
//  @Test
//  void test_createCustomerRelationShip_successful() {
//    ApplicationEntity applicationEntity = new ApplicationEntity();
//    applicationEntity.setCustomer(new CustomerEntity());
////    CustomerEntity customer = new CustomerEntity();
////    CmsCreateApplicationServiceImpl a = mock(CmsCreateApplicationServiceImpl.class);
////    when(a.persistCustomer(any())).thenReturn(customer);
//    test_checkCustomer_successful();
//    test_createCustomer_successful();
//    Map<String, CustomerEntity> customerEntityMap = new HashMap<>();
//    assertDoesNotThrow(
//        () -> cmsCreateApplicationService.createCustomerRelationShip(request, applicationEntity,
//            customerEntityMap));
//  }

  @Test
  void test_checkCmsApplication_should_created_new() {
    when(applicationRepository.findAppByRefIdAndSource(request.getApplication().getRefId(),
        request.getApplication().getSource())).thenReturn(
        Optional.empty());
    assertNull(cmsCreateApplicationService.checkCmsApplication(request.getApplication().getRefId(),
        request.getApplication().getSource()));
  }

  @Test
  void test_saveApplication_with_bpmId_should_throw_exception_approved() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setStatus(AS9999.getValue());
    applicationEntity.setCreatedAt(LocalDateTime.now());
    when(applicationRepository.findAppByRefIdAndSource(anyString(), anyString()))
        .thenReturn(Optional.of(Collections.singletonList(applicationEntity)));
    response = new CmsBaseResponse();
    GetListResponse getListResponse = new GetListResponse();
    Detail detail = new Detail();
    detail.setValue("PILOT_SOURCE_MAPPING");
    detail.setCode("PILOT_SOURCE_MAPPING");
    List<Detail> details = new ArrayList<>();
    details.add(detail);
    Map<String, List<Detail>> maps = new HashMap<>();
    maps.put("PILOT_SOURCE_MAPPING", details);
    getListResponse.setValue(maps);
    when(configurationListClient.findByListCategoryDataCodes(any(), any())).thenReturn(
        getListResponse);
    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.saveApplication(request, response));
  }

  @Test
  void test_saveApplication_with_bpmId_should_throw_exception_ldpStatus_4000() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setLdpStatus(LdpStatus.CUSTOMER_EDITED.getValue());
    applicationEntity.setCreatedAt(LocalDateTime.now());
    when(applicationRepository.findAppByRefIdAndSource(anyString(), anyString())).thenReturn(
        Optional.of(Collections.singletonList(applicationEntity)));
    response = new CmsBaseResponse();
    GetListResponse getListResponse = new GetListResponse();
    Detail detail = new Detail();
    detail.setValue("PILOT_SOURCE_MAPPING");
    detail.setCode("PILOT_SOURCE_MAPPING");
    List<Detail> details = new ArrayList<>();
    details.add(detail);
    Map<String, List<Detail>> maps = new HashMap<>();
    maps.put("PILOT_SOURCE_MAPPING", details);
    getListResponse.setValue(maps);
    when(configurationListClient.findByListCategoryDataCodes(any(), any())).thenReturn(
        getListResponse);
    assertThrows(Exception.class,
        () -> cmsCreateApplicationService.saveApplication(request, response));
  }

  @Test
  void test_saveOrUpdateEnterpriseRelations_should_remove_all_current_data() {
    Set<CustomerEntity> enterpriseRelationEntities = new HashSet<>();
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setId(1L);
    enterpriseRelationEntities.add(customerEntity);

    CustomerEntity mainCustomer = new CustomerEntity();
    mainCustomer.setId(2L);

    Set<CustomerRelationShipEntity> customerRelationShipEntities = new HashSet<>();
    CustomerRelationShipEntity customerRelationShipEntity = new CustomerRelationShipEntity();
    customerRelationShipEntity.setId(3L);
    customerRelationShipEntity.setCustomer(mainCustomer);
    customerRelationShipEntity.setCustomerRefId(1L);
    customerRelationShipEntities.add(customerRelationShipEntity);

    mainCustomer.setCustomerRelationShips(customerRelationShipEntities);

    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setCustomer(mainCustomer);

    assertDoesNotThrow(() -> cmsCreateApplicationService.saveOrUpdateEnterpriseRelations(null,
        enterpriseRelationEntities, applicationEntity));
  }

  @Test
  void test_saveOrUpdateEnterpriseRelations_should_createOrUpdateData() {
    Set<CustomerEntity> enterpriseRelationEntities = new HashSet<>();
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setId(1L);
    customerEntity.setRefCusId("1");
    customerEntity.setEnterpriseCustomer(new EnterpriseCustomerEntity());
    enterpriseRelationEntities.add(customerEntity);
    customerEntity = new CustomerEntity();
    customerEntity.setId(10L);
    customerEntity.setRefCusId("10");
    customerEntity.setEnterpriseCustomer(new EnterpriseCustomerEntity());
    enterpriseRelationEntities.add(customerEntity);

    CustomerEntity mainCustomer = new CustomerEntity();
    mainCustomer.setId(2L);

    Set<CustomerRelationShipEntity> customerRelationShipEntities = new HashSet<>();
    CustomerRelationShipEntity customerRelationShipEntity = new CustomerRelationShipEntity();
    customerRelationShipEntity.setId(3L);
    customerRelationShipEntity.setCustomer(mainCustomer);
    customerRelationShipEntity.setCustomerRefId(1L);
    customerRelationShipEntities.add(customerRelationShipEntity);
    customerRelationShipEntity = new CustomerRelationShipEntity();
    customerRelationShipEntity.setId(4L);
    customerRelationShipEntity.setCustomer(mainCustomer);
    customerRelationShipEntity.setCustomerRefId(10L);
    customerRelationShipEntities.add(customerRelationShipEntity);

    mainCustomer.setCustomerRelationShips(customerRelationShipEntities);

    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setCustomer(mainCustomer);

    List<CmsEnterpriseRelationDTO> cmsEnterpriseRelations = new ArrayList<>();
    CmsEnterpriseRelationDTO cmsEnterpriseRelationDTO = new CmsEnterpriseRelationDTO();
    cmsEnterpriseRelationDTO.setRefEnterpriseId("11");
    cmsEnterpriseRelationDTO.setBusinessRegistrationNumber("number1");
    cmsEnterpriseRelations.add(cmsEnterpriseRelationDTO);
    cmsEnterpriseRelationDTO = new CmsEnterpriseRelationDTO();
    cmsEnterpriseRelationDTO.setRefEnterpriseId("1");
    cmsEnterpriseRelationDTO.setBusinessRegistrationNumber("number2");
    cmsEnterpriseRelations.add(cmsEnterpriseRelationDTO);

    CustomerBaseResponse<CustomerEbResponse> customerEbResp = new CustomerBaseResponse<>();
    CustomerEbResponse customerEbResponse = new CustomerEbResponse();
    CustomerEb customerEb = new CustomerEb();
    customerEbResponse.setCustomer(customerEb);
    List<IdentityDocumentEbResponse> identityDocumentEbResponses = new ArrayList<>();
    IdentityDocumentEbResponse identityDocument = new IdentityDocumentEbResponse();
    identityDocument.setIdentityNumber("number1");
    identityDocumentEbResponses.add(identityDocument);
    customerEbResponse.setIdentityDocuments(identityDocumentEbResponses);
    customerEbResp.setData(customerEbResponse);
    when(customerClient.createCustomerEb(any())).thenReturn(customerEbResp);

    when(customerRepository.findById(any())).thenReturn(Optional.of(customerEntity));
    assertDoesNotThrow(
        () -> cmsCreateApplicationService.saveOrUpdateEnterpriseRelations(cmsEnterpriseRelations,
            enterpriseRelationEntities, applicationEntity));
  }

  @Test
  void test_saveOrUpdateApplicationContact_should_be_success() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    assertDoesNotThrow(
        () -> cmsCreateApplicationService.saveOrUpdateApplicationContact(null, applicationEntity));

    List<CmsApplicationContactDTO> contactList = new ArrayList<>();
    CmsApplicationContactDTO contactDTO = new CmsApplicationContactDTO();
    contactDTO.setFullName("Nguyen Van A");
    contactDTO.setRelationship("V001");
    contactDTO.setPhoneNumber("123456789");
    contactList.add(contactDTO);
    assertDoesNotThrow(() -> cmsCreateApplicationService.saveOrUpdateApplicationContact(
        contactList, applicationEntity));
  }

  @Test
  void test_buildFieldInformation_should_be_success() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    Set<ApplicationFieldInformationEntity> fieldInformationEntities = cmsCreateApplicationService.buildFieldInformation(
        applicationEntity, request);
    Assertions.assertNotNull(fieldInformationEntities);
  }

  @Test
  void test_saveOrUpdateFieldInformation_should_be_success() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    test_buildFieldInformation_should_be_success();
    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.saveOrUpdateFieldInformation(applicationEntity, request));
  }

  @Test
  void test_setBranchUserRM() {
    CmsBaseResponse response = new CmsBaseResponse();
    ApplicationEntity applicationEntity = new ApplicationEntity();
    DataResponse regionAreaResp = new DataResponse();
    OrganizationTreeDetail regionDetail = new OrganizationTreeDetail();
    List<OrganizationTreeDetail> children = new ArrayList<>();
    regionDetail.setFullName("abc");
    regionDetail.setPhoneNumber("12345");
    OrganizationTreeDetail businessUnitDetail = new OrganizationTreeDetail();
    regionDetail.setChildren(children);
    regionAreaResp.setRegionDetail(regionDetail);

    DataResponse dataResponse = new DataResponse();
    List<OrganizationTreeDetail> businessUnitDetails = new ArrayList<>();
    OrganizationTreeDetail organizationTreeDetail = new OrganizationTreeDetail();
    businessUnitDetails.add(organizationTreeDetail);
    dataResponse.setBusinessUnitDetails(businessUnitDetails);
    dataResponse.setRegionDetails(businessUnitDetails);
    when(userManagerClient.getRegionAreaByUserName(any(), any())).thenReturn(regionAreaResp);
    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.setBranchUserRM(applicationEntity, response,
            dataResponse));

  }

  @Test
  void test_checkInitializeInfo() throws JsonProcessingException {
    ApplicationDraftEntity applicationDraftEntity = new ApplicationDraftEntity();
    applicationDraftEntity.setStatus(FINISHED);
    when(objectMapper.writeValueAsString(any())).thenReturn("123");
    Set<ConstraintViolation<Object>> violations = new HashSet<>();
    when(validator.validate(any())).thenReturn(violations);
    InitializeInfoDTO initializeInfoDTO = new InitializeInfoDTO();
    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.checkInitializeInfo(applicationDraftEntity,
            initializeInfoDTO));
  }

  @Test
  void test_checkFieldInfo() throws JsonProcessingException {
    ApplicationDraftEntity applicationDraftEntity = new ApplicationDraftEntity();
    applicationDraftEntity.setStatus(FINISHED);
    FieldInforDTO fieldInforDTO = new FieldInforDTO();
    when(objectMapper.writeValueAsString(any())).thenReturn("123");
    Set<ConstraintViolation<Object>> violations = new HashSet<>();
    when(validator.validate(any())).thenReturn(violations);
    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.checkFieldInfo(applicationDraftEntity, fieldInforDTO));

  }

  @Test
  void test_checkDebtInfo() throws JsonProcessingException {
    ApplicationDraftEntity applicationDraftEntity = new ApplicationDraftEntity();
    applicationDraftEntity.setStatus(FINISHED);
    DebtInfoDTO debtInfoDTO = new DebtInfoDTO();
    when(objectMapper.writeValueAsString(any())).thenReturn("123");
    Set<ConstraintViolation<Object>> violations = new HashSet<>();
    when(validator.validate(any())).thenReturn(violations);
    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.checkDebtInfo(applicationDraftEntity, debtInfoDTO));
  }

  @Test
  void test_createFirstHistory() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setProcessingRole(PD_RB_RM.name());
    Assertions.assertDoesNotThrow(
        () -> cmsCreateApplicationService.createFirstHistory(applicationEntity));
  }

  @Test
  void test_persistCustomer() {
    CmsIndividualCustomerDTO individualCustomerDTO = (CmsIndividualCustomerDTO) request.getCustomer();
    CustomerBaseResponse<SearchCustomerResponse> response = new CustomerBaseResponse<>();
    SearchCustomerResponse response2 = new SearchCustomerResponse();
    Customer customer = new Customer();
    customer.setCif("test-cif");
    customer.setId(1L);
    customer.setBpmCif("test-bm-cif");

    response2.setCustomers(customer);
    response.setData(response2);
    when(customerClient.searchCustomer(any())).thenReturn(response);

    CustomerBaseResponse<UpdateRBCustomerResponse> updateResponse = new CustomerBaseResponse<>();
    UpdateRBCustomerResponse updateRBCustomerResponse = new UpdateRBCustomerResponse();
    RBCustomerDetailResponse rbCustomerDetailResponse = new RBCustomerDetailResponse();
    rbCustomerDetailResponse.setBirthday(LocalDate.now());
    rbCustomerDetailResponse.setGender("Nam");
    rbCustomerDetailResponse.setNational("VietNam");
    rbCustomerDetailResponse.setActive(true);
    updateRBCustomerResponse.setCustomer(rbCustomerDetailResponse);

    updateResponse.setData(updateRBCustomerResponse);

    List<IdentityDocumentResponse> identityDocuments = new ArrayList<>();
    IdentityDocumentResponse identityDocumentResponse = new IdentityDocumentResponse();
    identityDocumentResponse.setId(1L);
    identityDocumentResponse.setPrimary(true);
    identityDocuments.add(identityDocumentResponse);
    updateRBCustomerResponse.setIdentityDocuments(identityDocuments);
    when(customerClient.searchCustomerByList(any())).thenReturn(cusInfo);
    when(customerClient.updateCustomer(any())).thenReturn(updateResponse);
    CustomerEntity customerEntity = cmsCreateApplicationService.persistCustomer(individualCustomerDTO);
    assertEquals("RB", customerEntity.getCustomerType());
  }
  @Test
  void createCustomerRelationShipTest() {
    Map<String, String> categoryMap = new HashMap<>();
    categoryMap.put("relationship", "R87");
    Map<String, CustomerEntity> customerEntityMap = new HashMap<>();
    customerEntityMap.put("8a5ca57d8b5f87f2018b5fb8d04a001610156", customerEntity);

    Set<CustomerAddressEntity> setOfAddress = new HashSet<>();
    Set<CustomerIdentityEntity> setOfIdentities = new HashSet<>();
    ApplicationEntity applicationEntity = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    CustomerAddressEntity address = new CustomerAddressEntity();
    CustomerIdentityEntity identity = new CustomerIdentityEntity();

    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    customerEntity.setIndividualCustomer(individualCustomer);
    applicationEntity.setCustomer(customerEntity);
    applicationEntity.getCustomer().addCustomerAddresses(setOfAddress);
    applicationEntity.getCustomer().addCustomerIdentities(setOfIdentities);
    setOfIdentities.add(identity);
    setOfAddress.add(address);

    customerEntity.setRefCusId("refCusId_105");
    customerEntity.setId(1L);

    address.setLdpAddressId("refCusAddress_100");
    identity.setLdpIdentityId("refCusIdentity_100");

    // Set UpdateRBCustomerResponse data
    CustomerBaseResponse<UpdateRBCustomerResponse> res = new CustomerBaseResponse<>();
    UpdateRBCustomerResponse updateRBCustomerResponse = new UpdateRBCustomerResponse();
    RBCustomerDetailResponse rbCustomerDetailResponse = new RBCustomerDetailResponse();
    rbCustomerDetailResponse.setBpmCif("test-bpm-cif");
    rbCustomerDetailResponse.setId(1L);
    rbCustomerDetailResponse.setActive(true);
    updateRBCustomerResponse.setCustomer(rbCustomerDetailResponse);

    List<IdentityDocumentResponse> identityDocuments = new ArrayList<>();
    IdentityDocumentResponse identityDocumentResponse = new IdentityDocumentResponse();
    identityDocumentResponse.setPrimary(true);
    identityDocuments.add(identityDocumentResponse);
    updateRBCustomerResponse.setIdentityDocuments(identityDocuments);
    res.setData(updateRBCustomerResponse);
    when(customerClient.updateCustomer(any())).thenReturn(res);

    when(customerClient.searchCustomerByList(any())).thenReturn(cusInfo);
    assertDoesNotThrow(
        () -> cmsCreateApplicationService.createCustomerRelationShip(
            request, applicationEntity, customerEntityMap));
  }
  @Test
  void checkCustomerRelationshipTest() {
    // case create
    when(customerRepository.findById(any())).thenReturn(Optional.of(customerEntity));
    when(customerRepository.searchRefCustomerIdById(any())).thenReturn(Optional.of(1L));
    when(customerClient.searchCustomerRelationship(any())).thenReturn(cusRelationForSearchResponse);
    when(configurationListClient.findCategoryCondition(any())).thenReturn(categoriesConditionResponse);
    when(customerClient.createCustomerRelationship(any())).thenReturn(customerRelationResponse);
    when(customerClient.updateCustomerRelationship(any())).thenReturn(customerRelationResponse);
    assertDoesNotThrow(
    () -> cmsCreateApplicationService.checkCustomerRelationship(applicationEntity));
    // case update
    cusRelationForSearchResponse.getData().getRelationships().get(0).getRelationshipDetails().get(0).setRelatedDetail("R6");
    categoriesConditionResponse.getData().getCategory().get(0).getConditionData().get(0).setConditionCategoryDataCode("FAMILY");
    when(customerClient.searchCustomerRelationship(any())).thenReturn(cusRelationForSearchResponse);
    when(customerClient.updateCustomerRelationship(any())).thenReturn(customerRelationResponse);
    assertDoesNotThrow(
        () -> cmsCreateApplicationService.checkCustomerRelationship(applicationEntity));
  }
}
