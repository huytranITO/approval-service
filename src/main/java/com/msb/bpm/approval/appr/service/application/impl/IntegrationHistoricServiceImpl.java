package com.msb.bpm.approval.appr.service.application.impl;

import com.msb.bpm.approval.appr.constant.IntegrationConstant;
import com.msb.bpm.approval.appr.enums.card.IntegrationResponseCode;
import com.msb.bpm.approval.appr.enums.card.IntegrationStatus;
import com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.mapper.ApplicationHistoricIntegrationMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationHistoricIntegrationDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.request.IntegrationRetryRequest;
import com.msb.bpm.approval.appr.model.request.IntegrationRetryRequest.RetryRequest;
import com.msb.bpm.approval.appr.model.response.integration.ApplicationHistoricIntegrationResponse;
import com.msb.bpm.approval.appr.model.search.FullSearch;
import com.msb.bpm.approval.appr.repository.ApplicationHistoricIntegrationRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.application.IntegrationHistoricService;
import com.msb.bpm.approval.appr.service.intergated.HandleManualRetryService;
import com.msb.bpm.approval.appr.util.CriteriaUtil;
import com.msb.bpm.approval.appr.util.PageUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class IntegrationHistoricServiceImpl implements IntegrationHistoricService {

  private static final String SUB_CARD = "Sub Card";

  @Autowired
  ApplicationHistoricIntegrationRepository integrationRepository;

  @Autowired
  ApplicationRepository applicationRepository;

  @Autowired
  HandleManualRetryService handleManualRetryService;

  @Autowired
  CriteriaUtil criteriaUtil;

  @Value("${card.client.errors}")
  private String[] clientErrors;
  @Value("${card.credit.errors}")
  private String[] creditErrors;

  @Override
  @Transactional(readOnly = true)
  public ApplicationHistoricIntegrationResponse getHistoricIntegration(int page, int size,
      String sortField, int sortOrder) {
    log.info("GET_APPLICATION_HISTORIC_INTEGRATION  BEGIN");
    ApplicationHistoricIntegrationResponse response = new ApplicationHistoricIntegrationResponse();
    List<String> bpmIds = criteriaUtil.findGroupedByBpmId(sortField, sortOrder);

    List<ApplicationHistoricIntegrationDTO> dtoList = bpmIds.stream().map(bpmId -> {
      ApplicationHistoricIntegrationDTO integrationDTO = new ApplicationHistoricIntegrationDTO();

      ApplicationEntity entity = applicationRepository.findByBpmId(bpmId)
          .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION));
      CustomerEntity customerEntity = entity.getCustomer();
      IndividualCustomerEntity individualCustomer = customerEntity.getIndividualCustomer();

      List<ApplicationHistoricIntegration> integrationEntities = integrationRepository.findByBpmId(
              bpmId)
          .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_HISTORIC_INTEGRATION));
      ApplicationHistoricIntegration firstEntity = integrationEntities.get(0);
      BigDecimal totalAmount = integrationEntities.stream().filter(
              integratioEntity -> integratioEntity.getLoanAmount() != null && (
                  StringUtils.isBlank(integratioEntity.getCardType())
                      || !integratioEntity.getCardType().equals(SUB_CARD)))
          .map(ApplicationHistoricIntegration::getLoanAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      integrationDTO.setBpmId(bpmId);
      integrationDTO.setFullName(Util.convertFullName(individualCustomer.getFirstName(),
          individualCustomer.getLastName()));

      integrationDTO.setCif(firstEntity.getCif());
      integrationDTO.setIdentifierCode(firstEntity.getIdentifierCode());
      integrationDTO.setLoanAmount(totalAmount);

      integrationDTO.setDocumentCode(entity.getRegulatoryCode());
      integrationDTO.setBusinessUnit(entity.getBusinessUnit());
      integrationDTO.setCreatedBy(entity.getCreatedBy());
      IntegrationStatus integrationStatus = integrationStatus(integrationEntities);
      integrationDTO.setIntegratedStatus(integrationStatus.name());

      return integrationDTO;
    }).collect(Collectors.toList());
    int first = page * size;
    if (first >= dtoList.size() && page != 0) {
      throw new ApprovalException(DomainCode.INVALID_DATA_SIZE);
    }

    response.setTotalElements(dtoList.size());
    response.setPage(page);
    response.setSize(size);

    List<ApplicationHistoricIntegrationDTO> item = dtoList.stream().skip(first).limit(size)
        .collect(Collectors.toList());
    response.setItems(item);

    log.info("GET_APPLICATION_HISTORIC_INTEGRATION  END");
    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationHistoricIntegrationResponse getHistoricIntegrationSearch(String contents,
      int page, int size, String sortField, int sortOrder) {
    log.info("GET_HISTORIC_INTEGRATION_SEARCH  BEGIN contents={}", contents);
    contents = contents.trim();
    ApplicationHistoricIntegrationResponse response = new ApplicationHistoricIntegrationResponse();
    if (StringUtils.isBlank(contents)) {
      throw new ApprovalException(DomainCode.BLANK_SEARCH_INFORMATION);
    }

    List<String> bpmIds = criteriaUtil.findBySearch(contents, sortField, sortOrder);
    List<ApplicationHistoricIntegrationDTO> dtoList = bpmIds.stream().map(bpmId -> {
      ApplicationHistoricIntegrationDTO integrationDTO = new ApplicationHistoricIntegrationDTO();

      ApplicationEntity entity = applicationRepository.findByBpmId(bpmId)
          .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION));
      CustomerEntity customerEntity = entity.getCustomer();
      IndividualCustomerEntity individualCustomer = customerEntity.getIndividualCustomer();

      List<ApplicationHistoricIntegration> integrationEntities = integrationRepository.findByBpmId(
              bpmId)
          .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_HISTORIC_INTEGRATION));

      ApplicationHistoricIntegration firstEntity = integrationEntities.get(0);
      BigDecimal totalAmount = integrationEntities.stream().filter(
              integratioEntity -> integratioEntity.getLoanAmount() != null && (
                  StringUtils.isBlank(integratioEntity.getCardType())
                      || !integratioEntity.getCardType().equals(SUB_CARD)))
          .map(ApplicationHistoricIntegration::getLoanAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      integrationDTO.setBpmId(bpmId);
      integrationDTO.setFullName(Util.convertFullName(individualCustomer.getFirstName(),
          individualCustomer.getLastName()));
      integrationDTO.setCif(firstEntity.getCif());
      integrationDTO.setIdentifierCode(firstEntity.getIdentifierCode());
      integrationDTO.setLoanAmount(totalAmount);

      integrationDTO.setDocumentCode(entity.getRegulatoryCode());
      integrationDTO.setBusinessUnit(entity.getBusinessUnit());
      integrationDTO.setCreatedBy(entity.getCreatedBy());
      IntegrationStatus integrationStatus = integrationStatus(integrationEntities);
      integrationDTO.setIntegratedStatus(integrationStatus.name());

      return integrationDTO;
    }).collect(Collectors.toList());
    int first = page * size;
    if (first >= dtoList.size() && page != 0) {
      throw new ApprovalException(DomainCode.INVALID_DATA_SIZE);
    }

    response.setTotalElements(dtoList.size());
    response.setPage(page);
    response.setSize(size);
    List<ApplicationHistoricIntegrationDTO> item = dtoList.stream().skip(first).limit(size)
        .collect(Collectors.toList());
    response.setItems(item);

    log.info("GET_HISTORIC_INTEGRATION_SEARCH  BEGIN");
    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ApplicationHistoricIntegrationDTO> getHistoricIntegrationDetail(String bpmId) {
    log.info("GET_HISTORIC_INTEGRATION_DETAIL  BEGIN bpmId={}", bpmId);
    List<ApplicationHistoricIntegration> entities = integrationRepository.findByBpmId(bpmId)
        .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_HISTORIC_INTEGRATION));

    List<ApplicationHistoricIntegrationDTO> integrationDTOS = ApplicationHistoricIntegrationMapper.INSTANCE.toHistoricIntegration(
        entities);
    integrationDTOS = integrationDTOS.stream()
        .map(dto -> {
          Boolean retry = convertStatusRetry(dto.getIntegratedSystem(), dto.getErrorCode(),
              dto.getIntegratedStatus());
          dto.setRetry(retry);
          dto.setErrorCode(convertErrorCode(dto.getErrorCode()));
          return dto;
        })
        .collect(Collectors.toList());
    log.info("GET_HISTORIC_INTEGRATION_SEARCH END");
    return integrationDTOS;
  }

  @Override
  public Object integrationHistoricRetry(IntegrationRetryRequest requestList) {
    log.info("HISTORIC_INTEGRATION_RETRY BEGIN request={}", requestList);
    Set<Long> retryWay4 = new HashSet<>();
    Set<String> retryOperation = new HashSet<>();
    for (RetryRequest retryRequest : requestList.getRetryList()) {
      ApplicationHistoricIntegration applicationInfo = integrationRepository.findById(
              retryRequest.getId())
          .orElseThrow(() -> new ApprovalException(DomainCode.NOT_FOUND_APPLICATION));

      if (Boolean.TRUE.equals(convertStatusRetry(applicationInfo.getIntegratedSystem(),
          applicationInfo.getErrorCode(), applicationInfo.getIntegratedStatus()))) {
        if (applicationInfo.getIntegratedSystem().equals(IntegrationConstant.WAY_4)) {
          retryWay4.add(applicationInfo.getId());
        } else if (applicationInfo.getIntegratedSystem()
            .equals(IntegrationConstant.BPM_OPERATION)) {
          retryOperation.add(applicationInfo.getBpmId());
        }

      } else {
        throw new ApprovalException(DomainCode.ERROR_DATA_RETRY);
      }
    }

    handleManualRetryService.processRetryWay4(retryWay4);
    handleManualRetryService.processRetryOperation(retryOperation);
    log.info("HISTORIC_INTEGRATION_RETRY END");
    return new Object();
  }

  @Override
  @Transactional
  public ApplicationHistoricIntegrationResponse fullSearch(FullSearch fullSearch) {

    Pageable paging = PageUtil.getPageable(fullSearch.getPageable());
    String fullText = fullSearch.getFullText();
    boolean isFirstTime = fullSearch.isFirstTime();

    Specification<ApplicationEntity> rootSpec;

    rootSpec = Specification.where(((root, query, criteriaBuilder) -> {
      query.distinct(true);
      SetJoin<ApplicationEntity, ApplicationHistoricIntegration> integrationSetJoin = root.joinSet(
          "applicationHistoricIntegration");

      Predicate combinePredicate = criteriaBuilder.equal(criteriaBuilder.literal(1),
          criteriaBuilder.literal(1));

      if (isFirstTime) {
        Predicate effectiveDatePredicate = criteriaBuilder.greaterThanOrEqualTo(
            integrationSetJoin.get("effectiveDate"), LocalDate.now());
        Predicate createdByPredicate = criteriaBuilder.equal(
            criteriaBuilder.upper(integrationSetJoin.get("createdBy")),
            SecurityContextUtil.getCurrentUser().toUpperCase());

        combinePredicate = criteriaBuilder.and(effectiveDatePredicate, createdByPredicate);
      }

      if (StringUtils.isNotBlank(fullText)) {
        String fullTextUpper = "%" + fullText.toUpperCase() + "%";
        Predicate bpmIdPredicate = criteriaBuilder.like(
            criteriaBuilder.upper(integrationSetJoin.get("bpmId")), fullTextUpper);
        Predicate cifPredicate = criteriaBuilder.like(
            criteriaBuilder.upper(integrationSetJoin.get("cif")), fullTextUpper);

        Expression<String> fullNameExpression = criteriaBuilder.function("CONCAT", String.class,
            integrationSetJoin.get("lastName"), criteriaBuilder.literal(" "),
            integrationSetJoin.get("firstName"));
        Predicate fullNamePredicate = criteriaBuilder.like(
            criteriaBuilder.upper(fullNameExpression), fullTextUpper);

        combinePredicate = criteriaBuilder.and(
            criteriaBuilder.or(bpmIdPredicate, cifPredicate, fullNamePredicate));
      }

      return criteriaBuilder.and(combinePredicate);
    }));

    Page<ApplicationEntity> pageData = applicationRepository.findAll(rootSpec, paging);

    ApplicationHistoricIntegrationResponse response = new ApplicationHistoricIntegrationResponse();

    response.setSize(pageData.getSize());
    response.setPage(paging.getPageNumber());
    response.setTotalElements((int) pageData.getTotalElements());

    if (CollectionUtils.isEmpty(pageData.getContent())) {
      response.setItems(Collections.emptyList());
      return response;
    }

    List<ApplicationHistoricIntegrationDTO> historicIntegrationDTOList = new ArrayList<>();
    pageData.getContent().forEach(content -> {
      ApplicationHistoricIntegrationDTO historicIntegration = new ApplicationHistoricIntegrationDTO();

      List<ApplicationHistoricIntegration> historicIntegrations = new ArrayList<>(
          content.getApplicationHistoricIntegration());

      ApplicationHistoricIntegration maxHistoricIntegration = historicIntegrations
          .stream().max(Comparator.comparing(ApplicationHistoricIntegration::getCreatedAt))
          .orElse(new ApplicationHistoricIntegration());

      historicIntegration.setBpmId(content.getBpmId());
      historicIntegration.setFullName(Util.convertFullName(content.getCustomer().getIndividualCustomer().getFirstName(),
          content.getCustomer().getIndividualCustomer().getLastName()));
      historicIntegration.setCif(maxHistoricIntegration.getCif());
      historicIntegration.setIdentifierCode(maxHistoricIntegration.getIdentifierCode());

      BigDecimal loanAmount = historicIntegrations.stream().filter(
              item -> item.getLoanAmount() != null && (
                  StringUtils.isBlank(item.getCardType())
                      || !item.getCardType().equals(SUB_CARD)))
          .map(ApplicationHistoricIntegration::getLoanAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      historicIntegration.setLoanAmount(loanAmount);
      historicIntegration.setDocumentCode(maxHistoricIntegration.getDocumentCode());
      historicIntegration.setBusinessUnit(maxHistoricIntegration.getBusinessUnit());
      historicIntegration.setCreatedBy(maxHistoricIntegration.getCreatedBy());
      historicIntegration.setIntegratedStatus(integrationStatus(historicIntegrations).name());
      historicIntegration.setCreatedAt(content.getUpdatedAt());
      historicIntegration.setUpdatedAt(content.getUpdatedAt());

      historicIntegrationDTOList.add(historicIntegration);
    });

    response.setItems(historicIntegrationDTOList);

    return response;
  }

  public IntegrationStatus integrationStatus(
      List<ApplicationHistoricIntegration> integrationEntities) {
    Map<String, List<ApplicationHistoricIntegration>> listMap = integrationEntities.stream()
        .filter(entity -> entity.getIntegratedStatus() != null)
        .collect(Collectors.groupingBy(ApplicationHistoricIntegration::getIntegratedStatus));
    if (listMap.containsKey(IntegrationStatusDetail.ERR_CLIENT.getValue()) || listMap.containsKey(
        IntegrationStatusDetail.ERR_CARD.getValue()) || listMap.containsKey(
        IntegrationStatusDetail.ERR_SUB_CARD.getValue()) || listMap.containsKey(
        IntegrationStatusDetail.ANOTHER_ERROR.getValue()) || listMap.containsKey(
        IntegrationStatus.ERR.getValue())) {
      return IntegrationStatus.ERR;
    } else if (listMap.containsKey(IntegrationStatusDetail.NEW.getValue()) || listMap.containsKey(
        IntegrationStatus.NEW.getValue())) {
      return IntegrationStatus.NEW;
    } else if (listMap.containsKey(IntegrationStatusDetail.INPROGESS_CLIENT.getValue())
        || listMap.containsKey(IntegrationStatusDetail.INPROGESS_CARD.getValue())
        || listMap.containsKey(IntegrationStatusDetail.INPROGESS_SUB_CARD.getValue())
        || listMap.containsKey(IntegrationStatus.INPROGESS.getValue())) {
      return IntegrationStatus.INPROGESS;
    } else if (listMap.containsKey(IntegrationStatusDetail.SUCESSFULL.getValue())
        || listMap.containsKey(IntegrationStatus.SUCESSFULL.getValue())) {
      return IntegrationStatus.SUCESSFULL;
    }
    return IntegrationStatus.ERR;
  }

  public Boolean convertStatusRetry(
      String integratedSystem, String errorCode, String integratedStatus) {
    if (integratedSystem.equalsIgnoreCase(IntegrationConstant.WAY_4)) {
      return Arrays.asList(clientErrors).contains(errorCode)
          || Arrays.asList(creditErrors).contains(errorCode)
          || integratedStatus.equalsIgnoreCase(IntegrationStatusDetail.NEW.getValue())
          || integratedStatus.equalsIgnoreCase(IntegrationStatusDetail.INPROGESS_CLIENT.getValue())
          || integratedStatus.equalsIgnoreCase(IntegrationStatusDetail.INPROGESS_CARD.getValue())
          || integratedStatus.equalsIgnoreCase(
          IntegrationStatusDetail.INPROGESS_SUB_CARD.getValue());
    } else if (integratedSystem.equalsIgnoreCase(IntegrationConstant.BPM_OPERATION) && (StringUtils.isNotBlank(errorCode))) {
        HttpStatus httpStatus = HttpStatus.resolve(Integer.parseInt(errorCode));
      return httpStatus != null && (httpStatus.is5xxServerError()
              || httpStatus.equals(HttpStatus.REQUEST_TIMEOUT)
              || httpStatus.equals(HttpStatus.FORBIDDEN) || httpStatus.equals(HttpStatus.BAD_REQUEST)
      );
    }
    return false;
  }

  public String convertErrorCode(String errorCode) {
    if (StringUtils.isBlank(errorCode)) {
      return null;
    }
    return errorCode
        .equals(String.valueOf(IntegrationResponseCode.CLIENT_IS_EXIST.getCode())) ? null
        : errorCode;
  }
}
