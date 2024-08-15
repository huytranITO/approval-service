package com.msb.bpm.approval.appr.model.request.data;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.msb.bpm.approval.appr.model.dto.ApplicationFieldInformationDTO;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@ToString
public class PostFieldInformationRequest extends PostBaseRequest {

  @Valid
  private Set<ApplicationFieldInformationDTO> fieldInformations;

  public Set<ApplicationFieldInformationDTO> getFieldInformations() {
    if (CollectionUtils.isEmpty(fieldInformations)) {
      return fieldInformations;
    }

    // Sorted by orderDisplay
    return fieldInformations.stream().sorted(
            comparing(ApplicationFieldInformationDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
