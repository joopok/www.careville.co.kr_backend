package kr.co.cleaning.svc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import kr.co.cleaning.core.utils.SUtils;

@Service
public class EmailSvc {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ConfigSvc configSvc;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${kframe.adminBaseUrl:http://localhost:8080/apage}")
    private String adminBaseUrl;

    /**
     * 단일 이메일 발송 (비동기) - 텍스트 형식
     */
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    /**
     * HTML 이메일 발송 (비동기)
     */
    @Async
    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML 형식

            emailSender.send(mimeMessage);
            log.info("HTML Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
        }
    }

    /**
     * 관리자 알림 발송 (텍스트) - DB에서 수신자 목록 조회 후 발송
     */
    @Async
    public void sendAdminNotification(String subject, String text) {
        String recipientsStr = configSvc.getConfigValue("EMAIL_RECIPIENTS");

        if (recipientsStr == null || recipientsStr.trim().isEmpty()) {
            recipientsStr = "careville0424@naver.com";
        }

        String[] recipients = recipientsStr.split(",");
        for (String recipient : recipients) {
            String trimmedRecipient = recipient.trim();
            if (!trimmedRecipient.isEmpty()) {
                sendDirectMessage(trimmedRecipient, subject, text);
            }
        }
    }

    /**
     * 관리자 HTML 알림 발송 - DB에서 수신자 목록 조회 후 발송
     */
    @Async
    public void sendAdminHtmlNotification(String subject, String htmlContent) {
        String recipientsStr = configSvc.getConfigValue("EMAIL_RECIPIENTS");

        if (recipientsStr == null || recipientsStr.trim().isEmpty()) {
            recipientsStr = "careville0424@naver.com";
        }

        String[] recipients = recipientsStr.split(",");
        for (String recipient : recipients) {
            String trimmedRecipient = recipient.trim();
            if (!trimmedRecipient.isEmpty()) {
                sendDirectHtmlMessage(trimmedRecipient, subject, htmlContent);
            }
        }
    }

    /**
     * 동기 방식 텍스트 이메일 발송 (내부 호출용)
     */
    private void sendDirectMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    /**
     * 동기 방식 HTML 이메일 발송 (내부 호출용)
     */
    private void sendDirectHtmlMessage(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            emailSender.send(mimeMessage);
            log.info("HTML Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
        }
    }

    /**
     * 온라인 상담 등록 알림 발송 (HTML 형식)
     * @param paramMap 상담 정보가 담긴 Map
     */
    @Async
    public void sendConsultationNotification(HashMap<String, Object> paramMap) {
        try {
            String subject = "[케어빌] 새로운 온라인 상담이 접수되었습니다";
            String htmlContent = buildConsultationHtmlEmail(paramMap);

            sendAdminHtmlNotification(subject, htmlContent);
            log.info("Consultation notification email sent for customer: {}", paramMap.get("nm"));
        } catch (Exception e) {
            log.error("Failed to send consultation notification email", e);
        }
    }

    /**
     * 상담 알림 HTML 이메일 본문 생성 (Modern SaaS Style)
     */
    private String buildConsultationHtmlEmail(HashMap<String, Object> paramMap) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E) a h:mm", java.util.Locale.KOREAN));

        // 고객 정보
        String customerName = nvl(paramMap.get("nm"));
        String phone = nvl(paramMap.get("tel1"));
        String email = nvl(paramMap.get("email"));
        String addr1 = nvl(paramMap.get("adres1"));
        String addr2 = nvl(paramMap.get("adres2"));
        String compNm = nvl(paramMap.get("compNm"));

        // 서비스 정보
        String categoryNm = nvl(paramMap.get("categoryNmInput"));
        String serviceNm = nvl(paramMap.get("serviceNmInput"));
        String productNm = nvl(paramMap.get("productNmInput"));
        String hopeDay = nvl(paramMap.get("hopeDay"));
        String hopeTime = nvl(paramMap.get("hopeTime"));

        // 문의 내용
        String inqryCn = nvl(paramMap.get("inqryCn"));
        if (!inqryCn.isEmpty()) {
            inqryCn = inqryCn.replace("\n", "<br>");
        }

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"ko\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("</head>");
        html.append("<body style=\"margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; background-color: #ffffff;\">");

        // 메인 컨테이너
        html.append("<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse; background-color: #ffffff;\">");
        html.append("<tr><td style=\"padding: 0;\">");

        // 이메일 본문
        html.append("<table role=\"presentation\" style=\"max-width: 580px; margin: 0 auto; background-color: #ffffff;\">");

        // 헤더
        html.append("<tr>");
        html.append("<td style=\"padding: 48px 32px 32px 32px;\">");
        html.append("<p style=\"margin: 0 0 24px 0; font-size: 13px; font-weight: 600; color: #16a34a; letter-spacing: 0.5px;\">CAREVILLE</p>");
        html.append("<h1 style=\"margin: 0 0 8px 0; font-size: 24px; font-weight: 600; color: #111827; line-height: 1.3;\">새로운 상담 문의가 접수되었습니다</h1>");
        html.append("<p style=\"margin: 0; font-size: 14px; color: #6b7280;\">").append(now).append("</p>");
        html.append("</td>");
        html.append("</tr>");

        // 구분선
        html.append("<tr><td style=\"padding: 0 32px;\"><div style=\"height: 1px; background-color: #e5e7eb;\"></div></td></tr>");

        // 고객 정보
        html.append("<tr>");
        html.append("<td style=\"padding: 32px;\">");
        html.append("<p style=\"margin: 0 0 16px 0; font-size: 11px; font-weight: 600; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.5px;\">고객 정보</p>");

        // 이름
        html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
        html.append("<tr>");
        html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">이름</td>");
        html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827; font-weight: 500;\">").append(customerName.isEmpty() ? "-" : customerName).append("</td>");
        html.append("</tr>");
        html.append("</table>");

        // 연락처
        html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
        html.append("<tr>");
        html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">연락처</td>");
        html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(phone.isEmpty() ? "-" : phone).append("</td>");
        html.append("</tr>");
        html.append("</table>");

        // 이메일
        if (!email.isEmpty()) {
            html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">이메일</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(email).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        // 주소
        if (!addr1.isEmpty() || !addr2.isEmpty()) {
            String fullAddress = addr1 + (addr2.isEmpty() ? "" : " " + addr2);
            html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">주소</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827; line-height: 1.5;\">").append(fullAddress).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        // 회사명
        if (!compNm.isEmpty()) {
            html.append("<table role=\"presentation\" style=\"width: 100%;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">회사</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(compNm).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        html.append("</td>");
        html.append("</tr>");

        // 서비스 정보 (있는 경우)
        if (!categoryNm.isEmpty() || !serviceNm.isEmpty() || !productNm.isEmpty() || !hopeDay.isEmpty() || !hopeTime.isEmpty()) {
            html.append("<tr><td style=\"padding: 0 32px;\"><div style=\"height: 1px; background-color: #e5e7eb;\"></div></td></tr>");

            html.append("<tr>");
            html.append("<td style=\"padding: 32px;\">");
            html.append("<p style=\"margin: 0 0 16px 0; font-size: 11px; font-weight: 600; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.5px;\">서비스 정보</p>");

            if (!productNm.isEmpty() || !serviceNm.isEmpty()) {
                html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
                html.append("<tr>");
                html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">서비스</td>");
                html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827; font-weight: 500;\">").append(!productNm.isEmpty() ? productNm : serviceNm).append("</td>");
                html.append("</tr>");
                html.append("</table>");
            }

            if (!categoryNm.isEmpty()) {
                html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
                html.append("<tr>");
                html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">분류</td>");
                html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(categoryNm).append("</td>");
                html.append("</tr>");
                html.append("</table>");
            }

            if (!hopeDay.isEmpty() || !hopeTime.isEmpty()) {
                String hopeDateTimeText = "";
                if (!hopeDay.isEmpty()) hopeDateTimeText += hopeDay;
                if (!hopeDay.isEmpty() && !hopeTime.isEmpty()) hopeDateTimeText += "  ";
                if (!hopeTime.isEmpty()) hopeDateTimeText += hopeTime;
                html.append("<table role=\"presentation\" style=\"width: 100%;\">");
                html.append("<tr>");
                html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">희망일시</td>");
                html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(hopeDateTimeText).append("</td>");
                html.append("</tr>");
                html.append("</table>");
            }

            html.append("</td>");
            html.append("</tr>");
        }

        // 문의 내용
        html.append("<tr><td style=\"padding: 0 32px;\"><div style=\"height: 1px; background-color: #e5e7eb;\"></div></td></tr>");

        html.append("<tr>");
        html.append("<td style=\"padding: 32px;\">");
        html.append("<p style=\"margin: 0 0 16px 0; font-size: 11px; font-weight: 600; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.5px;\">문의 내용</p>");
        html.append("<div style=\"background-color: #f9fafb; border-radius: 8px; padding: 20px;\">");
        html.append("<p style=\"margin: 0; font-size: 14px; color: #374151; line-height: 1.7;\">");
        html.append(inqryCn.isEmpty() ? "<span style=\"color: #9ca3af;\">문의 내용이 입력되지 않았습니다.</span>" : inqryCn);
        html.append("</p>");
        html.append("</div>");
        html.append("</td>");
        html.append("</tr>");

        // CTA 버튼
        html.append("<tr>");
        html.append("<td style=\"padding: 8px 32px 48px 32px; text-align: center;\">");
        html.append("<a href=\"").append(adminBaseUrl).append("/cnslt010.do\" style=\"display: inline-block; background-color: #111827; color: #ffffff; text-decoration: none; padding: 12px 24px; font-size: 14px; font-weight: 500; border-radius: 6px;\">관리자 페이지에서 확인</a>");
        html.append("</td>");
        html.append("</tr>");

        // 푸터
        html.append("<tr>");
        html.append("<td style=\"padding: 24px 32px; border-top: 1px solid #e5e7eb; text-align: center;\">");
        html.append("<p style=\"margin: 0; font-size: 12px; color: #9ca3af;\">이 메일은 케어빌에서 자동 발송되었습니다.</p>");
        html.append("</td>");
        html.append("</tr>");

        html.append("</table>");

        html.append("</td></tr>");
        html.append("</table>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 예약 등록 알림 발송 (HTML 형식)
     * @param paramMap 예약 정보가 담긴 Map
     */
    @Async
    public void sendBookingNotification(HashMap<String, Object> paramMap) {
        try {
            String subject = "[케어빌] 새로운 예약이 접수되었습니다";
            String htmlContent = buildBookingHtmlEmail(paramMap);

            sendAdminHtmlNotification(subject, htmlContent);
            log.info("Booking notification email sent for customer: {}", paramMap.get("customerName"));
        } catch (Exception e) {
            log.error("Failed to send booking notification email", e);
        }
    }

    /**
     * 예약 알림 HTML 이메일 본문 생성 (Modern SaaS Style)
     */
    private String buildBookingHtmlEmail(HashMap<String, Object> paramMap) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E) a h:mm", java.util.Locale.KOREAN));

        // 고객 정보
        String customerName = nvl(paramMap.get("customerName"));
        String customerPhone = nvl(paramMap.get("customerPhone"));
        String customerEmail = nvl(paramMap.get("customerEmail"));
        String zip = nvl(paramMap.get("zip"));
        String address1 = nvl(paramMap.get("address1"));
        String address2 = nvl(paramMap.get("address2"));

        // 상품/서비스 정보
        String productNm = nvl(paramMap.get("productNm"));
        String salePrice = nvl(paramMap.get("salePrice"));
        String serviceTime = nvl(paramMap.get("serviceTime"));

        // 예약 정보
        String bookingDate = nvl(paramMap.get("bookingDate"));
        String timeSlot = nvl(paramMap.get("timeSlot"));
        String additionalRequests = nvl(paramMap.get("additionalRequests"));
        if (!additionalRequests.isEmpty()) {
            additionalRequests = additionalRequests.replace("\n", "<br>");
        }

        // 가격 포맷팅
        String formattedPrice = "";
        if (!salePrice.isEmpty()) {
            try {
                long price = Long.parseLong(salePrice.replaceAll("[^0-9]", ""));
                formattedPrice = String.format("%,d", price) + "원";
            } catch (NumberFormatException e) {
                formattedPrice = salePrice;
            }
        }

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"ko\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("</head>");
        html.append("<body style=\"margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; background-color: #ffffff;\">");

        // 메인 컨테이너
        html.append("<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse; background-color: #ffffff;\">");
        html.append("<tr><td style=\"padding: 0;\">");

        // 이메일 본문
        html.append("<table role=\"presentation\" style=\"max-width: 580px; margin: 0 auto; background-color: #ffffff;\">");

        // 헤더
        html.append("<tr>");
        html.append("<td style=\"padding: 48px 32px 32px 32px;\">");
        html.append("<p style=\"margin: 0 0 24px 0; font-size: 13px; font-weight: 600; color: #2563eb; letter-spacing: 0.5px;\">CAREVILLE</p>");
        html.append("<h1 style=\"margin: 0 0 8px 0; font-size: 24px; font-weight: 600; color: #111827; line-height: 1.3;\">새로운 예약이 접수되었습니다</h1>");
        html.append("<p style=\"margin: 0; font-size: 14px; color: #6b7280;\">").append(now).append("</p>");
        html.append("</td>");
        html.append("</tr>");

        // 구분선
        html.append("<tr><td style=\"padding: 0 32px;\"><div style=\"height: 1px; background-color: #e5e7eb;\"></div></td></tr>");

        // 예약 정보
        html.append("<tr>");
        html.append("<td style=\"padding: 32px;\">");
        html.append("<p style=\"margin: 0 0 16px 0; font-size: 11px; font-weight: 600; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.5px;\">예약 정보</p>");

        // 서비스명
        if (!productNm.isEmpty()) {
            html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">서비스</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827; font-weight: 500;\">").append(productNm).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        // 예약일
        if (!bookingDate.isEmpty()) {
            html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">예약일</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(bookingDate).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        // 예약시간
        if (!timeSlot.isEmpty()) {
            html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">시간</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(timeSlot).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        // 결제 금액
        if (!formattedPrice.isEmpty()) {
            html.append("<table role=\"presentation\" style=\"width: 100%; margin-top: 16px; background-color: #f9fafb; border-radius: 8px;\">");
            html.append("<tr>");
            html.append("<td style=\"padding: 16px 20px;\">");
            html.append("<table role=\"presentation\" style=\"width: 100%;\">");
            html.append("<tr>");
            html.append("<td style=\"font-size: 13px; color: #6b7280;\">결제 금액</td>");
            html.append("<td style=\"text-align: right; font-size: 18px; color: #111827; font-weight: 600;\">").append(formattedPrice).append("</td>");
            html.append("</tr>");
            html.append("</table>");
            html.append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        html.append("</td>");
        html.append("</tr>");

        // 구분선
        html.append("<tr><td style=\"padding: 0 32px;\"><div style=\"height: 1px; background-color: #e5e7eb;\"></div></td></tr>");

        // 고객 정보
        html.append("<tr>");
        html.append("<td style=\"padding: 32px;\">");
        html.append("<p style=\"margin: 0 0 16px 0; font-size: 11px; font-weight: 600; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.5px;\">고객 정보</p>");

        // 이름
        html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
        html.append("<tr>");
        html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">이름</td>");
        html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827; font-weight: 500;\">").append(customerName.isEmpty() ? "-" : customerName).append("</td>");
        html.append("</tr>");
        html.append("</table>");

        // 연락처
        html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
        html.append("<tr>");
        html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">연락처</td>");
        html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(customerPhone.isEmpty() ? "-" : customerPhone).append("</td>");
        html.append("</tr>");
        html.append("</table>");

        // 이메일
        if (!customerEmail.isEmpty()) {
            html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">이메일</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(customerEmail).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        // 주소
        if (!address1.isEmpty() || !address2.isEmpty()) {
            String fullAddress = "";
            if (!zip.isEmpty()) fullAddress = zip + " ";
            fullAddress += address1 + (address2.isEmpty() ? "" : " " + address2);

            html.append("<table role=\"presentation\" style=\"width: 100%;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">주소</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827; line-height: 1.5;\">").append(fullAddress).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        html.append("</td>");
        html.append("</tr>");

        // 요청사항 (있는 경우)
        if (!additionalRequests.isEmpty()) {
            html.append("<tr><td style=\"padding: 0 32px;\"><div style=\"height: 1px; background-color: #e5e7eb;\"></div></td></tr>");

            html.append("<tr>");
            html.append("<td style=\"padding: 32px;\">");
            html.append("<p style=\"margin: 0 0 16px 0; font-size: 11px; font-weight: 600; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.5px;\">요청사항</p>");
            html.append("<div style=\"background-color: #f9fafb; border-radius: 8px; padding: 20px;\">");
            html.append("<p style=\"margin: 0; font-size: 14px; color: #374151; line-height: 1.7;\">");
            html.append(additionalRequests);
            html.append("</p>");
            html.append("</div>");
            html.append("</td>");
            html.append("</tr>");
        }

        // CTA 버튼
        html.append("<tr>");
        html.append("<td style=\"padding: 8px 32px 48px 32px; text-align: center;\">");
        html.append("<a href=\"").append(adminBaseUrl).append("/booking010.do\" style=\"display: inline-block; background-color: #111827; color: #ffffff; text-decoration: none; padding: 12px 24px; font-size: 14px; font-weight: 500; border-radius: 6px;\">관리자 페이지에서 확인</a>");
        html.append("</td>");
        html.append("</tr>");

        // 푸터
        html.append("<tr>");
        html.append("<td style=\"padding: 24px 32px; border-top: 1px solid #e5e7eb; text-align: center;\">");
        html.append("<p style=\"margin: 0; font-size: 12px; color: #9ca3af;\">이 메일은 케어빌에서 자동 발송되었습니다.</p>");
        html.append("</td>");
        html.append("</tr>");

        html.append("</table>");

        html.append("</td></tr>");
        html.append("</table>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 테스트용 상담 HTML 이메일 본문 생성 (외부 호출용)
     */
    public String buildTestConsultationHtmlEmail() {
        HashMap<String, Object> testData = new HashMap<>();
        testData.put("nm", "테스트 고객");
        testData.put("tel1", "010-1234-5678");
        testData.put("email", "customer@example.com");
        testData.put("adres1", "서울시 강남구 테헤란로 123");
        testData.put("adres2", "케어빌빌딩 5층");
        testData.put("compNm", "케어빌 주식회사");
        testData.put("categoryNmInput", "청소 서비스");
        testData.put("serviceNmInput", "입주 청소");
        testData.put("productNmInput", "원룸/오피스텔 청소");
        testData.put("hopeDay", "2026-01-25");
        testData.put("hopeTime", "오전 10시");
        testData.put("inqryCn", "안녕하세요.\n\n이사를 앞두고 입주 청소를 문의드립니다.\n\n원룸 25평형이고, 화장실 2개, 주방 1개입니다.\n욕실 곰팡이 제거도 함께 부탁드려도 될까요?\n\n견적 부탁드립니다.\n감사합니다.");
        return buildConsultationHtmlEmail(testData);
    }

    /**
     * 테스트용 예약 HTML 이메일 본문 생성 (외부 호출용)
     */
    public String buildTestBookingHtmlEmail() {
        HashMap<String, Object> testData = new HashMap<>();
        testData.put("customerName", "홍길동");
        testData.put("customerPhone", "010-1234-5678");
        testData.put("customerEmail", "test@example.com");
        testData.put("zip", "06123");
        testData.put("address1", "서울시 강남구 테헤란로 123");
        testData.put("address2", "케어빌빌딩 5층");
        testData.put("productNm", "프리미엄 정기 청소 서비스");
        testData.put("salePrice", "150000");
        testData.put("serviceTime", "약 3시간");
        testData.put("bookingDate", "2026-01-25 (토요일)");
        testData.put("timeSlot", "오전 10:00 ~ 오후 1:00 (약 3시간 소요)");
        testData.put("additionalRequests", "반려동물(강아지 1마리) 있습니다.\n창문 청소도 부탁드립니다.");
        return buildBookingHtmlEmail(testData);
    }

    /**
     * 테스트용 HTML 이메일 본문 생성 (Premium Design v2)
     */
    public String buildTestHtmlEmail(String timestamp) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"ko\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("</head>");
        html.append("<body style=\"margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Apple SD Gothic Neo', 'Pretendard', 'Malgun Gothic', sans-serif; background-color: #F8FAFC; -webkit-font-smoothing: antialiased;\">");

        html.append("<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse; background-color: #F8FAFC;\">");
        html.append("<tr><td style=\"padding: 48px 24px;\">");

        html.append("<table role=\"presentation\" style=\"max-width: 480px; margin: 0 auto; background-color: #FFFFFF; border-radius: 24px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 8px 24px rgba(0,0,0,0.06);\">");

        // 헤더
        html.append("<tr>");
        html.append("<td style=\"padding: 56px 40px 40px 40px; text-align: center;\">");
        html.append("<div style=\"display: inline-block; background-color: #10B981; width: 64px; height: 64px; border-radius: 20px; line-height: 64px; margin-bottom: 28px;\">");
        html.append("<span style=\"color: #FFFFFF; font-size: 28px; font-weight: 700;\">C</span>");
        html.append("</div>");
        html.append("<h1 style=\"margin: 0 0 12px 0; color: #0F172A; font-size: 24px; font-weight: 700; letter-spacing: -0.5px;\">이메일 발송 테스트</h1>");
        html.append("<p style=\"margin: 0; color: #64748B; font-size: 15px; line-height: 1.6;\">시스템이 정상적으로 작동하고 있습니다</p>");
        html.append("</td>");
        html.append("</tr>");

        // 상태 표시
        html.append("<tr>");
        html.append("<td style=\"padding: 0 40px 40px 40px;\">");
        html.append("<table role=\"presentation\" style=\"width: 100%; background-color: #F0FDF4; border-radius: 16px; border: 1px solid #BBF7D0;\">");
        html.append("<tr>");
        html.append("<td style=\"padding: 24px; text-align: center;\">");
        html.append("<div style=\"display: inline-block; width: 48px; height: 48px; background-color: #22C55E; border-radius: 50%; line-height: 48px; margin-bottom: 16px;\">");
        html.append("<span style=\"color: #FFFFFF; font-size: 24px;\">&#10003;</span>");
        html.append("</div>");
        html.append("<p style=\"margin: 0 0 8px 0; color: #166534; font-size: 15px; font-weight: 600;\">발송 완료</p>");
        html.append("<p style=\"margin: 0; color: #15803D; font-size: 14px;\">").append(timestamp).append("</p>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");

        // 푸터
        html.append("<tr>");
        html.append("<td style=\"background-color: #F8FAFC; padding: 28px 40px; text-align: center; border-top: 1px solid #E2E8F0;\">");
        html.append("<p style=\"margin: 0 0 4px 0; color: #64748B; font-size: 12px;\">CAREVILLE</p>");
        html.append("<p style=\"margin: 0; color: #94A3B8; font-size: 11px;\">본 메일은 시스템에서 자동 발송되었습니다</p>");
        html.append("</td>");
        html.append("</tr>");

        html.append("</table>");

        html.append("</td></tr>");
        html.append("</table>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 빠른 문의 등록 알림 발송 (HTML 형식)
     * @param paramMap 문의 정보가 담긴 Map
     */
    @Async
    public void sendQuickInquiryNotification(HashMap<String, Object> paramMap) {
        try {
            String subject = "[케어빌] 새로운 빠른 문의가 접수되었습니다";
            String htmlContent = buildQuickInquiryHtmlEmail(paramMap);

            sendAdminHtmlNotification(subject, htmlContent);
            log.info("Quick inquiry notification email sent for customer: {}", paramMap.get("nm"));
        } catch (Exception e) {
            log.error("Failed to send quick inquiry notification email", e);
        }
    }

    /**
     * 빠른 문의 알림 HTML 이메일 본문 생성 (Modern SaaS Style)
     */
    private String buildQuickInquiryHtmlEmail(HashMap<String, Object> paramMap) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E) a h:mm", java.util.Locale.KOREAN));

        // 고객 정보
        String customerName = nvl(paramMap.get("nm"));
        String phone = nvl(paramMap.get("tel"));
        String email = nvl(paramMap.get("email"));

        // 문의 내용
        String inqryCn = nvl(paramMap.get("inqryCn"));
        if (!inqryCn.isEmpty()) {
            inqryCn = inqryCn.replace("\n", "<br>");
        }

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"ko\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("</head>");
        html.append("<body style=\"margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; background-color: #ffffff;\">");

        // 메인 컨테이너
        html.append("<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse; background-color: #ffffff;\">");
        html.append("<tr><td style=\"padding: 0;\">");

        // 이메일 본문
        html.append("<table role=\"presentation\" style=\"max-width: 580px; margin: 0 auto; background-color: #ffffff;\">");

        // 헤더 - 오렌지 컬러 (#f97316)
        html.append("<tr>");
        html.append("<td style=\"padding: 48px 32px 32px 32px;\">");
        html.append("<p style=\"margin: 0 0 24px 0; font-size: 13px; font-weight: 600; color: #f97316; letter-spacing: 0.5px;\">CAREVILLE</p>");
        html.append("<h1 style=\"margin: 0 0 8px 0; font-size: 24px; font-weight: 600; color: #111827; line-height: 1.3;\">빠른 문의가 접수되었습니다</h1>");
        html.append("<p style=\"margin: 0; font-size: 14px; color: #6b7280;\">").append(now).append("</p>");
        html.append("</td>");
        html.append("</tr>");

        // 구분선
        html.append("<tr><td style=\"padding: 0 32px;\"><div style=\"height: 1px; background-color: #e5e7eb;\"></div></td></tr>");

        // 고객 정보
        html.append("<tr>");
        html.append("<td style=\"padding: 32px;\">");
        html.append("<p style=\"margin: 0 0 16px 0; font-size: 11px; font-weight: 600; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.5px;\">고객 정보</p>");

        // 이름
        html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
        html.append("<tr>");
        html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">이름</td>");
        html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827; font-weight: 500;\">").append(customerName.isEmpty() ? "-" : customerName).append("</td>");
        html.append("</tr>");
        html.append("</table>");

        // 연락처
        html.append("<table role=\"presentation\" style=\"width: 100%; margin-bottom: 12px;\">");
        html.append("<tr>");
        html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">연락처</td>");
        html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(phone.isEmpty() ? "-" : phone).append("</td>");
        html.append("</tr>");
        html.append("</table>");

        // 이메일
        if (!email.isEmpty()) {
            html.append("<table role=\"presentation\" style=\"width: 100%;\">");
            html.append("<tr>");
            html.append("<td style=\"width: 80px; padding: 8px 0; font-size: 13px; color: #6b7280; vertical-align: top;\">이메일</td>");
            html.append("<td style=\"padding: 8px 0; font-size: 14px; color: #111827;\">").append(email).append("</td>");
            html.append("</tr>");
            html.append("</table>");
        }

        html.append("</td>");
        html.append("</tr>");

        // 문의 내용
        html.append("<tr><td style=\"padding: 0 32px;\"><div style=\"height: 1px; background-color: #e5e7eb;\"></div></td></tr>");

        html.append("<tr>");
        html.append("<td style=\"padding: 32px;\">");
        html.append("<p style=\"margin: 0 0 16px 0; font-size: 11px; font-weight: 600; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.5px;\">문의 내용</p>");
        html.append("<div style=\"background-color: #fff7ed; border-radius: 8px; padding: 20px; border-left: 3px solid #f97316;\">");
        html.append("<p style=\"margin: 0; font-size: 14px; color: #374151; line-height: 1.7;\">");
        html.append(inqryCn.isEmpty() ? "<span style=\"color: #9ca3af;\">문의 내용이 입력되지 않았습니다.</span>" : inqryCn);
        html.append("</p>");
        html.append("</div>");
        html.append("</td>");
        html.append("</tr>");

        // CTA 버튼
        html.append("<tr>");
        html.append("<td style=\"padding: 8px 32px 48px 32px; text-align: center;\">");
        html.append("<a href=\"").append(adminBaseUrl).append("/cnslt010.do\" style=\"display: inline-block; background-color: #111827; color: #ffffff; text-decoration: none; padding: 12px 24px; font-size: 14px; font-weight: 500; border-radius: 6px;\">관리자 페이지에서 확인</a>");
        html.append("</td>");
        html.append("</tr>");

        // 푸터
        html.append("<tr>");
        html.append("<td style=\"padding: 24px 32px; border-top: 1px solid #e5e7eb; text-align: center;\">");
        html.append("<p style=\"margin: 0; font-size: 12px; color: #9ca3af;\">이 메일은 케어빌에서 자동 발송되었습니다.</p>");
        html.append("</td>");
        html.append("</tr>");

        html.append("</table>");

        html.append("</td></tr>");
        html.append("</table>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 테스트용 빠른 문의 HTML 이메일 본문 생성 (외부 호출용)
     */
    public String buildTestQuickInquiryHtmlEmail() {
        HashMap<String, Object> testData = new HashMap<>();
        testData.put("nm", "김철수");
        testData.put("tel", "010-9876-5432");
        testData.put("email", "quicktest@example.com");
        testData.put("inqryCn", "안녕하세요, 에어컨 청소 서비스 관련하여 간단히 문의드립니다.\n청소 비용과 소요 시간이 궁금합니다.");
        return buildQuickInquiryHtmlEmail(testData);
    }

    /**
     * null 안전 문자열 변환
     */
    private String nvl(Object obj) {
        return SUtils.nvl(obj);
    }
}
