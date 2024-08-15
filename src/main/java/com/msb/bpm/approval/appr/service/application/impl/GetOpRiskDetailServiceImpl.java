package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.GET_OPR_DETAIL;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;

import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.AmlOprMapper;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.response.collateral.OpRiskAssetResponse.OpRiskAssetData;
import com.msb.bpm.approval.appr.repository.AmlOprRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetOpRiskDetailServiceImpl  extends AbstractBaseService implements
    BaseService<List<OpRiskAssetData>, String> {

  private final ApplicationRepository applicationRepository;
  private final AmlOprRepository amlOprRepository;
  private final CollateralClient collateralClient;
  @Override
  public String getType() {
    return GET_OPR_DETAIL;
  }

  @Override
  @Transactional
  public List<OpRiskAssetData> execute(String bpmId, Object... obj) {
    log.info("START GetOpRiskDetailServiceImpl.execute with bpmId= [{}]", bpmId);
    List<OpRiskAssetData> response;
    ApplicationEntity entity = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));
    List<AmlOprEntity> amlOprEntities = amlOprRepository.findByApplicationIdAndQueryType(entity.getId(), ApplicationConstant.OPR_ASSET_TAG).orElse(null);
    if (CollectionUtils.isEmpty(amlOprEntities)) {
      // Lấy thông tin OpRisk tài sản và lưu tại Approval-service
      response = saveOpRiskAsset(entity);
    } else {
      response = AmlOprMapper.INSTANCE.toDtos(amlOprEntities);
    }
    response.forEach(res -> res.setApplicationId(bpmId));

    log.info("END GetOpRiskDetailServiceImpl.execute with response= [{}]", response);
    return response;
  }
  private List<OpRiskAssetData> saveOpRiskAsset(ApplicationEntity applicationEntity) {
    // Gọi sang Collateral để lấy thông tin OpRisk tài sản
    List<OpRiskAssetData> response = collateralClient.getOpRiskAsset(applicationEntity.getBpmId());
    if (CollectionUtils.isEmpty(response)) {
      return Collections.emptyList();
    }
    List<AmlOprEntity> entities = AmlOprMapper.INSTANCE.toEntityList(response);
    if (CollectionUtils.isEmpty(entities)) {
      return Collections.emptyList();
    }
    entities.forEach(entity -> {
      entity.setApplication(applicationEntity);
      entity.setQueryType(ApplicationConstant.OPR_ASSET_TAG);
    });
    return AmlOprMapper.INSTANCE.toDtos(amlOprRepository.saveAllAndFlush(entities));
  }
}
