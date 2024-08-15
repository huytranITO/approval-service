package com.msb.bpm.approval.appr.config;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@AllArgsConstructor
@RefreshScope
@NoArgsConstructor
@ConfigurationProperties(value = "approval-service")
public class ApplicationConfig {

  private JwtClient jwt;

  private CacheClientConfig cache;

  private Kafka kafka;

  private Client client;

  private Validation validation;

  private AccountSource accountSource;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JwtClient {

    private String secretKey;
    private String algorithm;
    private String user;

    public String buildBase64() {
      String basicAuth = this.user + ":" + this.secretKey;
      return Base64.encodeBase64String(basicAuth.getBytes());
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CacheClientConfig {

    private Integer permissionTimeLive = 10;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Kafka {

    private Map<String, TopicKafka> topic;
    private External external;
    private DataKafka dataKafka;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Client {
    private UserManager userManager;
    private Otp otp;
    private Email email;
    private Checklist checklist;
    private Customer customer;
    private Mercury mercury;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserManager {
    private String urlBase;
    private String urlPermission;
    private String urlGetRegionArea;
    private String getUserByRoles;
    private String getUserByUsername;
    private String findOrganization;

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class TopicKafka {
    private String topicName;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Otp {

    private String urlBase;
    private String urlVerify;
    private String urlSend;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Email {
    private String urlBase;
    private String urlSendEmail;
    private String urlSendInternalEmail;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Checklist {
    private String urlBase;
    private Map<String, EndPoint> endPoint;
  }

  @Data
  public static class EndPoint {
    private String url;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Customer {
    private String urlBase;
    private String urlCustomerRb;
    private String urlCustomerRbByList;
    private String urlCustomerMigrateVersion;
    private String urlCustomerDetail;
    private String urlCustomerRbMigrateVersion;
    private String urlCustomerRelationship;
    private String urlCustomerRelationshipDetail;
    private String urlCreateCustomerEb;
    private String urlFindByList;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Mercury {
    private String urlBase;
    private String urlGetPlace;
  }

  @Data
  public static class External {
    private String bootstrapServers;
  }

  @Data
  public static class Validation {
    private int cardMax;
  }

  @Data
  public static class DataKafka {
    private String bootstrapServers;
    private boolean secure;
    private String protocol;
    private String saslMechanism;
    private String user;
    private String password;
  }
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AccountSource {
    private boolean t24Account;
  }
}
