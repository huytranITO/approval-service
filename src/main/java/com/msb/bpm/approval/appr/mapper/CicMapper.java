package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.CicDTO;
import com.msb.bpm.approval.appr.model.dto.CicDTO.CicDetail;
import com.msb.bpm.approval.appr.model.dto.cic.CicGroupDetail;
import com.msb.bpm.approval.appr.model.entity.CicEntity;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CicMapper {

  CicMapper INSTANCE = Mappers.getMapper(CicMapper.class);

  Set<CicDTO.CicDetail> toCicDetails(Set<CicEntity> cics);

  CicGroupDetail toCicDetail(CicEntity cic);

  CicGroupDetail toCicDetail(CicDetail cic);

  Set<CicEntity> toCicEntities(Set<CicDetail> cicDetails);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceCicEntity(@MappingTarget CicEntity e1, CicEntity e2);

  @Mapping(target = "id", ignore = true)
  void referenceCicDetail(@MappingTarget CicDetail c1, CicDetail c2);
}
