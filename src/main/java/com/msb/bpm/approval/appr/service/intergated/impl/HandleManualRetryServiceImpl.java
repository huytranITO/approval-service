package com.msb.bpm.approval.appr.service.intergated.impl;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.way4.CardWay4RetryRequest;
import com.msb.bpm.approval.appr.model.request.way4.CardWay4RetryRequest.CardRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.intergated.BpmOperationService;
import com.msb.bpm.approval.appr.service.intergated.HandleManualRetryService;
import com.msb.bpm.approval.appr.service.intergated.IntegrationWay4Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HandleManualRetryServiceImpl implements HandleManualRetryService {

  @Autowired
  ApplicationRepository applicationRepository;

  @Autowired
  IntegrationWay4Service integrationWay4Service;

  @Autowired
  BpmOperationService bpmOperationService;

  @Override
  public void processRetryWay4(Set<Long> request) {
    List<CardRequest> retryWay4 = new ArrayList<>();
    for (Long id : request) {
      CardRequest cardList = new CardRequest();
      cardList.setId(id);
      retryWay4.add(cardList);
    }
    if (!retryWay4.isEmpty()) {
      CardWay4RetryRequest way4RetryRequest = new CardWay4RetryRequest();
      way4RetryRequest.setCardList(retryWay4);
      integrationWay4Service.handleManualRetry(way4RetryRequest);
    }
  }

  @Override
  public void processRetryOperation(Set<String> request) {
    for (String bpmId : request) {
      ApplicationEntity app = applicationRepository.findByBpmId(bpmId)
          .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION));
      bpmOperationService.syncBpmOperation(app, true);
    }
  }
}
