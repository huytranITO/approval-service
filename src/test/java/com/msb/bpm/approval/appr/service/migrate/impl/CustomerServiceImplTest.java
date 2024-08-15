package com.msb.bpm.approval.appr.service.migrate.impl;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.customer.response.CommonMigrateVersionResponse;
import com.msb.bpm.approval.appr.client.customer.response.CommonMigrateVersionResponse.CommonWrapVersionResponse;
import com.msb.bpm.approval.appr.client.customer.response.CommonMigrateVersionResponse.CustomerVersionResponse;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * @created : 5/12/2023, Tuesday
 **/
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private CustomerClient customerClient;

  @InjectMocks
  private CustomerServiceImpl customerService;

  @ParameterizedTest
  @CsvSource({"1",})
  void test_updateCustomerVersion_should_be_ok(Long customerId) {
    List<CustomerEntity> list = new ArrayList<>();
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setIndividualCustomer(new IndividualCustomerEntity());
    customerEntity.setCustomerType(CustomerType.RB.name());
    customerEntity.setBpmCif("bpmCif");
    customerEntity.setCif("cif");
    customerEntity.setRefCusId("refCusId");
    customerEntity.setRefCustomerId(1L);
    customerEntity.setId(1L);
    customerEntity.getIndividualCustomer().setAge(25);
    customerEntity.getIndividualCustomer().setLastName("LastName");
    customerEntity.getIndividualCustomer().setFirstName("FirstName");
    customerEntity.getIndividualCustomer().setEmail("email@gmail.com");
    customerEntity.getIndividualCustomer().setGender("gender");
    customerEntity.getIndividualCustomer().setDateOfBirth(LocalDate.now());
    customerEntity.getIndividualCustomer().setEmployeeCode("EmployeeCode");
    customerEntity.getIndividualCustomer().setGenderValue("genderValue");
    customerEntity.getIndividualCustomer().setLiteracy("literacy");
    customerEntity.getIndividualCustomer().setLiteracyTxt("literacyTxt");
    customerEntity.getIndividualCustomer().setMartialStatus("1");
    customerEntity.getIndividualCustomer().setMartialStatusValue("maritalStatusValue");
    customerEntity.getIndividualCustomer().setMsbMember(true);
    customerEntity.getIndividualCustomer().setNation("nation");
    customerEntity.getIndividualCustomer().setNationValue("nationValue");
    customerEntity.getIndividualCustomer().setPhoneNumber("phoneNumber");
    customerEntity.getIndividualCustomer().setSubject("subject");
    list.add(customerEntity);

    when(customerRepository.findAllByVersionIsNull(customerId, CustomerType.RB.name()))
        .thenReturn(Optional.of(list));

    CommonMigrateVersionResponse response = new CommonMigrateVersionResponse();
    CustomerVersionResponse versionResponse = new CustomerVersionResponse();
    versionResponse.setVersion(100);
    versionResponse.setRefCustomerId(1L);
    CommonWrapVersionResponse wrapVersionResponse = new CommonWrapVersionResponse();
    wrapVersionResponse.getCustomers().add(versionResponse);
    response.setData(wrapVersionResponse);

    when(customerClient.migrateCustomerVersion(any()))
        .thenReturn(response);

//    verify(customerRepository, times(1)).saveAll(anyCollection());

    Map<String, Object> dataMap = customerService.updateCustomerVersion(customerId);

    assertNotNull(dataMap);
  }
}
