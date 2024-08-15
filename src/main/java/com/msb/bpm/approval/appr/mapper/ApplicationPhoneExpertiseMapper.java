package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationPhoneExpertiseDTO.ApplicationPhoneExpertiseDtlDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationPhoneExpertiseEntity;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationPhoneExpertiseMapper {

  ApplicationPhoneExpertiseMapper INSTANCE = Mappers.getMapper(
      ApplicationPhoneExpertiseMapper.class);

  Set<ApplicationPhoneExpertiseDtlDTO> toApplicationPhoneExpertises(
      Set<ApplicationPhoneExpertiseEntity> entities);

  Set<ApplicationPhoneExpertiseEntity> toApplicationPhoneExpertiseEntities(
      Set<ApplicationPhoneExpertiseDtlDTO> entities);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referencePhoneExpertiseEntity(@MappingTarget ApplicationPhoneExpertiseEntity e1, ApplicationPhoneExpertiseEntity e2);
}
