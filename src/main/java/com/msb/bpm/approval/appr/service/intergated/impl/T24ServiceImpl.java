package com.msb.bpm.approval.appr.service.intergated.impl;

import static com.msb.bpm.approval.appr.enums.t24.ProductType.RB_CA;

import com.msb.bpm.approval.appr.client.t24.T24Client;
import com.msb.bpm.approval.appr.enums.esb.EsbAccType;
import com.msb.bpm.approval.appr.enums.esb.EsbCurrencyType;
import com.msb.bpm.approval.appr.enums.esb.EsbStatus;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.t24.T24Request;
import com.msb.bpm.approval.appr.model.request.t24.T24Request.CommonInfo;
import com.msb.bpm.approval.appr.model.response.t24.T24AccountResponse;
import com.msb.bpm.approval.appr.model.response.t24.T24AccountResponse.Account;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.intergated.T24Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class T24ServiceImpl extends AbstractBaseService implements T24Service {

  private final T24Client t24Client;
  @Override
  public Object getAccountInfo(String cifNumber) {
    log.info("START T24ServiceImpl.getAccountInfo with cifNumber= {}", cifNumber);
    if (StringUtils.isBlank(cifNumber)) {
      log.error("Unable to get account info from T24 because CIF number is null or empty.");
      throw new ApprovalException(DomainCode.INVALID_CIF_VALUE);
    }
    // Lấy danh sách TKTT từ T24-integration
    List<Account> accounts = getAccountsFromT24(cifNumber);

    // Filter lấy danh sách account CA
    accounts = filteredAccounts(accounts);
    log.info("END T24ServiceImpl.getAccountInfo with response={}", accounts);
    return accounts;
  }
  /**
   * Filter lấy danh sách TKTT thỏa mãn các điều kiện sau:
   * 1. TKTT có acctType = D
   * 2. TKTT có currency = VND
   * 3. TKTT có Product Type có chuỗi ký tự đầu là RB.CA
   * 4. TKTT có status in (Active, New today,Dormant
   */
  private List<Account> filteredAccounts(List<Account> accounts) {
    return accounts.stream()
        .filter(a -> StringUtils.isNotEmpty(a.getAcctType()) && StringUtils.isNotEmpty(a.getAccountNumber())
            && StringUtils.isNotEmpty(a.getStatus()) && StringUtils.isNotEmpty(a.getCurrency()) && StringUtils.isNotEmpty(a.getProductType()))
        .filter(a -> a.getAcctType().equals(EsbAccType.ACC_TYPE_D.getValue()))
        .filter(a -> a.getCurrency().equalsIgnoreCase(EsbCurrencyType.CURRENCY_VND.getValue()))
        .filter(a -> RB_CA.getValue().equalsIgnoreCase(a.getProductType().substring(0, 5)))
        .filter(a -> Arrays.asList(EsbStatus.ACTIVE.getValue(), EsbStatus.NEW_TODAY.getValue(), EsbStatus.DORMANT.getValue()).contains(a.getStatus().toUpperCase()))
        .collect(Collectors.toList());
  }

  public List<Account> getAccountsFromT24(String cifNumber) {
    // Tạo request gọi sang T24
    T24Request request = T24Request.builder()
        .commonInfo(new CommonInfo())
        .cifNumber(cifNumber)
        .build();
    // Gọi T24 lấy danh sách tài khoản thanh toán
    T24AccountResponse response = t24Client.callT24(request);
    if (ObjectUtils.isEmpty(response) || ObjectUtils.isEmpty(response.getData())
        || ObjectUtils.isEmpty(response.getData().getAccountList())) {
      return Collections.emptyList();
    }
    List<Account> accounts = new ArrayList<>();
    accounts.addAll(response.getData().getAccountList().getListOfCAAccounts());
    accounts.addAll(response.getData().getAccountList().getListOfSAAccounts());
    accounts.addAll(response.getData().getAccountList().getListOfLNAccounts());
    accounts.addAll(response.getData().getAccountList().getListOfFDAccounts());
    return accounts;
  }
}
