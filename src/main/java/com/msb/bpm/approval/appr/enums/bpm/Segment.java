package com.msb.bpm.approval.appr.enums.bpm;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Segment {

    HP("V001","HP"),
    YP ("V002","YP"),
    SBO ("V003","SBO"),
    BO ("V004","BO"),
    MFIRST ("V005","Mfirst"),
    OTHER ("V006","Khác");

    private final String code;
    private final String value;

    Segment(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValue(String code){
        Segment creditForm = Arrays.stream(Segment.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);

        return creditForm == null ? "" : creditForm.value;
    }
}
