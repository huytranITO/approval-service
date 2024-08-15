package com.msb.bpm.approval.appr.model.response.cas;

import lombok.*;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class CASResponse {
    private String code;
    private String message;
    private Object data;
}
