package com.msb.bpm.approval.appr.model.response.collateral;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class AssetRuleChecklistResponse {
    private String objectId;
    private Long assetId;
    private String assetGroup;
    private String assetType;
    private Boolean isLoanPurpose;
    private String investorInformation;
    private Integer assetVersion;
    private List<FileDto> fileInfo;
}
