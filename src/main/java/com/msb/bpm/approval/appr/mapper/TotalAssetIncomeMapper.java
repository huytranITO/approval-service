package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.entity.TotalAssetIncomeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TotalAssetIncomeMapper {
  TotalAssetIncomeMapper INSTANCE = Mappers.getMapper(TotalAssetIncomeMapper.class);

  void mapToEntity(@MappingTarget TotalAssetIncomeEntity t1, TotalAssetIncomeEntity t2);
}
