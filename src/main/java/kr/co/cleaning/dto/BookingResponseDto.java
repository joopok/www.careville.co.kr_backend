package kr.co.cleaning.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예약 응답 데이터")
public class BookingResponseDto {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "예약이 성공적으로 접수되었습니다.")
    private String message;

    @Schema(description = "예약 순번", example = "123")
    private Integer bookingSeq;

    public BookingResponseDto() {
    }

    public BookingResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BookingResponseDto(boolean success, String message, Integer bookingSeq) {
        this.success = success;
        this.message = message;
        this.bookingSeq = bookingSeq;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getBookingSeq() {
        return bookingSeq;
    }

    public void setBookingSeq(Integer bookingSeq) {
        this.bookingSeq = bookingSeq;
    }
}
