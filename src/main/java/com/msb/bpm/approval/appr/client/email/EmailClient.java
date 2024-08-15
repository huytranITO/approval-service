package com.msb.bpm.approval.appr.client.email;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import com.msb.bpm.approval.appr.model.response.email.EmailResponse;
import com.msb.bpm.approval.appr.util.HeaderUtil;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/5/2023, Friday
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailClient extends AbstractClient {

  private final RestTemplate restTemplate;

  private final ApplicationConfig applicationConfig;


  @Override
  protected RestTemplate getRestTemplate() {
    return this.restTemplate;
  }

  public EmailResponse sendEmail(EmailRequest request, String token) {
    log.info("sendEmail START with request={}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(token);
      HttpEntity<EmailRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getEmail()
              .getUrlBase() + applicationConfig.getClient().getEmail().getUrlSendEmail())
          .toUriString();
      EmailResponse response = super.exchange(uri, HttpMethod.POST, entity,
          EmailResponse.class);
      log.info("sendEmail END with request={}, response={}", request, response);
      return response;
    } catch (Exception e) {
      log.error("sendEmail with request={} -> Error: ", request, e);
      throw new ApprovalException(DomainCode.VERIFY_OTP_ERROR, new Object[]{request});
    }
  }

  public EmailResponse sendInternalEmail(EmailRequest request) {
    log.info("sendEmail START with request={}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authentication", applicationConfig.getJwt().buildBase64());
      HttpEntity<EmailRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getEmail()
              .getUrlBase() + applicationConfig.getClient().getEmail().getUrlSendInternalEmail())
          .toUriString();
      EmailResponse response = super.exchange(uri, HttpMethod.POST, entity,
          EmailResponse.class);
      log.info("sendEmail END with request={}, response={}", request, response);
      return response;
    } catch (Exception e) {
      log.error("sendEmail with request={} -> Error: ", request, e);
      throw new ApprovalException(DomainCode.VERIFY_OTP_ERROR, new Object[]{request});
    }
  }


}
