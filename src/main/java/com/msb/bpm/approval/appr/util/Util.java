package com.msb.bpm.approval.appr.util;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_FIELD_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CHECK_LIST_TAB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSET_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.ASSET_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.FIELD_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_BM;
import static com.msb.bpm.approval.appr.enums.configuration.ConfigurationCategory.*;
import static com.msb.bpm.approval.appr.exception.DomainCode.TYPE_UNSUPPORTED;
import static java.util.stream.Collectors.joining;

import com.msb.bpm.approval.appr.constant.Constant;
import com.msb.bpm.approval.appr.enums.application.ApprovalResult;
import com.msb.bpm.approval.appr.enums.application.CriteriaGroup;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.authority.AuthorityRequestMapDTO;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.configuration.CategoryDataResponse;
import com.msb.bpm.approval.appr.model.response.configuration.MercuryDataResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.OrganizationTreeDetail;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ResourceUtils;

@Slf4j
@UtilityClass
public class Util {

  public static final String BPM_ID_FORMAT = "151-%s";
  public static final List<String> LIST_TABS =
      Arrays.asList(INITIALIZE_INFO,
              FIELD_INFO,
              DEBT_INFO,
              ASSET_INFO);
  public static final DateTimeFormatter FORMATTER_YYYYMMDD =
      DateTimeFormatter.ofPattern("yyyyMMdd");

  public static final String FILE_PATH_MINIO_FORMAT = "%s/%s/%s/%s";
  public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
  public static final String VN_DATE_FORMAT = "HH:mm:ss dd/MM/yyyy";
  public static final String SIGN_OTP_TEXT = "Ngày %s Đã Ký %s %s";
  public static final String NOTE_CMS = "Ngày phản hồi %s Người phản hồi %s thuộc role %s nội dung phản hồi: %s";

  private static final String regex = "\\[.*?\\]";

  public static final List<String> categoryCodes = Arrays.asList(GENDER.getCode(), MARTIAL_STATUS.getCode(),
          DOCUMENT_TYPE.getCode(), RB_ADDRESS_TYPE_V001.getCode(), RB_ADDRESS_TYPE_V002.getCode(),
          EB_ADDRESS_TYPE_V001.getCode(), ISSUE_BY.getCode(), PROCESSING_STEP.getCode(),
          PROCESS_FLOW.getCode(), SUBMISSION_PURPOSE.getCode(), NATIONAL.getCode(),
          RELATIONSHIP.getCode(), CARD_TYPE.getCode(), AUTO_DEDUCT_RATE.getCode(),
          CARD_FORM.getCode(), CARD_RECEIVE_ADDRESS.getCode(),
          LOAN_PURPOSE.getCode(), CONVERSION_METHOD.getCode(),
          PAY_TYPE.getCode(), RENTAL_PURPOSE.getCode(), ASSET_TYPE.getCode(),
          LABOR_TYPE.getCode(), DETAIL_INCOME_OTHER.getCode(), INCOME_TYPE.getCode(),
          CREDIT_TYPE.getCode(), GUARANTEE_FORM.getCode(), CREDIT_FORM.getCode());

  public Locale locale() {
    return LocaleContextHolder.getLocale();
  }

  public List<String> getSaveDataAcceptedRequestType() {
    return Arrays.asList(POST_INITIALIZE_INFO,
            POST_FIELD_INFO,
            POST_DEBT_INFO,
            POST_CHECK_LIST_TAB,
            POST_ASSET_INFO);
  }

  public static String mapTabCode(String type) {
    switch (type) {
      case POST_INITIALIZE_INFO:
        return INITIALIZE_INFO;
      case POST_FIELD_INFO:
        return FIELD_INFO;
      case POST_DEBT_INFO:
        return DEBT_INFO;
      case POST_ASSET_INFO:
        return ASSET_INFO;
      default:
        throw new ApprovalException(TYPE_UNSUPPORTED);
    }
  }

  public static <T extends Annotation> T getAnnotation(
      JoinPoint joinPoint, Class<T> annotationClass) {
    T annotation = null;

    Target annotationTarget = annotationClass.getAnnotation(Target.class);
    ElementType[] elementTypes = annotationTarget.value();

    boolean isMethodAnnotation = ArrayUtils.contains(elementTypes, ElementType.METHOD);
    if (isMethodAnnotation) {

      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      Method method = methodSignature.getMethod();

      if (method.isAnnotationPresent(annotationClass)) {
        annotation = method.getAnnotation(annotationClass);
      }
    }

    if (null == annotation) {
      boolean isTypeAnnotation = ArrayUtils.contains(elementTypes, ElementType.TYPE);
      if (isTypeAnnotation) {
        Object target = joinPoint.getTarget();
        Class<?> targetClass = target.getClass();
        annotation = targetClass.getAnnotation(annotationClass);
      }
    }

    return annotation;
  }

  public static String getCurrDate(String format) {
    return new SimpleDateFormat(format).format(new Date());
  }

  public static ProcessingRole checkRole(ProcessingRole givebackRole) {
    if (givebackRole == null) {
      return ProcessingRole.PD_RB_UNDEFINED;
    }
    if (givebackRole == PD_RB_BM) {
      return ProcessingRole.PD_RB_UNDEFINED;
    }
    return givebackRole;
  }

  public static String checkCustomerSegment(CustomerDTO customerDTO) {
    return RB.equalsIgnoreCase(customerDTO.getCustomerType())
        ? ((IndividualCustomerDTO) customerDTO).getSubject()
        : null;
  }

  public static List<String> checkRiskGroup(AuthorityRequestMapDTO authorityRequestMapDTO) {
    if (CollectionUtils.isEmpty(authorityRequestMapDTO.getSpecialRiskContents())) {
      return Collections.emptyList();
    }

    return authorityRequestMapDTO.getSpecialRiskContents().stream()
        .map(ApplicationAppraisalContentDTO::getCriteriaGroup)
        .collect(Collectors.toList());
  }

  public static Set<String> checkAdditionAuthorization(
      AuthorityRequestMapDTO authorityRequestMapDTO) {
    if (CollectionUtils.isEmpty(authorityRequestMapDTO.getAdditionalContents())) {
      return Collections.emptySet();
    }

    return authorityRequestMapDTO.getAdditionalContents().stream()
        .filter(filter -> CriteriaGroup.OTHER.getCode().equalsIgnoreCase(filter.getCriteriaGroup()))
        .map(ApplicationAppraisalContentDTO::getAuthorization)
        .collect(Collectors.toSet());
  }

  public static String getFinalApprovalResult(Set<ApplicationCreditDTO> credits) {
    return credits.stream()
        .allMatch(credit -> ApprovalResult.N.name().equalsIgnoreCase(credit.getApproveResult()))
        ? ApprovalResult.N.name()
        : ApprovalResult.Y.name();
  }

  public static Set<String> convertDetailToSet(String detail) {
    return StringUtils.isNotBlank(detail) ? new HashSet<>(Arrays.asList(detail.split(","))) : null;
  }

  public static String convertDetailToString(Set<String> detail) {
    return CollectionUtils.isNotEmpty(detail) ? String.join(",", detail) : null;
  }

  public static OrganizationTreeDetail getTreeDetailByType(
      String type, OrganizationTreeDetail request) {
    if (Objects.isNull(request)) {
      return null;
    }

    if (StringUtils.isNotBlank(type) && type.equalsIgnoreCase(request.getType())) {
      return request;
    }

    OrganizationTreeDetail response = null;
    if (CollectionUtils.isEmpty(request.getChildren())) {
      return null;
    }

    for (OrganizationTreeDetail treeDetail : request.getChildren()) {
      if (StringUtils.isNotBlank(type) && type.equalsIgnoreCase(treeDetail.getType())) {
        response = treeDetail;
        break;
      } else {
        response = getTreeDetailByType(type, treeDetail);
      }
    }

    return response;
  }

  public static boolean isNullOrEmpty(final Collection<?> c) {
    return c == null || c.isEmpty();
  }

  public static ChecklistDto getChecklistMappingId(
      ChecklistBaseResponse<GroupChecklistDto> checklistResponse, String checklistCode) {

    try {
      if (!Objects.isNull(checklistResponse.getData())) {
        Optional<ChecklistDto> checklistDto =
            checklistResponse.getData().getListChecklist().stream()
                .filter(checklist -> checklistCode.equalsIgnoreCase(checklist.getCode()))
                .findFirst();
        return checklistDto.isPresent() ? checklistDto.get() : null;
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return null;
  }

  public static String splitFirstName(String fullName) {
    if (StringUtils.isBlank(fullName)) {
      return "";
    }
    int idx = fullName.lastIndexOf(' ');
    if (idx < 0) {
      return fullName;
    }
    return fullName.substring(idx).trim();
  }

  public static String splitLastName(String fullName) {
    if (StringUtils.isBlank(fullName)) {
      return "";
    }
    int idx = fullName.lastIndexOf(' ');
    if (idx < 0) {
      return "";
    }
    return fullName.substring(0, idx).trim();
  }

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  public static String getExtensionFile(String fileName) {
    if (StringUtils.isEmpty(fileName)) {
      return "";
    }
    int i = fileName.lastIndexOf('.');
    if (i > 0) {
      return fileName.substring(i + 1);
    }
    return "";
  }

  public String convertFullName(String firstName, String lastName) {
    firstName = StringUtils.defaultIfBlank(firstName, "");
    lastName = StringUtils.defaultIfBlank(lastName, "");

    String fullName =
        StringUtils.join(
            StringUtils.defaultIfBlank(lastName, ""),
            " ",
            StringUtils.defaultIfBlank(firstName, ""),
            " ");

    return StringUtils.trimToEmpty(fullName);
  }

  public String buildDocumentNo(ApplicationEntity entity, String pattern) {
    Pattern paramPattern = Pattern.compile(regex);
    Matcher m = paramPattern.matcher(pattern);
    while (m.find()) {
      log.info("matcher: {}", m.group());
      switch (m.group()) {
        case "[ID hồ sơ]":
          pattern = pattern.replace(m.group(), entity.getBpmId());
          break;
        case "[Năm phê duyệt]":
          pattern = pattern.replace(m.group(), getCurrDate(Constant.YYYY_MM_DD_FORMAT));
          break;
        case "[CIF]":
          pattern = pattern.replace(m.group(),
              entity.getCustomer().getCif() == null ? "   "
                  : entity.getCustomer().getCif());
          break;
        case "[MB/MN]":
          pattern =
              pattern.replace(
                  m.group(), "Miền Bắc".equalsIgnoreCase(entity.getRegion()) ? "MB" : "MN");
          break;
        default:
          break;
      }
    }
    return pattern;
  }

  /**
   * Lấy tên service từ base url
   *
   * @param baseUrl String
   * @return String
   */
  public String getServiceNameFromBaseUrl(String baseUrl) {
    if (StringUtils.isBlank(baseUrl)) {
      return "";
    }

    return baseUrl.substring(baseUrl.lastIndexOf("/") + 1, baseUrl.length());
  }

  /**
   * Lấy tên bước xử lý từ danh mục MD014
   *
   * @param stepCode      String
   * @param dataResponses List<CategoryDataResponse>
   * @return String
   */
  public String getProcessingStep(String stepCode, List<CategoryDataResponse> dataResponses) {
    return dataResponses.stream()
        .filter(filterData -> stepCode.equalsIgnoreCase(filterData.getCode()))
        .findFirst()
        .map(CategoryDataResponse::getValue)
        .orElse(null);
  }

  public String readProperty(String key) {
    File file;
    try {
      file = ResourceUtils.getFile("classpath:generate/auto-generate.properties");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return "-1";
    }
    try (FileReader reader = new FileReader(file)) {
      Properties p = new Properties();
      p.load(reader);
      return p.getProperty(key);
    } catch (IOException e) {
      e.printStackTrace();
      return "-1";
    }
  }

  @SneakyThrows
  public void writeProperty(String key, String value) {
    File file = ResourceUtils.getFile("classpath:generate/auto-generate.properties");

    try (FileReader reader = new FileReader(file);FileWriter writer = new FileWriter(file)) {
      Properties p = new Properties();
      p.load(reader);
      p.setProperty(key, value);
      p.store(writer, "write a file");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public String getFullName(String firstName, String lastName) {
    return Stream.of(firstName, lastName)
        .filter(x -> x != null && !x.isEmpty())
        .collect(joining(" "));
  }

  public String escapeMetaCharacters(String inputString){
    final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")","*","+","|","<",">","%"};

    for (int i = 0 ; i < metaCharacters.length ; i++){
      if(inputString.contains(metaCharacters[i])){
        inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
      }
    }
    return inputString;
  }

  public String getValueByCategoryCode(String code, List<CategoryDataResponse> dataResponses) {
    return dataResponses.stream()
            .filter(filterData -> code.equalsIgnoreCase(filterData.getCode()))
            .findFirst()
            .map(CategoryDataResponse::getValue)
            .orElse(null);
  }

  public static String getNamePlaceByCodeFromCacheMercury(MercuryDataResponse lstPlaces,
                                                          String code) {
    try {
      if (StringUtils.isEmpty(code) || !code.chars().allMatch(Character::isDigit)) {
        return "";
      }
      if (Objects.nonNull(lstPlaces) && CollectionUtils
              .isNotEmpty(lstPlaces.getValue())) {
        MercuryDataResponse.Value cityVal = lstPlaces.getValue().stream().filter(e -> StringUtils
                .isNotBlank(e.getId()) && code.equalsIgnoreCase(e.getId())).findFirst().orElse(null);
        if (Objects.nonNull(cityVal)) {
          return cityVal.getName();
        }
      }
    } catch (Exception ex) {
      log.error("getCityByCodeFromCacheMercury END with error: {}", ex.getMessage());
    }
    return "";
  }

  public static LocalDateTime parseLocalDate(String date, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return LocalDateTime.parse(date, formatter);
  }

  public static LocalDate parseString2LocalDate(String date, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return LocalDate.parse(date, formatter);
  }
}
