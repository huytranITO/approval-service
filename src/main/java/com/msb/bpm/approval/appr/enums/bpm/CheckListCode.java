package com.msb.bpm.approval.appr.enums.bpm;

import lombok.Getter;

@Getter
public enum CheckListCode {

    CODE0501("0501"), CODE0502("0502"), CODE0504("0504");

    private final String code;

    CheckListCode(String code) {
        this.code = code;
    }

    public static Boolean isTypeAccepted(String code){
        return CODE0501.getCode().equals(code)
                || CODE0502.getCode().equals(code)
                || CODE0504.getCode().equals(code);
    }
}
