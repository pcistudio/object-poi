package com.pcistudio.poi.report;

import java.util.ArrayList;
import java.util.List;

public class CarRentalRevenue {
    private List<CarRentalHeader> carRentalHeaders = new ArrayList<>();
    private List<CarRentalRecord> carRentalRecords = new ArrayList<>();
    private List<CarRentalTotal> carRentalTotals = new ArrayList<>();

    private List<CarRentalAgentInfo> carRentalAgents = new ArrayList<>();

    public CarRentalRevenue() {
    }

    public void addInventoryRow(CarRentalRecord carRentalRecord) {
        if (carRentalRecord != null) {
            carRentalRecords.add(carRentalRecord);
        }
    }

    public List<CarRentalHeader> getCarRentalHeaders() {
        return carRentalHeaders;
    }

    public List<CarRentalRecord> getCarRentalRecords() {
        return carRentalRecords;
    }

    public List<CarRentalTotal> getCarRentalTotals() {
        return carRentalTotals;
    }

    public List<CarRentalAgentInfo> getCarRentalAgents() {
        return carRentalAgents;
    }
}
