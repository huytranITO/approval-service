package com.msb.bpm.approval.appr.model.request.cic;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CICCustomerInfo {
    private String customerType;
    private String productCode;
    private String cicCode;
    private String address;
    private String customerName;
    private String taxNo;
    private String cbusRegNumber;
    private String cusIdNumber;
}
