package com.msb.bpm.approval.appr.model.dto.checklist;

import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        message = "{javax.validation.constraints.NotEmpty.message}",
        field = "listFile",
        fieldDependOns = {"isRequired", "returnCode"}
    )
})
public class ChecklistDto implements Serializable {
  private static final long serialVersionUID = -8047154338141498820L;
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
  @Valid
  private List<FileDto> listFile;

  private Boolean isChecked;
}
