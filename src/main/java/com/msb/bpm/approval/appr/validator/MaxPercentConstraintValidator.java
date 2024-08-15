package com.msb.bpm.approval.appr.validator;

import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.ApplicationAssetAllocationDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.CreditAllocationDTO;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.ValidationUtil;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;
import java.util.stream.Collectors;

public class MaxPercentConstraintValidator implements ConstraintValidator<MaxAllocationPercentConstraint,
        Object> {
    private String field;
    private String[] fieldDependOns;

    @Override
    public void initialize(MaxAllocationPercentConstraint constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldDependOns = constraintAnnotation.fieldDependOns();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        Set<ApplicationAssetAllocationDTO> assetAllocations = (Set<ApplicationAssetAllocationDTO>)
                new BeanWrapperImpl(o)
                .getPropertyValue(field);
        Set<ApplicationCreditDTO> credits = (Set<ApplicationCreditDTO>)
                new BeanWrapperImpl(o)
                .getPropertyValue(fieldDependOns[0]);

        if (CollectionUtils.isEmpty(credits))   return true;

        // Check phan bo ts
        if(!CollectionUtils.isEmpty(credits.stream().filter(cr -> Boolean.TRUE.equals(cr.getIsAllocation())
                && CollectionUtils.isEmpty(cr.getAssets())).collect(Collectors.toList()))){
            return false;
        }

        if(!CollectionUtils.isEmpty(SecurityContextUtil.getAuthorities())
                && (SecurityContextUtil.getAuthorities().contains(ProcessingRole.PD_RB_RM.name())
                || SecurityContextUtil.getAuthorities().contains(ProcessingRole.PD_RB_BM.name()))
        ) {
            return true;
        }

        // check asset phan bo cho khoan vay có nam trong asset cua credit
        return ValidationUtil.isValidCreditAllocation(assetAllocations, credits);
    }
}
