package com.msb.bpm.approval.appr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreditConditionDTO {
    private String code;
    private String customerSegment;
    private String groupCode;
    private String group;
    private String detail; // 'Điều kiện chi tiết',
    private String creditType; // 'Khoản cấp tín dụng',
    private String object; // 'Đối tượng áp dụng'
    private String controlTime; // 'Thời điểm kiểm soát',
    private String controlUnit; // 'Đơn vị kiểm soát',
}