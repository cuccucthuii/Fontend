package org.example.cinema_reservation_system.dto.statistics;

import java.math.BigDecimal;

public class RevenueByQuarterDTO {
    private int year;
    private int quarter;
    private BigDecimal revenue;

    public RevenueByQuarterDTO(int year, int quarter, BigDecimal revenue) {
        this.year = year;
        this.quarter = quarter;
        this.revenue = revenue;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getQuarter() { return quarter; }
    public void setQuarter(int quarter) { this.quarter = quarter; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}