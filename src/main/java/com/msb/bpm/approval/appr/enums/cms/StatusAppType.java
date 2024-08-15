package com.msb.bpm.approval.appr.enums.cms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusAppType {
    NOT_READY("NOT READY"),
    READY("READY"),
    ;

    private final String value;

}