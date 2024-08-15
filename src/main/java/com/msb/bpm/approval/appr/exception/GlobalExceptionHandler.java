package com.msb.bpm.approval.appr.exception;

import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final ApiRespFactory apiRespFactory;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    return apiRespFactory.failWithBadInputParameter(ex);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
    return apiRespFactory.failWithBadInputParameter(ex);
  }

  @ExceptionHandler(ApprovalException.class)
  public ResponseEntity<ApiResponse> handleApprovalException(ApprovalException ex) {
    return apiRespFactory.failWithDomainException(ex);
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse> handleApiException(ApiException ex) {
    return apiRespFactory.failWithApiException(ex);
  }

  @ExceptionHandler(ValidationRequestException.class)
  public ResponseEntity<ApiResponse> handleValidationRequestException(ValidationRequestException ex) {
    return apiRespFactory.failWithValidationRequestException(ex);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    return apiRespFactory.failWithInternalException(ex);
  }

  @ExceptionHandler(ForwardServiceErrorException.class)
  public ResponseEntity<ApiResponse> handleForwardServiceErrorException(ForwardServiceErrorException ex) {
    return apiRespFactory.failWithForwardServiceErrorException(ex);
  }

  @ExceptionHandler(ExternalServiceErrorException.class)
  public ResponseEntity<ApiResponse> handleExternalServiceErrorException(ExternalServiceErrorException ex) {
    return apiRespFactory.failWithExternalServiceErrorException(ex);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public final ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    return apiRespFactory.failWithInternalException(ex);
  }

  @ExceptionHandler(IllegalStateException.class)
  public final ResponseEntity<ApiResponse> handleIllegalStateException(IllegalStateException ex) {
    return apiRespFactory.failWithInternalException(ex);
  }
}



