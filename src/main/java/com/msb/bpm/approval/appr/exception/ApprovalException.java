package com.msb.bpm.approval.appr.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApprovalException extends RuntimeException {

  private final DomainCode code;
  private transient Object[] args;
  private HttpStatus status;

  public ApprovalException(DomainCode code) {
    this.code = code;
  }

  public ApprovalException(DomainCode code, Object[] args) {
    this.code = code;
    this.args = args;
  }

  public ApprovalException(HttpStatus status, DomainCode code, Object[] args) {
    this.status = status;
    this.code = code;
    this.args = args;
  }

  public ApprovalException(HttpStatus status, DomainCode code) {
    this.status = status;
    this.code = code;
  }
}