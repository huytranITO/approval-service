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
public class ChiTietCk {
    private String tenTCTD;

    private BigDecimal giaTri;

    private String loaiTien;

    private String nhomNo;

    private String ngayBC;
}
