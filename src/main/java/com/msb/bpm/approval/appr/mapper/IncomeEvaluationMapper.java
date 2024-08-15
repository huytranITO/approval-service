package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.IncomeEvaluationDTO;
import com.msb.bpm.approval.appr.model.entity.IncomeEvaluationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IncomeEvaluationMapper {

  IncomeEvaluationMapper INSTANCE = Mappers.getMapper(IncomeEvaluationMapper.class);

  IncomeEvaluationEntity toIncomeEvaluationEntity(IncomeEvaluationDTO incomeEvaluation);

  IncomeEvaluationDTO toIncomeEvaluationDTO(IncomeEvaluationEntity incomeEvaluation);

  void toEntity(@MappingTarget IncomeEvaluationEntity i1, IncomeEvaluationEntity i2);
}
