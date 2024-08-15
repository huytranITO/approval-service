package com.msb.bpm.approval.appr.service.notification;

import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.notication.CICPDFNotificationStrategy;
import com.msb.bpm.approval.appr.notication.CICRatioNotificationStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"local"})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NotificationServiceImplTest {

    @Mock
    CICRatioNotificationStrategy cicRatioNotificationStrategy;

    @Mock
    CICPDFNotificationStrategy cicpdfNotificationStrategy;
    @Resource
    @InjectMocks
    NotificationServiceImpl notificationService;

    @BeforeAll
    static void init() {}

    @Test
    void testNoticeRatioCIC() {
        Set<String> users = new HashSet<>();
        users.add("test");
        assertDoesNotThrow(() -> notificationService.noticeRatioCIC(users, "151-0000111"));
    }

    @Test
    void testNoticePdfCic() {
        Set<String> users = new HashSet<>();
        users.add("test");
        assertDoesNotThrow(() -> notificationService.noticePdfCIC(users, "151-0000111"));
    }

    @Test
    void testNoticePdfCic2() {
        ApplicationEntity application = new ApplicationEntity();
        application.setAssignee("test");
        application.setBpmId("151-0000111");
        assertDoesNotThrow(() -> notificationService.noticePdfCic("test", application));
    }
}
