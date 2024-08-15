package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREDIT_RATING_V2;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.constant.Constant;
import com.msb.bpm.approval.appr.enums.rating.RatingRoleType;
import com.msb.bpm.approval.appr.enums.rating.RatingSystemType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditRatingsMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDtlDTO;
import com.msb.bpm.approval.appr.model.dto.legacy.CreditRatingCssResponse;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsDtlEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest.QueryCSS;
import com.msb.bpm.approval.appr.model.response.legacy.impl.css.GetScoreRBV2Response;
import com.msb.bpm.approval.appr.repository.ApParamRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.intergated.LegacyService;
import com.msb.bpm.approval.appr.util.DateUtils;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCreditRatingCSSV2ServiceImpl implements
    BaseService<Object, PostQueryCreditRatingRequest> {

  private final LegacyService legacyService;
  private final ApplicationRepository applicationRepository;
  private final ObjectMapper objectMapper;
  private final ApParamRepository apParamRepository;
  @Override
  public String getType() {
    return POST_CREDIT_RATING_V2;
  }
  @SneakyThrows
  @Override
  @Transactional
  public Object execute(PostQueryCreditRatingRequest request, Object... obj) {
    log.info("START PostQueryCreditRating with request={}, obj={}", JsonUtil.convertObject2String(request, objectMapper), obj);
    String bpmId = Arrays.stream(obj).toArray()[0].toString();
    ApplicationEntity entityApp = applicationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));

    //Get credit rating score css from legacy
    List<CreditRatingCssResponse> lstResponse = new ArrayList<>();
    Exception exception = null;

    for (QueryCSS c : request.getList()) {
      try {
        GetScoreRBV2Response response = legacyService.getScoringData(request.getProfileId(), c.getIdentifierCode());
        if(Objects.nonNull(response) && Objects.nonNull(response.getRbResponse()) &&
            Objects.nonNull(response.getRbResponse().getScoringData())) {
          CreditRatingCssResponse creditRatingCssResponse = new CreditRatingCssResponse();
          creditRatingCssResponse.setScoreRBV2Response(response);
          creditRatingCssResponse.setCustomerType(request.getCustomerType());
          creditRatingCssResponse.setProfileId(request.getProfileId());
          creditRatingCssResponse.setIdentityCard(c.getIdentifierCode());
          creditRatingCssResponse.setScoreRBV2Response(response);
          lstResponse.add(creditRatingCssResponse);
        }
      } catch (Exception ex) {
        log.error("getScoreRB with ProfileId={}, IdentifierCode={}, Error: ", request.getProfileId(),
            c.getIdentifierCode(), ex);
        if (Objects.isNull(exception)) {
          exception = ex;
        }
      }

      Thread.sleep(1000);
    }
    if (Objects.nonNull(exception) && CollectionUtils.isEmpty(lstResponse)
        && exception instanceof ApprovalException) {
      throw (ApprovalException) (exception);
    }
    // Save to DB
    if (CollectionUtils.isEmpty(lstResponse)) {
      log.info("END PostQueryCreditRating with request={}, obj={} response={}", JsonUtil.convertObject2String(request, objectMapper), obj, new ApplicationCreditRatingsDTO());
      return new ApplicationCreditRatingsDTO();
    }
    saveToDB(lstResponse, entityApp, request);
    ApplicationCreditRatingsEntity acrEntityResult = entityApp.getCreditRatings().stream()
        .filter(e -> e.getRatingId().equalsIgnoreCase(request.getProfileId()))
        .findFirst().orElse(null);
    ApplicationCreditRatingsDTO acrDTO = new ApplicationCreditRatingsDTO();
    if (Objects.nonNull(acrEntityResult)) {
      acrDTO = ApplicationCreditRatingsMapper.INSTANCE.toApplicationCreditRatings(acrEntityResult);
      if (CollectionUtils.isNotEmpty(acrDTO.getCreditRatingsDtls())) {
        ApplicationCreditRatingsDtlDTO applicationCreditRatingsDtlDTO =
            sortResponseByScoringTime(acrDTO.getCreditRatingsDtls());
        log.info("END sortResponseByScoringTime with response={}", JsonUtil.convertObject2String(applicationCreditRatingsDtlDTO, objectMapper));
        acrDTO.setRatingResult(applicationCreditRatingsDtlDTO.getRank());
        acrDTO.setApprovalComment(applicationCreditRatingsDtlDTO.getApprovalComment());
        acrDTO.setRecommendation(applicationCreditRatingsDtlDTO.getRecommendation());

        log.info("END PostQueryCreditRating with request={}, obj={} and response={}",
            JsonUtil.convertObject2String(request, objectMapper), obj, JsonUtil.convertObject2String(acrDTO, objectMapper));
        return acrDTO;
      }
    }
    log.info("END PostQueryCreditRating with request={}, obj={} and response={}",
        JsonUtil.convertObject2String(request, objectMapper), obj, JsonUtil.convertObject2String(acrDTO, objectMapper));
    return acrDTO;
  }

  public ApplicationCreditRatingsDtlDTO sortResponseByScoringTime(
      Set<ApplicationCreditRatingsDtlDTO> creditRatingsDtls) {
    log.info("Start sortResponseByScoringTime");
    return creditRatingsDtls.stream()
        .sorted(comparing(ApplicationCreditRatingsDtlDTO::getScoringDateTime, nullsLast(naturalOrder())).reversed())
        .findFirst()
        .orElse(new ApplicationCreditRatingsDtlDTO());
  }

  @Transactional
  public ApplicationCreditRatingsEntity saveToDB(List<CreditRatingCssResponse> lstResponse, ApplicationEntity entityApp, PostQueryCreditRatingRequest request) {
    log.info("saveToDB START with lstResponse={}, request={}", JsonUtil.convertObject2String(lstResponse, objectMapper),
       JsonUtil.convertObject2String(request, objectMapper));
    // Save to DB
    ApplicationCreditRatingsEntity aCREntity;
    Set<ApplicationCreditRatingsDtlEntity> lstACRDtlEntity = new HashSet<>();
    Set<ApplicationCreditRatingsEntity> lstCREntity = entityApp.getCreditRatings();
    Optional<ApplicationCreditRatingsEntity> acrEntityOptional = lstCREntity.stream()
        .filter(e -> Objects.nonNull(request.getProfileId())
            && request.getProfileId().equals(e.getRatingId())).findFirst();
    if (acrEntityOptional.isPresent()) {
      aCREntity = acrEntityOptional.get();
      aCREntity.setUpdatedAt(LocalDateTime.now());
      // clear child
      aCREntity.getCreditRatingsDtls().clear();
    } else {
      aCREntity = new ApplicationCreditRatingsEntity().withApplication(entityApp);
      entityApp.getCreditRatings().add(aCREntity);
    }
    aCREntity.setRatingId(request.getProfileId());
    aCREntity.setRatingSystem(RatingSystemType.CSS.name());

    // Build ApplicationCreditRatingsDtlEntity
    lstResponse.forEach(score ->
        lstACRDtlEntity.addAll(buildCRDtlEntity(score, aCREntity)));

    if (CollectionUtils.isNotEmpty(lstACRDtlEntity)) {
      // Sort ApplicationCreditRatingsDtlEntity by scoring time
      ApplicationCreditRatingsDtlEntity dtlEntity = lstACRDtlEntity.stream()
          .sorted(Comparator.nullsLast((x1 , x2) -> DateUtils.format(x2.getScoringTime(), Constant.DD_MM_YYYY_HH_MM_SS_A_FORMAT).compareTo(DateUtils.format(x1.getScoringTime(), Constant.DD_MM_YYYY_HH_MM_SS_A_FORMAT))))
          .findFirst()
          .orElse(new ApplicationCreditRatingsDtlEntity());
      // Save approval comment and recommendation follow the latest dtlEntity
      aCREntity.setApprovalComment(dtlEntity.getApprovalComment());
      aCREntity.setRecommendation(dtlEntity.getRecommendation());
      aCREntity.setRatingResult(dtlEntity.getRank());
    }
    aCREntity.getCreditRatingsDtls().addAll(lstACRDtlEntity);
    applicationRepository.save(entityApp);

    log.info("saveToDB END with lstResponse={},request={}",
        JsonUtil.convertObject2String(lstResponse, objectMapper),
        JsonUtil.convertObject2String(request, objectMapper));
    return aCREntity;
  }
  @Transactional(readOnly = true)
  public Set<ApplicationCreditRatingsDtlEntity> buildCRDtlEntity(CreditRatingCssResponse scoreResponse,
      ApplicationCreditRatingsEntity aCREntity) {
    log.info("START save ApplicationCreditRatingDtl with CreditRatingCssResponse={}",
        JsonUtil.convertObject2String(scoreResponse, objectMapper));
    Set<ApplicationCreditRatingsDtlEntity> lstACRDtlEntity = new HashSet<>();
    // Get List raw
    scoreResponse.getScoreRBV2Response().getRbResponse().getScoringData().getModel()
        .stream()
        .filter(x -> CollectionUtils.isNotEmpty(x.getModelDetail().getRaw()))
        .forEach(
            model -> model.getModelDetail().getRaw().forEach(
                raw -> lstACRDtlEntity.add(ApplicationCreditRatingsDtlEntity.builder()
                    .applicationCreditRating(aCREntity)
                    .role(mappingRole(raw.getRole()))
                    .rank(raw.getRank())
                    .score(Double.valueOf(raw.getScore()))
                    .identityCard(scoreResponse.getIdentityCard())
                    .status(scoreResponse.getScoreRBV2Response().getResponse().getStatusCode())
                    .statusDescription(mappingStatus(scoreResponse.getScoreRBV2Response().getResponse().getStatusCode()))
                    .recommendation(raw.getRecommendation())
                    .typeOfModel(model.getModelType())
                    .scoringSource(RatingSystemType.CSS.name())
                    .scoringTime(raw.getTimeStamp())
                    .executor(raw.getUser())
                    .approvalComment(scoreResponse.getScoreRBV2Response().getRbResponse().getScoringData().getApprovalComment())
                    .build()
                )
            ));
    // Get List of override
    scoreResponse.getScoreRBV2Response().getRbResponse().getScoringData().getModel()
        .stream()
        .filter(x -> CollectionUtils.isNotEmpty(x.getModelDetail().getOverride()))
        .forEach(
            model -> model.getModelDetail().getOverride().forEach(
                override -> lstACRDtlEntity.add(ApplicationCreditRatingsDtlEntity.builder()
                    .applicationCreditRating(aCREntity)
                    .role(mappingRole(override.getRole()))
                    .rank(override.getRank())
                    .score(Double.valueOf(override.getScore()))
                    .identityCard(scoreResponse.getIdentityCard())
                    .status(scoreResponse.getScoreRBV2Response().getResponse().getStatusCode())
                    .statusDescription(mappingStatus(scoreResponse.getScoreRBV2Response().getResponse().getStatusCode()))
                    .recommendation(override.getRecommendation())
                    .typeOfModel(model.getModelType() + "-override")
                    .scoringSource(RatingSystemType.CSS.name())
                    .scoringTime(override.getTimeStamp())
                    .executor(override.getUser())
                    .approvalComment(scoreResponse.getScoreRBV2Response().getRbResponse().getScoringData().getApprovalComment())
                    .build()
                )
            ));
    log.info("END save ApplicationCreditRatingDtl with CreditRatingCssResponse={}",
        JsonUtil.convertObject2String(scoreResponse, objectMapper));
    return lstACRDtlEntity;
  }
  public String mappingRole(String cssRole) {
    AtomicReference<String> role = new AtomicReference<>("");
    apParamRepository.findByCodeAndType(cssRole, RatingRoleType.CSS_ROLE.name())
        .ifPresent(apParamEntity -> role.set(apParamEntity.getMessage()));
    return role.get();
  }
  public String mappingStatus(String statusCode) {
    AtomicReference<String> message = new AtomicReference<>("");
    apParamRepository.findByCodeAndType(statusCode, RatingSystemType.CAS.name())
        .ifPresent(apParamEntity -> message.set(apParamEntity.getMessage()));
    return message.get();
  }
}
