package com.msb.bpm.approval.appr.model.response.cic;

import java.util.Map;
import lombok.Data;

@Data
public class CICErrorResponseData {

  private Map<String, CICFieldErrorDetail> errorDetail;

}
