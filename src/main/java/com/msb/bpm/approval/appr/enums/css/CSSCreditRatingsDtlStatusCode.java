package com.msb.bpm.approval.appr.enums.css;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum CSSCreditRatingsDtlStatusCode {
    _700002("700002");

    private final String code;

    public static List<String> cssCreditRatingsDtlStatusCodes() {
        return Stream.of(CSSCreditRatingsDtlStatusCode.values())
                .map(CSSCreditRatingsDtlStatusCode::getCode)
                .collect(Collectors.toList());
    }
}
