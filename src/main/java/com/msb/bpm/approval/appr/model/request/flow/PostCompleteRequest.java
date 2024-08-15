package com.msb.bpm.approval.appr.model.request.flow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 8/6/2023, Thursday
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PostCompleteRequest extends PostBaseRequest {

  @NotBlank
  @Size(max = 10)
  private String otp;

  @NotBlank
  @Size(max = 100)
  private String otpId;

  @NotBlank
  @Size(max = 100)
  private String transactionId;
}
