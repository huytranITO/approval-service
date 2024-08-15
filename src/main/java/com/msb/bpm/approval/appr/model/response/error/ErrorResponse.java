package com.msb.bpm.approval.appr.model.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 28/11/2023, Tuesday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse<T> {

  private String code;
  private T message;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MessageLanguageResponse {
    private String vi;
    private String en;
  }
}
