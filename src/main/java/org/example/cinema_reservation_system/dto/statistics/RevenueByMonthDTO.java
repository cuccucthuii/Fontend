package org.example.cinema_reservation_system.dto.statistics;

import java.math.BigDecimal;

public class RevenueByMonthDTO {
    private int year;
    private int month;
    private BigDecimal revenue;

    public RevenueByMonthDTO(int year, int month, BigDecimal revenue) {
        this.year = year;
        this.month = month;
        this.revenue = revenue;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}