package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.msb.bpm.approval.appr.constant.Constant;
import com.msb.bpm.approval.appr.enums.rating.RatingSystemType;
import com.msb.bpm.approval.appr.util.DateUtils;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class ApplicationCreditRatingsDTO {

  @NotNull
  private Long id;

  @NotBlank
  @Size(max = 10)
  private String ratingSystem;

  @NotBlank
  @Size(max = 20)
  private String ratingId;

  @NotBlank
  @Size(max = 250)
  private String ratingResult;

  private Integer orderDisplay;

  private String approvalComment;

  private String recommendation;

  private Set<ApplicationCreditRatingsDtlDTO> creditRatingsDtls;

  public String getRecommendation() {
    if (StringUtils.isNotEmpty(ratingSystem) && RatingSystemType.CAS.name().equals(ratingSystem)) {
      AtomicReference<String> rs = new AtomicReference<>("");
      if (CollectionUtils.isNotEmpty(getCreditRatingsDtls())) {
        getCreditRatingsDtls().stream().findFirst().ifPresent(dtlDTO -> rs.set(dtlDTO.getRecommendation()));
      }
      return rs.get();
    }
    return recommendation;
  }

  public String getRatingResult() {
    AtomicReference<String> rs = new AtomicReference<>(ratingResult);
    if (StringUtils.isNotEmpty(ratingSystem) && RatingSystemType.CAS.name().equals(ratingSystem)
            && CollectionUtils.isNotEmpty(getCreditRatingsDtls())
    ) {
      getCreditRatingsDtls().stream().findFirst().ifPresent(dtlDTO -> rs.set(dtlDTO.getRank()));
    }
    return rs.get();
  }

  public Set<ApplicationCreditRatingsDtlDTO> getCreditRatingsDtls() {
    if (CollectionUtils.isEmpty(creditRatingsDtls)) {
      return creditRatingsDtls;
    }
    if (StringUtils.isNotEmpty(ratingSystem) && RatingSystemType.CSS.name().equals(ratingSystem)) {
      creditRatingsDtls.forEach(item -> item.setScoringDateTime(
          DateUtils.format(item.getScoringTime(), Constant.DD_MM_YYYY_HH_MM_SS_A_FORMAT)));

      return creditRatingsDtls.stream()
          .sorted(comparing(ApplicationCreditRatingsDtlDTO::getScoringDateTime, nullsLast(naturalOrder())))
          .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    // Sorted by orderDisplay
    return creditRatingsDtls.stream()
            .sorted(RatingSystemType.CAS.name().equals(ratingSystem) ?
                    Comparator.comparing(ApplicationCreditRatingsDtlDTO::getScoringTime, Comparator.reverseOrder())
                    : comparing(ApplicationCreditRatingsDtlDTO::getOrderDisplay, nullsLast(naturalOrder())))
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
