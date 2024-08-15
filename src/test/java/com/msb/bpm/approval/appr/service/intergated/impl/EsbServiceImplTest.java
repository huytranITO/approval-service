package com.msb.bpm.approval.appr.service.intergated.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.msb.bpm.approval.appr.client.esb.EsbCoreClient;
import com.msb.bpm.approval.appr.enums.esb.EsbAccType;
import com.msb.bpm.approval.appr.enums.esb.EsbStatus;
import com.msb.bpm.approval.appr.model.request.esb.CommonInfoRequest;
import com.msb.bpm.approval.appr.model.request.esb.EsbRequest;
import com.msb.bpm.approval.appr.model.response.esb.AccountResponse;
import com.msb.bpm.approval.appr.model.response.esb.EsbDataResponse;
import com.msb.bpm.approval.appr.model.response.esb.EsbResponse;
import com.msb.bpm.approval.appr.model.response.esb.ListOfCAAccountsResponse;
import com.msb.bpm.approval.appr.service.cache.EsbServiceCache;
import com.msb.bpm.approval.appr.service.intergated.impl.EsbServiceImpl;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EsbServiceImplTest extends TestCase {
  @Mock
  private EsbCoreClient esbCoreClient;

  @Mock
  private EsbServiceCache esbServiceCache;
  @InjectMocks
  private EsbServiceImpl esbServiceImpl;

  @Test
  void testGetAccountInfo() {
    List<AccountResponse> responses = new ArrayList<>();
    // Create valid account data
    AccountResponse response1 = new AccountResponse();
    response1.setAcctType(EsbAccType.ACC_TYPE_D.getValue());
    response1.setAccountNumber("11001140004037");
    response1.setStatus(EsbStatus.ACTIVE.getValue());
    response1.setCurrency("VND");
    // Create valid account data
    AccountResponse response2 = new AccountResponse();
    response2.setAcctType(EsbAccType.ACC_TYPE_D.getValue());
    response2.setAccountNumber("11001370156911");
    response2.setStatus(EsbStatus.NEW_TODAY.getValue());
    response2.setCurrency("VND");
    // Create valid account data
    AccountResponse response3 = new AccountResponse();
    response3.setAcctType(EsbAccType.ACC_TYPE_D.getValue());
    response3.setAccountNumber("11001010159601");
    response3.setStatus(EsbStatus.DORMANT.getValue());
    response3.setCurrency("VND");

    responses.add(response1);
    responses.add(response2);
    responses.add(response3);

    //Create esb request
    String cifNumber = "test_cif_number";
    EsbRequest request = new EsbRequest();
    CommonInfoRequest commonInfoRequest = new CommonInfoRequest();
    request.setCif(cifNumber);
    request.setCommonInfo(commonInfoRequest);

    EsbResponse response = new EsbResponse();
    EsbDataResponse esbDataResponse = new EsbDataResponse();
    ListOfCAAccountsResponse listOfCAAccount = new ListOfCAAccountsResponse();
    esbDataResponse.setListOfCAAccounts(listOfCAAccount);
    response.setData(esbDataResponse);

    listOfCAAccount.setCaAccounts(responses);
    //Mock esbServiceCache
    when(esbServiceCache.callGetEsbAccountInfo(cifNumber)).thenReturn(response);
    //Mock esbCoreClient
//    when(esbCoreClient.callGetEsbAccountInfo(any())).thenReturn(response);

    List<AccountResponse> result = (List<AccountResponse>) esbServiceImpl.getAccountInfo(cifNumber);

    Assertions.assertEquals(result, responses);
  }

  @Test
  void testFilteredAccounts() {
    List<AccountResponse> responses = new ArrayList<>();
    // Create valid account data
    AccountResponse response1 = new AccountResponse();
    response1.setAcctType(EsbAccType.ACC_TYPE_D.getValue());
    response1.setAccountNumber("11001140004037");
    response1.setStatus(EsbStatus.ACTIVE.getValue());
    response1.setCurrency("VND");
    // Create invalid accType
    AccountResponse response2 = new AccountResponse();
    response2.setAcctType("Not_D");
    response2.setAccountNumber("11001140004037");
    response2.setStatus(EsbStatus.ACTIVE.getValue());
    response2.setCurrency("USD");
    // Create invalid accType
    AccountResponse response3 = new AccountResponse();
    response3.setAcctType(EsbAccType.ACC_TYPE_D.getValue());
    response3.setAccountNumber("11055140004037");
    response3.setStatus(EsbStatus.ACTIVE.getValue());
    response3.setCurrency("USD");
    // Create invalid status
    AccountResponse response4 = new AccountResponse();
    response4.setAcctType(EsbAccType.ACC_TYPE_D.getValue());
    response4.setAccountNumber("11001140004037");
    response4.setStatus("Not valid status test");
    response4.setCurrency("USD");

    responses.add(response1);
    responses.add(response2);
    responses.add(response3);
    responses.add(response4);

    List<AccountResponse> compareResponse = new ArrayList<>();
    compareResponse.add(response1);

    List<AccountResponse> testResp = esbServiceImpl.filteredAccounts(responses);

    Assertions.assertEquals(testResp, compareResponse);

  }

}
