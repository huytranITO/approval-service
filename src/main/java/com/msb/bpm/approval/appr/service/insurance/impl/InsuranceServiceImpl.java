package com.msb.bpm.approval.appr.service.insurance.impl;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.TopicKafka.BPM_LEAD_INSURANCE;
import static com.msb.bpm.approval.appr.enums.application.ProcessingRole.PD_RB_RM;
import static com.msb.bpm.approval.appr.enums.common.SourceApplication.BPM;
import static com.msb.bpm.approval.appr.enums.common.SourceApplication.CJBO;
import static com.msb.bpm.approval.appr.enums.common.SourceApplication.CJMHOME;
import static com.msb.bpm.approval.appr.exception.DomainCode.NOT_FOUND_APPLICATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.enums.application.AddressType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.kafka.sender.DataKafkaSender;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.request.insurance.PostPublishInsuranceInfoRequest;
import com.msb.bpm.approval.appr.model.request.insurance.mq.InsuranceMessage;
import com.msb.bpm.approval.appr.model.request.insurance.mq.InsuranceMessage.Data;
import com.msb.bpm.approval.appr.repository.ApplicationRepository;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.insurance.InsuranceService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceServiceImpl extends AbstractBaseService implements InsuranceService {

  private final DataKafkaSender sender;
  private final ApplicationRepository applicationRepository;
  private final ObjectMapper objectMapper;
  private final ApplicationConfig config;

  @Override
  @Transactional
  public void execute(String bpmId, PostPublishInsuranceInfoRequest request) {
    log.info("InsuranceServiceImpl execute START with bpmId={}, request{}", bpmId, JsonUtil.convertObject2String(request, objectMapper), request);
    ApplicationEntity entity =  applicationRepository.findByBpmId(bpmId)
   .orElseThrow(() -> new ApprovalException(NOT_FOUND_APPLICATION, new Object[]{bpmId}));

    CustomerIdentityEntity identityEntity = entity.getCustomer().getCustomerIdentitys().stream().filter(i -> i.isPriority()).findFirst().orElse(null);

    CustomerAddressEntity addressEntity = entity.getCustomer().getCustomerAddresses().stream()
        .filter(address -> AddressType.DIA_CHI_TSC.getValue().equals(address.getAddressType()))
        .findFirst()
        .orElse(null);

    if (!PD_RB_RM.name().equals(entity.getPreviousRole())) {
      log.info("InsuranceServiceImpl execute END with ProcessingRole={}", entity.getProcessingRole());
      return;
    }

     Data data =  Data.builder()
          .fullName(Util.getFullName(entity.getCustomer().getIndividualCustomer().getLastName(),entity.getCustomer().getIndividualCustomer().getFirstName()))
          .dateOfBirth(entity.getCustomer().getIndividualCustomer().getDateOfBirth())
          .gender(getGender(entity.getCustomer().getIndividualCustomer().getGenderValue()))
          .phoneNumber(entity.getCustomer().getIndividualCustomer().getPhoneNumber())
          .email(entity.getCustomer().getIndividualCustomer().getEmail())
          .identifierNumber(identityEntity != null ? identityEntity.getIdentifierCode() : null)
          .issuedAt(identityEntity != null ? identityEntity.getIssuedAt() : null)
          .issuedPlace(identityEntity != null ? identityEntity.getIssuedPlaceValue() : null)
          .cityCode(addressEntity != null ? addressEntity.getCityCode() : null)
          .districtCode(addressEntity != null ? addressEntity.getDistrictCode() : null)
          .wardCode(addressEntity != null ? addressEntity.getWardCode() : null)
          .addressLine(addressEntity != null ? addressEntity.getAddressLine() : null)
          .recognizedIncome(getTotalRecognizedIncome(entity.getIncomes()))
          .martialStatus(Integer.valueOf(entity.getCustomer().getIndividualCustomer().getMartialStatus()))
          .bpmId(entity.getBpmId())
          .rmStatus(request.isRmStatus())
          .insuranceStatus(entity.isInsurance())
          .insuranceLead(null)
          .rmEmployee(entity.getSaleCode())
          .build();

        // Lấy danh sách mã văn bản
        List<String> regulatoryCode = new ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(entity.getRegulatoryCode())) {
          regulatoryCode = Arrays.asList(entity.getRegulatoryCode().split(";"));
        }

        try {
          InsuranceMessage message = InsuranceMessage.builder()
                  .source(CJMHOME.name().equalsIgnoreCase(entity.getSource())
                          || CJBO.name().equalsIgnoreCase(entity.getSource())
                          || BPM.name().equalsIgnoreCase(entity.getSource())  ? entity.getSource() : null)
                  .refId(CJMHOME.name().equalsIgnoreCase(entity.getSource())
                          || CJBO.name().equalsIgnoreCase(entity.getSource()) ? entity.getRefId() : null)
                  .userName(entity.getAssignee())
                  .status(entity.getStatus())
                  .responseDate(LocalDateTime.now())
                  .regulatoryCode(regulatoryCode)
                  .source(entity.getSource())
                  .refId(entity.getRefId())
                  .data(data)
                  .build();
          sender.sendMessage(message, config.getKafka().getTopic().get(BPM_LEAD_INSURANCE).getTopicName());
        } catch (Exception e){
          log.error("sendMessage to topic bpm_lead_insurances error: ", e);
        }
    updateRmStatus(entity, request);
    applicationRepository.save(entity);
  }

  private String getGender(String value) {
    if (value == null) return null;
    if ("Nam".equals(value)) {
      return "M";
    } else {
      return "F";
    }
  }

  private void updateRmStatus(ApplicationEntity entity, PostPublishInsuranceInfoRequest request) {
    entity.setRmStatus(request.isRmStatus());
  }

  private BigDecimal getTotalRecognizedIncome (Collection<ApplicationIncomeEntity> incomes) {
    BigDecimal sum = BigDecimal.ZERO;
    try {
      if (CollectionUtils.isNotEmpty(incomes)) {
        Iterator<ApplicationIncomeEntity> it = incomes.iterator();
        while (it.hasNext()) {
          ApplicationIncomeEntity income = it.next();
          sum = sum.add(income.getRecognizedIncome());
        }
      }
    } catch (Exception ex) {
      log.error("getTotalRecognizedIncome occurred exception {}", ex.getMessage());
    }
    return sum;
  }
}
