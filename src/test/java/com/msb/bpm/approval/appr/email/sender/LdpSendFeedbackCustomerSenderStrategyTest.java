package com.msb.bpm.approval.appr.email.sender;

import static com.msb.bpm.approval.appr.enums.email.EventCodeEmailType.LDP_SEND_FEEDBACK_CUSTOMER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.msb.bpm.approval.appr.email.EmailSender;
import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.request.email.EmailRequest;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import junit.framework.TestCase;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LdpSendFeedbackCustomerSenderStrategyTest extends TestCase {
  @Mock
  private CommonService commonService;
  @Mock
  private AbstractBaseService abstractBaseService;
  @Mock
  private EmailSender emailSender;
  @InjectMocks
  private LdpSendFeedbackCustomerSenderStrategy ldpSendFeedbackCustomerSenderStrategy;
  @Captor ArgumentCaptor dataSendCaptor;
  @Captor ArgumentCaptor eventCodeCaptor;
  @Captor ArgumentCaptor TOKENCaptor;
  @Before
  public void setUp() {
    // Deregister the previous static mock
    Mockito.framework().clearInlineMocks();

    // Create a new static mock
    MockitoAnnotations.initMocks(this);
  }
  @Test
  public void testGetType() {
    assertEquals(LDP_SEND_FEEDBACK_CUSTOMER, ldpSendFeedbackCustomerSenderStrategy.getType());
  }
  @Test
  public void testPublishEmail() {
    // Set metadata
    ConcurrentMap<String, Object> metadata = new ConcurrentHashMap<>();
    metadata.put("token", "testToken");
    metadata.put("CustomerName", "testUserName");
    metadata.put("LdpReason", "testReason");
    ldpSendFeedbackCustomerSenderStrategy.setMetadata(metadata);

    String eventCode = LDP_SEND_FEEDBACK_CUSTOMER.name();
    ApplicationEntity entityApp = new ApplicationEntity();
    entityApp.setBpmId("testBpmId");
    entityApp.setAssignee("testAssignee");
    entityApp.setSource("testSource");
    when(commonService.getAppEntity(any())).thenReturn(entityApp);
    when(abstractBaseService.getPreviousHistoryApprovalByRole(new ArrayList<>(entityApp.getHistoryApprovals()), ProcessingRole.PD_RB_BM)).thenReturn(
        Collections.emptyList());
    Map<String, Object> params = new HashMap<>();
    params.put("CustomerName", "");
    params.put("ApplicationSource", "testSource");
    params.put("ApplicationBpmId", "testBpmId");
    params.put("LdpReason", "testReason");
    EmailRequest dataSend = EmailRequest.builder()
        .to(Collections.singletonList("testAssignee@msb.com.vn"))
        .cc(Collections.emptyList())
        .eventCode(eventCode)
        .params(params)
        .build();

    // Act
    ldpSendFeedbackCustomerSenderStrategy.publishEmail(eventCode);

    verify(emailSender).sendEmail(dataSendCaptor.capture(), (String) eventCodeCaptor.capture(),(String) TOKENCaptor.capture());

    var dataSendValue = dataSendCaptor.getValue();
    var eventCodeValue = eventCodeCaptor.getValue();
    var TOKENValue = TOKENCaptor.getValue();

    // Assert
    assertEquals(dataSend, dataSendValue);
    assertEquals(eventCode, eventCodeValue);
    assertEquals(TOKENValue, TOKENValue);
  }
}
