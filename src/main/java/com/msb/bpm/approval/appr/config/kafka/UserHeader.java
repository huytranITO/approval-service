package com.msb.bpm.approval.appr.config.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserHeader {

  @JsonProperty("user")
  private String user;

  @JsonProperty("table-name")
  private String tableName;

  @JsonProperty("sql-type")
  private String sqlType;

  @JsonProperty("crt-code")
  private String crtCode;

}
