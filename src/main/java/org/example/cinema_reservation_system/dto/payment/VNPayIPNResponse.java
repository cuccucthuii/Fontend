package org.example.cinema_reservation_system.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VNPayIPNResponse {
    
    @JsonProperty("RspCode")
    private String rspCode;
    
    @JsonProperty("Message")
    private String message;
    
    // Constructors
    public VNPayIPNResponse() {}
    
    public VNPayIPNResponse(String rspCode, String message) {
        this.rspCode = rspCode;
        this.message = message;
    }
    
    // Getters and Setters
    public String getRspCode() {
        return rspCode;
    }
    
    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}









































