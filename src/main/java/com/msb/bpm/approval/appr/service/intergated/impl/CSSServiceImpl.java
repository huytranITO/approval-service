package com.msb.bpm.approval.appr.service.intergated.impl;

import com.msb.bpm.approval.appr.client.css.CSSClient;
import com.msb.bpm.approval.appr.client.css.CSSClientConfigProperties;
import com.msb.bpm.approval.appr.client.css.CSSEndpointProperties;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.css.CSSEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.ScoreRBCSSMapper;
import com.msb.bpm.approval.appr.model.dto.css.ScoreRBCSS;
import com.msb.bpm.approval.appr.model.request.css.CommonLegacyRequest;
import com.msb.bpm.approval.appr.model.request.css.GetScoreRBLegacyInfo;
import com.msb.bpm.approval.appr.model.request.css.GetScoreRBLegacyRequest;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRBResponse;
import com.msb.bpm.approval.appr.service.intergated.CSSService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.msb.bpm.approval.appr.util.Util.YYYYMMDDHHMMSS;

@Service
@AllArgsConstructor
public class CSSServiceImpl implements CSSService {

  private final CSSClientConfigProperties cssClientConfigProp;
  private final CSSClient cssClient;

  @Override
  public ScoreRBCSS getScoreRB(String profileId, String legalDocNo) {
    GetScoreRBLegacyRequest request = buildRequest(profileId, legalDocNo);

    GetScoreRBResponse response = cssClient.getScoreRB(request);

    if (!response.isSuccess()) {
      throw new ApprovalException(DomainCode.get(response.getResponse().getCode()), new Object[]{legalDocNo});
    }
    return ScoreRBCSSMapper.INSTANCE.sourceToDestination(response.getRbResponse());
  }

  private GetScoreRBLegacyRequest buildRequest(String profileId, String legalDocNo) {
    CommonLegacyRequest commonLegacyRequest = buildCommonLegacyRequest(CSSEndpoint.GET_SCORE_RB);
    GetScoreRBLegacyInfo getScoreRBLegacyInfo = GetScoreRBLegacyInfo.builder()
        .customerType(CustomerType.RB.name())
        .legalDocNo(legalDocNo)
        .profileId(profileId)
        .build();
    return new GetScoreRBLegacyRequest(commonLegacyRequest, getScoreRBLegacyInfo);
  }

  private CommonLegacyRequest buildCommonLegacyRequest(CSSEndpoint cssEndpoint) {
    CSSEndpointProperties cssEndpointProperties = cssClientConfigProp.getEndpoint().get(cssEndpoint.getValue());
    return CommonLegacyRequest.builder()
        .channel(cssEndpointProperties.getChannel())
        .requestTime(DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS).format(LocalDateTime.now()))
        .password(cssClientConfigProp.getPassword())
        .userAuthen(cssClientConfigProp.getUserAuthen())
        .build();
  }

}
