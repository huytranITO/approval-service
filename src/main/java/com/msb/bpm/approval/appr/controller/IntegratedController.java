package com.msb.bpm.approval.appr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.msb.bpm.approval.appr.controller.api.IntegratedApi;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.cas.PostCASRequest;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.model.response.cic.CICDataResponse;
import com.msb.bpm.approval.appr.service.intergated.CASService;
import com.msb.bpm.approval.appr.service.intergated.CICService;
import com.msb.bpm.approval.appr.service.intergated.CSSService;
import com.msb.bpm.approval.appr.service.intergated.OpriskService;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import java.util.Arrays;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IntegratedController implements IntegratedApi {

  private final ApiRespFactory apiRespFactory;
  private final OpriskService opriskService;
  private final CICService cicService;
  private final CASService casService;

  @Override
  public ResponseEntity<ApiResponse> syncOpriskPerson(String maDinhDanh) {
    return apiRespFactory.success(opriskService.syncOpriskPerson(maDinhDanh));
  }

  @Override
  public ResponseEntity<ApiResponse> syncOpriskLegal(String mst) throws JsonProcessingException {
    return apiRespFactory.success(opriskService.syncOpriskLegal(mst));
  }

  private final CSSService cssService;

  @Override
  public ResponseEntity<ApiResponse> css(String profileId, String legalDocNo) {
    return apiRespFactory.success(cssService.getScoreRB(profileId, legalDocNo));
  }

  @Override
  public ResponseEntity<ApiResponse> searchCodeCIC(String customerUniqueId, String customerType) {
    return apiRespFactory.success(cicService.searchCode(customerUniqueId, customerType,
        HeaderUtil.getToken()));
  }

  @Override
  public ResponseEntity<ApiResponse> publishMessage(String clientQuestionId,
      String applicationBpmId) {
    CICDataResponse response = new CICDataResponse();
    response.setClientQuestionId(Long.valueOf(clientQuestionId));
    response.setStatus(6);
    ApplicationEntity applicationEntity = new ApplicationEntity()
        .withBpmId(applicationBpmId);
    cicService.publishMessage(Arrays.asList(response), applicationEntity);
    return apiRespFactory.success();
  }

  @Override
  public ResponseEntity<ApiResponse> casInfo(PostCASRequest postCASRequest) {
    casService.getCASInfo(postCASRequest);
    return apiRespFactory.success();
  }

  @Override
  public ResponseEntity<ApiResponse> casDetail(PostCASRequest postCASRequest) {
    return apiRespFactory.success(casService.getCASDetail(postCASRequest));
  }

}
