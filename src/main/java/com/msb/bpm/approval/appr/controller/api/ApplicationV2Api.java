package com.msb.bpm.approval.appr.controller.api;

import static com.msb.bpm.approval.appr.constant.PermissionConstant.MANAGE_RATING_QUERY;

import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.model.search.FullSearch;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@RequestMapping("/api/v2/application")
public interface ApplicationV2Api {
  @PreAuthorize(MANAGE_RATING_QUERY)
  @PostMapping("/{bpmId}/credit-rating-score")
  ResponseEntity<ApiResponse> postSyncCreditRatingScore(@PathVariable String bpmId,
      @Valid @RequestBody PostQueryCreditRatingRequest request);

  @PostMapping("/integration/historic")
  ResponseEntity<ApiResponse> fullSearchIntegrationHistoric(@Valid @RequestBody FullSearch fullSearch);
}
