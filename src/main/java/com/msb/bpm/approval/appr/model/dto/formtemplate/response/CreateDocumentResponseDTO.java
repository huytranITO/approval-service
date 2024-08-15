package com.msb.bpm.approval.appr.model.dto.formtemplate.response;

import com.fasterxml.jackson.annotation.JsonProperty;import com.msb.bpm.approval.appr.model.response.email.EmailResponse.DataMsg;import lombok.AllArgsConstructor;import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;import lombok.Value;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateDocumentResponseDTO {

  private String code;

  private String message;

  @JsonProperty("data")
  private DataFormTemplate data;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public class DataFormTemplate {
    private String fileName;
    private String filePath;
    private String fileId;
    private String fileType;
    private long fileSize;
    private String bucket;
  }



}
