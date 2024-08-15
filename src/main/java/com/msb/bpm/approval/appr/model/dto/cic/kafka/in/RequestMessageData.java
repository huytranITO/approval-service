package com.msb.bpm.approval.appr.model.dto.cic.kafka.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestMessageData {

  @JsonProperty("UserData")
  private UserData userData;

  @JsonProperty("ActionArray")
  private List<Action> actionArray;

}
