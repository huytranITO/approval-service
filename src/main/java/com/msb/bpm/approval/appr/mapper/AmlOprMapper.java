package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.AmlOprDTO.AmlOprDtl;
import com.msb.bpm.approval.appr.model.entity.AmlOprEntity;
import com.msb.bpm.approval.appr.model.response.collateral.OpRiskAssetResponse.OpRiskAssetData;
import com.msb.bpm.approval.appr.model.response.oprisk.OpRiskResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AmlOprMapper {

  AmlOprMapper INSTANCE = Mappers.getMapper(AmlOprMapper.class);

  Set<AmlOprDtl> toAmlOprs(Set<AmlOprEntity> amlOprs);
  @Mapping(target = "resultOnBlackList", source = "reasonInList")
  AmlOprEntity toEntity(OpRiskResponse response);
  @Mapping(target = "resultOnBlackList", source = "reasonInList")
  @Mapping(target = "syncDate", expression = "java(mapSyncDate(response))")
  AmlOprEntity toEntity(OpRiskAssetData response);
  @Mapping(target = "createdAt", expression = "java(mapCreatedAt(entity))")
  @Mapping(target = "reasonInList", source = "resultOnBlackList")
  OpRiskAssetData toDto(AmlOprEntity entity);
  @Mapping(target = "applicationId", ignore = true)
  List<OpRiskAssetData> toDtos(List<AmlOprEntity> amlOprEntities);
  @Mapping(target = "application", ignore = true)
  List<AmlOprEntity> toEntityList(List<OpRiskAssetData> response);

  default LocalDateTime mapSyncDate(OpRiskAssetData res) {
    return ObjectUtils.isNotEmpty(res.getUpdatedAt()) ? res.getUpdatedAt() : res.getCreatedAt();
  }
  /**
   * mapCreatedAt
   * Nếu bản ghi được đồng bộ từ Collateral-Service thì createdAt sẽ lấy theo syncDate
   * Nếu bản ghi được thêm mới tại Approval-Service thì createdAt sẽ lấy theo thời gian cập nhật gần nhất
   */
  default LocalDateTime mapCreatedAt(AmlOprEntity entity) {
    // Bản ghi được đồng bộ từ Collateral-Service
    if (ObjectUtils.isNotEmpty(entity.getSyncDate())) {
      return entity.getSyncDate();
    }
    // Bản ghi được thêm mới tại Approval-Service
    return ObjectUtils.isNotEmpty(entity.getUpdatedAt()) ? entity.getUpdatedAt() : entity.getCreatedAt();
  }
}
