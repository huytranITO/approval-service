package com.msb.bpm.approval.appr.controller.api;


import com.msb.bpm.approval.appr.model.request.way4.CardWay4RetryRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/*
* @author: Nguyễn Văn Bảo
* @since: 11/6/2023
* @email:  baonv2@msb.com.vn
* */
@Validated
@RequestMapping("/api/v1")
public interface CardWay4IntegrationApi {
  @PostMapping("/card/credit/retry")
  ResponseEntity<ApiResponse> handleManualRetry(@RequestBody CardWay4RetryRequest requestList);

  @GetMapping("/card/auto/retry")
  ResponseEntity<ApiResponse> handleAutoRetry();
}
