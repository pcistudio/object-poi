package com.pcistudio.poi.util;

import com.pcistudio.poi.parser.impl.FullRevenueColumnSheetParser;
import com.pcistudio.poi.parser.impl.FullRevenueColumnSheetParser2;
import com.pcistudio.poi.parser.impl.RevenueColumnSheetParser;
import com.pcistudio.poi.parser.impl.RevenueMultiRowSectionColumnSheetParser;
import com.pcistudio.poi.processor.DefaultWorkbookReader;
import com.pcistudio.poi.processor.WorkbookReader;
import com.pcistudio.poi.report.CarRentalWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Util {
    public static InputStream ioStream(String name) throws FileNotFoundException {
        InputStream resource = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(name);
        if(resource != null) {
            return resource;
        }
        return new FileInputStream(name);
    }

    public static CarRentalWorkbook carRentalExample() throws IOException {
        return carRentalExample("CarRental.xlsx");
    }

    public static CarRentalWorkbook carRentalExample(String file) throws IOException {
        try (InputStream inputStream = ioStream(file)) {
            WorkbookReader workbookReader = new DefaultWorkbookReader(new RevenueColumnSheetParser());
            return workbookReader.parseToObject(inputStream, CarRentalWorkbook.class);
        }
    }

    public static CarRentalWorkbook fullCarRentalExample() throws IOException {
        return fullCarRentalExample("CarRental.xlsx");
    }
    public static CarRentalWorkbook fullCarRentalExample(String file) throws IOException {
        try (InputStream inputStream = ioStream(file)) {
            WorkbookReader workbookReader = new DefaultWorkbookReader(new FullRevenueColumnSheetParser());
            return workbookReader.parseToObject(inputStream, CarRentalWorkbook.class);
        }
    }


    public static CarRentalWorkbook fullCarRentalWithStartIndexExample() throws IOException {
        return fullCarRentalWithStartIndexExample("CarRental.xlsx");
    }
    public static CarRentalWorkbook fullCarRentalWithStartIndexExample(String file) throws IOException {
        try (InputStream inputStream = ioStream(file)) {
            WorkbookReader workbookReader = new DefaultWorkbookReader(new FullRevenueColumnSheetParser2());
            return workbookReader.parseToObject(inputStream, CarRentalWorkbook.class);
        }
    }

    public static CarRentalWorkbook multiSectionCarRentalExample() throws IOException {
        return multiSectionCarRentalExample("CarRentalRowMultiSection.xlsx");
    }
    public static CarRentalWorkbook multiSectionCarRentalExample(String file) throws IOException {
        try (InputStream inputStream = ioStream(file)) {
            WorkbookReader workbookReader = new DefaultWorkbookReader(new RevenueMultiRowSectionColumnSheetParser());
            return workbookReader.parseToObject(inputStream, CarRentalWorkbook.class);
        }
    }

    public static <T> boolean jsonCompare(T obj, T obj2) {
        return GsonUtil.toJson(obj).equals(GsonUtil.toJson(obj2));
    }

}
