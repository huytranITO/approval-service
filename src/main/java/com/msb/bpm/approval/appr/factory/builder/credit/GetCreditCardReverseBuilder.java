package com.msb.bpm.approval.appr.factory.builder.credit;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import org.apache.commons.collections4.CollectionUtils;

public class GetCreditCardReverseBuilder implements ObjectBuilder<ApplicationCreditEntity, ApplicationCreditDTO> {

  @Override
  public ApplicationCreditEntity build(ApplicationCreditDTO creditDTO) {
    ApplicationCreditEntity creditEntity = ApplicationCreditMapper.INSTANCE.toCreditEntity(creditDTO);
    ApplicationCreditCardEntity creditCardEntity = ApplicationCreditMapper.INSTANCE.toCreditCardEntity((ApplicationCreditCardDTO) creditDTO);
    creditCardEntity.setLdpCardId(creditDTO.getLdpCreditId());
    creditEntity.setCreditCard(creditCardEntity);
    if (CollectionUtils.isNotEmpty(creditCardEntity.getSubCreditCards())) {
      creditCardEntity.getSubCreditCards().forEach(subCreditCardEntity -> subCreditCardEntity.setApplicationCreditCard(creditCardEntity));
    }
    return creditEntity;
  }
}
