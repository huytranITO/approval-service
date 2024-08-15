package com.msb.bpm.approval.appr.model.request.feeback;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.model.dto.comment.ApplicationCommentDataDTO;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostApplicationFeedbackRequest {

  private String bpmId;

  @Valid
  private ApplicationCommentDataDTO applicationCommentData;
}
