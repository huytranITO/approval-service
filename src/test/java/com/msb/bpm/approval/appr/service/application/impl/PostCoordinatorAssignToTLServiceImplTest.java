//  package com.msb.bpm.approval.appr.service.application.impl;

//  import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSIGN_TO_TL;
//  import static org.junit.jupiter.api.Assertions.assertEquals;
//  import static org.junit.jupiter.api.Assertions.assertNotNull;
//  import static org.mockito.ArgumentMatchers.any;
//  import static org.mockito.ArgumentMatchers.anyLong;
//  import static org.mockito.ArgumentMatchers.anyString;
//  import static org.mockito.Mockito.when;

//  import com.fasterxml.jackson.databind.ObjectMapper;
//  import com.msb.bpm.approval.appr.enums.application.CustomerType;
//  import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
//  import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
//  import com.msb.bpm.approval.appr.exception.ApprovalException;
//  import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
//  import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
//  import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
//  import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
//  import com.msb.bpm.approval.appr.model.response.rule.RuleResponse;
//  import com.msb.bpm.approval.appr.model.response.rule.RuleResponse.RuleDataItem;
//  import com.msb.bpm.approval.appr.model.response.rule.TransitionResponse;
//  import com.msb.bpm.approval.appr.repository.ApplicationRepository;
//  import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
//  import com.msb.bpm.approval.appr.service.camunda.CamundaService;
//  import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
//  import com.msb.bpm.approval.appr.service.verify.VerifyService;
//  import com.msb.bpm.approval.appr.util.JsonUtil;
//  import java.time.LocalDateTime;
//  import java.util.ArrayList;
//  import java.util.HashMap;
//  import java.util.List;
//  import java.util.Optional;
//  import org.junit.jupiter.api.BeforeEach;
//  import org.junit.jupiter.api.Test;
//  import org.junit.jupiter.params.ParameterizedTest;
//  import org.junit.jupiter.params.provider.CsvSource;
//  import org.mockito.InjectMocks;
//  import org.mockito.Mock;
//  import org.mockito.MockitoAnnotations;

//  /**
//   * @author : Manh Nguyen Van (CN-SHQLQT)
//   * @mailto : manhnv8@msb.com.vn
//   * @created : 22/10/2023, Sunday
//   **/
//  class PostCoordinatorAssignToTLServiceImplTest {

//    @Mock
//    private ApplicationRepository applicationRepository;

//    @Mock
//    private DecisionRuleIntegrateService decisionRuleIntegrateService;

//    @Mock
//    private CamundaService camundaService;

//    @Mock
//    private VerifyService verifyService;

//    @Mock
//    private ConfigurationServiceCache configurationServiceCache;

//    @InjectMocks
//    private PostCoordinatorAssignToTLServiceImpl postCoordinatorAssignToTLService;

//    private List<CategoryDataResponse> md014;

//    @BeforeEach
//    public void setUp() {
//      MockitoAnnotations.openMocks(this);
//      ObjectMapper mapper = new ObjectMapper();

//      md014 = new ArrayList<>(JsonUtil.convertString2Set(
//          "[{\"id\":19062,\"code\":\"V001\",\"props\":null,\"value\":\"Lập đề xuất\",\"description\":null,\"version\":null},{\"id\":19063,\"code\":\"V002\",\"props\":null,\"value\":\"Phê duyệt đề xuất\",\"description\":null,\"version\":null},{\"id\":19064,\"code\":\"V005\",\"props\":null,\"value\":\"Kiểm tra yêu cầu\",\"description\":null,\"version\":null},{\"id\":19065,\"code\":\"V006\",\"props\":null,\"value\":\"Phê duyệt cá nhân 1\",\"description\":null,\"version\":null},{\"id\":19066,\"code\":\"V007\",\"props\":null,\"value\":\"Phê duyệt cá nhân 2\",\"description\":null,\"version\":null},{\"id\":19067,\"code\":\"V008\",\"props\":null,\"value\":\"Phê duyệt cá nhân 3\",\"description\":null,\"version\":null},{\"id\":19068,\"code\":\"V009\",\"props\":null,\"value\":\"Kiểm Soát Hội Đồng\",\"description\":null,\"version\":null},{\"id\":19069,\"code\":\"V010\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 1\",\"description\":null,\"version\":null},{\"id\":19070,\"code\":\"V011\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 2\",\"description\":null,\"version\":null},{\"id\":19071,\"code\":\"V012\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 3\",\"description\":null,\"version\":null},{\"id\":19072,\"code\":\"HT04\",\"props\":null,\"value\":\"Phân bổ yêu cầu\",\"description\":null,\"version\":null}]",
//          CategoryDataResponse.class, mapper));
//    }

//    @Test
//    void test_getType_should_be_ok() {
//      assertEquals(POST_ASSIGN_TO_TL, postCoordinatorAssignToTLService.getType());
//    }

//    @ParameterizedTest
//    @CsvSource({"true,unknown,0004,true,true", "true,unknown,0004,false,true",
//        "true,unknown,0004,false,false", "true,unknown2,0004,false,false",
//        "true,unknown,0099,false,false", "true,unknown,9999,false,false", "false,'','',false,false"})
//    void test_execute_should_be_ok(boolean founded, String currentAssignee, String currentStatus,
//        boolean emptyHistory, boolean notFoundReception) {
//      ApplicationEntity applicationEntity = new ApplicationEntity();
//      applicationEntity.setId(1L);
//      applicationEntity.setStatus(currentStatus);
//      applicationEntity.setAssignee(currentAssignee);

//      ApplicationHistoryApprovalEntity approvalEntity = new ApplicationHistoryApprovalEntity();

//      if (!founded) {
//        when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.empty());

//        try {
//          postCoordinatorAssignToTLService.execute(anyString());
//        } catch (ApprovalException ax) {
//          assertNotNull(ax);
//        }
//      } else if ("unknown2".equals(currentAssignee) || "0099".equals(currentStatus) || "9999".equals(currentStatus)) {
//        when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(applicationEntity));

//        try {
//          postCoordinatorAssignToTLService.execute(anyString());
//        } catch (ApprovalException ax) {
//          assertNotNull(ax);
//        }
//      } else if (emptyHistory) {
//        when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(applicationEntity));

//        try {
//          postCoordinatorAssignToTLService.execute(anyString());
//        } catch (ApprovalException ax) {
//          assertNotNull(ax);
//        }
//      } else if (notFoundReception) {

//        approvalEntity.setFromUserRole(ProcessingRole.PD_RB_RM);
//        applicationEntity.getHistoryApprovals().add(approvalEntity);

//        when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(applicationEntity));

//        try {
//          postCoordinatorAssignToTLService.execute(anyString());
//        } catch (ApprovalException ax) {
//          assertNotNull(ax);
//        }
//      } else {

//        approvalEntity.setFromUserRole(ProcessingRole.PD_RB_CONTACT_LEAD);
//        approvalEntity.setExecutedAt(LocalDateTime.MAX);
//        approvalEntity.setCreatedBy("teamlead");
//        applicationEntity.getHistoryApprovals().add(approvalEntity);
//        applicationEntity.setCustomer(new CustomerEntity().withCustomerType(CustomerType.RB.name()));

//        when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(applicationEntity));

//        RuleResponse ruleResponse = new RuleResponse();
//        ruleResponse.setRuleDataItem(new RuleDataItem());
//        ruleResponse.getRuleDataItem().setData(new TransitionResponse());
//        when(decisionRuleIntegrateService.getDecisionRule(anyLong(), any(), any(Object.class)))
//            .thenReturn(ruleResponse);

//        when(configurationServiceCache.getCategoryDataByCode(ConfigurationCategory.PROCESSING_STEP))
//            .thenReturn(md014);

//        when(camundaService.completeTaskWithReturnVariables(
//                applicationEntity, ruleResponse.getRuleDataItem().getData().getStepCode()))
//            .thenReturn(new HashMap<>());

//        applicationEntity = postCoordinatorAssignToTLService.execute(anyString());

//        assertNotNull(applicationEntity);
//      }
//    }
//  }
