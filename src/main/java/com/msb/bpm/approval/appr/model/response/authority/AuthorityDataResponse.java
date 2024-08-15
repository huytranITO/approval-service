package com.msb.bpm.approval.appr.model.response.authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDataResponse {
    private List<AuthorityInfoResponse> data;
}