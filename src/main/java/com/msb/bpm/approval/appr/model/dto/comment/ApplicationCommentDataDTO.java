package com.msb.bpm.approval.appr.model.dto.comment;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
public class ApplicationCommentDataDTO {

  @Size(max = 8000)
  private String customer;
  @Size(max = 8000)
  private String applicationCredit;
  @Size(max = 8000)
  private String applicationIncome;
  @Size(max = 8000)
  private String assetInfo;

  @Valid
  @NotNull
  private DocumentCommentDataDTO documentCommentData;

}
