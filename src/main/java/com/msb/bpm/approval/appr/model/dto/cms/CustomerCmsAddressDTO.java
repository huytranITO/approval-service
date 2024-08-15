package com.msb.bpm.approval.appr.model.dto.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class CustomerCmsAddressDTO extends CmsAddressDTO {

  @NotBlank
  @Size(max = 4)
  private String addressType;

  private String addressLinkId = UUID.randomUUID().toString();
}