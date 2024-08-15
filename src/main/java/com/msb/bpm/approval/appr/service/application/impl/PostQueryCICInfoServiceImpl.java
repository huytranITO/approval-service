package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_QUERY_CIC_INFO;
import static com.msb.bpm.approval.appr.exception.DomainCode.CIC_IDENTIFY_NOT_BLANK;
import static com.msb.bpm.approval.appr.exception.DomainCode.CIC_REF_CUSTOMER_ID_NOT_NULL;
import static com.msb.bpm.approval.appr.exception.DomainCode.CIC_SERVICE_ERROR;
import static com.msb.bpm.approval.appr.exception.DomainCode.SYNCING_CIC;

import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.ApParamEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest.QueryCIC;
import com.msb.bpm.approval.appr.model.response.cic.CICDataResponse;
import com.msb.bpm.approval.appr.repository.ApParamRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.intergated.CICService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.msb.bpm.approval.appr.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 11/5/2023, Thursday
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class PostQueryCICInfoServiceImpl extends AbstractBaseService implements
    BaseService<Object, PostQueryCICRequest> {

  private final CICService cicService;
  private final CommonService commonService;
  private final ApplicationRepository applicationRepository;
  private final ApParamRepository apParamRepository;

  @Override
  public String getType() {
    return POST_QUERY_CIC_INFO;
  }

  @Override
  @Transactional
  public Object execute(PostQueryCICRequest request, Object... obj) {
    log.info("START with request={}, obj={}", request, obj);
    String bpmId = (String) obj[0];
    if (CollectionUtils.isEmpty(request.getData())) {
      return buildCicData(syncCICData(bpmId));
    }
    validateRequest(request);
    ApplicationEntity entityApp = commonService.findAppByBpmId(bpmId);
    return buildCicData(queryCICInfo(request, entityApp));
  }

  private void validateRequest(PostQueryCICRequest request) {
    if (CollectionUtils.isEmpty(request.getData())) {
      return;
    }

    for (QueryCIC queryCIC : request.getData()) {
      if (queryCIC.getIdentifierCode() == null) {
        throw new ApprovalException(CIC_IDENTIFY_NOT_BLANK);
      }
      if (queryCIC.getRefCustomerId() == null) {
        throw new ApprovalException(CIC_REF_CUSTOMER_ID_NOT_NULL);
      }
    }
  }

  private Set<CicEntity> syncCICData(String bpmId) {
    ApplicationEntity application = commonService.findAppByBpmId(bpmId);
    Set<CicEntity> cics = application.getCics();
    if (CollectionUtils.isEmpty(cics)) {
      return new HashSet<>();
    }

    if (cicService.isSyncingCIC(application.getBpmId())) {
      throw new ApprovalException(SYNCING_CIC);
    }
    String currentUser = SecurityContextUtil.getCurrentUser();
    cicService.publishMessage(application);
    cicService.getCicReportAsync(application, currentUser);

    return cics.stream().sorted(Comparator.comparing(CicEntity::getId))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Transactional
  public Set<CicEntity> queryCICInfo(PostQueryCICRequest request, ApplicationEntity entityApp) {
    String currentUser = SecurityContextUtil.getCurrentUser();
    log.info("queryCICInfo START with request={}, entityApp={}", request, entityApp);
    cicService.updateSyncingIndicatorCICFlag(entityApp.getBpmId(), true);
    cicService.updateSyncingPDFCICFlag(entityApp.getBpmId(), true);
    List<ApParamEntity> paramEntities = apParamRepository.findAllByType(ApplicationConstant.CIC_TIME);
    AtomicReference<Long> time = new AtomicReference<>(5L);
    if(ObjectUtils.isNotEmpty(paramEntities)) {
      paramEntities.stream().findFirst().ifPresent(dt -> {
        if(!StringUtil.isEmpty(dt.getCode())) time.set(Long.valueOf(dt.getCode()));
      });
    }

    List<CICDataResponse> lstResult = new ArrayList<>();
    List<CompletableFuture<List<CICDataResponse>>> lstCF = new ArrayList<>();
    for (PostQueryCICRequest.QueryCIC item : request.getData()) {
      lstCF.add(cicService.getDataFromCICAsync(item, HeaderUtil.getToken(), entityApp));

      try {
        log.info("Thread sleep with time: {}", time);
        Thread.sleep(time.get() *1000);
      } catch (InterruptedException e) {
        log.info("CIC Thread sleep error with error: {}", e);
        Thread.currentThread().interrupt();
      }
    }
    try {
      CompletableFuture<Void> allTaskFuture = CompletableFuture
          .allOf(lstCF.toArray(new CompletableFuture[lstCF.size()]));
      allTaskFuture.get();
      for (int k = 0; k < lstCF.size(); k++) {
        lstResult.addAll(lstCF.get(k).get());
      }
    } catch (Exception ex) {
      cicService.updateSyncingIndicatorCICFlag(entityApp.getBpmId(), false);
      cicService.updateSyncingPDFCICFlag(entityApp.getBpmId(), false);
      log.error("Error: ", ex);
      Thread.currentThread().interrupt();
      if (ex.getCause() instanceof ApprovalException && Objects.equals(
          DomainCode.EXTERNAL_SERVICE_CLIENT_ERROR,
          ((ApprovalException) ex.getCause()).getCode())) {
        throw new ApprovalException(CIC_SERVICE_ERROR,
            ((ApprovalException) ex.getCause()).getArgs());
      }

      if (ex.getCause() instanceof ApprovalException && Objects.equals(
              DomainCode.ERROR_CIC,
              ((ApprovalException) ex.getCause()).getCode())) {
        throw new ApprovalException(DomainCode.ERROR_CIC,
                ((ApprovalException) ex.getCause()).getArgs());
      }
      throw new ApprovalException(CIC_SERVICE_ERROR);
    }

    // Save to DB
    saveToDB(lstResult, entityApp);

    // publish message lên kafka để lấy kết quả truy vấn cic
    cicService.publishMessage(lstResult, entityApp);
    cicService.getCicReportAsync(entityApp, lstResult, request, currentUser);

    Set<CicEntity> setEntitySorted = entityApp.getCics().stream()
        .sorted(Comparator.comparing(CicEntity::getId))
        .collect(Collectors.toCollection(LinkedHashSet::new));
    log.info("queryCICInfo END with bpmId={}, request={}, setEntitySorted={}", entityApp.getBpmId(),
        request, setEntitySorted);
    return setEntitySorted;
  }

  @Transactional
  public void saveToDB(List<CICDataResponse> lstResponse, ApplicationEntity entityApp) {
    log.info("saveToDB START with lstResponse={}, entityApp={}", lstResponse, entityApp);
    Set<CicEntity> lstCicEntity = entityApp.getCics();
    for (CICDataResponse item : lstResponse) {
      CicEntity cicEntity;
      // check if exist then update, others is insert
      Optional<CicEntity> cicEntityOptional = lstCicEntity.stream()
          .filter(e -> item.getIdentifierCode() != null
              && item.getIdentifierCode().equals(e.getIdentifierCode())
              && item.getProductCode() != null
              && item.getProductCode().equals(e.getProductCode())
              && item.getRefCustomerId() != null
              && item.getRefCustomerId().equals(e.getRefCustomerId())
              && item.getCustomerType() != null
              && item.getCustomerType().equals(e.getCustomerType())
              && (StringUtil.isEmpty(e.getCicCode())
              || (!StringUtil.isEmpty(item.getCicCode())
              && item.getCicCode().equals(e.getCicCode())))
          ).findFirst();
      if (cicEntityOptional.isPresent()) {
        cicEntity = cicEntityOptional.get();
      } else {
        cicEntity = new CicEntity().withApplication(entityApp);
        lstCicEntity.add(cicEntity);
      }
      cicEntity.setQueryAt(item.getQueryAt());
      cicEntity.setStatusCode(item.getStatus().toString());
      cicEntity.setCustomerId(item.getCustomerId());
      cicEntity.setRefCustomerId(item.getRefCustomerId());
      cicEntity.setRelationship(item.getRelationship());
      cicEntity.setIdentifierCode(item.getIdentifierCode());
      cicEntity.setCicCode(item.getCicCode());
      cicEntity.setClientQuestionId(item.getClientQuestionId());
      cicEntity.setSubject(item.getSubject());
      cicEntity.setUpdatedAt(LocalDateTime.now());
      cicEntity.setCustomerType(item.getCustomerType());
      cicEntity.setProductCode(item.getProductCode());
      cicEntity.setCicCustomerName(item.getCustomerName());
      cicEntity.setErrorCodeSBV(item.getErrorCodeSBV());
    }

    applicationRepository.save(entityApp);
    log.info("saveToDB END with lstResponse={}, entityApp={}, lstCicEntity={}",
        lstResponse, entityApp, lstCicEntity);
  }
}
