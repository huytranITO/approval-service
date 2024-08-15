package com.msb.bpm.approval.appr.model.response.email;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/5/2023, Friday
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailResponse {

  private String code;

  private String message;

  @JsonProperty("data")
  private DataMsg data;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DataMsg {

    private Long id;

    private String eventCode;

    private String status;

    private Date createdAt;
  }

}