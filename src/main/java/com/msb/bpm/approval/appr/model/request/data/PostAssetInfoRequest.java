package com.msb.bpm.approval.appr.model.request.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.msb.bpm.approval.appr.model.request.PostBaseRequest;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@Builder
public class PostAssetInfoRequest extends PostBaseRequest {
    private Object collateral;
    private Object assetData;
}
