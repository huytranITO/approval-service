package com.msb.bpm.approval.appr.model.request.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class AssetInfoRequest {
    @NotNull
    @Valid
    private String applicationId;

    @NotNull
    @Valid
    private String businessType;

    private String actionType;

    private List<Object> customerLoans;

    @NotNull
    @Valid
    private List<Object> assetData;
}
