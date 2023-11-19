package com.pcistudio.poi.report;

import com.pcistudio.poi.parser.DataField;

public class CarRentalTotal {

    @DataField(name = "Total Revenue\n(Last Month)")
    private String totalRevenue;

    public String getTotalRevenue() {
        return totalRevenue;
    }

    public CarRentalTotal setTotalRevenue(String totalRevenue) {
        this.totalRevenue = totalRevenue;
        return this;
    }
}
