package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.msb.bpm.approval.appr.model.dto.application.ApplicationContactDTO;
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
public class CustomerAndRelationPersonDTO {
  @NotNull
  @Valid
  private CustomerDTO customer;

  @Valid
  private Set<CustomerDTO> customerRelations;

  @Valid
  private Set<CustomerDTO> enterpriseRelations;

  private Set<ApplicationContactDTO> applicationContact;

  @NotNull
  @Valid
  private CicDTO cic;

  @NotNull
  @Valid
  private AmlOprDTO amlOpr;

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

  public Set<ApplicationContactDTO> getApplicationContact() {
    if (CollectionUtils.isEmpty(applicationContact)) {
      return applicationContact;
    }

    // Sorted by orderDisplay
    return applicationContact.stream()
        .sorted(comparing(ApplicationContactDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
