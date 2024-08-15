package com.msb.bpm.approval.appr.factory.builder;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.ENTERPRISE_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.INDIVIDUAL_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.OTHER;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.PROPERTY_BUSINESS;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.RENTAL;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ActuallyIncomeDTO.SALARY;

import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BusinessIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.OtherIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.PropertyBusinessIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.RentalIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.SalaryIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseIncomeItemDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsOtherDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsPropertyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsRentalIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsSalaryDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;

public interface ObjectBuilder<O, I> {

  O build(I i);

  default void referenceBaseIncomeToEntity(ApplicationIncomeEntity applicationIncomeEntity,
      BaseIncomeDTO baseIncome) {
    switch (baseIncome.getIncomeType()) {
      case SALARY:
        applicationIncomeEntity.getSalaryIncomes().add(
            ApplicationIncomeMapper.INSTANCE.toSalaryIncomeEntity((SalaryIncomeDTO) baseIncome));
        break;
      case RENTAL:
        applicationIncomeEntity.getRentalIncomes().add(
            ApplicationIncomeMapper.INSTANCE.toRentalIncomeEntity((RentalIncomeDTO) baseIncome));
        break;
      case INDIVIDUAL_BUSINESS:
      case ENTERPRISE_BUSINESS:
        applicationIncomeEntity.getIndividualEnterpriseIncomes()
            .add(ApplicationIncomeMapper.INSTANCE.toBizEntity((BusinessIncomeDTO) baseIncome));
        break;
      case OTHER:
        applicationIncomeEntity.getOtherIncomes()
            .add(ApplicationIncomeMapper.INSTANCE.toOtherIncomeEntity((OtherIncomeDTO) baseIncome));
        break;
      case PROPERTY_BUSINESS:
        applicationIncomeEntity.getPropertyBusinessIncomes()
            .add(ApplicationIncomeMapper.INSTANCE.toPropertyBusinessIncomeEntity(
                (PropertyBusinessIncomeDTO) baseIncome));
        break;
      default:
        break;
    }
  }

  default void referenceCmsBaseIncomeItemToEntity(ApplicationIncomeEntity incomeEntity,
      CmsBaseIncomeItemDTO cmsIncomeItem) {
    switch (cmsIncomeItem.getIncomeType()) {
      case SALARY:
        incomeEntity.getSalaryIncomes().add(ApplicationIncomeMapper.INSTANCE.cmsSalaryToEntity(
            (CmsSalaryDTO) cmsIncomeItem));
        break;

      case RENTAL:
        incomeEntity.getRentalIncomes().add(ApplicationIncomeMapper.INSTANCE.cmsRentalToEntity(
            (CmsRentalIncomeDTO) cmsIncomeItem));
        break;

      case INDIVIDUAL_BUSINESS:
        incomeEntity.getIndividualEnterpriseIncomes()
            .add(ApplicationIncomeMapper.INSTANCE.cmsIndividualToEntity(
                (CmsIndividualBusinessDTO) cmsIncomeItem));
        break;

      case ENTERPRISE_BUSINESS:
        incomeEntity.getIndividualEnterpriseIncomes()
            .add(ApplicationIncomeMapper.INSTANCE.cmsEnterpriseToEntity(
                (CmsEnterpriseBusinessDTO) cmsIncomeItem));
        break;

      case OTHER:
        incomeEntity.getOtherIncomes().add(ApplicationIncomeMapper.INSTANCE.cmsOtherToEntity(
            (CmsOtherDTO) cmsIncomeItem));
        break;

      case PROPERTY_BUSINESS:
        incomeEntity.getPropertyBusinessIncomes().add(ApplicationIncomeMapper.INSTANCE.cmsPropertyToEntity(
            (CmsPropertyIncomeDTO) cmsIncomeItem));
        break;

      default:
        break;
    }
  }
}
