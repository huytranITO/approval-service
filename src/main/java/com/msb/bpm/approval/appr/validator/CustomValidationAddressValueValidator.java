package com.msb.bpm.approval.appr.validator;

import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.service.cache.MercuryConfigurationServiceCache;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 24/8/2023, Thursday
 **/
@RequiredArgsConstructor
@Slf4j
public class CustomValidationAddressValueValidator implements ConstraintValidator<CustomValidationAddressValue, Object> {

  private String parentCode;
  private String code;

  private final MercuryConfigurationServiceCache mercuryConfigurationServiceCache;

  @Override
  public void initialize(CustomValidationAddressValue constraintAnnotation) {
    parentCode = constraintAnnotation.parentCode();
    code = constraintAnnotation.code();
  }

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
    if (obj == null) {
      return true;
    }

    String parentCodeValue = StringUtils.isNotBlank(parentCode)
        ? (String) new BeanWrapperImpl(obj).getPropertyValue(parentCode)
        : "";
    String codeValue = (String) new BeanWrapperImpl(obj).getPropertyValue(code);

    if (StringUtils.isBlank(codeValue)) {
      return true;
    }

    constraintValidatorContext.disableDefaultConstraintViolation();
    constraintValidatorContext.buildConstraintViolationWithTemplate(
            constraintValidatorContext.getDefaultConstraintMessageTemplate())
        .addPropertyNode(code)
        .addConstraintViolation();

    MercuryDataResponse response = mercuryConfigurationServiceCache.searchPlace(parentCodeValue);
    if (response == null) {
      return true;
    }
    return -1 == response.getCode() || response.getValue().stream()
        .anyMatch(data -> data.getId().equals(codeValue));
  }
}
