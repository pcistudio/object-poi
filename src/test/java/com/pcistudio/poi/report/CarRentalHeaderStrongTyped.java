package com.pcistudio.poi.report;

import com.pcistudio.poi.parser.DataField;

import java.time.LocalDateTime;

public class CarRentalHeaderStrongTyped {

    @DataField(name = "Report Date", order = 3)
    private LocalDateTime reportDate;
    @DataField(name = "Number of Clients", order = 2)
    private int numberOfClients;
    @DataField(name = "Total Records", order = 4)
    private int totalRecords;
    @DataField(name = "Report Title:", order = 1)
    private String reportTitle;

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public CarRentalHeaderStrongTyped setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
        return this;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public CarRentalHeaderStrongTyped setNumberOfClients(int numberOfClients) {
        this.numberOfClients = numberOfClients;
        return this;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public CarRentalHeaderStrongTyped setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
        return this;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public CarRentalHeaderStrongTyped setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
        return this;
    }

    @Override
    public String toString() {
        return "CarRentalHeader{" +
                "reportTitle='" + reportTitle + '\'' +
                ", numberOfClients='" + numberOfClients + '\'' +
                ", reportDate='" + reportDate + '\'' +
                ", totalRecords='" + totalRecords + '\'' +
                '}';
    }
}