package com.msb.bpm.approval.appr.service.intergated.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;

import com.msb.bpm.approval.appr.client.legacy.LegacyClient;
import com.msb.bpm.approval.appr.client.legacy.LegacyClientConfigProperties;
import com.msb.bpm.approval.appr.client.legacy.LegacyEndpointProperties;
import com.msb.bpm.approval.appr.enums.legacy.LegacyEndpoint;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.legacy.CreditRatingCssRbRequest;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRBV2Response;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRbCssResponse;
import com.msb.bpm.approval.appr.service.intergated.LegacyService;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LegacyServiceImpl implements LegacyService {

  private final LegacyClient legacyClient;

  private final LegacyClientConfigProperties legacyClientConfigProperties;
  @Override
  public GetScoreRBV2Response getScoringData(String profileId, String legalDocNo) {
    CreditRatingCssRbRequest request = buildRequest(profileId, legalDocNo);
    GetScoreRbCssResponse getScoreRbCssResponse = legacyClient.getCreditRating(request);
    GetScoreRBV2Response response = new GetScoreRBV2Response();
    if (!getScoreRbCssResponse.isSuccess()) {
      throw new ApprovalException(DomainCode.get(getScoreRbCssResponse.getData().getResponse().getCode()), new Object[]{legalDocNo});
    }
    if (Objects.nonNull(getScoreRbCssResponse.getData())) {
      response = getScoreRbCssResponse.getData();
    }
    return response;
  }

  private CreditRatingCssRbRequest buildRequest(String profileId, String legalDocNo) {
    LegacyEndpointProperties properties = legacyClientConfigProperties.getEndpoint().get(
        LegacyEndpoint.GET_CREDIT_RATING_RB.getValue());
    return CreditRatingCssRbRequest.builder()
      .legalDocNo(legalDocNo)
      .profileId(profileId)
      .customerType(RB)
      .channel(properties.getChannel())
      .build();
  }
}
