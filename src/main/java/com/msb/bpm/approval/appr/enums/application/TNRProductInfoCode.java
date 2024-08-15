package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum TNRProductInfoCode {
    _20021("20021"),
    _20275("20275");

    private final String code;

    public static List<String> tnrProductInfoCodes() {
        return Stream.of(TNRProductInfoCode.values())
                .map(TNRProductInfoCode::getCode)
                .collect(Collectors.toList());
    }
}
