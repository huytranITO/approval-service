package com.msb.bpm.approval.appr.model.response.legacy.impl.css;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RawResponse {

  private String type;

  private String role;

  private String user;

  private String timeStamp;

  private String rank;

  private String score;

  private String recommendation;

}
