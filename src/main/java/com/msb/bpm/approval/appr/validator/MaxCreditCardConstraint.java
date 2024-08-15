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
 * @created : 5/10/2023, Thursday
 **/
@Constraint(validatedBy = MaxCreditCardValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxCreditCardConstraint {

  String systemObj() default "CMS";

  String message() default "{custom.validation.constraints.NotExceed2Card.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
