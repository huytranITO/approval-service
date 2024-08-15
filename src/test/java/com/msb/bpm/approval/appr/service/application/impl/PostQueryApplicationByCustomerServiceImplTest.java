package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_QUERY_APPLICATION_BY_CUSTOMER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.request.query.PostQueryApplicationRequest;
import com.msb.bpm.approval.appr.model.response.customer.CustomerBaseResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomerDetailResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse.CustomersRBResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse.CustomersRBResponse.CustomerResponse;
import com.msb.bpm.approval.appr.model.response.customer.CustomersResponse.CustomersRBResponse.IdentityDocuments;
import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV2Response;
import com.msb.bpm.approval.appr.repository.ApplicationHistoryApprovalRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 25/10/2023, Wednesday
 **/
@ExtendWith(MockitoExtension.class)
class PostQueryApplicationByCustomerServiceImplTest {

  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private ApplicationHistoryApprovalRepository applicationHistoryApprovalRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private CustomerClient customerClient;
  @InjectMocks
  private PostQueryApplicationByCustomerServiceImpl postQueryApplicationByCustomerService;

  @Test
  void test_getType_should_be_ok() {
    assertEquals(POST_QUERY_APPLICATION_BY_CUSTOMER,
        postQueryApplicationByCustomerService.getType());
  }

  @ParameterizedTest
  @CsvSource({"RB", "UNDEFINED"})
  void test_getQueryApplicationByCusDto_should_be_ok(String customerType) {
    ApplicationEntity entity = getData(customerType);

    if ("RB".equalsIgnoreCase(customerType)) {
      assertNotNull(postQueryApplicationByCustomerService.getQueryApplicationByCusDto(entity,
          entity.getCustomer()));
    } else if ("UNDEFINED".equalsIgnoreCase(customerType)) {
      try {
        postQueryApplicationByCustomerService.getQueryApplicationByCusDto(entity,
            entity.getCustomer());
      } catch (ApprovalException ex) {
        assertNotNull(ex);
      }
    }
  }

  @ParameterizedTest
  @CsvSource({",", "cif,username", "cif,"})
  void test_execute_should_be_ok(String cif, String username) {
    PostQueryApplicationRequest request = new PostQueryApplicationRequest();
    request.setUsername(username);
    List<ApplicationEntity> listApp = new ArrayList<>();
    ApplicationEntity applicationEntity = getData("RB");
    listApp.add(applicationEntity);
    List<Long> customerIds = new ArrayList<>();
    customerIds.add(421L);
    customerIds.add(2068L);
    CustomerBaseResponse<SearchCustomerV2Response> response = new CustomerBaseResponse<>();
    SearchCustomerV2Response searchCustomerV2Response = new SearchCustomerV2Response();
    CustomerDetailResponse customerDetailResponse = new CustomerDetailResponse();
    customerDetailResponse.setId(1L);
    searchCustomerV2Response.setRelatedCustomerIds(customerIds);
    searchCustomerV2Response.setCustomer(customerDetailResponse);
    response.setData(searchCustomerV2Response);
    if (cif != null) {
      request.setBpmCifs(Collections.singletonList(cif));
    }

    if (CollectionUtils.isEmpty(request.getBpmCifs())) {
      try {
        postQueryApplicationByCustomerService.execute(request);
      } catch (ApprovalException ex) {
        assertNotNull(ex);
      }
    } else if (StringUtils.isEmpty(username)) {
      when(customerClient.searchCustomerDetail(any(), any())).thenReturn(response);
      when(customerRepository.searchCustomerIdsByBpmCifs(any())).thenReturn(
          Optional.of(customerIds));
      when(applicationRepository.findByCustomerRefCustomerIdIn(any())).thenReturn(Optional.of(Collections.singletonList(getData(
          CustomerType.RB.name()))));
      assertNotNull(postQueryApplicationByCustomerService.execute(request));
    } else {
      when(applicationHistoryApprovalRepository.getApplicationIdList(request.getUsername(),
          ProcessingRole.PD_RB_RM)).thenReturn(customerIds);
      when(customerRepository.searchCustomerIdsByBpmCifs(any())).thenReturn(
          Optional.of(customerIds));
      when(applicationRepository.findByIdInAndCustomerRefCustomerIdIn(any(), any())).thenReturn(
          Optional.of(listApp));
      assertNotNull(postQueryApplicationByCustomerService.execute(request));
    }
  }

  private ApplicationEntity getData(String customerType) {
    ApplicationEntity entity = new ApplicationEntity();
    CustomerEntity customer = new CustomerEntity();
    customer.setCustomerType(customerType);

    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    individualCustomer.setLastName("lastName");
    individualCustomer.setFirstName("firstName");
    customer.setIndividualCustomer(individualCustomer);

    entity.setProcessingRole(ProcessingRole.PD_RB_CA1.name());
    entity.setCustomer(customer);

    ApplicationHistoryApprovalEntity historyApproval = new ApplicationHistoryApprovalEntity();
    historyApproval.setUserRole(ProcessingRole.PD_RB_BM);
    historyApproval.setUsername("rm");
    entity.getHistoryApprovals().add(historyApproval);

    historyApproval = new ApplicationHistoryApprovalEntity();
    historyApproval.setFromUserRole(ProcessingRole.PD_RB_BM);
    historyApproval.setUserRole(ProcessingRole.PD_RB_CA1);
    historyApproval.setUsername("bm");
    entity.getHistoryApprovals().add(historyApproval);

    return entity;
  }
}
