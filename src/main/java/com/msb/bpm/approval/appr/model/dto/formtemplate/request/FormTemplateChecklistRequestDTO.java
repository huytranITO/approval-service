package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FormTemplateChecklistRequestDTO implements Serializable {

  private static final long serialVersionUID=-9162315412721778582L;

  private String jsonData;
  private Long additionalDataChecklistId;
  private String code;
  private String name;
  private String returnCode;
  private String returnDate;
  private Boolean isRequired;
  private Boolean isGenerated;
  private Boolean isError;
  private Integer orderDisplay;
  private Long checklistVersion;
  private Long checklistMappingId;
  private String groupCode;
  private Long domainObjectId;
  private String domainType;
  private Long maxFileSize;
  private String fileType;
  private Long ruleVersion;
  private List<FormTemplateFileRequestDTO> files;
}
