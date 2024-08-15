package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Field {

  @JsonProperty("FieldName")
  private String fieldName;

  @JsonProperty("FieldType")
  private String fieldType;
}
