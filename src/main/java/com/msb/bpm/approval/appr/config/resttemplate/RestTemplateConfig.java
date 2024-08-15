package com.msb.bpm.approval.appr.config.resttemplate;

import com.msb.bpm.approval.appr.exception.CustomResponseErrorHandler;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.camunda.community.rest.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RestTemplateConfig {

  private final RestTemplateProperties restTemplateProperties;
  private final CustomResponseErrorHandler customResponseErrorHandler;

  @Value( "${camunda.bpm.client.base-url}" )
  private String basePath;

  @Bean
  @Primary
  @RefreshScope
  public ApiClient getApiClient() {
      ApiClient apiClient = new ApiClient();
      if (basePath != null) {
          apiClient.setBasePath(basePath);
        }
        apiClient.setConnectTimeout(restTemplateProperties.getConnectionTimeout() * 1000);
        apiClient.setReadTimeout(restTemplateProperties.getReadTimeout() * 1000);
        return apiClient;
  }

  @Bean("restTemplateCustomHandler")
  public RestTemplate getRestTemplateCustomHandler(RestTemplateBuilder builder)
      throws NoSuchAlgorithmException, KeyManagementException {
    ClientHttpRequestFactory requestFactory = requestFactory();
    return builder
        .setConnectTimeout(Duration.ofSeconds(restTemplateProperties.getConnectionTimeout()))
        .setReadTimeout(Duration.ofSeconds(restTemplateProperties.getReadTimeout()))
        .errorHandler(customResponseErrorHandler)
        .requestFactory(() -> requestFactory).build();
  }

  @Bean
  @Primary
  public RestTemplate restTemplate(RestTemplateBuilder builder)
      throws NoSuchAlgorithmException, KeyManagementException {
    ClientHttpRequestFactory requestFactory = requestFactory();
    return builder
        .setConnectTimeout(Duration.ofSeconds(restTemplateProperties.getConnectionTimeout()))
        .setReadTimeout(Duration.ofSeconds(restTemplateProperties.getReadTimeout()))
        .requestFactory(() -> requestFactory).build();
  }

  private ClientHttpRequestFactory requestFactory()
      throws KeyManagementException, NoSuchAlgorithmException {
    CloseableHttpClient httpClient = HttpClients.custom()
        .setSSLContext(sslContext())
        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        .build();
    return new HttpComponentsClientHttpRequestFactory(httpClient);
  }

  private SSLContext sslContext()
      throws NoSuchAlgorithmException, KeyManagementException {
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }

          public void checkClientTrusted(
              X509Certificate[] certs, String authType) {
            log.info(authType);
          }

          public void checkServerTrusted(
              X509Certificate[] certs, String authType) {
            log.info(authType);
          }
        }
    };
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustAllCerts, new SecureRandom());
    return sslContext;
  }
}

