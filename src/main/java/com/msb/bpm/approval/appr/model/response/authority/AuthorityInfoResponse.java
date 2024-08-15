package com.msb.bpm.approval.appr.model.response.authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityInfoResponse {
    private String name;

    private String code;

    private Integer priority;

    private String title;

    private String rank;

    private Integer level;

    private Float rate;

    private String type;

    private String segmentation;

    private String groupCode;
}