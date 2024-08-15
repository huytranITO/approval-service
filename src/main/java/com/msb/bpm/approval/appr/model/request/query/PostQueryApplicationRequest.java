package com.msb.bpm.approval.appr.model.request.query;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostQueryApplicationRequest {

  @NotNull
  private List<String> bpmCifs;

  private String username;
}
