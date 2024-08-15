package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.EB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_QUERY_APPLICATION_BY_CUSTOMER;
import static com.msb.bpm.approval.appr.exception.DomainCode.TYPE_UNSUPPORTED;

import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.factory.CustomerFactory;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.EnterpriseCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.request.customer.SearchByListCustomerRequest;
import com.msb.bpm.approval.appr.model.request.query.PostQueryApplicationRequest;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV2Response;
import com.msb.bpm.approval.appr.model.response.query.QueryApplicationByCusResponse;
import com.msb.bpm.approval.appr.repository.ApplicationHistoryApprovalRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.util.CommonUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 11/5/2023, Thursday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class PostQueryApplicationByCustomerServiceImpl extends AbstractBaseService implements
    BaseService<Object, PostQueryApplicationRequest> {

  private final ApplicationRepository applicationRepository;
  private final ApplicationHistoryApprovalRepository applicationHistoryApprovalRepository;
  private final CustomerClient customerClient;
  private final CustomerRepository customerRepository;
  @Override
  public String getType() {
    return POST_QUERY_APPLICATION_BY_CUSTOMER;
  }

  @Override
  @Transactional(readOnly = true)
  public Object execute(PostQueryApplicationRequest request, Object... obj) {
    log.info("START with request={}, obj={}", request, obj);
    if (CollectionUtils.isEmpty(request.getBpmCifs()) || request.getBpmCifs().stream()
        .noneMatch(StringUtils::isNotBlank)) {
      throw new ApprovalException(DomainCode.INVALID_BPM_CIF_LIST_ERROR, new Object[]{request});
    }
    if (StringUtils.isEmpty(request.getUsername())) {
      return getDataForHistoryApproval(request);
    } else {
      return getDataForCopyApplication(request);
    }
  }

  private Object getDataForHistoryApproval(PostQueryApplicationRequest request) {
    List<QueryApplicationByCusResponse> lstResult = new ArrayList<>();
    Optional<List<ApplicationEntity>> lstAppByCus;
    // Get customerIds by bpmCifs
    List<Long> oldCustomerIds = customerRepository.searchCustomerIdsByBpmCifs(request.getBpmCifs()).orElse(null);
    // Get customerIds active and inactive from customer-additional-info-service
    List<Long> newCustomerIds = getCustomerIdsFromCusInfoService(oldCustomerIds);
    // Search application by newCustomerIds
    lstAppByCus = applicationRepository.findByCustomerRefCustomerIdIn(newCustomerIds);
    if (lstAppByCus.isPresent()) {
      for (ApplicationEntity entityApp : lstAppByCus.get()) {
        lstResult.add(getQueryApplicationByCusDto(entityApp, entityApp.getCustomer()));
      }
    }
    return lstResult.stream()
        .sorted(Comparator.comparing(QueryApplicationByCusResponse::getCreatedAt).reversed());
  }
  private List<Long> getCustomerIdsFromCusInfoService(List<Long> oldCustomerIds) {
    log.info("Start method PostQueryApplicationByCustomerServiceImpl.getCustomerFromCusInfoService with oldCustomerIds {}", oldCustomerIds);
    List<Long> newCustomerIds = new ArrayList<>();
    // Create request to call customer-additional-service
    if (CollectionUtils.isEmpty(oldCustomerIds)) {
      return newCustomerIds;
    }
    oldCustomerIds.stream()
        .filter(Objects::nonNull)
        .forEach(ci -> {
          CustomerBaseResponse<SearchCustomerV2Response> response = customerClient.searchCustomerDetail(ci, null);
          if (Objects.isNull(response) || Objects.isNull(response.getData())) {
            newCustomerIds.add(ci);
          } else {
            if (Objects.nonNull(response.getData().getCustomer())) {
              newCustomerIds.add(response.getData().getCustomer().getId());
            }
            if (CollectionUtils.isNotEmpty(response.getData().getRelatedCustomerIds())) {
              newCustomerIds.addAll(response.getData().getRelatedCustomerIds());
            }
          }
        });
    if (CollectionUtils.isEmpty(newCustomerIds)) {
      return oldCustomerIds;
    }
    log.info("End method PostQueryApplicationByCustomerServiceImpl.getCustomerFromCusInfoService with newCustomerIds {}", newCustomerIds);
    return newCustomerIds;
  }

  private Object getDataForCopyApplication(PostQueryApplicationRequest request) {
    List<QueryApplicationByCusResponse> lstResult = new ArrayList<>();
    List<Long> applicationIdList = applicationHistoryApprovalRepository.getApplicationIdList(
        request.getUsername(), ProcessingRole.PD_RB_RM);
    if (CollectionUtils.isNotEmpty(applicationIdList)) {
      // Get customerIds by bpmCifs
      List<Long> oldCustomerIds = customerRepository.searchCustomerIdsByBpmCifs(request.getBpmCifs()).orElse(null);
      // Get customerIds active and inactive from customer-additional-info-service
      List<Long> newCustomerIds = getCustomerIdsFromCusInfoService(oldCustomerIds);

      Optional<List<ApplicationEntity>> lstAppByCus = applicationRepository.findByIdInAndCustomerRefCustomerIdIn(
          applicationIdList, newCustomerIds);
      if (lstAppByCus.isPresent()) {
        for (ApplicationEntity entityApp : lstAppByCus.get()) {
          lstResult.add(getQueryApplicationByCusDto(entityApp, entityApp.getCustomer()));
        }
      }
      return lstResult.stream()
          .sorted(Comparator.comparing(QueryApplicationByCusResponse::getCreatedAt).reversed());
    }
    return lstResult;
  }

  @Transactional(readOnly = true)
  public QueryApplicationByCusResponse getQueryApplicationByCusDto(ApplicationEntity entity,
      CustomerEntity customer) {
    String cusName;
    String approvalUserName = null;
    String approvalFullName = null;
    String identity = null;
    CustomerDTO cusDto = CustomerFactory.getCustomer(customer.getCustomerType()).build(customer);
    switch (customer.getCustomerType()) {
      case RB:
        cusName = ((IndividualCustomerDTO) cusDto).getFullName();
        break;
      case EB:
        cusName = ((EnterpriseCustomerDTO) cusDto).getCompanyName();
        break;
      default:
        throw new ApprovalException(TYPE_UNSUPPORTED);
    }
    identity = cusDto.getMainIdentity().getIdentifierCode();
    // lấy thông tin cán bộ phê duyệt
    if (Objects.nonNull(entity.getProcessingRole()) && ProcessingRole.isRoleApproval(
        entity.getProcessingRole()) && CollectionUtils.isNotEmpty(entity.getHistoryApprovals())) {
      List<ApplicationHistoryApprovalEntity> approvalHistories = getPreviousHistoryApprovalByRole(
          new ArrayList<>(entity.getHistoryApprovals()),
          ProcessingRole.valueOf(entity.getProcessingRole()));
      if (CollectionUtils.isNotEmpty(approvalHistories)) {
        approvalUserName = approvalHistories.get(0).getUsername();
        approvalFullName = approvalHistories.get(0).getFullName();
      }
    }
    return QueryApplicationByCusResponse.builder()
        .idBpm(null == entity.getBpmId() ? "" : entity.getBpmId())
        .cif(null == cusDto.getCif() ? "" : cusDto.getCif())
        .customerName(null == cusName ? "" : cusName)
        .processingStep(null == entity.getProcessingStep() ? "" : entity.getProcessingStep())
        .status(CommonUtil.getStatus(entity)).createdAt(entity.getCreatedAt())
        .regulatoryCode(null == entity.getRegulatoryCode() ? "" : entity.getRegulatoryCode())
        .proposalApprovalFullName(approvalFullName).proposalApprovalUser(approvalUserName)
        .processingRole(null == entity.getProcessingRole() ? "" : entity.getProcessingRole())
        .identity(identity).build();
  }
}
