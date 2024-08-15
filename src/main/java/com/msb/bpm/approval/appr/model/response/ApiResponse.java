package com.msb.bpm.approval.appr.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.dto.FieldErrorDTO;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ApiResponse {

  private String code = DomainCode.SUCCESS.getCode();

  private String message;

  private Object data;

  private Set<FieldErrorDTO> fieldErrors;
}
