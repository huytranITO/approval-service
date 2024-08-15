package com.msb.bpm.approval.appr.service.intergated.impl;

import com.msb.bpm.approval.appr.enums.esb.EsbAccType;
import com.msb.bpm.approval.appr.enums.esb.EsbAccountType;
import com.msb.bpm.approval.appr.enums.esb.EsbCurrencyType;
import com.msb.bpm.approval.appr.enums.esb.EsbStatus;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.response.esb.AccountResponse;
import com.msb.bpm.approval.appr.model.response.esb.EsbResponse;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.cache.EsbServiceCache;
import com.msb.bpm.approval.appr.service.intergated.EsbService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EsbServiceImpl extends AbstractBaseService implements EsbService {

  private final EsbServiceCache esbServiceCache;

  @Override
  public Object getAccountInfo(String cifNumber) {
    if (StringUtils.isBlank(cifNumber)) {
      log.error("Unable to get account info from ESB because CIF number is null or empty.");
      throw new ApprovalException(DomainCode.INVALID_CIF_VALUE);
    }
    EsbResponse esbResponse = esbServiceCache.callGetEsbAccountInfo(cifNumber);
    List<AccountResponse> responses = new ArrayList<>();
    if (Objects.nonNull(esbResponse.getData().getListOfCAAccounts())) {
      responses.addAll(esbResponse.getData().getListOfCAAccounts().getCaAccounts());
    }
    if (Objects.nonNull(esbResponse.getData().getListOfSAAccounts())) {
      responses.addAll(esbResponse.getData().getListOfSAAccounts().getSaAccounts());
    }
    if (Objects.nonNull(esbResponse.getData().getListOfFDAccounts())) {
      responses.addAll(esbResponse.getData().getListOfFDAccounts().getFdAccounts());
    }
    if (Objects.nonNull(esbResponse.getData().getListOfLNAccounts())) {
      responses.addAll(esbResponse.getData().getListOfLNAccounts().getLnAccounts());
    }
    log.info("START EsbServiceImpl.filteredAccounts");
    responses = filteredAccounts(responses);
    return responses;
  }

  public List<AccountResponse> filteredAccounts(List<AccountResponse> accounts) {
      return accounts.stream()
        .filter(a -> StringUtils.isNotEmpty(a.getAcctType()) && StringUtils.isNotEmpty(a.getAccountNumber())
            && StringUtils.isNotEmpty(a.getStatus()) && StringUtils.isNotEmpty(a.getCurrency()))
        .filter(a -> a.getAcctType().equals(EsbAccType.ACC_TYPE_D.getValue()))
        .filter(a -> Arrays.asList(EsbAccountType.TYPE_01.getValue(), EsbAccountType.TYPE_11.getValue(),
            EsbAccountType.TYPE_68.getValue(), EsbAccountType.TYPE_79.getValue(), EsbAccountType.TYPE_86.getValue(),
            EsbAccountType.TYPE_88.getValue()).contains(a.getAccountNumber().substring(3, 5)))
        .filter(a -> Arrays.asList(EsbStatus.ACTIVE.getValue(), EsbStatus.NEW_TODAY.getValue(), EsbStatus.DORMANT.getValue()).contains(a.getStatus().toUpperCase()))
        .filter(a -> a.getCurrency().equalsIgnoreCase(EsbCurrencyType.CURRENCY_VND.getValue()))
        .collect(Collectors.toList());
  }

}
