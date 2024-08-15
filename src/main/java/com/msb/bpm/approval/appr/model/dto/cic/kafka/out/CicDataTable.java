package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.msb.bpm.approval.appr.kafka.CICDSTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CicDataTable {

  @CICDSTable(name = "CIC_CLIENT_QUESTION")
  private CICClientQuestionData cicClientQuestionData;

  @CICDSTable(name = "CIC_TBL_OUT_CLIENT")
  private CICClientData cicClientData;
}
