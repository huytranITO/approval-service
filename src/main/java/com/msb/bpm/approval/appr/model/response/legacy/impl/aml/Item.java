package com.msb.bpm.approval.appr.model.response.legacy.impl.aml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

  private String id;

  private String birthDate;

  private String dataSource;

  private String idPassport;

  private String name;

  private String remark;


}
