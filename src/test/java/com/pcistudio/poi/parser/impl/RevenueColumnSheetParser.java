package com.pcistudio.poi.parser.impl;

import com.pcistudio.poi.parser.ColumnSheetParser;
import com.pcistudio.poi.parser.SectionParserManagerBuilder;
import com.pcistudio.poi.report.CarRentalHeader;
import com.pcistudio.poi.report.CarRentalRecord;
import com.pcistudio.poi.report.CarRentalRevenue;
//rows
public class RevenueColumnSheetParser extends ColumnSheetParser<CarRentalRevenue> {

    @Override
    protected void describeSections(CarRentalRevenue carRentalRevenue, SectionParserManagerBuilder builder) {
//        Section1
        builder.pivot(CarRentalHeader.class)
                .describe(
                        config -> config
                        .withName("Report Header")
                        .withObjectToBuild(carRentalRevenue.getCarRentalHeaders())
                        .keyValue()
                        .withRowStartIndex(0));
//        Section2
        builder.table(CarRentalRecord.class)
                    .describe(
                        config -> config.withName("Revenue by Car")
                        .withObjectToBuild(carRentalRevenue.getCarRentalRecords())
                        .withStartValue("Plate Number"));
//        Section3
        builder.pivot()
                    .describe(config -> config.withName("Total")
                        .withColumnStartIndex(6)
                        .keyValue()
                        .withStartValue("Total Revenue\n(Last Month)"));

    }

    @Override
    public String getSheetName() {
        return "Revenue";
    }
}
