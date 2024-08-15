package com.msb.bpm.approval.appr.model.request.checklist;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAdditionalData {

  @NotNull
  private Long id;
  
  private String returnCode;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date returnDate;

  private boolean isError;
}
