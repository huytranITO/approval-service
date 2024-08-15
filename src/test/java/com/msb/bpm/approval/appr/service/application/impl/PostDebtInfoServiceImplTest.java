package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.BlockKey.OTHER_REVIEW;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_FORMAT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msb.bpm.approval.appr.client.collateral.AssetAllocationConfigProperties;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralConfigProperties;
import com.msb.bpm.approval.appr.client.creditconditions.CreditConditionClient;
import com.msb.bpm.approval.appr.config.StringTrimModuleConfig;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.collateral.AssetAllocationMapper;
import com.msb.bpm.approval.appr.model.dto.*;
import com.msb.bpm.approval.appr.model.dto.ApplicationPhoneExpertiseDTO.ApplicationPhoneExpertiseDtlDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.ApplicationAssetAllocationDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.CreditAllocationDTO;
import com.msb.bpm.approval.appr.model.entity.*;
import com.msb.bpm.approval.appr.model.request.collateral.AssetAllocationRequest;
import com.msb.bpm.approval.appr.model.request.collateral.CreditAssetRequest;
import com.msb.bpm.approval.appr.model.request.creditconditions.CreateCreditConditionRequest;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import com.msb.bpm.approval.appr.model.response.collateral.AssetAllocationResponse;
import com.msb.bpm.approval.appr.model.response.collateral.CreditAssetAllocationResponse;
import com.msb.bpm.approval.appr.model.response.collateral.CreditAssetMappingResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionClientResponse;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionResponse;
import com.msb.bpm.approval.appr.repository.ApplicationCreditConditionRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 22/10/2023, Sunday
 **/
@ExtendWith(MockitoExtension.class)
class PostDebtInfoServiceImplTest {

  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private MessageSource messageSource;

  @Mock
  private CreditConditionClient creditConditionClient;

  @Mock
  private ApplicationCreditConditionRepository applicationCreditConditionRepository;

  @Mock
  private CollateralClient collateralClient;

  @Mock
  private CollateralConfigProperties collateralConfigProperties;

  @InjectMocks
  private PostDebtInfoServiceImpl postDebtInfoService;

  private PostDebtInfoRequest postDebtInfoRequest;

  @BeforeEach
  public void setUp() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(javaTimeModule());
    mapper.configure(
        JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),
        true);
    mapper.setSerializationInclusion(Include.ALWAYS);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.registerModule(new StringTrimModuleConfig());
    postDebtInfoRequest = JsonUtil.convertString2Object(
        "{\"limitCredits\":[{\"id\":null,\"loanLimit\":\"CURRENT\",\"loanLimitValue\":\"Đã cấp hiện hữu\",\"loanProductCollateral\":null,\"otherLoanProductCollateral\":null,\"unsecureProduct\":null,\"otherUnsecureProduct\":null,\"total\":null,\"orderDisplay\":0},{\"id\":null,\"loanLimit\":\"PRE_APPROVE\",\"loanLimitValue\":\"Phê duyệt trước\",\"loanProductCollateral\":null,\"otherLoanProductCollateral\":null,\"unsecureProduct\":null,\"otherUnsecureProduct\":null,\"total\":null,\"orderDisplay\":1},{\"id\":null,\"loanLimit\":\"RECOMMEND\",\"loanLimitValue\":\"Đề xuất lần này\",\"loanProductCollateral\":null,\"otherLoanProductCollateral\":null,\"unsecureProduct\":null,\"otherUnsecureProduct\":216247246,\"total\":216247246,\"orderDisplay\":2}],\"credits\":[{\"id\":null,\"creditType\":\"V001\",\"creditTypeValue\":\"Cho vay\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":\"QD_RB_076\",\"approveResult\":\"Y\",\"approveResultValue\":\"Đồng ý\",\"loanAmount\":112123123,\"appCreditLoanId\":null,\"productCode\":\"RLNSTTBO1\",\"productName\":\"BO1 - Vay NH -TD luồng thường-OFF\",\"payback\":true,\"loanPurpose\":\"V001\",\"loanPurposeValue\":\"Cho vay mua sắm trang thiết bị, nội thất gia đình\",\"loanPurposeExplanation\":\"1\",\"loanPeriod\":1,\"kunnPeriod\":1,\"originalPeriod\":1,\"totalCapital\":13123123123,\"equityCapital\":13011000000,\"ltd\":1,\"creditForm\":\"V001\",\"creditFormValue\":\"Cho vay từng lần\",\"disburseFrequency\":\"V001\",\"disburseFrequencyValue\":\"1 lần\",\"debtPayMethod\":\"V001\",\"debtPayMethodValue\":\"Nợ gốc trả đều hàng tháng lãi hàng tháng tính trên dư nợ thực tế giảm dần\",\"disburseMethod\":\"V001\",\"disburseMethodValue\":\"Tiền mặt\",\"disburseMethodExplanation\":\"1\",\"principalPayPeriod\":null,\"principalPayUnit\":null,\"principalPayUnitValue\":null,\"interestPayPeriod\":null,\"interestPayUnit\":null,\"interestPayUnitValue\":null,\"appCreditOverdraftId\":null,\"appCreditCardId\":null,\"interestRateCode\":null,\"limitSustentivePeriod\":null,\"infoAdditional\":null,\"cardPolicyCode\":null,\"cardType\":null,\"cardTypeValue\":null,\"way4CardCode\":null,\"cardName\":null,\"secretQuestion\":null,\"email\":null,\"autoDeductRate\":null,\"autoDeductRateValue\":null,\"deductAccountNumber\":null,\"cardForm\":null,\"cardFormValue\":null,\"cardReceiveAddress\":null,\"cardReceiveAddressValue\":null,\"contractL\":null,\"acfNo\":null,\"accountNo\":null,\"issuingContract\":null,\"contractNumber\":null,\"createdDate\":null,\"hasSubCard\":null,\"subCreditCards\":[],\"address\":{\"cityCode\":null,\"cityValue\":null,\"districtCode\":null,\"districtValue\":null,\"wardCode\":null,\"wardValue\":null,\"addressLine\":null},\"uuid\":\"4kZILD4r0VTv\",\"isAllocation\":false,\"ldpCreditId\":null,\"way4BranchCode\":null,\"assets\":[],\"idDraft\":\"16979677073840\",\"orderDisplay\":0},{\"id\":null,\"creditType\":\"V002\",\"creditTypeValue\":\"Thấu chi\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":\"QD_RB_076\",\"approveResult\":\"Y\",\"approveResultValue\":\"Đồng ý\",\"loanAmount\":4124123,\"appCreditLoanId\":null,\"productCode\":null,\"productName\":\"Thau chi BO1-OFF - luong nhanh\",\"payback\":null,\"loanPurpose\":\"V001\",\"loanPurposeValue\":\"Cho vay mua sắm trang thiết bị, nội thất gia đình\",\"loanPurposeExplanation\":null,\"loanPeriod\":null,\"kunnPeriod\":null,\"originalPeriod\":null,\"totalCapital\":null,\"equityCapital\":null,\"ltd\":null,\"creditForm\":null,\"creditFormValue\":null,\"disburseFrequency\":null,\"disburseFrequencyValue\":null,\"debtPayMethod\":null,\"debtPayMethodValue\":null,\"disburseMethod\":null,\"disburseMethodValue\":null,\"disburseMethodExplanation\":null,\"principalPayPeriod\":null,\"principalPayUnit\":null,\"principalPayUnitValue\":null,\"interestPayPeriod\":null,\"interestPayUnit\":null,\"interestPayUnitValue\":null,\"appCreditOverdraftId\":null,\"appCreditCardId\":null,\"interestRateCode\":\"8701\",\"limitSustentivePeriod\":12312312,\"infoAdditional\":\"2\",\"cardPolicyCode\":null,\"cardType\":null,\"cardTypeValue\":null,\"way4CardCode\":null,\"cardName\":null,\"secretQuestion\":null,\"email\":null,\"autoDeductRate\":null,\"autoDeductRateValue\":null,\"deductAccountNumber\":null,\"cardForm\":null,\"cardFormValue\":null,\"cardReceiveAddress\":null,\"cardReceiveAddressValue\":null,\"contractL\":null,\"acfNo\":null,\"accountNo\":null,\"issuingContract\":null,\"contractNumber\":null,\"createdDate\":null,\"hasSubCard\":null,\"subCreditCards\":[],\"address\":{\"cityCode\":null,\"cityValue\":null,\"districtCode\":null,\"districtValue\":null,\"wardCode\":null,\"wardValue\":null,\"addressLine\":null},\"uuid\":\"WpQx7BNl7DQY\",\"isAllocation\":false,\"ldpCreditId\":null,\"way4BranchCode\":null,\"assets\":[],\"idDraft\":\"16979677073841\",\"orderDisplay\":1},{\"id\":null,\"creditType\":\"V003\",\"creditTypeValue\":\"Thẻ tín dụng\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":\"QD_RB_068\",\"approveResult\":\"Y\",\"approveResultValue\":\"Đồng ý\",\"loanAmount\":100000000,\"appCreditLoanId\":null,\"productCode\":null,\"productName\":\"Thẻ cầm cố sổ tiết kiệm\",\"payback\":null,\"loanPurpose\":null,\"loanPurposeValue\":null,\"loanPurposeExplanation\":null,\"loanPeriod\":null,\"kunnPeriod\":null,\"originalPeriod\":null,\"totalCapital\":null,\"equityCapital\":null,\"ltd\":null,\"creditForm\":null,\"creditFormValue\":null,\"disburseFrequency\":null,\"disburseFrequencyValue\":null,\"debtPayMethod\":null,\"debtPayMethodValue\":null,\"disburseMethod\":null,\"disburseMethodValue\":null,\"disburseMethodExplanation\":null,\"principalPayPeriod\":null,\"principalPayUnit\":null,\"principalPayUnitValue\":null,\"interestPayPeriod\":null,\"interestPayUnit\":null,\"interestPayUnitValue\":null,\"appCreditOverdraftId\":null,\"appCreditCardId\":null,\"interestRateCode\":null,\"limitSustentivePeriod\":1,\"infoAdditional\":null,\"cardPolicyCode\":\"SEC01\",\"cardType\":\"MCDS\",\"cardTypeValue\":\"010-MSB MASTERCARD DAILY SHOPPING\",\"way4CardCode\":\"MCDSMC\",\"cardName\":\"A B\",\"secretQuestion\":\"B\",\"email\":\"vanmanh6394@gmail.com\",\"autoDeductRate\":\"V001\",\"autoDeductRateValue\":\"Không trích nợ\",\"deductAccountNumber\":\"\",\"cardForm\":\"V001\",\"cardFormValue\":\"Vật lý\",\"cardReceiveAddress\":\"V003\",\"cardReceiveAddressValue\":\"Tại PGD đăng ký thẻ\",\"contractL\":null,\"acfNo\":null,\"accountNo\":null,\"issuingContract\":null,\"contractNumber\":null,\"createdDate\":null,\"hasSubCard\":null,\"subCreditCards\":[],\"address\":{\"cityCode\":null,\"cityValue\":null,\"districtCode\":null,\"districtValue\":null,\"wardCode\":null,\"wardValue\":null,\"addressLine\":null},\"uuid\":\"2AfCYi6UE03J\",\"isAllocation\":false,\"ldpCreditId\":null,\"way4BranchCode\":\"03100\",\"assets\":[],\"idDraft\":\"16979677073872\",\"orderDisplay\":2}],\"creditRatings\":[{\"uuid\":\"CTeaTNMiS6D7\",\"id\":2392,\"ratingSystem\":\"CSS\",\"ratingId\":\"51455\",\"ratingResult\":\"B15\",\"recommendation\":\"Đồng ý cấp tín dụng\",\"approvalComment\":null,\"creditRatingsDtls\":[{\"identityCard\":\"11111\",\"id\":11283,\"score\":0.0197609251124,\"rank\":\"B15\",\"executor\":\"rb_ca1\",\"role\":\"CA\",\"status\":\"700002\",\"createdAt\":\"22/10/2023 16:28:23\",\"scoringTime\":\"22/05/2023 03:51:07 PM\",\"statusDescription\":\"Hồ sơ kết thúc quy trình\",\"recommendation\":\"Đồng ý cấp tín dụng\",\"typeOfModel\":\"sco\"},{\"identityCard\":\"11111\",\"id\":11282,\"score\":0.0197609251124,\"rank\":\"B15\",\"executor\":\"rb_rm1\",\"role\":\"RM\",\"status\":\"700002\",\"createdAt\":\"22/10/2023 16:28:23\",\"scoringTime\":\"22/05/2023 02:04:52 PM\",\"statusDescription\":\"Hồ sơ kết thúc quy trình\",\"recommendation\":\"Đồng ý cấp tín dụng\",\"typeOfModel\":\"sco\"},{\"identityCard\":\"11111\",\"id\":11284,\"score\":1,\"rank\":\"\",\"executor\":\"rb_rm1\",\"role\":\"RM\",\"status\":\"700002\",\"createdAt\":\"22/10/2023 16:28:23\",\"scoringTime\":\"22/05/2023 01:43:46 PM\",\"statusDescription\":\"Hồ sơ kết thúc quy trình\",\"recommendation\":\"\",\"typeOfModel\":\"pre\"},{\"identityCard\":\"11111\",\"id\":11285,\"score\":1,\"rank\":\"\",\"executor\":\"rb_ca1\",\"role\":\"CA\",\"status\":\"700002\",\"createdAt\":\"22/10/2023 16:28:23\",\"scoringTime\":\"22/05/2023 03:39:13 PM\",\"statusDescription\":\"Hồ sơ kết thúc quy trình\",\"recommendation\":\"\",\"typeOfModel\":\"pre\"}],\"orderDisplay\":0}],\"repayment\":{\"totalIncome\":0,\"totalRepay\":5,\"dti\":null,\"dsr\":null,\"mue\":null,\"evaluate\":\"y\"},\"specialRiskContents\":[{\"uuid\":\"L3M7JIGdIVKK\",\"id\":null,\"checkNewItem\":false,\"contentType\":\"SPECIAL_RISK\",\"criteriaGroup\":\"V001\",\"criteriaGroupValue\":\"Đơn lẻ\",\"detail\":[\"V101\"],\"detailValue\":[\"Vượt quy định chính sách hiện hành\"],\"regulation\":\"1\",\"managementMeasures\":\"1\",\"authorization\":null,\"authorizationValue\":\"\",\"optionDropdown\":[{\"label\":\"Đơn lẻ\",\"value\":\"V001\",\"props\":null,\"disabled\":false},{\"label\":\"Rủi ro đặc biệt\",\"value\":\"V002\",\"props\":null},{\"label\":\"Rủi ro cao\",\"value\":\"V003\",\"props\":null},{\"label\":\"Đặc thù trọng yếu\",\"value\":\"V004\",\"props\":null},{\"label\":\"Đặc thù không trọng yếu\",\"value\":\"V005\",\"props\":null},{\"label\":\"Khác biệt hướng dẫn thẩm định\",\"value\":\"V006\",\"props\":null},{\"label\":\"Khác biệt chỉ thị tiền mặt\",\"value\":\"V007\",\"props\":null},{\"label\":\"Đặc thù định hướng\",\"value\":\"V008\",\"props\":null}],\"orderDisplay\":0}],\"additionalContents\":[{\"uuid\":\"5C1Yqef288QD\",\"id\":null,\"checkNewItem\":false,\"contentType\":\"OTHER_ADDITIONAL\",\"criteriaGroup\":\"V001\",\"criteriaGroupValue\":\"Khác\",\"detail\":[\"1\"],\"detailValue\":[],\"regulation\":\"1\",\"managementMeasures\":\"1\",\"authorization\":\"TQC01\",\"authorizationValue\":\"CVC PDTD\",\"optionDropdown\":[{\"label\":\"Đơn lẻ\",\"value\":\"V001\",\"props\":null},{\"label\":\"Rủi ro đặc biệt\",\"value\":\"V002\",\"props\":null},{\"label\":\"Rủi ro cao\",\"value\":\"V003\",\"props\":null},{\"label\":\"Đặc thù trọng yếu\",\"value\":\"V004\",\"props\":null},{\"label\":\"Đặc thù không trọng yếu\",\"value\":\"V005\",\"props\":null},{\"label\":\"Khác biệt hướng dẫn thẩm định\",\"value\":\"V006\",\"props\":null},{\"label\":\"Khác biệt chỉ thị tiền mặt\",\"value\":\"V007\",\"props\":null},{\"label\":\"Đặc thù định hướng\",\"value\":\"V008\",\"props\":null}],\"orderDisplay\":0}],\"otherReviews\":[{\"uuid\":\"3ArwFRB0HRrv\",\"orderDisplay\":0,\"approvalPosition\":\"PD_RB_RM\",\"contentDetail\":\"1\"}],\"creditConditions\":[{\"uuid\":\"dXXJYhiQMmNS\",\"masterId\":4,\"id\":null,\"creditConditionId\":null,\"checkNewItem\":false,\"group\":\"INC\",\"groupValue\":\"Điều kiện nguồn thu\",\"code\":\"R4\",\"detail\":\"Khách hành cam kết chuyển dòng tiền về TKTT của KH mở tại MSB với: Doanh số ghi có tối thiểu bằng [….] % tổng số tiền cấp tín dụng trong toàn bộ thời gian cấp tín dụng [Vay + Thấu chi].  ${a}\",\"timeOfControl\":\"V003\",\"timeOfControlValue\":\"Sau giải ngân\",\"applicableSubject\":\"V001\",\"applicableSubjectValue\":\"Khách hàng\",\"controlUnit\":\"V001\",\"controlUnitValue\":\"ĐVKD\",\"timeControlDisburse\":\"\",\"creditConditionParams\":[{\"parameter\":\"a\",\"dataType\":\"Text\",\"length\":100,\"content\":\"\",\"value\":null,\"description\":null,\"position\":[{\"start\":188,\"end\":192}]}],\"params\":[{\"parameter\":\"a\",\"dataType\":\"Text\",\"length\":100,\"content\":\"\",\"value\":null,\"description\":null,\"position\":[{\"start\":188,\"end\":192}]}],\"orderDisplay\":0},{\"uuid\":\"dXXJYhiQMmNS\",\"masterId\":4,\"id\":1,\"creditConditionId\":2,\"checkNewItem\":false,\"group\":\"INC\",\"groupValue\":\"Điều kiện nguồn thu\",\"code\":\"R4\",\"detail\":\"Khách hành cam kết chuyển dòng tiền về TKTT của KH mở tại MSB với: Doanh số ghi có tối thiểu bằng [….] % tổng số tiền cấp tín dụng trong toàn bộ thời gian cấp tín dụng [Vay + Thấu chi].  ${a}\",\"timeOfControl\":\"V003\",\"timeOfControlValue\":\"Sau giải ngân\",\"applicableSubject\":\"V001\",\"applicableSubjectValue\":\"Khách hàng\",\"controlUnit\":\"V001\",\"controlUnitValue\":\"ĐVKD\",\"timeControlDisburse\":\"\",\"creditConditionParams\":[{\"parameter\":\"a\",\"dataType\":\"Text\",\"length\":100,\"content\":\"\",\"value\":null,\"description\":null,\"position\":[{\"start\":188,\"end\":192}]}],\"params\":[{\"parameter\":\"a\",\"dataType\":\"Text\",\"length\":100,\"content\":\"\",\"value\":null,\"description\":null,\"position\":[{\"start\":188,\"end\":192}]}],\"orderDisplay\":0}],\"phoneExpertise\":{\"id\":null,\"ext\":null,\"phoneExpertiseDtls\":[]},\"assetAllocations\":[],\"proposalApprovalPosition\":null,\"loanApprovalPosition\":null,\"loanApprovalPositionValue\":null,\"effectivePeriod\":null,\"applicationAuthorityLevel\":null,\"priorityAuthority\":null,\"effectivePeriodUnit\":\"Tháng\",\"type\":\"POST_DEBT_INFO\",\"bpmId\":\"151-00007853\"}",
        PostDebtInfoRequest.class, mapper);
  }

  @BeforeAll
  static void init() {
    User applicationUser = new User("test", "test",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

    Authentication auth = new UsernamePasswordAuthenticationToken(applicationUser, null);

    SecurityContextHolder.getContext().setAuthentication(auth);
  }
  @Test
  void test_getType_should_be_ok() {
    assertEquals(POST_DEBT_INFO, postDebtInfoService.getType());
  }

  @ParameterizedTest
  @CsvSource({"false", "true"})
  void test_referenceLimitCreditEntities_should_be_ok(boolean limitCreditEmpty) {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    if (limitCreditEmpty) {
      assertDoesNotThrow(
          () -> postDebtInfoService.referenceLimitCreditEntities(applicationEntity, null));
    } else {
      Set<ApplicationLimitCreditDTO> limitCredits = postDebtInfoRequest.getLimitCredits();
      assertDoesNotThrow(
          () -> postDebtInfoService.referenceLimitCreditEntities(applicationEntity, limitCredits));
    }
  }

  @Test
  void test_referenceRepaymentEntity_should_be_ok() {
    assertDoesNotThrow(() -> postDebtInfoService.referenceRepaymentEntity(new ApplicationEntity(),
        postDebtInfoRequest.getRepayment()));
  }

  @ParameterizedTest
  @CsvSource({"false", "true"})
  void test_referenceAppraisalContentsEntities_should_be_ok(boolean appraisalEmpty) {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    if (appraisalEmpty) {
      assertDoesNotThrow(
          () -> postDebtInfoService.referenceAppraisalContentsEntities(applicationEntity, null));
    } else {
      Set<ApplicationAppraisalContentDTO> appraisalContents = new HashSet<>();
      appraisalContents.addAll(postDebtInfoRequest.getAdditionalContents());
      appraisalContents.addAll(postDebtInfoRequest.getSpecialRiskContents());
      assertDoesNotThrow(
          () -> postDebtInfoService.referenceAppraisalContentsEntities(applicationEntity,
              appraisalContents));
    }
  }

  @ParameterizedTest
  @CsvSource({"false","true"})
  void test_referenceExtraDataEntities_should_be_ok(boolean otherReviewEmpty) throws JsonProcessingException {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    if (otherReviewEmpty) {
      assertDoesNotThrow(
          () -> postDebtInfoService.referenceExtraDataEntities(applicationEntity, null,
              new HashMap<>(), DEBT_INFO, OTHER_REVIEW));
    } else {
      Set<OtherReviewDTO> otherReviews = postDebtInfoRequest.getOtherReviews();

      when(objectMapper.writeValueAsString(otherReviews)).thenReturn(
          "[{\"uuid\":\"3ArwFRB0HRrv\",\"orderDisplay\":0,\"approvalPosition\":\"PD_RB_RM\",\"contentDetail\":\"1\"}]");

      assertDoesNotThrow(
          () -> postDebtInfoService.referenceExtraDataEntities(applicationEntity, otherReviews,
              new HashMap<>(), DEBT_INFO, OTHER_REVIEW));
    }

  }

  @ParameterizedTest
  @CsvSource({"'',false","1432,true","1432,false"})
  void test_referencePhoneExpertises_should_be_ok(String ext, boolean dtlEmpty) {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    ApplicationPhoneExpertiseDTO phoneExpertiseDTO = new ApplicationPhoneExpertiseDTO();
    phoneExpertiseDTO.setExt(ext);

    if (StringUtils.isNotBlank(ext)) {
      if (dtlEmpty) {
        assertDoesNotThrow(() -> postDebtInfoService.referencePhoneExpertises(applicationEntity, phoneExpertiseDTO));
      } else {
        Set<ApplicationPhoneExpertiseDtlDTO> phoneExpertiseDtlSet = new HashSet<>();
        ApplicationPhoneExpertiseDtlDTO dtl = new ApplicationPhoneExpertiseDtlDTO();
        dtl.setNote("");
        dtl.setPhoneNumber("");
        dtl.setCalledAt("");
        dtl.setPersonAnswer("");
        dtl.setNote("");
        phoneExpertiseDtlSet.add(dtl);
        phoneExpertiseDTO.setPhoneExpertiseDtls(phoneExpertiseDtlSet);
        assertDoesNotThrow(() -> postDebtInfoService.referencePhoneExpertises(applicationEntity, phoneExpertiseDTO));
      }
    } else {
      assertDoesNotThrow(() -> postDebtInfoService.referencePhoneExpertises(applicationEntity, phoneExpertiseDTO));
    }
  }

  @ParameterizedTest
  @CsvSource({"false","true"})
  void test_referenceCreditEntities_should_be_ok(boolean hasCredit) {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    if (hasCredit) {
      assertDoesNotThrow(() -> postDebtInfoService.referenceCreditEntities(applicationEntity, postDebtInfoRequest.getCredits()));
    } else {
      assertDoesNotThrow(() -> postDebtInfoService.referenceCreditEntities(applicationEntity, null));
    }
  }

  @Test
  void test_referenceCreditConditionsEntities_should_be_ok() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setId(1L);

    CustomerEntity customerEntity = new CustomerEntity();
    IndividualCustomerEntity individualCustomerEntity = new IndividualCustomerEntity();
    individualCustomerEntity.setFirstName("");
    individualCustomerEntity.setLastName("");
    customerEntity.setIndividualCustomer(individualCustomerEntity);
    customerEntity.setCif("");
    customerEntity.setBpmCif("");
    applicationEntity.setCustomer(customerEntity);

    ApplicationCreditConditionsEntity conditionsEntity = new ApplicationCreditConditionsEntity();
    conditionsEntity.setId(1L);
    conditionsEntity.setCreditConditionId(2L);
    applicationEntity.getCreditConditions().add(conditionsEntity);

    conditionsEntity = new ApplicationCreditConditionsEntity();
    conditionsEntity.setId(2L);
    conditionsEntity.setCreditConditionId(3L);
    applicationEntity.getCreditConditions().add(conditionsEntity);

    when(applicationCreditConditionRepository
        .findByApplicationIdOrderByCreditConditionId(anyLong()))
        .thenReturn(new ArrayList<>(applicationEntity.getCreditConditions()));

//    verify(applicationCreditConditionRepository, times(1)).deleteAllByIdInBatch(Collections.singletonList(3L));
//
//    verify(creditConditionClient, times(1)).deleteCreditConditionByListId(any(
//        DeleteCreditConditionRequest.class));

    CreditConditionClientResponse<List<CreditConditionResponse>> createResponse = new CreditConditionClientResponse<>();
    List<CreditConditionResponse> valueList = new ArrayList<>();
    CreditConditionResponse value = new CreditConditionResponse();
    value.setId(100L);
    valueList.add(value);
    createResponse.setValue(valueList);
    when(creditConditionClient.createCreditConditions(any(CreateCreditConditionRequest.class)))
        .thenReturn(createResponse);

//    verify(applicationCreditConditionRepository, times(1)).save(any(ApplicationCreditConditionsEntity.class));

    assertDoesNotThrow(() -> postDebtInfoService.referenceCreditConditionsEntities(applicationEntity, postDebtInfoRequest.getCreditConditions()));
  }

  @ParameterizedTest
  @CsvSource({"false","true"})
  void test_referenceCreditRatingsEntities_should_be_ok(boolean hasCreditRatings) {
    ApplicationEntity applicationEntity = new ApplicationEntity();

    if (hasCreditRatings) {
      assertDoesNotThrow(() -> postDebtInfoService.referenceCreditRatingsEntities(applicationEntity, postDebtInfoRequest.getCreditRatings()));
    } else {
      assertDoesNotThrow(() -> postDebtInfoService.referenceCreditRatingsEntities(applicationEntity, null));
    }
  }

  @Test
  void test_saveData_should_be_ok() throws JsonProcessingException {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setId(1L);

    CustomerEntity customerEntity = new CustomerEntity();
    IndividualCustomerEntity individualCustomerEntity = new IndividualCustomerEntity();
    individualCustomerEntity.setFirstName("");
    individualCustomerEntity.setLastName("");
    customerEntity.setIndividualCustomer(individualCustomerEntity);
    customerEntity.setCif("");
    customerEntity.setBpmCif("");
    applicationEntity.setCustomer(customerEntity);

    ApplicationCreditConditionsEntity conditionsEntity = new ApplicationCreditConditionsEntity();
    conditionsEntity.setId(1L);
    conditionsEntity.setCreditConditionId(2L);
    applicationEntity.getCreditConditions().add(conditionsEntity);

    conditionsEntity = new ApplicationCreditConditionsEntity();
    conditionsEntity.setId(2L);
    conditionsEntity.setCreditConditionId(3L);
    applicationEntity.getCreditConditions().add(conditionsEntity);

    test_referenceLimitCreditEntities_should_be_ok(false);
    test_referenceRepaymentEntity_should_be_ok();
    test_referenceAppraisalContentsEntities_should_be_ok(false);
    test_referenceExtraDataEntities_should_be_ok(false);
    test_referencePhoneExpertises_should_be_ok("4123123", false);
    test_referenceCreditEntities_should_be_ok(true);
    test_referenceCreditConditionsEntities_should_be_ok();
    test_referenceCreditRatingsEntities_should_be_ok(true);
    postDebtInfoRequest.setAssetAllocations(null);
    assertDoesNotThrow(() -> postDebtInfoService.saveData(applicationEntity, postDebtInfoRequest));
  }

  public JavaTimeModule javaTimeModule() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS_FORMAT);
    LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
    LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter);
    javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
    javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer);

    formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT);
    LocalDateDeserializer dateDeserializer = new LocalDateDeserializer(formatter);
    LocalDateSerializer dateSerializer = new LocalDateSerializer(formatter);
    javaTimeModule.addDeserializer(LocalDate.class, dateDeserializer);
    javaTimeModule.addSerializer(LocalDate.class, dateSerializer);

    return javaTimeModule;
  }

  @Test
  void test_referenceAssetAllocationInfo_should_be_ok() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setBpmId("151-0005757");
    applicationEntity.setId(1L);

    AssetAllocationConfigProperties assetAllocationConfigProperties = new AssetAllocationConfigProperties();
    assetAllocationConfigProperties.setSaveAssetAllocation("/internal/asset-allocation/save?applicationId=%s&businessType=%s");
    assetAllocationConfigProperties.setViewAssetAllocation("/internal/asset-allocation/view?applicationId=%s&businessType=%s");
    assetAllocationConfigProperties.setSaveAssets("/internal/asset-allocation/saveAssets?applicationId=%s");
    assetAllocationConfigProperties.setRoleEdit("PD_RB_CA1,PD_RB_CA2,PD_RB_CA3,PD_RB_CC_CONTROL,PD_RB_CC1,PD_RB_CC2,PD_RB_CC3,PD_RB_RM,PD_RB_BM");

    Set<ApplicationCreditEntity> creditEntities = new HashSet<>();

    ApplicationCreditEntity dtl = new ApplicationCreditEntity();
    List<Long> assetIds = Mockito.mock(List.class);
    dtl.setAssets(assetIds);
    dtl.setIsAllocation(true);
    creditEntities.add(dtl);
    applicationEntity.setCredits(creditEntities);

    ApplicationAssetAllocationDTO dto = new ApplicationAssetAllocationDTO();
    dto.setAssetId(1L);
    List<CreditAllocationDTO> allocationDTOList = new ArrayList<>();
    CreditAllocationDTO creditAllocationDTO = new CreditAllocationDTO();
    creditAllocationDTO.setCreditId(1L);
    creditAllocationDTO.setCreditType("V001");
    creditAllocationDTO.setPercentValue(17F);
    creditAllocationDTO.setCreditTypeValue("VL");
    allocationDTOList.add(creditAllocationDTO);
    dto.setCreditAllocations(allocationDTOList);

    Set<ApplicationAssetAllocationDTO> allocationDTOS = new HashSet<>();
    allocationDTOS.add(dto);
    postDebtInfoRequest.setAssetAllocations(allocationDTOS);

    when(collateralConfigProperties.getAllocationConfig()).thenReturn(assetAllocationConfigProperties);

    CreditAssetAllocationResponse response = new CreditAssetAllocationResponse();
    List<CreditAssetMappingResponse> creditAssetMappingResponses = Mockito.mock(List.class);
    List<AssetAllocationResponse> assetAllocationResponses = Mockito.mock(List.class);
    response.setAssets(creditAssetMappingResponses);
    response.setAssetAllocations(assetAllocationResponses);

    when(collateralClient.updateAssetAllocationInfo(anyString(), any())).thenReturn(response);
    assertDoesNotThrow(() -> postDebtInfoService.referenceAssetAllocationInfo(applicationEntity, allocationDTOS));
  }

  @Test
  void test_addAssetForCredit_should_be_ok() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setBpmId("151-0005757");
    applicationEntity.setId(1L);

    Set<ApplicationCreditEntity> creditEntities = new HashSet<>();

    ApplicationCreditEntity dtl = new ApplicationCreditEntity();
    List<Long> assetIds = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L));
    dtl.setAssets(assetIds);
    dtl.setIsAllocation(true);
    dtl.setIdDraft("IdDraft");
    creditEntities.add(dtl);
    applicationEntity.setCredits(creditEntities);

    ApplicationAssetAllocationDTO dto = new ApplicationAssetAllocationDTO();
    dto.setAssetId(1L);
    List<CreditAllocationDTO> allocationDTOList = new ArrayList<>();
    CreditAllocationDTO creditAllocationDTO = new CreditAllocationDTO();
    creditAllocationDTO.setCreditId(1L);
    creditAllocationDTO.setCreditType("V001");
    creditAllocationDTO.setPercentValue(17F);
    creditAllocationDTO.setCreditTypeValue("VL");
    allocationDTOList.add(creditAllocationDTO);
    dto.setCreditAllocations(allocationDTOList);

    Set<ApplicationCreditDTO> allocationDTOS = new HashSet<>();
    ApplicationCreditDTO applicationCreditDTO = new ApplicationCreditLoanDTO();
    applicationCreditDTO.setIsAllocation(true);
    applicationCreditDTO.setAssets(assetIds);
    applicationCreditDTO.setIdDraft("IdDraft");
    allocationDTOS.add(applicationCreditDTO);

    List<CreditAssetRequest> dataAsset = new ArrayList<>();
    CreditAssetRequest response = new CreditAssetRequest();
    response.setCredit(1L);
    response.setAssets(assetIds);
    dataAsset.add(response);

    when(collateralClient.mappingCreditAssetInfo(anyString(), any())).thenReturn(dataAsset);
    assertDoesNotThrow(() -> postDebtInfoService.addAssetForCredit(applicationEntity, allocationDTOS));
  }

  @Test
  void testGetDebtInfoDTO() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setBpmId("151-0005757");
    applicationEntity.setId(1L);
    applicationEntity.setAssignee("test1");
    assertDoesNotThrow(() -> postDebtInfoService.getDebtInfoDTO(applicationEntity));
  }

  @ParameterizedTest
  @CsvSource({"author","invalid", "throw"})
  void testExecuteFail(String type) {
    PostDebtInfoRequest infoRequest = JsonUtil.convertString2Object("[{\"type\":\"POST_DEBT_INFO\",\"type\":\"POST_DEBT_INFO\",\"bpmId\":\"151-00011404\",\"assetAllocations\":[],\"limitCredits\":[{\"id\":null,\"loanLimit\":\"CURRENT\",\"loanLimitValue\":\"Đã cấp hiện hữu\",\"loanProductCollateral\":null,\"otherLoanProductCollateral\":null,\"unsecureProduct\":null,\"otherUnsecureProduct\":null,\"total\":null,\"orderDisplay\":0},{\"id\":null,\"loanLimit\":\"PRE_APPROVE\",\"loanLimitValue\":\"Phê duyệt trước\",\"loanProductCollateral\":null,\"otherLoanProductCollateral\":null,\"unsecureProduct\":null,\"otherUnsecureProduct\":null,\"total\":null,\"orderDisplay\":1},{\"id\":null,\"loanLimit\":\"RECOMMEND\",\"loanLimitValue\":\"Đề xuất lần này\",\"loanProductCollateral\":null,\"otherLoanProductCollateral\":null,\"unsecureProduct\":null,\"otherUnsecureProduct\":1200000000,\"total\":1200000000,\"orderDisplay\":2}],\"creditRatings\":[{\"id\":3351,\"ratingSystem\":\"CSS\",\"ratingId\":\"54426\",\"ratingResult\":\"B21\",\"orderDisplay\":0,\"approvalComment\":null,\"recommendation\":\"Đồng ý cấp tín dụng\",\"creditRatingsDtls\":[{\"id\":15495,\"identityCard\":\"163386777563\",\"score\":1.0,\"rank\":\"Pass\",\"executor\":\"rb_rm1\",\"role\":\"RM\",\"status\":\"400100\",\"createdAt\":\"17/04/2024 14:04:17\",\"orderDisplay\":null,\"scoringId\":null,\"identityNo\":null,\"typeOfModel\":\"pre\",\"scoringSource\":null,\"recommendation\":\"Thỏa mãn điều kiện sàng lọc\",\"scoringTime\":\"18/10/2023 10:05:15 AM\",\"approvalComment\":null,\"statusDescription\":\"Chấm điểm xếp hạng khách hàng\",\"scoringDateTime\":\"18/10/2023 10:05:15\"},{\"id\":15496,\"identityCard\":\"163386777563\",\"score\":0.0866592515167,\"rank\":\"B21\",\"executor\":\"rb_rm1\",\"role\":\"RM\",\"status\":\"400100\",\"createdAt\":\"17/04/2024 14:04:17\",\"orderDisplay\":null,\"scoringId\":null,\"identityNo\":null,\"typeOfModel\":\"sco\",\"scoringSource\":null,\"recommendation\":\"Đồng ý cấp tín dụng\",\"scoringTime\":\"18/10/2023 10:08:14 AM\",\"approvalComment\":null,\"statusDescription\":\"Chấm điểm xếp hạng khách hàng\",\"scoringDateTime\":\"18/10/2023 10:08:14\"}]}],\"credits\":[{\"id\":null,\"ldpCreditId\":null,\"assets\":[],\"creditType\":\"V001\",\"creditTypeValue\":\"Cho vay\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":\"QD_RB_076\",\"approveResult\":\"Y\",\"approveResultValue\":\"Đồng ý\",\"loanAmount\":1200000000,\"orderDisplay\":0,\"isAllocation\":false,\"idDraft\":\"17133374760480\",\"appCreditLoanId\":null,\"productCode\":\"RLNMTTBO1\",\"productName\":\"BO1 - Vay TH - TD luồng thường-OFF\",\"payback\":false,\"loanPurpose\":\"V001\",\"loanPurposeValue\":\"Cho vay mua sắm trang thiết bị, nội thất gia đình\",\"loanPurposeExplanation\":null,\"loanPeriod\":12,\"kunnPeriod\":null,\"originalPeriod\":null,\"totalCapital\":2000000000,\"equityCapital\":800000000,\"ltd\":60,\"creditForm\":\"V001\",\"creditFormValue\":\"Cho vay từng lần\",\"disburseFrequency\":\"V001\",\"disburseFrequencyValue\":\"1 lần\",\"debtPayMethod\":\"V001\",\"debtPayMethodValue\":\"Nợ gốc trả đều hàng tháng lãi hàng tháng tính trên dư nợ thực tế giảm dần\",\"disburseMethod\":\"V001\",\"disburseMethodValue\":\"Tiền mặt\",\"disburseMethodExplanation\":null,\"principalPayPeriod\":null,\"principalPayUnit\":null,\"principalPayUnitValue\":null,\"interestPayPeriod\":null,\"interestPayUnit\":null,\"interestPayUnitValue\":null,\"acfNo\":null,\"accountNo\":null,\"status\":null}],\"repayment\":{\"totalIncome\":11111,\"totalRepay\":12,\"dti\":0.11,\"dsr\":null,\"mue\":null,\"evaluate\":\"12000\"},\"specialRiskContents\":[],\"additionalContents\":[],\"creditConditions\":[],\"otherReviews\":[{\"approvalPosition\":\"PD_RB_RM\",\"contentDetail\":\"\",\"orderDisplay\":0}],\"phoneExpertise\":{\"ext\":null,\"phoneExpertiseDtls\":[]},\"effectivePeriodUnit\":\"Tháng\",\"proposalApprovalPosition\":\"TQC23\",\"loanApprovalPosition\":\"TQC08\",\"loanApprovalPositionValue\":\"GĐ QLPD\",\"applicationAuthorityLevel\":8,\"priorityAuthority\":\"CTQ002\"}]"
            , PostDebtInfoRequest.class, objectMapper);
    ApplicationEntity application = new ApplicationEntity();
    application.setBpmId("bpm");

    if("author".equals(type)) {
      when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(application));
      assertThrows(ApprovalException.class, () -> postDebtInfoService.execute(infoRequest, "151-00011404"));
    }

    if("invalid".equals(type)) {
      application.setAssignee("test");
      application.setStatus("0099");
      when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(application));
      assertThrows(ApprovalException.class, () -> postDebtInfoService.execute(infoRequest, "151-00011404"));
    }

    if("throw".equals(type)) {
      when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.empty());
      assertThrows(ApprovalException.class, () -> postDebtInfoService.execute(infoRequest, "151-00011404"));
    }
  }
}
