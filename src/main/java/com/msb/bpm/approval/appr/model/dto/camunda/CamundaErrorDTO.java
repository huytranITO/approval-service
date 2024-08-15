package com.msb.bpm.approval.appr.model.dto.camunda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 14/6/2023, Wednesday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamundaErrorDTO {

  private String type;
  private String message;
}
