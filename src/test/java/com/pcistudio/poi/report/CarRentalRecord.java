package com.pcistudio.poi.report;

import com.pcistudio.poi.parser.DataField;

public class CarRentalRecord {
    //TODO Do I check that the all the columns in the class are present in the excel
    // Or the other way around


    @DataField(name = "Plate Number")
    private String plateNumber;
    @DataField(name = "Model Year")
    private String modelYear;
    @DataField(name = "Maker")
    private String maker;
    @DataField(name = "Model")
    private String model;
    @DataField(name = "Rental Price/Day")
    private String rentalPricePerDay;
    @DataField(name = "Days Rented\n(Last Month)")
    private String daysRentedLastMonth;
    @DataField(name = "Revenue\n(Last Month)", format = "#.##")
    private String revenueLastMonth;

    public String getPlateNumber() {
        return plateNumber;
    }

    public CarRentalRecord setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
        return this;
    }

    public String getModelYear() {
        return modelYear;
    }

    public CarRentalRecord setModelYear(String modelYear) {
        this.modelYear = modelYear;
        return this;
    }

    public String getMaker() {
        return maker;
    }

    public CarRentalRecord setMaker(String maker) {
        this.maker = maker;
        return this;
    }

    public String getModel() {
        return model;
    }

    public CarRentalRecord setModel(String model) {
        this.model = model;
        return this;
    }

    public String getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public CarRentalRecord setRentalPricePerDay(String rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
        return this;
    }

    public String getDaysRentedLastMonth() {
        return daysRentedLastMonth;
    }

    public CarRentalRecord setDaysRentedLastMonth(String daysRentedLastMonth) {
        this.daysRentedLastMonth = daysRentedLastMonth;
        return this;
    }

    public String getRevenueLastMonth() {
        return revenueLastMonth;
    }

    public CarRentalRecord setRevenueLastMonth(String revenueLastMonth) {
        this.revenueLastMonth = revenueLastMonth;
        return this;
    }
}
