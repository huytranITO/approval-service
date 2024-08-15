package com.msb.bpm.approval.appr.model.response.integration;

import com.msb.bpm.approval.appr.model.dto.ApplicationHistoricIntegrationDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationHistoricIntegrationResponse {
  private List<ApplicationHistoricIntegrationDTO> items;
  private int totalElements;
  private int page;
  private int size;
}
