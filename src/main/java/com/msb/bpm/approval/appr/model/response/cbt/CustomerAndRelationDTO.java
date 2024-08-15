package com.msb.bpm.approval.appr.model.response.cbt;


import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class CustomerAndRelationDTO {
  @NotNull
  @Valid
  private CustomerDTO customer;

  @Valid
  private Set<CustomerDTO> customerRelations;

  @Valid
  private Set<CustomerDTO> enterpriseRelations;

  public Set<CustomerDTO> getCustomerRelations() {
    if (CollectionUtils.isEmpty(customerRelations)) {
      return customerRelations;
    }

    // Sorted by orderDisplay
    return customerRelations.stream()
        .sorted(comparing(CustomerDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<CustomerDTO> getEnterpriseRelations() {
    if (CollectionUtils.isEmpty(enterpriseRelations)) {
      return enterpriseRelations;
    }

    // Sorted by orderDisplay
    return enterpriseRelations.stream()
        .sorted(comparing(CustomerDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

}
