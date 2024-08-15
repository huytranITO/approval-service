package com.msb.bpm.approval.appr.util;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.ENTERPRISE_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INDIVIDUAL_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.RENTAL;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.SALARY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LIMIT_CARD_AMOUNT;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.EB;
import static com.msb.bpm.approval.appr.enums.application.AppraisalContent.SPECIAL_RISK;
import static com.msb.bpm.approval.appr.enums.application.AutoDeductRate.isDeduct;
import static com.msb.bpm.approval.appr.enums.application.CardForm.V001;
import static com.msb.bpm.approval.appr.enums.application.CardForm.V002;
import static com.msb.bpm.approval.appr.enums.application.CreditType.CARD;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.application.AddressType;
import com.msb.bpm.approval.appr.enums.application.CardReceiveAddress;
import com.msb.bpm.approval.appr.enums.application.IssuedBy;
import com.msb.bpm.approval.appr.enums.application.LoanLimit;
import com.msb.bpm.approval.appr.enums.common.LdpConfirmStatus;
import com.msb.bpm.approval.appr.model.dto.ActuallyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.AddressDTO;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO.AmlOprGeneral;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BusinessIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO.CicDetail;
import com.msb.bpm.approval.appr.model.dto.CustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.ExchangeIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.RentalIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.SalaryIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.SubCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.ApplicationAssetAllocationDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.CreditAllocationDTO;
import com.msb.bpm.approval.appr.repository.AmlOprRepository;
import com.msb.bpm.approval.appr.repository.CICRepository;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidatorContext;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 11/6/2023, Sunday
 **/
@UtilityClass
public class ValidationUtil {

  private static final String INCOME_ITEM_FIELD_PROPERTY_NODE = "%s[%s].incomeItems[%s].%s";
  private static final String JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK = "{javax.validation.constraints.NotBlank.message}";
  private static final String JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL = "{javax.validation.constraints.NotNull.message}";
  private static final String JAVAX_CONSTRAINT_VALIDATOR_NOT_EMPTY = "{javax.validation.constraints.NotEmpty.message}";

  private static final List<String> INDIVIDUAL_ENTERPRISE_INCOME_TYPE = Arrays.asList(
      INDIVIDUAL_BUSINESS, ENTERPRISE_BUSINESS);

  private void buildConstraintValidatorContext(ConstraintValidatorContext cvx, String propertyNode,
      String errorMessage) {
    cvx.buildConstraintViolationWithTemplate(errorMessage).addPropertyNode(propertyNode)
        .addConstraintViolation();
  }

  private boolean isValidStringFieldIncome(ApplicationIncomeDTO income, String value) {

    return !ACTUALLY.equalsIgnoreCase(income.getIncomeRecognitionMethod()) || (
        ACTUALLY.equalsIgnoreCase(income.getIncomeRecognitionMethod()) && StringUtils.isNotBlank(
            value));
  }

  private boolean isValidObjectFieldIncome(ApplicationIncomeDTO income, Object value) {

    return !ACTUALLY.equalsIgnoreCase(income.getIncomeRecognitionMethod()) || (
        ACTUALLY.equalsIgnoreCase(income.getIncomeRecognitionMethod()) && Objects.nonNull(value));
  }

  private boolean isValidRankType(BaseIncomeDTO incomeItem,
      Map<Long, Boolean> refCustomerMsbMember) {
    if (refCustomerMsbMember.isEmpty()) {
      return true;
    }

    if (!SALARY.equalsIgnoreCase(incomeItem.getIncomeType()) || Objects.isNull(
        incomeItem.getRefCustomerId())
        || (refCustomerMsbMember.containsKey(incomeItem.getRefCustomerId())) && Objects.equals(
        Boolean.FALSE, refCustomerMsbMember.get(incomeItem.getRefCustomerId()))) {
      return true;
    }

    return refCustomerMsbMember.containsKey(incomeItem.getRefCustomerId()) && Objects.equals(
        Boolean.TRUE, refCustomerMsbMember.get(incomeItem.getRefCustomerId()))
        && StringUtils.isNotBlank(((SalaryIncomeDTO) incomeItem).getRankType());
  }

  private boolean isValidKpiRating(BaseIncomeDTO incomeItem,
      Map<Long, Boolean> refCustomerMsbMember) {
    if (refCustomerMsbMember.isEmpty()) {
      return true;
    }

    if (!SALARY.equalsIgnoreCase(incomeItem.getIncomeType()) || Objects.isNull(
        incomeItem.getRefCustomerId())
        || (refCustomerMsbMember.containsKey(incomeItem.getRefCustomerId())) && Objects.equals(
        Boolean.FALSE, refCustomerMsbMember.get(incomeItem.getRefCustomerId()))) {
      return true;
    }

    return refCustomerMsbMember.containsKey(incomeItem.getRefCustomerId()) && Objects.equals(
        Boolean.TRUE, refCustomerMsbMember.get(incomeItem.getRefCustomerId()))
        && StringUtils.isNotBlank(((SalaryIncomeDTO) incomeItem).getKpiRating());
  }

  public boolean isValidAddress(ConstraintValidatorContext constraintValidatorContext,
      Object fieldValue, String fieldName, String fieldNeedValidate) {
    constraintValidatorContext.buildConstraintViolationWithTemplate(
            constraintValidatorContext.getDefaultConstraintMessageTemplate())
        .addPropertyNode(fieldName + "." + fieldNeedValidate).addConstraintViolation();

    AddressDTO addressDTO = (AddressDTO) fieldValue;

    if (addressDTO == null) {
      return true;
    }

    if (fieldNeedValidate.equals("addressLine")) {
      return StringUtils.isNotBlank(addressDTO.getAddressLine());
    }

    return true;
  }

  public boolean isValidAddress(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnCardForm = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);

    if (Objects.equals(V002.name(), fieldDependOnCardForm)) {
      return true;
    }

    Object fieldDependOnCardReceiveAddress = new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[1]);

    if (!CardReceiveAddress.isRequired((String) fieldDependOnCardReceiveAddress)) {
      return true;
    }

    if (Objects.nonNull(fieldValue)) {
      AddressDTO addressDTO = (AddressDTO) fieldValue;
      return StringUtils.isNotBlank(addressDTO.getCityCode()) && StringUtils.isNotBlank(
          addressDTO.getDistrictCode()) && StringUtils.isNotBlank(addressDTO.getWardCode())
          && StringUtils.isNotBlank(addressDTO.getAddressLine());
    }

    return false;
  }

  public boolean isValidLimitCreditValue(Object o, Object fieldValue, String[] fieldDependOns) {

    Object fieldDependOnLoanLimit = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);

    if (Objects.isNull(fieldDependOnLoanLimit)) {
      return true;
    }

    if (!Objects.equals(LoanLimit.RECOMMEND.name(), fieldDependOnLoanLimit)) {
      return true;
    }

    return Objects.nonNull(fieldValue);
  }

  public boolean isValidCardReceiveAddress(Object o, Object fieldValue, String[] fieldDependOns) {
    String fieldDependOnCardForm = (String) new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[0]);

    if (StringUtils.isBlank(fieldDependOnCardForm)) {
      return true;
    }

    if (!V001.name().equalsIgnoreCase(fieldDependOnCardForm)) {
      return true;
    }

    return StringUtils.isNotBlank((String) fieldValue);
  }

  public boolean isValidIssuedPlace(Object o, Object fieldValue, String[] fieldDependOns) {
    Boolean fieldDependOnPriority = (Boolean) new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[0]);
    String fieldDependOnIssuedBy = (String) new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[1]);

    if (Objects.equals(Boolean.FALSE, fieldDependOnPriority)) {
      return true;
    }

    if (!IssuedBy.V002.name().equalsIgnoreCase(fieldDependOnIssuedBy)) {
      return true;
    }

    return StringUtils.isNotBlank((String) fieldValue);
  }

  public boolean isValidIssuedBy(Object o, Object fieldValue, String[] fieldDependOns) {
    Boolean fieldDependOnPriority = (Boolean) new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[0]);

    if (Objects.equals(Boolean.FALSE, fieldDependOnPriority)) {
      return true;
    }

    return StringUtils.isNotBlank((String) fieldValue);
  }

  public boolean isValidIssuedAt(Object o, Object fieldValue, String[] fieldDependOns) {
    Boolean fieldDependOnPriority = (Boolean) new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[0]);

    if (Objects.equals(Boolean.FALSE, fieldDependOnPriority)) {
      return true;
    }

    return Objects.nonNull(fieldValue);
  }

  public boolean isValidRelationship(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnMainCustomer = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    Object fieldDependOnCustomerType = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[1]);

    if (Objects.equals(EB, fieldDependOnCustomerType)) {
      return StringUtils.isNotBlank((String) fieldValue);
    }
    if (Objects.equals(Boolean.TRUE, fieldDependOnMainCustomer)) {
      return true;
    }
    return StringUtils.isNotBlank((String) fieldValue);
  }

  public boolean isValidEmployeeCode(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnMsbMember = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (Objects.equals(Boolean.FALSE, fieldDependOnMsbMember)) {
      return true;
    }
    return StringUtils.isNotBlank((String) fieldValue);
  }

  public boolean isValidAuthorization(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnContentType = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);

    if (Objects.equals(SPECIAL_RISK.name(), fieldDependOnContentType)) {
      return true;
    }

    Set<String> fieldDependOnDetail = (Set<String>) new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[1]);

    if (CollectionUtils.isEmpty(fieldDependOnDetail)) {
      return true;
    }

    return StringUtils.isNotBlank((String) fieldValue);
  }

  public boolean isValidConfirmStatus(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnContentType = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    List<String> list = Arrays.asList(LdpConfirmStatus.EXPIRED.name(), LdpConfirmStatus.REJECT.name());
    if (!list.contains(fieldDependOnContentType.toString())) {
      return true;
    }
    return StringUtils.isNotBlank((String)fieldValue);
  }

  public boolean isValidBranchReceive(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnBranchReceive = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (!Objects.equals("V005", fieldDependOnBranchReceive)) {
      return true;
    }
    return StringUtils.isNotBlank((String)fieldValue);
  }

  public boolean isValidPayPeriod(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnDebtPayMethod = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (!Objects.equals("V005", fieldDependOnDebtPayMethod)) {
      return true;
    }
    return Objects.nonNull(fieldValue);
  }

  public boolean isValidPayUnit(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnDebtPayMethod = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (!Objects.equals("V005", fieldDependOnDebtPayMethod)) {
      return true;
    }
    return StringUtils.isNotBlank((String) fieldValue);
  }

  public boolean isValidSubCreditCards(ConstraintValidatorContext constraintValidatorContext,
      String fieldNeedValidate, String field, Object o, String[] fieldDependOns) {
    Object fieldDependOnHasSubCard = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (Objects.equals(Boolean.FALSE, fieldDependOnHasSubCard)) {
      return true;
    }

    List<SubCreditCardDTO> fieldValue = (List<SubCreditCardDTO>) new BeanWrapperImpl(
        o).getPropertyValue(field);
    if (CollectionUtils.isEmpty(fieldValue)) {
      constraintValidatorContext.buildConstraintViolationWithTemplate(
          JAVAX_CONSTRAINT_VALIDATOR_NOT_EMPTY).addPropertyNode(field).addConstraintViolation();

      return false;
    }

    BigDecimal fieldDependOnLoanAmount = (BigDecimal) new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[1]);
    if (fieldDependOnLoanAmount == null) {
      return true;
    }

    boolean valid = true;
    int count = 0;
    for (SubCreditCardDTO subCreditCardDTO : fieldValue) {
      if (subCreditCardDTO.getCardLimitAmount() != null
          && fieldDependOnLoanAmount.compareTo(subCreditCardDTO.getCardLimitAmount()) < 0) {
        constraintValidatorContext.buildConstraintViolationWithTemplate(
                "{custom.validation.constraints.LessThanOrEqual.message} " + fieldDependOns[1])
            .addPropertyNode("subCreditCards[" + count + "]." + fieldNeedValidate)
            .addConstraintViolation();
        valid = false;
      }
      count++;
    }

    return valid;
  }

  public boolean isValidIdentities(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnCustomerType = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (Objects.equals(EB, fieldDependOnCustomerType)) {
      return true;
    }
    return CollectionUtils.isNotEmpty((Collection<?>) fieldValue);
  }

  public boolean isValidCapitalContributionRate(Object o, Object fieldValue,
      String[] fieldDependOns) {
    Object fieldDependOnIncomeType = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (Objects.equals(INDIVIDUAL_BUSINESS, fieldDependOnIncomeType)) {
      return true;
    }
    return Objects.nonNull(fieldValue);
  }

  public boolean isValidBusinessRegistrationNumber(Object o, Object fieldValue,
      String[] fieldDependOns) {
    Object fieldDependOnIncomeType = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);

    if (!Objects.equals(ENTERPRISE_BUSINESS, fieldDependOnIncomeType)) {
      return true;
    }

    return StringUtils.isNotBlank((String) fieldValue);
  }

  public boolean isValidDeductAccountNumber(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnAutoDeductRate = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);

    if (!isDeduct((String) fieldDependOnAutoDeductRate)) {
      return true;
    }

    return StringUtils.isNotBlank((String) fieldValue);
  }

  /**
   * Verify required list file checklist
   *
   * @param o              Object
   * @param fieldValue     Object
   * @param fieldDependOns String[]
   * @return Boolean
   */
  public boolean isValidChecklistListFile(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnIsRequired = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    String fieldDependOnReturnCode = (String) new BeanWrapperImpl(o).getPropertyValue(
        fieldDependOns[1]);

    if (Objects.equals(Boolean.FALSE, fieldDependOnIsRequired)) {
      return true;
    }

    if (StringUtils.isNotBlank(fieldDependOnReturnCode)) {
      return true;
    }

    return CollectionUtils.isNotEmpty((Collection<?>) fieldValue);
  }

  /**
   * Verify loan amount
   *
   * @param o              Object
   * @param fieldValue     Object
   * @param fieldDependOns String[]
   * @return Boolean
   */
  public static boolean isValidLoanAmount(Object o, Object fieldValue, String[] fieldDependOns) {
    Object fieldDependOnCreditType = new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (Objects.equals(CARD.getCode(), fieldDependOnCreditType) && fieldValue != null) {
      return ((BigDecimal) fieldValue).compareTo(LIMIT_CARD_AMOUNT) < 1;
    }

    return true;
  }

  private Map<Long, Boolean> getMapRefCustomerMsbMember(Object o, String customerAndRelationField) {
    CustomerAndRelationPersonDTO customerAndRelationPerson = (CustomerAndRelationPersonDTO) new BeanWrapperImpl(
        o).getPropertyValue(customerAndRelationField);

    if (customerAndRelationPerson == null) {
      return Collections.emptyMap();
    }

    CustomerDTO customer = customerAndRelationPerson.getCustomer();
    Set<CustomerDTO> customerRelations = customerAndRelationPerson.getCustomerRelations();

    if (CollectionUtils.isNotEmpty(customerRelations)) {
      customerRelations.add(customer);
    } else {
      customerRelations = new HashSet<>(Collections.singletonList(customer));
    }

    return customerRelations.stream().map(IndividualCustomerDTO.class::cast).collect(
        Collectors.toMap(IndividualCustomerDTO::getRefCustomerId,
            IndividualCustomerDTO::isMsbMember));
  }

  private List<BaseIncomeDTO> getIncomeItems(ApplicationIncomeDTO income) {
    return new ArrayList<>((ACTUALLY.equalsIgnoreCase(income.getIncomeRecognitionMethod())
        ? ((ActuallyIncomeDTO) income).getIncomeItems()
        : ((ExchangeIncomeDTO) income).getIncomeItems())).stream()
        .sorted(comparing(BaseIncomeDTO::getOrderDisplay, nullsLast(naturalOrder()))).collect(Collectors.toList());
  }

  public boolean isValidIncomes(ConstraintValidatorContext cvx, Object fieldValue, Object o,
      String[] fieldDependOns, String[] fieldNeedValidates, String fieldValidate) {
    List<ApplicationIncomeDTO> incomes = ((Set<ApplicationIncomeDTO>) fieldValue).stream()
        .sorted(comparing(ApplicationIncomeDTO::getOrderDisplay, nullsLast(naturalOrder()))).collect(Collectors.toList());

    if (CollectionUtils.isEmpty(incomes)) {
      return true;
    }

    List<String> fieldNeedValidateConverts = Arrays.asList(fieldNeedValidates);

    Map<Long, Boolean> refCustomerMsbMember = getMapRefCustomerMsbMember(o, fieldDependOns[0]);

    int incomeIdx = 0;
    int incomeItemIdx = 0;
    int validFailCount = 0;

    for (ApplicationIncomeDTO income : incomes) {
      List<BaseIncomeDTO> incomeItems = getIncomeItems(income);

      for (BaseIncomeDTO incomeItem : incomeItems) {
        validFailCount += validRankType(fieldNeedValidateConverts, incomeItem, refCustomerMsbMember,
            cvx, fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validKpiRating(fieldNeedValidateConverts, incomeItem,
            refCustomerMsbMember, cvx, fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validRecognizedIncome(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validRentalPrice(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validProductionProcess(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validRecordMethod(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validInput(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validOutput(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validBusinessScale(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validInventory(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validEvaluationPeriod(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validIncomeMonthly(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validExpenseMonthly(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validProfitMonthly(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validProfitMargin(fieldNeedValidateConverts, income, incomeItem, cvx,
            fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validCapitalContributionRate(fieldNeedValidateConverts, income,
            incomeItem, cvx, fieldValidate, incomeIdx, incomeItemIdx);

        validFailCount += validRentalIncomeAddress(cvx, incomeItem, incomeItemIdx);

        incomeItemIdx++;
      }
      incomeIdx++;
    }

    return validFailCount < 1;
  }

  private static int validCapitalContributionRate(List<String> fieldNeedValidateConverts,
      ApplicationIncomeDTO income, BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx,
      String fieldValidate,
      int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("capitalContributionRate")
        && ACTUALLY.equalsIgnoreCase(income.getIncomeRecognitionMethod())
        && ENTERPRISE_BUSINESS.equalsIgnoreCase(incomeItem.getIncomeType())
        && !isValidObjectFieldIncome(income,
        ((BusinessIncomeDTO) incomeItem).getCapitalContributionRate())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "capitalContributionRate"),
          JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
      return 1;
    }
    return 0;
  }

  private int validRankType(List<String> fieldNeedValidateConverts, BaseIncomeDTO incomeItem,
      Map<Long, Boolean> refCustomerMsbMember, ConstraintValidatorContext cvx, String fieldValidate,
      int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("rankType") && !isValidRankType(incomeItem,
        refCustomerMsbMember)) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "rankType"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validKpiRating(List<String> fieldNeedValidateConverts, BaseIncomeDTO incomeItem,
      Map<Long, Boolean> refCustomerMsbMember, ConstraintValidatorContext cvx, String fieldValidate,
      int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("kpiRating") && !isValidKpiRating(incomeItem,
        refCustomerMsbMember)) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "kpiRating"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validRecognizedIncome(List<String> fieldNeedValidateConverts,
      ApplicationIncomeDTO income, BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx,
      String fieldValidate, int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("recognizedIncome") && !isValidObjectFieldIncome(income,
        incomeItem.getRecognizedIncome())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "recognizedIncome"), JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
      return 1;
    }
    return 0;
  }

  private int validRentalPrice(List<String> fieldNeedValidateConverts, ApplicationIncomeDTO income,
      BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx, String fieldValidate, int incomeIdx,
      int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("rentalPrice") && RENTAL.equalsIgnoreCase(
        incomeItem.getIncomeType()) && !isValidObjectFieldIncome(income,
        ((RentalIncomeDTO) incomeItem).getRentalPrice())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "rentalPrice"), JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
      return 1;
    }
    return 0;
  }

  private int validProductionProcess(List<String> fieldNeedValidateConverts,
      ApplicationIncomeDTO income, BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx,
      String fieldValidate, int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("productionProcess")
        && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(incomeItem.getIncomeType())
        && !isValidStringFieldIncome(income,
        ((BusinessIncomeDTO) incomeItem).getProductionProcess())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "productionProcess"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validRecordMethod(List<String> fieldNeedValidateConverts, ApplicationIncomeDTO income,
      BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx, String fieldValidate, int incomeIdx,
      int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("recordMethod") && INDIVIDUAL_BUSINESS.equalsIgnoreCase(
        incomeItem.getIncomeType()) && !isValidStringFieldIncome(income,
        ((BusinessIncomeDTO) incomeItem).getRecordMethod())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "recordMethod"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validInput(List<String> fieldNeedValidateConverts, ApplicationIncomeDTO income,
      BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx, String fieldValidate, int incomeIdx,
      int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("input") && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(
        incomeItem.getIncomeType()) && !isValidStringFieldIncome(income,
        ((BusinessIncomeDTO) incomeItem).getInput())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "input"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validOutput(List<String> fieldNeedValidateConverts, ApplicationIncomeDTO income,
      BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx, String fieldValidate, int incomeIdx,
      int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("output") && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(
        incomeItem.getIncomeType()) && !isValidStringFieldIncome(income,
        ((BusinessIncomeDTO) incomeItem).getOutput())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "output"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validBusinessScale(List<String> fieldNeedValidateConverts,
      ApplicationIncomeDTO income, BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx,
      String fieldValidate, int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("businessScale")
        && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(incomeItem.getIncomeType())
        && !isValidStringFieldIncome(income, ((BusinessIncomeDTO) incomeItem).getBusinessScale())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "businessScale"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validInventory(List<String> fieldNeedValidateConverts, ApplicationIncomeDTO income,
      BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx, String fieldValidate, int incomeIdx,
      int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("inventory")
        && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(incomeItem.getIncomeType())
        && !isValidStringFieldIncome(income, ((BusinessIncomeDTO) incomeItem).getInventory())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "inventory"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validEvaluationPeriod(List<String> fieldNeedValidateConverts,
      ApplicationIncomeDTO income, BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx,
      String fieldValidate, int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("evaluationPeriod")
        && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(incomeItem.getIncomeType())
        && !isValidStringFieldIncome(income,
        ((BusinessIncomeDTO) incomeItem).getEvaluationPeriod())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "evaluationPeriod"), JAVAX_CONSTRAINT_VALIDATOR_NOT_BLANK);
      return 1;
    }
    return 0;
  }

  private int validIncomeMonthly(List<String> fieldNeedValidateConverts,
      ApplicationIncomeDTO income, BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx,
      String fieldValidate, int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("incomeMonthly")
        && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(incomeItem.getIncomeType())
        && !isValidObjectFieldIncome(income, ((BusinessIncomeDTO) incomeItem).getIncomeMonthly())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "incomeMonthly"), JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
      return 1;
    }
    return 0;
  }

  private int validExpenseMonthly(List<String> fieldNeedValidateConverts,
      ApplicationIncomeDTO income, BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx,
      String fieldValidate, int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("expenseMonthly")
        && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(incomeItem.getIncomeType())
        && !isValidObjectFieldIncome(income,
        ((BusinessIncomeDTO) incomeItem).getExpenseMonthly())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "expenseMonthly"), JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
      return 1;
    }
    return 0;
  }

  private int validProfitMonthly(List<String> fieldNeedValidateConverts,
      ApplicationIncomeDTO income, BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx,
      String fieldValidate, int incomeIdx, int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("profitMonthly")
        && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(incomeItem.getIncomeType())
        && !isValidObjectFieldIncome(income, ((BusinessIncomeDTO) incomeItem).getProfitMonthly())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "profitMonthly"), JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
      return 1;
    }
    return 0;
  }

  private int validProfitMargin(List<String> fieldNeedValidateConverts, ApplicationIncomeDTO income,
      BaseIncomeDTO incomeItem, ConstraintValidatorContext cvx, String fieldValidate, int incomeIdx,
      int incomeItemIdx) {
    if (fieldNeedValidateConverts.contains("profitMargin")
        && INDIVIDUAL_ENTERPRISE_INCOME_TYPE.contains(incomeItem.getIncomeType())
        && !isValidObjectFieldIncome(income, ((BusinessIncomeDTO) incomeItem).getProfitMargin())) {
      buildConstraintValidatorContext(cvx,
          String.format(INCOME_ITEM_FIELD_PROPERTY_NODE, fieldValidate, incomeIdx, incomeItemIdx,
              "profitMargin"), JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
      return 1;
    }
    return 0;
  }

  public boolean isValidCustomerAndRelationPerson(ConstraintValidatorContext cvx,
      Object fieldValue,
      String[] fieldNeedValidates, CICRepository cicRepository, AmlOprRepository amlOprRepository) {

    List<String> fieldNeedValidateConverts = Arrays.asList(fieldNeedValidates);

    CustomerAndRelationPersonDTO customerAndRelationPerson = (CustomerAndRelationPersonDTO) fieldValue;

    int validFailCount = 0;
    validFailCount += validAmlOpr(cvx, customerAndRelationPerson.getAmlOpr(),
        amlOprRepository, fieldNeedValidateConverts);

    validFailCount += validCic(cvx, customerAndRelationPerson.getCic(), cicRepository,
        fieldNeedValidateConverts);

    validFailCount += validAddressTypeForCustomer(cvx, customerAndRelationPerson.getCustomer(), null);

    if (CollectionUtils.isNotEmpty(customerAndRelationPerson.getCustomerRelations())) {
      int index = 0;
      for (CustomerDTO customerRelation : customerAndRelationPerson.getCustomerRelations()) {
        validFailCount += validAddressTypeForCustomer(cvx, customerRelation, index);
        validFailCount += validRelationTypeForCustomer(cvx, customerRelation, index);
        index++;
      }
    }

    return validFailCount < 1;
  }

  private int validAmlOpr(ConstraintValidatorContext cvx, AmlOprDTO amlOpr,
      AmlOprRepository amlOprRepository, List<String> fieldNeedValidateConverts) {

    if (!fieldNeedValidateConverts.contains("amlOpr") || amlOpr == null || CollectionUtils.isEmpty(
        amlOpr.getGenerals())) {
      return 0;
    }

    int amlOprGeneralIdx = 0;
    int validFailCount = 0;
    for (AmlOprGeneral amlOprGeneral : amlOpr.getGenerals()) {
      if (Objects.isNull(amlOprGeneral.getAmlId()) ||
          !amlOprRepository.existsByIdAndIdentifierCode(amlOprGeneral.getAmlId(),
          amlOprGeneral.getIdentifierCode())) {
        buildConstraintValidatorContext(cvx,
            "customerAndRelationPerson.amlOpr.generals[" + amlOprGeneralIdx + "]",
            "{custom.validation.constraints.AmlOprMustSync.message}");
        validFailCount++;
      }
      if (Objects.isNull(amlOprGeneral.getOprId()) ||
          !amlOprRepository.existsByIdAndIdentifierCode(amlOprGeneral.getOprId(),
              amlOprGeneral.getIdentifierCode())) {
        buildConstraintValidatorContext(cvx,
            "customerAndRelationPerson.amlOpr.generals[" + amlOprGeneralIdx + "]",
            "{custom.validation.constraints.AmlOprMustSync.message}");
        validFailCount++;
      }
      amlOprGeneralIdx++;
    }

    return validFailCount;
  }

  private int validCic(ConstraintValidatorContext cvx, CicDTO cic, CICRepository cicRepository,
      List<String> fieldNeedValidateConverts) {

    if (!fieldNeedValidateConverts.contains("cic") || cic == null || CollectionUtils.isEmpty(
        cic.getCicDetails())) {
      return 0;
    }

    int cicDetailIdx = 0;
    int validFailCount = 0;

    for (CicDetail cicDetail : cic.getCicDetails()) {
      if (Objects.nonNull(cicDetail.getId()) && cicRepository.existsByIdAndIdentifierCode(
          cicDetail.getId(), cicDetail.getIdentifierCode())) {
        validFailCount = 0;
        break;
      } else {
        buildConstraintValidatorContext(cvx,
            "customerAndRelationPerson.cic.cicDetails[" + cicDetailIdx + "]",
            "{custom.validation.constraints.CicMustLookedUp.message}");
        validFailCount++;
      }

      cicDetailIdx++;
    }

    return validFailCount;
  }

  private int validAddressTypeForCustomer(ConstraintValidatorContext cvx, CustomerDTO customer,
      Integer index) {

    if (customer == null) {
      return 0;
    }

    int v001Count = 0;
    int v002Count = 0;

    for (CustomerAddressDTO address : customer.getAddresses()) {
      if (AddressType.HK_THUONG_TRU.getValue().equalsIgnoreCase(address.getAddressType())) {
        v001Count++;
      }
      if (AddressType.DIA_CHI_TSC.getValue().equalsIgnoreCase(address.getAddressType())) {
        v002Count++;
      }
    }

    if (customer.isMainCustomer() && (v001Count < 1 || v002Count < 1)) {
      buildConstraintValidatorContext(cvx, "customerAndRelationPerson.customer",
          "{custom.validation.constraints.CustomerAddress.must.have.2.addr}");
      return 1;
    }

    if (!customer.isMainCustomer() && v001Count < 1) {
      buildConstraintValidatorContext(cvx,
          "customerAndRelationPerson.customerRelations[" + index + "]",
          "{custom.validation.constraints.CustomerAddress.must.have.1.addr}");
      return 1;
    }

    return 0;
  }

  public static boolean isValidIncomeOwnerName(Object o, Object fieldValue, String[] fieldDependOns) {
    String incomeOwner = (String) new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);

    if ("V017".equals(incomeOwner)) {
      return true;
    }

    return StringUtils.isNotBlank((String) fieldValue);
  }

  public static boolean isValidGuaranteeForm(Object o, Object fieldValue, String[] fieldDependOns) {
    String creditType = (String) new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (!"V001".equals(creditType)) {
      return true;
    }

    return StringUtils.isNotBlank((String)fieldValue);
  }

  public static boolean isValidCustomerEmail(Object o, Object fieldValue, String[] fieldDependOns) {
    boolean mainCustomer = (boolean) new BeanWrapperImpl(o).getPropertyValue(fieldDependOns[0]);
    if (mainCustomer) {
      return StringUtils.isNotBlank((String) fieldValue);
    }
    return true;
  }

  public static boolean isValidCreditAllocation(Set<ApplicationAssetAllocationDTO> assetAllocations, Set<ApplicationCreditDTO> credits){
    try {
      for (ApplicationAssetAllocationDTO assetAllocationDTO : assetAllocations) {
        List<Long> countTmp= credits.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsAllocation()) && !CollectionUtils.isEmpty(c.getAssets()))
                .flatMap(credit -> credit.getAssets().stream())
                .filter(assetId -> assetId.equals(assetAllocationDTO.getAssetId())).collect(Collectors.toList());
        if(countTmp.isEmpty()) continue;

        if (assetAllocationDTO.getCreditAllocations().size() != countTmp.size()) {
          return false;
        }

        boolean isNotAllocation = assetAllocationDTO.getCreditAllocations().stream()
                .anyMatch(creditAllocationDTO -> creditAllocationDTO.getPercentValue() == null || creditAllocationDTO.getPercentValue() == 0);

        if (!isNotAllocation) {
          double percent = assetAllocationDTO.getCreditAllocations().stream()
                  .mapToDouble(CreditAllocationDTO::getPercentValue)
                  .sum();

          percent = formatDouble(percent);
          if (percent < 0D || percent > 100D) {
            return false;
          }

        } else {
          return false;
        }
      }
    }catch (Exception e){
      return false;
    }
    return true;
  }

  private static double formatDouble(double percent) {
    DecimalFormat decimalFormat = new DecimalFormat("#.###");
    percent = new Double(decimalFormat.format(percent));
    return percent;
  }

  public static boolean checkSumPercentAllocation(Set<ApplicationAssetAllocationDTO> assetAllocations) {
    if(CollectionUtils.isEmpty(assetAllocations)) {
      return true;
    }
    // Check sum percent
    List<ApplicationAssetAllocationDTO> counts = new ArrayList<>();
    assetAllocations.forEach(d -> {
      double percent = formatDouble(d.getCreditAllocations().stream()
              .filter(c -> c.getPercentValue() != null)
              .mapToDouble(CreditAllocationDTO::getPercentValue).sum());
      if((percent > 100D)
      || d.getCreditAllocations().stream().anyMatch(al -> ObjectUtils.isEmpty(al.getPercentValue()))
      ) {
        counts.add(d);
      }
    });
    return counts.isEmpty();
  }

  private int validRelationTypeForCustomer(ConstraintValidatorContext cvx, CustomerDTO customer,
                                          Integer index) {

    if (customer == null) {
      return 0;
    }

    if(ObjectUtils.isEmpty( customer.getRelationshipRefId())
            || ObjectUtils.isEmpty( customer.getRelationship())) {
      buildConstraintValidatorContext(cvx,
              "customerAndRelationPerson.customerRelations[" + index + "]",
              "{custom.validation.constraints.relations}");
      return 1;
    }

    return 0;
  }

  private static int validRentalIncomeAddress(ConstraintValidatorContext cvx, BaseIncomeDTO incomeItem, Integer index) {
    int countError = 0;
    if (incomeItem instanceof RentalIncomeDTO) {
      if (ApplicationConstant.REAL_ESTATE.equalsIgnoreCase(((RentalIncomeDTO) incomeItem).getAssetType())) {
        if (ObjectUtils.isEmpty(((RentalIncomeDTO) incomeItem).getAddress().getCityCode())) {
          buildConstraintValidatorContext(cvx,
                  "incomes.incomeItems[" + index + "].address.cityCode", JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
          countError++;
        }
        if (ObjectUtils.isEmpty(((RentalIncomeDTO) incomeItem).getAddress().getDistrictCode())) {
          buildConstraintValidatorContext(cvx,
                  "incomes.incomeItems[" + index + "].address.districtCode", JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
          countError++;
        }
        if (ObjectUtils.isEmpty(((RentalIncomeDTO) incomeItem).getAddress().getWardCode())) {
          buildConstraintValidatorContext(cvx,
                  "incomes.incomeItems[" + index + "].address.wardCode", JAVAX_CONSTRAINT_VALIDATOR_NOT_NULL);
          countError++;
        }
      }
    }
    return countError > 0 ? 1 : 0;
  }

}