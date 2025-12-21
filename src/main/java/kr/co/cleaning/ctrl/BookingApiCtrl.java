package kr.co.cleaning.ctrl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.cleaning.dto.BookingRequestDto;
import kr.co.cleaning.dto.BookingResponseDto;
import kr.co.cleaning.svc.BookingSvc;
import kr.co.cleaning.core.config.KFException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Booking API", description = "예약 관리 API")
public class BookingApiCtrl {

    private static final Logger log = LoggerFactory.getLogger(BookingApiCtrl.class);

    @Autowired
    private BookingSvc bookingSvc;

    /**
     * 예약 등록
     * POST /api/v1/booking
     */
    @PostMapping("/booking")
    @Operation(summary = "예약 등록", description = "새로운 예약을 등록합니다.")
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequestDto request,
            BindingResult bindingResult) {

        log.info("Received booking request for product: {}", request.getProductNo());

        // 유효성 검증 에러 처리
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

            log.warn("Validation failed: {}", errors);
            return ResponseEntity
                .badRequest()
                .body(new BookingResponseDto(false, "입력값 검증 실패: " + errors));
        }

        try {
            BookingResponseDto response = bookingSvc.createBooking(request);
            return ResponseEntity.ok(response);

        } catch (KFException e) {
            log.error("Business logic error: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BookingResponseDto(false, e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error during booking creation", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BookingResponseDto(false, "예약 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 예약 상세 조회
     * GET /api/v1/booking/{seq}
     */
    @GetMapping("/booking/{seq}")
    @Operation(summary = "예약 조회", description = "예약 상세 정보를 조회합니다.")
    public ResponseEntity<?> getBooking(@PathVariable int seq) {
        try {
            HashMap<String, Object> booking = bookingSvc.getBookingDetails(seq);
            if (booking != null) {
                return ResponseEntity.ok(booking);
            } else {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BookingResponseDto(false, "예약 정보를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            log.error("Error retrieving booking", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BookingResponseDto(false, "조회 중 오류가 발생했습니다."));
        }
    }
}
