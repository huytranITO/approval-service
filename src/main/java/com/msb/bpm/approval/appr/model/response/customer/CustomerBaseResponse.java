package com.msb.bpm.approval.appr.model.response.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 10/7/2023, Monday
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBaseResponse<T> {
  private String code;
  @JsonProperty("data")
  private T data;
}
