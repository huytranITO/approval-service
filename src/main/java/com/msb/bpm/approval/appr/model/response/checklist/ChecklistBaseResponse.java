package com.msb.bpm.approval.appr.model.response.checklist;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 1/6/2023, Thursday
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistBaseResponse<T> {
  private Date timestamp;
  private StatusCode status;
  @JsonProperty("data")
  private T data;

  @Data
  public static class StatusCode {
    private String code;
    private String message;
  }
}
