package com.msb.bpm.approval.appr.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationCreditOverdraftDTO extends ApplicationCreditDTO {

  private Long appCreditOverdraftId;

  @NotBlank
  @Size(max = 255)
  private String interestRateCode;

  @NotBlank
  @Size(max = 250)
  private String productName;

  @NotBlank
  @Size(max = 10)
  private String loanPurpose;

  @Size(max = 100)
  private String loanPurposeValue;

  @NotNull
  private Integer limitSustentivePeriod;

//  @NotBlank
  @Size(max = 10)
  private String debtPayMethod;

  @Size(max = 100)
  private String debtPayMethodValue;

  @Size(max = 1000)
  private String infoAdditional;

  // Số acf
  private String acfNo;

  // Số tài khoản
  private String accountNo;

  // Trạng thái từ bpm vận hành
  private String status;

  // Mã sản phẩm chi tiết
  @NotBlank
  @Size(max = 500)
  private String productInfoCode;

  // Tên sản phẩm chi tiết
  @NotBlank
  @Size(max = 500)
  private String productInfoName;
}
