package com.msb.bpm.approval.appr.model.dto.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetDataDTO implements Serializable {

  private String  applicationId;

  private List<AssetInfoDTO> assetInfo = new ArrayList<>();

  private List<AssetOwnerDTO> assetOwner = new ArrayList<>();

}
