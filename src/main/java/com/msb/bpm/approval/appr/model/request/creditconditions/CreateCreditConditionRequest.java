package com.msb.bpm.approval.appr.model.request.creditconditions;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

@Data
public class CreateCreditConditionRequest {
    @Size(max = 255, message = "customerCifNumber không được vượt quá 255")
    private String customerCifNumber;
    @Size(max = 255, message = "customerCifNumberBPM không được vượt quá 255")
    private String customerCifNumberBPM;
    @NotNull(message = "Thiếu tên khách hàng")
    @NotBlank(message = "Tên khách hàng không được rỗng")
    @Size(max = 255, message = "Tên khách hàng không được vượt quá 255")
    private String customerName;
    @Valid
    private List<CreditConditionRequest> creditConditions;
}
