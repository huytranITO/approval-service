package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.EB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.enums.application.ApplicationStatus.isComplete;
import static com.msb.bpm.approval.appr.exception.DomainCode.APPLICATION_PREVIOUSLY_CLOSED;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;
import static com.msb.bpm.approval.appr.exception.DomainCode.NUMBER_OF_CONTACT_ERROR;
import static com.msb.bpm.approval.appr.exception.DomainCode.USER_DONT_HAVE_PERMISSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.usermanager.UserManagerClient;
import com.msb.bpm.approval.appr.client.usermanager.v2.UserManagementClient;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.factory.ApplicationIncomeReverseFactory;
import com.msb.bpm.approval.appr.factory.CustomerFactory;
import com.msb.bpm.approval.appr.mapper.ApplicationContactMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationMapper;
import com.msb.bpm.approval.appr.mapper.CicMapper;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO.AmlOprDtl;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.EnterpriseCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.InitializeInfoDTO;
import com.msb.bpm.approval.appr.model.dto.application.ApplicationContactDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationContactEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.EnterpriseCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.request.customer.CustomerSearchVersion;
import com.msb.bpm.approval.appr.model.request.customer.FindByListCustomerRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV3Response;
import com.msb.bpm.approval.appr.model.response.usermanager.OrganizationTreeDetail;
import com.msb.bpm.approval.appr.model.response.usermanager.UserManagerRegionArea;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.msb.bpm.approval.appr.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostInitializeInfoServiceImpl extends AbstractBaseService implements
    BaseService<Object, PostInitializeInfoRequest> {

  private final CustomerRepository customerRepository;
  private final ApplicationRepository applicationRepository;
  private final UserManagementClient userManagementClient;
  private final UserManagerClient userManagerClient;
  private final ObjectMapper objectMapper;
  private final CustomerClient customerClient;
  @Override
  public String getType() {
    return POST_INITIALIZE_INFO;
  }

  @Override
  @Transactional
  public Object execute(PostInitializeInfoRequest request, Object... objects) {

    log.info("PostInitializeInfoServiceImpl.execute() start with bpmId : {} , request : [{}]",
        objects[0], JsonUtil.convertObject2String(request, objectMapper));

    return Optional.of(applicationRepository.findByBpmId((String) objects[0]))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(applicationEntity -> {

          if (!SecurityContextUtil.getCurrentUser()
              .equalsIgnoreCase(applicationEntity.getAssignee())) {
            log.error("User {} don't have permission to execute application {}",
                SecurityContextUtil.getCurrentUser(), objects[0]);
            throw new ApprovalException(USER_DONT_HAVE_PERMISSION);
          }

          if (isComplete(applicationEntity.getStatus())) {
            log.error("Application {} is {} , process interrupted", objects[0],
                applicationEntity.getStatus());
            throw new ApprovalException(APPLICATION_PREVIOUSLY_CLOSED);
          }

          checkRegionArea(applicationEntity);
          checkEnterpriseCustomer(request.getCustomerAndRelationPerson().getEnterpriseRelations());
          // Mapping enterprise customer common
          mappingEnterpriseCustomerCommon(request.getCustomerAndRelationPerson().getEnterpriseRelations());


          Set<CustomerDTO> combineRelations = persistCustomerRelationsFlushAll(request.getCustomerAndRelationPerson()
              .getCustomerRelations(), request.getCustomerAndRelationPerson().getEnterpriseRelations());

          CustomerEntity customer = persistCustomerWithRelation(request.getCustomerAndRelationPerson().getCustomer(), combineRelations);

          applicationEntity.setCustomer(customer);

          Map<Long, Long> customerRefCustomerIdMap = combineRelations.stream()
              .filter(filter -> filter.getRefCustomerId() != null)
              .collect(Collectors.toMap(CustomerDTO::getRefCustomerId, CustomerDTO::getId));
          customerRefCustomerIdMap.put(customer.getRefCustomerId(), customer.getId());

          saveData(applicationEntity, request, customerRefCustomerIdMap);

          Set<CustomerDTO> customerRelationSaved = combineRelations
              .stream()
              .filter(cr -> RB.equalsIgnoreCase(cr.getCustomerType()))
              .collect(Collectors.toSet());
          Set<CustomerDTO> enterpriseRelationSaved = combineRelations
              .stream()
              .filter(cr -> EB.equalsIgnoreCase(cr.getCustomerType()))
              .collect(Collectors.toSet());

          log.info("PostInitializeInfoServiceImpl.execute() end with bpmId : {}", objects[0]);

          return getInitializeInfoDTO(applicationEntity, customerRelationSaved, enterpriseRelationSaved);

        })
        .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION));
  }

  private void mappingEnterpriseCustomerCommon(Set<CustomerDTO> enterpriseRelations) {
    if(!ObjectUtils.isEmpty(enterpriseRelations)) {
      Map<Long, Integer> listCusEBs = enterpriseRelations.stream()
              .collect(Collectors.toMap(CustomerDTO::getRefCustomerId, CustomerDTO::getVersion));
      if (!CollectionUtils.isEmpty(listCusEBs)) {
        List<CustomerSearchVersion> customerSearchVersions = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : listCusEBs.entrySet()) {
          customerSearchVersions.add(CustomerSearchVersion.builder()
                  .id(entry.getKey())
                  .version(entry.getValue())
                  .build());
        }
        FindByListCustomerRequest request = new FindByListCustomerRequest();
        final int NUMBER_REQUEST_LIMIT = 10;
        int size = Math.abs(customerSearchVersions.size()/NUMBER_REQUEST_LIMIT);
        for (int batchCurrent = 0; batchCurrent <= size; batchCurrent++) {
          List<CustomerSearchVersion> listBatchRequest = null;
          if(batchCurrent == size) {
            listBatchRequest = customerSearchVersions.subList(NUMBER_REQUEST_LIMIT * batchCurrent, customerSearchVersions.size());
          } else {
            listBatchRequest = customerSearchVersions.subList(NUMBER_REQUEST_LIMIT * batchCurrent, NUMBER_REQUEST_LIMIT * (batchCurrent + 1));
          }
          request.setCustomers(listBatchRequest);
          log.info("Call get EB common info with batch current {} and request : {}", batchCurrent, JsonUtil.convertObject2String(request, objectMapper));
          CustomerBaseResponse<SearchCustomerV3Response> cusDataSearch = customerClient.findByListCustomerCommon(request);

          if(!ObjectUtils.isEmpty(cusDataSearch)
                  && !ObjectUtils.isEmpty(cusDataSearch.getData())
                  && !ObjectUtils.isEmpty(cusDataSearch.getData().getCustomers())) {

            // Mapping info
            enterpriseRelations.forEach(er -> {
              List<SearchCustomerV3Response.CustomersEBResponse> dataCommonList = cusDataSearch.getData().getCustomers().stream().filter(cm -> !ObjectUtils.isEmpty(cm.getCustomer())
                      && cm.getCustomer().getId().equals(er.getRefCustomerId())
                      && !ObjectUtils.isEmpty(cm.getCustomer().getVersion())
                      && cm.getCustomer().getVersion().equals(er.getVersion())).collect(Collectors.toList());
              if(!CollectionUtils.isEmpty(dataCommonList)) {
                er.setCif(dataCommonList.stream().findFirst().get().getCustomer().getCif());
                er.setBpmCif(dataCommonList.stream().findFirst().get().getCustomer().getBpmCif());
                er.setIssuedBy(dataCommonList.stream().findFirst().get().getCustomer().getIssuedBy());
                er.setIssuedAt(dataCommonList.stream().findFirst().get().getCustomer().getIssuedDate());
                er.setRefCustomerId(dataCommonList.stream().findFirst().get().getCustomer().getId());
                er.setIssuedPlaceValue(dataCommonList.stream().findFirst().get().getCustomer().getIssuedPlace());
                er.setIdentities(CustomerMapper.INSTANCE.toListCustomerIdentityCommonDTO(dataCommonList.stream().findFirst().get().getIdentityDocuments()));
                er.setAddresses(CustomerMapper.INSTANCE.toListCustomerAddressCommonDTO(dataCommonList.stream().findFirst().get().getAddresses()));
              }
            });
          }
        }
      }
    }
  }

  @Transactional
  public void saveData(ApplicationEntity applicationEntity, PostInitializeInfoRequest request, Map<Long, Long> customerRefCustomerIdMap) {

    applicationEntity.setSegment(request.getApplication().getSegment());
    applicationEntity.setApprovalType(request.getApplication().getApprovalType());
    applicationEntity.setProcessFlow(request.getApplication().getProcessFlow());
    applicationEntity.setProcessFlowValue(request.getApplication().getProcessFlowValue());
    applicationEntity.setSubmissionPurpose(request.getApplication().getSubmissionPurpose());
    applicationEntity.setSubmissionPurposeValue(request.getApplication().getSubmissionPurposeValue());
    applicationEntity.setRiskLevel(request.getApplication().getRiskLevel());
    applicationEntity.setPartnerCode(request.getApplication().getPartnerCode());
    applicationEntity.setSaleCode(userManagementClient.getSaleCode());
    applicationEntity.setRmStatus(request.getApplication().isRmStatus());

    referenceApplicationIncome(applicationEntity, request.getIncomes(), customerRefCustomerIdMap);

    referenceCicData(applicationEntity, request.getCustomerAndRelationPerson().getCic());

    referenceAmlOprData(applicationEntity, request.getCustomerAndRelationPerson().getAmlOpr());

    referenceApplicationContact(applicationEntity, request.getCustomerAndRelationPerson().getApplicationContact());

    applicationRepository.saveAndFlush(applicationEntity);
  }

  @Transactional
  public void referenceApplicationIncome(ApplicationEntity applicationEntity,
      Set<ApplicationIncomeDTO> incomes, Map<Long, Long> customerRefCustomerIdMap) {
    if (CollectionUtils.isEmpty(incomes)) {
      applicationEntity.getIncomes().clear();
      return;
    }

    Set<ApplicationIncomeEntity> postIncomes = incomes
        .stream()
        .map(income -> {
          ApplicationIncomeEntity incomeEntity = ApplicationIncomeReverseFactory.getApplicationIncomeReverse(
              income.getIncomeRecognitionMethod()).build(income);

          incomeEntity.getSalaryIncomes().forEach(salaryIncomeEntity -> salaryIncomeEntity.setCustomerId(
              customerRefCustomerIdMap.get(salaryIncomeEntity.getRefCustomerId())));

          incomeEntity.getRentalIncomes().forEach(rentalIncomeEntity -> rentalIncomeEntity.setCustomerId(
              customerRefCustomerIdMap.get(rentalIncomeEntity.getRefCustomerId())));
          
          incomeEntity.getIndividualEnterpriseIncomes().forEach(
              individualEnterpriseIncomeEntity -> individualEnterpriseIncomeEntity.setCustomerId(
                  customerRefCustomerIdMap.get(
                      individualEnterpriseIncomeEntity.getRefCustomerId())));

          incomeEntity.getOtherIncomes().forEach(otherIncomeEntity -> otherIncomeEntity.setCustomerId(
              customerRefCustomerIdMap.get(otherIncomeEntity.getRefCustomerId())));

          incomeEntity.getPropertyBusinessIncomes().forEach(propertyBusinessIncomeEntity -> propertyBusinessIncomeEntity.setCustomerId(
                  customerRefCustomerIdMap.get(propertyBusinessIncomeEntity.getRefCustomerId())));

          return incomeEntity;
        })
        .collect(Collectors.toSet());

    Set<ApplicationIncomeEntity> applicationIncomeCreateSet = postIncomes
        .stream()
        .filter(applicationIncome -> Objects.isNull(applicationIncome.getId()))
        .collect(Collectors.toSet());

    Set<ApplicationIncomeEntity> applicationIncomeUpdateSet = postIncomes
        .stream()
        .filter(applicationIncome -> Objects.nonNull(applicationIncome.getId()))
        .collect(Collectors.toSet());

    applicationEntity.updateApplicationIncomes(applicationIncomeUpdateSet);

    applicationEntity.addApplicationIncomes(applicationIncomeCreateSet);

    applicationEntity.setTotalIncome(calculateTotalIncome(incomes));
  }

  @Transactional
  public void referenceCicData(ApplicationEntity applicationEntity, CicDTO cicDTO) {
    if (Objects.isNull(cicDTO) || CollectionUtils.isEmpty(cicDTO.getCicDetails())) {
      applicationEntity.getCics().clear();
      return;
    }

    Set<CicEntity> cicEntities = CicMapper.INSTANCE.toCicEntities(cicDTO.getCicDetails())
        .stream()
        .filter(filterCic -> Objects.nonNull(filterCic.getId()))
        .collect(Collectors.toSet());

    if (CollectionUtils.isEmpty(cicEntities)) {
      return;
    }

    cicEntities.forEach(cicEntity -> cicEntity.setExplanation(cicDTO.getExplanation()));
    applicationEntity.updateCicEntities(cicEntities);
  }

  @Transactional
  public void referenceAmlOprData(ApplicationEntity applicationEntity, AmlOprDTO amlOpr) {
    Set<AmlOprDtl> combineAmlOprs = new HashSet<>();

    if (amlOpr == null) {
      applicationEntity.getAmlOprs().clear();
      return;
    }

    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(amlOpr.getAmls())) {
      combineAmlOprs.addAll(amlOpr.getAmls());
    }

    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(amlOpr.getOprs())) {
      combineAmlOprs.addAll(amlOpr.getOprs());
    }

    if (CollectionUtils.isEmpty(combineAmlOprs)) {
      applicationEntity.getAmlOprs().clear();
      return;
    }

    applicationEntity.removeAmlOprEntities(combineAmlOprs);
  }

  @Transactional
  public CustomerEntity persistCustomerWithRelation(CustomerDTO customerDTO,
      Set<CustomerDTO> customerRelations) {
    CustomerEntity customerEntity = preCreateUpdate(customerDTO);

    referenceCustomerRelationship(customerEntity, customerRelations);

    return customerEntity;
  }

  @Transactional
  public Set<CustomerDTO> persistCustomerRelationsFlushAll(Set<CustomerDTO> customerRelations, Set<CustomerDTO> enterpriseRelations) {

    Set<CustomerDTO> combineRelations = new HashSet<>();
    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(customerRelations)) {
      combineRelations.addAll(customerRelations);
    }

    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(enterpriseRelations)) {
      combineRelations.addAll(enterpriseRelations);
    }

    if (CollectionUtils.isEmpty(combineRelations)) {
      return Collections.emptySet();
    }

    return persistCustomerFlushAll(combineRelations);
  }

  @Transactional
  public Set<CustomerDTO> persistCustomerFlushAll(Set<CustomerDTO> customers) {
    Set<CustomerEntity> customerEntities = new HashSet<>();

    if (!CollectionUtils.isEmpty(customers)) {
      customers.forEach(customerDTO -> customerEntities.add(preCreateUpdate(customerDTO)));

      customerRepository.saveAllAndFlush(customerEntities);

      Set<CustomerDTO> responses = new HashSet<>();

      customerEntities.forEach(customer -> responses.add(
          CustomerFactory.getCustomer(customer.getCustomerType()).build(customer)));

      return responses;
    }

    return Collections.emptySet();
  }

  private CustomerEntity preCreateUpdate(CustomerDTO customerDTO) {
    return Objects.nonNull(customerDTO.getId())
        ? updateCustomer(customerDTO)
        : createCustomerCreate(customerDTO);
  }

  private CustomerEntity createCustomerCreate(CustomerDTO customerDTO) {
    CustomerEntity customerCreate = new CustomerEntity();

    buildPersistCustomer(customerCreate, customerDTO);

    return customerCreate;
  }

  private CustomerEntity updateCustomer(CustomerDTO customerDTO) {
    return Optional.of(customerRepository.findById(customerDTO.getId()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(customerUpdate -> {
          buildPersistCustomer(customerUpdate, customerDTO);

          return customerUpdate;
        })
        .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_CUSTOMER));
  }

  private void buildPersistCustomer(CustomerEntity customer, CustomerDTO customerDTO) {
    initPersistCustomer(customer, customerDTO);

    referenceCustomerAddressToCustomerEntity(customer, customerDTO.getAddresses());

    referenceCustomerIdentityToCustomerEntity(customer, customerDTO.getIdentities());

  }

  private void initPersistCustomer(CustomerEntity customerEntity, CustomerDTO customerDTO) {
    CustomerMapper.INSTANCE.referenceCustomer(customerEntity, customerDTO);

    if (RB.equalsIgnoreCase(customerDTO.getCustomerType())) {
      IndividualCustomerEntity individualCustomer =
          Objects.nonNull(customerEntity.getIndividualCustomer())
              ? customerEntity.getIndividualCustomer() : new IndividualCustomerEntity();

      CustomerMapper.INSTANCE.referenceIndividualCustomer(individualCustomer,
          (IndividualCustomerDTO) customerDTO);

      customerEntity.setIndividualCustomer(individualCustomer);
    } else if (EB.equalsIgnoreCase(customerDTO.getCustomerType())) {
      EnterpriseCustomerEntity enterpriseCustomer =
          Objects.nonNull(customerEntity.getEnterpriseCustomer())
              ? customerEntity.getEnterpriseCustomer() : new EnterpriseCustomerEntity();

      CustomerMapper.INSTANCE.referenceEnterpriseCustomer(enterpriseCustomer,
          (EnterpriseCustomerDTO) customerDTO);
      if(customerDTO != null && !ObjectUtils.isEmpty(customerDTO.getIdentities())) {
        List<CustomerIdentityDTO> tmp = customerDTO.getIdentities().stream().filter(id -> id.isPriority()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(tmp)) {
          enterpriseCustomer.setBusinessRegistrationNumber(tmp.get(0).getIdentifierCode());
        }
      }
      customerEntity.setEnterpriseCustomer(enterpriseCustomer);
    }
  }

  private void referenceCustomerAddressToCustomerEntity(CustomerEntity customerEntity,
      Set<CustomerAddressDTO> addresses) {
    if (CollectionUtils.isEmpty(addresses)) {
      customerEntity.getCustomerAddresses().clear();
      return;
    }

    Set<CustomerAddressEntity> addressSet =
        CustomerMapper.INSTANCE.toCustomerAddressEntities(addresses);

    Set<CustomerAddressEntity> addressCreateSet = addressSet
        .stream()
        .filter(addr -> Objects.isNull(addr.getId()))
        .collect(Collectors.toSet());

    Set<CustomerAddressEntity> addressUpdateSet = addressSet
        .stream()
        .filter(addr -> Objects.nonNull(addr.getId()))
        .collect(Collectors.toSet());

    customerEntity.updateCustomerAddresses(addressUpdateSet);

    customerEntity.addCustomerAddresses(addressCreateSet);
  }

  private void referenceCustomerIdentityToCustomerEntity(CustomerEntity customerEntity,
      Set<CustomerIdentityDTO> identities) {
    if (CollectionUtils.isEmpty(identities)) {
      customerEntity.getCustomerIdentitys().clear();
      return;
    }

    Set<CustomerIdentityEntity> identitySet =
        CustomerMapper.INSTANCE.toCustomerIdentityEntities(identities);

    Set<CustomerIdentityEntity> identityCreateSet = identitySet
        .stream()
        .filter(identity -> Objects.isNull(identity.getId()))
        .collect(Collectors.toSet());

    Set<CustomerIdentityEntity> identityUpdateSet = identitySet
        .stream()
        .filter(identity -> Objects.nonNull(identity.getId()))
        .collect(Collectors.toSet());

    customerEntity.updateCustomerIdentities(identityUpdateSet);

    customerEntity.addCustomerIdentities(identityCreateSet);
  }

  private void referenceCustomerRelationship(CustomerEntity customerEntity,
      Set<CustomerDTO> customerRelations) {

    if (CollectionUtils.isEmpty(customerRelations)) {
      removeCustomerRelationshipEntities(customerEntity, customerEntity.getCustomerRelationShips());
      return;
    }

    Map<Long, CustomerRelationShipEntity> relationShipMap = customerEntity.getCustomerRelationShips()
        .stream()
        .collect(
            Collectors.toMap(CustomerRelationShipEntity::getCustomerRefId, Function.identity()));

    customerRelations.forEach(cr -> {
      if (relationShipMap.containsKey(cr.getId())) {
        CustomerRelationShipEntity relationShipEntity = relationShipMap.get(cr.getId());
        relationShipEntity.setRelationship(cr.getRelationship());
        relationShipEntity.setRelationshipValue(cr.getRelationshipValue());
        relationShipEntity.setRelationshipRefId(cr.getRelationshipRefId());
        relationShipEntity.setPaymentGuarantee(cr.getPaymentGuarantee());
        relationShipEntity.setCustomer(customerEntity);
      } else {
        CustomerRelationShipEntity obj = new CustomerRelationShipEntity()
            .withCustomer(customerEntity)
            .withCustomerRefId(cr.getId())
            .withRelationship(cr.getRelationship())
            .withRelationshipValue(cr.getRelationshipValue())
                .withRelationshipRefId(cr.getRelationshipRefId())
                .withPaymentGuarantee(cr.getPaymentGuarantee());
        customerEntity.getCustomerRelationShips().add(obj);
      }
    });

    Set<Long> cusIds = customerRelations
        .stream()
        .map(CustomerDTO::getId)
        .collect(Collectors.toSet());
    Set<CustomerRelationShipEntity> removeCustomerRelationshipSet = customerEntity.getCustomerRelationShips()
        .stream()
        .filter(cr -> !cusIds.contains(cr.getCustomerRefId()))
        .collect(Collectors.toSet());

    removeCustomerRelationshipEntities(customerEntity, removeCustomerRelationshipSet);
  }

  private void removeCustomerRelationshipEntities(CustomerEntity customerEntity,
      Set<CustomerRelationShipEntity> removeCustomerRelationshipSet) {
    if (CollectionUtils.isEmpty(removeCustomerRelationshipSet)) {
      return;
    }

    List<Long> customerRefIds = removeCustomerRelationshipSet.stream()
        .map(CustomerRelationShipEntity::getCustomerRefId).collect(Collectors.toList());

    customerRepository.deleteAllById(customerRefIds);

    customerEntity.removeCustomerRelationshipEntities(removeCustomerRelationshipSet);
  }

  @Transactional(readOnly = true)
  public InitializeInfoDTO getInitializeInfoDTO(ApplicationEntity applicationEntity, Set<CustomerDTO> customerRelationSaved,
      Set<CustomerDTO> enterpriseRelationSaved) {


    return new InitializeInfoDTO()
        .withApplication(ApplicationMapper.INSTANCE.toDTO(applicationEntity))
        .withCustomerAndRelationPerson(new CustomerAndRelationPersonDTO()
            .withCustomer(CustomerFactory.getCustomer(applicationEntity.getCustomer().getCustomerType()).build(applicationEntity.getCustomer()))
            .withCustomerRelations(customerRelationSaved)
            .withEnterpriseRelations(enterpriseRelationSaved)
            .withApplicationContact(ApplicationContactMapper.INSTANCE.entityToDTO(applicationEntity.getContact()))
            .withCic(buildCicData(applicationEntity.getCics()))
            .withAmlOpr(buildAmlOpr(applicationEntity.getAmlOprs())))
        .withIncomes(buildIncomes(applicationEntity.getIncomes()));
  }

  public void referenceApplicationContact(ApplicationEntity applicationEntity, Set<ApplicationContactDTO> applicationContact) {
    if (CollectionUtils.isEmpty(applicationContact)) {
      applicationEntity.removeAllContact();
    } else {

      if (applicationContact.size() > 2) {
        throw new ApprovalException(NUMBER_OF_CONTACT_ERROR);
      }

      Set<ApplicationContactEntity> collection = new HashSet<>();
      applicationContact.forEach(item -> {
        ApplicationContactEntity contactEntity = new ApplicationContactEntity();
        contactEntity.setApplication(applicationEntity);
        contactEntity.setPhoneNumber(item.getPhoneNumber());
        contactEntity.setFullName(item.getFullName());
        contactEntity.setRelationship(item.getRelationship());
        contactEntity.setRelationshipTxt(item.getRelationshipTxt());
        contactEntity.setOrderDisplay(item.getOrderDisplay());
        collection.add(contactEntity);
      });
      applicationEntity.addContact(collection);
    }
  }

  private void checkRegionArea(ApplicationEntity applicationEntity) {
    if(ProcessingRole.PD_RB_RM.name().equals(applicationEntity.getProcessingRole()) && StringUtils.isBlank(applicationEntity.getBusinessCode())) {
      UserManagerRegionArea.DataResponse regionAreaResp = userManagerClient.getRegionAreaByUserName(applicationEntity.getAssignee());
      if (Objects.isNull(regionAreaResp)) {
        throw new ApprovalException(DomainCode.ERR_GET_ORGANIZATION_BY_USER, new Object[]{applicationEntity});
      }
      applicationEntity.setCreatedFullName(regionAreaResp.getCreatedFullName());
      applicationEntity.setCreatedPhoneNumber(regionAreaResp.getCreatedPhoneNumber());
      applicationEntity.setBusinessUnit(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ?
              regionAreaResp.getBusinessUnitDetail().getName() : null);
      applicationEntity.setBusinessCode(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ?
              regionAreaResp.getBusinessUnitDetail().getCode() : null);

      applicationEntity.setAreaCode(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ? regionAreaResp.getBusinessUnitDetail().getBusinessAreaCode() : "");
      applicationEntity.setArea(Objects.nonNull(regionAreaResp.getBusinessUnitDetail()) ? regionAreaResp.getBusinessUnitDetail().getBusinessAreaFullName() : "");


      if (Objects.nonNull(regionAreaResp.getRegionDetail())) {
        applicationEntity.setRegion(regionAreaResp.getRegionDetail().getFullName());
        applicationEntity.setRegionCode(regionAreaResp.getRegionDetail().getCode());
      }

      OrganizationTreeDetail branchDetail = Util.getTreeDetailByType("CN",
              regionAreaResp.getRegionDetail());
      if (Objects.nonNull(branchDetail)) {
        applicationEntity.setBranchCode(branchDetail.getCode());
        applicationEntity.setBranchName(branchDetail.getFullName());
      }
    }
  }

  /***
   * validate EnterpriseCustomer
   * @param enterpriseRelations
   */
  private void checkEnterpriseCustomer(Set<CustomerDTO> enterpriseRelations) {
    if(enterpriseRelations.isEmpty())
      return;
    List<EnterpriseCustomerEntity> customerEntities = new ArrayList<>();

    // Extract EnterpriseCustomer from dto
    enterpriseRelations.forEach(customerDTO ->{
      CustomerEntity customerEntity = new CustomerEntity();
      CustomerMapper.INSTANCE.referenceCustomer(customerEntity, customerDTO);
        if (EB.equalsIgnoreCase(customerDTO.getCustomerType())) {

          EnterpriseCustomerEntity enterpriseCustomer =
                  Objects.nonNull(customerEntity.getEnterpriseCustomer())
                          ? customerEntity.getEnterpriseCustomer() : new EnterpriseCustomerEntity();
          CustomerMapper.INSTANCE.referenceEnterpriseCustomer(enterpriseCustomer,
                  (EnterpriseCustomerDTO) customerDTO);
          if (enterpriseCustomer != null) {
            customerEntities.add(enterpriseCustomer);
          }
        }
      });
    // validate duplicated data
    Set<String> isDuplicate = new HashSet<>();
    customerEntities.forEach(x -> {
      if(!StringUtils.isEmpty(x.getBusinessRegistrationNumber())){
        if(isDuplicate.contains(x.getBusinessRegistrationNumber())){
          throw new ApprovalException(DomainCode.ERR_DUPLICATE_BUSINESSNUMBER);
        }else {
          isDuplicate.add(x.getBusinessRegistrationNumber());
        }
      }
    });

  }
}
