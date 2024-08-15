package com.msb.bpm.approval.appr.client.formtemplate;

import com.msb.bpm.approval.appr.client.AbstractClient;
import com.msb.bpm.approval.appr.model.dto.formtemplate.response.CreateDocumentResponseDTO;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@AllArgsConstructor
public class FormTemplateClient extends AbstractClient {

  private final RestTemplate restTemplate;

  private final FormTemplateProperties properties;

  @Override
  protected RestTemplate getRestTemplate() {
    return this.restTemplate;
  }

  public CreateDocumentResponseDTO getDocumentByCode(
      String documentCode, String version, String token, Serializable requestDto) {

    Map<String, String> vars = new HashMap<>();
    vars.put("code", documentCode);
    //    vars.put("version", version);

    log.info(
        "------call create form template [{}] with vars: [{}]",
        properties.getUrlBase() + properties.getUrlGenerateTemplateByCode(),
        vars);

    HttpHeaders headers = buildCommonHeaders(token);

    HttpEntity<Serializable> entity = new HttpEntity<>(requestDto, headers);

    String uri =
        UriComponentsBuilder.fromUriString(
                properties.getUrlBase() + properties.getUrlGenerateTemplateByCode())
            .buildAndExpand(vars)
            .toUriString();

    CreateDocumentResponseDTO createResponse =
        exchange(uri, HttpMethod.POST, entity, CreateDocumentResponseDTO.class);

    log.info("create response: {}", createResponse);

    return createResponse;
  }

  public void getDocuments(String token, Serializable requestDto) {


    log.info(
        "------call create form template [{}] with vars: [{}]",
        properties.getUrlBase() + properties.getUrlGenerateTemplates());

    HttpHeaders headers = buildCommonHeaders(token);

    HttpEntity<Serializable> entity = new HttpEntity<>(requestDto, headers);

    String uri =
        UriComponentsBuilder.fromUriString(
                properties.getUrlBase() + properties.getUrlGenerateTemplates())
            .build()
            .toUriString();

    exchange(uri, HttpMethod.POST, entity, CreateDocumentResponseDTO.class);

    log.info("[call generate form template successfully]");
  }
}
