package com.msb.bpm.approval.appr.model.dto.formtemplate.response;

import java.io.Serializable;
import lombok.Data;

@Data
public class ChecklistFileMessageDTO implements Serializable {

  private static final long serialVersionUID = -713796410043088545L;
  private Long id;
  private String code;

  private String fileName;

  private String filePath;

  private String fileId;

  private String fileType;

  private long fileSize;

  private String bucket;

  private String createdBy;
}
