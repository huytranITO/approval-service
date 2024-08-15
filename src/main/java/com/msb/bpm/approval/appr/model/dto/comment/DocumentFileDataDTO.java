package com.msb.bpm.approval.appr.model.dto.comment;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DocumentFileDataDTO {

  @Size(max = 20)
  @NotBlank
  private String code;

  @Size(max = 255)
  @NotBlank
  private String name;

  @NotBlank
  @Size(max = 50)
  private String groupCode;
  private Boolean checkEnough;
  private List<@Size(max = 255) String> fileName;

}
