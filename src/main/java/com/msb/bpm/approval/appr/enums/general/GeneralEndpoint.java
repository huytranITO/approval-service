package com.msb.bpm.approval.appr.enums.general;

import lombok.Getter;

@Getter
public enum GeneralEndpoint {

    GET_GENERAL_INFO("get-general-info"),
    ;
    private final String value;

    GeneralEndpoint(String value) {
        this.value = value;
    }
}
