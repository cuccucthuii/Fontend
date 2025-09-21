package org.example.cinema_reservation_system.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VNPayCreateRequest {
    
    @JsonProperty("bookingId")
    private Integer bookingId;
    
    @JsonProperty("amount")
    private Long amount; // Amount in VND (multiply by 100 for VNPay)
    
    @JsonProperty("orderInfo")
    private String orderInfo;
    
    @JsonProperty("orderType")
    private String orderType = "other";
    
    @JsonProperty("bankCode")
    private String bankCode; // Optional: specific bank code
    
    // Constructors
    public VNPayCreateRequest() {}
    
    public VNPayCreateRequest(Integer bookingId, Long amount, String orderInfo) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.orderInfo = orderInfo;
    }
    
    // Getters and Setters
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
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public String getBankCode() {
        return bankCode;
    }
    
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}









































