package org.example.cinema_reservation_system.dto.statistics;

import java.math.BigDecimal;

public class RevenueByYearDTO {
    private int year;
    private BigDecimal revenue;

    public RevenueByYearDTO(int year, BigDecimal revenue) {
        this.year = year;
        this.revenue = revenue;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}