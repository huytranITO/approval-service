package com.msb.bpm.approval.appr.validator;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.CARD;

import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 5/10/2023, Thursday
 **/
public class MaxCreditCardValidator implements ConstraintValidator<MaxCreditCardConstraint, Object> {

  private String systemObj;

  @Override
  public void initialize(MaxCreditCardConstraint constraintAnnotation) {
    this.systemObj = constraintAnnotation.systemObj();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {

    context.disableDefaultConstraintViolation();

    if (this.systemObj.equals("CMS")) {
      context.buildConstraintViolationWithTemplate(
          context.getDefaultConstraintMessageTemplate()
      ).addPropertyNode("applicationCredits")
          .addConstraintViolation();

      List<CmsBaseCreditDTO> credits = (List<CmsBaseCreditDTO>) new BeanWrapperImpl(
          value).getPropertyValue("applicationCredits");

      if (CollectionUtils.isEmpty(credits)) {
        return true;
      }

      credits = credits.stream()
          .filter(obj -> CARD.equals(obj.getCreditType()))
          .collect(Collectors.toList());

      return credits.size() <= 1;
    }

    return true;
  }
}
