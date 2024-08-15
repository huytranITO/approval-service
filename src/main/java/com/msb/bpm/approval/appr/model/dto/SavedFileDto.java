package com.msb.bpm.approval.appr.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SavedFileDto {

  Long id;
  Long checklistDtlId;
  String fileId;
  String filePath;
  String fileName;
  String fileType;
}
