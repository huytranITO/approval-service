package com.msb.bpm.approval.appr.exception;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.model.response.error.ErrorResponse;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 4/12/2023, Monday
 **/
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomResponseErrorHandler implements ResponseErrorHandler {

  private final ObjectMapper objectMapper;
  private final MessageSource messageSource;

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    // Nhận các message lỗi HttpStatus = 5xx | 4xx
    return CLIENT_ERROR == response.getStatusCode().series()
        || SERVER_ERROR == response.getStatusCode().series();
  }

  @Override
  public void handleError(ClientHttpResponse response) {
    // Todo
  }

  @Override
  public void handleError(@NotNull URI url, @NotNull HttpMethod method, ClientHttpResponse response)
      throws IOException {
    // Convert InputStream -> Object
    ErrorResponse<?> errorResponse = JsonUtil.convertInputStream2Object(response.getBody(),
        ErrorResponse.class, objectMapper);

    log.error("URI : {} , ClientHttpResponse statusCode : {} , body : [{}]", method + " " + url,
        response.getStatusCode().value(),
        JsonUtil.convertObject2String(errorResponse, objectMapper));

    // Lấy lại tên service từ URI
    String path = url.getPath().substring(1);
    String serviceName = messageSource.getMessage(path.substring(0, path.indexOf("/")), null,
        Util.locale());

    // Với lỗi timeout , forbidden hoặc lỗi không được define từ các service vệ tinh thì show lỗi ExternalServiceErrorException
    // Với lỗi được define từ các service vệ tinh thì throw lỗi ForwardServiceErrorException
    switch (response.getStatusCode()) {
      case GATEWAY_TIMEOUT:
        throw new ExternalServiceErrorException(DomainCode.TIMEOUT_ERROR, new Object[]{serviceName});
      case FORBIDDEN:
        throw new ExternalServiceErrorException(DomainCode.FORBIDDEN);
      default:
        if (errorResponse != null) {
          throw new ForwardServiceErrorException(errorResponse);
        }
        throw new ExternalServiceErrorException(DomainCode.EXTERNAL_SERVICE_SERVER_ERROR,
            new Object[]{serviceName});
    }
  }
}
