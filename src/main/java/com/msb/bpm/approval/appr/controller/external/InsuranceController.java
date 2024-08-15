package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.controller.api.InsuranceApi;
import com.msb.bpm.approval.appr.model.request.insurance.PostPublishInsuranceInfoRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.insurance.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InsuranceController  implements InsuranceApi {

  private final ApiRespFactory apiRespFactory;
  private final InsuranceService insuranceService;

  @Override
  public ResponseEntity<ApiResponse> publicInsuranceInfoToMq(PostPublishInsuranceInfoRequest request, String bpmApplicationId) {
    insuranceService.execute(bpmApplicationId, request);
    return apiRespFactory.success();
  }
}
