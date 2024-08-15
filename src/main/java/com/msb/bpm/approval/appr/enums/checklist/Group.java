package com.msb.bpm.approval.appr.enums.checklist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum Group {
    LEGAL_PROFILE("01"),
    LOAN_PROFILE("02"),
    INCOME_PROFILE("03"),
    ASSET_PROFILE("04"),
    APPROVAL_PROFILE("05"),
    BANK_PROFILE("06"),
    OTHER_PROFILE("07"),
    CUSTOMER_LEGAL_PROFILE("08"),
    SPOUSE_LEGAL_PROFILE("09"),
    DEBT_GUARANTOR_LEGAL_PROFILE("10"),
    INCOME_FROM_SALARY("11"),
    INCOME_FROM_RENTAL_ASSET("12"),
    INCOME_FROM_DN_HGD_HKD("13"),
    INCOME_FROM_ENTERPRISE("14"),
    INCOME_OTHER("15"),
    SECURITY_ASSET_IS_REAL_ESTATE_PROFILE("16"),
    TRANSPORTATION_PROFILE("17"),
    MACHINE_EQUIPMENT_PROFILE("18"),
    INCOME_FROM_PROPERTY_BUSINESS("26")
    ;
    private final String value;

    public static final Set<String> CIC_GROUPS = new HashSet<>(Arrays.asList(
        CUSTOMER_LEGAL_PROFILE.getValue(),
        SPOUSE_LEGAL_PROFILE.getValue(),
        DEBT_GUARANTOR_LEGAL_PROFILE.getValue()));

    Group(String value) {
        this.value = value;
    }
    public static Group get(String value) {
        return Arrays.stream(Group.values()).filter(e -> StringUtils.equals(e.getValue(), value))
            .findFirst().orElse(null);
    }

    public static boolean isCicGroup(String groupCode) {
        return org.springframework.util.StringUtils.hasText(groupCode)
            && CIC_GROUPS.contains(groupCode);

    }
}