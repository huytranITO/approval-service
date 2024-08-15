package com.msb.bpm.approval.appr.model.response.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import static com.msb.bpm.approval.appr.constant.Constant.YYYY_MM_DD;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 7/10/2023, Saturday
 **/
@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValuationInfoResponse implements Serializable {
  private Long id;
  private String mvlId;
  private String status;
  @JsonFormat(pattern = YYYY_MM_DD)
  private LocalDate valuationDate;
  private BigDecimal valuationAmount;
  private String assetName;
  private String valuationAssetId;
}
