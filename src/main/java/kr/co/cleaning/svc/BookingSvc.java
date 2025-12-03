package kr.co.cleaning.svc;

import kr.co.cleaning.dto.BookingRequestDto;
import kr.co.cleaning.dto.BookingResponseDto;
import kr.co.cleaning.mapper.BookingMapper;
import kr.co.cleaning.core.config.KFException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

@Service
public class BookingSvc {

    private static final Logger log = LoggerFactory.getLogger(BookingSvc.class);
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private BookingMapper bookingMapper;

    /**
     * 예약 생성 (유효성 검증 포함)
     * @param request 예약 요청 DTO
     * @return 예약 순번이 포함된 응답 DTO
     * @throws KFException 유효성 검증 실패 시
     */
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request) throws KFException {
        log.info("Creating booking for customer: {}", request.getCustomerName());

        // 예약 날짜 유효성 검증
        validateBookingDate(request.getBookingDate());

        // MyBatis 파라미터 맵 구성
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("nm", request.getCustomerName());
        paramMap.put("tel1", request.getCustomerPhone());
        paramMap.put("tel2", request.getCustomerPhone());
        paramMap.put("zip", request.getZip());
        paramMap.put("adres1", request.getAddress1());
        paramMap.put("adres2", request.getAddress2());
        paramMap.put("productNo", request.getProductNo());
        paramMap.put("hopeDay", request.getBookingDate());

        // 문의 내용 구성
        StringBuilder inqryCn = new StringBuilder();
        inqryCn.append("상품명: ").append(request.getProductNm()).append("\n");
        inqryCn.append("가격: ").append(request.getSalePrice()).append("원\n");
        if (request.getServiceTime() != null) {
            inqryCn.append("소요시간: ").append(request.getServiceTime()).append("\n");
        }
        if (request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
            inqryCn.append("이메일: ").append(request.getCustomerEmail()).append("\n");
        }
        if (request.getTimeSlot() != null) {
            inqryCn.append("희망시간대: ").append(request.getTimeSlot()).append("\n");
        }
        if (request.getAdditionalRequests() != null && !request.getAdditionalRequests().isEmpty()) {
            inqryCn.append("\n추가요청사항:\n").append(request.getAdditionalRequests());
        }

        paramMap.put("inqryCn", inqryCn.toString());

        // 예약 등록
        int result = bookingMapper.insertBooking(paramMap);

        if (result > 0) {
            Integer bookingSeq = (Integer) paramMap.get("CNSLT_SEQ");
            log.info("Booking created successfully with seq: {}", bookingSeq);
            return new BookingResponseDto(
                true,
                "예약이 성공적으로 접수되었습니다.",
                bookingSeq
            );
        } else {
            throw new KFException("예약 등록에 실패했습니다.");
        }
    }

    /**
     * 예약 날짜가 과거가 아닌지 검증
     * @param bookingDate YYYYMMDD 형식의 날짜
     * @throws KFException 날짜가 유효하지 않거나 과거인 경우
     */
    private void validateBookingDate(String bookingDate) throws KFException {
        try {
            LocalDate date = LocalDate.parse(bookingDate, DATE_FORMATTER);
            LocalDate today = LocalDate.now();

            if (date.isBefore(today)) {
                throw new KFException("과거 날짜는 예약할 수 없습니다.");
            }
        } catch (DateTimeParseException e) {
            throw new KFException("날짜 형식이 올바르지 않습니다. (YYYYMMDD)");
        }
    }

    /**
     * 예약 상세 정보 조회
     * @param cnsltSeq 예약 순번
     * @return 예약 상세 정보
     */
    public HashMap<String, Object> getBookingDetails(int cnsltSeq) {
        return bookingMapper.getBookingBySeq(cnsltSeq);
    }
}
