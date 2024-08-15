package com.msb.bpm.approval.appr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class FieldErrorDTO {

  private String fieldName;

  private String errorMessage;
}
