package com.msb.bpm.approval.appr.model.dto.cms.v2;

import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.validator.CategoryConstraint;
import java.io.Serializable;
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
public class CmsApplicationDTO implements Serializable {

  @NotBlank
  @Size(max = 100)
  private String refId;

  @NotBlank
  @Size(max = 20)
  private String source;

  @Size(max = 6)
  @CategoryConstraint(category = ConfigurationCategory.SEGMENT)
  private String segment;

  @Size(max = 50)
  @NotBlank
  private String createdBy;

  @Size(max = 36)
  private String bpmId;

  @Size(max = 50)
  private String transactionId;

}
