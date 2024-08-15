package com.msb.bpm.approval.appr.model.response.general;

import lombok.Data;

@Data
public class GeneralInfoResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String step;

    private Boolean isFinish;

    private Boolean isRollback;

    private Boolean isWaitingForAssignee;

    private String businessType;

    private String label;

    private String program;



}
