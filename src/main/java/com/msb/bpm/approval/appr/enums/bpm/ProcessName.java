package com.msb.bpm.approval.appr.enums.bpm;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProcessName {

    PROCESS_PRI("V001","KH ưu tiên"),
    PROCESS_FST ("V002","Nhanh"),
    PROCESS_NRM ("V003","Thông thường");

    private final String code;
    private final String value;

    ProcessName(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValue(String code){
        ProcessName creditForm = Arrays.stream(ProcessName.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);

        return creditForm == null ? "" : creditForm.value;
    }
}