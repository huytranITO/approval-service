package com.msb.bpm.approval.appr.model.response.cic;

import java.util.List;
import lombok.Data;

@Data
public class CICFieldErrorDetail {

  private String value;

  private List<String> errorCodeList;

  private List<String> errorMessageList;

}
