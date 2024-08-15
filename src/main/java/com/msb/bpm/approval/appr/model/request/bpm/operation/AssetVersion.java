package com.msb.bpm.approval.appr.model.request.bpm.operation;

import lombok.*;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class AssetVersion {
    private Long assetId;
    private Integer version;
}