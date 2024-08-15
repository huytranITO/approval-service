package com.msb.bpm.approval.appr.factory.builder.credit;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCreditLoanDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 19/8/2023, Saturday
 **/
public class GetCmsCreditLoanReverseBuilder implements ObjectBuilder<ApplicationCreditEntity, CmsBaseCreditDTO> {

  @Override
  public ApplicationCreditEntity build(CmsBaseCreditDTO baseCreditDTO) {
    ApplicationCreditEntity creditEntity = ApplicationCreditMapper.INSTANCE.cmsCreditToEntity(baseCreditDTO);
    creditEntity.setCreditLoan(ApplicationCreditMapper.INSTANCE.cmsCreditLoanToEntity(
        (CmsCreditLoanDTO) baseCreditDTO));
    return creditEntity;
  }
}
