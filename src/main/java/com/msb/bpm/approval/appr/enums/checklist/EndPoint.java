package com.msb.bpm.approval.appr.enums.checklist;

import lombok.Getter;

@Getter
public enum EndPoint {
    UPLOAD_FILE("upload-file"),
    UPLOAD_FILE_INTERNAL("upload-file-internal"),
    GET_MASTER("get-master"),
    GENERATE_CHECKLIST("generate"),
    RELOAD("reload"),
    ADDITIONAL_DATA("additional-data"),
    DELETE_CHECKLIST_GROUP("delete-checklist-group"),
    UPDATE_CHECKLIST_VERSION("update-checklist-version"),
    HISTORY_FILE("history-file"),
    DELETE_FILE("delete-file"),
    SAVE_FILE("save-file"),
    GET_GROUP("get-group"),

    GET_BY_REQUEST_CODE("get-by-request-code"),
    SEARCH("search");

    private final String value;
    EndPoint(String value) {
        this.value = value;
    }
}