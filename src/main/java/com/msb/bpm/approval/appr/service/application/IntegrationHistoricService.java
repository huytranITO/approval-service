package com.msb.bpm.approval.appr.service.application;

import com.msb.bpm.approval.appr.model.dto.ApplicationHistoricIntegrationDTO;
import com.msb.bpm.approval.appr.model.request.IntegrationRetryRequest;
import com.msb.bpm.approval.appr.model.response.integration.ApplicationHistoricIntegrationResponse;
import com.msb.bpm.approval.appr.model.search.FullSearch;
import java.util.List;

public interface IntegrationHistoricService {

  ApplicationHistoricIntegrationResponse getHistoricIntegration(int page, int size,
      String sortField, int sortOrder);

  ApplicationHistoricIntegrationResponse getHistoricIntegrationSearch(String contents, int page,
      int size, String sortField, int sortOrder);

  List<ApplicationHistoricIntegrationDTO> getHistoricIntegrationDetail(String bpmId);

  Object integrationHistoricRetry(IntegrationRetryRequest requestList);

  ApplicationHistoricIntegrationResponse fullSearch(FullSearch fullSearch);
}
