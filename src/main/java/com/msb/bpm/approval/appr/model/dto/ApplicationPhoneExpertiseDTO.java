package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
public class ApplicationPhoneExpertiseDTO {

  @Size(max = 20)
  private String ext;

  @Valid
  private Set<ApplicationPhoneExpertiseDtlDTO> phoneExpertiseDtls;

  public Set<ApplicationPhoneExpertiseDtlDTO> getPhoneExpertiseDtls() {
    if (CollectionUtils.isEmpty(phoneExpertiseDtls)) {
      return phoneExpertiseDtls;
    }

    // Sorted by orderDisplay
    return phoneExpertiseDtls.stream().sorted(
            comparing(ApplicationPhoneExpertiseDtlDTO::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Getter
  @Setter
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ApplicationPhoneExpertiseDtlDTO {
    private Long id;

    @Size(max = 1000)
    private String personAnswer;

    @Size(max = 1000)
    private String personAnswerValue;

    @Size(max = 20)
    private String phoneNumber;

    private String calledAt;

    @Size(max = 1000)
    private String note;

    private String ext;

    private Integer orderDisplay;
  }
}