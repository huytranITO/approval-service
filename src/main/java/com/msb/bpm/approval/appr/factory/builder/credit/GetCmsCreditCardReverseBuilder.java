package com.msb.bpm.approval.appr.factory.builder.credit;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCreditCardDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 19/8/2023, Saturday
 **/
public class GetCmsCreditCardReverseBuilder implements ObjectBuilder<ApplicationCreditEntity, CmsBaseCreditDTO> {

  @Override
  public ApplicationCreditEntity build(CmsBaseCreditDTO baseCreditDTO) {
    ApplicationCreditEntity creditEntity = ApplicationCreditMapper.INSTANCE.cmsCreditToEntity(baseCreditDTO);
    ApplicationCreditCardEntity creditCardEntity = ApplicationCreditMapper.INSTANCE.cmsCreditCardToEntity((CmsCreditCardDTO) baseCreditDTO);
    creditEntity.setCreditCard(creditCardEntity);

    if (CollectionUtils.isNotEmpty(creditCardEntity.getSubCreditCards())) {
      creditCardEntity.getSubCreditCards().forEach(
          subCreditCardEntity -> subCreditCardEntity.setApplicationCreditCard(creditCardEntity));
    }

    return creditEntity;
  }
}
