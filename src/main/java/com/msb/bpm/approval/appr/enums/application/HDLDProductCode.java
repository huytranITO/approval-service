package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Getter
@RequiredArgsConstructor
public enum HDLDProductCode {
    RLNLTHDLD("RLNLTHDLD"),
    RLNMTHDLD("RLNMTHDLD"),
    RLNSTHDLD("RLNSTHDLD"),
    _7069("7069"),
    STAFF_S("STAFF-S");

    private final String code;

    public static List<String> hdldProductCodes() {
        return Stream.of(HDLDProductCode.values())
                .map(HDLDProductCode::getCode)
                .collect(Collectors.toList());
    }
}
