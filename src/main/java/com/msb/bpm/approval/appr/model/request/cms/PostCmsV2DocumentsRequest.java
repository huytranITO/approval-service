package com.msb.bpm.approval.appr.model.request.cms;


import com.msb.bpm.approval.appr.enums.common.LdpConfirmStatus;
import com.msb.bpm.approval.appr.validator.CustomValidationFieldDependOn;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@CustomValidationFieldDependOn.List({
    @CustomValidationFieldDependOn(
        field = "reason",
        fieldDependOns = "confirmStatus"
    )
})
public class PostCmsV2DocumentsRequest {

  @NotBlank
  @Valid
  @Size(max = 20)
  private String actionType;

  @Valid
  private List<Document> documents;

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class Document {

    @Valid
    @NotBlank
    @Size(max = 36)
    private String cmsDomainReferenceId;

    @NotBlank
    @Valid
    @Size(max = 20)
    private String docCode;

    @Size(max = 255)
    private String docName;

    @NotBlank
    @Valid
    @Size(max = 50)
    private String group;

    @Valid
    private List<File> files;

    private Object metadata;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class File {
    @NotBlank
    @Size(max = 500)
    private String minioPath;
    @NotBlank
    @Valid
    @Size(max = 20)
    private String status;
    @NotBlank
    @Valid
    @Size(max = 255)
    private String fileName;
  }

  @NotNull
  @Valid
  private LdpConfirmStatus confirmStatus;

  @Size(max = 255)
  @Valid
  private String reason;
}
