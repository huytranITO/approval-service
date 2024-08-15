package com.msb.bpm.approval.appr.controller.api;

import com.msb.bpm.approval.appr.model.request.otp.SendOtpRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : Pham Van Bien
 **/
@RequestMapping("/otp")
public interface OtpApi {

  @PostMapping("/api/v1/send")
  ResponseEntity<ApiResponse> sendOtp(@RequestBody SendOtpRequest sendOtpRequest);
}
