package com.msb.bpm.approval.appr.exception;

import javax.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.Setter;import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ValidationRequestException extends ApprovalException {

  private final ConstraintViolationException exception;

  public ValidationRequestException(DomainCode code, Object[] args,
      ConstraintViolationException exception) {
    super(HttpStatus.valueOf(code.getStatus()), code, args);
    this.exception = exception;
  }
}