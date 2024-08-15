package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SYNC_OPR_ASSET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.legacy.OpRiskLegacyClient;
import com.msb.bpm.approval.appr.config.OpriskPersonConfig;
import com.msb.bpm.approval.appr.config.OpriskPersonConfig.Asset;
import com.msb.bpm.approval.appr.mapper.AmlOprMapper;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.oprisk.OpRiskRequest;
import com.msb.bpm.approval.appr.model.response.legacy.LegacyBaseResponse;
import com.msb.bpm.approval.appr.model.response.legacy.LegacyRespMessage;
import com.msb.bpm.approval.appr.model.response.oprisk.BlackList4LosResponse;
import com.msb.bpm.approval.appr.model.response.oprisk.CheckBlackListResDomain;
import com.msb.bpm.approval.appr.model.response.oprisk.OpRiskResponse;
import com.msb.bpm.approval.appr.model.response.oprisk.SyncOpRiskAssetResponse;
import com.msb.bpm.approval.appr.repository.AmlOprRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class PostSyncOpRiskAssetServiceImplTest {
  @Mock
  private OpRiskLegacyClient opRiskLegacyClient;
  @Mock
  private AmlOprRepository opRiskRepository;
  @Mock
  private AmlOprMapper opRiskMapper;
  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private OpriskPersonConfig opRiskPersonConfig;
  @InjectMocks
  private PostSyncOpRiskAssetServiceImpl postSyncOpRiskAssetService;
  private String pathSourceFile = "src/test/resources/oprisk_asset/";
  private OpRiskRequest opRiskRequest;
  private SyncOpRiskAssetResponse syncOpRiskAssetResponse = new SyncOpRiskAssetResponse();
  private AmlOprEntity amlOprEntity;
  private Asset assetConfig;
  private ApplicationEntity applicationEntity;
  @BeforeEach
  public void setUp() throws IOException {
    System.out.println("SETUP");
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(ObjectMapperUtil.javaTimeModule());

    initData();
  }
  private void initData() throws IOException {
    opRiskRequest = objectMapper.readValue(
        new File(pathSourceFile + "oprisk_request.json"), OpRiskRequest.class);
    amlOprEntity = objectMapper.readValue(
        new File(pathSourceFile, "aml_opr_entity.json"), AmlOprEntity.class);
    assetConfig = objectMapper.readValue(
        new File(pathSourceFile + "asset_config.json"), Asset.class);
    applicationEntity = objectMapper.readValue(
        new File(pathSourceFile + "application_entity.json"), ApplicationEntity.class);

    LegacyBaseResponse checkBlackListLegal = LegacyBaseResponse.builder().build();
    CheckBlackListResDomain resDomain = new CheckBlackListResDomain();
    BlackList4LosResponse blackList4LosLegalResponse = new BlackList4LosResponse();
    resDomain.setBlackList4LosC(blackList4LosLegalResponse);
    checkBlackListLegal.setRespDomain(resDomain);
    // Mock respMessage
    LegacyRespMessage respMessage = new LegacyRespMessage();
    respMessage.setRespCode(0L);
    respMessage.setRespDesc("Desc");
    checkBlackListLegal.setRespMessage(respMessage);

    syncOpRiskAssetResponse.setCheckBlackListC(checkBlackListLegal);
  }
  @Test
  void testGetType() {
    assertEquals(POST_SYNC_OPR_ASSET, postSyncOpRiskAssetService.getType());
  }

  @Test
  void testExecute() {
    when(opRiskPersonConfig.getAsset()).thenReturn(assetConfig);
    when(opRiskLegacyClient.syncOpriskLegal(any())).thenReturn(syncOpRiskAssetResponse);
    when(applicationRepository.findByBpmId(any())).thenReturn(Optional.of(applicationEntity));
    when(opRiskMapper.toEntity((OpRiskResponse) any())).thenReturn(amlOprEntity);
    when(opRiskRepository.findByApplicationIdAndIdentifierCodeAndAssetGroupAndAssetType(any(), any(), any(), any())).thenReturn(null);
    assertNotNull(postSyncOpRiskAssetService.execute(opRiskRequest));
  }
}
