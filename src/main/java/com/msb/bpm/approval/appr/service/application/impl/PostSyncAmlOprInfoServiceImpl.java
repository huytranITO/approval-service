package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SYNC_AML_OPR_INFO;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCICRequest.QueryCIC;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.intergated.AMLService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.intergated.OpriskService;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class PostSyncAmlOprInfoServiceImpl extends AbstractBaseService implements
    BaseService<Object,
            PostSyncAmlOprRequest> {

  private final CommonService commonService;
  private final AMLService amlService;
  private final OpriskService opriskService;
  private final ApplicationRepository applicationRepository;

  @Override
  public String getType() {
    return POST_SYNC_AML_OPR_INFO;
  }

  @Override
  @Transactional
  public Object execute(PostSyncAmlOprRequest request, Object... obj) {
    log.info("START with request={}, obj={}", request, obj);
    if (CollectionUtils.isEmpty(request.getData()) ||
        request.getData().stream().map(QueryCIC::getIdentifierCode).noneMatch(
            StringUtils::isNotBlank)) {
      throw new ApprovalException(DomainCode.INVALID_INPUT_AML_OPRISK_ERROR,
          new Object[]{request.getData()});
    }
    String bpmId = (String) obj[0];
    ApplicationEntity entityApp = commonService.findAppByBpmId(bpmId);
    //Query info from AML
    amlService.syncAmlInfo(entityApp, request);
    //Query info from OpRisk
    opriskService.syncOprInfo(entityApp, request);
    applicationRepository.save(entityApp);
    Set<AmlOprEntity> setEntitySorted = entityApp.getAmlOprs().stream().sorted(Comparator.
        comparing(AmlOprEntity::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
    log.info("END with bpmId={}, request={}, setEntitySorted={}", bpmId, request,
        setEntitySorted);
    return buildAmlOpr(setEntitySorted);
  }
}
