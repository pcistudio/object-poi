package com.pcistudio.poi.parser.impl;

import com.pcistudio.poi.parser.ColumnSheetParser;
import com.pcistudio.poi.parser.SectionParserManagerBuilder;
import com.pcistudio.poi.report.CarRentalHeader;
import com.pcistudio.poi.report.CarRentalRecord;
import com.pcistudio.poi.report.CarRentalRevenue;
import com.pcistudio.poi.report.CarRentalTotal;

//rows
public class FullRevenueColumnSheetParserNotReadingHeader extends ColumnSheetParser<CarRentalRevenue> {

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
                                .withRowStartIndex(6));
        /**
         * this section has the real values in withColumnStartIndex(5)
         * here is changed to 7 and run the reader test and check if it alerts something about not reading any of the headers
         */
//        Section3
        builder.pivot(CarRentalTotal.class)
                .describe(config -> config.withName("Total")
                        .withColumnStartIndex(7)
                        .withObjectToBuild(carRentalRevenue.getCarRentalTotals())
                        .keyValue()
                        .withRowStartIndex(16));
    }

    @Override
    public String getSheetName() {
        return "Revenue";
    }
}
