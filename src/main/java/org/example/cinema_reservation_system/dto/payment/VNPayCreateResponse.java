package org.example.cinema_reservation_system.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VNPayCreateResponse {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("paymentUrl")
    private String paymentUrl;
    
    @JsonProperty("bookingId")
    private Integer bookingId;
    
    @JsonProperty("amount")
    private Long amount;
    
    @JsonProperty("orderInfo")
    private String orderInfo;
    
    @JsonProperty("noRefundPolicy")
    private String noRefundPolicy = "Beta Cinemas áp dụng chính sách KHÔNG HOÀN/HỦY VÉ. Vui lòng kiểm tra kỹ thông tin trước khi thanh toán.";
    
    // Constructors
    public VNPayCreateResponse() {}
    
    public VNPayCreateResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public VNPayCreateResponse(String code, String message, String paymentUrl, Integer bookingId, Long amount, String orderInfo) {
        this.code = code;
        this.message = message;
        this.paymentUrl = paymentUrl;
        this.bookingId = bookingId;
        this.amount = amount;
        this.orderInfo = orderInfo;
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
    
    public String getPaymentUrl() {
        return paymentUrl;
    }
    
    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
    
    public Integer getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    
    public Long getAmount() {
        return amount;
    }
    
    public void setAmount(Long amount) {
        this.amount = amount;
    }
    
    public String getOrderInfo() {
        return orderInfo;
    }
    
    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
}
