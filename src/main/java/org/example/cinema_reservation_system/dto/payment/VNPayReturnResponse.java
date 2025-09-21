package org.example.cinema_reservation_system.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VNPayReturnResponse {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("bookingId")
    private Integer bookingId;
    
    @JsonProperty("transactionId")
    private String transactionId;
    
    @JsonProperty("amount")
    private Long amount;
    
    @JsonProperty("paymentStatus")
    private String paymentStatus;
    
    @JsonProperty("redirectUrl")
    private String redirectUrl; // URL to redirect user after payment
    
    @JsonProperty("noRefundPolicy")
    private String noRefundPolicy = "Beta Cinemas áp dụng chính sách KHÔNG HOÀN/HỦY VÉ. Vui lòng kiểm tra kỹ thông tin trước khi thanh toán.";
    
    // Constructors
    public VNPayReturnResponse() {}
    
    public VNPayReturnResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public VNPayReturnResponse(String code, String message, Integer bookingId, String transactionId, Long amount, String paymentStatus, String redirectUrl) {
        this.code = code;
        this.message = message;
        this.bookingId = bookingId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.redirectUrl = redirectUrl;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public Long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
