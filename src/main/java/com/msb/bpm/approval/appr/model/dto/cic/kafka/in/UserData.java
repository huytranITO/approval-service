package com.msb.bpm.approval.appr.model.dto.cic.kafka.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

  @JsonProperty("User")
  private String user;

  @JsonProperty("SocketChannel")
  private String socketChannel;

  @JsonProperty("MyAction")
  private String myAction;

  @JsonProperty("TableName")
  private String tableName;

  @JsonProperty("SpecialMission")
  private String specialMission;

  @JsonProperty("RequestJsonId")
  private String requestJsonId;

  @JsonProperty("SpecialMessage")
  private SpecialMessage specialMessage;
}
