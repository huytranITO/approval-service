package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
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
public class FieldInforDTO {
  private String type;
  private boolean completed = Boolean.FALSE;
  private Set<ApplicationFieldInformationDTO> fieldInformations;

  public Set<ApplicationFieldInformationDTO> getFieldInformations() {
    if (CollectionUtils.isEmpty(fieldInformations)) {
      return fieldInformations;
    }
    return fieldInformations
        .stream()
        .sorted(
            comparing(ApplicationFieldInformationDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
