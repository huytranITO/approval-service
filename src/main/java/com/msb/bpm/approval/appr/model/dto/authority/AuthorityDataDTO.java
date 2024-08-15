package com.msb.bpm.approval.appr.model.dto.authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDataDTO {
    private List<AuthorityDetailDTO> authorities;
}