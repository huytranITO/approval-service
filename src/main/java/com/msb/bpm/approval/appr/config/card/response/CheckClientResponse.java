package com.msb.bpm.approval.appr.config.card.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.msb.bpm.approval.appr.constant.Constant;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CheckClientResponse {

  private Date timestamp;
  private Integer code;
  private String message;
  private Data data;

  @Getter
  @Setter
  @ToString
  public static class Data {
    private String fullName;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String regNumber;
    private String address;
    private String gender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DEFAULT_DATE_TIME_FORMAT, timezone = "Asia/Bangkok")
    private LocalDateTime dateOBirth;
  }
}
