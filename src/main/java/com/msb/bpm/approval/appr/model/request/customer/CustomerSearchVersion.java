package com.msb.bpm.approval.appr.model.request.customer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerSearchVersion {
    private Long id;
    private Integer version;
}
