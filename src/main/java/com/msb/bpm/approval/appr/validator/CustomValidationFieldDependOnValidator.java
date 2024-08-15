package com.msb.bpm.approval.appr.validator;

import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.isRequiredEffectivePeriod;
import static com.msb.bpm.approval.appr.model.entity.AddressEntity_.ADDRESS_LINE;
import static com.msb.bpm.approval.appr.model.entity.ApplicationAppraisalContentEntity_.AUTHORIZATION;
import static com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity_.CARD_RECEIVE_ADDRESS;
import static com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity_.DEDUCT_ACCOUNT_NUMBER;
import static com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity_.SUB_CREDIT_CARDS;
import static com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity_.GUARANTEE_FORM;
import static com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity_.INTEREST_PAY_PERIOD;
import static com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity_.INTEREST_PAY_UNIT;
import static com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity_.PRINCIPAL_PAY_PERIOD;
import static com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity_.PRINCIPAL_PAY_UNIT;
import static com.msb.bpm.approval.appr.model.entity.ApplicationEntity_.EFFECTIVE_PERIOD;
import static com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity_.RELATIONSHIP;
import static com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity_.LOAN_PRODUCT_COLLATERAL;
import static com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity_.OTHER_LOAN_PRODUCT_COLLATERAL;
import static com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity_.OTHER_UNSECURE_PRODUCT;
import static com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity_.TOTAL;
import static com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity_.UNSECURE_PRODUCT;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_AT;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_BY;
import static com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity_.ISSUED_PLACE;
import static com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity_.EMPLOYEE_CODE;
import static com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity_.BUSINESS_REGISTRATION_NUMBER;
import static com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity_.INCOME_OWNER_NAME;

import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.repository.AmlOprRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CICRepository;
import com.msb.bpm.approval.appr.util.ValidationUtil;
import java.util.Collection;
import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

@RequiredArgsConstructor
public class CustomValidationFieldDependOnValidator implements
    ConstraintValidator<CustomValidationFieldDependOn, Object> {

  private String field;
  private String fieldNeedValidate;
  private String[] fieldNeedValidates;
  private String[] fieldDependOns;
  private final ApplicationRepository applicationRepository;
  private final CICRepository cicRepository;
  private final AmlOprRepository amlOprRepository;

  private final ApplicationConfig config;

  @Override
  public void initialize(CustomValidationFieldDependOn constraintAnnotation) {
    this.field = constraintAnnotation.field();
    this.fieldNeedValidate = constraintAnnotation.fieldNeedValidate();
    this.fieldNeedValidates = constraintAnnotation.fieldNeedValidates();
    this.fieldDependOns = constraintAnnotation.fieldDependOns();
  }

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
    Object fieldValue = new BeanWrapperImpl(o)
        .getPropertyValue(field);

    constraintValidatorContext.disableDefaultConstraintViolation();

    if (StringUtils.isBlank(fieldNeedValidate)
        && (fieldNeedValidates == null || fieldNeedValidates.length < 1)) {
      constraintValidatorContext.buildConstraintViolationWithTemplate(
              constraintValidatorContext.getDefaultConstraintMessageTemplate())
          .addPropertyNode(field)
          .addConstraintViolation();
    }

    switch (field) {
      case RELATIONSHIP: {
        return ValidationUtil.isValidRelationship(o, fieldValue, fieldDependOns);
      }
      case "staffId":
      case EMPLOYEE_CODE: {
        return ValidationUtil.isValidEmployeeCode(o, fieldValue, fieldDependOns);
      }
      case "reason": {
        return ValidationUtil.isValidConfirmStatus(o, fieldValue, fieldDependOns);
      }
      case AUTHORIZATION: {
        return ValidationUtil.isValidAuthorization(o, fieldValue, fieldDependOns);
      }
      case PRINCIPAL_PAY_PERIOD:
      case INTEREST_PAY_PERIOD: {
        return ValidationUtil.isValidPayPeriod(o, fieldValue, fieldDependOns);
      }
      case PRINCIPAL_PAY_UNIT:
      case INTEREST_PAY_UNIT: {
        return ValidationUtil.isValidPayUnit(o, fieldValue, fieldDependOns);
      }
      case SUB_CREDIT_CARDS: {
        return ValidationUtil.isValidSubCreditCards(constraintValidatorContext, fieldNeedValidate,
            field, o, fieldDependOns);
      }
      case "identities": {
        return ValidationUtil.isValidIdentities(o, fieldValue, fieldDependOns);
      }
      case ISSUED_AT: {
        return ValidationUtil.isValidIssuedAt(o, fieldValue, fieldDependOns);
      }
      case ISSUED_BY: {
        return ValidationUtil.isValidIssuedBy(o, fieldValue, fieldDependOns);
      }
      case ISSUED_PLACE: {
        return ValidationUtil.isValidIssuedPlace(o, fieldValue, fieldDependOns);
      }
      case CARD_RECEIVE_ADDRESS: {
        return ValidationUtil.isValidCardReceiveAddress(o, fieldValue, fieldDependOns);
      }
      case LOAN_PRODUCT_COLLATERAL:
      case OTHER_LOAN_PRODUCT_COLLATERAL:
      case UNSECURE_PRODUCT:
      case OTHER_UNSECURE_PRODUCT:
      case TOTAL: {
        return ValidationUtil.isValidLimitCreditValue(o, fieldValue, fieldDependOns);
      }
      case "address": {
        if (StringUtils.isNotBlank(fieldNeedValidate)) {
          return ValidationUtil.isValidAddress(constraintValidatorContext, fieldValue, field,
              fieldNeedValidate);
        }
        return ValidationUtil.isValidAddress(o, fieldValue, fieldDependOns);
      }
      case BUSINESS_REGISTRATION_NUMBER: {
        return ValidationUtil.isValidBusinessRegistrationNumber(o, fieldValue, fieldDependOns);
      }
      case ADDRESS_LINE: {
        return StringUtils.isNotBlank((String) fieldValue);
      }
      case DEDUCT_ACCOUNT_NUMBER: {
        return ValidationUtil.isValidDeductAccountNumber(o, fieldValue, fieldDependOns);
      }
      case "listFile": {
        return ValidationUtil.isValidChecklistListFile(o, fieldValue, fieldDependOns);
      }
      case "incomes": {
        return ValidationUtil.isValidIncomes(constraintValidatorContext, fieldValue, o,
            fieldDependOns, fieldNeedValidates, field);
      }
      case "loanAmount": {
        return ValidationUtil.isValidLoanAmount(o, fieldValue, fieldDependOns);
      }
      case EFFECTIVE_PERIOD: {
        return isValidEffectivePeriod(o, fieldValue, fieldDependOns);
      }
      case "customerAndRelationPerson": {
        return ValidationUtil.isValidCustomerAndRelationPerson(constraintValidatorContext,
                fieldValue, fieldNeedValidates, cicRepository, amlOprRepository);
      }
      case INCOME_OWNER_NAME: {
        return ValidationUtil.isValidIncomeOwnerName(o, fieldValue, fieldDependOns);
      }
      case GUARANTEE_FORM: {
        return ValidationUtil.isValidGuaranteeForm(o, fieldValue, fieldDependOns);
      }
      case "subCard": {
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                constraintValidatorContext.getDefaultConstraintMessageTemplate())
            .addPropertyNode(field)
            .addConstraintViolation();
        if (CollectionUtils.isEmpty((Collection<?>) fieldValue)) {
          return true;
        }
        return ((Collection<?>) fieldValue).size() <= config.getValidation().getCardMax();
      }
      case "email": {
        return ValidationUtil.isValidCustomerEmail(o, fieldValue, fieldDependOns);
      }
      case "branchReceive": {
        return ValidationUtil.isValidBranchReceive(o, fieldValue, fieldDependOns);
      }
      default:
        return true;
    }
  }

  private boolean isValidEffectivePeriod(Object o, Object fieldValue, String[] fieldDependOns) {
    String bpmId = (String) new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    ApplicationEntity applicationEntity = applicationRepository.findByBpmIdCustomQuery(bpmId)
        .orElse(null);

    if (applicationEntity == null
        || !isRequiredEffectivePeriod(applicationEntity.getProcessingRole())) {
      return true;
    }

    return Objects.nonNull(fieldValue);
  }
}
