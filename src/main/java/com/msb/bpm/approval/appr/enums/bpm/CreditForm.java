package com.msb.bpm.approval.appr.enums.bpm;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CreditForm {
    SINGLE("V001","Cho vay từng lần"),
    ROUND ("V002","Hạn mức quay vòng"),
    SHORT ("V003","Hạn mức - Ngắn hạn"),
    MID ("V004","Hạn mức - Trung hạn"),
    LONG ("V005","Hạn mức - Dài hạn");

    private final String code;
    private final String value;

    CreditForm(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValue(String code){
        CreditForm creditForm = Arrays.stream(CreditForm.values())
                .filter(e -> e.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", code)));
        return creditForm.value;
    }
}
