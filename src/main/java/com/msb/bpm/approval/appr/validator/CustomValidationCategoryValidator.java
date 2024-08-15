package com.msb.bpm.approval.appr.validator;

import com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.service.cache.ConfigurationServiceCache;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class CustomValidationCategoryValidator implements ConstraintValidator<CategoryConstraint, String> {

  private final ConfigurationServiceCache serviceCache;
  private ConfigurationCategory category;

  @Override
  public void initialize(CategoryConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    category = constraintAnnotation.category();
  }

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    if (StringUtils.isBlank(s)) {
      return true;
    }
    List<CategoryDataResponse> response = serviceCache.getCategoryDataByCode(category);
    return CollectionUtils.isEmpty(response) ||
        response.stream().anyMatch(matching -> matching.getCode().equals(s));
  }
}
