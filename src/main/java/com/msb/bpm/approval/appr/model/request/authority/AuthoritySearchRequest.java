package com.msb.bpm.approval.appr.model.request.authority;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthoritySearchRequest {

    @Schema(description = "Loại khách hàng")
    private String type;

    @Schema(description = "mã thẩm quyền")
    private String code;

    @Schema(description = "tên thẩm quyền")
    private String name;

    private String customerType;

    @JsonProperty("status")
    private String active;
}