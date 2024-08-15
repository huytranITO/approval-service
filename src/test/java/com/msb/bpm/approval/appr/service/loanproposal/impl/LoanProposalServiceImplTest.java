package com.msb.bpm.approval.appr.service.loanproposal.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.loanproposal.LoanProposalClient;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.ApplicationFbDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationAppraisalContentEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationContactEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.application.impl.ApplicationFeedbackServiceImpl;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 27/11/2023, Monday
 **/
@ExtendWith(MockitoExtension.class)
class LoanProposalServiceImplTest {

  @Mock
  private LoanProposalClient loanProposalClient;

  @Mock
  private ApplicationFeedbackServiceImpl applicationFeedbackService;

  @Mock
  private ApplicationRepository applicationRepository;

  @InjectMocks
  private LoanProposalServiceImpl loanProposalService;

  private String pathSourceFile = "src/test/resources/feedback_application/";

  private ApplicationEntity applicationEntity;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void init() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(ObjectMapperUtil.javaTimeModule());
  }

  @Test
  void test_updateApplicationData_should_be_ok() throws IOException {
    applicationEntity = createApplicationEntity();

    when(applicationRepository.findByBpmId(anyString()))
        .thenReturn(Optional.of(applicationEntity));

    ApplicationFbDTO applicationFbDTO = new ApplicationFbDTO();
    applicationFbDTO.setApplication(new CmsApplicationDTO());
    applicationFbDTO.getApplication().setBpmId(applicationEntity.getBpmId());

    when(applicationFeedbackService.convertApplicationEntityToApplicationFbDTO(applicationEntity))
        .thenReturn(applicationFbDTO);

    loanProposalService.updateApplicationData(anyString());

    assertNotNull(applicationFbDTO);
    assertEquals(applicationEntity.getBpmId(), applicationFbDTO.getApplication().getBpmId());
  }

  private ApplicationEntity createApplicationEntity() throws IOException {
    ApplicationEntity applicationEntity = objectMapper.readValue(
        new File(pathSourceFile + "application.json"), ApplicationEntity.class);
    CustomerEntity customerEntity = objectMapper.readValue(
        new File(pathSourceFile + "customer.json"), CustomerEntity.class);
    Set<CustomerRelationShipEntity> customerRelationShipEntities = objectMapper.readValue(
        new File(pathSourceFile + "customer_relationship.json"),
        new TypeReference<Set<CustomerRelationShipEntity>>() {
        });
    //Set customerRelationships
    customerEntity.setCustomerRelationShips(customerRelationShipEntities);
    //Set customer
    applicationEntity.setCustomer(customerEntity);
    //Application Contact
    Set<ApplicationContactEntity> applicationContactEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_contact.json"),
        new TypeReference<Set<ApplicationContactEntity>>() {
        });
    applicationEntity.setContact(applicationContactEntities);
    //Income
    Set<ApplicationIncomeEntity> applicationIncomeEntities = objectMapper.readValue(
        new File(pathSourceFile + "income.json"),
        new TypeReference<Set<ApplicationIncomeEntity>>() {
        });
    applicationEntity.setIncomes(applicationIncomeEntities);
    //Credit
    Set<ApplicationCreditEntity> applicationCreditEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_credit.json"),
        new TypeReference<Set<ApplicationCreditEntity>>() {
        });
    applicationEntity.setCredits(applicationCreditEntities);
    //Field
    Set<ApplicationFieldInformationEntity> applicationFieldInformationEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_field.json"),
        new TypeReference<Set<ApplicationFieldInformationEntity>>() {
        });
    applicationEntity.setFieldInformations(applicationFieldInformationEntities);
    //Limit Credit
    Set<ApplicationLimitCreditEntity> applicationLimitCreditEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_limit_credit.json"),
        new TypeReference<Set<ApplicationLimitCreditEntity>>() {
        });
    applicationEntity.setLimitCredits(applicationLimitCreditEntities);
    //Appraisal Content
    Set<ApplicationAppraisalContentEntity> applicationAppraisalContentEntities = objectMapper.readValue(
        new File(pathSourceFile + "application_appraisal_content.json"),
        new TypeReference<Set<ApplicationAppraisalContentEntity>>() {
        });
    applicationEntity.setAppraisalContents(applicationAppraisalContentEntities);

    return applicationEntity;
  }
}
