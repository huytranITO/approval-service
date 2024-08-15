package com.msb.bpm.approval.appr.model.request.data;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditConditionsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationLimitCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationPhoneExpertiseDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationRepaymentDTO;
import com.msb.bpm.approval.appr.model.dto.OtherReviewDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.ApplicationAssetAllocationDTO;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.msb.bpm.approval.appr.validator.MaxAllocationPercentConstraint;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        message = "{javax.validation.constraints.NotNull.message}",
        field = "effectivePeriod",
        fieldDependOns = "bpmId"
    )
})
@MaxAllocationPercentConstraint(
        field = "assetAllocations",
        fieldDependOns = "credits"
)
public class PostDebtInfoRequest extends PostBaseRequest {
  @Valid
  private Set<ApplicationAssetAllocationDTO> assetAllocations;

//  @NotEmpty
//  @Valid
  private Set<ApplicationLimitCreditDTO> limitCredits;

  @NotEmpty
  @Valid
  private Set<ApplicationCreditRatingsDTO> creditRatings;

  @NotEmpty
  @Valid
  private Set<ApplicationCreditDTO> credits;

  @NotNull
  @Valid
  private ApplicationRepaymentDTO repayment;

  @Valid
  private Set<ApplicationAppraisalContentDTO> specialRiskContents;

  @Valid
  private Set<ApplicationAppraisalContentDTO> additionalContents;

  @Valid
  private Set<ApplicationCreditConditionsDTO> creditConditions;

  private Set<OtherReviewDTO> otherReviews;

  private ApplicationPhoneExpertiseDTO phoneExpertise;

  private Integer effectivePeriod;

  private String effectivePeriodUnit;

  @Size(max = 100)
  @NotBlank
  private String proposalApprovalPosition;                // Cấp TQ phê duyệt đề xuất

  @NotBlank
  @Size(max = 100)
  private String loanApprovalPosition;                    // Cấp TQ phê duyệt khoản vay

  @Size(max = 255)
  private String loanApprovalPositionValue;

  @NotNull
  private Integer applicationAuthorityLevel;              // Cấp độ ưu tiên của TQ

  @NotBlank
  @Size(max = 50)
  private String priorityAuthority;                       // Cấp độ TQ (cá nhân | hội đông)

  public Set<ApplicationLimitCreditDTO> getLimitCredits() {
    if (CollectionUtils.isEmpty(limitCredits)) {
      return limitCredits;
    }

    // Sorted by orderDisplay
    return limitCredits.stream()
        .sorted(comparing(ApplicationLimitCreditDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationCreditRatingsDTO> getCreditRatings() {
    if (CollectionUtils.isEmpty(creditRatings)) {
      return creditRatings;
    }

    // Sorted by orderDisplay
    return creditRatings.stream()
        .sorted(comparing(ApplicationCreditRatingsDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationCreditDTO> getCredits() {
    if (CollectionUtils.isEmpty(credits)) {
      return credits;
    }

    // Sorted by orderDisplay
    return credits.stream()
        .sorted(comparing(ApplicationCreditDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationAppraisalContentDTO> getSpecialRiskContents() {
    if (CollectionUtils.isEmpty(specialRiskContents)) {
      return specialRiskContents;
    }

    // Sorted by orderDisplay
    return specialRiskContents.stream()
        .sorted(comparing(ApplicationAppraisalContentDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationAppraisalContentDTO> getAdditionalContents() {
    if (CollectionUtils.isEmpty(additionalContents)) {
      return additionalContents;
    }

    // Sorted by orderDisplay
    return additionalContents.stream()
        .sorted(comparing(ApplicationAppraisalContentDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationCreditConditionsDTO> getCreditConditions() {
    if (CollectionUtils.isEmpty(creditConditions)) {
      return creditConditions;
    }

    // Sorted by orderDisplay
    return creditConditions.stream()
        .sorted(comparing(ApplicationCreditConditionsDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<OtherReviewDTO> getOtherReviews() {
    if (CollectionUtils.isEmpty(otherReviews)) {
      return otherReviews;
    }

    // Sorted by orderDisplay
    return otherReviews.stream()
        .sorted(comparing(OtherReviewDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
