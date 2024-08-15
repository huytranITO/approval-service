package com.msb.bpm.approval.appr.model.response.usermanager;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTreeDetail {

    private String code;

    private String parent;

    private String ecmCode;

    private String fullName;

    private String name;

    private String shortName;

    private String phoneNumber;

    private String fax;

    private String address;

    private String specializedBank;

    private String type;

    private List<OrganizationTreeDetail> children = new ArrayList<>();

    private String businessAreaCode;

    private String businessAreaFullName;

    private String businessAreaName;

}
