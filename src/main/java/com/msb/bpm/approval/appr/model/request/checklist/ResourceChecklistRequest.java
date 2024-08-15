package com.msb.bpm.approval.appr.model.request.checklist;

import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceChecklistRequest {
  private Long additionalDataChecklistId;
  private String code;
  private String name;
  private String returnCode;
  private Timestamp returnDate;
  private Boolean isRequired;
  private Boolean isGenerated;
  private Boolean isError;
  private Integer orderDisplay;
  private Long checklistVersion;
  private Long ruleVersion;
  private Long checklistMappingId;
  private String groupCode;
  private Long domainObjectId;
  private String domainType;
  private List<FileDto> listFile;
}
