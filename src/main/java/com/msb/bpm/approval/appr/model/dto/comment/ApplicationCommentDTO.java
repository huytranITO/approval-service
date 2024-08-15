package com.msb.bpm.approval.appr.model.dto.comment;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@With
public class ApplicationCommentDTO {

  private Long applicationId;
  private String bpmId;
  private String refId;
  private ApplicationCommentDataDTO applicationCommentData;
}
