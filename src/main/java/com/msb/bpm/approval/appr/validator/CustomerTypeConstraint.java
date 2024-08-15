package com.msb.bpm.approval.appr.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 24/8/2023, Thursday
 **/
@Documented
@Constraint(validatedBy = CustomerTypeConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomerTypeConstraint {
  String message() default "{custom.validation.constraints.category.message}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
