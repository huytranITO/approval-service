package com.msb.bpm.approval.appr.model.request.checklist;

import java.util.List;

import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateChecklistRequest {
  private String requestCode;
  private String businessFlow;
  private String phaseCode;
  private List<ChecklistDto> listChecklist;
}
