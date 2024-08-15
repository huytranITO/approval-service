package com.msb.bpm.approval.appr.model.dto.cas;

import com.msb.bpm.approval.appr.model.dto.cic.FieldName;
import lombok.Data;

@Data
public class CASResultData {


  @FieldName("CUSTOMER_NO")
  private String customerNo;

  @FieldName("PROFILE_ID")
  private String profileId;

  @FieldName("MODEL_TYPE_CODE")
  private String modelTypeCode;

  @FieldName("MODEL_TYPE_INFO_ID")
  private String modelTypeInfoId;

  @FieldName("RANK1")
  private String rank1;

  @FieldName("SCORE1")
  private String score1;

  @FieldName("LEGAL_DOC_NO")
  private String legalDocNo;

  @FieldName("CREATED_BY")
  private String createdBy;

  @FieldName("SCORING_ACTION")
  private String scoringAction;

  @FieldName("STATUS")
  private String status;

  @FieldName("CUSTOMER_NAME")
  private String customerName;

  @FieldName("RECOMMENDATION")
  private String recommendation;

  @FieldName("BUS_REG_NUMBER")
  private String busRegNumber;

  @FieldName("TAXCODE")
  private String taxcode;

  @FieldName("TYPE_OF_MODEL")
  private String typeOfModel;

  @FieldName("DATE_CREATED")
  private String dateCreated;

  @FieldName("ETL_DATE")
  private String etlDate;

}
