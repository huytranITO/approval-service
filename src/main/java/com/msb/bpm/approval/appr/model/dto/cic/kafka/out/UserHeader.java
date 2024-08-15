package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserHeader {

  @JsonProperty("SocketChannel")
  private String socketChannel;

  @JsonProperty("TableName")
  private String tableName;

  @JsonProperty("MyAction")
  private String myAction;

  @JsonProperty("User")
  private String user;

  @JsonProperty("SpecialMission")
  private String specialMission;

  @JsonProperty("RequestJsonId")
  private String requestJsonId;

  @JsonProperty("SpecialMessage")
  private SpecialMessage specialMessage;

}
