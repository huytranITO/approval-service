package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.enums.feedback.FeedbackCommentTitle;
import com.msb.bpm.approval.appr.model.dto.ApplicationHistoryFeedbackDTO;
import com.msb.bpm.approval.appr.model.dto.comment.ApplicationCommentDataDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationHistoryFeedbackEntity;
import com.msb.bpm.approval.appr.model.response.usermanager.GetUserProfileResponse;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

@Mapper
public interface FeedbackHistoryMapper {

  FeedbackHistoryMapper INSTANCE = Mappers.getMapper(FeedbackHistoryMapper.class);

  List<ApplicationHistoryFeedbackDTO> toFeedbackHistoryDto(
      List<ApplicationHistoryFeedbackEntity> data);

  @Mapping(target = "feedbackContent", expression = "java(convertContentToFeedback(entity.getFeedbackContent(), entity.getComment()))")
  ApplicationHistoryFeedbackDTO toApplicationHistoryFeedbackDto(
      ApplicationHistoryFeedbackEntity entity);

  @Mapping(target = "feedbackContent", expression = "java(convertContentToFeedback(entity.getFeedbackContent(), entity.getComment()))")
  @Mapping(target = "editable", expression = "java(isEditableHistoryFeedback(entity, applicationEntity, userLogin))")
  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "createdBy", source = "entity.createdBy")
  @Mapping(target = "createdAt", source = "entity.createdAt")
  @Mapping(target = "updatedBy", source = "entity.updatedBy")
  @Mapping(target = "updatedAt", source = "entity.updatedAt")
  @Mapping(target = "createdPhoneNumber", source = "entity.createdPhoneNumber")
  ApplicationHistoryFeedbackDTO toApplicationHistoryFeedbackDtoWithApplication(
      ApplicationHistoryFeedbackEntity entity, ApplicationEntity applicationEntity,
      String userLogin);

  default String convertContentToFeedback(String feedbackContent, byte[] content) {
    if (feedbackContent != null) {
      return feedbackContent;
    } else {
      return JsonUtil.convertBytes2String(content);
    }
  }

  default boolean isEditableHistoryFeedback(
      ApplicationHistoryFeedbackEntity applicationHistoryFeedbackEntity,
      ApplicationEntity applicationEntity, String userLogin) {
    boolean result = false;
    // content not null, assignee == username, receptionAt < updatedAt
    if (org.apache.commons.lang3.StringUtils.isNotEmpty(
        applicationHistoryFeedbackEntity.getFeedbackContent())
        && org.apache.commons.lang3.StringUtils.isNotEmpty(applicationEntity.getAssignee())
        && applicationEntity.getAssignee().equals(userLogin) && applicationEntity.getAssignee()
        .equalsIgnoreCase(applicationHistoryFeedbackEntity.getUsername()) && (
        applicationEntity.getReceptionAt() == null || applicationEntity.getReceptionAt()
            .isBefore(applicationHistoryFeedbackEntity.getUpdatedAt()))) {
      result = true;
    }
    return result;
  }

  default ApplicationHistoryFeedbackEntity createApplicationHistoryFeedbackEntity(
      ApplicationEntity applicationEntity,
      GetUserProfileResponse userProfileResponse, String comment) {
    ApplicationHistoryFeedbackEntity feedbackEntity = new ApplicationHistoryFeedbackEntity();
    feedbackEntity.setApplication(applicationEntity);
    feedbackEntity.setUsername(userProfileResponse.getUser().getUsername());
    feedbackEntity.setUserRole(applicationEntity.getProcessingRole());
    feedbackEntity.setFeedbackAt(LocalDateTime.now());
    feedbackEntity.setCreatedPhoneNumber(userProfileResponse.getUser().getPhoneNumber());
    feedbackEntity.setComment(comment.getBytes(StandardCharsets.UTF_8));
    return feedbackEntity;
  }

  default ApplicationHistoryFeedbackEntity convertAppCommentToApplicationFeedback(
      ApplicationCommentDataDTO applicationCommentDataDTO,
      ApplicationEntity applicationEntity, GetUserProfileResponse userProfileResponse) {
    if (applicationCommentDataDTO == null) {
      return null;
    }
    String comment = "";
    if (StringUtils.hasText(applicationCommentDataDTO.getCustomer())) {
      comment += FeedbackCommentTitle.CUSTOMER_AND_CUSTOMER_RELATIONSHIP_INFO.getValue() +
          applicationCommentDataDTO.getCustomer() + "\n\n";
    }
    if (StringUtils.hasText(applicationCommentDataDTO.getApplicationCredit())) {
      comment += FeedbackCommentTitle.APPLICATION_CREDIT.getValue() +
          applicationCommentDataDTO.getApplicationCredit() + "\n\n";
    }
    if (StringUtils.hasText(applicationCommentDataDTO.getApplicationIncome())) {
      comment += FeedbackCommentTitle.INCOME_INFO.getValue() +
          applicationCommentDataDTO.getApplicationIncome() + "\n\n";
    }
    if (StringUtils.hasText(applicationCommentDataDTO.getAssetInfo())) {
      comment += FeedbackCommentTitle.ASSET_INFO.getValue() +
          applicationCommentDataDTO.getAssetInfo() + "\n\n";
    }
    if (StringUtils.hasText(
        applicationCommentDataDTO.getDocumentCommentData().getComment())) {
      comment += FeedbackCommentTitle.DOCUMENT_COMMENT_DATA.getValue() +
          applicationCommentDataDTO.getDocumentCommentData().getComment();
    }
    return createApplicationHistoryFeedbackEntity(applicationEntity, userProfileResponse, comment);
  }
}
