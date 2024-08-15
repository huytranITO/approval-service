package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationCreditConditionsDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditConditionsEntity;
import com.msb.bpm.approval.appr.model.response.creditconditions.CreditConditionResponse;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationCreditConditionMapper {

  ApplicationCreditConditionMapper INSTANCE = Mappers.getMapper(
      ApplicationCreditConditionMapper.class);

  Set<ApplicationCreditConditionsDTO> toCreditConditions(
      Set<ApplicationCreditConditionsEntity> creditConditions);
  
  List<ApplicationCreditConditionsDTO> toCreditConditions(
	      List<CreditConditionResponse> creditConditions);
  
  @Mapping(target = "timeOfControl", source = "timeControl")
  @Mapping(target = "group", source = "creditConditionGroup")
  @Mapping(target = "code", source = "conditionGroupCode")
  @Mapping(target = "applicableSubject", source = "objectApply")
  @Mapping(target = "controlUnit", source = "controlLevel")
  @Mapping(target = "creditConditionId", source = "id")
  @Mapping(target = "masterId",	source = "policyConditionId")
  ApplicationCreditConditionsDTO toCreditConditions(
		  CreditConditionResponse creditConditions);

  Set<ApplicationCreditConditionsEntity> toCreditConditionEntities(
      Set <ApplicationCreditConditionsDTO> creditConditions);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceCreditConditionsEntity(@MappingTarget ApplicationCreditConditionsEntity e1,
      ApplicationCreditConditionsEntity e2);
}
