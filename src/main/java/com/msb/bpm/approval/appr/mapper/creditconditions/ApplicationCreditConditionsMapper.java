package com.msb.bpm.approval.appr.mapper.creditconditions;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.msb.bpm.approval.appr.model.dto.ApplicationCreditConditionsDTO;
import com.msb.bpm.approval.appr.model.request.creditconditions.CreditConditionRequest;

@Mapper
public interface ApplicationCreditConditionsMapper {
	ApplicationCreditConditionsMapper INSTANCE = Mappers.getMapper(
			ApplicationCreditConditionsMapper.class);
	
	@Mapping(target = "id", source = "creditConditionId")
	@Mapping(target = "conditionGroupCode", source = "code")
	@Mapping(target = "creditConditionGroup", source = "group")
	@Mapping(target = "objectApply", source = "applicableSubject")
	@Mapping(target = "timeControl", source = "timeOfControl")
	@Mapping(target = "controlLevel", source = "controlUnit")
	@Mapping(target = "policyConditionId", source = "masterId")
	@Mapping(target = "source", ignore = true)
	@Mapping(target = "allowDelete", ignore = true)
	CreditConditionRequest toCreditConditionRequest(ApplicationCreditConditionsDTO creditConditions);
}
