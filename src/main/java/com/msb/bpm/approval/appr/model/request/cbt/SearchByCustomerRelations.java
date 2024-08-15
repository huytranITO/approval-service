package com.msb.bpm.approval.appr.model.request.cbt;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchByCustomerRelations {
  @NotNull
  private String bpmId;

  @Valid
  @NotNull
  private CustomerInfo customerInfo;

  @Valid
  private List<CustomerInfo> relations;

  @Valid
  private List<AssetDTOInfo> assetInfo;

  @Valid
  private List<EnterpriseInfo> enterpriseInfo;

  @Getter
  @Setter
  public static class CustomerInfo {
    @NotNull
    private Long id;
    @NotNull
    private Integer version;
  }

  @Getter
  @Setter
  public static class AssetDTOInfo {
    @NotNull
    private Long id;
    @NotNull
    private Integer version;
  }

  @Getter
  @Setter
  public static class EnterpriseInfo {
    @NotNull
    private Long id;
    @NotNull
    private Integer version;
  }

}
