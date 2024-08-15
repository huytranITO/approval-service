package com.msb.bpm.approval.appr.model.dto.statisticfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResult {

  private String path;

  private Long fileSize;

  private String fileId;

  private String bucketName;

  private String fileType;

  private String cicDataJson;

}
