package com.msb.bpm.approval.appr.model.dto.cic.report.tnpn2;

import com.msb.bpm.approval.appr.model.dto.cic.report.CICReportData;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListCamKetNgoaiBang;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListChamTT36T;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListDauTuTraiPhieu;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListDiemTD;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListDiemXHTD;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListDuNoVay12T;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListHopDongTinDung;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListNoCanCY12T;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListNoVAMC;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListNoXau36T;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListTCTDTT;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListTheTD;
import com.msb.bpm.approval.appr.model.dto.cic.report.ListTheTD12T;
import java.util.Collections;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

@Data
@EqualsAndHashCode(callSuper = false)
public class CICReportTNPN2Data extends CICReportData {

  private ListData lstData;
  private ListDuNoVay12T lstDuNoVay12t;
  private String messageDuNoVay12t;
  private ListNoXau36T lstNoXau36t;
  private String messageNoXau36t;
  private ListNoCanCY12T lstNoCanCY12t;
  private String messageNoCanCY12t;
  private ListNoVAMC lstNoVAMC;
  private String messageNoVAMC;
  private ListTheTD lstTheTD;
  private String messageTheTD;
  private ListTheTD12T lstTheTD12t;
  private String messageTheTD12t;
  private ListChamTT36T lstChamTT36t;
  private String messageChamTT36t;
  private ListDauTuTraiPhieu lstDauTuTraiPhieu;
  private String messageDauTuTraiPhieu;
  private ListCamKetNgoaiBang lstCamKetNgoaiBang;
  private String messageCamKetNgoaiBang;
  private ListHopDongTinDung lstHopDongTinDung;
  private String messageHopDongTinDung;
  private ListTCTDTT lstTCTDTT;
  private String messageTCTDTT;

  private ListDiemXHTD lstDiemXHTD;
  private ListDiemTD lstDiemTD;

  @Override
  public Map<String, Object> parameters() {
    Map<String, Object> result = super.parameters();

    if (lstData != null
        && !CollectionUtils.isEmpty(lstData.getData())) {
      result.put("lstData", lstData.getData());
    } else {
      result.remove("lstData");
    }

    if (lstDuNoVay12t != null
        && !CollectionUtils.isEmpty(lstDuNoVay12t.getData())) {
      result.put("lstDuNoVay12t", lstDuNoVay12t.getData());
    } else {
      result.remove("lstDuNoVay12t");
    }

    if (lstNoXau36t != null
        && !CollectionUtils.isEmpty(lstNoXau36t.getData())) {
      result.put("lstNoXau36t", lstNoXau36t.getData());
    } else {
      result.remove("lstNoXau36t");
    }

    if (lstNoCanCY12t != null
        && !CollectionUtils.isEmpty(lstNoCanCY12t.getData())) {
      result.put("lstNoCanCY12t", lstNoCanCY12t.getData());
    } else {
      result.remove("lstNoCanCY12t");
    }

    if (lstNoVAMC != null
        && !CollectionUtils.isEmpty(lstNoVAMC.getData())) {
      result.put("lstNoVAMC", lstNoVAMC.getData());
    } else {
      result.remove("lstNoVAMC");
    }

    if (lstTheTD != null
        && !CollectionUtils.isEmpty(lstTheTD.getData())) {
      result.put("lstTheTD", lstTheTD.getData());
    } else {
      result.remove("lstTheTD");
    }

    if (lstTheTD12t != null
        && !CollectionUtils.isEmpty(lstTheTD12t.getData())) {
      result.put("lstTheTD12t", lstTheTD12t.getData());
    } else {
      result.remove("lstTheTD12t");
    }

    if (lstChamTT36t != null
        && !CollectionUtils.isEmpty(lstChamTT36t.getData())) {
      result.put("lstChamTT36t", lstChamTT36t.getData());
    } else {
      result.remove("lstChamTT36t");
    }

    if (lstDauTuTraiPhieu != null
        && !CollectionUtils.isEmpty(lstDauTuTraiPhieu.getData())) {
      result.put("lstDauTuTraiPhieu", lstDauTuTraiPhieu.getData());
    } else {
      result.remove("lstDauTuTraiPhieu");
    }

    if (lstCamKetNgoaiBang != null
        && !CollectionUtils.isEmpty(lstCamKetNgoaiBang.getData())) {
      result.put("lstCamKetNgoaiBang", lstCamKetNgoaiBang.getData());
    } else {
      result.remove("lstCamKetNgoaiBang");
    }

    if (lstHopDongTinDung != null
        && !CollectionUtils.isEmpty(lstHopDongTinDung.getData())) {
      result.put("lstHopDongTinDung", lstHopDongTinDung.getData());
    } else {
      result.remove("lstHopDongTinDung");
    }

    if (lstTCTDTT != null
        && !CollectionUtils.isEmpty(lstTCTDTT.getData())) {
      result.put("lstTCTDTT", lstTCTDTT.getData());
    } else {
      result.remove("lstTCTDTT");
    }

    if (lstDiemXHTD != null
            && !CollectionUtils.isEmpty(lstDiemXHTD.getData())) {
      result.put("lstDiemXHTD", lstDiemXHTD.getData());
    } else {
      result.remove("lstDiemXHTD");
    }

    if (lstDiemTD != null
            && !CollectionUtils.isEmpty(lstDiemTD.getData())) {
      result.put("lstDiemTD", lstDiemTD.getData());
    } else {
      result.remove("lstDiemTD");
    }
    return result;
  }
}
