package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.controller.api.ApplicationFeedbackApi;
import com.msb.bpm.approval.appr.model.request.feeback.PostApplicationFeedbackRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.application.ApplicationFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplicationFeedbackController implements ApplicationFeedbackApi {

  private final ApplicationFeedbackService applicationFeedbackService;
  private final ApiRespFactory apiRespFactory;


  /**
   * Phản hồi thông tin sai lệch cho khách hàng bên LDP
   *
   * @param request
   * @return
   */
  @Override
  public ResponseEntity<ApiResponse> postApplicationFeedbackCustomer(
      PostApplicationFeedbackRequest request) {
    return apiRespFactory.success(applicationFeedbackService.applicationFeedbackCustomer(request));
  }
}
