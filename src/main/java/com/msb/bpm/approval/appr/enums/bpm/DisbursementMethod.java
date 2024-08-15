package com.msb.bpm.approval.appr.enums.bpm;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DisbursementMethod {
    TM("V001"),
    CK("V002"),
    GNPT("V003"),
    TQDMSB("V004");

    private final String code;

    DisbursementMethod(String code) {
        this.code = code;
    }

    public static String getDisbursementByCode(String code) {
        DisbursementMethod disbursementMethod = Arrays.stream(DisbursementMethod.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
        return disbursementMethod == null ? "" : disbursementMethod.name();
    }
}
