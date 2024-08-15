package com.msb.bpm.approval.appr.model.request.flow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostCreateApplicationRequest {

  @NotNull
  @Valid
  private CustomerDTO customer;

  private String phase;
}
