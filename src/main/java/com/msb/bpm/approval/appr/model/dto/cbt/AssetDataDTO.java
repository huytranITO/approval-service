package com.msb.bpm.approval.appr.model.dto.cbt;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Data
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetDataDTO {
  private Long id;
  private int version;
}
