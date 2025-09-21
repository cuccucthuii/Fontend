package org.example.cinema_reservation_system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfig {
    
    // Environment-based configuration
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    
    // VNPay credentials - should be moved to environment variables
    private String tmnCode;
    private String hashSecret;
    
    // Environment-specific URLs
    private String payUrl;
    private String returnUrl;
    private String ipnUrl;
    
    // Fixed configuration
    private String locale = "vn";
    private String currCode = "VND";
    private String version = "2.1.0";
    private String command = "pay";
    
    // Timeout settings
    private int timeout = 30000; // 30 seconds
    
    // Security settings
    private boolean enableSignatureValidation = true;
    private boolean enableAmountValidation = true;
    
    // Getters and Setters
    public String getTmnCode() {
        return tmnCode;
    }
    
    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }
    
    public String getHashSecret() {
        return hashSecret;
    }
    
    public void setHashSecret(String hashSecret) {
        this.hashSecret = hashSecret;
    }
    
    public String getPayUrl() {
        return payUrl;
    }
    
    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
    
    public String getIpnUrl() {
        return ipnUrl;
    }
    
    public void setIpnUrl(String ipnUrl) {
        this.ipnUrl = ipnUrl;
    }
    
    public String getLocale() {
        return locale;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public String getCurrCode() {
        return currCode;
    }
    
    public void setCurrCode(String currCode) {
        this.currCode = currCode;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    // New getters and setters
    public String getActiveProfile() {
        return activeProfile;
    }
    
    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public boolean isEnableSignatureValidation() {
        return enableSignatureValidation;
    }
    
    public void setEnableSignatureValidation(boolean enableSignatureValidation) {
        this.enableSignatureValidation = enableSignatureValidation;
    }
    
    public boolean isEnableAmountValidation() {
        return enableAmountValidation;
    }
    
    public void setEnableAmountValidation(boolean enableAmountValidation) {
        this.enableAmountValidation = enableAmountValidation;
    }
    
    // Environment-specific URL getters
    public String getPayUrlForEnvironment() {
        if ("prod".equals(activeProfile)) {
            return "https://vnpayment.vn/paymentv2/vpcpay.html";
        } else {
            return "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        }
    }
    
    public String getReturnUrlForEnvironment() {
        if ("prod".equals(activeProfile)) {
            return returnUrl; // Should be configured for production domain
        } else {
            return returnUrl; // Development URL
        }
    }
    
    public String getIpnUrlForEnvironment() {
        if ("prod".equals(activeProfile)) {
            return ipnUrl; // Should be configured for production domain
        } else {
            return ipnUrl; // Development URL
        }
    }
    
    // Validation methods
    public boolean isProductionEnvironment() {
        return "prod".equals(activeProfile);
    }
    
    public boolean isDevelopmentEnvironment() {
        return "dev".equals(activeProfile) || "test".equals(activeProfile);
    }
}