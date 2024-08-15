package com.msb.bpm.approval.appr.mapper.formtemplate;

import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditOverdraftEntity;
import java.util.List;
import java.util.Set;

public interface FormTemplateCreditExecutorMapper<T> {

  Set<FormTemplateCreditDTO> transformCreditDTOs(List<ApplicationCreditEntity> set);

  T map(ApplicationCreditCardEntity cardEntity, ApplicationCreditEntity entity);

  T map(ApplicationCreditLoanEntity cardEntity, ApplicationCreditEntity entity);

  T map(ApplicationCreditOverdraftEntity cardEntity, ApplicationCreditEntity entity);


}
