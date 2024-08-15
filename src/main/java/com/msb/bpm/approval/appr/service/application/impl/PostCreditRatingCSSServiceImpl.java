package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREDIT_RATING;

import com.msb.bpm.approval.appr.enums.common.PhaseCode;
import com.msb.bpm.approval.appr.enums.rating.RatingSystemType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditRatingsMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.css.CreditRatingResponse;
import com.msb.bpm.approval.appr.model.dto.css.ScoreRBCSS;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsDtlEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryCreditRatingRequest.QueryCSS;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.intergated.CSSService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 9/5/2023, Tuesday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class PostCreditRatingCSSServiceImpl implements
    BaseService<Object, PostQueryCreditRatingRequest> {

  private final CSSService cssService;
  private final CommonService commonService;
  private final ApplicationRepository applicationRepository;

  @Override
  public String getType() {
    return POST_CREDIT_RATING;
  }

  @SneakyThrows
  @Override
  @Transactional
  public Object execute(PostQueryCreditRatingRequest request, Object... obj) {
    log.info("START with request={}, obj={}", request, obj);
    String bpmId = Arrays.stream(obj).toArray()[0].toString();
    ApplicationEntity entityApp = commonService.findAppByBpmId(bpmId);
    List<CreditRatingResponse> lstResponse = new ArrayList<>();
    Exception exception = null;
    for (QueryCSS item : request.getList()) {
      try {
        ScoreRBCSS scoreRbCss = cssService.getScoreRB(request.getProfileId(),
            item.getIdentifierCode());
        lstResponse.add(CreditRatingResponse.builder()
            .identityCard(item.getIdentifierCode())
            .scoreRbCss(scoreRbCss)
            .profileId(request.getProfileId())
            .customerType(request.getCustomerType())
            .build());
      } catch (Exception ex) {
        log.error("getScoreRB with ProfileId={}, IdentifierCode={}, Error: ", request.getProfileId(),
            item.getIdentifierCode(), ex);
        if (Objects.isNull(exception)) {
          exception = ex;
        }
      }

      Thread.sleep(1000);
    }
    if (Objects.nonNull(exception) && CollectionUtils.isEmpty(lstResponse)
        && exception instanceof ApprovalException) {
      throw (ApprovalException)(exception);
    }
    if (CollectionUtils.isNotEmpty(lstResponse)) {
      saveToDB(lstResponse, entityApp, request);
      ApplicationCreditRatingsEntity acrEntityResult = entityApp.getCreditRatings().stream()
          .filter(e -> e.getRatingId().equalsIgnoreCase(request.getProfileId()))
          .findFirst().orElse(null);
      if(Objects.nonNull(acrEntityResult)) {
        return ApplicationCreditRatingsMapper.INSTANCE.toApplicationCreditRatings(acrEntityResult);
      }
    }
    log.info("END with request={}, obj={}", request, obj);

    return new ApplicationCreditRatingsDTO();
  }

  @Transactional
  public ApplicationCreditRatingsEntity saveToDB(List<CreditRatingResponse> lstResponse,
      ApplicationEntity entityApp, PostQueryCreditRatingRequest request) {
    log.info("saveToDB START with lstResponse={}, entityApp={}, request={}", lstResponse, entityApp,
        request);
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
    for (CreditRatingResponse item : lstResponse) {
      lstACRDtlEntity.addAll(buildCRDtlEntity(item, aCREntity));
    }

    if(CollectionUtils.isNotEmpty(lstACRDtlEntity)) {
      for (ApplicationCreditRatingsDtlEntity item : lstACRDtlEntity) {
        if (Objects.nonNull(item.getRank())) {
          aCREntity.setRatingResult(item.getRank());
          break;
        }
      }
    }
    aCREntity.getCreditRatingsDtls().addAll(lstACRDtlEntity);

    ApplicationEntity result = applicationRepository.save(entityApp);
    log.info("saveToDB END with lstResponse={}, entityApp={}, request={}, result={}",
        lstResponse, entityApp, request, result);
    return aCREntity;
  }

  @Transactional
  public Set<ApplicationCreditRatingsDtlEntity> buildCRDtlEntity(CreditRatingResponse item,
      ApplicationCreditRatingsEntity aCREntity) {
    Set<ApplicationCreditRatingsDtlEntity> lstACRDtlEntity = new HashSet<>();
    List<Integer> listSize = new ArrayList<>(Arrays.asList(item.getScoreRbCss().getScoresRM().size(),
        item.getScoreRbCss().getApprovalScores().size(),
        item.getScoreRbCss().getRankRM().size(), item.getScoreRbCss().getApprovalRank().size()));

    for (int i = 0; i < Collections.max(listSize); i++) {
      // RM
      lstACRDtlEntity.add(ApplicationCreditRatingsDtlEntity.builder()
          .applicationCreditRating(aCREntity)
          .identityCard(item.getIdentityCard())
          .score(item.getScoreRbCss().getScoresRM().size() > i  &&
              Objects.nonNull(item.getScoreRbCss().getScoresRM().get(i)) ?
              item.getScoreRbCss().getScoresRM() .get(i).doubleValue() : null)
          .rank(item.getScoreRbCss().getRankRM().size() > i  && Objects.nonNull(item.getScoreRbCss()
              .getRankRM().get(i)) ? item.getScoreRbCss().getRankRM().get(i) : null)
          .role(PhaseCode.RM.name())
          .build());
      // CA
      lstACRDtlEntity.add(ApplicationCreditRatingsDtlEntity.builder()
          .applicationCreditRating(aCREntity)
          .identityCard(item.getIdentityCard())
          .score(item.getScoreRbCss().getApprovalScores().size() > i &&
              Objects.nonNull(item.getScoreRbCss().getApprovalScores().get(i)) ?
              item.getScoreRbCss().getApprovalScores().get(i).doubleValue() : null)
          .rank(item.getScoreRbCss().getApprovalRank().size() > i  &&
              Objects.nonNull(item.getScoreRbCss().getApprovalRank().get(i)) ? item.getScoreRbCss()
              .getApprovalRank().get(i) : null)
          .role(PhaseCode.CA.name())
          .build());
    }

    return lstACRDtlEntity;
  }
}
