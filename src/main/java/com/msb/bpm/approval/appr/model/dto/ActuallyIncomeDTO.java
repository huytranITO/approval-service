package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
public class ActuallyIncomeDTO extends ApplicationIncomeDTO {

  @NotNull
  @Valid
  private Set<BaseIncomeDTO> incomeItems;

  public Set<BaseIncomeDTO> getIncomeItems() {
    if (CollectionUtils.isEmpty(incomeItems)) {
      return incomeItems;
    }

    // Sorted by orderDisplay
    return incomeItems.stream()
        .sorted(comparing(BaseIncomeDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
