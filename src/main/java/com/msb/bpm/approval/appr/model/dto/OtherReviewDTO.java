package com.msb.bpm.approval.appr.model.dto;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtherReviewDTO {

  private String approvalPosition;

  @Size(max = 1000)
  private String contentDetail;

  private Integer orderDisplay;
}
