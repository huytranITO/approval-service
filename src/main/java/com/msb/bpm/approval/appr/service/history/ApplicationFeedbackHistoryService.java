package com.msb.bpm.approval.appr.service.history;

import com.msb.bpm.approval.appr.model.dto.ApplicationHistoryFeedbackDTO;
import com.msb.bpm.approval.appr.model.request.data.PostCreateFeedbackRequest;

import java.util.List;

public interface ApplicationFeedbackHistoryService {

  List<ApplicationHistoryFeedbackDTO> getFeedbackHistory(String bpmId);

  void postCreateFeedback(String bpmId, PostCreateFeedbackRequest request);

  void putUpdateFeedback(String bpmId, Long id, PostCreateFeedbackRequest request);

  void deleteHistoryFeedback(String bpmId, Long id);
}
