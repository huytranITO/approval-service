package com.msb.bpm.approval.appr.validator;

import com.msb.bpm.approval.appr.enums.application.CustomerType;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 24/8/2023, Thursday
 **/
public class CustomerTypeConstraintValidator implements ConstraintValidator<CustomerTypeConstraint, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    if (StringUtils.isNotBlank(value)) {
      return Arrays.stream(CustomerType.values()).map(CustomerType::name)
          .collect(Collectors.toList()).contains(value);
    }
    return true;
  }
}
