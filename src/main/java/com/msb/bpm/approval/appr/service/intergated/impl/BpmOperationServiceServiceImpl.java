package com.msb.bpm.approval.appr.service.intergated.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.bpm.BpmOperationClient;
import com.msb.bpm.approval.appr.client.checklist.ChecklistClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.constant.IntegrationConstant;
import com.msb.bpm.approval.appr.enums.application.AddressType;
import com.msb.bpm.approval.appr.enums.application.ApplicationStatus;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.application.GuaranteeForm;
import com.msb.bpm.approval.appr.enums.bpm.CheckListCode;
import com.msb.bpm.approval.appr.enums.bpm.ProcessName;
import com.msb.bpm.approval.appr.enums.bpm.RelationType;
import com.msb.bpm.approval.appr.enums.card.IntegrationStatus;
import com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.mapper.BpmOperationMapper;
import com.msb.bpm.approval.appr.model.dto.bpm.operation.ApplicantBuild;
import com.msb.bpm.approval.appr.model.dto.bpm.operation.LoanBuild;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.FileDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.*;
import com.msb.bpm.approval.appr.model.request.bpm.operation.*;
import com.msb.bpm.approval.appr.model.response.bpm.operation.AsyncBpmStatusCredit;
import com.msb.bpm.approval.appr.model.response.bpm.operation.ClientResponse;
import com.msb.bpm.approval.appr.model.response.bpm.operation.DataBpmOperationResponse;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.repository.ApplicationCreditRepository;
import com.msb.bpm.approval.appr.repository.ApplicationHistoricIntegrationRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.intergated.BpmOperationService;
import com.msb.bpm.approval.appr.util.DateUtils;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LOAN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.OVERDRAFT;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class BpmOperationServiceServiceImpl implements BpmOperationService {

    private final BpmOperationClient bpmOperationClient;
    private final ChecklistClient checklistClient;
    private final CustomerRepository customerRepository;
    private final BpmOperationMapper bpmOperationMapper;
    private final ApplicationRepository applicationRepository;
    private final ApplicationCreditRepository applicationCreditRepository;
    private final ObjectMapper objectMapper;
    private final CollateralClient collateralClient;


    private final ApplicationHistoricIntegrationRepository integrationRepository;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  
    private static final String[] GUARANTOR_RELATION = {"V003", "V004", "V005", "V006", "V007", "V008", "V009", "V010", "V011", "V012", "V016", "R88", "R89", "R90", "R91", "R92", "R93", "R94", "R95", "R96", "R97", "R98", "R85", "R101", "R102", "R103", "R104"};
    private static final String[] SPOUSE_RELATION = {"V001", "V002", "R87"};


    @Override
    @Async
    @Transactional
    public void syncBpmOperation(ApplicationEntity entityApp, boolean isRetry) {
        if (!ApplicationStatus.isDone(entityApp.getStatus())) {
            return;
        }

        ApplicationEntity applicationEntity = applicationRepository.findByBpmId(entityApp.getBpmId())
                .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));

        Set<ApplicationCreditEntity> syncCredits = applicationEntity.getCredits().stream()
                .filter(credit ->
//                        GuaranteeForm.UNSECURE.getCode().equals(credit.getGuaranteeForm()) &&
                         (LOAN.equalsIgnoreCase(credit.getCreditType()) || OVERDRAFT.equalsIgnoreCase(credit.getCreditType()))
                        && "Y".equals(credit.getApproveResult())
                        && (credit.getCreditLoan() == null || credit.getCreditLoan().getAcfNo() == null)
                        && (credit.getCreditOverdraft() == null || credit.getCreditOverdraft().getAcfNo() == null)
                        && (credit.getCreditCard() == null || credit.getCreditCard().getContractL() == null)
                )
                .collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(syncCredits)) {
            createClient(applicationEntity, syncCredits, isRetry);
        }
    }

    public List<ApplicationHistoricIntegration> createClient(ApplicationEntity entityApp, Set<ApplicationCreditEntity> credits, boolean isRetry) {
        List<ApplicationHistoricIntegration> historicIntegrations =
                integrationRepository.findByBpmIdAndIntegratedSystem(entityApp.getBpmId(), IntegrationConstant.BPM_OPERATION)
                        .orElse(null);

        if (CollectionUtils.isEmpty(historicIntegrations)) {
            ApplicationHistoricIntegration applicationInfo = composeBpmInfo(entityApp);
            List<ApplicationHistoricIntegration> integrations = new ArrayList<>();
            credits.forEach(credit ->
                    integrations.add(ApplicationHistoricIntegration.builder()
                            .applicationId(entityApp.getId())
                            .bpmId(applicationInfo.getBpmId())
                            .cif(applicationInfo.getCif())
                            .applicationCreditId(credit.getId())
                            .firstName(applicationInfo.getFirstName())
                            .lastName(applicationInfo.getLastName())
                            .creditType(credit.getCreditType())
                            .businessUnit(entityApp.getBusinessUnit())
                            .documentCode(credit.getDocumentCode())
                            .guaranteeForm(credit.getGuaranteeFormValue())
                            .loanAmount(credit.getLoanAmount())
                            .effectiveDate(DateUtils.getByPeriodTime(entityApp.getUpdatedAt(), entityApp.getEffectivePeriod(), entityApp.getEffectivePeriodUnit())
                                    .toLocalDate())
                            .integratedStatus(IntegrationStatus.INPROGESS.getValue())
                            .integratedSystem(IntegrationConstant.BPM_OPERATION)
                            .identifierCode(applicationInfo.getIdentifierCode())
                            .createdAt(LocalDateTime.now())
                            .createdBy(entityApp.getCreatedBy())
                            .build())
            );
            historicIntegrations = integrationRepository.saveAll(integrations);
        }

        ClientResponse responseBpmOperation = bpmOperationClient.syncApplication(buildRequestClientBpm(entityApp, credits));

        historicIntegrations.forEach(historicIntegration -> {
            if (responseBpmOperation == null
                    || !IntegrationConstant.SUCCESS_STATUS.equals(responseBpmOperation.getCode())) {
                historicIntegration.setIntegratedStatus(IntegrationStatus.ERR.getValue());
                historicIntegration.setErrorCode(responseBpmOperation == null ? "" : responseBpmOperation.getCode());
                historicIntegration.setErrorDescription(responseBpmOperation == null ? "" : responseBpmOperation.getMessage());
                historicIntegration.setIntegratedStatusDetail(IntegrationStatusDetail.ANOTHER_ERROR.getValue());
            } else {
                historicIntegration.setIntegratedStatus(IntegrationStatus.SUCESSFULL.getValue());
                historicIntegration.setIntegratedStatusDetail(IntegrationStatus.SUCESSFULL.getValue());
                historicIntegration.setErrorCode("");
                historicIntegration.setErrorDescription("");
            }
            historicIntegration.setUpdatedBy(isRetry ? SecurityContextUtil.getUsername() : null);
            historicIntegration.setUpdatedAt(isRetry ? LocalDateTime.now() : null);
        });

        return integrationRepository.saveAll(historicIntegrations);
    }

    public ApplicationHistoricIntegration composeBpmInfo(ApplicationEntity application) {
        ApplicationHistoricIntegration historicIntegration = new ApplicationHistoricIntegration();
        CustomerEntity customerEntity = application.getCustomer();
        IndividualCustomerEntity individualCustomer = customerEntity.getIndividualCustomer();

        historicIntegration.setBpmId(application.getBpmId());
        historicIntegration.setCif(customerEntity.getCif());
        historicIntegration.setLastName(individualCustomer.getLastName());
        historicIntegration.setFirstName(individualCustomer.getFirstName());
        historicIntegration.setIdentifierCode(getIdentifierCode(customerEntity).getIdentifierCode());
        historicIntegration.setCreatedBy(SecurityContextUtil.getUsername());

        return historicIntegration;
    }

    public CustomerIdentityEntity getIdentifierCode(CustomerEntity entity) {
        return entity.getCustomerIdentitys().stream()
                .filter(item -> item.isPriority())
                .findFirst()
                .orElse(null);
    }

    public CustomerAddressEntity getPermanentAddress(CustomerEntity customer) {
        return customer.getCustomerAddresses().stream()
                .filter(address -> AddressType.HK_THUONG_TRU.getValue().equals(address.getAddressType()))
                .findFirst()
                .orElse(null);
    }

    public SynApplicationRequest buildRequestClientBpm(ApplicationEntity entityApp, Set<ApplicationCreditEntity> credits) {

        ProcessInstanceEntity processInstance = entityApp.getProcessInstance();

        CustomerEntity customer = entityApp.getCustomer();
        Set<ApplicationCreditConditionsEntity> creditConditions = entityApp.getCreditConditions();

        List<Long> idCreditConditions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(creditConditions)) {
            idCreditConditions.addAll(creditConditions.stream()
                    .map(ApplicationCreditConditionsEntity::getCreditConditionId)
                    .collect(Collectors.toList()));
        }
        // Collateral asset
        DataBpmOperationResponse dataBpmOperationResponse = getDataBpmOperation(entityApp.getBpmId());
        // Kiểm tra hình thức đảm bảo trong khoản vay?
        int countGuaranteeForm = applicationCreditRepository.countApplicationCreditByBpmId(entityApp.getBpmId(), GuaranteeForm.COLLATERAL.getCode());
        // Kiểm tra TSĐB có được khai báo không?
        boolean existCollateralAsset = ObjectUtils.isNotEmpty(dataBpmOperationResponse.getCollateralCommonRequests());

        boolean isChangeBusinessType = countGuaranteeForm > 0 || (countGuaranteeForm == 0 && existCollateralAsset);

        List<LoanInfo> loanData = buildLoan(entityApp, customer, credits);
        List<LoanCollateralMapping> loanCollateralMappings = dataBpmOperationResponse.getLoanCollateralMappings();

        loanCollateralMappings.removeIf(loanCollateral -> loanData.stream().noneMatch(loanInfo -> loanInfo.getApprovalLoanId().equals(loanCollateral.getLoanId())));

        return new SynApplicationRequest().withAssignee(entityApp.getCreatedBy())
                .withBusinessType(isChangeBusinessType ? "RB_HL_OPS" : "RB_SBO_OPS")
                .withRequestCode(entityApp.getBpmId())
//                .withApprovalProcessId(isChangeBusinessType ? String.valueOf(entityApp.getId()): processInstance.getProcessInstanceId())
                .withApprovalProcessId(String.valueOf(entityApp.getId()))
                .withApprovalBpmProcessDefinitionId(processInstance.getProcessDefinitionId())
                .withApprovalFromTaskDefinitionKey(processInstance.getTaskDefinitionKey())
                .withApprovalFromTaskId(processInstance.getTaskId())
                .withBusinessUnit(entityApp.getBusinessUnit())
                .withProcessName(ProcessName.getValue(entityApp.getProcessFlow()))
                .withApprovalOrganizationCodes(Arrays.asList(entityApp.getBranchCode(), entityApp.getBusinessCode()))
                .withApprovalAt(entityApp.getUpdatedAt().format(formatter))
                .withApprovalNum(entityApp.getApprovalDocumentNo()) // ID luồng phê duyệt
                .withLoans(loanData)
                .withApplicants(buildApplicantInfo(entityApp, customer))
                .withFiles(buildFiles(entityApp.getBpmId()))
                .withCollateralCommonRequests(
                        dataBpmOperationResponse.getCollateralCommonRequests())
                .withLoanCollateralMappings(loanCollateralMappings)
                .withApplicantCollateralMappings(dataBpmOperationResponse.getApplicantCollateralMappings())
                .withSegment(entityApp.getSegment())
                .withReferenceRequestCode(entityApp.getReferenceRequestCode())
                .withApprovalConditionIds(idCreditConditions)
                ;
    }

    public List<FileInfo> buildFiles(String requestCode) {
        try {

            ChecklistBaseResponse<GroupChecklistDto> checklistBaseResponse = checklistClient.getChecklistByRequestCode(requestCode);
            if (!IntegrationConstant.CHECKLIST_SUCCESS_CODE.equals(checklistBaseResponse.getStatus().getCode())) {
                return Collections.emptyList();
            }
            GroupChecklistDto checklistGroup = checklistBaseResponse.getData();

            List<ChecklistDto> checklists = checklistGroup.getListChecklist().stream()
                    .filter(checkList -> CheckListCode.isTypeAccepted(checkList.getCode())
                            && !CollectionUtils.isEmpty(checkList.getListFile()))
                    .collect(Collectors.toList());

            List<FileInfo> fileInfo = new ArrayList<>();
            checklists.stream().forEach(checklist -> {
                List<FileDto> listFile = checklist.getListFile();
                fileInfo.addAll(listFile.stream()
                        .filter(Objects::nonNull)
                        .map(this::buildFileDetail).collect(Collectors.toList()));
            });

            return fileInfo;
        } catch (Exception ex) {
            log.error("Call get info file from checklist error: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    public FileInfo buildFileDetail(FileDto fileDto) {
        return new FileInfo().withName(fileDto.getFileName())
                .withPath(fileDto.getMinioPath())
                .withType(IntegrationConstant.BBPD);
    }

    public List<ApplicantInfo> buildApplicantInfo(ApplicationEntity entityApp, CustomerEntity customer) {
        log.info("buildApplicantInfo info START with application=[{}]", entityApp.getBpmId());

        ApplicantBuild applicantBuild = new ApplicantBuild().withApp(entityApp).withCust(customer);
        List<ApplicantInfo> applicants = buildApplicant(applicantBuild);
        Set<CustomerRelationShipEntity> customerRelationShips = customer.getCustomerRelationShips();

        if (!CollectionUtils.isEmpty(customerRelationShips)) {
            List<ApplicantInfo> applicantRelation = new ArrayList<>();
            customerRelationShips.stream()
                    .filter(relationShip -> StringUtils.isNotEmpty(relationShip.getRelationship()))
                    .forEach(customerRelationShip -> {
                        CustomerEntity customerRelation = customerRepository.findById(customerRelationShip.getCustomerRefId())
                                .orElse(new CustomerEntity());
                        if (CustomerType.RB.name().equalsIgnoreCase(customerRelation.getCustomerType())) {
                            applicantRelation.addAll(buildApplicant(applicantBuild.withCust(customerRelation)
                                    .withRelation(customerRelationShip)));
                        }
                    });
            applicants.addAll(applicantRelation.stream()
                    .filter(applicant -> RelationType.SPOUSE.name().equals(applicant.getRelationType()))
                    .collect(Collectors.toList()));
            List<ApplicantInfo> collect = applicantRelation.stream()
                    .filter(applicant ->
                            RelationType.GUARANTOR.name().equals(applicant.getRelationType())
//                                    && !CollectionUtils.isEmpty(applicant.getIncomes())
                    )
                    .sorted(Comparator.comparing(ApplicantInfo::getOrderDisplay))
                    .collect(Collectors.toList());
            applicants.addAll(collect);
        }

        IntStream.range(0, applicants.size()).forEach(idx -> applicants.get(idx).setIndex(idx));

        return applicants;
    }

    public List<ApplicantInfo> buildApplicant(ApplicantBuild applicantBuild) {
        List<ApplicantInfo> applicants = new ArrayList<>();
        CustomerEntity customer = applicantBuild.getCust();

        applicantBuild = applicantBuild.withAddress(getPermanentAddress(customer))
                .withIdentity(getIdentifierCode(customer))
                .withIndividual(customer.getIndividualCustomer());

        if (Objects.isNull(applicantBuild.getRelation())) {

            applicants.add(buildApplicantDetail(applicantBuild.withRelationType(RelationType.CUSTOMER)
                    .withOwner(true)));
        } else if (Arrays.stream(SPOUSE_RELATION)
                .anyMatch(applicantBuild.getRelation().getRelationship()::equals)) {
            applicants.add(buildApplicantDetail(applicantBuild.withRelationType(RelationType.SPOUSE)));

        } else if (Arrays.stream(GUARANTOR_RELATION)
                .anyMatch(applicantBuild.getRelation().getRelationship()::equals)) {
            applicants.add(buildApplicantDetail(applicantBuild.withRelationType(RelationType.GUARANTOR)));
        }
        return applicants;
    }

    public ApplicantInfo buildApplicantDetail(ApplicantBuild applicantBuild) {
        return bpmOperationMapper.mapApplicantInfo(applicantBuild);
    }

    public List<LoanInfo> buildLoan(ApplicationEntity entityApp, CustomerEntity customer, Set<ApplicationCreditEntity> credits) {
        log.info("buildLoan info START with application=[{}]", entityApp.getBpmId());

        List<LoanInfo> loans = new ArrayList<>();

        credits.forEach(credit -> {

            if (LOAN.equalsIgnoreCase(credit.getCreditType())) {
                loans.add(bpmOperationMapper.mapLoanInfo(new LoanBuild().withApp(entityApp)
                        .withCustomer(customer)
                        .withCredit(credit)
                        .withCreditLoan(credit.getCreditLoan())
                        .withIndex(loans.size())));
            }
            if (OVERDRAFT.equalsIgnoreCase(credit.getCreditType())) {
                loans.add(bpmOperationMapper.mapOverdraftInfo(new LoanBuild().withApp(entityApp)
                        .withCustomer(customer)
                        .withCredit(credit)
                        .withCreditOverdraft(credit.getCreditOverdraft())
                        .withIndex(loans.size())));
            }
        });
        AtomicInteger index = new AtomicInteger(0);
        return loans.stream().sorted(Comparator.comparing(LoanInfo::getApprovalLoanId)).map(loanInfo -> {
            loanInfo.setIndex(index.getAndIncrement());
            loanInfo.setName(BpmOperationMapper.PREFIX_LOAN_NAME.concat(" ").concat(String.valueOf(loanInfo.getIndex() + 1)));
            return loanInfo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatusCredit(String creditInfo) {
        try {
            AsyncBpmStatusCredit bpmStatusCredit =
                    objectMapper.readValue(creditInfo, AsyncBpmStatusCredit.class);
            Optional<ApplicationCreditEntity> credit =
                    applicationCreditRepository.findById(bpmStatusCredit.getApprovalLoanId());
            if (!credit.isPresent()) {
                log.error("Consumer: not found credit info by id: {} ", bpmStatusCredit.getApprovalLoanId());
                return;
            }
            log.info("bpm ops update status application credit =[{}]", bpmStatusCredit.getApprovalLoanId());

            ApplicationCreditEntity creditEntity = credit.get();
            if (LOAN.equalsIgnoreCase(creditEntity.getCreditType())) {
                ApplicationCreditLoanEntity creditLoan = creditEntity.getCreditLoan();
                creditLoan.setAccountNo(bpmStatusCredit.getAccountNo());
                creditLoan.setAcfNo(bpmStatusCredit.getAcfNo());
                creditLoan.setStatus(bpmStatusCredit.getStatus());
                creditEntity.setCreditLoan(creditLoan);
            } else if (OVERDRAFT.equalsIgnoreCase(creditEntity.getCreditType())) {
                ApplicationCreditOverdraftEntity creditOverdraft =
                        creditEntity.getCreditOverdraft();
                creditOverdraft.setAccountNo(bpmStatusCredit.getAccountNo());
                creditOverdraft.setAcfNo(bpmStatusCredit.getAcfNo());
                creditOverdraft.setStatus(bpmStatusCredit.getStatus());
                creditEntity.setCreditOverdraft(creditOverdraft);
            }
            applicationCreditRepository.save(creditEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private DataBpmOperationResponse getDataBpmOperation(String bpmId) {
        return collateralClient.getDataRequestBpmOperation(bpmId);
    }
}
