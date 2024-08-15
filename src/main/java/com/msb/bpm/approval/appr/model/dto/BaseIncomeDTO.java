package com.msb.bpm.approval.appr.model.dto;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.ENTERPRISE_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INCOME_TYPE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INDIVIDUAL_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.OTHER;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.PROPERTY_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.RENTAL;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.SALARY;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO.DefaultBaseIncomeDTO;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = INCOME_TYPE, defaultImpl = DefaultBaseIncomeDTO.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SalaryIncomeDTO.class, name = SALARY),
    @JsonSubTypes.Type(value = RentalIncomeDTO.class, name = RENTAL),
    @JsonSubTypes.Type(value = BusinessIncomeDTO.class, names = {INDIVIDUAL_BUSINESS, ENTERPRISE_BUSINESS}),
    @JsonSubTypes.Type(value = OtherIncomeDTO.class, name = OTHER),
    @JsonSubTypes.Type(value = PropertyBusinessIncomeDTO.class, name = PROPERTY_BUSINESS)
})
public abstract class BaseIncomeDTO {
  private Long id;

  @NotBlank
  @Size(max = 10)
  private String incomeType;

  private Long customerId;

  private Long refCustomerId;   // ID khách hing từ customer-additional-info

  @NotBlank
  @Size(max = 10)
  private String incomeOwner;

  @Size(max = 100)
  private String incomeOwnerValue;

  @NotBlank
  @Size(max = 255)
  private String incomeOwnerName;

  @Size(max = 100)
  private String incomeTypeValue;

  private BigDecimal recognizedIncome;

  private Integer orderDisplay;

  @NoArgsConstructor
  public static class DefaultBaseIncomeDTO extends BaseIncomeDTO {}
}
