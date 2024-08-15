package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.ASSIGN_APPLICATION;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_FORMAT;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0002;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0004;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0005;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0099;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.camunda.CamundaAction;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.enums.email.EventCodeEmailType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostAssignRequest;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
import com.msb.bpm.approval.appr.model.response.rule.RuleResponse.RuleDataItem;
import com.msb.bpm.approval.appr.model.response.rule.TransitionResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.service.verify.VerifyService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/10/2023, Saturday
 **/
@ExtendWith(MockitoExtension.class)
class AssignApplicationServiceImplTest {

  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private DecisionRuleIntegrateService decisionRuleIntegrateService;

  @Mock
  private CamundaService camundaService;

  @Mock
  private VerifyService verifyService;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private ConfigurationServiceCache configurationServiceCache;

  @InjectMocks
  private AssignApplicationServiceImpl assignApplicationService;

  private List<CategoryDataResponse> md014;

  @BeforeEach
  public void setUp() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(javaTimeModule());

    md014 = new ArrayList<>(JsonUtil.convertString2Set(
        "[{\"id\":19062,\"code\":\"V001\",\"props\":null,\"value\":\"Lập đề xuất\",\"description\":null,\"version\":null},{\"id\":19063,\"code\":\"V002\",\"props\":null,\"value\":\"Phê duyệt đề xuất\",\"description\":null,\"version\":null},{\"id\":19064,\"code\":\"V005\",\"props\":null,\"value\":\"Kiểm tra yêu cầu\",\"description\":null,\"version\":null},{\"id\":19065,\"code\":\"V006\",\"props\":null,\"value\":\"Phê duyệt cá nhân 1\",\"description\":null,\"version\":null},{\"id\":19066,\"code\":\"V007\",\"props\":null,\"value\":\"Phê duyệt cá nhân 2\",\"description\":null,\"version\":null},{\"id\":19067,\"code\":\"V008\",\"props\":null,\"value\":\"Phê duyệt cá nhân 3\",\"description\":null,\"version\":null},{\"id\":19068,\"code\":\"V009\",\"props\":null,\"value\":\"Kiểm Soát Hội Đồng\",\"description\":null,\"version\":null},{\"id\":19069,\"code\":\"V010\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 1\",\"description\":null,\"version\":null},{\"id\":19070,\"code\":\"V011\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 2\",\"description\":null,\"version\":null},{\"id\":19071,\"code\":\"V012\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 3\",\"description\":null,\"version\":null},{\"id\":19072,\"code\":\"HT04\",\"props\":null,\"value\":\"Phân bổ yêu cầu\",\"description\":null,\"version\":null}]",
        CategoryDataResponse.class, mapper));
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
  void test_getType_should_be_ok() {
    Assertions.assertEquals(ASSIGN_APPLICATION, assignApplicationService.getType());
  }

  @Test
  void test_setAssignee_should_be_ok() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setCustomerType(CustomerType.RB.name());
    applicationEntity.setId(1L);
    applicationEntity.setCustomer(customerEntity);
    applicationEntity.setStatus(AS0002.getValue());

    Mockito.when(applicationRepository.findByBpmId("151-00000001"))
        .thenReturn(Optional.of(applicationEntity));

    RuleResponse ruleResponse = ruleTeamLeadToContactResponse();
    Mockito.when(decisionRuleIntegrateService.getDecisionRule(anyLong(), any(), any(Object.class)))
        .thenReturn(ruleResponse);

    Mockito.when(configurationServiceCache.getCategoryDataByCode(ConfigurationCategory.PROCESSING_STEP))
        .thenReturn(md014);

    Map<String, VariableValueDto> variableValueDtoMap = new HashMap<>();
    Mockito.when(camundaService.completeTaskWithReturnVariables(
            applicationEntity, ruleResponse.getRuleDataItem().getData().getStepCode()))
        .thenReturn(variableValueDtoMap);

    Assertions.assertDoesNotThrow(() -> assignApplicationService.setAssignee(CamundaAction.PD_APP_REASSIGN, "151-00000001", "user", EventCodeEmailType.TEAM_LEAD_ASSIGN_APPROVER));
  }

  @Test
  void test_setAssignee_should_be_ok_reassign() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setCustomerType(CustomerType.RB.name());
    applicationEntity.setId(1L);
    applicationEntity.setCustomer(customerEntity);
    applicationEntity.setStatus(AS0005.getValue());

    Mockito.when(applicationRepository.findByBpmId("151-00000001"))
        .thenReturn(Optional.of(applicationEntity));

    Assertions.assertDoesNotThrow(() -> assignApplicationService.setAssignee(CamundaAction.PD_APP_REASSIGN, "151-00000001", "user", EventCodeEmailType.TEAM_LEAD_ASSIGN_APPROVER));
  }

  @Test
  void test_setAssignee_should_be_exception_when_notFound_application() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setCustomerType(CustomerType.RB.name());
    applicationEntity.setId(1L);
    applicationEntity.setCustomer(customerEntity);
    applicationEntity.setStatus(AS0099.getValue());

    Mockito.when(applicationRepository.findByBpmId("151-00000001"))
        .thenReturn(Optional.of(applicationEntity));

    Assertions.assertThrows(ApprovalException.class, () -> assignApplicationService.setAssignee(CamundaAction.PD_APP_REASSIGN, "151-00000001", "user", EventCodeEmailType.TEAM_LEAD_ASSIGN_APPROVER));
  }

  @Test
  void test_setAssignee_should_be_exception_when_status_invalid() {
    Mockito.when(applicationRepository.findByBpmId("151-00000001"))
        .thenReturn(Optional.empty());

    Assertions.assertThrows(ApprovalException.class, () -> assignApplicationService.setAssignee(CamundaAction.PD_APP_REASSIGN, "151-00000001", "user", EventCodeEmailType.TEAM_LEAD_ASSIGN_APPROVER));
  }

//  @Test
//  void test_execute_should_be_ok() {
//    PostAssignRequest postAssignRequest = new PostAssignRequest();
//    postAssignRequest.setBpmIds(Collections.singletonList("151-00000001"));
//    postAssignRequest.setReceptionUser("user");
//    test_setAssignee_should_be_ok();
//    Assertions.assertDoesNotThrow(() -> assignApplicationService.execute(postAssignRequest, CamundaAction.PD_APP_REASSIGN, EventCodeEmailType.TEAM_LEAD_ASSIGN_APPROVER));
//  }

  private RuleResponse ruleTeamLeadToContactResponse() {
    RuleResponse response = new RuleResponse();
    RuleDataItem item = new RuleDataItem();
    TransitionResponse transition = new TransitionResponse();
    transition.setNextTask(AS0004.getValue());
    transition.setNextRole("PD_RB_CONTACT");
    transition.setNextStep("Contact");
    item.setData(transition);
    response.setRuleDataItem(item);
    return response;
  }
}
