package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.LinkedHashSet;
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
public class InitializeInfoDTO {
  private String type;
  private boolean completed = Boolean.FALSE;
  private ApplicationDTO application;
  private CustomerAndRelationPersonDTO customerAndRelationPerson;
  private Set<ApplicationIncomeDTO> incomes;

  public Set<ApplicationIncomeDTO> getIncomes() {
    if (CollectionUtils.isEmpty(incomes)) {
      return incomes;
    }

    // Sorted by orderDisplay
    return incomes.stream()
        .sorted(comparing(ApplicationIncomeDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
