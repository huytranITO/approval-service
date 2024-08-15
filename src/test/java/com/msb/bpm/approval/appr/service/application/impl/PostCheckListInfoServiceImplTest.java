package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CHECK_LIST_TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.data.PostCheckListTabRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class PostCheckListInfoServiceImplTest {

  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private SecurityContextUtil securityContextUtil;
  @InjectMocks
  private PostCheckListInfoServiceImpl postCheckListInfoService;

  private ApplicationEntity applicationEntity;
  private ObjectMapper objectMapperTest;
  private String pathSourceFile = "src/test/resources/application_impl/";

  @BeforeEach
  void setUp() throws IOException {
    objectMapperTest = new ObjectMapper();
    objectMapperTest.registerModule(ObjectMapperUtil.javaTimeModule());
    applicationEntity = createApplicationEntity();
  }

  @Test
  void testGetType() {
    assertEquals(POST_CHECK_LIST_TAB, postCheckListInfoService.getType());
  }

  @Test
  void testExecute() {
    PostCheckListTabRequest request = new PostCheckListTabRequest();
    request.setType(POST_CHECK_LIST_TAB);
    request.setBpmId("BPM_ID");
    when(applicationRepository.findByBpmId(request.getBpmId())).thenReturn(
        Optional.of(applicationEntity));

    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getName()).thenReturn("thietpt");
    SecurityContextHolder securityContextHolder = Mockito.mock(SecurityContextHolder.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    Mockito.when(securityContextUtil.getAuthentication()).thenReturn(authentication);

    assertNotNull(postCheckListInfoService.execute(request, request.getBpmId()));
  }

  private ApplicationEntity createApplicationEntity() throws IOException {
    ApplicationEntity applicationEntity = objectMapperTest.readValue(
        new File(pathSourceFile + "application.json"), ApplicationEntity.class);

    return applicationEntity;
  }

}