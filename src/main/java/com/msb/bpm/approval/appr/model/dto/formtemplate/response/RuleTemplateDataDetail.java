package com.msb.bpm.approval.appr.model.dto.formtemplate.response;

import com.msb.bpm.approval.appr.model.response.rule.EntryResponse;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleTemplateDataDetail implements Serializable {

  private static final long serialVersionUID = 7993927734921115703L;
  private EntryResponse templateCode;
  private EntryResponse checklistCode;
  private EntryResponse docNumber;
  private EntryResponse opsMapping;
}
