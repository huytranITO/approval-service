package com.msb.bpm.approval.appr.model.request.bpm.operation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Condition {
  private String name;
  private String type;
  private String description;
  private String level;
  private String startDate;
  private String endDate;
}
