package com.msb.bpm.approval.appr.enums.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum HDLDDocumentCode {
    QD_RB_012("QD_RB_012");

    private final String code;

    public static List<String> hdldDocumentCodes() {
        return Stream.of(HDLDDocumentCode.values())
                .map(HDLDDocumentCode::getCode)
                .collect(Collectors.toList());
    }
}
