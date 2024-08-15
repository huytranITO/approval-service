package com.msb.bpm.approval.appr.service.application;

import com.msb.bpm.approval.appr.model.request.feeback.PostApplicationFeedbackRequest;
import com.msb.bpm.approval.appr.model.response.MessageResponse;

public interface ApplicationFeedbackService {

  MessageResponse applicationFeedbackCustomer(PostApplicationFeedbackRequest request);
}
