package com.msb.bpm.approval.appr.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.msb.bpm.approval.appr.model.dto.CreditConditionDTO;

import com.msb.bpm.approval.appr.model.entity.CreditConditionEntity;

@Mapper
public interface CreditConditionMapper {
    CreditConditionMapper INSTANCE = Mappers.getMapper(CreditConditionMapper.class);

    List<CreditConditionDTO> toCreditConditions(
            List<CreditConditionEntity> creditConditions);
}
