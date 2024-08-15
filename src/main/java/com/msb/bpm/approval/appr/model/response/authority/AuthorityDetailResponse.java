package com.msb.bpm.approval.appr.model.response.authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDetailResponse {
    private Integer version;
    private AuthorityInfoResponse info;
}