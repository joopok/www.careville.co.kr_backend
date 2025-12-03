package kr.co.cleaning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "예약 요청 데이터")
public class BookingRequestDto {

    @Schema(description = "상품 번호", example = "1", required = true)
    @NotNull(message = "상품 번호는 필수입니다")
    private Integer productNo;

    @Schema(description = "상품명", example = "에어컨 청소", required = true)
    @NotBlank(message = "상품명은 필수입니다")
    private String productNm;

    @Schema(description = "판매 가격", example = "150000", required = true)
    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private Integer salePrice;

    @Schema(description = "서비스 소요시간", example = "2-3시간")
    private String serviceTime;

    @Schema(description = "고객 이름", example = "홍길동", required = true)
    @NotBlank(message = "고객 이름은 필수입니다")
    @Size(max = 30, message = "이름은 30자 이하여야 합니다")
    private String customerName;

    @Schema(description = "고객 연락처", example = "01012345678", required = true)
    @NotBlank(message = "연락처는 필수입니다")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String customerPhone;

    @Schema(description = "고객 이메일", example = "hong@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String customerEmail;

    @Schema(description = "예약 날짜", example = "20231225", required = true)
    @NotBlank(message = "예약 날짜는 필수입니다")
    @Pattern(regexp = "^\\d{8}$", message = "날짜는 YYYYMMDD 형식이어야 합니다")
    private String bookingDate;

    @Schema(description = "희망 시간대", example = "09:00 - 10:00")
    private String timeSlot;

    @Schema(description = "추가 요청사항", example = "현관문 비밀번호는 1234입니다")
    private String additionalRequests;

    @Schema(description = "우편번호", example = "12345")
    private String zip;

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    private String address1;

    @Schema(description = "상세 주소", example = "101동 201호")
    private String address2;

    // Getters and Setters
    public Integer getProductNo() {
        return productNo;
    }

    public void setProductNo(Integer productNo) {
        this.productNo = productNo;
    }

    public String getProductNm() {
        return productNm;
    }

    public void setProductNm(String productNm) {
        this.productNm = productNm;
    }

    public Integer getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Integer salePrice) {
        this.salePrice = salePrice;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getAdditionalRequests() {
        return additionalRequests;
    }

    public void setAdditionalRequests(String additionalRequests) {
        this.additionalRequests = additionalRequests;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }
}
