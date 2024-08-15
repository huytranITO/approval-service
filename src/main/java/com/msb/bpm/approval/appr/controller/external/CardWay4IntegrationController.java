package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.controller.api.CardWay4IntegrationApi;
import com.msb.bpm.approval.appr.model.request.way4.CardWay4RetryRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.intergated.IntegrationWay4Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/*
 * @author: Nguyễn Văn Bảo
 * @since: 11/6/2023
 * @email:  baonv2@msb.com.vn
 * */

@RestController
@RequiredArgsConstructor
public class CardWay4IntegrationController implements CardWay4IntegrationApi {

  private final IntegrationWay4Service integrationWay4Service;
  private final ApiRespFactory apiRespFactory;

  @Override
  public ResponseEntity<ApiResponse> handleManualRetry(CardWay4RetryRequest requestList) {
    integrationWay4Service.handleManualRetry(requestList);
    return apiRespFactory.success();
  }

  @Override
  public ResponseEntity<ApiResponse> handleAutoRetry() {
    integrationWay4Service.handleAutoRetry();
    return apiRespFactory.success();
  }

}
