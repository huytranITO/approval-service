package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREDIT_RATING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.css.CreditRatingResponse;
import com.msb.bpm.approval.appr.model.dto.css.ScoreRBCSS;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest.QueryCSS;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.intergated.CSSService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 22/10/2023, Sunday
 **/
@ExtendWith(MockitoExtension.class)
class PostCreditRatingCSSServiceImplTest {

  @Mock
  private CSSService cssService;

  @Mock
  private CommonService commonService;

  @Mock
  private ApplicationRepository applicationRepository;

  @InjectMocks
  private PostCreditRatingCSSServiceImpl postCreditRatingCSSService;

  @BeforeEach
  public void setUp() {

  }

  @Test
  void test_getType_should_be_ok() {
    assertEquals(POST_CREDIT_RATING, postCreditRatingCSSService.getType());
  }

  @Test
  void test_buildCRDtlEntity_should_be_ok() {
    CreditRatingResponse item = CreditRatingResponse.builder()
        .customerType("RB")
        .profileId("")
        .identityCard("")
        .build();

    ScoreRBCSS scoreRBCSS = new ScoreRBCSS();
    scoreRBCSS.setScoresRM(Collections.singletonList(BigDecimal.TEN));
    scoreRBCSS.setApprovalScores(Collections.singletonList(BigDecimal.TEN));
    scoreRBCSS.setRankRM(Collections.singletonList("1"));
    scoreRBCSS.setApprovalRank(Collections.singletonList("1"));
    item.setScoreRbCss(scoreRBCSS);

    ApplicationCreditRatingsEntity aCREntity = new ApplicationCreditRatingsEntity();

    assertNotNull(postCreditRatingCSSService.buildCRDtlEntity(item, aCREntity));

  }

  @Test
  void test_saveToDB_should_be_ok() {
    CreditRatingResponse item = CreditRatingResponse.builder()
        .customerType("RB")
        .profileId("")
        .identityCard("")
        .build();

    ScoreRBCSS scoreRBCSS = new ScoreRBCSS();
    scoreRBCSS.setScoresRM(Collections.singletonList(BigDecimal.TEN));
    scoreRBCSS.setApprovalScores(Collections.singletonList(BigDecimal.TEN));
    scoreRBCSS.setRankRM(Collections.singletonList("1"));
    scoreRBCSS.setApprovalRank(Collections.singletonList("1"));
    item.setScoreRbCss(scoreRBCSS);

    List<CreditRatingResponse> lstResponse = new ArrayList<>();
    lstResponse.add(item);

    item = CreditRatingResponse.builder()
        .customerType("RB")
        .profileId("")
        .identityCard("")
        .build();

    scoreRBCSS = new ScoreRBCSS();
    scoreRBCSS.setScoresRM(Collections.singletonList(BigDecimal.TEN));
    scoreRBCSS.setApprovalScores(Collections.singletonList(BigDecimal.TEN));
    scoreRBCSS.setRankRM(Collections.singletonList("1"));
    scoreRBCSS.setApprovalRank(Collections.singletonList("1"));
    item.setScoreRbCss(scoreRBCSS);

    lstResponse.add(item);

    ApplicationEntity entityApp = new ApplicationEntity();
    entityApp.getCreditRatings().add(new ApplicationCreditRatingsEntity().withRatingId("123"));

    PostQueryCreditRatingRequest request = new PostQueryCreditRatingRequest();
    request.setCustomerType("RB");
    request.setProfileId("123");
    request.setList(new HashSet<>());

    ApplicationCreditRatingsEntity response = postCreditRatingCSSService.saveToDB(lstResponse, entityApp, request);
    assertNotNull(response);
  }

  @ParameterizedTest
  @CsvSource({"false,false,false","true,false,false","false,true,false","false,false,true"})
  void test_execute_should_be_ok(boolean hasOldData, boolean hasException, boolean identityEmpty) {
    PostQueryCreditRatingRequest request = new PostQueryCreditRatingRequest();
    request.setCustomerType("RB");
    request.setProfileId("123");

    if (!identityEmpty) {
      Set<QueryCSS> queryCSSSet = new HashSet<>();
      QueryCSS queryCSS = new QueryCSS();
      queryCSS.setIdentifierCode("1");
      queryCSSSet.add(queryCSS);
      request.setList(queryCSSSet);
    } else {
      request.setList(Collections.emptySet());
    }

    ApplicationEntity entityApp = new ApplicationEntity();
    if (hasOldData) {
      entityApp.getCreditRatings().add(new ApplicationCreditRatingsEntity().withRatingId("123"));
    }

    when(commonService.findAppByBpmId(anyString())).thenReturn(entityApp);

    if (hasException) {
      when(cssService.getScoreRB(anyString(), anyString())).thenThrow(ApprovalException.class);
      assertThrows(ApprovalException.class, () -> postCreditRatingCSSService.execute(request, "151-00000001"));
    } else {

      ScoreRBCSS scoreRBCSS = new ScoreRBCSS();
      scoreRBCSS.setScoresRM(Collections.singletonList(BigDecimal.TEN));
      scoreRBCSS.setApprovalScores(Collections.singletonList(BigDecimal.TEN));
      scoreRBCSS.setRankRM(Collections.singletonList("1"));
      scoreRBCSS.setApprovalRank(Collections.singletonList("1"));

      if (!identityEmpty) {
        when(cssService.getScoreRB(anyString(), anyString())).thenReturn(scoreRBCSS);
      }

      assertNotNull(postCreditRatingCSSService.execute(request, "151-00000001"));
    }
  }
}
