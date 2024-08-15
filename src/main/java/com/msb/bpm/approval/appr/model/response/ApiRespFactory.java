package com.msb.bpm.approval.appr.model.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.exception.ExternalServiceErrorException;
import com.msb.bpm.approval.appr.exception.ForwardServiceErrorException;
import com.msb.bpm.approval.appr.exception.ValidationRequestException;
import com.msb.bpm.approval.appr.model.dto.FieldErrorDTO;
import com.msb.bpm.approval.appr.model.dto.camunda.CamundaErrorDTO;
import com.msb.bpm.approval.appr.model.response.error.ErrorResponse;
import com.msb.bpm.approval.appr.model.response.error.ErrorResponse.MessageLanguageResponse;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.msb.bpm.approval.appr.exception.DomainCode.*;

@Component
@RequiredArgsConstructor
public class ApiRespFactory {

  private final MessageSource messageSource;
  private final ObjectMapper objectMapper;
  private static final String ISSUED_FIELDS = "Ngày cấp / Cấp bởi";

  public ResponseEntity<ApiResponse> failWithDomainException(ApprovalException e) {

    return ResponseEntity.status(e.getCode().getStatus()).body(new ApiResponse().withCode(e.getCode().getCode())
        .withMessage(messageSource.getMessage(e.getCode().getCode(), e.getArgs(), Util.locale())));
  }

  public ResponseEntity<ApiResponse> success(Object data) {
    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse().withCode(SUCCESS.getCode())
        .withMessage(messageSource.getMessage(SUCCESS.getCode(), null, Util.locale()))
        .withData(data));
  }

  public ResponseEntity<ApiResponse> success(DomainCode domainCode, Object... args) {
    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse().withCode(domainCode.getCode())
        .withMessage(messageSource.getMessage(domainCode.getCode(), args, Util.locale())));
  }

  public ResponseEntity<ApiResponse> success(DomainCode domainCode) {
    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse().withCode(domainCode.getCode())
        .withMessage(messageSource.getMessage(domainCode.getCode(), null, Util.locale())));
  }

  public ResponseEntity<ApiResponse> success() {
    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse().withCode(SUCCESS.getCode())
        .withMessage(messageSource.getMessage(SUCCESS.getCode(), null, Util.locale())));
  }

  public ResponseEntity<ApiResponse> failWithInternalException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse().withCode(INTERNAL_SERVICE_ERROR.getCode())
        .withMessage(messageSource.getMessage(INVALID_PARAMETER.getCode(), null, Util.locale()))
        .withFieldErrors(parseErrorData(e)));
  }

  public ResponseEntity<ApiResponse> failWithBadInputParameter(Exception e) {
    Set<FieldErrorDTO> fieldErrors = parseErrorData(e);
    Set<String> fieldErrorNames = fieldErrors.stream().map(FieldErrorDTO::getFieldName).collect(Collectors.toSet());
    if (fieldErrorNames.contains("customer.identities[].issuedBy") || fieldErrorNames.contains("customer.identities[].issuedAt")){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse().withCode(CUSTOM_INVALID_PARAMETER.getCode())
              .withMessage(messageSource.getMessage(CUSTOM_INVALID_PARAMETER.getCode(), new String[]{ISSUED_FIELDS}, Util.locale()))
              .withFieldErrors(fieldErrors));
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse().withCode(INVALID_PARAMETER.getCode())
        .withMessage(messageSource.getMessage(INVALID_PARAMETER.getCode(), null, Util.locale()))
        .withFieldErrors(fieldErrors));
  }


  public ResponseEntity<ApiResponse> failWithApiException(ApiException ex) {

    CamundaErrorDTO error = JsonUtil.convertString2Object(ex.getResponseBody(), CamundaErrorDTO.class, objectMapper);
    return ResponseEntity.status(ex.getCode()).body(new ApiResponse().withCode(CAMUNDA_SYSTEM_ERROR.getCode())
        .withMessage(messageSource.getMessage(CAMUNDA_SYSTEM_ERROR.getCode(), new Object[]{error.getType(), error.getMessage()}, Util.locale()))
        .withFieldErrors(parseErrorData(ex)));

  }

  public ResponseEntity<ApiResponse> failWithValidationRequestException(ValidationRequestException ex) {
    return ResponseEntity.status(ex.getStatus()).body(new ApiResponse().withCode(ex.getCode().getCode())
        .withMessage(messageSource.getMessage(ex.getCode().getCode(), ex.getArgs(), Util.locale()))
        .withFieldErrors(parseErrorData(ex.getException())));
  }

  public ResponseEntity<ApiResponse> failWithForwardServiceErrorException(
      ForwardServiceErrorException ex) {

    String errorMessage;

    if (ex.getErrorResponse().getMessage() instanceof ErrorResponse.MessageLanguageResponse) {
      errorMessage = ((MessageLanguageResponse) ex.getErrorResponse().getMessage()).getVi();
    } else {
      errorMessage = (String) ex.getErrorResponse().getMessage();
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse()
            .withCode(ex.getErrorResponse().getCode())
            .withMessage(errorMessage));
  }

  public ResponseEntity<ApiResponse> failWithExternalServiceErrorException(
      ExternalServiceErrorException ex) {
    return ResponseEntity.status(ex.getStatus())
        .body(new ApiResponse()
            .withCode(ex.getCode().getCode())
            .withMessage(messageSource.getMessage(ex.getMessage(), ex.getArgs(), Util.locale())));
  }

  private Set<FieldErrorDTO> parseErrorData(Exception e) {
    Set<FieldErrorDTO> errorSet = ConcurrentHashMap.newKeySet();

    if (e instanceof HttpMessageNotReadableException) {
      InvalidFormatException formatException = (InvalidFormatException) e.getCause();

      formatException.getPath().parallelStream().forEach(err -> {
        if (StringUtils.isNotEmpty(err.getFieldName())) {
          errorSet.add(new FieldErrorDTO().withFieldName(err.getFieldName())
              .withErrorMessage(formatException.getMessage()));
        }
      });
    }

    if (e instanceof ConstraintViolationException) {
      Set<ConstraintViolation<?>> set = ((ConstraintViolationException) e).getConstraintViolations();
      for (ConstraintViolation<?> next : set) {
        String field = String.valueOf(next.getPropertyPath());
        errorSet.add(new FieldErrorDTO().withFieldName(field).withErrorMessage(next.getMessage()));
      }
    }

    if (e instanceof MethodArgumentNotValidException) {

      List<FieldError> errors = ((MethodArgumentNotValidException) e).getFieldErrors();

      errors.parallelStream().forEach(err ->
          errorSet.add(new FieldErrorDTO().withFieldName(err.getField())
              .withErrorMessage(err.getDefaultMessage())));
    }

    return errorSet;
  }
}