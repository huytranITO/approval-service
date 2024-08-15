package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.application.ApplicationContactDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsApplicationContactDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationContactEntity;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 29/8/2023, Tuesday
 **/
@Mapper
public interface ApplicationContactMapper {

  ApplicationContactMapper INSTANCE = Mappers.getMapper(ApplicationContactMapper.class);


  Set<ApplicationContactDTO> entityToDTO(Set<ApplicationContactEntity> contactEntity);

  Set<CmsApplicationContactDTO> entityToCmsApplicationContactDto(
      Set<ApplicationContactEntity> applicationContactEntity);

  List<CmsApplicationContactDTO> entityToCmsApplicationContactDtoList(
      List<ApplicationContactEntity> applicationContactEntity);
}
