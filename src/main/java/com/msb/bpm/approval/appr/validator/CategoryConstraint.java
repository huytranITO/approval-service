package com.msb.bpm.approval.appr.validator;


import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = CustomValidationCategoryValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryConstraint {
  ConfigurationCategory category();
  String message() default "{custom.validation.constraints.category.message}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
