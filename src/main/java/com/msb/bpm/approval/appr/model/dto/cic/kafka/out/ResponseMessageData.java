package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ResponseMessageData {

  @JsonProperty("FieldList")
  private List<Field> fieldList;

  @JsonProperty("Data")
  private List<Map<String, String>> data;
}
