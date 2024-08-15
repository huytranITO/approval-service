package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import com.msb.bpm.approval.appr.model.request.way4.CardWay4RetryRequest;

/*
 * @author: Nguyễn Văn Bảo
 * @since: 11/6/2023
 * @email:  baonv2@msb.com.vn
 * */
public interface IntegrationWay4Service {

  ApplicationHistoricIntegration init(Long appId) throws Exception;

  Boolean checkDuplicate(Long appId, String bpmId);

  void asyncForCard(ApplicationHistoricIntegration applicationInfo);

  void handleManualRetry(CardWay4RetryRequest requestList);

  void handleAutoRetry();
}
