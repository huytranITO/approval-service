package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CLOSE_APP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostCloseApplicationRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
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
 * @created : 22/10/2023, Sunday
 **/
@ExtendWith(MockitoExtension.class)
class PostCloseApplicationServiceImplTest {

  private String pathSourceFile = "src/test/resources/application_impl/";
  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private CamundaService camundaService;

  @Spy
  private ObjectMapper objectMapper;

  @Mock
  private SecurityContextUtil securityContextUtil;

  @InjectMocks
  private PostCloseApplicationServiceImpl postCloseApplicationService;

  private ApplicationEntity applicationEntity;

  @BeforeEach
  public void setUp() throws IOException {
    objectMapper.registerModule(ObjectMapperUtil.javaTimeModule());
    applicationEntity = createApplicationEntity();
  }

  @Test
  void test_getType_should_be_ok() {
    assertEquals(POST_CLOSE_APP, postCloseApplicationService.getType());
  }

  @Test
  void testExecute() {
    PostCloseApplicationRequest request = new PostCloseApplicationRequest();
    request.setNote("note");
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
    when(camundaService.completeTaskWithReturnVariables(applicationEntity, "close"))
        .thenReturn(new HashMap<>());

    assertNotNull(postCloseApplicationService.execute(request, bpmId));
  }

  private ApplicationEntity createApplicationEntity() throws IOException {
    ApplicationEntity applicationEntity = objectMapper.readValue(
        new File(pathSourceFile + "application.json"), ApplicationEntity.class);

    return applicationEntity;
  }
}
