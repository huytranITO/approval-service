package com.msb.bpm.approval.appr.model.request.flow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PostSubmitRequest extends PostBaseRequest {

//  @NotBlank
  private String proposalApprovalReception;   // Cấp TQ tiếp nhận yêu cầu

//  @NotBlank
  private String receptionUser;               // CB tiếp nhận yêu cầu
}
