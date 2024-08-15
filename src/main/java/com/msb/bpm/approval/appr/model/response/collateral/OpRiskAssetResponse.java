package com.msb.bpm.approval.appr.model.response.collateral;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OpRiskAssetResponse {

  private String code;

  private MessageAsset message;

  private List<OpRiskAssetData> data;
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MessageAsset {
    private String vi;
    private String en;
  }
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OpRiskAssetData {
    private Long id;
    private String applicationId;
    private String queryType;
    private String resultCode;
    private String resultDescription;
    private String reasonInList;
    private String subject;
    private String identifierCode;
    private String startDate;
    private String endDate;
    private Integer orderDisplay;
    private String assetGroup;
    private String assetType;
    private String createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    private String updatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
  }
}
