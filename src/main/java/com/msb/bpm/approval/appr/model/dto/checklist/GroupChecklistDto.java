package com.msb.bpm.approval.appr.model.dto.checklist;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChecklistDto implements Serializable {
  private static final long serialVersionUID = 1527891011556487584L;

  private boolean completed = Boolean.FALSE;

  private boolean rmCommitStatus;
  String requestCode;
  Boolean isCompleted;
  List<GroupDto> listGroup;

  @NotEmpty
  @Valid
  List<ChecklistDto> listChecklist;
}
