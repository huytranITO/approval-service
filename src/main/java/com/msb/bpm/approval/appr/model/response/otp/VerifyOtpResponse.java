package com.msb.bpm.approval.appr.model.response.otp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.constant.Constant;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpResponse {

  @JsonProperty("timestamp")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DEFAULT_DATE_TIME_FORMAT, timezone = "Asia/Bangkok")
  private Timestamp createdAt;

  @JsonProperty("success")
  private boolean success;

  @JsonProperty("data")
  private DataMsg data;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DataMsg {
    private String message;
  }

}