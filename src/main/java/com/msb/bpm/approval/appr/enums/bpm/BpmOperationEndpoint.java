package com.msb.bpm.approval.appr.enums.bpm;

import lombok.Getter;

@Getter
public enum BpmOperationEndpoint {
    SYNC_APPLICATION("sync-application");

    private final String value;

    BpmOperationEndpoint(String value) {
        this.value = value;
    }
}