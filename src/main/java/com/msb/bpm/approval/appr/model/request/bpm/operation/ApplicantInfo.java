package com.msb.bpm.approval.appr.model.request.bpm.operation;


import com.msb.bpm.approval.appr.enums.bpm.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplicantInfo {
    private Long approvalApplicantId;
    private Integer version;
    private String address;
    private String cif;
    private String city;
    private String district;
    private String ward;
    private String dob;
    private String email;
    private String employeeId;
    private String fullName;
    private Gender gender;
    private String icNumber;
    private String icType;
    private String issueAt;
    private String issuePlace;
    private String issueBy;
    private String lastName;
    private String maritalStatus;
    private String nationality;
    private Boolean owner;
    private String phoneNumber;
    private String relationType;
    private String segment;
    private String shortName;
    private List<IncomeInfo> incomes;
    private Integer index;
    private Integer orderDisplay;
}
