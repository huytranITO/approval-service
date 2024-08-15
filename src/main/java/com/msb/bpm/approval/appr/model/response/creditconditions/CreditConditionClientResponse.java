package com.msb.bpm.approval.appr.model.response.creditconditions;

import java.util.Date;

import lombok.Data;

@Data
public class CreditConditionClientResponse<T> {
	private Date transactionTime; 
	private String responseCode;
    private String responseMessage;
    private T value;
}
