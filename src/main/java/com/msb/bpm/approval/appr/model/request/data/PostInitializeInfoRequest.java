package com.msb.bpm.approval.appr.model.request.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.msb.bpm.approval.appr.model.dto.ApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "incomes",
        fieldNeedValidates = {"rankType", "kpiRating", "recognizedIncome", "rentalPrice",
            "productionProcess", "recordMethod", "input", "output", "businessScale", "inventory",
            "evaluationPeriod", "incomeMonthly", "expenseMonthly", "profitMonthly", "profitMargin",
            "capitalContributionRate"},
        fieldDependOns = "customerAndRelationPerson"
    ),
    @CustomValidationFieldDependOn(
        field = "customerAndRelationPerson",
        fieldNeedValidates = {"cic", "amlOpr"},
        fieldDependOns = "bpmId"
    ),
    @CustomValidationFieldDependOn(
            field = "customerAndRelationPerson",
            fieldNeedValidates = {"customerRelations"}
    )
})
public class PostInitializeInfoRequest extends PostBaseRequest {

  @NotNull
  @Valid
  private ApplicationDTO application;

  @NotNull
  @Valid
  private CustomerAndRelationPersonDTO customerAndRelationPerson;

  @NotEmpty
  @Valid
  private Set<ApplicationIncomeDTO> incomes;

}
