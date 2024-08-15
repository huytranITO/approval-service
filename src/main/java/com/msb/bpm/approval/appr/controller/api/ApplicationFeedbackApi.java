package com.msb.bpm.approval.appr.controller.api;

import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_APPLICATION_FEEDBACK;

import com.msb.bpm.approval.appr.model.request.feeback.PostApplicationFeedbackRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/api/v1/application-feedback")
public interface ApplicationFeedbackApi {

  //ROLE của RM mới được comment
  @PostMapping()
  @PreAuthorize(MANAGE_APPLICATION_FEEDBACK)
  ResponseEntity<ApiResponse> postApplicationFeedbackCustomer(
      @Valid @RequestBody PostApplicationFeedbackRequest request);


}
