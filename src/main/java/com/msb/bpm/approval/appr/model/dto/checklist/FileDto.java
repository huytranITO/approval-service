package com.msb.bpm.approval.appr.model.dto.checklist;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.msb.bpm.approval.appr.constant.Constant;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class FileDto implements Serializable {
  private static final long serialVersionUID = -3636624261429239345L;
  private Long id;
  private String bucket;
  @NotBlank
  private String minioPath;
  private String fileName;
  private String fileType;
  private String fileSize; // đơn vị: MB
  private String createdBy;
  private Map<String, String> props;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  private LocalDateTime createdAt;
  private boolean isFileOfPreviousRoles;
}
