package com.msb.bpm.approval.appr.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = CustomValidationFieldDependOnValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValidationFieldDependOn {

  String message() default "{javax.validation.constraints.NotBlank.message}";

  String field();

  String fieldNeedValidate() default "";

  String[] fieldNeedValidates() default {};

  String[] fieldDependOns() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {

    CustomValidationFieldDependOn[] value();
  }
}
