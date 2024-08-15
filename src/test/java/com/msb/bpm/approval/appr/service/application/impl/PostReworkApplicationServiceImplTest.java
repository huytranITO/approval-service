package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_REWORK_APPLICATION;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.AS0100;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostReworkApplicationRequest;
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
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 27/10/2023, Friday
 **/
@ExtendWith(MockitoExtension.class)
class PostReworkApplicationServiceImplTest {

  private String pathSourceFile = "src/test/resources/application_impl/";
  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private DecisionRuleIntegrateService decisionRuleIntegrateService;

  @Mock
  private CamundaService camundaService;

  @Mock
  private VerifyService verifyService;

  @Spy
  private ObjectMapper objectMapper;

  @Mock
  private ConfigurationServiceCache configurationServiceCache;
  @Mock
  private SecurityContextUtil securityContextUtil;
  @InjectMocks
  private PostReworkApplicationServiceImpl postReworkApplicationService;
  private List<CategoryDataResponse> md014;
  private ApplicationEntity applicationEntity;

  @BeforeEach
  public void setUp() throws IOException {
    objectMapper.registerModule(ObjectMapperUtil.javaTimeModule());
    applicationEntity = createApplicationEntity();

    md014 = new ArrayList<>(JsonUtil.convertString2Set(
        "[{\"id\":19062,\"code\":\"V001\",\"props\":null,\"value\":\"Lập đề xuất\",\"description\":null,\"version\":null},{\"id\":19063,\"code\":\"V002\",\"props\":null,\"value\":\"Phê duyệt đề xuất\",\"description\":null,\"version\":null},{\"id\":19064,\"code\":\"V005\",\"props\":null,\"value\":\"Kiểm tra yêu cầu\",\"description\":null,\"version\":null},{\"id\":19065,\"code\":\"V006\",\"props\":null,\"value\":\"Phê duyệt cá nhân 1\",\"description\":null,\"version\":null},{\"id\":19066,\"code\":\"V007\",\"props\":null,\"value\":\"Phê duyệt cá nhân 2\",\"description\":null,\"version\":null},{\"id\":19067,\"code\":\"V008\",\"props\":null,\"value\":\"Phê duyệt cá nhân 3\",\"description\":null,\"version\":null},{\"id\":19068,\"code\":\"V009\",\"props\":null,\"value\":\"Kiểm Soát Hội Đồng\",\"description\":null,\"version\":null},{\"id\":19069,\"code\":\"V010\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 1\",\"description\":null,\"version\":null},{\"id\":19070,\"code\":\"V011\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 2\",\"description\":null,\"version\":null},{\"id\":19071,\"code\":\"V012\",\"props\":null,\"value\":\"Phê duyệt Hội Đồng 3\",\"description\":null,\"version\":null},{\"id\":19072,\"code\":\"HT04\",\"props\":null,\"value\":\"Phân bổ yêu cầu\",\"description\":null,\"version\":null}]",
        CategoryDataResponse.class, objectMapper));
  }

  @Test
  void test_getType_should_be_ok() {
    assertEquals(POST_REWORK_APPLICATION, postReworkApplicationService.getType());
  }

  @Test
  void testExecute() {
    PostReworkApplicationRequest request = new PostReworkApplicationRequest();
    request.setReceptionUser("currentAssignee");
    String bpmId = "BPM_ID";
    when(applicationRepository.findByBpmId(bpmId)).thenReturn(
        Optional.of(applicationEntity));

    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getName()).thenReturn("thietpt");
    SecurityContextHolder securityContextHolder = Mockito.mock(SecurityContextHolder.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    Mockito.when(securityContextUtil.getAuthentication()).thenReturn(authentication);
    Map<String, VariableValueDto> variableValueDtoMap = new HashMap<>();
    RuleResponse ruleResponse = ruleTeamLeadToContactResponse();
    Mockito.when(decisionRuleIntegrateService.getDecisionRule(anyLong(), any(), any(Object.class)))
        .thenReturn(ruleResponse);
    when(configurationServiceCache.getCategoryDataByCode(ConfigurationCategory.PROCESSING_STEP))
        .thenReturn(md014);
    Mockito.when(camundaService.completeTaskWithReturnVariables(
            applicationEntity, ruleResponse.getRuleDataItem().getData().getStepCode()))
        .thenReturn(variableValueDtoMap);

    assertNotNull(postReworkApplicationService.execute(request, bpmId));
  }

  private ApplicationEntity createApplicationEntity() throws IOException {
    ApplicationEntity applicationEntity = objectMapper.readValue(
        new File(pathSourceFile + "application.json"), ApplicationEntity.class);

    return applicationEntity;
  }

  private RuleResponse ruleTeamLeadToContactResponse() {
    RuleResponse response = new RuleResponse();
    RuleDataItem item = new RuleDataItem();
    TransitionResponse transition = new TransitionResponse();
    transition.setNextTask(AS0100.getValue());
    transition.setNextRole("PD_RB_RM");
    transition.setNextStep("Lap de xuat");
    item.setData(transition);
    response.setRuleDataItem(item);
    return response;
  }
}
