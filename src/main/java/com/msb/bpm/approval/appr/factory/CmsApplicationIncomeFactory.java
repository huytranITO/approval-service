package com.msb.bpm.approval.appr.factory;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.factory.builder.income.GetCmsIncomeReserveBuilder;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 17/8/2023, Thursday
 **/
public interface CmsApplicationIncomeFactory {

  static ObjectBuilder<ApplicationIncomeEntity, CmsIncomeDTO> getApplicationIncomeReverse() {
    return new GetCmsIncomeReserveBuilder();
  }
}
