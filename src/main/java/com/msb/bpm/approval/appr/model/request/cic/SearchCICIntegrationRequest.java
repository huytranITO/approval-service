package com.msb.bpm.approval.appr.model.request.cic;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchCICIntegrationRequest {
    private String userNameDirectRq;
    private String requestUser;
    private String branchCode;
    private String dealingRoomCode;
    private List<CICCustomerInfo> customerInfo;
}
