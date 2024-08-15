package com.msb.bpm.approval.appr.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MaxPercentConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxAllocationPercentConstraint {
    String field();
    String[] fieldDependOns() default "";
    String message() default "{asset.validation.constraints.max.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        MaxAllocationPercentConstraint[] value();
    }
}
