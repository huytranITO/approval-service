package com.msb.bpm.approval.appr.model.request.data;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PostCheckListTabRequest extends PostBaseRequest {

  private boolean rmCommitStatus;
}
