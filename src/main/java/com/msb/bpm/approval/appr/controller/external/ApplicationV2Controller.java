package com.msb.bpm.approval.appr.controller.external;

import com.msb.bpm.approval.appr.controller.api.ApplicationV2Api;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.model.search.FullSearch;
import com.msb.bpm.approval.appr.service.application.ApplicationService;
import com.msb.bpm.approval.appr.service.application.IntegrationHistoricService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplicationV2Controller implements ApplicationV2Api {
  private final ApplicationService applicationService;
  private final ApiRespFactory apiRespFactory;
  private final IntegrationHistoricService integrationHistoricService;

  @Override
  public ResponseEntity<ApiResponse> postSyncCreditRatingScore(@PathVariable String bpmId,
      @Valid @RequestBody PostQueryCreditRatingRequest request) {
    return apiRespFactory.success(applicationService.syncCreditRatingCSSV2(bpmId, request));
  }

  @Override
  public ResponseEntity<ApiResponse> fullSearchIntegrationHistoric(FullSearch fullSearch) {
    return apiRespFactory.success(integrationHistoricService.fullSearch(fullSearch));
  }
}
