package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREATE_APPLICATION;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msb.bpm.approval.appr.client.camunda.CamundaProperties;
import com.msb.bpm.approval.appr.client.camunda.ExternalTaskSubscription;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.UserManagementClient;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea.DataResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.service.checklist.impl.ChecklistServiceImpl;
import com.msb.bpm.approval.appr.service.idgenerate.IDSequenceService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 22/10/2023, Sunday
 **/
@ExtendWith(MockitoExtension.class)
class PostCreateApplicationServiceImplTest {

  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private UserManagerClient userManagerClient;

  @Mock
  private MessageSource messageSource;

  @Mock
  private ChecklistServiceImpl checklistService;

  @Mock
  private PostInitializeInfoServiceImpl postInitializeInfoService;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CommonService commonService;

  @Mock
  private CamundaService camundaService;

  @Mock
  private CamundaProperties camundaProperties;

  @Mock
  private UserManagementClient userManagementClient;

  @Mock
  private IDSequenceService idSequenceService;
  @Mock
  private CustomerClient customerClient;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private PostCreateApplicationServiceImpl postCreateApplicationService;

  private CustomerDTO customer;

  private CustomerEntity customerEntity;

  private Set<CustomerDTO> customerRelations;

  private DataResponse regionArea;

  @BeforeEach
  public void setUp() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(javaTimeModule());

    customer = JsonUtil.convertString2Object(
        "{\"refCustomerId\":1,\"refCusId\":\"customer-001\",\"customerType\":\"RB\",\"typeOfCustomer\":\"V001\",\"fullName\":\"Phạm Ngọc Anh\",\"gender\":\"F\",\"dateOfBirth\":\"01/10/2023\",\"martialStatus\":2,\"national\":\"VN\",\"phoneNumber\":\"0946464090\",\"email\":\"Linhanh@gmail.com\",\"msbMember\":null,\"staffId\":null,\"literacy\":\"V002\",\"identityDocuments\":[{\"priority\":true,\"type\":\"IA\",\"identityNumber\":\"235253453535\",\"issuedDate\":\"01/10/2023\",\"issuedBy\":\"V001\",\"issuedPlace\":\"40\"}],\"addresses\":[{\"addressType\":\"V001\",\"cityCode\":\"50\",\"districtCode\":\"102\",\"wardCode\":\"1405\",\"addressLine\":\"45 chùa láng\"},{\"addressType\":\"V002\",\"cityCode\":\"3\",\"districtCode\":\"149\",\"wardCode\":\"1814\",\"addressLine\":\"50 chùa láng\"}]}",
        CustomerDTO.class,
        mapper);

    customerRelations = JsonUtil.convertString2Set(
        "[{\"cifNo\":null,\"relationship\":\"V001\",\"refCusId\":\"customer-relate-001\",\"customerType\":\"RB\",\"typeOfCustomer\":\"V001\",\"fullName\":\"Người liên quan\",\"gender\":\"F\",\"dateOfBirth\":\"01/10/2023\",\"martialStatus\":2,\"national\":\"VN\",\"phoneNumber\":\"0968653243\",\"email\":null,\"msbMember\":null,\"staffId\":null,\"literacy\":null,\"identityDocuments\":[{\"priority\":true,\"type\":\"IP\",\"identityNumber\":\"457474574745\",\"issuedDate\":\"01/10/2023\",\"issuedBy\":\"V003\",\"issuedPlace\":null}],\"addresses\":[{\"addressType\":\"V002\",\"cityCode\":\"1\",\"districtCode\":\"659\",\"wardCode\":\"1512\",\"addressLine\":\"659\"}]}]",
        CustomerDTO.class, mapper);

    regionArea = JsonUtil.convertString2Object(
        "{\"userCode\":\"nhant4\",\"specializedBank\":\"RB\",\"createdFullName\":\"Nhã role KS HĐTD\",\"createdPhoneNumber\":\"0915163465\",\"regionDetail\":{\"code\":\"NORTH\",\"parent\":\"RB\",\"ecmCode\":\"\",\"fullName\":\"Miền Bắc\",\"name\":\"Miền Bắc\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"\",\"specializedBank\":\"RB\",\"type\":\"MIEN\",\"children\":[{\"code\":\"AREA2\",\"parent\":\"NORTH\",\"ecmCode\":\"\",\"fullName\":\"AREA 2\",\"name\":\"AREA 2\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"\",\"specializedBank\":\"RB\",\"type\":\"\",\"children\":[{\"code\":\"031\",\"parent\":\"AREA2\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa\",\"name\":\"CN Đống Đa\",\"shortName\":\"DDA\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Số 47A Huỳnh Thúc Kháng, phường Láng Hạ, quận Đống Đa, thành phố Hà Nội\",\"specializedBank\":\"RB\",\"type\":\"CN\",\"children\":[{\"code\":\"3110LH\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Láng Hạ\",\"name\":\"LH Láng Hạ\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Tòa tháp A, 88 Láng Hạ, Phường Láng Hạ, Quận Đống Đa, Thành phố Hà Nội.\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"3110L3\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Láng Hạ\",\"name\":\"LH Thăng Long\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Tòa tháp A, 88 Láng Hạ, Phường Láng Hạ, Quận Đống Đa, Thành phố Hà Nội.\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"01100DSMB\",\"parent\":\"031\",\"ecmCode\":\"DSFB\",\"fullName\":\"Directsales miền Bắc\",\"name\":\"Directsales miền Bắc\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"01100T1\",\"parent\":\"031\",\"ecmCode\":\"TSFB\",\"fullName\":\"Telesales Miền Bắc\",\"name\":\"Telesales Miền Bắc\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03113\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Hoàng Cầu\",\"name\":\"MSB Hoàng Cầu\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Số 65 phố Hoàng Cầu, phường Ô Chợ Dừa, quận Đống Đa, thành phố Hà Nội\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03114\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Thái Thịnh \",\"name\":\"MSB Thái Thịnh\",\"shortName\":\"\",\"phoneNumber\":\"0234\",\"fax\":\"123\",\"address\":\"Số 110 Thái Thịnh, phường Trung Liệt, quận Đống Đa, thành phố Hà Nội.\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03115\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Hùng Vương\",\"name\":\"MSB Hùng Vương\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Số 54A đường Nguyễn Chí Thanh, phường Láng Thượng, quận Đống Đa, thành phố Hà Nội.\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03110\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Láng Hạ\",\"name\":\"MSB Láng Hạ\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Tòa tháp A, 88 Láng Hạ, Phường Láng Hạ, Quận Đống Đa, Thành phố Hà Nội.\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03111\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Nguyễn Chí Thanh\",\"name\":\"MSB Nguyễn Chí Thanh\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Số 95 Nguyễn Chí Thanh, phường Láng Hạ, quận Đống Đa, thành phố Hà Nội.\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03112\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Vạn Phúc\",\"name\":\"MSB Vạn Phúc\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Tầng trệt, khu phức hợp thương mại nhà ở Gold Silk, số 430 Cầu Am, phường Vạn Phúc, quận Hà Đông, thành phố Hà Nội\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03106\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa -  Địa điểm kinh doanh phòng giao dịch Hào Nam\",\"name\":\"MSB Hào Nam\",\"shortName\":\"\",\"phoneNumber\":\"02435132452\",\"fax\":\"123\",\"address\":\"A-08 Dự án khu hỗn hợp nhà ở thương mại và văn phòng tại số 83 Hào Nam, phường Ô Chợ Dừa, Quận Đống Đa, Thành phố Hà Nội, Việt Nam\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03107\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Đông Đô\",\"name\":\"MSB Đông Đô\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Số 07, đường Chùa Bộc, phường Quang Trung, quận Đống Đa, thành phố Hà Nội.\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03109\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa - Phòng giao dịch Kim Liên \",\"name\":\"MSB Kim Liên\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"Kios số 3 và 4, tầng 1, tòa nhà B4 Kim Liên, phường Kim Liên, quận Đống Đa, thành phố Hà Nội\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]},{\"code\":\"03100\",\"parent\":\"031\",\"ecmCode\":\"031\",\"fullName\":\"Chi nhánh Đống Đa\",\"name\":\"MSB Đống Đa\",\"shortName\":\"\",\"phoneNumber\":\"787867866\",\"fax\":\"123\",\"address\":\"Số 47A Huỳnh Thúc Kháng, phường Láng Hạ, quận Đống Đa, thành phố Hà Nội\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]}]}]}]},\"businessUnitDetail\":{\"code\":\"01100DSMB\",\"parent\":\"031\",\"ecmCode\":\"DSFB\",\"fullName\":\"Directsales miền Bắc\",\"name\":\"Directsales miền Bắc\",\"shortName\":\"\",\"phoneNumber\":\"\",\"fax\":\"123\",\"address\":\"\",\"specializedBank\":\"RB\",\"type\":\"DVKD\",\"children\":[]}}",
        DataResponse.class, mapper);

    Map<String, ExternalTaskSubscription> map = new HashMap<>();
    ExternalTaskSubscription subscription = new ExternalTaskSubscription();
    subscription.setProcessDefinitionKey("RB_PD_GENERAL");
    map.put("CHECK_WORKFLOW", subscription);

    lenient().when(camundaProperties.getSubscriptions()).thenReturn(map);
  }

  @Test
  void test_getType_should_be_ok() {
    assertEquals(POST_CREATE_APPLICATION, postCreateApplicationService.getType());
  }

  @Test
  void test_persistCustomerWithCustomerRelationLatest_should_be_ok() {
    when(postInitializeInfoService.persistCustomerFlushAll(customerRelations))
        .thenReturn(customerRelations);

    when(postInitializeInfoService.persistCustomerWithRelation(customer, customerRelations))
        .thenReturn(new CustomerEntity());

    customerEntity = postCreateApplicationService.persistCustomerWithCustomerRelationLatest(
        customer, customerRelations);

    assertNotNull(customerEntity);
  }

  @ParameterizedTest
  @CsvSource({"false", "true"})
  void test_persistCustomer_should_be_ok(boolean hasRefCustomerId) {
    customerEntity = new CustomerEntity();
    if (!hasRefCustomerId) {
      customer.setRefCustomerId(null);
      assertNotNull(postCreateApplicationService.persistCustomer(customer));
    } else {
      when(customerRepository.findFirstByRefCustomerIdOrderByCreatedAtDesc(anyLong()))
          .thenReturn(Optional.of(customerEntity));
      assertNotNull(postCreateApplicationService.persistCustomer(customer));
    }
  }

  @ParameterizedTest
  @CsvSource({"false", "true"})
  void test_execute_should_be_ok(boolean regionFail) {
    test_persistCustomer_should_be_ok(true);
    when(idSequenceService.generateBpmId()).thenReturn("151-00000001");

    PostCreateApplicationRequest request = new PostCreateApplicationRequest();
    request.setCustomer(customer);

    if (regionFail) {
      when(userManagerClient.getRegionAreaByUserName(anyString())).thenReturn(null);
      assertThrows(ApprovalException.class, () -> postCreateApplicationService.execute(request));
    } else {
      when(userManagerClient.getRegionAreaByUserName(anyString())).thenReturn(regionArea);

      ProcessInstanceWithVariablesDto variablesDto = new ProcessInstanceWithVariablesDto();
      variablesDto.setVariables(new HashMap<>());
      variablesDto.getVariables().put("nextTask", new VariableValueDto().value(new TaskDto()));
      when(camundaService.startProcessInstance("151-00000001", "RB_PD_GENERAL", null)).thenReturn(variablesDto);
      when(userManagementClient.getSaleCode()).thenReturn("041951");
      assertNotNull(postCreateApplicationService.execute(request));
    }
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
}
