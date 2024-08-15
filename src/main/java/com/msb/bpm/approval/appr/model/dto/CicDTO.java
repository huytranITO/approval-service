package com.msb.bpm.approval.appr.model.dto;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.msb.bpm.approval.appr.model.dto.cic.CicGroupDetail;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@ToString
public class CicDTO {

  @Size(max = 2000)
  private String explanation;

  @NotEmpty
  @Valid
  private Set<CicDetail> cicDetails;

  private Set<CicGroupDetail> cicGroupDetails;

  public Set<CicDetail> getCicDetails() {
    if (CollectionUtils.isEmpty(cicDetails)) {
      return cicDetails;
    }

    // Sorted by orderDisplay
    return cicDetails.stream()
        .sorted(comparing(CicDetail::getOrderDisplay, nullsLast(naturalOrder())))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Getter
  @Setter
  @ToString
  public static class CicDetail {

//    @NotNull
    private Long id;

//    @NotNull
    private Long customerId;

    private Long refCustomerId;

    @NotBlank
    @Size(max = 45)
    private String subject;

    @NotBlank
    @Size(max = 100)
    private String identifierCode;

//    @NotBlank
    @Size(max = 10)
    private String deftGroupCurrent;

//    @NotBlank
    @Size(max = 10)
    private String deftGroupLast12;

//    @NotBlank
    @Size(max = 10)
    private String deftGroupLast24;

//    @NotNull
    private BigDecimal totalLoanCollateral;

    private BigDecimal totalLoanCollateralUSD;

//    @NotNull
    private BigDecimal totalUnsecureLoan;

    private BigDecimal totalUnsecureLoanUSD;

//    @NotNull
    private BigDecimal totalCreditCardLimit;

    private BigDecimal totalDebtCardLimit;

//    @NotNull
    private BigDecimal totalDebt;

    @Size(max = 10)
    private String statusCode;

    @Size(max = 255)
    private String statusDescription;

//    @NotNull
    private LocalDateTime queryAt;

    private Integer orderDisplay;

    private String cicCode;

    private String customerType;

    private String productCode;

    private String errorCodeSBV;
  }
}
