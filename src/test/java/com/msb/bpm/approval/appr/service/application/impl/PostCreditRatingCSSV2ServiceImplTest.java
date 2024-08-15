package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREDIT_RATING_V2;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_A_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msb.bpm.approval.appr.enums.rating.RatingRoleType;
import com.msb.bpm.approval.appr.enums.rating.RatingSystemType;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDtlDTO;
import com.msb.bpm.approval.appr.model.dto.legacy.CreditRatingCssResponse;
import com.msb.bpm.approval.appr.model.entity.ApParamEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsDtlEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest.QueryCSS;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRBV2Response;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.ModelDetailResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.ModelResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.RawResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.RbResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.Response;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.ScoringDataResponse;
import com.msb.bpm.approval.appr.repository.ApParamRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.intergated.LegacyService;
import com.msb.bpm.approval.appr.util.DateUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostCreditRatingCSSV2ServiceImplTest {
  @Mock
  private LegacyService legacyService;
  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private ApParamRepository apParamRepository;
  @InjectMocks
  private PostCreditRatingCSSV2ServiceImpl postCreditRatingCSSV2Service;

  private PostQueryCreditRatingRequest request = new PostQueryCreditRatingRequest();

  private ApplicationCreditRatingsEntity ratingsEntity = new ApplicationCreditRatingsEntity();

  private ApplicationEntity applicationEntity = new ApplicationEntity();

  private ApplicationCreditRatingsDtlEntity ratingDtlEntity = new ApplicationCreditRatingsDtlEntity();

  private CreditRatingCssResponse creditRatingCssResponse = new CreditRatingCssResponse();

  private GetScoreRBV2Response getScoreRBV2Response = new GetScoreRBV2Response();
  @BeforeEach
  public void setUp() {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(javaTimeModule());

    // Set PostQueryCreditRatingRequest initial
    request.setCustomerType("test-customer-type");
    request.setProfileId("test-profile-id");
    Set<QueryCSS> css = new HashSet<>();
    QueryCSS queryCSS = new QueryCSS();
    queryCSS.setIdentifierCode("test-identifier-code");
    css.add(queryCSS);
    request.setList(css);

    // Set ApplicationCreditRatingsEntity
    Set<ApplicationCreditRatingsDtlEntity> applicationCreditRatingsDtls = new HashSet<>();
    ratingDtlEntity.setApplicationCreditRating(null);
    ratingDtlEntity.setExecutor("test-executor");
    ratingDtlEntity.setRank("test-rank");
    ratingDtlEntity.setRole("");
    ratingDtlEntity.setScore(0.3);
    ratingDtlEntity.setIdentityCard("test-id");
    ratingDtlEntity.setRecommendation("test-recommendation");
    ratingDtlEntity.setStatusDescription("test-status-description");
    ratingDtlEntity.setScoringSource("test-scoring-source");
    ratingDtlEntity.setScoringTime("10/10/2023 10:30:03 AM");
    ratingDtlEntity.setScoringId(1);
    ratingDtlEntity.setTypeOfModel("test-type-of-model");
    ratingDtlEntity.setApprovalComment("test-approval-comment");
    applicationCreditRatingsDtls.add(ratingDtlEntity);

    ratingsEntity.setRatingId("test-profile-id");
    ratingsEntity.setRatingSystem("test-rating-system");
    ratingsEntity.setRatingResult("test-rating-result");
    ratingsEntity.setApplication(applicationEntity);
    ratingsEntity.setCreditRatingsDtls(applicationCreditRatingsDtls);

    // Set CreditRatingCssResponse
    creditRatingCssResponse.setIdentityCard("test-id");
    Response response = new Response();
    response.setStatus("test-status");
    response.setStatusCode("test-status-code");
    RbResponse rbResponse = new RbResponse();
    ScoringDataResponse scoringDataResponse = new ScoringDataResponse();
    scoringDataResponse.setApprovalComment("test-approval-comment");
    ModelResponse modelResponse = new ModelResponse();
    modelResponse.setModelName("test-model-name");
    modelResponse.setModelType("test-model-type");
    ModelDetailResponse modelDetailResponse = new ModelDetailResponse();
    RawResponse rawResponse = new RawResponse();
    rawResponse.setRank("test-rank");
    rawResponse.setRole("");
    rawResponse.setScore("0.3");
    rawResponse.setRecommendation("test-recommendation");
    rawResponse.setType("test-type-of-model");
    rawResponse.setTimeStamp("10/10/2023 10:30:03 AM");
    rawResponse.setUser("test-executor");
    List<RawResponse> rawResponseList = new ArrayList<>();
    rawResponseList.add(rawResponse);
    modelDetailResponse.setRaw(rawResponseList);
    modelDetailResponse.setOverride(rawResponseList);
    modelResponse.setModelDetail(modelDetailResponse);
    List<ModelResponse> modelResponseList = new ArrayList<>();
    modelResponseList.add(modelResponse);
    scoringDataResponse.setModel(modelResponseList);
    rbResponse.setScoringData(scoringDataResponse);
    getScoreRBV2Response.setResponse(response);
    getScoreRBV2Response.setRbResponse(rbResponse);
    creditRatingCssResponse.setScoreRBV2Response(getScoreRBV2Response);
  }

  public JavaTimeModule javaTimeModule() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS_FORMAT);
    LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
    LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter);
    javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
    javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer);

    formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT);
    LocalDateDeserializer dateDeserializer = new LocalDateDeserializer(formatter);
    LocalDateSerializer dateSerializer = new LocalDateSerializer(formatter);
    javaTimeModule.addDeserializer(LocalDate.class, dateDeserializer);
    javaTimeModule.addSerializer(LocalDate.class, dateSerializer);

    return javaTimeModule;
  }

  @Test
  void testGetType() {
    Assert.assertEquals(POST_CREDIT_RATING_V2, postCreditRatingCSSV2Service.getType());
  }
  @Test
  void testExecute() {
    String bpmId = "test-bpm-id";
    ApplicationEntity entity = new ApplicationEntity();
    when(applicationRepository.findByBpmId(bpmId)).thenReturn(Optional.of(entity));
    doReturn(getScoreRBV2Response).when(legacyService).getScoringData("test-profile-id", "test-identifier-code");
    ApplicationCreditRatingsDTO dto = new ApplicationCreditRatingsDTO();
    dto.setRatingId("test-profile-id");
    dto.setApprovalComment("test-approval-comment");
    dto.setRecommendation("test-recommendation");
    dto.setRatingResult("test-rank");
    dto.setRatingSystem("CSS");

    ApplicationCreditRatingsDTO result = (ApplicationCreditRatingsDTO) postCreditRatingCSSV2Service.execute(request, bpmId);
    assertEquals(dto.getRatingId(), result.getRatingId());
    assertEquals(dto.getRatingSystem(), result.getRatingSystem());
    assertEquals(dto.getId(), result.getId());
    assertEquals(dto.getOrderDisplay(), result.getOrderDisplay());
  }

  @Test
  void testSortResponseByScoringTime() {
    ApplicationCreditRatingsDtlDTO dto1 = new ApplicationCreditRatingsDtlDTO();
    dto1.setScoringDateTime(DateUtils.format("10/10/2023 10:33:52 AM" , DD_MM_YYYY_HH_MM_SS_A_FORMAT));
    dto1.setRole("Customer1");
    ApplicationCreditRatingsDtlDTO dto2 = new ApplicationCreditRatingsDtlDTO();
    dto2.setScoringDateTime(DateUtils.format("10/10/2023 10:30:03 AM", DD_MM_YYYY_HH_MM_SS_A_FORMAT));
    dto2.setRole("Customer2");
    ApplicationCreditRatingsDtlDTO dto3 = new ApplicationCreditRatingsDtlDTO();
    dto3.setScoringDateTime(DateUtils.format("10/10/2023 10:29:03 AM", DD_MM_YYYY_HH_MM_SS_A_FORMAT));
    dto3.setRole("Customer1");
    ApplicationCreditRatingsDtlDTO dto4 = new ApplicationCreditRatingsDtlDTO();
    dto4.setScoringDateTime(DateUtils.format("10/10/2023 10:30:03 PM", DD_MM_YYYY_HH_MM_SS_A_FORMAT));
    dto4.setRole("Customer1");
    Set<ApplicationCreditRatingsDtlDTO> set = new HashSet<>();
    set.add(dto1);
    set.add(dto2);
    set.add(dto3);
    set.add(dto4);
    ApplicationCreditRatingsDtlDTO result = postCreditRatingCSSV2Service.sortResponseByScoringTime(set);
    assertEquals(result.getScoringTime(), dto4.getScoringTime());
  }
  @Test
  void testSaveToDB() {
     List<CreditRatingCssResponse> lstResponse = new ArrayList<>();
     lstResponse.add(creditRatingCssResponse);

    ApplicationCreditRatingsEntity result =
        postCreditRatingCSSV2Service.saveToDB(lstResponse, applicationEntity, request);

    assertEquals(result.getRatingId(), ratingsEntity.getRatingId());

  }
  @Test
  void testBuildCRDtlEntity() {
    // Check result
    Set<ApplicationCreditRatingsDtlEntity> result =
        postCreditRatingCSSV2Service.buildCRDtlEntity(creditRatingCssResponse, null);
    // Assert value of result
    result.forEach(c -> {
      assertEquals(c.getRank(), ratingDtlEntity.getRank());
      assertEquals(c.getRole(), ratingDtlEntity.getRole());
      assertEquals(c.getScore(), ratingDtlEntity.getScore());
      assertEquals(c.getScoringTime(), ratingDtlEntity.getScoringTime());
      assertEquals(c.getExecutor(), ratingDtlEntity.getExecutor());
      assertEquals(c.getIdentityCard(), c.getIdentityCard());
      assertEquals(c.getRecommendation(), c.getRecommendation());
      assertEquals(c.getScoringId(), c.getScoringId());
      assertEquals(c.getStatus(), c.getStatus());
      assertEquals(c.getStatusDescription(), c.getStatusDescription());
    });
  }
  @Test
  void testMappingRole () {
    String cssRole = "role 1";
    String result = "Test Value";
    ApParamEntity apParamEntity = new ApParamEntity();
    apParamEntity.setMessage(result);
    when(apParamRepository.findByCodeAndType(cssRole, RatingRoleType.CSS_ROLE.name()))
        .thenReturn(Optional.of(apParamEntity));
    Assert.assertEquals(result, postCreditRatingCSSV2Service.mappingRole(cssRole));
  }
  @Test
  void testMappingStatus () {
    String cssStatus = "status";
    String result = "Test Value";
    ApParamEntity apParamEntity = new ApParamEntity();
    apParamEntity.setMessage(result);
    when(apParamRepository.findByCodeAndType(cssStatus, RatingSystemType.CAS.name()))
        .thenReturn(Optional.of(apParamEntity));
    Assert.assertEquals(result, postCreditRatingCSSV2Service.mappingStatus(cssStatus));
  }
}

