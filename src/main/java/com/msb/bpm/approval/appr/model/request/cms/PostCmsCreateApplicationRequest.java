package com.msb.bpm.approval.appr.model.request.cms;

import com.msb.bpm.approval.appr.model.dto.cms.ApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredLoanDTO;
import com.msb.bpm.approval.appr.model.dto.cms.UnsecuredOverdraftDTO;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCmsCreateApplicationRequest {

  @NotNull
  @Valid
  private ApplicationDTO application;

  @NotNull
  @Valid
  private CustomerDTO customer;

  @NotNull
  @Valid
  private ApplicationCredit applicationCredits;

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  @ToString
  public static class ApplicationCredit {
    @Valid
    private List<UnsecuredCreditCardDTO> unsecuredCreditCard = new ArrayList<>();
    @Valid
    private List<UnsecuredLoanDTO> unsecuredLoan = new ArrayList<>();
    @Valid
    private List<UnsecuredOverdraftDTO> unsecuredOverdraft = new ArrayList<>();
  }
}
