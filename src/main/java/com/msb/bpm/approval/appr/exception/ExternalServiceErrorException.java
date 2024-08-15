package com.msb.bpm.approval.appr.exception;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 27/11/2023, Monday
 **/
public class ExternalServiceErrorException extends ApprovalException {

  public ExternalServiceErrorException(DomainCode code) {
    super(code);
  }

  public ExternalServiceErrorException(DomainCode code, Object[] args) {
    super(code, args);
  }
}
