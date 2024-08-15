package com.msb.bpm.approval.appr.service.intergated.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.client.t24.T24Client;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.response.t24.T24AccountResponse;
import com.msb.bpm.approval.appr.model.response.t24.T24AccountResponse.AccountList;
import com.msb.bpm.approval.appr.util.ObjectMapperUtil;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class T24ServiceImplTest {
  @Mock
  private T24Client t24Client;
  @InjectMocks
  private T24ServiceImpl t24Service;
  private ObjectMapper objectMapper;
  private T24AccountResponse t24AccountResponse;
  private AccountList accountList;
  private String pathSourceFile = "src/test/resources/t24_integration/";

  T24ServiceImplTest() {
  }
  @BeforeEach
  void setUp() throws IOException {
    System.out.println("SETUP");
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(ObjectMapperUtil.javaTimeModule());
    initT24();
  }
  private void initT24() throws IOException {
    t24AccountResponse = objectMapper.readValue(
        new File(pathSourceFile + "t24_account_response.json"), T24AccountResponse.class);
    accountList = objectMapper.readValue(
        new File(pathSourceFile + "t24_account_filter.json"), AccountList.class);
  }
  @Test
  void testGetAccountInfo() {
    when(t24Client.callT24(any())).thenReturn(t24AccountResponse);
    Object response = t24Service.getAccountInfo("100423");
    assertEquals(accountList.getListOfCAAccounts(), response);

    // Test case response = null
    t24AccountResponse.setData(null);
    Object emptyResponse = t24Service.getAccountInfo("100423");
    assertEquals(Collections.emptyList(), emptyResponse);

    // Test case throw exception
    assertThrows(ApprovalException.class, () -> t24Service.getAccountInfo(null));
  }

}
