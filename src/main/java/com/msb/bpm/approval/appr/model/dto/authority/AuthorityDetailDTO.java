package com.msb.bpm.approval.appr.model.dto.authority;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorityDetailDTO {
    private String name;

    private String code;

    private Integer priority;

    private String title;

    private Integer level;

    private String type;

    private String rank;

    private String groupCode;

    private Integer version;
}