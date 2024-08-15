package com.msb.bpm.approval.appr.client.loanproposal;

import static com.msb.bpm.approval.appr.enums.loanproposal.LoanProposalEndpoint.REPLACE_DATA_APPLICATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.enums.loanproposal.LoanProposalEndpoint;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2CreateApplicationRequest;
import com.msb.bpm.approval.appr.model.request.loanproposal.ComparatorApplicationRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.model.response.loanproposal.ComparatorApplicationResponse;
import com.msb.bpm.approval.appr.model.response.loanproposal.LoanProposalResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@AllArgsConstructor
public class LoanProposalClient extends AbstractClient {

  private final RestTemplate restTemplate;
  private final LoanProposalConfigProperties properties;
  private final ObjectMapper objectMapper;
  @Override
  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public ComparatorApplicationResponse callLoanProposalService(String bpmId, ComparatorApplicationRequest request) {
    try {
      String endpoint = this.properties
          .getEndpoint()
          .get(LoanProposalEndpoint.COMPARATOR_APPLICATION.getValue())
          .getUrl();

      log.info("[CALL_API_LOAN_PROPOSAL_SERVICE] call api {} with bpmId {} and request : {}",
          endpoint, bpmId, JsonUtil.convertObject2String(request, objectMapper));
      Map<String, String> vars = new HashMap<>();
      vars.put("bpmId", bpmId);

      String token = HeaderUtil.getToken();
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBearerAuth(token);
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
      HttpEntity<?> http = new HttpEntity<>(request, httpHeaders);

      String uri = UriComponentsBuilder
          .fromUriString(this.properties.getBaseUrl() + endpoint)
          .buildAndExpand(vars)
          .toUriString();

      LoanProposalResponse loanProposalResponse = exchange(uri, HttpMethod.POST, http, LoanProposalResponse.class);

      ComparatorApplicationResponse response = new ComparatorApplicationResponse();
      if (loanProposalResponse.getData() != null) {
        response = loanProposalResponse.getData();
      }
      log.info("[CALL_API_LOAN_PROPOSAL_SERVICE] call api {} with bpmId {} and request : {}, response : {}", endpoint,
          bpmId,
          JsonUtil.convertObject2String(request, objectMapper),
          JsonUtil.convertObject2String(response, objectMapper));

      return response;
    } catch (Exception exception) {
      log.error("method: callLoanProposalService with bpmId = {}, error: ", bpmId, exception);
      return null;
    }
  }

  /**
   * Set lại dữ liệu application_data của loan-proposal
   *
   * @param request
   */
  public void replaceDataApplication(PostCmsV2CreateApplicationRequest request) {
    String url = UriComponentsBuilder.fromUriString(
            properties.getBaseUrl() + properties.getEndpoint().get(REPLACE_DATA_APPLICATION.getValue())
                .getUrl())
        .build()
        .toString();

    log.info("[CALL_REPLACE_DATA_APPLICATION] api : {} with request : [{}]", url,
        JsonUtil.convertObject2String(request, objectMapper));

    ApiResponse response;

    try {
      response = exchange(url, HttpMethod.POST,
          new HttpEntity<>(request, buildCommonHeaders(HeaderUtil.getToken())), ApiResponse.class);
    } catch (HttpClientErrorException ex) {
      log.error("[CALL_REPLACE_DATA_APPLICATION] error : [{}]", ex.getResponseBodyAsString());
      return;
    }

    log.info("[CALL_REPLACE_DATA_APPLICATION] response : [{}]",
        JsonUtil.convertObject2String(response, objectMapper));
  }
}
