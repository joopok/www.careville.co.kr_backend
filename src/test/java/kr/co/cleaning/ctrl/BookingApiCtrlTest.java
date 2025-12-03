package kr.co.cleaning.ctrl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.cleaning.dto.BookingRequestDto;
import kr.co.cleaning.dto.BookingResponseDto;
import kr.co.cleaning.svc.BookingSvc;
import kr.co.cleaning.core.config.KFException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(BookingApiCtrl.class)
@DisplayName("BookingApiController Integration Tests")
class BookingApiCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingSvc bookingSvc;

    private BookingRequestDto validRequest;
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    @BeforeEach
    void setUp() {
        validRequest = new BookingRequestDto();
        validRequest.setProductNo(1);
        validRequest.setProductNm("에어컨 청소");
        validRequest.setSalePrice(150000);
        validRequest.setServiceTime("2-3시간");
        validRequest.setCustomerName("홍길동");
        validRequest.setCustomerPhone("01012345678");
        validRequest.setCustomerEmail("hong@example.com");
        validRequest.setBookingDate(
            LocalDate.now().plusDays(1).format(DATE_FORMATTER)
        );
        validRequest.setTimeSlot("09:00 - 10:00");
    }

    @Test
    @DisplayName("POST /api/v1/booking - 성공")
    void testCreateBooking_Success() throws Exception {
        // Given
        BookingResponseDto expectedResponse =
            new BookingResponseDto(true, "예약이 성공적으로 접수되었습니다.", 123);

        when(bookingSvc.createBooking(any(BookingRequestDto.class)))
            .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.message", is("예약이 성공적으로 접수되었습니다.")))
            .andExpect(jsonPath("$.bookingSeq", is(123)));

        verify(bookingSvc, times(1)).createBooking(any(BookingRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/booking - 필수 필드 누락 시 400 에러")
    void testCreateBooking_MissingRequiredFields() throws Exception {
        // Given
        BookingRequestDto invalidRequest = new BookingRequestDto();
        invalidRequest.setCustomerName("홍길동");
        // productNo, customerPhone, bookingDate 누락

        // When & Then
        mockMvc.perform(post("/api/v1/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", containsString("입력값 검증 실패")));

        verify(bookingSvc, never()).createBooking(any());
    }

    @Test
    @DisplayName("POST /api/v1/booking - 잘못된 전화번호 형식")
    void testCreateBooking_InvalidPhoneFormat() throws Exception {
        // Given
        validRequest.setCustomerPhone("123-456-7890");  // Invalid format

        // When & Then
        mockMvc.perform(post("/api/v1/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success", is(false)));

        verify(bookingSvc, never()).createBooking(any());
    }

    @Test
    @DisplayName("POST /api/v1/booking - 비즈니스 로직 예외 처리")
    void testCreateBooking_BusinessException() throws Exception {
        // Given
        when(bookingSvc.createBooking(any(BookingRequestDto.class)))
            .thenThrow(new KFException("과거 날짜는 예약할 수 없습니다."));

        // When & Then
        mockMvc.perform(post("/api/v1/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", is("과거 날짜는 예약할 수 없습니다.")));
    }

    @Test
    @DisplayName("POST /api/v1/booking - 서버 내부 오류")
    void testCreateBooking_InternalServerError() throws Exception {
        // Given
        when(bookingSvc.createBooking(any(BookingRequestDto.class)))
            .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/v1/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message",
                       is("예약 처리 중 오류가 발생했습니다.")));
    }

    @Test
    @DisplayName("GET /api/v1/booking/{seq} - 성공")
    void testGetBooking_Success() throws Exception {
        // Given
        HashMap<String, Object> booking = new HashMap<>();
        booking.put("cnsltSeq", 123);
        booking.put("nm", "홍길동");
        booking.put("productNo", 1);

        when(bookingSvc.getBookingDetails(123)).thenReturn(booking);

        // When & Then
        mockMvc.perform(get("/api/v1/booking/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cnsltSeq", is(123)))
            .andExpect(jsonPath("$.nm", is("홍길동")));

        verify(bookingSvc, times(1)).getBookingDetails(123);
    }

    @Test
    @DisplayName("GET /api/v1/booking/{seq} - 존재하지 않는 예약")
    void testGetBooking_NotFound() throws Exception {
        // Given
        when(bookingSvc.getBookingDetails(999)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/v1/booking/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", is("예약 정보를 찾을 수 없습니다.")));
    }
}
