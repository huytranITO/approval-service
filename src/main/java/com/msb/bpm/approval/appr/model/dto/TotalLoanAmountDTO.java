package com.msb.bpm.approval.appr.model.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 15/5/2023, Monday
 **/
@Getter
@Setter
@With
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TotalLoanAmountDTO {
  private BigDecimal productAmount;
  private BigDecimal otherProductAmount;
}
