package com.msb.bpm.approval.appr.model.request.creditconditions;

import java.util.List;

import lombok.Data;

@Data
public class UpdateFlagCreditConditionRequest {
	private String state;
	private List<Long> ids;
}
