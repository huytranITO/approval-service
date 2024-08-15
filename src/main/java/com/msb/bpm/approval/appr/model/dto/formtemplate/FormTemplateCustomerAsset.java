package com.msb.bpm.approval.appr.model.dto.formtemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormTemplateCustomerAsset {
    private String assetCustomerName = "null";
    private String assetBusinessName = "null";
    private String assetRelationshipName = "null";
    private String assetCustomerRefCode = "null";
}
