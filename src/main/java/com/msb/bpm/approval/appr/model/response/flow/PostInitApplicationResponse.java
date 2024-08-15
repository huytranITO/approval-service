package com.msb.bpm.approval.appr.model.response.flow;

import com.msb.bpm.approval.appr.model.dto.ApplicationDTO;
import com.msb.bpm.approval.appr.model.request.flow.PostCreateApplicationRequest;
import lombok.Getter;
import lombok.Setter;


public class PostInitApplicationResponse extends PostCreateApplicationRequest {

  @Setter
  @Getter
  private ApplicationDTO application;
}
