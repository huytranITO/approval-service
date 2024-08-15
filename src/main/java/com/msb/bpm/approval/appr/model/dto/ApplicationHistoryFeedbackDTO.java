package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationHistoryFeedbackDTO {

  private Long id;

  private String userRole;

  @NotBlank
  private String username;

  private LocalDateTime feedbackAt;

  @Size(max = 5000)
  private String feedbackContent;

  private String createdBy;
  private LocalDateTime createdAt;
  private String updatedBy;
  private LocalDateTime updatedAt;
  private String createdPhoneNumber;
  private boolean editable;
}
