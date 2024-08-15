package com.msb.bpm.approval.appr.model.response.cic;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DebtDetailByOrganizationDto {

    private String tenTCTD;

    private String maTCTD;

    private BigDecimal duNoNganHanVND;

    private BigDecimal duNoNganHanUSD;

    private BigDecimal duNoTrungDaiVND;

    private BigDecimal duNoTrungDaiUSD;

    private BigDecimal duNoKhacVND;

    private BigDecimal duNoKhacUSD;

    private BigDecimal tongVND;

    private BigDecimal tongUSD;
}
