package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.enums.card.IntegrationResponseCode;
import com.msb.bpm.approval.appr.enums.card.IntegrationStatusDetail;
import com.msb.bpm.approval.appr.model.dto.ApplicationHistoricIntegrationDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoricIntegration;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationHistoricIntegrationMapper {

  ApplicationHistoricIntegrationMapper INSTANCE = Mappers.getMapper(
      ApplicationHistoricIntegrationMapper.class);

  @Mapping(target = "integratedStatusDetail", expression = "java(convertStatusDetail(data))")
  @Mapping(target = "errorDescription", expression = "java(convertErrorDescription(data))")
  ApplicationHistoricIntegrationDTO toHistoricIntegrationDto(ApplicationHistoricIntegration data);

  List<ApplicationHistoricIntegrationDTO> toHistoricIntegration(
      List<ApplicationHistoricIntegration> data);

  default String convertStatusDetail(ApplicationHistoricIntegration data) {
    return Arrays.stream(IntegrationStatusDetail.values())
        .filter(statusDetail -> statusDetail.getValue().equals(data.getIntegratedStatusDetail()))
        .findFirst().map(Enum::name).orElse(null);
  }

  default String convertErrorDescription(ApplicationHistoricIntegration data) {
    if (StringUtils.isBlank(data.getErrorCode())) {
      return null;
    }
    return data.getErrorCode()
        .equals(String.valueOf(IntegrationResponseCode.CLIENT_IS_EXIST.getCode())) ? null
        : data.getErrorDescription();
  }
}
