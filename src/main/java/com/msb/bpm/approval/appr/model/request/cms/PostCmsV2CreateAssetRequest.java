package com.msb.bpm.approval.appr.model.request.cms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 7/9/2023, Thursday
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostCmsV2CreateAssetRequest implements Serializable {

  @NotNull
  private String applicationId;
  private List<AssetInfo> assetInfo;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class AssetInfo implements Serializable {
    private String idAsset;
    private String collateralGroup;
    private String collateralType;
    private String certificate;
    private String cityCode;
    private String districtCode;
    private String wardCode;
    private String addressLine;
    private String landParcel;
    private String descriptionAsset;
    private String houseNumber;
    private String floor;
    private String landPlot;
    private String mapLocation;
    private List<AssetOwner> assetOwner;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class AssetOwner implements Serializable {
    private String relationshipCustomer;
    private String assetOwnerName;
  }
}
