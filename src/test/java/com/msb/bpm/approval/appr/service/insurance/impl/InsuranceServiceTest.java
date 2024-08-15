package com.msb.bpm.approval.appr.service.insurance.impl;


import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_LEAD_INSURANCE;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.config.ApplicationConfig.Kafka;
import com.msb.bpm.approval.appr.config.ApplicationConfig.TopicKafka;
import com.msb.bpm.approval.appr.enums.application.AddressType;
import com.msb.bpm.approval.appr.kafka.sender.DataKafkaSender;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import com.msb.bpm.approval.appr.model.request.insurance.PostPublishInsuranceInfoRequest;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.insurance.impl.InsuranceServiceImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InsuranceServiceTest {
  @Mock
  private ApplicationRepository applicationRepository;
  @Mock
  private ObjectMapper objectMapper;
  @InjectMocks
  private InsuranceServiceImpl insuranceService;
  @Mock
  private DataKafkaSender sender;

  @Mock
  private ApplicationConfig config;

  @BeforeEach
  public void setUp() {
    Kafka kafka = new Kafka();
    Map<String, TopicKafka> topic = new HashMap<>();
    topic.put(BPM_LEAD_INSURANCE, new TopicKafka());
    kafka.setTopic(topic);
    lenient().when(config.getKafka()).thenReturn(kafka);
  }


  @Test
  void test_execute() {
    ApplicationEntity entity = new ApplicationEntity();
    CustomerIdentityEntity identity = new CustomerIdentityEntity();
    Set<CustomerIdentityEntity> setOfIdentities = new HashSet<>();
    CustomerAddressEntity address = new CustomerAddressEntity();
    CustomerEntity customerEntity = new CustomerEntity();
    entity.setCustomer(customerEntity);
    address.setId(1L);
    address.setAddressType( AddressType.DIA_CHI_TSC.getValue());
    Set<CustomerAddressEntity> setOfAddress = new HashSet<>();
    setOfIdentities.add(identity);
    setOfAddress.add(address);
    customerEntity.setCustomerIdentitys(setOfIdentities);
    customerEntity.setCustomerAddresses(setOfAddress);
    entity.setPreviousRole(PD_RB_RM.name());

    IndividualCustomerEntity individualCustomer = new IndividualCustomerEntity();
    individualCustomer.setMartialStatus("1");
    individualCustomer.setGenderValue("NU");
    customerEntity.setIndividualCustomer(individualCustomer);
    setOfIdentities.add(identity);
    setOfAddress.add(address);

    ApplicationConfig config = new ApplicationConfig();
    Kafka kafka = new Kafka();
    Map<String, TopicKafka> topic = new HashMap<>();
    topic.put("BPM_LEAD_INSURANCE",new TopicKafka());
    kafka.setTopic(topic);
    config.setKafka(kafka);

    when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(entity));

    Assertions.assertDoesNotThrow(
        () -> insuranceService.execute("bpmId", new PostPublishInsuranceInfoRequest()));
  }

}
