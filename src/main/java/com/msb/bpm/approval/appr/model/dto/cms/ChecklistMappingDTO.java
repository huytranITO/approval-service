package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChecklistMappingDTO {
  private String object_type;

  private Long checklistMappingId;
}

