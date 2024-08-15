package com.msb.bpm.approval.appr.model.dto.formtemplate.response;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import lombok.Data;

@Data
public class ChecklistMessageResponseDTO implements Serializable {

  private static final long serialVersionUID = -5334956868586729036L;
  private Long additionalDataChecklistId;
  private Long id;
  private String code;
  private String name;
  private String returnCode;
  private Date returnDate;
  private Boolean isRequired;
  private Boolean isGenerated;
  private Boolean isError;
  private Integer orderDisplay;
  private Long checklistVersion;
  private Long checklistMappingId;
  private String groupCode;
  private Long domainObjectId;
  private Long maxFileSize;
  private String fileType;
  private String domainType;
  private List<ChecklistFileMessageDTO> checklistFiles;
}
