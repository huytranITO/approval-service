package com.msb.bpm.approval.appr.factory.builder.credit;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditOverdraftDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditOverdraftEntity;

public class GetCreditOverdraftReverseBuilder implements
    ObjectBuilder<ApplicationCreditEntity, ApplicationCreditDTO> {

  @Override
  public ApplicationCreditEntity build(ApplicationCreditDTO creditDTO) {
    ApplicationCreditEntity creditEntity = ApplicationCreditMapper.INSTANCE.toCreditEntity(creditDTO);
    ApplicationCreditOverdraftEntity creditOverdraftEntity = ApplicationCreditMapper.INSTANCE.toCreditOverdraftEntity(
        (ApplicationCreditOverdraftDTO) creditDTO);
    creditOverdraftEntity.setLdpOverdraftId(creditDTO.getLdpCreditId());
    creditEntity.setCreditOverdraft(creditOverdraftEntity);
    return creditEntity;
  }
}
