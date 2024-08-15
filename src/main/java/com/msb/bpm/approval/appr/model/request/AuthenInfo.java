package com.msb.bpm.approval.appr.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
public class AuthenInfo {

  @JsonProperty("req_id")
  private String reqId;
  @JsonProperty("req_app")
  private String reqApp;
  private String srv;
  @JsonProperty("req_time")
  private Date reqTime;
  private String authorizer;
  private String password;
}
