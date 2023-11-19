package com.pcistudio.poi.report;

import com.pcistudio.poi.parser.DataField;

public class CarRentalHeader {

    @DataField(name = "Report Date", order = 3)
    private String reportDate;
    @DataField(name = "Number of Clients", order = 2)
    private String numberOfClients;
    @DataField(name = "Total Records", order = 4)
    private String totalRecords;
    @DataField(name = "Report Title:", order = 1)
    private String reportTitle;

    public String getReportTitle() {
        return reportTitle;
    }

    public CarRentalHeader setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
        return this;
    }

    public String getNumberOfClients() {
        return numberOfClients;
    }

    public CarRentalHeader setNumberOfClients(String numberOfClients) {
        this.numberOfClients = numberOfClients;
        return this;
    }

    public String getReportDate() {
        return reportDate;
    }

    public CarRentalHeader setReportDate(String reportDate) {
        this.reportDate = reportDate;
        return this;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public CarRentalHeader setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
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