package com.msb.bpm.approval.appr.model.request.checklist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.CollectionUtils;

@Data
public class UpdateAdditionalDataRequest {

  @NotNull
  private Long applicationId;
  
  @Valid
  private List<UpdateAdditionalData> listChecklist;

  @JsonIgnore
  public boolean isEmptyData() {
    return CollectionUtils.isEmpty(listChecklist);
  }

}
