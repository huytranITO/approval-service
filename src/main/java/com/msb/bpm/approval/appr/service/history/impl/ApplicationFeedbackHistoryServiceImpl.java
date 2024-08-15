package com.msb.bpm.approval.appr.service.history.impl;

import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_ID_FEEDBACK;
import static com.msb.bpm.approval.appr.exception.DomainCode.WRONG_USER;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.FeedbackHistoryMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationHistoryFeedbackDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import com.msb.bpm.approval.appr.model.request.data.PostCreateFeedbackRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.FeedbackHistoryRepository;
import com.msb.bpm.approval.appr.service.history.ApplicationFeedbackHistoryService;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationFeedbackHistoryServiceImpl implements ApplicationFeedbackHistoryService {

  private final FeedbackHistoryRepository feedbackHistoryRepository;
  private final ApplicationRepository applicationRepository;
  private final FeedbackHistoryMapper feedbackHistoryMapper;

  /**
   * Lấy lịch sử phản hồi
   *
   * @param bpmId String
   * @return List<ApplicationHistoryFeedbackDTO>
   */
  @Override
  @Transactional(readOnly = true)
  public List<ApplicationHistoryFeedbackDTO> getFeedbackHistory(String bpmId) {
    log.info("GET_FEED_BACK_HISTORY BEGIN:: bpmId {}", bpmId);
    List<ApplicationHistoryFeedbackEntity> feedBackList = feedbackHistoryRepository.findByApplicationBpmId(
        bpmId).orElseThrow(
        () -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    ApplicationEntity applicationEntity = applicationRepository.findByBpmIdCustomQuery(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));

    List<ApplicationHistoryFeedbackDTO> feedbackDtoList = new ArrayList<>();
    feedBackList.forEach(applicationHistoryFeedbackEntity -> feedbackDtoList.add(
        feedbackHistoryMapper.toApplicationHistoryFeedbackDtoWithApplication(
            applicationHistoryFeedbackEntity, applicationEntity,
            SecurityContextUtil.getCurrentUser())));
    log.info("GET_FEED_BACK_HISTORY END");
    return feedbackDtoList;
  }

  /**
   * Tạo mới phản hồi
   *
   * @param bpmId   String
   * @param request PostCreateFeedbackRequest
   */
  @Override
  @Transactional
  public void postCreateFeedback(String bpmId, PostCreateFeedbackRequest request) {
    log.info("POST_CREATE_FEED_BACK_HISTORY BEGIN:: bpmId {}", bpmId);
    String username = request.getHistoryFeedback().getUsername();
    String feedbackContent = request.getHistoryFeedback().getFeedbackContent();
    String createdPhoneNumber = request.getHistoryFeedback().getCreatedPhoneNumber();
    ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    if (!SecurityContextUtil.getCurrentUser().equals(applicationEntity.getAssignee())) {
      throw new ApprovalException(WRONG_USER);
    }
    ApplicationHistoryFeedbackEntity feedbackEntity = new ApplicationHistoryFeedbackEntity();
    feedbackEntity.setApplication(applicationEntity);
    feedbackEntity.setUsername(username);
    feedbackEntity.setUserRole(applicationEntity.getProcessingRole());
    feedbackEntity.setFeedbackContent(feedbackContent);
    feedbackEntity.setFeedbackAt(LocalDateTime.now());
    feedbackEntity.setCreatedPhoneNumber(createdPhoneNumber);
    feedbackHistoryRepository.save(feedbackEntity);
    log.info("POST_CREATE_FEED_BACK_HISTORY END");
  }

  /**
   * Chỉnh sửa phản hồi
   *
   * @param bpmId   String
   * @param id      Long
   * @param request PostCreateFeedbackRequest
   */
  @Override
  public void putUpdateFeedback(String bpmId, Long id, PostCreateFeedbackRequest request) {
    log.info("PUT_UPDATE_FEED_BACK_HISTORY BEGIN:: bpmId: {}, id: {}", bpmId, id);
    String feedbackContent = request.getHistoryFeedback().getFeedbackContent();

    ApplicationEntity applicationEntity = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    ApplicationHistoryFeedbackEntity feedbackEntity = feedbackHistoryRepository.findById(id)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_ID_FEEDBACK, new Object[]{id}));

    if (!feedbackHistoryMapper.isEditableHistoryFeedback(feedbackEntity, applicationEntity,
        SecurityContextUtil.getCurrentUser())) {
      throw new ApprovalException(WRONG_USER);
    }

    feedbackEntity.setFeedbackContent(feedbackContent);
    feedbackHistoryRepository.save(feedbackEntity);
    log.info("PUT_UPDATE_FEED_BACK_HISTORY END");
  }

  /**
   * Delete feedback history
   *
   * @param bpmId
   * @param id
   */
  @Override
  public void deleteHistoryFeedback(String bpmId, Long id) {
    log.info("DELETE_FEED_BACK_HISTORY BEGIN:: bpmId: {}, id: {}", bpmId, id);

    ApplicationEntity applicationEntity = applicationRepository.findByBpmIdCustomQuery(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));

    ApplicationHistoryFeedbackEntity feedbackEntity = feedbackHistoryRepository.findById(id)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_ID_FEEDBACK, new Object[]{id}));

    if (!feedbackHistoryMapper.isEditableHistoryFeedback(feedbackEntity, applicationEntity,
        SecurityContextUtil.getCurrentUser())) {
      throw new ApprovalException(WRONG_USER);
    }
    feedbackHistoryRepository.deleteById(id);
    log.info("DELETE_FEED_BACK_HISTORY END");
  }
}
