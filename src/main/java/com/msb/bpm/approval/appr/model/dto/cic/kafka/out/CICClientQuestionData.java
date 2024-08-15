package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.msb.bpm.approval.appr.model.dto.cic.FieldName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CICClientQuestionData {

  @FieldName("MA_CIC")
  private String cicCode;

  @FieldName("TRANG_THAI")
  private String status;

  @FieldName("ID")
  private String clientQuestionId;

  @FieldName("LOAI_SP")
  private String productCode;

  @FieldName("ERROR_CODE_LIST")
  private String errorCodeList;

  public boolean hasData() {
    return StringUtils.hasText(status)
        && StringUtils.hasText(clientQuestionId);
  }

  public Integer statusAsInt() {
    if (!StringUtils.hasText(status)) {
      return null;
    }
    Integer result = null;

    try{
      result = Integer.parseInt(status);
    } catch (Exception e) {
      log.error("Parse status error! status " + status, e);
    }

    return result;
  }

  public Long clientQuestionIdAsLong() {
    if (!StringUtils.hasText(clientQuestionId)) {
      return null;
    }
    Long result = null;

    try{
      result = Long.parseLong(clientQuestionId);
    } catch (Exception e) {
      log.error("Parse clientQuestionId error! clientQuestionId " + clientQuestionId, e);
    }

    return result;
  }
}
