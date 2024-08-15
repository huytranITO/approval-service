package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import java.util.Map;

/*
* @author: BaoNV2
* @since: 29/9/2023 10:14 AM
*
* */
public interface BaseIntegrationService {
  void createSubCard(ApplicationHistoricIntegration cardInfo) throws Exception;
  void retrySubCard(ApplicationHistoricIntegration cardInfo) throws Exception;
  ApplicationHistoricIntegration createOrRetryCreditCard(ApplicationHistoricIntegration clientInfo) throws Exception;
  ApplicationHistoricIntegration createOrRetryClient(ApplicationHistoricIntegration applicationInfo) throws Exception;
  ApplicationHistoricIntegration checkCreated(ApplicationHistoricIntegration applicationInfo) throws Exception;
  ApplicationHistoricIntegration composeCardInfo(ApplicationHistoricIntegration historicIntegration) throws Exception;
  Boolean checkToCreateCard(ApplicationHistoricIntegration cardInfo);
  Boolean checkToRetryOrCreateClient(ApplicationHistoricIntegration applicationInfo);
  Boolean checkToCreateSubCard(ApplicationHistoricIntegration cardInfo);
  Boolean checkToRetrySubCard(ApplicationHistoricIntegration cardInfo);
  void checkClient(String cifNo, Map<String, Object> data);
}
