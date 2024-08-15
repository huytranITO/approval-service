package com.msb.bpm.approval.appr.model.request.customer;

import lombok.Data;

import java.util.List;

@Data
public class FindByListCustomerRequest {
    private List<CustomerSearchVersion> customers;
    private boolean searchCore = false;
}
