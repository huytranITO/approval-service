package com.msb.bpm.approval.appr.enums.checklist;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessFlow {
    BO_USL_FST("Luồng quy trình BO SBO luồng nhanh"),
    BO_USL_NRM("Luồng quy trình BO SBO luồng thường"),
    BO_S("Luồng quy trình có TSBĐ"),
    SB("Luồng quy trình SB tại HO"),
    HL("Luồng quy trình HL tại HO");

    private final String value;
}