package com.msb.bpm.approval.appr.service.history.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.mapper.FeedbackHistoryMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationHistoryFeedbackDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import com.msb.bpm.approval.appr.model.request.data.PostCreateFeedbackRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.FeedbackHistoryRepository;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class ApplicationFeedbackHistoryServiceImplTest {


  private String pathSourceFile = "src/test/resources/feedback_history/";

  private ObjectMapper objectMapperTest;

  @Mock
  private FeedbackHistoryRepository feedbackHistoryRepository;
  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private SecurityContextUtil securityContextUtil;
  @Spy
  private FeedbackHistoryMapper feedbackHistoryMapper = Mappers.getMapper(
      FeedbackHistoryMapper.class);
  @InjectMocks
  private ApplicationFeedbackHistoryServiceImpl applicationFeedbackHistoryService;

  private List<ApplicationHistoryFeedbackEntity> applicationHistoryFeedbackEntities;
  private ApplicationHistoryFeedbackEntity applicationHistoryFeedbackEntity;
  private ApplicationEntity applicationEntity;
  private PostCreateFeedbackRequest postCreateFeedbackRequest;

  @BeforeEach
  void setUp() throws IOException {
    System.out.println("SETUP");
    MockitoAnnotations.initMocks(this);
    objectMapperTest = new ObjectMapper();
    objectMapperTest.registerModule(ObjectMapperUtil.javaTimeModule());
    initData();
  }


  @Test
  void getFeedbackHistory() {
    when(feedbackHistoryRepository.findByApplicationBpmId(any())).thenReturn(
        Optional.of(applicationHistoryFeedbackEntities));
    when(applicationRepository.findByBpmIdCustomQuery(any())).thenReturn(
        Optional.of(applicationEntity));

    List<ApplicationHistoryFeedbackDTO> result = applicationFeedbackHistoryService.getFeedbackHistory(
        "BMP_ID");
    assertEquals(6, result.size());
  }

  @Test
  void postCreateFeedback() {
    when(applicationRepository.findByBpmId(any())).thenReturn(
        Optional.of(applicationEntity));
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("thietpt");
    SecurityContextHolder securityContextHolder = mock(SecurityContextHolder.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    when(securityContextUtil.getAuthentication()).thenReturn(authentication);
    // Save
    when(feedbackHistoryRepository.save(any())).thenReturn(any());
    assertDoesNotThrow(() -> applicationFeedbackHistoryService.postCreateFeedback("BPM_ID",
        postCreateFeedbackRequest));

  }

  @Test
  void putUpdateFeedback() {
    when(applicationRepository.findByBpmId(any())).thenReturn(
        Optional.of(applicationEntity));
    when(feedbackHistoryRepository.findById(any())).thenReturn(
        Optional.of(applicationHistoryFeedbackEntity));
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("thietpt");
    SecurityContextHolder securityContextHolder = mock(SecurityContextHolder.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    when(securityContextUtil.getAuthentication()).thenReturn(authentication);
    // Save
    when(feedbackHistoryRepository.save(any())).thenReturn(any());
    assertDoesNotThrow(() -> applicationFeedbackHistoryService.putUpdateFeedback("BPM_ID", 10L,
        postCreateFeedbackRequest));
  }

  @Test
  void deleteHistoryFeedback() {
    when(applicationRepository.findByBpmIdCustomQuery(any())).thenReturn(
        Optional.of(applicationEntity));
    when(feedbackHistoryRepository.findById(any())).thenReturn(
        Optional.of(applicationHistoryFeedbackEntity));
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("thietpt");
    SecurityContextHolder securityContextHolder = mock(SecurityContextHolder.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    securityContext.setAuthentication(authentication);
    securityContextHolder.setContext(securityContext);
    when(securityContextUtil.getAuthentication()).thenReturn(authentication);
    // Delete
    doNothing().when(feedbackHistoryRepository).deleteById(any());

    assertDoesNotThrow(
        () -> applicationFeedbackHistoryService.deleteHistoryFeedback("BPM_ID", 10L));
  }

  private void initData() throws IOException {
    applicationEntity = objectMapperTest.readValue(
        new File(pathSourceFile + "application.json"),
        ApplicationEntity.class);

    postCreateFeedbackRequest = objectMapperTest.readValue(
        new File(pathSourceFile + "post_create_feedback_request.json"),
        PostCreateFeedbackRequest.class);

    applicationHistoryFeedbackEntity = objectMapperTest.readValue(
        new File(pathSourceFile + "feedback_history_one.json"),
        ApplicationHistoryFeedbackEntity.class);
    //Credit condition
    applicationHistoryFeedbackEntities = objectMapperTest.readValue(
        new File(pathSourceFile + "feedback_history_list.json"),
        new TypeReference<List<ApplicationHistoryFeedbackEntity>>() {
        });
  }
}