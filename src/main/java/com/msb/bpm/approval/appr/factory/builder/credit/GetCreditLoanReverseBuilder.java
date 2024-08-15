package com.msb.bpm.approval.appr.factory.builder.credit;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditLoanDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity;

public class GetCreditLoanReverseBuilder implements
    ObjectBuilder<ApplicationCreditEntity, ApplicationCreditDTO> {

  @Override
  public ApplicationCreditEntity build(ApplicationCreditDTO applicationCreditDTO) {
    ApplicationCreditEntity creditEntity = ApplicationCreditMapper.INSTANCE.toCreditEntity(applicationCreditDTO);
    ApplicationCreditLoanEntity creditLoanEntity = ApplicationCreditMapper.INSTANCE.toCreditLoanEntity(
        (ApplicationCreditLoanDTO) applicationCreditDTO);
    creditLoanEntity.setLdpLoanId(applicationCreditDTO.getLdpCreditId());
    creditEntity.setCreditLoan(creditLoanEntity);
    return creditEntity;
  }
}
