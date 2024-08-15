package com.msb.bpm.approval.appr.client.otp;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.config.ApplicationConfig;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.otp.SendOtpRequest;
import com.msb.bpm.approval.appr.model.request.otp.VerifyOtpRequest;
import com.msb.bpm.approval.appr.model.response.otp.VerifyOtpResponse;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpClient extends AbstractClient {

  private final RestTemplate restTemplate;

  private final ApplicationConfig applicationConfig;


  @Override
  protected RestTemplate getRestTemplate() {
    return this.restTemplate;
  }

  public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
    log.info("verifyOtp START with request={}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<VerifyOtpRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getOtp()
              .getUrlBase() + applicationConfig.getClient().getOtp().getUrlVerify())
          .toUriString();
      VerifyOtpResponse response = super.exchange(uri, HttpMethod.POST, entity,
          VerifyOtpResponse.class);
      log.info("verifyOtp END with request={}, response={}", request, response);
      return response;
    } catch (Exception e) {
      log.error("verifyOtp with request={} -> Error: ", request, e);
      throw new ApprovalException(DomainCode.VERIFY_OTP_ERROR, new Object[]{request});
    }
  }

  public VerifyOtpResponse sendOtp(SendOtpRequest request) {
    log.info("sendOtp START with request={}", request);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(HeaderUtil.getToken());
      HttpEntity<SendOtpRequest> entity = new HttpEntity<>(request, headers);
      String uri = UriComponentsBuilder.fromUriString(applicationConfig.getClient().getOtp()
              .getUrlBase() + applicationConfig.getClient().getOtp().getUrlSend())
          .toUriString();
      VerifyOtpResponse response = super.exchange(uri, HttpMethod.POST, entity,
          VerifyOtpResponse.class);
      log.info("sendOtp END with request={}, response={}", request, response);
      return response;
    } catch (Exception e) {
      log.error("sendOtp with request={} -> Error: ", request, e);
      throw new ApprovalException(DomainCode.VERIFY_OTP_ERROR, new Object[]{request});
    }
  }
}
