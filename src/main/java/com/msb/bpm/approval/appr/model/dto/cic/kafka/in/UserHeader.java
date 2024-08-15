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
public class UserHeader {

  @JsonProperty("User")
  private String user;

  @JsonProperty("TableName")
  private String tableName;

  @JsonProperty("SpecialMission")
  private String specialMission;

  @JsonProperty("KafkaTopicOut")
  private String kafkaTopicOut;

}
