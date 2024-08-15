package com.msb.bpm.approval.appr.model.request.oprisk;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpRiskRequest {
  @NotEmpty(message = "applicationId not empty")
  @NotNull(message = "applicationId not null")
  private String applicationId;

  @NotNull
  @NotEmpty
  @Valid
  private List<@Valid Item> assetData;

  @Getter
  @Setter
  public static class Item{
    @NotNull(message = "certificate not null")
    @NotEmpty(message = "certificate not empty")
    private String certificate;

    @NotNull(message = "assetType not null")
    @NotEmpty(message = "assetType not empty")
    private String assetType;

    @NotNull(message = "assetGroup not null")
    @NotEmpty(message = "assetGroup not empty")
    private String assetGroup;

    public String getKey() {
      return certificate.concat(assetType).concat(assetGroup);
    }
  }
}
