package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CREATE_APPLICATION;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.enums.application.ProcessingStep.MAKE_PROPOSAL;
import static com.msb.bpm.approval.appr.enums.application.SubmissionPurpose.NEW_LEVEL;
import static com.msb.bpm.approval.appr.enums.camunda.CamundaTopic.CHECK_WORKFLOW;

import com.msb.bpm.approval.appr.chat.events.CreateGroupEvent;
import com.msb.bpm.approval.appr.client.camunda.CamundaProperties;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.customer.request.SearchCusRelationshipRequest;
import com.msb.bpm.approval.appr.client.customer.response.CusRelationForSearchResponse;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.UserManagementClient;
import com.msb.bpm.approval.appr.enums.application.AddressType;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.application.GeneratorStatusEnums;
import com.msb.bpm.approval.appr.enums.application.ProcessInstanceType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import com.msb.bpm.approval.appr.model.dto.CustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.ProcessInstanceEntity;
import com.msb.bpm.approval.appr.model.request.flow.PostCreateApplicationRequest;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.usermanager.OrganizationTreeDetail;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea.DataResponse;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.service.checklist.impl.ChecklistServiceImpl;
import com.msb.bpm.approval.appr.service.idgenerate.IDSequenceService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.util.CamundaUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostCreateApplicationServiceImpl extends AbstractBaseService implements
    BaseService<ApplicationEntity, PostCreateApplicationRequest> {

  private final ApplicationRepository applicationRepository;
  private final UserManagerClient userManagerClient;
  private final MessageSource messageSource;
  private final ChecklistServiceImpl checklistService;
  private final PostInitializeInfoServiceImpl postInitializeInfoService;
  private final CustomerRepository customerRepository;
  private final CommonService commonService;
  private final CamundaService camundaService;
  private final CamundaProperties camundaProperties;
  private final UserManagementClient userManagementClient;
  private final IDSequenceService idSequenceService;
  private final CustomerClient customerClient;
  private final ApplicationEventPublisher applicationEventPublisher;


  @Override
  public String getType() {
    return POST_CREATE_APPLICATION;
  }

  @Override
  @Transactional
  public ApplicationEntity execute(PostCreateApplicationRequest request, Object... objects) {
    log.info("execute START with request={}, objects={}", request, objects);

    CustomerEntity entityCus = persistCustomer(request.getCustomer());

    // Save Application
    // gen bpm_id
    String bpmId = idSequenceService.generateBpmId();
    ApplicationEntity entityApp = new ApplicationEntity().withBpmId(bpmId)
        .withRefId(bpmId);
    entityApp.setCustomer(entityCus);
    entityApp.setSubmissionPurpose(NEW_LEVEL.getCode());
    entityApp.setSubmissionPurposeValue(messageSource.getMessage(NEW_LEVEL.getText(), null, Util.locale()));
    DataResponse regionAreaResp = userManagerClient.getRegionAreaByUserName(SecurityContextUtil.
        getCurrentUser());
    if (Objects.isNull(regionAreaResp)) {
        throw new ApprovalException(DomainCode.ERR_GET_ORGANIZATION_BY_USER, new Object[]{request});
    }
    entityApp.setCreatedFullName(regionAreaResp.getCreatedFullName());
    entityApp.setCreatedPhoneNumber(regionAreaResp.getCreatedPhoneNumber());
    entityApp.setBusinessUnit(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ?
        regionAreaResp.getBusinessUnitDetail().getName() : null);
    entityApp.setBusinessCode(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ?
        regionAreaResp.getBusinessUnitDetail().getCode() : null);
    entityApp.setAreaCode(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ? regionAreaResp.getBusinessUnitDetail().getBusinessAreaCode() : "");
    entityApp.setArea(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ? regionAreaResp.getBusinessUnitDetail().getBusinessAreaFullName() : "");

    if (Objects.nonNull(regionAreaResp.getRegionDetail())) {
      entityApp.setRegion(regionAreaResp.getRegionDetail().getFullName());
      entityApp.setRegionCode(regionAreaResp.getRegionDetail().getCode());
    }

    OrganizationTreeDetail branchDetail = Util.getTreeDetailByType("CN",
        regionAreaResp.getRegionDetail());
    if (Objects.nonNull(branchDetail)) {
      entityApp.setBranchCode(branchDetail.getCode());
      entityApp.setBranchName(branchDetail.getFullName());
    }
    entityApp.setProcessingRole(PD_RB_RM.name());
    entityApp.setProcessingStepCode(MAKE_PROPOSAL.getCode());
    entityApp.setProcessingStep(messageSource.getMessage(MAKE_PROPOSAL.getValue(), null, Util.locale()));
    entityApp.setAssignee(SecurityContextUtil.getCurrentUser());
    entityApp.setGeneratorStatus(GeneratorStatusEnums.DEFAULT.getValue());

    // Camunda start instance workflow
    startCamundaInstance(entityApp);

    // get sale code from user management
    entityApp.setSaleCode(userManagementClient.getSaleCode());

    applicationRepository.saveAndFlush(entityApp);
    commonService.saveDraft(bpmId);
    checklistService.generateChecklist(entityApp);
    log.info("execute END with request={}, objects={}, result={}", request, objects, entityApp);

    log.info("START SmartChat CreateGroup Event with applicationId={}",
            entityApp.getBpmId());
    applicationEventPublisher.publishEvent(new CreateGroupEvent(this,entityApp.getBpmId()));

    return entityApp;
  }

  @Transactional
  public CustomerEntity persistCustomer(CustomerDTO customerDTO) {
    customerDTO.setRefCusId(UUID.randomUUID().toString());

    // Get latest customer by refCustomerId
    CustomerEntity latestCustomerEntity = Objects.nonNull(customerDTO.getRefCustomerId())
        ? customerRepository.findFirstByRefCustomerIdOrderByCreatedAtDesc(
            customerDTO.getRefCustomerId())
        .orElse(null)
        : null;

    if (latestCustomerEntity == null) {
      return persistCustomerWithoutCustomerRelationLatest(customerDTO);
    }

    // Get customer relation list
    Set<CustomerRelationShipEntity> customerRelationShipEntities = latestCustomerEntity.getCustomerRelationShips();

    if (CollectionUtils.isEmpty(customerRelationShipEntities)) {
      return persistCustomerWithoutCustomerRelationLatest(customerDTO);
    }

    Map<String, Set<CustomerDTO>> latestCustomerRelationMap = getCustomerRelations(customerRelationShipEntities);

    Set<CustomerDTO> rbCustomerRelations = latestCustomerRelationMap.get(CustomerType.RB.name());

    if (CollectionUtils.isEmpty(rbCustomerRelations)) {
      return persistCustomerWithoutCustomerRelationLatest(customerDTO);
    } else {
      mappingRelationCommon(customerDTO, rbCustomerRelations);
    }

    return persistCustomerWithCustomerRelationLatest(customerDTO, rbCustomerRelations);
  }

  @Transactional
  public CustomerEntity persistCustomerWithCustomerRelationLatest(CustomerDTO customerDTO, Set<CustomerDTO> customerRelations) {

    customerRelations.forEach(c -> {
      c.setId(null);
      if (CustomerType.RB.name().equalsIgnoreCase(c.getCustomerType())) {
        c.setRefCusId(UUID.randomUUID().toString());
      }
      c.getAddresses().forEach(a -> a.setId(null));
      c.getIdentities().forEach(i -> i.setId(null));
    });

    Set<CustomerDTO> finalCustomerRelations = postInitializeInfoService.persistCustomerFlushAll(customerRelations);

    return postInitializeInfoService.persistCustomerWithRelation(customerDTO, finalCustomerRelations);
  }

  private CustomerEntity persistCustomerWithoutCustomerRelationLatest(CustomerDTO customerDTO) {
    // Customer
    CustomerEntity entityCus = new CustomerEntity()
        .withRefCusId(customerDTO.getRefCusId())
        .withRefCustomerId(customerDTO.getRefCustomerId())
        .withBpmCif(customerDTO.getBpmCif())
        .withCif(customerDTO.getCif())
        .withCustomerType(customerDTO.getCustomerType())
        .withVersion(customerDTO.getVersion());
    entityCus.setCreatedAt(LocalDateTime.now());
    // Individual Customer
    IndividualCustomerEntity indiCus = new IndividualCustomerEntity()
        .withFirstName(CustomerMapper.INSTANCE.splitFirstName((IndividualCustomerDTO) customerDTO))
        .withLastName(CustomerMapper.INSTANCE.splitLastName((IndividualCustomerDTO) customerDTO))
        .withGender(((IndividualCustomerDTO) customerDTO).getGender())
        .withDateOfBirth(((IndividualCustomerDTO) customerDTO).getDateOfBirth())
        .withAge(((IndividualCustomerDTO) customerDTO).getAge())
        .withMartialStatus(((IndividualCustomerDTO) customerDTO).getMartialStatus())
        .withNation(((IndividualCustomerDTO) customerDTO).getNation())
        .withSubject(((IndividualCustomerDTO) customerDTO).getSubject())
        .withMsbMember(((IndividualCustomerDTO) customerDTO).isMsbMember())
        .withEmployeeCode(((IndividualCustomerDTO) customerDTO).getEmployeeCode())
        .withGenderValue(((IndividualCustomerDTO) customerDTO).getGenderValue())
        .withMartialStatusValue(((IndividualCustomerDTO) customerDTO).getMartialStatusValue())
        .withNationValue(((IndividualCustomerDTO) customerDTO).getNationValue())
        .withEmail(((IndividualCustomerDTO) customerDTO).getEmail())
        .withPhoneNumber(((IndividualCustomerDTO) customerDTO).getPhoneNumber());
    entityCus.setIndividualCustomer(indiCus);
    // Customer Identity
    List<CustomerIdentityEntity> lstEntityCusIdentity = new ArrayList<>();
    for (CustomerIdentityDTO item : customerDTO.getIdentities()) {
      CustomerIdentityEntity entity = new CustomerIdentityEntity()
          .withDocumentType(item.getDocumentType())
          .withIdentifierCode(item.getIdentifierCode())
          .withIssuedAt(item.getIssuedAt())
          .withIssuedBy(item.getIssuedBy())
          .withIssuedPlace(item.getIssuedPlace())
          .withPriority(item.isPriority())
          .withDocumentTypeValue(item.getDocumentTypeValue())
          .withIssuedByValue(item.getIssuedByValue())
          .withIssuedPlaceValue(item.getIssuedPlaceValue())
          .withOrderDisplay(item.getOrderDisplay())
          .withRefIdentityId(item.getRefIdentityId());
      entity.setCustomer(entityCus);
      entity.setCreatedAt(LocalDateTime.now());
      lstEntityCusIdentity.add(entity);
    }

    // Customer address
    List<CustomerAddressEntity> lstEntityCusAddress = new ArrayList<>();
    for (CustomerAddressDTO item : customerDTO.getAddresses()) {
      CustomerAddressEntity entity = new CustomerAddressEntity()
          .withAddressType(item.getAddressType())
          .withSamePermanentResidence(item.isSamePermanentResidence())
          .withCanDelete(!AddressType.HK_THUONG_TRU.getValue().equals(item.getAddressType()));
      entity.setCustomer(entityCus);
      entity.setCreatedAt(LocalDateTime.now());
      entity.setCityCode(item.getCityCode());
      entity.setDistrictCode(item.getDistrictCode());
      entity.setWardCode(item.getWardCode());
      entity.setAddressLine(item.getAddressLine());
      entity.setAddressTypeValue(item.getAddressTypeValue());
      entity.setCityValue(item.getCityValue());
      entity.setDistrictValue(item.getDistrictValue());
      entity.setWardValue(item.getWardValue());
      entity.setAddressLinkId(item.getAddressLinkId());
      entity.setOrderDisplay(item.getOrderDisplay());
      entity.setRefAddressId(item.getRefAddressId());
      lstEntityCusAddress.add(entity);
    }

    entityCus.setCustomerIdentitys(new HashSet<>(lstEntityCusIdentity));
    entityCus.setCustomerAddresses(new HashSet<>(lstEntityCusAddress));

    return entityCus;
  }

  public void startCamundaInstance(ApplicationEntity entityApp) {
    String processDefinitionKey = camundaProperties.getSubscriptions().get(CHECK_WORKFLOW.name())
        .getProcessDefinitionKey();

    ProcessInstanceWithVariablesDto returnVar = camundaService.startProcessInstance(entityApp.getBpmId(),
        processDefinitionKey, null);

    TaskDto nextTask = (TaskDto) returnVar.getVariables().get("nextTask").getValue();

    entityApp.setStatus(CamundaUtil.getStatus(returnVar.getVariables()));
    entityApp.setProcessInstance(new ProcessInstanceEntity().withProcessInstanceType(
            ProcessInstanceType.MANUAL).withProcessInstanceId(returnVar.getId())
        .withProcessDefinitionId(returnVar.getDefinitionId())
        .withProcessBusinessKey(returnVar.getBusinessKey())
        .withProcessKey(processDefinitionKey)
        .withWorkflowVersion(CamundaUtil.getWorkflowVersion(returnVar.getVariables()))
            .withTaskId(nextTask.getId())
            .withTaskDefinitionKey(nextTask.getTaskDefinitionKey())
        .withCreatedAt(LocalDateTime.now()));
  }

  private void mappingRelationCommon(CustomerDTO customerDTO, Set<CustomerDTO> rbCustomerRelations) {
    SearchCusRelationshipRequest request = SearchCusRelationshipRequest.builder()
            .customerId(customerDTO.getRefCustomerId())
            .build();

    CustomerBaseResponse<CusRelationForSearchResponse> relations = customerClient.searchCustomerRelationship(request);
    if(ObjectUtils.isNotEmpty(relations)
            && ObjectUtils.isNotEmpty(relations.getData())
            && CollectionUtils.isNotEmpty(relations.getData().getRelationships())) {
      rbCustomerRelations.forEach(rl -> {
        List<CusRelationForSearchResponse.Relationship> relationshipList = relations.getData().getRelationships().stream()
                .filter(rel -> rl.getRefCustomerId().equals(rel.getRelatedCustomerId()))
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(relationshipList)
                && relationshipList.size() == 1) {
          List<CusRelationForSearchResponse.RelationshipDetail> relationshipDetail = relationshipList.get(0).getRelationshipDetails();
          if(CollectionUtils.isNotEmpty(relationshipDetail)
                  && relationshipDetail.size() == 1) {
            rl.setRelationship(relationshipDetail.get(0).getRelatedDetail());
            rl.setRelationshipRefId(relationshipDetail.get(0).getId());
          } else {
            rl.setRelationship(null);
            rl.setRelationshipRefId(null);
          }
        } else {
          rl.setRelationship(null);
          rl.setRelationshipRefId(null);
        }
      });
    } else {
      rbCustomerRelations.forEach(rel -> {
        rel.setRelationship(null);
        rel.setRelationshipRefId(null);
      });
    }
  }
}
