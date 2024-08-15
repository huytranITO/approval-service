package com.msb.bpm.approval.appr.exception;

import com.msb.bpm.approval.appr.model.response.error.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 28/11/2023, Tuesday
 **/
@AllArgsConstructor
@Getter
@Setter
public class ForwardServiceErrorException extends RuntimeException {

  private ErrorResponse errorResponse;
}
