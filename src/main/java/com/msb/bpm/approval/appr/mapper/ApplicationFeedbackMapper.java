package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.enums.common.DocumentStatus;
import com.msb.bpm.approval.appr.model.dto.comment.DocumentCommentDataDTO;
import com.msb.bpm.approval.appr.model.dto.comment.DocumentFileDataDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.DocumentCommentFbDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.DocumentFileCommentFbDTO;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApplicationFeedbackMapper {

  ApplicationFeedbackMapper INSTANCE = Mappers.getMapper(ApplicationFeedbackMapper.class);
  default DocumentCommentFbDTO convertDocumentCommentToDocumentFeedback(
      DocumentCommentDataDTO documentCommentDataDTO) {
    if (documentCommentDataDTO == null) {
      return null;
    }
    DocumentCommentFbDTO documentFeedbackDTO = new DocumentCommentFbDTO();
    documentFeedbackDTO.setComment(StringUtils.trim(documentCommentDataDTO.getComment()));
    documentFeedbackDTO.setFile(convertDocumentFileListToDocumentFileFeedbackList(
        documentCommentDataDTO.getDocumentFiles()));
    return documentFeedbackDTO;
  }


  @Mapping(target = "docCode", source = "code")
  @Mapping(target = "docName", source = "name")
  @Mapping(target = "group", source = "groupCode")
  @Mapping(target = "checkEnough", expression = "java(convertCheckEnough(documentFileDataDTO.getCheckEnough()))")
  DocumentFileCommentFbDTO convertDocumentFileToDocumentFileFeedback(
      DocumentFileDataDTO documentFileDataDTO);

  List<DocumentFileCommentFbDTO> convertDocumentFileListToDocumentFileFeedbackList(
      List<DocumentFileDataDTO> documentCommentDataDTOList);

  default String convertCheckEnough(Boolean checkEnough) {
    if (checkEnough == null) {
      return null;
    }
    if (checkEnough) {
      return DocumentStatus.ENOUGH.getValue();
    }
    return DocumentStatus.LACK.getValue();
  }
}
