package com.msb.bpm.approval.appr.model.request.cms;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCmsDocumentsRequest {

  @NotBlank
  private String actionType;

  @NotNull
  private List<Document> documents;

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class Document {
    @NotBlank
    private String cmsDomainReferenceId;
    @NotBlank
    private String docCode;
    @NotBlank
    private String docName;
    @NotBlank
    private String group;

    private List<File> files;

    private Object metadata;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class File {
    @NotBlank
    private String minioPath;
    @NotBlank
    private String status;
    @NotBlank
    private String fileName;
  }
}
