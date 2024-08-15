package com.msb.bpm.approval.appr.model.request.data;

import com.msb.bpm.approval.appr.model.dto.ApplicationHistoryFeedbackDTO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateFeedbackRequest {

  @NotNull
  @Valid
  private ApplicationHistoryFeedbackDTO historyFeedback;
}
