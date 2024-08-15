package com.msb.bpm.approval.appr.service.intergated.impl;


import static com.msb.bpm.approval.appr.constant.IntegrationConstant.APPROVE_RESULT_NO;
import static com.msb.bpm.approval.appr.constant.IntegrationConstant.WAY_4;

import com.msb.bpm.approval.appr.enums.card.IntegrationResponseCode;
import com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import com.msb.bpm.approval.appr.model.request.way4.CardWay4RetryRequest;
import com.msb.bpm.approval.appr.model.request.way4.CardWay4RetryRequest.CardRequest;
import com.msb.bpm.approval.appr.repository.ApplicationHistoricIntegrationRepository;
import com.msb.bpm.approval.appr.service.intergated.BaseIntegrationService;
import com.msb.bpm.approval.appr.service.intergated.IntegrationWay4Service;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


/*
 * @author: Nguyễn Văn Bảo
 * @since: 5/6/2023
 * @email:  baonv2@msb.com.vn
 * */
@Component
@Slf4j
@AllArgsConstructor
public class IntegrationWay4ServiceImpl implements IntegrationWay4Service {
  private final BaseIntegrationService baseIntegrationService;
  private final ApplicationHistoricIntegrationRepository integrationRepository;

  public ApplicationHistoricIntegration init(Long appId) throws Exception {
    log.info("initCardInfo START:");
    return baseIntegrationService.composeCardInfo(ApplicationHistoricIntegration.builder().applicationId(appId).build());
  }

  @Async
  public void asyncForCard(ApplicationHistoricIntegration applicationInfo) {
    log.info("asyncForCard START with, request={}", applicationInfo);
    try {
      if (Objects.isNull(applicationInfo) || applicationInfo.getApproveResult().equals(APPROVE_RESULT_NO)) {
        log.info("asyncForCard was canceled!, card type: {}, approve result: {}", applicationInfo.getCardType(), applicationInfo.getApproveResult());
        return;
      }
      if (baseIntegrationService.checkToRetryOrCreateClient(applicationInfo) == Boolean.FALSE) {
        log.info("Step check create client was canceled! with bpmId={}", applicationInfo.getBpmId());
        return;
      }
      ApplicationHistoricIntegration clientInfo = baseIntegrationService.createOrRetryClient(applicationInfo);
      if (baseIntegrationService.checkToCreateCard(clientInfo) == Boolean.FALSE) {
        log.info("asyncForCard Step check create card was canceled! with bpmId={}", applicationInfo.getBpmId());
        return;
      }
      ApplicationHistoricIntegration mainCard = baseIntegrationService.createOrRetryCreditCard(clientInfo);
      if (baseIntegrationService.checkToCreateSubCard(mainCard) == Boolean.FALSE) {
        log.info("asyncForCard Step check create sub card was canceled! with bpmId={}", applicationInfo.getBpmId());
        return;
      }
      baseIntegrationService.createSubCard(mainCard);
    } catch (Exception e) {
      log.error("[asyncForCard] with bpmId={}, Error: {}", applicationInfo.getBpmId(), e);
    }
  }

  @Override
  public void handleManualRetry(CardWay4RetryRequest cardList) {
    log.info("handleManualRetry START with, request={}", cardList);
    if (CollectionUtils.isEmpty(cardList.getCardList())) {
      return;
    }
    try {
      Iterator<CardRequest> it = cardList.getCardList().iterator();
      while (it.hasNext()) {
        CardRequest cardRequest = it.next();
        ApplicationHistoricIntegration item = integrationRepository.findInternal(WAY_4, cardRequest.getId(), LocalDate.now());
        if (item != null) {
          executeInternal(item);
        }
      }
    } catch (Exception e) {
      log.error("[handleManualRetry] with, Error: {}", e);
    }
  }

  @Override
  public void handleAutoRetry() {
    log.info("handleAutoRetry START:");
    List<ApplicationHistoricIntegration> cardList = integrationRepository.findByErrorStatus(WAY_4, LocalDate.now());
    if (CollectionUtils.isEmpty(cardList)) {
      log.info("handleAutoRetry method was canceled!, No data to handle retry");
      return;
    }
    Iterator<ApplicationHistoricIntegration> it = cardList.iterator();
    while (it.hasNext()) {
      ApplicationHistoricIntegration item = it.next();
      executeInternal(item);
    }
    log.info("handleAutoRetry END:");
  }

  private void executeInternal(ApplicationHistoricIntegration item) {
    log.info("executeInternal START with request {}:", item);
    try {
      ApplicationHistoricIntegration applicationInfo = baseIntegrationService.composeCardInfo(item);
      if (Objects.isNull(applicationInfo)) {
        return;
      }
      ApplicationHistoricIntegration mainCard = null;
      ApplicationHistoricIntegration clientInfo = null;
      ApplicationHistoricIntegration subCard = null;
      switch (IntegrationStatusDetail.decode(item.getIntegratedStatusDetail())) {
        case ERR_CLIENT:
        case NEW:
          if (baseIntegrationService.checkToRetryOrCreateClient(applicationInfo) == Boolean.FALSE) {
            log.info("Step check create client was canceled! with bpmId={}", applicationInfo.getBpmId());
            break;
          }
          clientInfo = baseIntegrationService.createOrRetryClient(applicationInfo);
          if (baseIntegrationService.checkToCreateCard(clientInfo) == Boolean.FALSE) {
            log.info("Step check create card was canceled! with bpmId={}", applicationInfo.getBpmId());
            break;
          }
          mainCard = baseIntegrationService.createOrRetryCreditCard(clientInfo);
          if (baseIntegrationService.checkToCreateSubCard(mainCard) == Boolean.FALSE) {
            log.info("Step check create sub card was canceled! with bpmId={}", applicationInfo.getBpmId());
            break;
          }
          baseIntegrationService.createSubCard(mainCard);
          break;
        case ERR_CARD:
        case INPROGESS_CARD:
          // Kiểm tra lỗi được tạo lại thẻ
          if (baseIntegrationService.checkToCreateCard(applicationInfo) == Boolean.FALSE) {
            log.info("Step check create card was canceled! with bpmId={}", applicationInfo.getBpmId());
            break;
          }

          // Kiểm tra lỗi Timeout thì gọi api check-created
          if (checkTimeoutError(applicationInfo.getErrorCode()) == Boolean.TRUE) {
            log.info("Lỗi timeout tạo thẻ chính with bpmId = {}", applicationInfo.getBpmId());
            mainCard = baseIntegrationService.checkCreated(applicationInfo);
          }

          // Nếu thẻ chưa được tạo thì gọi api create card
          if (mainCard == null) {
            mainCard = baseIntegrationService.createOrRetryCreditCard(applicationInfo);
          }

          // Kiểm tra tạo thẻ chính thành công thì tạo thẻ phụ
          if (baseIntegrationService.checkToCreateSubCard(mainCard) == Boolean.FALSE) {
            log.info("Step check create sub card was canceled! with bpmId={}", applicationInfo.getBpmId());
            break;
          }
          baseIntegrationService.createSubCard(mainCard);
          break;
        case ERR_SUB_CARD:
          if (baseIntegrationService.checkToRetrySubCard(applicationInfo) == Boolean.FALSE) {
            log.info("Step check retry sub card was canceled! with bpmId={}", applicationInfo.getBpmId());
            break;
          }

          // Kiểm tra lỗi timeout tạo thẻ phụ
          if (checkTimeoutError(applicationInfo.getErrorCode()) == Boolean.TRUE) {
            log.info("Lỗi timeout tạo thẻ phụ with bpmId = {}", applicationInfo.getBpmId());
            subCard = baseIntegrationService.checkCreated(applicationInfo);
          }

          // Nếu thẻ phụ chưa được tạo thì gọi api create sub-card
          if (subCard == null) {
            baseIntegrationService.retrySubCard(applicationInfo);
          }
          break;
        default:
          break;
      }
    } catch (Exception e) {
      log.error("[executeInternal] with, Error: {}", e.getMessage());
    }
  }

  public Boolean checkDuplicate(Long appId, String bpmId)
  {
    List<ApplicationHistoricIntegration> infoList = integrationRepository.findByApplicationIdAndBpmId(appId, bpmId);
    if (!CollectionUtils.isEmpty(infoList))
    {
      log.info("System has been found existing credit card");
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  private Boolean checkTimeoutError(String errorCode) {

    if (errorCode!= null && IntegrationResponseCode.TIMEOUT.getCode().equals(Integer.parseInt(errorCode))) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
