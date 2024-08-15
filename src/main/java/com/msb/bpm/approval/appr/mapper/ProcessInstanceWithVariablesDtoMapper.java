package com.msb.bpm.approval.appr.mapper;

import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 10/5/2023, Wednesday
 **/
@Mapper
public interface ProcessInstanceWithVariablesDtoMapper {
  ProcessInstanceWithVariablesDtoMapper INSTANCE = Mappers.getMapper(ProcessInstanceWithVariablesDtoMapper.class);

  void referenceProcessInstanceWithVariablesDto(@MappingTarget ProcessInstanceWithVariablesDto e1, ProcessInstanceWithVariablesDto e2);
}
