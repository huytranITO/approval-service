package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.client.otp.OtpClient;
import com.msb.bpm.approval.appr.controller.api.OtpApi;
import com.msb.bpm.approval.appr.model.request.otp.SendOtpRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Pham Bien
 **/

@RestController
@RequiredArgsConstructor
public class OtpController implements OtpApi {

  private final ApiRespFactory factory;
  private final OtpClient otpClient;

  @Override
  public ResponseEntity<ApiResponse> sendOtp(SendOtpRequest sendOtpRequest) {
    otpClient.sendOtp(sendOtpRequest);
    return factory.success();
  }
}
