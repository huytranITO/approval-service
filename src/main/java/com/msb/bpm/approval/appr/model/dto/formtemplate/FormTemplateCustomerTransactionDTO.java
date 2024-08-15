package com.msb.bpm.approval.appr.model.dto.formtemplate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormTemplateCustomerTransactionDTO {
    private String propertyTransactionTime = "null";
    private String propertyAsset = "null";
    private String propertyTransactionValue = "null";
    private String propertyPurchaseCost = "null";
    private String propertyBrokerageCost = "null";
    private String propertyTransferNameCost = "null";
    private String propertyProfit = "null";
}
