package com.msb.bpm.approval.appr.model.request.flow;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostReworkApplicationRequest extends PostReceptionUserRequest {

  private Set<String> reasons;
}
