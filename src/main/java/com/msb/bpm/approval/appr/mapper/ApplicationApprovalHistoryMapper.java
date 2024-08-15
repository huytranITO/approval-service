package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationApprovalHistoryDTO;
import com.msb.bpm.approval.appr.model.dto.application.HistoryApprovalDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryApprovalEntity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationApprovalHistoryMapper {

  ApplicationApprovalHistoryMapper INSTANCE = Mappers.getMapper(
      ApplicationApprovalHistoryMapper.class);

  @Mapping(target = "reasons", expression = "java(convertDetailToSet(source.getReason()))")
  ApplicationApprovalHistoryDTO toApprovalHistoryDto(ApplicationHistoryApprovalEntity source);

  List<ApplicationApprovalHistoryDTO> toApprovalHistoryDtoList(List<ApplicationHistoryApprovalEntity> data);

  @Mapping(target = "reason", expression = "java(com.msb.bpm.approval.appr.util.Util.convertDetailToString(history.getReasons()))")
  @Mapping(target = "stepDescription", source = "step")
  @Mapping(target = "username", source = "userName")
  ApplicationHistoryApprovalEntity toHistoryApprovalEntity(HistoryApprovalDTO history);

  default Set<String> convertDetailToSet(String detail) {
    return StringUtils.isNotBlank(detail)
        ? new HashSet<>(Arrays.asList(detail.split(",")))
        : null;
  }
}
