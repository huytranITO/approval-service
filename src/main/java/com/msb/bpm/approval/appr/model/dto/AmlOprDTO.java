package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
public class AmlOprDTO {

//  @NotEmpty
//  @Valid
  private Set<AmlOprDtl> amls;

//  @NotEmpty
//  @Valid
  private Set<AmlOprDtl> oprs;

  private LocalDateTime firstTimeQuery;

  @NotEmpty
  @Valid
  private Set<AmlOprGeneral> generals;

  private Set<AmlOprGeneral> groupGenerals;

  public Set<AmlOprDtl> getAmls() {
    if (CollectionUtils.isEmpty(amls)) {
      return amls;
    }

    // Sorted by orderDisplay
    return amls.stream()
        .sorted(comparing(AmlOprDtl::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<AmlOprDtl> getOprs() {
    if (CollectionUtils.isEmpty(oprs)) {
      return oprs;
    }

    // Sorted by orderDisplay
    return oprs.stream()
        .sorted(comparing(AmlOprDtl::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public Set<AmlOprGeneral> getGenerals() {
    if (CollectionUtils.isEmpty(generals)) {
      return generals;
    }

    // Sorted by orderDisplay
    return generals.stream()
        .sorted(comparing(AmlOprGeneral::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Getter
  @Setter
  @With
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AmlOprDtl {

//    @NotNull
    private Long id;

    private Long customerId;

    private Long refCustomerId;

//    @NotBlank
    @Size(max = 45)
    private String subject;

//    @NotBlank
    @Size(max = 100)
    private String identifierCode;

    @Size(max = 6)
    private String queryType;

    @Size(max = 20)
    private String resultCode;

//    @NotBlank
    @Size(max = 255)
    private String resultDescription;

    @Size(max = 500)
    private String resultOnBlackList;

    private String startDate;

    private String endDate;

    private Integer orderDisplay;

    private LocalDateTime updatedAt;

    private boolean priority;
  }

  @Getter
  @Setter
  @With
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AmlOprGeneral {
    private Long amlId;

    private Long oprId;

    private Long customerId;

    private Long refCustomerId;

    @NotBlank
    @Size(max = 45)
    private String subject;

    @NotBlank
    @Size(max = 100)
    private String identifierCode;

    @NotBlank
    @Size(max = 255)
    private String amlResult;

    @NotBlank
    @Size(max = 255)
    private String oprResult;

    private Integer orderDisplay;

    private boolean priority;
  }
}
