package com.msb.bpm.approval.appr.enums.card;


import lombok.Getter;

@Getter
public enum CardStatus {

  WAITING_PUSH_CARD(0),
  WAITING_RESPONSE(1),
  WAITING_CREATE_CARD(2),
  CREATE_CARD_ERROR(3),
  COMPLETE(4)
  ;

  private Integer code;

  CardStatus(Integer code) {
    this.code = code;
  }
}
