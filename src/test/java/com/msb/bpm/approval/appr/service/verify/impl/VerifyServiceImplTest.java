package com.msb.bpm.approval.appr.service.verify.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TabCode.ASSET_INFO;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_FORMAT;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_CA1;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msb.bpm.approval.appr.client.asset.AssetClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.client.loanproposal.LoanProposalClient;
import com.msb.bpm.approval.appr.client.otp.OtpClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.config.cic.CICProperties;
import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.AssetCommonInfoDTO;
import com.msb.bpm.approval.appr.model.dto.GetApplicationDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationDraftEntity;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.loanproposal.ComparatorApplicationRequest;
import com.msb.bpm.approval.appr.model.response.collateral.CollateralClientResponse;
import com.msb.bpm.approval.appr.model.response.loanproposal.ComparatorApplicationResponse;
import com.msb.bpm.approval.appr.repository.AmlOprRepository;
import com.msb.bpm.approval.appr.repository.ApplicationCreditConditionRepository;
import com.msb.bpm.approval.appr.repository.ApplicationCreditRepository;
import com.msb.bpm.approval.appr.repository.ApplicationDraftRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.checklist.ChecklistService;
import com.msb.bpm.approval.appr.service.intergated.DecisionRuleIntegrateService;
import com.msb.bpm.approval.appr.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Validator;
import junit.framework.TestCase;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Assertions;
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

@ExtendWith(MockitoExtension.class)
class VerifyServiceImplTest extends TestCase {
  @Mock
  private DecisionRuleIntegrateService decisionRuleIntegrateService;
  @Mock
  private OtpClient otpClient;
  @Mock
  private UserManagerClient userManagerClient;
  @Mock
  private ChecklistService checklistService;
  @Mock
  private Validator validator;
  @Mock
  private MessageSource messageSource;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private CICProperties cicProperties;
  @Mock
  private LoanProposalClient loanProposalClient;

  @Mock
  private AssetClient assetClient;

  @Mock
  private AbstractBaseService abstractBaseService;

  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  ApplicationDraftRepository applicationDraftRepository;

  @InjectMocks
  private VerifyServiceImpl verifyServiceImpl;

  private ComparatorApplicationRequest comparatorApplicationRequest;

  private GetApplicationDTO getApplicationDTO;
  @Mock
  private ApplicationCreditConditionRepository applicationCreditConditionRepository;

  @Mock
  private AmlOprRepository amlOprRepository;

  @Mock
  ApplicationCreditRepository applicationCreditRepository;

  @Mock
  CollateralClient collateralClient;

  private String pathSourceFile = "src/test/resources/feedback_application/";
  private ApplicationDraftEntity applicationDraftEntity;
  Set<ApplicationDraftEntity> applicationDraftEntities;

  ApplicationDraftEntity draftAssetEntities;
  @BeforeEach
  public void setUp() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(javaTimeModule());
    comparatorApplicationRequest = JsonUtil.convertString2Object(
        "{\"data\":{\"insurance\":false,\"enterpriseRelations\":[{\"id\":14606,\"refCustomerId\":null,\"refCusId\":\"6b995326\",\"bpmCif\":null,\"cif\":null,\"customerType\":\"EB\",\"relationship\":null,\"relationshipValue\":null,\"orderDisplay\":null,\"mainIdentity\":null,\"addresses\":null,\"mainAddress\":null,\"businessType\":\"V001\",\"businessRegistrationNumber\":\"40\",\"companyName\":\"Lê Đức Thọ Mỹ Đình aaaaa\",\"firstRegistrationAt\":null,\"deputy\":null,\"identityDocuments\":null}],\"loanApprovalPositionValue\":null,\"applicationCredits\":[{\"id\":10255,\"ldpCreditId\":\"f2cd3825f\",\"creditType\":\"V002\",\"creditTypeValue\":\"Thấu chi\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":null,\"approveResult\":null,\"approveResultValue\":null,\"loanAmount\":1000000,\"orderDisplay\":null,\"isAllocation\":null,\"appCreditOverdraftId\":2715,\"interestRateCode\":null,\"productName\":null,\"loanPurpose\":\"V005\",\"loanPurposeValue\":\"Cho vay đầu tư TSCĐ là máy móc, thiết bị, phương tiện vận tải\",\"limitSustentivePeriod\":12,\"debtPayMethod\":null,\"debtPayMethodValue\":null,\"infoAdditional\":null,\"acfNo\":null,\"accountNo\":null,\"status\":null},{\"id\":10256,\"ldpCreditId\":\"f2cd385f\",\"creditType\":\"V001\",\"creditTypeValue\":\"Cho vay\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":null,\"approveResult\":null,\"approveResultValue\":null,\"loanAmount\":1000000,\"orderDisplay\":null,\"isAllocation\":null,\"appCreditLoanId\":3788,\"productCode\":null,\"productName\":null,\"payback\":false,\"loanPurpose\":\"V001\",\"loanPurposeValue\":\"Cho vay mua sắm trang thiết bị, nội thất gia đình\",\"loanPurposeExplanation\":null,\"loanPeriod\":12,\"kunnPeriod\":null,\"originalPeriod\":null,\"totalCapital\":600000000,\"equityCapital\":null,\"ltd\":null,\"creditForm\":\"V001\",\"creditFormValue\":\"Cho vay từng lần\",\"disburseFrequency\":null,\"disburseFrequencyValue\":null,\"debtPayMethod\":null,\"debtPayMethodValue\":null,\"disburseMethod\":null,\"disburseMethodValue\":null,\"disburseMethodExplanation\":null,\"principalPayPeriod\":null,\"principalPayUnit\":null,\"principalPayUnitValue\":null,\"interestPayPeriod\":null,\"interestPayUnit\":null,\"interestPayUnitValue\":null,\"acfNo\":null,\"accountNo\":null,\"status\":null},{\"id\":10254,\"ldpCreditId\":\"4525f6e2\",\"creditType\":\"V003\",\"creditTypeValue\":\"Thẻ tín dụng\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":null,\"approveResult\":null,\"approveResultValue\":null,\"loanAmount\":9600000,\"orderDisplay\":null,\"isAllocation\":null,\"appCreditCardId\":3667,\"cardPolicyCode\":null,\"productName\":null,\"cardType\":\"MCDSS\",\"cardTypeValue\":\"010-MSB MASTERCARD DAILY SHOPPING STAFF\",\"way4CardCode\":null,\"cardName\":\"NGUYEN VAN MANH    \",\"secretQuestion\":\"Who is Batman?\",\"limitSustentivePeriod\":null,\"email\":\"vanmanh6394@gmail.com\",\"autoDeductRate\":\"V002\",\"autoDeductRateValue\":\"Tối thiểu\",\"deductAccountNumber\":\"172638761278647\",\"cardForm\":\"V001\",\"cardFormValue\":\"Vật lý\",\"cardReceiveAddress\":\"V001\",\"cardReceiveAddressValue\":\"Địa chỉ cư trú hiện tại\",\"address\":{\"cityCode\":\"42\",\"cityValue\":\"Tỉnh Hà Giang\",\"districtCode\":\"733\",\"districtValue\":\"Huyện Yên Minh\",\"wardCode\":\"11288\",\"wardValue\":\"Xã Thắng Mố\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình 234567890-\"},\"hasSubCard\":false,\"subCreditCards\":[],\"contractL\":null,\"issuingContract\":null,\"contractNumber\":null,\"createdDate\":null}],\"applicationAuthorityLevel\":null,\"specialRiskContents\":null,\"phoneExpertise\":null,\"proposalApprovalPosition\":null,\"creditRatings\":[],\"priorityAuthority\":null,\"assetInfo\":[],\"repayment\":{\"totalIncome\":null,\"totalRepay\":null,\"dti\":null,\"dsr\":null,\"mue\":null,\"evaluate\":null},\"otherReviews\":[],\"effectivePeriod\":null,\"applicationIncomes\":[{\"id\":6602,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"bds02\",\"incomeItems\":[{\"id\":1190,\"incomeType\":\"V006\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"Nguyễn thị nhã\",\"incomeTypeValue\":\"Kinh doanh bất động sản (giao dịch gom xây)\",\"recognizedIncome\":360000,\"orderDisplay\":null,\"businessExperience\":null,\"experienceTime\":null,\"accumulateAsset\":null,\"businessScale\":null,\"incomeBase\":null,\"basisIncome\":null,\"incomeAssessment\":null,\"businessPlan\":null,\"ldpPropertyBusinessId\":\"cb14581d-3f2e-11ee-9faf-0242ac120005\",\"customerTransactionIncomes\":[]}]},{\"id\":6600,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"other1\",\"incomeItems\":[{\"id\":2699,\"incomeType\":\"V005\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"\",\"incomeTypeValue\":\"Nguồn thu khác\",\"recognizedIncome\":50000000,\"orderDisplay\":null,\"incomeFuture\":false,\"incomeInfo\":\"Other\",\"incomeDetail\":\"V005\",\"incomeDetailValue\":\"Mở lớp dạy học\",\"explanation\":null,\"ldpOtherId\":\"cb14581d-3f2e-11ee-9faf-0242ac120002\"}]},{\"id\":6601,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"89fb718b-3f2e-11ee-9faf-0242ac120002\",\"incomeItems\":[{\"id\":2202,\"incomeType\":\"V003\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"incomeTypeValue\":\"CNKD/HGĐ/HKD\",\"recognizedIncome\":30000000,\"orderDisplay\":null,\"businessRegistrationNumber\":\"827382781\",\"companyName\":\"SBDDD\",\"mainBusinessSector\":\"aksdjlkajskldjasd\",\"capitalContributionRate\":null,\"businessPlaceOwnership\":null,\"businessPlaceOwnershipValue\":null,\"productionProcess\":null,\"recordMethod\":null,\"input\":null,\"output\":null,\"businessScale\":null,\"inventory\":null,\"evaluationPeriod\":null,\"evaluationPeriodValue\":null,\"incomeMonthly\":null,\"expenseMonthly\":null,\"profitMonthly\":null,\"profitMargin\":null,\"evaluateResult\":null,\"businessExperience\":null,\"address\":{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"},\"ldpBusinessId\":\"92f39280-3f2e-11ee-9faf-0242ac120002\"}]},{\"id\":6598,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"b91ef0a7-3f2e-11ee-9faf-0242ac120002\",\"incomeItems\":[{\"id\":2201,\"incomeType\":\"V004\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"incomeTypeValue\":\"Doanh nghiệp\",\"recognizedIncome\":40000000,\"orderDisplay\":null,\"businessRegistrationNumber\":\"827382781\",\"companyName\":\"SBDDD\",\"mainBusinessSector\":\"aksdjlkajskldjasd\",\"capitalContributionRate\":null,\"businessPlaceOwnership\":null,\"businessPlaceOwnershipValue\":null,\"productionProcess\":null,\"recordMethod\":null,\"input\":null,\"output\":null,\"businessScale\":null,\"inventory\":null,\"evaluationPeriod\":null,\"evaluationPeriodValue\":null,\"incomeMonthly\":null,\"expenseMonthly\":null,\"profitMonthly\":null,\"profitMargin\":null,\"evaluateResult\":null,\"businessExperience\":null,\"address\":{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"},\"ldpBusinessId\":\"bd1d6a3c-3f2e-11ee-9faf-0242ac120002\"}]},{\"id\":6597,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"88bb7abd-3f21-11ee-9faf-0242ac120002\",\"incomeItems\":[{\"id\":494,\"incomeType\":\"V002\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":null,\"incomeOwnerName\":null,\"incomeTypeValue\":null,\"recognizedIncome\":20000000,\"orderDisplay\":null,\"assetType\":null,\"assetTypeValue\":null,\"assetOwner\":null,\"assetAddress\":null,\"renter\":null,\"renterPhone\":null,\"rentalPurpose\":null,\"rentalPurposeValue\":null,\"rentalPrice\":null,\"explanation\":null,\"ldpRentalId\":\"af6bd22d-3f21-11ee-9faf-0242ac120002\"}]},{\"id\":6599,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"e7ba9dfa-3f4e-11ee-9faf-0242ac120002\",\"incomeItems\":[{\"id\":2947,\"incomeType\":\"V001\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"Nguyễn Thị Nhã\",\"incomeTypeValue\":\"Từ lương\",\"recognizedIncome\":10000000,\"orderDisplay\":null,\"taxCode\":null,\"socialInsuranceCode\":null,\"rankType\":null,\"rankTypeValue\":null,\"kpiRating\":null,\"kpiRatingValue\":null,\"payType\":\"V002\",\"payTypeValue\":\"Chuyển khoản\",\"laborType\":null,\"laborTypeValue\":null,\"businessRegistrationNumber\":null,\"groupOfWorking\":null,\"companyName\":\"MSB Bank\",\"position\":\"Chuyen vien chính\",\"startWorkingDay\":null,\"explanation\":null,\"address\":{},\"ldpSalaryId\":\"ebab45f2-3f4e-11ee-9faf-0242ac120002\"}]}],\"amlOpr\":null,\"effectivePeriodUnit\":null,\"creditConditions\":[],\"customerRelations\":[{\"id\":14605,\"refCustomerId\":1276,\"refCusId\":\"0e584e0413\",\"bpmCif\":\"00001276\",\"cif\":null,\"customerType\":\"RB\",\"relationship\":\"V003\",\"relationshipValue\":\"Bố ruột / bố nuôi hợp pháp\",\"orderDisplay\":null,\"mainIdentity\":{\"id\":21776,\"refIdentityId\":null,\"priority\":true,\"documentTypeValue\":\"Khác\",\"issuedBy\":\"V004\",\"issuedByValue\":\"Cục quản lý xuất nhập cảnh\",\"issuedPlace\":\"3\",\"issuedPlaceValue\":\"Tỉnh Vĩnh Long\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c58\",\"type\":\"OTH\",\"identityNumber\":\"170520230987\",\"issuedDate\":\"29/03/2019\"},\"addresses\":[{\"cityCode\":\"2\",\"cityValue\":\"Tỉnh Vĩnh Phúc\",\"districtCode\":\"579\",\"districtValue\":\"Thị xã Phúc Yên\",\"wardCode\":\"1641\",\"wardValue\":\"Xã Nam Viêm\",\"addressLine\":\"Nha test\",\"addressLinkId\":\"9050eedd-ed08-4f99-a538-c2400775778e\",\"id\":13978,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"6b9953261234\",\"hktt\":false},{\"cityCode\":\"42\",\"cityValue\":\"Tỉnh Hà Giang\",\"districtCode\":\"733\",\"districtValue\":\"Huyện Yên Minh\",\"wardCode\":\"11289\",\"wardValue\":\"Xã Sủng Tráng\",\"addressLine\":\" while back I needed to count the amount of letters that a piece of text in an email template had (to avoid passing any character limits). Unfortunately, I culd not think of a quick way to do so on my macbook and I therefore turned to the Internet.-\",\"addressLinkId\":\"f15c75e0-3a97-4b48-8a49-600d9396cfd4\",\"id\":13979,\"canDelete\":true,\"addressType\":\"V002\",\"addressTypeValue\":\"Địa chỉ sinh sống hiện tại\",\"ldpAddressId\":\"6b995326122343@@4\",\"hktt\":false}],\"mainAddress\":{\"cityCode\":\"2\",\"cityValue\":\"Tỉnh Vĩnh Phúc\",\"districtCode\":\"579\",\"districtValue\":\"Thị xã Phúc Yên\",\"wardCode\":\"1641\",\"wardValue\":\"Xã Nam Viêm\",\"addressLine\":\"Nha test\",\"addressLinkId\":\"9050eedd-ed08-4f99-a538-c2400775778e\",\"id\":13978,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"6b9953261234\",\"hktt\":false},\"gender\":\"F\",\"genderValue\":\"Nữ\",\"age\":null,\"msbMember\":true,\"email\":\"nhaa@gmail.com\",\"phoneNumber\":\"0987654327\",\"literacy\":\"V003\",\"identityDocuments\":[{\"id\":21776,\"refIdentityId\":null,\"priority\":true,\"documentTypeValue\":\"Khác\",\"issuedBy\":\"V004\",\"issuedByValue\":\"Cục quản lý xuất nhập cảnh\",\"issuedPlace\":\"3\",\"issuedPlaceValue\":\"Tỉnh Vĩnh Long\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c58\",\"type\":\"OTH\",\"identityNumber\":\"170520230987\",\"issuedDate\":\"29/03/2019\"}],\"name\":\"Nguyễn Thị Nhã\",\"birthday\":\"06/03/1996\",\"maritalStatus\":\"2\",\"maritalStatusValue\":\"Đã kết hôn\",\"national\":\"VN\",\"nationalValue\":\"Việt Nam\",\"customerSegment\":\"V001\",\"staffId\":\"908765432\",\"literacyValue\":null}],\"application\":{\"refId\":\"n098765467\",\"bpmId\":\"151-00006180\",\"source\":\"BPM\",\"segment\":\"V003\",\"createdBy\":\"Nhant4\",\"approvalType\":null,\"processFlow\":\"V003\",\"processFlowValue\":\"Luồng thường\",\"submissionPurpose\":\"V001\",\"submissionPurposeValue\":\"Cấp mới\",\"riskLevel\":null,\"businessUnit\":\"MSB Đống Đa\",\"partnerCode\":null,\"createdAt\":\"05/09/2023 15:12:41\",\"createdFullName\":\"Nhã role KS HĐTD\",\"createdPhoneNumber\":\"0915163465\",\"assignee\":\"Nhant4\",\"proposalApprovalFullName\":null,\"proposalApprovalPhoneNumber\":null,\"status\":null,\"generatorStatus\":\"00\",\"area\":\"AREA 2\",\"region\":\"Miền Bắc\",\"suggestedAmount\":null,\"distributionForm\":null,\"regulatoryCode\":null,\"processingRole\":\"PD_RB_RM\",\"updatedBy\":\"cj_bo_rm01\",\"areaCode\":\"AREA2\",\"regionCode\":\"NORTH\",\"dealCode\":\"nhadeal1\"},\"loanApprovalPosition\":null,\"limitCredits\":[],\"cic\":null,\"additionalContents\":null,\"customer\":{\"id\":14607,\"refCustomerId\":1264,\"refCusId\":\"0e58e013\",\"bpmCif\":\"00001264\",\"cif\":null,\"customerType\":\"RB\",\"relationship\":null,\"relationshipValue\":null,\"orderDisplay\":null,\"mainIdentity\":{\"id\":21780,\"refIdentityId\":null,\"priority\":true,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V001\",\"issuedByValue\":\"Cục Cảnh sát ĐKQL cư trú và DLQG về dân cư\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c3\",\"type\":\"CMND\",\"identityNumber\":\"1705202309\",\"issuedDate\":\"29/03/2012\"},\"addresses\":[{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\",\"addressLinkId\":\"4543ebda-1ad5-4d81-b6e4-c8ae80644a4a\",\"id\":13982,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"1624c379\",\"hktt\":false},{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\",\"addressLinkId\":\"7a2f85c1-4691-4b92-b81a-14f0e87666f8\",\"id\":13981,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"1624c390\",\"hktt\":false},{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\",\"addressLinkId\":\"0d3e75fd-95c7-4ff3-8378-2f457b8e3e49\",\"id\":13980,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"1624c323\",\"hktt\":false}],\"mainAddress\":{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\",\"addressLinkId\":\"4543ebda-1ad5-4d81-b6e4-c8ae80644a4a\",\"id\":13982,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"1624c379\",\"hktt\":false},\"gender\":\"F\",\"genderValue\":\"Nữ\",\"age\":null,\"msbMember\":true,\"email\":\"nhant@gmail.com\",\"phoneNumber\":\"0397203893\",\"literacy\":\"V001\",\"identityDocuments\":[{\"id\":21777,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c8\",\"type\":\"CMND\",\"identityNumber\":\"170520237\",\"issuedDate\":\"29/03/2012\"},{\"id\":21778,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c4\",\"type\":\"CMND\",\"identityNumber\":\"170520230\",\"issuedDate\":\"29/03/2012\"},{\"id\":21782,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c7\",\"type\":\"CMND\",\"identityNumber\":\"170520237\",\"issuedDate\":\"29/03/2012\"},{\"id\":21781,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c6\",\"type\":\"CMND\",\"identityNumber\":\"170520236\",\"issuedDate\":\"29/03/2012\"},{\"id\":21779,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c5\",\"type\":\"CMND\",\"identityNumber\":\"170520235\",\"issuedDate\":\"29/03/2012\"},{\"id\":21780,\"refIdentityId\":null,\"priority\":true,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V001\",\"issuedByValue\":\"Cục Cảnh sát ĐKQL cư trú và DLQG về dân cư\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c3\",\"type\":\"CMND\",\"identityNumber\":\"1705202309\",\"issuedDate\":\"29/03/2012\"}],\"name\":\"Nguyễn Nhã Test\",\"birthday\":\"06/03/1996\",\"maritalStatus\":\"1\",\"maritalStatusValue\":\"Độc thân\",\"national\":\"VN\",\"nationalValue\":\"Việt Nam\",\"customerSegment\":\"V001\",\"staffId\":\"87654\",\"literacyValue\":null}}}",
        ComparatorApplicationRequest.class, objectMapper);
    getApplicationDTO = JsonUtil.convertString2Object(
        "{\"initializeInfo\":{\"completed\":false,\"application\":{\"refId\":\"n098765467\",\"bpmId\":\"151-00006180\",\"source\":\"BPM\",\"segment\":\"V003\",\"createdBy\":\"Nhant4\",\"approvalType\":null,\"processFlow\":\"V003\",\"processFlowValue\":\"Luồng thường\",\"submissionPurpose\":\"V001\",\"submissionPurposeValue\":\"Cấp mới\",\"riskLevel\":null,\"businessUnit\":\"MSB Đống Đa\",\"partnerCode\":null,\"createdAt\":\"05/09/2023 15:12:41\",\"createdFullName\":\"Nhã role KS HĐTD\",\"createdPhoneNumber\":\"0915163465\",\"assignee\":\"Nhant4\",\"proposalApprovalFullName\":null,\"proposalApprovalPhoneNumber\":null,\"status\":null,\"generatorStatus\":\"00\",\"area\":\"AREA 2\",\"region\":\"Miền Bắc\",\"suggestedAmount\":null,\"distributionForm\":null,\"regulatoryCode\":null,\"processingRole\":\"PD_RB_RM\",\"updatedBy\":\"cj_bo_rm01\",\"areaCode\":\"AREA2\",\"regionCode\":\"NORTH\",\"dealCode\":\"nhadeal1\"},\"customerAndRelationPerson\":{\"customer\":{\"id\":14607,\"refCustomerId\":1264,\"refCusId\":\"0e58e013\",\"bpmCif\":\"00001264\",\"cif\":null,\"customerType\":\"RB\",\"relationship\":null,\"relationshipValue\":null,\"orderDisplay\":null,\"mainIdentity\":{\"id\":21780,\"refIdentityId\":null,\"priority\":true,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V001\",\"issuedByValue\":\"Cục Cảnh sát ĐKQL cư trú và DLQG về dân cư\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c3\",\"type\":\"CMND\",\"identityNumber\":\"1705202309\",\"issuedDate\":\"29/03/2012\"},\"addresses\":[{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\",\"addressLinkId\":\"0d3e75fd-95c7-4ff3-8378-2f457b8e3e49\",\"id\":13980,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"1624c323\",\"hktt\":false},{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\",\"addressLinkId\":\"7a2f85c1-4691-4b92-b81a-14f0e87666f8\",\"id\":13981,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"1624c390\",\"hktt\":false},{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\",\"addressLinkId\":\"4543ebda-1ad5-4d81-b6e4-c8ae80644a4a\",\"id\":13982,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"1624c379\",\"hktt\":false}],\"mainAddress\":{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\",\"addressLinkId\":\"0d3e75fd-95c7-4ff3-8378-2f457b8e3e49\",\"id\":13980,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"1624c323\",\"hktt\":false},\"gender\":\"F\",\"genderValue\":\"Nữ\",\"age\":null,\"msbMember\":true,\"email\":\"nhant@gmail.com\",\"phoneNumber\":\"0397203893\",\"literacy\":\"V001\",\"identityDocuments\":[{\"id\":21779,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c5\",\"type\":\"CMND\",\"identityNumber\":\"170520235\",\"issuedDate\":\"29/03/2012\"},{\"id\":21778,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c4\",\"type\":\"CMND\",\"identityNumber\":\"170520230\",\"issuedDate\":\"29/03/2012\"},{\"id\":21781,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c6\",\"type\":\"CMND\",\"identityNumber\":\"170520236\",\"issuedDate\":\"29/03/2012\"},{\"id\":21777,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c8\",\"type\":\"CMND\",\"identityNumber\":\"170520237\",\"issuedDate\":\"29/03/2012\"},{\"id\":21780,\"refIdentityId\":null,\"priority\":true,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V001\",\"issuedByValue\":\"Cục Cảnh sát ĐKQL cư trú và DLQG về dân cư\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c3\",\"type\":\"CMND\",\"identityNumber\":\"1705202309\",\"issuedDate\":\"29/03/2012\"},{\"id\":21782,\"refIdentityId\":null,\"priority\":false,\"documentTypeValue\":\"CMND\",\"issuedBy\":\"V005\",\"issuedByValue\":\"Bộ Quốc phòng\",\"issuedPlace\":\"24\",\"issuedPlaceValue\":\"Tỉnh Nam Định\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c7\",\"type\":\"CMND\",\"identityNumber\":\"170520237\",\"issuedDate\":\"29/03/2012\"}],\"name\":\"Nguyễn Nhã Test\",\"birthday\":\"06/03/1996\",\"maritalStatus\":\"1\",\"maritalStatusValue\":\"Độc thân\",\"national\":\"VN\",\"nationalValue\":\"Việt Nam\",\"customerSegment\":\"V001\",\"staffId\":\"87654\",\"literacyValue\":null},\"customerRelations\":[{\"id\":14605,\"refCustomerId\":1276,\"refCusId\":\"0e584e0413\",\"bpmCif\":\"00001276\",\"cif\":null,\"customerType\":\"RB\",\"relationship\":\"V003\",\"relationshipValue\":\"Bố ruột / bố nuôi hợp pháp\",\"orderDisplay\":null,\"mainIdentity\":{\"id\":21776,\"refIdentityId\":null,\"priority\":true,\"documentTypeValue\":\"Khác\",\"issuedBy\":\"V004\",\"issuedByValue\":\"Cục quản lý xuất nhập cảnh\",\"issuedPlace\":\"3\",\"issuedPlaceValue\":\"Tỉnh Vĩnh Long\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c58\",\"type\":\"OTH\",\"identityNumber\":\"170520230987\",\"issuedDate\":\"29/03/2019\"},\"addresses\":[{\"cityCode\":\"42\",\"cityValue\":\"Tỉnh Hà Giang\",\"districtCode\":\"733\",\"districtValue\":\"Huyện Yên Minh\",\"wardCode\":\"11289\",\"wardValue\":\"Xã Sủng Tráng\",\"addressLine\":\" while back I needed to count the amount of letters that a piece of text in an email template had (to avoid passing any character limits). Unfortunately, I culd not think of a quick way to do so on my macbook and I therefore turned to the Internet.-\",\"addressLinkId\":\"f15c75e0-3a97-4b48-8a49-600d9396cfd4\",\"id\":13979,\"canDelete\":true,\"addressType\":\"V002\",\"addressTypeValue\":\"Địa chỉ sinh sống hiện tại\",\"ldpAddressId\":\"6b995326122343@@4\",\"hktt\":false},{\"cityCode\":\"2\",\"cityValue\":\"Tỉnh Vĩnh Phúc\",\"districtCode\":\"579\",\"districtValue\":\"Thị xã Phúc Yên\",\"wardCode\":\"1641\",\"wardValue\":\"Xã Nam Viêm\",\"addressLine\":\"Nha test\",\"addressLinkId\":\"9050eedd-ed08-4f99-a538-c2400775778e\",\"id\":13978,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"6b9953261234\",\"hktt\":false}],\"mainAddress\":{\"cityCode\":\"2\",\"cityValue\":\"Tỉnh Vĩnh Phúc\",\"districtCode\":\"579\",\"districtValue\":\"Thị xã Phúc Yên\",\"wardCode\":\"1641\",\"wardValue\":\"Xã Nam Viêm\",\"addressLine\":\"Nha test\",\"addressLinkId\":\"9050eedd-ed08-4f99-a538-c2400775778e\",\"id\":13978,\"canDelete\":false,\"addressType\":\"V001\",\"addressTypeValue\":\"Hộ khẩu thường trú\",\"ldpAddressId\":\"6b9953261234\",\"hktt\":false},\"gender\":\"F\",\"genderValue\":\"Nữ\",\"age\":null,\"msbMember\":true,\"email\":\"nhaa@gmail.com\",\"phoneNumber\":\"0987654327\",\"literacy\":\"V003\",\"identityDocuments\":[{\"id\":21776,\"refIdentityId\":null,\"priority\":true,\"documentTypeValue\":\"Khác\",\"issuedBy\":\"V004\",\"issuedByValue\":\"Cục quản lý xuất nhập cảnh\",\"issuedPlace\":\"3\",\"issuedPlaceValue\":\"Tỉnh Vĩnh Long\",\"orderDisplay\":null,\"ldpIdentityId\":\"126969c58\",\"type\":\"OTH\",\"identityNumber\":\"170520230987\",\"issuedDate\":\"29/03/2019\"}],\"name\":\"Nguyễn Thị Nhã\",\"birthday\":\"06/03/1996\",\"maritalStatus\":\"2\",\"maritalStatusValue\":\"Đã kết hôn\",\"national\":\"VN\",\"nationalValue\":\"Việt Nam\",\"customerSegment\":\"V001\",\"staffId\":\"908765432\",\"literacyValue\":null}],\"enterpriseRelations\":[{\"id\":14606,\"refCustomerId\":null,\"refCusId\":\"6b995326\",\"bpmCif\":null,\"cif\":null,\"customerType\":\"EB\",\"relationship\":null,\"relationshipValue\":null,\"orderDisplay\":null,\"mainIdentity\":null,\"addresses\":null,\"mainAddress\":null,\"businessType\":\"V001\",\"businessRegistrationNumber\":\"40\",\"companyName\":\"Lê Đức Thọ Mỹ Đình aaaaa\",\"firstRegistrationAt\":null,\"deputy\":null,\"identityDocuments\":null}],\"applicationContact\":null,\"cic\":null,\"amlOpr\":null},\"incomes\":[{\"id\":6598,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"b91ef0a7-3f2e-11ee-9faf-0242ac120002\",\"incomeItems\":[{\"id\":2201,\"incomeType\":\"V004\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"incomeTypeValue\":\"Doanh nghiệp\",\"recognizedIncome\":40000000,\"orderDisplay\":null,\"businessRegistrationNumber\":\"827382781\",\"companyName\":\"SBDDD\",\"mainBusinessSector\":\"aksdjlkajskldjasd\",\"capitalContributionRate\":null,\"businessPlaceOwnership\":null,\"businessPlaceOwnershipValue\":null,\"productionProcess\":null,\"recordMethod\":null,\"input\":null,\"output\":null,\"businessScale\":null,\"inventory\":null,\"evaluationPeriod\":null,\"evaluationPeriodValue\":null,\"incomeMonthly\":null,\"expenseMonthly\":null,\"profitMonthly\":null,\"profitMargin\":null,\"evaluateResult\":null,\"businessExperience\":null,\"address\":{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"},\"ldpBusinessId\":\"bd1d6a3c-3f2e-11ee-9faf-0242ac120002\"}]},{\"id\":6601,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"89fb718b-3f2e-11ee-9faf-0242ac120002\",\"incomeItems\":[{\"id\":2202,\"incomeType\":\"V003\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"Nguyễn Văn Mạnh\",\"incomeTypeValue\":\"CNKD/HGĐ/HKD\",\"recognizedIncome\":30000000,\"orderDisplay\":null,\"businessRegistrationNumber\":\"827382781\",\"companyName\":\"SBDDD\",\"mainBusinessSector\":\"aksdjlkajskldjasd\",\"capitalContributionRate\":null,\"businessPlaceOwnership\":null,\"businessPlaceOwnershipValue\":null,\"productionProcess\":null,\"recordMethod\":null,\"input\":null,\"output\":null,\"businessScale\":null,\"inventory\":null,\"evaluationPeriod\":null,\"evaluationPeriodValue\":null,\"incomeMonthly\":null,\"expenseMonthly\":null,\"profitMonthly\":null,\"profitMargin\":null,\"evaluateResult\":null,\"businessExperience\":null,\"address\":{\"cityCode\":\"40\",\"cityValue\":\"Thành phố Hà Nội\",\"districtCode\":\"746\",\"districtValue\":\"Quận Nam Từ Liêm\",\"wardCode\":\"11730\",\"wardValue\":\"Phường Mỹ Đình 2\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình\"},\"ldpBusinessId\":\"92f39280-3f2e-11ee-9faf-0242ac120002\"}]},{\"id\":6599,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"e7ba9dfa-3f4e-11ee-9faf-0242ac120002\",\"incomeItems\":[{\"id\":2947,\"incomeType\":\"V001\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"Nguyễn Thị Nhã\",\"incomeTypeValue\":\"Từ lương\",\"recognizedIncome\":10000000,\"orderDisplay\":null,\"taxCode\":null,\"socialInsuranceCode\":null,\"rankType\":null,\"rankTypeValue\":null,\"kpiRating\":null,\"kpiRatingValue\":null,\"payType\":\"V002\",\"payTypeValue\":\"Chuyển khoản\",\"laborType\":null,\"laborTypeValue\":null,\"businessRegistrationNumber\":null,\"groupOfWorking\":null,\"companyName\":\"MSB Bank\",\"position\":\"Chuyen vien chính\",\"startWorkingDay\":null,\"explanation\":null,\"address\":{},\"ldpSalaryId\":\"ebab45f2-3f4e-11ee-9faf-0242ac120002\"}]},{\"id\":6600,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"other1\",\"incomeItems\":[{\"id\":2699,\"incomeType\":\"V005\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"\",\"incomeTypeValue\":\"Nguồn thu khác\",\"recognizedIncome\":50000000,\"orderDisplay\":null,\"incomeFuture\":false,\"incomeInfo\":\"Other\",\"incomeDetail\":\"V005\",\"incomeDetailValue\":\"Mở lớp dạy học\",\"explanation\":null,\"ldpOtherId\":\"cb14581d-3f2e-11ee-9faf-0242ac120002\"}]},{\"id\":6597,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"88bb7abd-3f21-11ee-9faf-0242ac120002\",\"incomeItems\":[{\"id\":494,\"incomeType\":\"V002\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":null,\"incomeOwnerName\":null,\"incomeTypeValue\":null,\"recognizedIncome\":20000000,\"orderDisplay\":null,\"assetType\":null,\"assetTypeValue\":null,\"assetOwner\":null,\"renter\":null,\"renterPhone\":null,\"rentalPurpose\":null,\"rentalPurposeValue\":null,\"rentalPrice\":null,\"explanation\":null,\"ldpRentalId\":\"af6bd22d-3f21-11ee-9faf-0242ac120002\"}]},{\"id\":6602,\"incomeRecognitionMethod\":\"ACTUALLY\",\"incomeRecognitionMethodValue\":\"Thực nhận\",\"orderDisplay\":null,\"ldpIncomeId\":\"bds02\",\"incomeItems\":[{\"id\":1190,\"incomeType\":\"V006\",\"customerId\":null,\"refCustomerId\":null,\"incomeOwner\":\"V017\",\"incomeOwnerValue\":\"Khách hàng\",\"incomeOwnerName\":\"Nguyễn thị nhã\",\"incomeTypeValue\":\"Kinh doanh bất động sản (giao dịch gom xây)\",\"recognizedIncome\":360000,\"orderDisplay\":null,\"businessExperience\":null,\"experienceTime\":null,\"accumulateAsset\":null,\"businessScale\":null,\"incomeBase\":null,\"basisIncome\":null,\"incomeAssessment\":null,\"businessPlan\":null,\"ldpPropertyBusinessId\":\"cb14581d-3f2e-11ee-9faf-0242ac120005\",\"customerTransactionIncomes\":[]}]}]},\"fieldInfor\":{\"completed\":false,\"fieldInformations\":[]},\"debtInfo\":{\"completed\":false,\"limitCredits\":[],\"credits\":[{\"id\":10254,\"ldpCreditId\":\"4525f6e2\",\"creditType\":\"V003\",\"creditTypeValue\":\"Thẻ tín dụng\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":null,\"approveResult\":null,\"approveResultValue\":null,\"loanAmount\":9600000,\"orderDisplay\":null,\"isAllocation\":null,\"appCreditCardId\":3667,\"cardPolicyCode\":null,\"productName\":null,\"cardType\":\"MCDSS\",\"cardTypeValue\":\"010-MSB MASTERCARD DAILY SHOPPING STAFF\",\"way4CardCode\":null,\"cardName\":\"NGUYEN VAN MANH    \",\"secretQuestion\":\"Who is Batman?\",\"limitSustentivePeriod\":null,\"email\":\"vanmanh6394@gmail.com\",\"autoDeductRate\":\"V002\",\"autoDeductRateValue\":\"Tối thiểu\",\"deductAccountNumber\":\"172638761278647\",\"cardForm\":\"V001\",\"cardFormValue\":\"Vật lý\",\"cardReceiveAddress\":\"V001\",\"cardReceiveAddressValue\":\"Địa chỉ cư trú hiện tại\",\"address\":{\"cityCode\":\"42\",\"cityValue\":\"Tỉnh Hà Giang\",\"districtCode\":\"733\",\"districtValue\":\"Huyện Yên Minh\",\"wardCode\":\"11288\",\"wardValue\":\"Xã Thắng Mố\",\"addressLine\":\"Lê Đức Thọ Mỹ Đình 234567890-\"},\"hasSubCard\":false,\"subCreditCards\":[],\"contractL\":null,\"issuingContract\":null,\"contractNumber\":null,\"createdDate\":null},{\"id\":10256,\"ldpCreditId\":\"f2cd385f\",\"creditType\":\"V001\",\"creditTypeValue\":\"Cho vay\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":null,\"approveResult\":null,\"approveResultValue\":null,\"loanAmount\":1000000,\"orderDisplay\":null,\"isAllocation\":null,\"appCreditLoanId\":3788,\"productCode\":null,\"productName\":null,\"payback\":false,\"loanPurpose\":\"V001\",\"loanPurposeValue\":\"Cho vay mua sắm trang thiết bị, nội thất gia đình\",\"loanPurposeExplanation\":null,\"loanPeriod\":12,\"kunnPeriod\":null,\"originalPeriod\":null,\"totalCapital\":600000000,\"equityCapital\":null,\"ltd\":null,\"creditForm\":\"V001\",\"creditFormValue\":\"Cho vay từng lần\",\"disburseFrequency\":null,\"disburseFrequencyValue\":null,\"debtPayMethod\":null,\"debtPayMethodValue\":null,\"disburseMethod\":null,\"disburseMethodValue\":null,\"disburseMethodExplanation\":null,\"principalPayPeriod\":null,\"principalPayUnit\":null,\"principalPayUnitValue\":null,\"interestPayPeriod\":null,\"interestPayUnit\":null,\"interestPayUnitValue\":null,\"acfNo\":null,\"accountNo\":null,\"status\":null},{\"id\":10255,\"ldpCreditId\":\"f2cd3825f\",\"creditType\":\"V002\",\"creditTypeValue\":\"Thấu chi\",\"guaranteeForm\":\"V002\",\"guaranteeFormValue\":\"Không TSBĐ\",\"documentCode\":null,\"approveResult\":null,\"approveResultValue\":null,\"loanAmount\":1000000,\"orderDisplay\":null,\"isAllocation\":null,\"appCreditOverdraftId\":2715,\"interestRateCode\":null,\"productName\":null,\"loanPurpose\":\"V005\",\"loanPurposeValue\":\"Cho vay đầu tư TSCĐ là máy móc, thiết bị, phương tiện vận tải\",\"limitSustentivePeriod\":12,\"debtPayMethod\":null,\"debtPayMethodValue\":null,\"infoAdditional\":null,\"acfNo\":null,\"accountNo\":null,\"status\":null}],\"creditRatings\":[],\"repayment\":{\"totalIncome\":null,\"totalRepay\":null,\"dti\":null,\"dsr\":null,\"mue\":null,\"evaluate\":null},\"specialRiskContents\":null,\"additionalContents\":null,\"otherReviews\":[],\"creditConditions\":[],\"phoneExpertise\":null,\"effectivePeriod\":null,\"effectivePeriodUnit\":null,\"proposalApprovalPosition\":null,\"loanApprovalPosition\":null,\"loanApprovalPositionValue\":null,\"applicationAuthorityLevel\":null,\"priorityAuthority\":null,\"assetAllocations\":null}}",
        GetApplicationDTO.class, objectMapper);
    AssetCommonInfoDTO assetInfo = JsonUtil.convertString2Object("{\"assetData\":[{\"id\":1828,\"assetVersion\":3,\"objectId\":null,\"applicationId\":\"151-00004651\",\"assetCode\":\"Định danh tài sản\",\"assetType\":\"V401\",\"assetTypeName\":\"Đất ở\",\"assetGroup\":\"V001\",\"assetGroupName\":\"Bất động sản\",\"assetName\":\"Bất động sản là Thửa đất số Thửa đất/lô đất số, tờ bản đồ số Tờ bản đồ/vị trí lô đất số, tại Số nhà/Tên đường, Xã Văn Phú, Thành phố Yên Bái, Tỉnh Yên Bái theo GCN QSD đất số 12313123 do Nơi cấp ngày 14/11/2023, đứng tên\",\"ownerType\":null,\"ownerTypeName\":null,\"nextValuationDay\":null,\"addressLinkId\":null,\"status\":\"ACTIVE\",\"mortgageStatus\":\"V001\",\"hasComponent\":false,\"state\":null,\"assetAdditionalInfo\":{\"realEstateInfo\":{\"id\":2836,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-27T15:57:42\",\"updatedBy\":\"bpmtest1\",\"updatedAt\":\"2024-02-27T16:05:40\",\"provinceCode\":\"1\",\"provinceName\":\"Tỉnh Yên Bái\",\"districtCode\":\"657\",\"districtName\":\"Thành phố Yên Bái\",\"wardCode\":\"1467\",\"wardName\":\"Xã Văn Phú\",\"streetNumber\":\"Số nhà/Tên đường\",\"houseNumber\":null,\"floor\":null,\"landPlot\":\"Tờ bản đồ/vị trí lô đất số\",\"landOverValueStr\":null,\"landParcel\":\"Thửa đất/lô đất số\",\"mapLocation\":null,\"investorInformation\":null,\"description\":\"Mô tả tài sản (nếu có)\",\"payMethod\":null,\"buildingWork\":false,\"futureAsset\":false,\"expertiseValue\":null,\"landValue\":null,\"landValueStr\":null,\"landOverValue\":null,\"isNew\":false,\"propertyTypeDetail\":null,\"blockadeDisbursementPlan\":null,\"blocked\":null,\"soNguoiBenBan\":null,\"beneficiaryType\":null,\"certificateBookNo\":null,\"certificateVerified\":null,\"contractDetail\":null,\"landArea\":null,\"landAreaText\":null,\"landUsageForm\":null,\"landGeneralUse\":null,\"landUsePrivate\":null,\"landUseTarget\":null,\"landExpired\":null,\"landOriginOfUse\":null,\"landNote\":null,\"houseType\":null,\"houseAddress\":null,\"houseFloor\":null,\"houseName\":null,\"houseAreaUse\":null,\"houseArea\":null,\"houseStructure\":null,\"houseFloorArea\":null,\"houseOwnershipForm\":null,\"houseTypeDetail\":null,\"houseExpired\":null,\"completedConstructionYear\":null,\"houseAreaOutside\":null,\"houseNote\":null,\"houseDesc\":null,\"houseFloor1\":null,\"houseStructure1\":null,\"houseTypeDetail1\":null,\"houseArea1\":null,\"houseFloorArea1\":null,\"houseCreatedAt\":null,\"houseArchitectural\":null,\"houseStatus\":null,\"buyerName\":null,\"investorConfirmed\":null,\"projectId\":null,\"proposalCollateralValueStr\":null,\"sellers\":null,\"project\":null,\"notarizedInformationId\":null,\"moreAssetsOnLand\":null,\"mortgageContractNo\":null,\"houseUpdatedAt1\":null},\"transportationInfo\":null,\"stockInfo\":null,\"ownerInfo\":[{\"id\":29028,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-28T14:39:01\",\"updatedBy\":null,\"updatedAt\":null,\"customerId\":2772,\"customerRefCode\":\"9466\",\"customerName\":\"hant92 test 4\",\"relationshipCode\":\"V001\",\"relationshipName\":\"Khách hàng\",\"customerVersion\":null}],\"legalDocumentInfo\":{\"id\":5113,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-27T15:57:45\",\"updatedBy\":\"bpmtest1\",\"updatedAt\":\"2024-02-27T16:05:40\",\"docValue\":\"12313123\",\"docType\":\"V001\",\"docName\":\"GCN QSD đất\",\"issuedBy\":\"Nơi cấp\",\"dateOfIssue\":\"2023-11-14T00:00:00\",\"description\":\"\"},\"laborContractInfo\":null,\"otherInfo\":null,\"collateralAssetInfo\":{\"id\":6204,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-27T15:57:45\",\"updatedBy\":null,\"updatedAt\":null,\"proposalCollateralValue\":12312312,\"valuationGuaranteed\":null,\"ltv\":null,\"description\":null,\"isAdditional\":null,\"kvalue\":null},\"valuationInfo\":{\"id\":847,\"createdBy\":\"bpmtest1\",\"createdAt\":\"2024-02-27T18:16:47\",\"updatedBy\":\"bpmtest1\",\"updatedAt\":\"2024-02-28T13:21:08.732\",\"mvlId\":\"18772\",\"status\":\"HS đã hoàn thành (đã đóng)\",\"valuationDate\":\"2023-10-18\",\"valuationAmount\":150000000,\"assetName\":null,\"valuationAssetId\":\"PTVT.TB.61\",\"assetCodeCore\":null,\"syncValuationDay\":\"2024-02-28T13:21:09.16\",\"assetType\":null,\"assetGroup\":null,\"assetCode\":null},\"components\":[],\"generalObligations\":[]},\"sourceType\":null}]}", AssetCommonInfoDTO.class, objectMapper);
    getApplicationDTO.setAssetInfo(assetInfo);
    applicationDraftEntities = new HashSet<>();
    draftAssetEntities = objectMapper.readValue(
            new File(pathSourceFile + "application_asset_draft.json"),
            ApplicationDraftEntity.class);
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
  void testVerifyLdpStatus() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setProcessingRole("IsNotRM");

    // Call the verifyLdpStatus function with the ApplicationEntity object
    Object result = verifyServiceImpl.verifyLdpStatus(applicationEntity);

    // Check that the result is a ComparatorApplicationResponse object with a valid property set to true
    Assertions.assertEquals(ComparatorApplicationResponse.class, result.getClass());
    Assertions.assertEquals(Optional.of(true),
        Optional.of(((ComparatorApplicationResponse) result).getValid()));
  }

//  @Test
//  public void testVerifyLdpStatusWhenSubmit() {
//    String bpmId = "151-00006180";
//    // Create an ApplicationEntity object with a valid source and status
//    ApplicationEntity applicationEntity = new ApplicationEntity();
//    applicationEntity.setBpmId(bpmId);
//    applicationEntity.setSource(SourceApplication.BPM.name());
//    applicationEntity.setStatus(ApplicationStatus.AS0001.getValue());
//    applicationEntity.setLdpStatus(ApplicationStatus.AS4001.getValue());
//
//    // Mock the applicationRepository to return the ApplicationEntity object when findByBpmId is called
//    when(applicationRepository.findByBpmId("151-00006180")).thenReturn(Optional.of(applicationEntity));
//
//    // Call the verifyLdpStatusWhenSubmit function with a valid BPM ID
//    Object result = verifyServiceImpl.verifyLdpStatusWhenSubmit(bpmId);
//
//    // Check that the result is a ComparatorApplicationResponse object with a valid property set to true
//    assertEquals(ComparatorApplicationResponse.class, result.getClass());
//    assertEquals(Optional.of(false), Optional.of(((ComparatorApplicationResponse) result).getValid()));
//  }
  @Test
  void testCompareWithApplicationLDP() {
    String bpmId = "151-00006180";
    // Create a mock ApplicationEntity object
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setBpmId("151-00006180");

    // Create a mock ComparatorApplicationResponse object
    ComparatorApplicationResponse response = new ComparatorApplicationResponse();
    response.setValid(false);

    // Mock the loanProposalClient.callLoanProposalService method to return the mock response
    when(loanProposalClient.callLoanProposalService(bpmId, comparatorApplicationRequest))
        .thenReturn(response);

    ComparatorApplicationResponse resp =
        loanProposalClient.callLoanProposalService(bpmId, comparatorApplicationRequest);

    Assertions.assertEquals(response, resp);
  }

//  @Test
//  public void testMapToAssetRequest() {
//    // Create test data
//    List<AssetDataResponse> assetData = new ArrayList<>();
//    AssetDataResponse asset1 = new AssetDataResponse();
//    asset1.setObjectId(123L);
//    asset1.setAssetGroup("Group1");
//    asset1.setAssetType("Type1");
//    asset1.setAssetCode("Code1");
//    asset1.setAssetAdditionalInfo(new AssetAdditionalInfoResponse());
//    asset1.getAssetAdditionalInfo().setRealEstateInfo(new RealEstateInfoResponse());
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setProvinceCode("City1");
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setDistrictCode("District1");
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setWardCode("Ward1");
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setHouseNumber("10");
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setFloor("Floor1");
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setLandPlot("Land Plot1");
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setLandParcel("Land Parcel1");
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setMapLocation("Map Location1");
//    asset1.getAssetAdditionalInfo().getRealEstateInfo().setDescription("Description1");
//    asset1.getAssetAdditionalInfo().getOwnerInfo().add(new OwnerInfoDTO());
//    asset1.getAssetAdditionalInfo().getOwnerInfo().get(0).setCustomerName("Owner1");
//    asset1.getAssetAdditionalInfo().getOwnerInfo().get(0).setRelationshipCode("Relation1");
//    assetData.add(asset1);
//
//    AssetDataResponse asset2 = new AssetDataResponse();
//    asset2.setObjectId(456L);
//    asset2.setAssetGroup("Group2");
//    asset2.setAssetType("Type2");
//    asset2.setAssetCode("Code2");
//    asset2.setAssetAdditionalInfo(new AssetAdditionalInfoResponse());
//    asset2.getAssetAdditionalInfo().setRealEstateInfo(new RealEstateInfoResponse());
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setProvinceCode("City2");
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setDistrictCode("District2");
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setWardCode("Ward2");
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setHouseNumber("20");
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setFloor("Floor2");
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setLandPlot("Land Plot2");
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setLandParcel("Land Parcel2");
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setMapLocation("Map Location2");
//    asset2.getAssetAdditionalInfo().getRealEstateInfo().setDescription("Description2");
//    asset2.getAssetAdditionalInfo().getOwnerInfo().add(new OwnerInfoDTO());
//    asset2.getAssetAdditionalInfo().getOwnerInfo().get(0).setCustomerName("Owner2");
//    asset2.getAssetAdditionalInfo().getOwnerInfo().get(0).setRelationshipCode("Relation2");
//    assetData.add(asset2);
//
//    // Call the method being tested
//    AssetRequest request = new AssetRequest();
//    request = verifyServiceImpl.mapToAssetRequest(assetData);
//
//    // Verify the result
//    List<AssetInforRequest> assetInfo = request.getAssetInfo();
//    assertEquals(2, assetInfo.size());
//    assertEquals("Group1", assetInfo.get(0).getCollateralGroup());
//    assertEquals("Type1", assetInfo.get(0).getCollateralType());
//    assertEquals("Code1", assetInfo.get(0).getCertificate());
//    assertEquals("City1", assetInfo.get(0).getCityCode());
//    assertEquals("District1", assetInfo.get(0).getDistrictCode());
//    assertEquals("Ward1", assetInfo.get(0).getWardCode());
//    assertEquals("Group2", assetInfo.get(1).getCollateralGroup());
//    assertEquals("Type2", assetInfo.get(1).getCollateralType());
//    assertEquals("Code2", assetInfo.get(1).getCertificate());
//    assertEquals("City2", assetInfo.get(1).getCityCode());
//    assertEquals("District2", assetInfo.get(1).getDistrictCode());
//    assertEquals("Ward2", assetInfo.get(1).getWardCode());
//    assertNull(assetInfo.get(1).getAddressLine());
//    assertEquals("20", assetInfo.get(1).getHouseNumber());
//    assertEquals("Floor2", assetInfo.get(1).getFloor());
//    assertEquals("Land Plot2", assetInfo.get(1).getLandPlot());
//    assertEquals("Land Parcel2", assetInfo.get(1).getLandParcel());
//    assertEquals("Map Location2", assetInfo.get(1).getMapLocation());
//    assertEquals("Description2", assetInfo.get(1).getDescriptionAsset());
//    assertEquals(1, assetInfo.get(1).getAssetOwner().size());
//    assertEquals("Owner2", assetInfo.get(1).getAssetOwner().get(0).getAssetOwnerName());
//    assertEquals("Relation2", assetInfo.get(1).getAssetOwner().get(0).getRelationshipCustomer());
//  }

  @Test
  void test_verifiRmCommitStatus_isFalse(){
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setProcessingRole(ProcessingRole.PD_RB_RM.name());

    // throw error when rmCommitStatus is false
    applicationEntity.setRmCommitStatus(false);
    Assertions.assertThrows(ApprovalException.class, ()-> verifyServiceImpl.verifyRmCommitStatus(applicationEntity));

    // Happy case
    applicationEntity.setRmCommitStatus(true);
    Assertions.assertDoesNotThrow(()-> verifyServiceImpl.verifyRmCommitStatus(applicationEntity));
  }
  @Test
  void verifyOpRiskCollateralTest() {
    ApplicationEntity applicationEntity = new ApplicationEntity();
    applicationEntity.setProcessingRole(PD_RB_RM.name());
    List<AmlOprEntity> amlOprEntities = new ArrayList<>();
    AmlOprEntity amlOprEntity = new AmlOprEntity();
    amlOprEntities.add(amlOprEntity);
    // Case 1: Không có bản ghi AmlOprEntity
    when(amlOprRepository.findByApplicationIdAndQueryType(any(), any())).thenReturn(Optional.of(Collections.emptyList()));
    when(collateralClient.validOpr(any(), any())).thenReturn(false);
    Assertions.assertThrows(ApprovalException.class, ()-> verifyServiceImpl.verifyOpRiskCollateral(applicationEntity));
    // Case 2: Có bản ghi và processingRole = PD_RB_RM
    when(amlOprRepository.findByApplicationIdAndQueryType(any(), any())).thenReturn(Optional.of(amlOprEntities));
    Assertions.assertThrows(ApprovalException.class, ()-> verifyServiceImpl.verifyOpRiskCollateral(applicationEntity));
    // Case 3: Có bản ghi và processingRole = PD_RB_CA1
    applicationEntity.setProcessingRole(PD_RB_CA1.name());
    Assertions.assertThrows(ApprovalException.class, ()-> verifyServiceImpl.verifyOpRiskCollateral(applicationEntity));

  }

  @ParameterizedTest
  @CsvSource({"null", "1"})
  void test_verifyAssetInfoStatus_success(String statusTab) throws InvocationTargetException, IllegalAccessException {
    Map<String, ApplicationDraftEntity>  draftEntityMap = new HashMap<>();
    Object data = mock(Object.class);
    if (statusTab.equals("1")){
      draftAssetEntities.setStatus(1);
      applicationDraftEntities.add(draftAssetEntities);
      when(applicationDraftRepository.findByBpmId(anyString())).thenReturn(Optional.of(applicationDraftEntities));
      // Happy case
      Assertions.assertDoesNotThrow(()-> verifyServiceImpl.verifyAssetInfoStatus("151-00006180"));
    }
    if (statusTab.equals("null")){
      CollateralClientResponse response = new CollateralClientResponse();
      response.setData(false);
      when(applicationDraftRepository.findByBpmId(anyString())).thenReturn(Optional.empty());
      when(applicationCreditRepository.countApplicationCreditByBpmId(anyString(), anyString())).thenReturn(1);
      when(collateralClient.checkCollateralAssetStatus(anyString(), anyInt())).thenReturn(response);

      // Happy case
      Assertions.assertThrows(ApprovalException.class, ()-> verifyServiceImpl.verifyAssetInfoStatus("151-00006180"));
    }
  }
}

