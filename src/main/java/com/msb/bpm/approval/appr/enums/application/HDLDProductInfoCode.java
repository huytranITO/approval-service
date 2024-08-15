package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum HDLDProductInfoCode {
    _20039("20039"),
    _40194("40194");

    private final String code;

    public static List<String> hdldProductInfoCodes() {
        return Stream.of(HDLDProductInfoCode.values())
                .map(HDLDProductInfoCode::getCode)
                .collect(Collectors.toList());
    }
}
