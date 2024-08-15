package com.msb.bpm.approval.appr.controller.api;


import com.msb.bpm.approval.appr.model.request.insurance.PostPublishInsuranceInfoRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/api")
public interface InsuranceApi {

  @PostMapping(value = "/v1/insurance-integration/application/{bpmApplicationId}")
  ResponseEntity<ApiResponse> publicInsuranceInfoToMq(@Valid @RequestBody PostPublishInsuranceInfoRequest request,
      @PathVariable String bpmApplicationId);

}
