package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.GET_OPR_DETAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.response.collateral.OpRiskAssetResponse.OpRiskAssetData;
import com.msb.bpm.approval.appr.repository.AmlOprRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetOpRiskDetailServiceImplTest {
  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private AmlOprRepository amlOprRepository;
  @Mock
  private CollateralClient collateralClient;
  @Mock
  private ObjectMapper objectMapper;
  @InjectMocks
  private GetOpRiskDetailServiceImpl getOpRiskDetailService;
  private String pathSourceFile = "src/test/resources/oprisk_asset/";
  private ApplicationEntity applicationEntity;
  private List<OpRiskAssetData> opRiskAssetDataList;
  private AmlOprEntity amlOprEntity;
  @BeforeEach
  public void setUp() throws IOException {
    System.out.println("SETUP");
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(ObjectMapperUtil.javaTimeModule());

    initData();
  }
  private void initData() throws IOException {
    amlOprEntity = objectMapper.readValue(
        new File(pathSourceFile + "aml_opr_entity.json"), AmlOprEntity.class);
    applicationEntity = objectMapper.readValue(
        new File(pathSourceFile + "application_entity.json"), ApplicationEntity.class);
    opRiskAssetDataList = objectMapper.readValue(
      new File(pathSourceFile + "oprisk_asset_data_list.json"),
      new TypeReference<List<OpRiskAssetData>>() {}
    );
  }
  @Test
  void testGetType() {
    assertEquals(GET_OPR_DETAIL, getOpRiskDetailService.getType());
  }

  @Test
  void testExecute() {
    List<AmlOprEntity> amlOprEntities = new ArrayList<>();
    amlOprEntities.add(amlOprEntity);
    when(applicationRepository.findByBpmId(any())).thenReturn(Optional.of(applicationEntity));
    when(amlOprRepository.findByApplicationIdAndQueryType(any(), any())).thenReturn(Optional.of(amlOprEntities));
    assertNotNull(getOpRiskDetailService.execute(any()));

    when(amlOprRepository.findByApplicationIdAndQueryType(any(), any())).thenReturn(Optional.of(Collections.emptyList()));
    when(collateralClient.getOpRiskAsset(anyString())).thenReturn(opRiskAssetDataList);
    assertNotNull(getOpRiskDetailService.execute(any()));
  }
}
