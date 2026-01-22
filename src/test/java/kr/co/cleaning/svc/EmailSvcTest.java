package kr.co.cleaning.svc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EmailSvcTest {

    @Autowired
    private EmailSvc emailSvc;

    @MockBean
    private JavaMailSender emailSender;

    @MockBean
    private ConfigSvc configSvc;

    @Test
    public void testSendSimpleMessage() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Text";

        emailSvc.sendSimpleMessage(to, subject, text);

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendAdminNotification() {
        String subject = "Admin Subject";
        String text = "Admin Text";
        // Mock multiple recipients
        String recipients = "admin1@example.com,admin2@example.com";

        when(configSvc.getConfigValue("EMAIL_RECIPIENTS")).thenReturn(recipients);

        emailSvc.sendAdminNotification(subject, text);

        // Verify that email is sent to both recipients
        verify(emailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendConsultationNotification() {
        // Mock recipients
        String recipients = "careville0424@naver.com";
        when(configSvc.getConfigValue("EMAIL_RECIPIENTS")).thenReturn(recipients);

        // Create test consultation data
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nm", "홍길동");
        paramMap.put("tel1", "010-1234-5678");
        paramMap.put("email", "customer@example.com");
        paramMap.put("adres1", "서울시 강남구 테헤란로 123");
        paramMap.put("serviceNmInput", "주방청소");
        paramMap.put("hopeDay", "2025-01-25");
        paramMap.put("hopeTime", "09:00-10:00");
        paramMap.put("inqryCn", "주방 깊은 청소 요청합니다.");

        emailSvc.sendConsultationNotification(paramMap);

        // Verify that email is sent
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
