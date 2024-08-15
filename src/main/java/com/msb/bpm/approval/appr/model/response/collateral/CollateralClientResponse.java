package com.msb.bpm.approval.appr.model.response.collateral;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CollateralClientResponse {
    private String code;
    private Object data;
}
