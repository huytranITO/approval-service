package com.msb.bpm.approval.appr.validator;

import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 22/6/2023, Thursday
 **/
public class CustomValidationNumberCompareValidator implements
    ConstraintValidator<CustomValidationNumberCompare, Object> {

  private String fieldCompare1;
  private String fieldCompare2;
  private String operator;

  @Override
  public void initialize(CustomValidationNumberCompare constraintAnnotation) {
    this.fieldCompare1 = constraintAnnotation.fieldCompare1();
    this.fieldCompare2 = constraintAnnotation.fieldCompare2();
    this.operator = constraintAnnotation.operator();
  }

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

    Object o1 = new BeanWrapperImpl(o).getPropertyValue(this.fieldCompare1);
    Object o2 = new BeanWrapperImpl(o).getPropertyValue(this.fieldCompare2);

    if (Objects.isNull(o1)) {
      return true;
    }

    if (Objects.isNull(o2)) {
      return true;
    }

    constraintValidatorContext.disableDefaultConstraintViolation();

    constraintValidatorContext.buildConstraintViolationWithTemplate(
            constraintValidatorContext.getDefaultConstraintMessageTemplate())
        .addPropertyNode(this.fieldCompare1)
        .addConstraintViolation();

    if (o1 instanceof BigDecimal && o2 instanceof BigDecimal) {
      return compareBigDecimal(o1, o2, operator);
    } else if (o1 instanceof Integer && o2 instanceof Integer) {
      return compareInteger(o1, o2, operator);
    } else {
      return false;
    }

  }

  private boolean compareBigDecimal(Object o1, Object o2, String operator) {
    BigDecimal c1 = (BigDecimal) o1;
    BigDecimal c2 = (BigDecimal) o2;
    switch (operator) {
      case "=":
        return c1.compareTo(c2) == 0;
      case ">":
        return c1.compareTo(c2) > 0;
      case "<":
        return c1.compareTo(c2) < 0;
      case ">=":
        return c1.compareTo(c2) > -1;
      case "<=":
        return c1.compareTo(c2) < 1;
      default:
        return false;
    }
  }

  private boolean compareInteger(Object o1, Object o2, String operator) {
    Integer c1 = (Integer) o1;
    Integer c2 = (Integer) o2;
    switch (operator) {
      case "=":
        return c1.compareTo(c2) == 0;
      case ">":
        return c1.compareTo(c2) > 0;
      case "<":
        return c1.compareTo(c2) < 0;
      case ">=":
        return c1.compareTo(c2) > -1;
      case "<=":
        return c1.compareTo(c2) < 1;
      default:
        return false;
    }
  }

}
