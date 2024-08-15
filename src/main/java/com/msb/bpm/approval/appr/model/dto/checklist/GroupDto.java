package com.msb.bpm.approval.appr.model.dto.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDto {
  Long id;
  String code;
  String name;
  Long parentId;
  Integer orderDisplay;
  String domainType;
  Long domainObjectId;
  Long ruleVersion;
}
