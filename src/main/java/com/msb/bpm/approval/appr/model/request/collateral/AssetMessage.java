package com.msb.bpm.approval.appr.model.request.collateral;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Builder
public class AssetMessage {
    private String vi;
    private String en;
}
