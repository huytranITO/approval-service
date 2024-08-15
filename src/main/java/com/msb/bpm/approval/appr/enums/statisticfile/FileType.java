package com.msb.bpm.approval.appr.enums.statisticfile;

import lombok.Getter;

@Getter
public enum FileType {
  PDF("pdf"),

  ;

  private final String fileType;

  FileType(String fileType) {
    this.fileType = fileType;
  }
}
