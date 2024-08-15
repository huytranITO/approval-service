package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO.DefaultApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity_;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.EXCHANGE;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = ApplicationIncomeEntity_.INCOME_RECOGNITION_METHOD, defaultImpl = DefaultApplicationIncomeDTO.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ActuallyIncomeDTO.class, name = ACTUALLY),
    @JsonSubTypes.Type(value = ExchangeIncomeDTO.class, name = EXCHANGE)
})
public abstract class ApplicationIncomeDTO {

  private Long id;

  @NotBlank
  @Size(max = 20)
  private String incomeRecognitionMethod;

  @Size(max = 100)
  private String incomeRecognitionMethodValue;

  private Integer orderDisplay;

  private String ldpIncomeId;

  @NoArgsConstructor
  public static class DefaultApplicationIncomeDTO extends ApplicationIncomeDTO {}
}


