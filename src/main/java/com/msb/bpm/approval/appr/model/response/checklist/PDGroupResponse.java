package com.msb.bpm.approval.appr.model.response.checklist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDGroupResponse {

  private Long id;

  private String code;

  private String name;

  private Long parentId;

  private Integer orderDisplay;

  private Integer version;
}
