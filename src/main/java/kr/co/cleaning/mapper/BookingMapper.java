package kr.co.cleaning.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface BookingMapper {

    /**
     * 예약 등록
     * @param paramMap 예약 정보
     * @return 등록된 행 수
     */
    int insertBooking(HashMap<String, Object> paramMap);

    /**
     * 예약 순번으로 조회
     * @param cnsltSeq 예약 순번
     * @return 예약 상세 정보
     */
    HashMap<String, Object> getBookingBySeq(int cnsltSeq);

    /**
     * 날짜별 예약 목록 조회
     * @param bookingDate 예약 날짜 (YYYYMMDD)
     * @return 예약 목록
     */
    List<HashMap<String, Object>> getBookingsByDate(String bookingDate);
}
