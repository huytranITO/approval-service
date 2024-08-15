package com.msb.bpm.approval.appr.model.dto.cic;

import lombok.Data;

@Data
public class CICResultData {

//  @FieldName("fieldindex")
//  private String fieldIndex;

  @FieldName("MAINID")
  private String mainId;

//  @FieldName("modelcode")
//  private String modelCode;

  @FieldName("FIELD_NAME")
  private String fieldName;

  @FieldName("FIELD_VALUE")
  private String fieldValue;

//  @FieldName("versionid")
//  private String versionId;
//
//  @FieldName("matrixid")
//  private String matrixId;
//
//  @FieldName("purposecode")
//  private String purposeCode;
//
//  @FieldName("processid")
//  private String processId;

}
