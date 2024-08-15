package com.msb.bpm.approval.appr.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

@Getter
@Setter
public class PropertyBusinessIncomeDTO extends BaseIncomeDTO {

  @NotNull
  private Integer businessExperience;

  private String experienceTime;

  @Size(max = 2000)
  private String accumulateAsset;

  @Size(max = 2000)
  private String businessScale;

  @Size(max = 2000)
  private String incomeBase;

  @Size(max = 2000)
  private String basisIncome;

  @Size(max = 2000)
  private String incomeAssessment;

  @Size(max = 2000)
  private String businessPlan;

  private String ldpPropertyBusinessId;

  @Valid
  private Set<CustomerTransactionIncomeDTO> customerTransactionIncomes;
  public Set<CustomerTransactionIncomeDTO> getCustomerTransactionIncomes() {
    if (CollectionUtils.isEmpty(customerTransactionIncomes)) {
      return customerTransactionIncomes;
    }

    // Sorted by orderDisplay
    return customerTransactionIncomes.stream()
            .sorted(comparing(CustomerTransactionIncomeDTO::getOrderDisplay, nullsLast(naturalOrder())))
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
