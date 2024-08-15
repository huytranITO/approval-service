package com.msb.bpm.approval.appr.model.response.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class AssetDataResponse {

  private String assetVersion;

  private Long id;

  private String objectId;

  private String applicationId;

  private String assetCode;

  private String assetType;

  private String assetTypeName;

  private String assetGroup;

  private String assetGroupName;

  private String assetName;

  private String ownerType;

  private String addressLinkId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime nextValuationDay;

  private String status;

  private String mortgageStatus; // Tình trạng thế chấp

  private Boolean hasComponent; // Có tài sản thành phần?

  private String  state; // Trạng thái Hiện Trạng hoặc Rút rasss

  private String sourceType; // Nguon tai san

  private Boolean isEditAssetName; // Tên tài sản đã đuợc chỉnh sửa từ người dùng?

  private Integer assetLatestVersion; //

  private AssetAdditionalInfoResponse assetAdditionalInfo;

  private Long tempAssetId;
}
