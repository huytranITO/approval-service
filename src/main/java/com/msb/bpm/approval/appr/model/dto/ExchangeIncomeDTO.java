package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
@NoArgsConstructor
@AllArgsConstructor
@With
public class ExchangeIncomeDTO extends ApplicationIncomeDTO {

  @NotBlank
  private String conversionMethod;

  private String conversionMethodValue;

  private String explanation;

  @NotNull
  private BigDecimal recognizedIncome;

  @NotEmpty
  @Valid
  private Set<BaseIncomeDTO> incomeItems;

  @Valid
  private IncomeEvaluationDTO incomeEvaluation;

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
