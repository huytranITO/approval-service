package com.msb.bpm.approval.appr.model.dto;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalAssetIncomeDTO {

  private Long id;

  @NotBlank
  private String assetGroup;

  @NotBlank
  private String assetGroupValue;


  @NotBlank
  @Size(max = 500)
  private String baseDocument;

  @NotBlank
  @Size(max = 1000)
  private String assetDescription;

  @NotNull
  @DecimalMin(value = "0")
  @Digits(integer = 20, fraction = 0)
  private BigDecimal assetValue;

  private String evaluateMethod;

  private String evaluateMethodValue;

  private Integer orderDisplay;
}
