package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditRatingsEntity;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationCreditRatingsMapper {

  ApplicationCreditRatingsMapper INSTANCE = Mappers.getMapper(ApplicationCreditRatingsMapper.class);

  Set<ApplicationCreditRatingsDTO> toApplicationCreditRatings(Set<ApplicationCreditRatingsEntity> creditRatingsEntities);

  ApplicationCreditRatingsDTO toApplicationCreditRatings(ApplicationCreditRatingsEntity creditRatingsEntities);
}
