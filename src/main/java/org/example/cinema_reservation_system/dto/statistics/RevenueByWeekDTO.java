package org.example.cinema_reservation_system.dto.statistics;

import java.math.BigDecimal;

public class RevenueByWeekDTO {
    private int year;
    private int week;
    private BigDecimal revenue;

    public RevenueByWeekDTO(int year, int week, BigDecimal revenue) {
        this.year = year;
        this.week = week;
        this.revenue = revenue;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getWeek() { return week; }
    public void setWeek(int week) { this.week = week; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}