package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationRepaymentDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationRepaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationRepaymentMapper {

  ApplicationRepaymentMapper INSTANCE = Mappers.getMapper(ApplicationRepaymentMapper.class);

  @Mapping(target = "totalRepay", source = "repayment.totalRepay")
  @Mapping(target = "dti", source = "repayment.dti")
  @Mapping(target = "dsr", source = "repayment.dsr")
  @Mapping(target = "mue", source = "repayment.mue")
  @Mapping(target = "evaluate", source = "repayment.evaluate")
  ApplicationRepaymentDTO toRepayment(ApplicationEntity entity);

  ApplicationRepaymentEntity toRepaymentEntity(ApplicationRepaymentDTO dto);



}
