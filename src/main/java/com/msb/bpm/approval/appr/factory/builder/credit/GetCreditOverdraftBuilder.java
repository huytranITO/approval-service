package com.msb.bpm.approval.appr.factory.builder.credit;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;

public class GetCreditOverdraftBuilder implements
    ObjectBuilder<ApplicationCreditDTO, ApplicationCreditEntity> {

  @Override
  public ApplicationCreditDTO build(ApplicationCreditEntity applicationCreditEntity) {
    return ApplicationCreditMapper.INSTANCE.toCreditOverdraftDTO(applicationCreditEntity,
        applicationCreditEntity.getCreditOverdraft());
  }
}
