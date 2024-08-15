package com.msb.bpm.approval.appr.service;

public interface BaseService<T, D> {

  String getType();

  T execute(D d, Object... obj);
}