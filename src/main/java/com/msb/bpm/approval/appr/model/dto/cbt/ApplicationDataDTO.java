package com.msb.bpm.approval.appr.model.dto.cbt;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Data
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDataDTO {
  private String processFlow;
  private String processFlowValue;
  private String submissionPurpose;
  private String submissionPurposeValue;
  private String segment;
  private List<String> documentCode;
  private String partnerCode;
  private String rm;
  private String businessUnit;
  private String businessUnitValue;
  private String area;
  private String areaValue;
  private String region;
  private String regionValue;
}
