package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.dto.cic.GeneratedPDFResult;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.cic.SearchCICIntegrationRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest.QueryCIC;
import com.msb.bpm.approval.appr.model.response.cic.CICData;
import com.msb.bpm.approval.appr.model.response.cic.CICDataResponse;
import com.msb.bpm.approval.appr.model.response.cic.CustomerCIC;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface CICService {

  Set<CustomerCIC> searchCode(String customerUniqueId, String customerType, String token);

  CICData search(SearchCICIntegrationRequest request, String token);

  CompletableFuture<List<CICDataResponse>> getDataFromCICAsync(QueryCIC request,
      String token,
      ApplicationEntity entityApp);
  List<CICDataResponse> getDataFromCIC(QueryCIC request, String token,
      ApplicationEntity entityApp);

  void publishMessage(List<CICDataResponse> lstResult, ApplicationEntity entityApp);

  void publishMessage(ApplicationEntity entityApp);

  void getCicReportAsync(ApplicationEntity entityApp,
      List<CICDataResponse> lstResult,
      PostQueryCICRequest request,
      String currentUser);

  void getCicReportAsync(ApplicationEntity application, String currentUser);

  GeneratedPDFResult generatePdf(ApplicationEntity entityApp,
      QueryCIC queryCIC,
      CICDataResponse cicDataResponse, String token);

  boolean isSyncingCIC(String applicationBpmId);

  void updateSyncingIndicatorCICFlag(String applicationBpmId, boolean syncingFlag);

  void updateSyncingPDFCICFlag(String applicationBpmId, boolean syncingFlag);
}
