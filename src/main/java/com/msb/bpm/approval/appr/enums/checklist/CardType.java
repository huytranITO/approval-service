package com.msb.bpm.approval.appr.enums.checklist;

import lombok.Getter;

@Getter
public enum CardType {
    V001("Master Blue"),
    V002("Master White"),
    V003("Master Platinum"),
    V004("Master Platinum FCB"),
    V005("Master Blue cho Staff MSB"),
    V006("Master White cho Staff MSB"),
    V007("Master Platinum cho Staff MSB"),
    V008("Master Lotte"),
    V009("Master Lotte cho Staff của Lotte"),
    V010("Master Vpoint"),
    V011("Master Vpoint cho Staff của VNPT"),
    V012("Visa Travel"),
    V013("Visa Travel cho Staff MSB"),
    V014("Visa Online"),
    V015("Visa Online cho Staff MSB"),
    V016("Mới chưa dùng"),
    V017("Visa Dinning Signature"),
    V018("Visa Dinning Signature cho Staff MSB"),
    V019("Visa Dinning Signature cho Mfirst"),
    V020("MSB Mastercard Daily Shopping"),
    V021("MSB Mastercard Daily Shopping cho Staff MSB"),
    V022("MSB MasterCard mDigi Main Card"),
    V023("MSB MasterCard mDigi Sub Card"),
    V024("MSB MasterCard mDigi Staff Main Card"),
    V025("MSB MasterCard mDigi Staff Sub Card"),
    ;
    private final String value;
    CardType(String value) {
        this.value = value;
    }
}