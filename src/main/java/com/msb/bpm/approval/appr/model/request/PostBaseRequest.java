package com.msb.bpm.approval.appr.model.request;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_CHECK_LIST_TAB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_COMPLETE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_DEBT_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_FIELD_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_INITIALIZE_INFO;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_SUBMIT_APP;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.TYPE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSET_INFO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.msb.bpm.approval.appr.model.request.data.*;
import com.msb.bpm.approval.appr.model.request.flow.PostCompleteRequest;
import com.msb.bpm.approval.appr.model.request.flow.PostSubmitRequest;
import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PostInitializeInfoRequest.class, name = POST_INITIALIZE_INFO),
    @JsonSubTypes.Type(value = PostFieldInformationRequest.class, name = POST_FIELD_INFO),
    @JsonSubTypes.Type(value = PostDebtInfoRequest.class, name = POST_DEBT_INFO),
    @JsonSubTypes.Type(value = PostCompleteRequest.class, name = POST_COMPLETE),
    @JsonSubTypes.Type(value = PostSubmitRequest.class, name = POST_SUBMIT_APP),
    @JsonSubTypes.Type(value = PostCheckListTabRequest.class, name = POST_CHECK_LIST_TAB),
    @JsonSubTypes.Type(value = PostAssetInfoRequest.class, name = POST_ASSET_INFO)
})
public abstract class PostBaseRequest {

  @NotBlank
  private String type;

  private String bpmId;
}
