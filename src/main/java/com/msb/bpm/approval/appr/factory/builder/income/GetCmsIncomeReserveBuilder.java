package com.msb.bpm.approval.appr.factory.builder.income;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 17/8/2023, Thursday
 **/
public class GetCmsIncomeReserveBuilder implements
    ObjectBuilder<ApplicationIncomeEntity, CmsIncomeDTO> {

  @Override
  public ApplicationIncomeEntity build(CmsIncomeDTO cmsIncomeDTO) {
    ApplicationIncomeEntity incomeEntity = ApplicationIncomeMapper.INSTANCE.cmsIncomeToEntity(
        cmsIncomeDTO);
    cmsIncomeDTO.getIncomeItems()
        .forEach(incomeItem -> referenceCmsBaseIncomeItemToEntity(incomeEntity, incomeItem));
    return incomeEntity;
  }
}
