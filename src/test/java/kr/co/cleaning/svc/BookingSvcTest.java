package kr.co.cleaning.svc;

import kr.co.cleaning.dto.BookingRequestDto;
import kr.co.cleaning.dto.BookingResponseDto;
import kr.co.cleaning.mapper.BookingMapper;
import kr.co.cleaning.core.config.KFException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Tests")
class BookingSvcTest {

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingSvc bookingSvc;

    private BookingRequestDto validRequest;
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    @BeforeEach
    void setUp() {
        // 유효한 예약 요청 데이터 설정
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
        validRequest.setAdditionalRequests("현관문 비밀번호는 1234입니다");
        validRequest.setZip("12345");
        validRequest.setAddress1("서울시 강남구 테헤란로 123");
        validRequest.setAddress2("101동 201호");
    }

    @Test
    @DisplayName("정상 예약 생성 성공")
    void testCreateBooking_Success() throws KFException {
        // Given
        when(bookingMapper.insertBooking(any(HashMap.class)))
            .thenAnswer(invocation -> {
                HashMap<String, Object> param = invocation.getArgument(0);
                param.put("CNSLT_SEQ", 123);
                return 1;
            });

        // When
        BookingResponseDto response = bookingSvc.createBooking(validRequest);

        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(123, response.getBookingSeq());
        assertEquals("예약이 성공적으로 접수되었습니다.", response.getMessage());

        verify(bookingMapper, times(1)).insertBooking(any(HashMap.class));
    }

    @Test
    @DisplayName("과거 날짜 예약 시 예외 발생")
    void testCreateBooking_PastDate_ThrowsException() {
        // Given
        validRequest.setBookingDate("20200101");

        // When & Then
        KFException exception = assertThrows(KFException.class, () -> {
            bookingSvc.createBooking(validRequest);
        });

        assertEquals("과거 날짜는 예약할 수 없습니다.", exception.getMessage());
        verify(bookingMapper, never()).insertBooking(any());
    }

    @Test
    @DisplayName("잘못된 날짜 형식 시 예외 발생")
    void testCreateBooking_InvalidDateFormat_ThrowsException() {
        // Given
        validRequest.setBookingDate("2023-12-25");  // Wrong format

        // When & Then
        KFException exception = assertThrows(KFException.class, () -> {
            bookingSvc.createBooking(validRequest);
        });

        assertEquals("날짜 형식이 올바르지 않습니다. (YYYYMMDD)",
                     exception.getMessage());
        verify(bookingMapper, never()).insertBooking(any());
    }

    @Test
    @DisplayName("DB 저장 실패 시 예외 발생")
    void testCreateBooking_DatabaseFailure_ThrowsException() {
        // Given
        when(bookingMapper.insertBooking(any(HashMap.class))).thenReturn(0);

        // When & Then
        KFException exception = assertThrows(KFException.class, () -> {
            bookingSvc.createBooking(validRequest);
        });

        assertEquals("예약 등록에 실패했습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("예약 상세 조회 성공")
    void testGetBookingDetails_Success() {
        // Given
        HashMap<String, Object> expectedBooking = new HashMap<>();
        expectedBooking.put("cnsltSeq", 123);
        expectedBooking.put("nm", "홍길동");

        when(bookingMapper.getBookingBySeq(123)).thenReturn(expectedBooking);

        // When
        HashMap<String, Object> result = bookingSvc.getBookingDetails(123);

        // Then
        assertNotNull(result);
        assertEquals(123, result.get("cnsltSeq"));
        assertEquals("홍길동", result.get("nm"));

        verify(bookingMapper, times(1)).getBookingBySeq(123);
    }

    @Test
    @DisplayName("요청 내용 문자열 생성 검증")
    void testCreateBooking_InquiryContentGeneration() throws KFException {
        // Given
        when(bookingMapper.insertBooking(any(HashMap.class)))
            .thenAnswer(invocation -> {
                HashMap<String, Object> param = invocation.getArgument(0);
                String inqryCn = (String) param.get("inqryCn");

                // Verify content format
                assertTrue(inqryCn.contains("상품명: 에어컨 청소"));
                assertTrue(inqryCn.contains("가격: 150000원"));
                assertTrue(inqryCn.contains("소요시간: 2-3시간"));
                assertTrue(inqryCn.contains("희망시간대: 09:00 - 10:00"));
                assertTrue(inqryCn.contains("현관문 비밀번호는 1234입니다"));

                param.put("CNSLT_SEQ", 123);
                return 1;
            });

        // When
        bookingSvc.createBooking(validRequest);

        // Then
        verify(bookingMapper, times(1)).insertBooking(any(HashMap.class));
    }
}
