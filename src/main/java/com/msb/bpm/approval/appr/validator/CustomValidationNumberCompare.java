package com.msb.bpm.approval.appr.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 22/6/2023, Thursday
 **/
@Constraint(validatedBy = CustomValidationNumberCompareValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValidationNumberCompare {
  String message() default "{javax.validation.constraints.NotBlank.message}";

  String fieldCompare1();

  String fieldCompare2();

  String operator() default "=";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
    CustomValidationNumberCompare[] value();
  }
}
