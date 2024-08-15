package com.msb.bpm.approval.appr.mapper.draft;

import com.msb.bpm.approval.appr.model.dto.DebtInfoDTO;
import com.msb.bpm.approval.appr.model.dto.FieldInforDTO;
import com.msb.bpm.approval.appr.model.dto.InitializeInfoDTO;
import com.msb.bpm.approval.appr.model.request.data.PostDebtInfoRequest;
import com.msb.bpm.approval.appr.model.request.data.PostFieldInformationRequest;
import com.msb.bpm.approval.appr.model.request.data.PostInitializeInfoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 9/11/2023, Thursday
 **/
@Mapper
public interface EntityDraftMapper {

  EntityDraftMapper INSTANCE = Mappers.getMapper(EntityDraftMapper.class);

  PostInitializeInfoRequest dtoToPostInitializeInfoRequest(InitializeInfoDTO initializeInfoDTO);

  PostFieldInformationRequest dtoToPostFieldInformationRequest(FieldInforDTO fieldInforDTO);

  PostDebtInfoRequest dtoToPostDebtInfoRequest(DebtInfoDTO debtInfoDTO);
}
