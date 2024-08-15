package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetCommonInfoDTO {
    private boolean completed = Boolean.FALSE;
    private String type;
    private String bpmId;
    private Object collateral;
    private Object assetData;
}
