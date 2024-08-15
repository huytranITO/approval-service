package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationAppraisalContentEntity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationAppraisalContentMapper {

  ApplicationAppraisalContentMapper INSTANCE = Mappers.getMapper(
      ApplicationAppraisalContentMapper.class);

  Set<ApplicationAppraisalContentDTO> toAppraisalContentDTO(
      Set<ApplicationAppraisalContentEntity> appraisalContents);

  @Mapping(target = "detail", expression = "java(convertDetailToSet(entity.getDetail()))")
  @Mapping(target = "detailValue", expression = "java(convertDetailToSet(entity.getDetailValue()))")
  ApplicationAppraisalContentDTO toDTO(ApplicationAppraisalContentEntity entity);

  Set<ApplicationAppraisalContentEntity> toAppraisalContentEntities(
      Set<ApplicationAppraisalContentDTO> appraisalContents);

  @Mapping(target = "detail", expression = "java(convertDetailToString(dto.getDetail()))")
  @Mapping(target = "detailValue", expression = "java(convertDetailToString(dto.getDetailValue()))")
  ApplicationAppraisalContentEntity toEntity(ApplicationAppraisalContentDTO dto);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceAppraisalContentEntity(@MappingTarget ApplicationAppraisalContentEntity e1,
      ApplicationAppraisalContentEntity e2);

  default Set<String> convertDetailToSet(String detail) {
    return StringUtils.isNotBlank(detail)
        ? new HashSet<>(Arrays.asList(detail.split(",")))
        : null;
  }

  default String convertDetailToString(Set<String> detail) {
    return CollectionUtils.isNotEmpty(detail) ? String.join(",", detail) : null;
  }


  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "application", ignore = true)
  ApplicationAppraisalContentEntity copyApplicationAppraisalContentEntity(ApplicationAppraisalContentEntity applicationAppraisalContentEntity);

  Set<ApplicationAppraisalContentEntity> copyApplicationAppraisalContentEntities(Set<ApplicationAppraisalContentEntity> applicationAppraisalContentEntities);

}
