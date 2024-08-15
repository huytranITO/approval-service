package com.msb.bpm.approval.appr.model.dto.formtemplate.response;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleTemplateData implements Serializable {

  private static final long serialVersionUID = 7850053164479103947L;

  private List<RuleTemplateDataDetail> data;
  private int version;
}
