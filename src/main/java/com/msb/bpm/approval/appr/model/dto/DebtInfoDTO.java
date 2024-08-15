package com.msb.bpm.approval.appr.model.dto;

import com.msb.bpm.approval.appr.model.dto.collateral.ApplicationAssetAllocationDTO;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
public class DebtInfoDTO {
  private String type;
  private boolean completed = Boolean.FALSE;
  private Set<ApplicationLimitCreditDTO> limitCredits;
  private Set<ApplicationCreditDTO> credits;
  private Set<ApplicationCreditRatingsDTO> creditRatings;
  private ApplicationRepaymentDTO repayment;
  private Set<ApplicationAppraisalContentDTO> specialRiskContents;
  private Set<ApplicationAppraisalContentDTO> additionalContents;
  private Set<OtherReviewDTO> otherReviews;
  private List<ApplicationCreditConditionsDTO> creditConditions;
  private ApplicationPhoneExpertiseDTO phoneExpertise;
  private Integer effectivePeriod;
  private String effectivePeriodUnit;
  private String proposalApprovalPosition;        // Cấp TQ phê duyệt đề xuất
  private String loanApprovalPosition;            // Cấp TQ phê duyệt khoản vay

  private String loanApprovalPositionValue;       // Cấp TQ phê duyệt khoản vay (text)
  private Integer applicationAuthorityLevel;      // Cấp độ ưu tiên của TQ phê duyệt khoản vay
  private String priorityAuthority;               // Cấp độ TQ (cá nhân / hội đông) phê duyệt khoản vay

  private Set<ApplicationAssetAllocationDTO> assetAllocations;  // Phan bo tai san

  public Set<ApplicationLimitCreditDTO> getLimitCredits() {
    if (CollectionUtils.isEmpty(limitCredits)) {
      return limitCredits;
    }
    return limitCredits
        .stream()
        .sorted(
            comparing(ApplicationLimitCreditDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationCreditDTO> getCredits() {
    if (CollectionUtils.isEmpty(credits)) {
      return credits;
    }
    return credits
        .stream()
        .sorted(
            comparing(ApplicationCreditDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationCreditRatingsDTO> getCreditRatings() {
    if (CollectionUtils.isEmpty(creditRatings)) {
      return creditRatings;
    }
    return creditRatings
        .stream()
        .sorted(
            comparing(ApplicationCreditRatingsDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationAppraisalContentDTO> getSpecialRiskContents() {
    if (CollectionUtils.isEmpty(specialRiskContents)) {
      return specialRiskContents;
    }
    return specialRiskContents
        .stream()
        .sorted(
            comparing(ApplicationAppraisalContentDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<ApplicationAppraisalContentDTO> getAdditionalContents() {
    if (CollectionUtils.isEmpty(additionalContents)) {
      return additionalContents;
    }
    return additionalContents
        .stream()
        .sorted(
            comparing(ApplicationAppraisalContentDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<OtherReviewDTO> getOtherReviews() {
    if (CollectionUtils.isEmpty(otherReviews)) {
      return otherReviews;
    }
    return otherReviews
        .stream()
        .sorted(
            comparing(OtherReviewDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public List<ApplicationCreditConditionsDTO> getCreditConditions() {
    if (CollectionUtils.isEmpty(creditConditions)) {
      return creditConditions;
    }
    return creditConditions
        .stream()
        .sorted(
            comparing(ApplicationCreditConditionsDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toList());
  }
}
