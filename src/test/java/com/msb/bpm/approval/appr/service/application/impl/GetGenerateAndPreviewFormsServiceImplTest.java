//package com.msb.bpm.approval.appr.service.application.impl;
//
//import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.GET_PREVIEW_FORM;
//import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0004;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.msb.bpm.approval.appr.enums.application.CustomerType;
//import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
//import com.msb.bpm.approval.appr.enums.rule.RuleCode;
//import com.msb.bpm.approval.appr.exception.ApprovalException;
//import com.msb.bpm.approval.appr.model.dto.formtemplate.response.RuleTemplateData;
//import com.msb.bpm.approval.appr.model.dto.formtemplate.response.RuleTemplateDataDetail;
//import com.msb.bpm.approval.appr.model.dto.formtemplate.response.RuleTemplateResponseDTO;
//import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
//import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
//import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
//import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
//import com.msb.bpm.approval.appr.model.response.rule.EntryResponse;
//import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
//import com.msb.bpm.approval.appr.model.response.rule.RuleResponse.RuleDataItem;
//import com.msb.bpm.approval.appr.model.response.rule.TransitionResponse;
//import com.msb.bpm.approval.appr.repository.ApplicationRepository;
//import com.msb.bpm.approval.appr.service.formtemplate.FormTemplateService;
//import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
//import com.msb.bpm.approval.appr.service.verify.VerifyService;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import org.apache.commons.lang3.StringUtils;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
///**
// * @author : Manh Nguyen Van (CN-SHQLQT)
// * @mailto : manhnv8@msb.com.vn
// * @created : 21/10/2023, Saturday
// **/
//@ExtendWith({MockitoExtension.class})
//class GetGenerateAndPreviewFormsServiceImplTest {
//
//  @Mock
//  private ApplicationRepository applicationRepository;
//
//  @Mock
//  private PostSubmitApplicationServiceImpl postSubmitApplicationService;
//
//  @Mock
//  private DecisionRuleIntegrateService decisionRuleIntegrateService;
//
//  @Mock
//  private FormTemplateService formTemplateService;
//
//  @Mock
//  private VerifyService verifyService;
//
//  @Mock
//  private ObjectMapper objectMapper;
//
//  @InjectMocks
//  private GetGenerateAndPreviewFormsServiceImpl getGenerateAndPreviewFormsService;
//
//  private ObjectMapper mapper;
//
//  @BeforeEach
//  public void setUp() {
//    mapper = new ObjectMapper();
//  }
//
//  @Test
//  void test_getType_should_be_ok() {
//    Assertions.assertEquals(GET_PREVIEW_FORM, getGenerateAndPreviewFormsService.getType());
//  }
//
//  @Test
//  void test_execute_should_be_throw_when_notFoundApplication() {
//    Mockito.when(applicationRepository.findByBpmId(anyString()))
//        .thenReturn(Optional.empty());
//
//    Assertions.assertThrows(ApprovalException.class,
//        () -> getGenerateAndPreviewFormsService.execute("151-00000001"));
//  }
//
//  @Test
//  void test_execute_should_be_throw_when_wrong_assignee() {
//    Mockito.when(applicationRepository.findByBpmId(anyString()))
//        .thenReturn(Optional.of(new ApplicationEntity()));
//
//    Assertions.assertThrows(ApprovalException.class,
//        () -> getGenerateAndPreviewFormsService.execute("151-00000001"));
//  }
//
//  @Test
//  void test_execute_should_be_call_formTemplate_to_generate_file() {
//    ApplicationEntity applicationEntity = new ApplicationEntity();
//    CustomerEntity customerEntity = new CustomerEntity();
//    customerEntity.setCustomerType(CustomerType.RB.name());
//    customerEntity.setIndividualCustomer(new IndividualCustomerEntity());
//    applicationEntity.setId(1L);
//    applicationEntity.setCustomer(customerEntity);
//    applicationEntity.setAssignee("unknown");
//    applicationEntity.setBpmId("151-00000001");
//    applicationEntity.setGeneratorStatus("00");
//
//    Mockito.when(applicationRepository.findByBpmId(anyString()))
//        .thenReturn(Optional.of(applicationEntity));
//
//    Mockito.when(
//            decisionRuleIntegrateService.getDecisionRule(eq(1L), eq(RuleCode.R003), any(Object.class)))
//        .thenReturn(getRuleData());
//
//    Mockito.when(
//            decisionRuleIntegrateService.getDecisionRule(eq(1L), eq(RuleCode.R004), any(Object.class)))
//        .thenReturn(getRuleTemplateData());
//
//    Assertions.assertDoesNotThrow(() -> getGenerateAndPreviewFormsService.execute("151-00000001"));
//  }
//
//  @ParameterizedTest
//  @CsvSource(value = {"V002,PD_RB_BM,","HT04,PD_RB_CONTACT_LEAD,","V005,PD_RB_CA1,PD_RB_RM","V007,PD_RB_CA2,","V009,PD_RB_CC_CONTROL,","HT01,PD_RB_CONTACT,"})
//  void test_execute_should_be_waiting_generate_file(String nextStepCode, String nextRole, String giveBackRole) {
//    ApplicationEntity applicationEntity = new ApplicationEntity();
//    CustomerEntity customerEntity = new CustomerEntity();
//    customerEntity.setCustomerType(CustomerType.RB.name());
//    customerEntity.setIndividualCustomer(new IndividualCustomerEntity());
//    applicationEntity.setId(1L);
//    applicationEntity.setCustomer(customerEntity);
//    applicationEntity.setAssignee("unknown");
//    applicationEntity.setBpmId("151-00000001");
//    applicationEntity.setGeneratorStatus("01");
//    if (StringUtils.isNotBlank(giveBackRole)) {
//      applicationEntity.setGivebackRole(ProcessingRole.valueOf(giveBackRole));
//    }
//
//    ApplicationHistoryApprovalEntity approvalEntity = new ApplicationHistoryApprovalEntity();
//    approvalEntity.setUserRole(ProcessingRole.valueOf(nextRole));
//    applicationEntity.getHistoryApprovals().add(approvalEntity);
//
//    Mockito.when(applicationRepository.findByBpmId(anyString()))
//        .thenReturn(Optional.of(applicationEntity));
//
//    RuleResponse ruleResponse = getRuleData();
//    ruleResponse.getRuleDataItem().getData().setStepCode(nextStepCode);
//    ruleResponse.getRuleDataItem().getData().setNextRole(nextRole);
//    Mockito.when(
//            decisionRuleIntegrateService.getDecisionRule(eq(1L), eq(RuleCode.R003), any(Object.class)))
//        .thenReturn(ruleResponse);
//
//    Assertions.assertDoesNotThrow(() -> getGenerateAndPreviewFormsService.execute("151-00000001"));
//  }
//
////  @Test
////  void test_execute_should_be_generated_file_success() {
////    ApplicationEntity applicationEntity = new ApplicationEntity();
////    CustomerEntity customerEntity = new CustomerEntity();
////    customerEntity.setCustomerType(CustomerType.RB.name());
////    customerEntity.setIndividualCustomer(new IndividualCustomerEntity());
////    applicationEntity.setId(1L);
////    applicationEntity.setCustomer(customerEntity);
////    applicationEntity.setAssignee("unknown");
////    applicationEntity.setBpmId("151-00000001");
////    applicationEntity.setGeneratorStatus("02");
////
////    ApplicationExtraDataEntity extraDataEntity = new ApplicationExtraDataEntity();
////    extraDataEntity.setKey(FORM_TEMPLATE);
////    extraDataEntity.setCategory(DOCUMENTS_INFO);
////    extraDataEntity.setValue(
////        "[{\"code\":\"MB0601.RM\",\"fileName\":\"MB0601_Template to trinh de xuat - Copy - Copy.pdf\",\"filePath\":\"test/approval//20230714/MB0601.RM/b07a5882-6cc1-42e4-ab21-b6bcffc45f11.pdf\",\"fileId\":\"b07a5882-6cc1-42e4-ab21-b6bcffc45f11.pdf\",\"fileType\":\"pdf\",\"fileSize\":113115,\"bucket\":\"bpm-sit\",\"createdBy\":\"rm1\"}]".getBytes(
////            StandardCharsets.UTF_8));
////    applicationEntity.getExtraDatas().add(extraDataEntity);
////
////    Mockito.when(applicationRepository.findByBpmId(anyString()))
////        .thenReturn(Optional.of(applicationEntity));
////
////    RuleResponse ruleResponse = getRuleData();
////    ruleResponse.getRuleDataItem().getData().setStepCode("HT01");
////    Mockito.when(
////            decisionRuleIntegrateService.getDecisionRule(eq(1L), eq(RuleCode.R003), any(Object.class)))
////        .thenReturn(ruleResponse);
////
////    AbstractBaseService abstractBaseService = Mockito.mock(AbstractBaseService.class, Answers.RETURNS_SMART_NULLS);
////    Set<ChecklistFileMessageDTO> listFile = JsonUtil.convertString2Set(
////        new String(extraDataEntity.getValue(), StandardCharsets.UTF_8),
////        ChecklistFileMessageDTO.class, mapper);
////    Mockito.when(abstractBaseService.buildExtraData(applicationEntity.getExtraDatas(),
////            new ImmutablePair<>(DOCUMENTS_INFO, FORM_TEMPLATE), ChecklistFileMessageDTO.class))
////        .thenReturn(listFile);
////
////    Assertions.assertDoesNotThrow(() -> getGenerateAndPreviewFormsService.execute("151-00000001"));
////  }
//
//  private RuleResponse getRuleData() {
//    RuleResponse response = new RuleResponse();
//    RuleDataItem item = new RuleDataItem();
//    TransitionResponse transition = new TransitionResponse();
//    transition.setNextTask(AS0004.getValue());
//    transition.setNextRole("PD_RB_CONTACT");
//    transition.setNextStep("Contact");
//    item.setData(transition);
//    response.setRuleDataItem(item);
//    return response;
//  }
//
//  private RuleTemplateResponseDTO getRuleTemplateData() {
//    RuleTemplateResponseDTO response = new RuleTemplateResponseDTO();
//    RuleTemplateData templateData = new RuleTemplateData();
//    List<RuleTemplateDataDetail> data = new ArrayList<>();
//
//    RuleTemplateDataDetail item = new RuleTemplateDataDetail();
//    item.setTemplateCode(EntryResponse.builder().value("1").build());
//    item.setChecklistCode(EntryResponse.builder().value("1").build());
//    item.setDocNumber(EntryResponse.builder().value("1").build());
//    data.add(item);
//
//    templateData.setData(data);
//
//    response.setData(templateData);
//
//    return response;
//  }
//}
