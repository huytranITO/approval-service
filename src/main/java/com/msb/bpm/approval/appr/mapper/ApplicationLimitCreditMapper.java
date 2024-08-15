package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationLimitCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationLimitCreditMapper {

  ApplicationLimitCreditMapper INSTANCE = Mappers.getMapper(ApplicationLimitCreditMapper.class);

  Set<ApplicationLimitCreditDTO> toLimitCredits(Set<ApplicationLimitCreditEntity> limitCredits);

  Set<ApplicationLimitCreditEntity> toLimitCreditEntities(Set<ApplicationLimitCreditDTO> limitCredits);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceLimitCreditEntity(@MappingTarget ApplicationLimitCreditEntity e1, ApplicationLimitCreditEntity e2);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "application", ignore = true)
  ApplicationLimitCreditEntity copyApplicationLimitCreditEntity(ApplicationLimitCreditEntity applicationLimitCreditEntity);

  Set<ApplicationLimitCreditEntity> copyApplicationLimitCreditEntities(Set<ApplicationLimitCreditEntity> applicationLimitCreditEntities);
}
