package com.msb.bpm.approval.appr.model.request.collateral;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Builder
public class CreditAssetRequest {
    private Long credit;
    private List<Long> assets;
}
