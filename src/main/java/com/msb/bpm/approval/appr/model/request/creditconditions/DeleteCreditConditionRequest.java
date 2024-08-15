package com.msb.bpm.approval.appr.model.request.creditconditions;

import java.util.List;

import lombok.Data;

@Data
public class DeleteCreditConditionRequest {
	List<Long> ids;
}
