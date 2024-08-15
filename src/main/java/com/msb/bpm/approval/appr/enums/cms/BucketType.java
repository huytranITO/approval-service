package com.msb.bpm.approval.appr.enums.cms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BucketType {
    CMS("cms"),
    ;
    private final String value;
}