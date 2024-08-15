package com.msb.bpm.approval.appr.model.dto.cms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApplicationDTO {

  @NotBlank
  @Size(max = 36)
  private String refId;

  @NotBlank
  @Size(max = 20)
  private String source;

  @NotBlank
  @Size(max = 6)
  private String segment;

  @NotBlank
  @Size(max = 50)
  private String createdBy;
}
