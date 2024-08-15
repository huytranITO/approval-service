package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.query.PostSyncAmlOprRequest;
import com.msb.bpm.approval.appr.model.response.legacy.impl.oprisklegal.SyncOpriskLegalResponse;
import com.msb.bpm.approval.appr.model.response.legacy.impl.opriskperson.SyncOpriskPersonResponse;
import org.springframework.transaction.annotation.Transactional;

public interface OpriskService {

  @Transactional
  SyncOpriskPersonResponse syncOpriskPerson(String maDinhDanh);

  @Transactional
  SyncOpriskLegalResponse syncOpriskLegal(String mst);

  @Transactional
  void syncOprInfo(ApplicationEntity entityApp, PostSyncAmlOprRequest request);
}
