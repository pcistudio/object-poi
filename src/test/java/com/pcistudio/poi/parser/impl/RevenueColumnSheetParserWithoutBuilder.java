package com.pcistudio.poi.parser.impl;

import com.pcistudio.poi.parser.*;
import com.pcistudio.poi.report.CarRentalHeader;
import com.pcistudio.poi.report.CarRentalRecord;
import com.pcistudio.poi.report.CarRentalRevenue;

public class RevenueColumnSheetParserWithoutBuilder extends ColumnSheetParser<CarRentalRevenue> {

    @Override
    protected void describeSections(CarRentalRevenue carRentalRevenue, SectionParserManager sectionParserManager) {
        sectionParserManager
                .register(
                        new PivotSectionParserBuilder<CarRentalHeader>()
                                .withName("Report Header")
                                .withObjectToBuild(carRentalRevenue.getCarRentalHeaders())
                                .withStartValue("Report Title:")
                                .keyValue()
                                .build()
                )
                .register(
                        new TableSectionParserBuilder<CarRentalRecord>()
                                .withName("MainTable")
                                .withRecordClass(CarRentalRecord.class)
                                .withObjectToBuild(carRentalRevenue.getCarRentalRecords())
                                .withStartValue("Plate Number")
                                .build()
                )
                .register(
                        new PivotSectionParserBuilder<>()
                                .withName("Header")
                                .withObjectToBuild(null)
                                .withStartValue("Total Revenue\n(Last Month)")
                                .keyValue()
                                .build()
                );
    }

    @Override
    protected void describeSections(CarRentalRevenue result, SectionParserManagerBuilder builder) {

    }

    @Override
    public String getSheetName() {
        return "Revenue";
    }

}
