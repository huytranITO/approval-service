package com.msb.bpm.approval.appr.model.request.cic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchCICRequest {
    private String cicCode;

    private String customerUniqueId;

    private String customerType;

    private String productCode;

    private Integer validityPeriod;

    private String address;

    private String customerName;

    private String userNameDirectRq;

    private String branchCode;

    private String dealingRoomCode;
}
