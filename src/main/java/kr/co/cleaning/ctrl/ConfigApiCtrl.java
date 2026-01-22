package kr.co.cleaning.ctrl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.cleaning.svc.EmailSvc;
import kr.co.cleaning.core.config.SessionCmn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 시스템 설정 및 관리자 유틸리티 API
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Config API", description = "관리자 설정 및 유틸리티 API")
public class ConfigApiCtrl {

    private static final Logger log = LoggerFactory.getLogger(ConfigApiCtrl.class);

    @Autowired
    private EmailSvc emailSvc;

    /**
     * 테스트 이메일 발송 (텍스트 형식)
     * POST /api/admin/email/test
     */
    @PostMapping("/email/test")
    @Operation(summary = "테스트 이메일 발송 (텍스트)", description = "지정된 이메일 주소로 텍스트 형식의 테스트 이메일을 발송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이메일 발송 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (이메일 주소 누락)")
    })
    public ResponseEntity<Map<String, Object>> sendTestEmail(
            @Parameter(description = "수신자 이메일 주소", required = true, example = "doshyun@naver.com")
            @RequestParam String to,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // 이메일 주소 유효성 검사
        if (!isValidEmail(to)) {
            response.put("success", false);
            response.put("message", "올바른 이메일 형식이 아닙니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String subject = "[케어빌] 이메일 발송 테스트";
            String content = buildTestEmailContent(now);

            emailSvc.sendSimpleMessage(to.trim(), subject, content);

            log.info("Test email request sent to: {}", to);

            response.put("success", true);
            response.put("message", "테스트 이메일 발송 요청이 완료되었습니다.");
            response.put("to", to.trim());
            response.put("type", "text");
            response.put("requestTime", now);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to send test email to: {}", to, e);
            response.put("success", false);
            response.put("message", "이메일 발송 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * HTML 테스트 이메일 발송
     * POST /api/admin/email/test-html
     */
    @PostMapping("/email/test-html")
    @Operation(summary = "테스트 이메일 발송 (HTML)", description = "지정된 이메일 주소로 HTML 형식의 테스트 이메일을 발송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이메일 발송 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (이메일 주소 누락)")
    })
    public ResponseEntity<Map<String, Object>> sendTestHtmlEmail(
            @Parameter(description = "수신자 이메일 주소", required = true, example = "doshyun@naver.com")
            @RequestParam String to,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        if (!isValidEmail(to)) {
            response.put("success", false);
            response.put("message", "올바른 이메일 형식이 아닙니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String subject = "[케어빌] HTML 이메일 발송 테스트";
            String htmlContent = emailSvc.buildTestHtmlEmail(now);

            emailSvc.sendHtmlMessage(to.trim(), subject, htmlContent);

            log.info("HTML Test email request sent to: {}", to);

            response.put("success", true);
            response.put("message", "HTML 테스트 이메일 발송 요청이 완료되었습니다.");
            response.put("to", to.trim());
            response.put("type", "html");
            response.put("requestTime", now);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to send HTML test email to: {}", to, e);
            response.put("success", false);
            response.put("message", "이메일 발송 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 상담 알림 이메일 테스트 발송
     * POST /api/admin/email/test-consultation
     */
    @PostMapping("/email/test-consultation")
    @Operation(summary = "상담 알림 이메일 테스트", description = "샘플 데이터로 상담 알림 HTML 이메일을 테스트 발송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이메일 발송 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<Map<String, Object>> sendTestConsultationEmail(
            @Parameter(description = "수신자 이메일 주소", required = true, example = "doshyun@naver.com")
            @RequestParam String to,
            @Parameter(description = "테스트 고객명", required = false, example = "홍길동")
            @RequestParam(required = false, defaultValue = "테스트 고객") String customerName,
            @Parameter(description = "테스트 연락처", required = false, example = "010-1234-5678")
            @RequestParam(required = false, defaultValue = "010-1234-5678") String phone,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        if (!isValidEmail(to)) {
            response.put("success", false);
            response.put("message", "올바른 이메일 형식이 아닙니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 샘플 상담 데이터 생성
            HashMap<String, Object> sampleData = new HashMap<>();
            sampleData.put("nm", customerName);
            sampleData.put("tel1", phone);
            sampleData.put("email", "customer@example.com");
            sampleData.put("adres1", "서울시 강남구 테헤란로 123");
            sampleData.put("adres2", "케어빌빌딩 5층");
            sampleData.put("compNm", "케어빌 주식회사");
            sampleData.put("categoryNmInput", "청소 서비스");
            sampleData.put("serviceNmInput", "입주 청소");
            sampleData.put("productNmInput", "원룸/오피스텔 청소");
            sampleData.put("hopeDay", "2026-01-25");
            sampleData.put("hopeTime", "오전 10시");
            sampleData.put("inqryCn", "안녕하세요.\n\n이사를 앞두고 입주 청소를 문의드립니다.\n\n원룸 25평형이고, 화장실 2개, 주방 1개입니다.\n욕실 곰팡이 제거도 함께 부탁드려도 될까요?\n\n견적 부탁드립니다.\n감사합니다.");

            // 상담 알림 이메일 발송 (Modern SaaS Style)
            String subject = "[케어빌] 새로운 온라인 상담이 접수되었습니다 (테스트)";
            String htmlContent = emailSvc.buildTestConsultationHtmlEmail();

            emailSvc.sendHtmlMessage(to.trim(), subject, htmlContent);

            log.info("Consultation test email request sent to: {}", to);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            response.put("success", true);
            response.put("message", "상담 알림 테스트 이메일 발송 요청이 완료되었습니다.");
            response.put("to", to.trim());
            response.put("type", "consultation-html");
            response.put("sampleData", sampleData);
            response.put("requestTime", now);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to send consultation test email to: {}", to, e);
            response.put("success", false);
            response.put("message", "이메일 발송 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 예약 알림 이메일 테스트 발송
     * POST /api/admin/email/test-booking
     */
    @PostMapping("/email/test-booking")
    @Operation(summary = "예약 알림 이메일 테스트", description = "샘플 데이터로 예약 알림 HTML 이메일을 테스트 발송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이메일 발송 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<Map<String, Object>> sendTestBookingEmail(
            @Parameter(description = "수신자 이메일 주소", required = true, example = "doshyun@naver.com")
            @RequestParam String to,
            @Parameter(description = "테스트 고객명", required = false, example = "홍길동")
            @RequestParam(required = false, defaultValue = "홍길동") String customerName,
            @Parameter(description = "테스트 연락처", required = false, example = "010-1234-5678")
            @RequestParam(required = false, defaultValue = "010-1234-5678") String phone,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        if (!isValidEmail(to)) {
            response.put("success", false);
            response.put("message", "올바른 이메일 형식이 아닙니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 샘플 예약 데이터 생성
            HashMap<String, Object> sampleData = new HashMap<>();
            sampleData.put("customerName", customerName);
            sampleData.put("customerPhone", phone);
            sampleData.put("customerEmail", "customer@example.com");
            sampleData.put("zip", "06123");
            sampleData.put("address1", "서울시 강남구 테헤란로 123");
            sampleData.put("address2", "케어빌빌딩 5층");
            sampleData.put("productNm", "프리미엄 정기 청소 서비스");
            sampleData.put("salePrice", "150000");
            sampleData.put("serviceTime", "약 3시간");
            sampleData.put("bookingDate", "2026-01-25");
            sampleData.put("timeSlot", "오전 10:00 ~ 13:00");
            sampleData.put("additionalRequests", "반려동물(강아지 1마리) 있습니다.\n창문 청소도 부탁드립니다.");

            // 예약 알림 이메일 발송
            String subject = "[케어빌] 새로운 예약이 접수되었습니다 (테스트)";
            String htmlContent = emailSvc.buildTestBookingHtmlEmail();

            emailSvc.sendHtmlMessage(to.trim(), subject, htmlContent);

            log.info("Booking test email request sent to: {}", to);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            response.put("success", true);
            response.put("message", "예약 알림 테스트 이메일 발송 요청이 완료되었습니다.");
            response.put("to", to.trim());
            response.put("type", "booking-html");
            response.put("sampleData", sampleData);
            response.put("requestTime", now);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to send booking test email to: {}", to, e);
            response.put("success", false);
            response.put("message", "이메일 발송 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 빠른 문의 알림 이메일 테스트 발송
     * POST /api/admin/email/test-quick-inquiry
     */
    @PostMapping("/email/test-quick-inquiry")
    @Operation(summary = "빠른 문의 알림 이메일 테스트", description = "샘플 데이터로 빠른 문의 알림 HTML 이메일을 테스트 발송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이메일 발송 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<Map<String, Object>> sendTestQuickInquiryEmail(
            @Parameter(description = "수신자 이메일 주소", required = true, example = "doshyun@naver.com")
            @RequestParam String to,
            @Parameter(description = "테스트 고객명", required = false, example = "김철수")
            @RequestParam(required = false, defaultValue = "김철수") String customerName,
            @Parameter(description = "테스트 연락처", required = false, example = "010-9876-5432")
            @RequestParam(required = false, defaultValue = "010-9876-5432") String phone,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        if (!isValidEmail(to)) {
            response.put("success", false);
            response.put("message", "올바른 이메일 형식이 아닙니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 빠른 문의 알림 이메일 발송
            String subject = "[케어빌] 새로운 빠른 문의가 접수되었습니다 (테스트)";
            String htmlContent = emailSvc.buildTestQuickInquiryHtmlEmail();

            emailSvc.sendHtmlMessage(to.trim(), subject, htmlContent);

            log.info("Quick inquiry test email request sent to: {}", to);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            response.put("success", true);
            response.put("message", "빠른 문의 알림 테스트 이메일 발송 요청이 완료되었습니다.");
            response.put("to", to.trim());
            response.put("type", "quick-inquiry-html");
            response.put("requestTime", now);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to send quick inquiry test email to: {}", to, e);
            response.put("success", false);
            response.put("message", "이메일 발송 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 이메일 주소 유효성 검사
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }

    /**
     * 텍스트 테스트 이메일 본문 생성
     */
    private String buildTestEmailContent(String timestamp) {
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("       [케어빌] 이메일 발송 테스트\n");
        sb.append("========================================\n\n");

        sb.append("이 이메일은 케어빌 시스템의 이메일 발송 기능\n");
        sb.append("테스트를 위해 발송되었습니다.\n\n");

        sb.append("■ 발송 시간: ").append(timestamp).append("\n");
        sb.append("■ 발송 서버: 케어빌 관리 시스템\n");
        sb.append("■ SMTP 서버: smtp.naver.com\n\n");

        sb.append("이 이메일을 수신하셨다면 이메일 설정이\n");
        sb.append("정상적으로 작동하고 있는 것입니다.\n\n");

        sb.append("========================================\n");
        sb.append("  본 메일은 테스트 목적으로 발송되었습니다.\n");
        sb.append("========================================\n");

        return sb.toString();
    }

}
