package com.msb.bpm.approval.appr.service.application.impl;

import static com.msb.bpm.approval.appr.constant.IntegrationConstant.WAY_4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.ApplicationHistoricIntegrationDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.request.IntegrationRetryRequest;
import com.msb.bpm.approval.appr.model.request.IntegrationRetryRequest.RetryRequest;
import com.msb.bpm.approval.appr.model.response.integration.ApplicationHistoricIntegrationResponse;
import com.msb.bpm.approval.appr.model.search.BaseSearch;
import com.msb.bpm.approval.appr.model.search.FullSearch;
import com.msb.bpm.approval.appr.repository.ApplicationHistoricIntegrationRepository;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.intergated.HandleManualRetryService;
import com.msb.bpm.approval.appr.util.CriteriaUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 21/10/2023, Saturday
 **/
@ExtendWith(MockitoExtension.class)
class IntegrationHistoricServiceImplTest {

  @Mock
  private ApplicationHistoricIntegrationRepository integrationRepository;

  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private HandleManualRetryService handleManualRetryService;

  @Mock
  private CriteriaUtil criteriaUtil;

  @InjectMocks
  private IntegrationHistoricServiceImpl integrationHistoricService;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(integrationHistoricService, "clientErrors",
        new String[]{"2008", "2023", "2014", "2024", "3004", "3003", "2039", "2064", "2065", "2036",
            "2022", "504", "3005"});
    ReflectionTestUtils.setField(integrationHistoricService, "creditErrors",
        new String[]{"2008", "2014", "2023", "2024", "504", "2010", "2002", "2037", "2003", "2012",
            "2011", "2067", "2006", "2068", "2009", "2027"});
  }

  @ParameterizedTest
  @CsvSource({"0,10,bpmId,-1", "0,10,bpmId,1"})
  void test_getHistoricIntegration_should_be_ok(int page, int size,
      String sortField, int sortOrder) {

    List<String> bpmIds = Arrays.asList("151-00000001", "151-00000002");
    when(criteriaUtil.findGroupedByBpmId(sortField, sortOrder)).thenReturn(bpmIds);

    ApplicationEntity applicationEntity = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    individualCustomer.setLastName("");
    individualCustomer.setFirstName("");
    customerEntity.setIndividualCustomer(individualCustomer);
    applicationEntity.setCustomer(customerEntity);
    when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(applicationEntity));

    List<ApplicationHistoricIntegration> historicIntegrations = new ArrayList<>();
    ApplicationHistoricIntegration integration = new ApplicationHistoricIntegration();
    integration.setCardType("Main card");
    integration.setLoanAmount(BigDecimal.TEN);
    historicIntegrations.add(integration);
    integration = new ApplicationHistoricIntegration();
    integration.setLoanAmount(BigDecimal.TEN);
    historicIntegrations.add(integration);
    when(integrationRepository.findByBpmId(anyString())).thenReturn(
        Optional.of(historicIntegrations));

    ApplicationHistoricIntegrationResponse response = integrationHistoricService.getHistoricIntegration(
        page, size, sortField, sortOrder);

    assertNotNull(response);
    assertEquals(0, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(2, response.getTotalElements());
  }

  @ParameterizedTest
  @CsvSource({"151-00000001,0,10,bpmId,-1"})
  void test_getHistoricIntegrationSearch_should_be_ok(String contents,
      int page, int size, String sortField, int sortOrder) {

    List<String> bpmIds = Collections.singletonList("151-00000001");
    when(criteriaUtil.findBySearch(contents, sortField, sortOrder))
        .thenReturn(bpmIds);

    ApplicationEntity applicationEntity = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    individualCustomer.setLastName("");
    individualCustomer.setFirstName("");
    customerEntity.setIndividualCustomer(individualCustomer);
    applicationEntity.setCustomer(customerEntity);
    when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(applicationEntity));

    List<ApplicationHistoricIntegration> historicIntegrations = new ArrayList<>();
    ApplicationHistoricIntegration integration = new ApplicationHistoricIntegration();
    integration.setCardType("Main card");
    integration.setLoanAmount(BigDecimal.TEN);
    historicIntegrations.add(integration);
    integration = new ApplicationHistoricIntegration();
    integration.setLoanAmount(BigDecimal.TEN);
    historicIntegrations.add(integration);
    when(integrationRepository.findByBpmId(anyString())).thenReturn(
        Optional.of(historicIntegrations));

    ApplicationHistoricIntegrationResponse response = integrationHistoricService.getHistoricIntegrationSearch(
        contents, page, size, sortField, sortOrder);

    assertNotNull(response);
    assertEquals(0, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(1, response.getTotalElements());
  }

  @ParameterizedTest
  @CsvSource({"151-00000001"})
  void test_getHistoricIntegrationDetail_should_be_ok(String bpmId) {

    List<ApplicationHistoricIntegration> historicIntegrations = new ArrayList<>();
    ApplicationHistoricIntegration integration = new ApplicationHistoricIntegration();
    integration.setCardType("Main card");
    integration.setLoanAmount(BigDecimal.TEN);
    integration.setIntegratedSystem(WAY_4);
    integration.setIntegratedStatus("");
    integration.setErrorCode("2008");
    historicIntegrations.add(integration);

    when(integrationRepository.findByBpmId(bpmId))
        .thenReturn(Optional.of(historicIntegrations));

    test_convertStatusRetry_should_be_ok(integration.getIntegratedSystem(),
        integration.getErrorCode(), integration.getIntegratedStatus());

    List<ApplicationHistoricIntegrationDTO> responses = integrationHistoricService.getHistoricIntegrationDetail(
        bpmId);

    assertNotNull(responses);
    assertEquals(1, responses.size());
  }

  @ParameterizedTest
  @CsvSource({"WAY4,2008,''","BPM_OPERATION,500,","WAY4,123123,''"})
  void test_integrationHistoricRetry_should_be_ok(String integratedSystem, String errorCode,
      String integratedStatus) {

    IntegrationRetryRequest requestList = new IntegrationRetryRequest();
    RetryRequest retryRequest = new RetryRequest();
    retryRequest.setId(1L);
    requestList.setRetryList(Collections.singletonList(retryRequest));

    ApplicationHistoricIntegration integration = new ApplicationHistoricIntegration();
    integration.setCardType("Main card");
    integration.setLoanAmount(BigDecimal.TEN);
    integration.setIntegratedSystem(integratedSystem);
    integration.setIntegratedStatus(integratedStatus);
    integration.setErrorCode(errorCode);

    when(integrationRepository.findById(anyLong()))
        .thenReturn(Optional.of(integration));

    if ("123123".equals(errorCode)) {
      assertThrows(ApprovalException.class, () -> integrationHistoricService.integrationHistoricRetry(requestList));
    } else {
      Object response = integrationHistoricService.integrationHistoricRetry(requestList);
      assertNotNull(response);
    }
  }

  @ParameterizedTest
  @CsvSource({"true,","false,","false,151-00000001"})
  void test_fullSearch_should_be_ok(boolean isFirstTime, String fullText) {
    FullSearch fullSearch = new FullSearch();
    fullSearch.setFirstTime(isFirstTime);
    fullSearch.setFullText(fullText);

    BaseSearch.Pageable pageable = new BaseSearch.Pageable();
    pageable.setPage(0);
    pageable.setSize(10);
    pageable.setSorts(Collections.singletonList("updatedAt,desc"));
    fullSearch.setPageable(pageable);

    List<ApplicationEntity> dataList = new ArrayList<>();
    ApplicationEntity integration = new ApplicationEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    individualCustomer.setFirstName("");
    individualCustomer.setLastName("");
    customerEntity.setIndividualCustomer(individualCustomer);
    integration.setCustomer(customerEntity);
    integration.setBpmId("151-00000001");
    dataList.add(integration);

    Page<ApplicationEntity> pageData = new PageImpl<>(dataList);

    when(applicationRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
        .thenReturn(pageData);

    ApplicationHistoricIntegrationResponse response = integrationHistoricService.fullSearch(fullSearch);

    assertNotNull(response);
  }

  @ParameterizedTest
  @CsvSource({"WAY4,2008,''", "WAY4,2027,''", "WAY4,1424123,Chờ tích hợp",
      "WAY4,1424123,Chờ tạo client",
      "WAY4,1424123,Chờ tạo thẻ chính", "WAY4,1424123,Chờ tạo thẻ phụ",
      "BPM_OPERATION,'',''", "BPM_OPERATION,500,", "BPM_OPERATION,408,", "BPM_OPERATION,403,",
      "ABC,'',''"})
  void test_convertStatusRetry_should_be_ok(String integratedSystem, String errorCode,
      String integratedStatus) {

    boolean expected;

    if ("WAY4".equals(integratedSystem)) {
      expected = errorCode.equals("2008") || errorCode.equals("2027")
          || integratedStatus.equals("Chờ tích hợp")
          || integratedStatus.equals("Chờ tạo client")
          || integratedStatus.equals("Chờ tạo thẻ chính")
          || integratedStatus.equals("Chờ tạo thẻ phụ");
    } else {
      expected = errorCode.equals("500") || errorCode.equals("408") || errorCode.equals("403");
    }

    assertEquals(expected, integrationHistoricService.convertStatusRetry(integratedSystem, errorCode,
        integratedStatus));
  }
}
