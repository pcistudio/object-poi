package com.pcistudio.poi.processor;

import com.pcistudio.poi.report.*;
import com.pcistudio.poi.util.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultWorkbookReaderTest {
    @Test
    void testRevenueReport() throws IOException {
        CarRentalWorkbook carRentalWorkbook = Util.carRentalExample();

        CarRentalHeader carRentalHeader = carRentalWorkbook.getCarRentalRevenue().getCarRentalHeaders().get(0);
        assertEquals("Car rental revenue", carRentalHeader.getReportTitle());
        assertEquals("9", carRentalHeader.getTotalRecords());

        List<CarRentalRecord> carRentalRecords = carRentalWorkbook.getCarRentalRevenue().getCarRentalRecords();
        CarRentalRecord carRentalRecord = carRentalRecords.get(0);
        assertEquals("FG3425", carRentalRecord.getPlateNumber());
        assertEquals("2023", carRentalRecord.getModelYear());
        assertEquals("TOYOTA", carRentalRecord.getMaker());

        CarRentalRecord carRentalRecord1 = carRentalRecords.get(8);
        assertEquals("Y5RTGM", carRentalRecord1.getPlateNumber());
        assertEquals("2022", carRentalRecord1.getModelYear());
        assertEquals("HYUNDAI", carRentalRecord1.getMaker());

    }

    @Test
    void testFullRevenueReport() throws IOException {
        CarRentalWorkbook carRentalWorkbook = Util.fullCarRentalExample();

        CarRentalHeader carRentalHeader = carRentalWorkbook.getCarRentalRevenue().getCarRentalHeaders().get(0);
        assertEquals("Car rental revenue", carRentalHeader.getReportTitle());
        assertEquals("9", carRentalHeader.getTotalRecords());

        List<CarRentalRecord> carRentalRecords = carRentalWorkbook.getCarRentalRevenue().getCarRentalRecords();
        CarRentalRecord carRentalRecord = carRentalRecords.get(0);
        assertEquals("FG3425", carRentalRecord.getPlateNumber());
        assertEquals("2023", carRentalRecord.getModelYear());
        assertEquals("TOYOTA", carRentalRecord.getMaker());

        CarRentalRecord carRentalRecord1 = carRentalRecords.get(8);
        assertEquals("Y5RTGM", carRentalRecord1.getPlateNumber());
        assertEquals("2022", carRentalRecord1.getModelYear());
        assertEquals("HYUNDAI", carRentalRecord1.getMaker());

        List<CarRentalTotal> carRentalTotals = carRentalWorkbook.getCarRentalRevenue().getCarRentalTotals();
        CarRentalTotal carRentalTotal = carRentalTotals.get(0);
        assertEquals("17,819.04", carRentalTotal.getTotalRevenue());
    }

    @Test
    void testFullCarRentalWithStartIndex() throws IOException {
        CarRentalWorkbook carRentalWorkbook = Util.fullCarRentalWithStartIndexExample();

        CarRentalHeader carRentalHeader = carRentalWorkbook.getCarRentalRevenue().getCarRentalHeaders().get(0);
        assertEquals("Car rental revenue", carRentalHeader.getReportTitle());
        assertEquals("9", carRentalHeader.getTotalRecords());

        List<CarRentalRecord> carRentalRecords = carRentalWorkbook.getCarRentalRevenue().getCarRentalRecords();
        CarRentalRecord carRentalRecord = carRentalRecords.get(0);
        assertEquals("FG3425", carRentalRecord.getPlateNumber());
        assertEquals("2023", carRentalRecord.getModelYear());
        assertEquals("TOYOTA", carRentalRecord.getMaker());

        CarRentalRecord carRentalRecord1 = carRentalRecords.get(8);
        assertEquals("Y5RTGM", carRentalRecord1.getPlateNumber());
        assertEquals("2022", carRentalRecord1.getModelYear());
        assertEquals("HYUNDAI", carRentalRecord1.getMaker());

        List<CarRentalTotal> carRentalTotals = carRentalWorkbook.getCarRentalRevenue().getCarRentalTotals();
        CarRentalTotal carRentalTotal = carRentalTotals.get(0);
        assertEquals("17,819.04", carRentalTotal.getTotalRevenue());
    }

    @Test
    void testMultiRowSectionRevenueReport() throws IOException {
        CarRentalWorkbook carRentalWorkbook = Util.multiSectionCarRentalExample();

        CarRentalHeader carRentalHeader = carRentalWorkbook.getCarRentalRevenue().getCarRentalHeaders().get(0);
        assertEquals("Car rental revenue", carRentalHeader.getReportTitle());
        assertEquals("9", carRentalHeader.getTotalRecords());

        List<CarRentalRecord> carRentalRecords = carRentalWorkbook.getCarRentalRevenue().getCarRentalRecords();
        CarRentalRecord carRentalRecord = carRentalRecords.get(0);
        assertEquals("FG3425", carRentalRecord.getPlateNumber());
        assertEquals("2023", carRentalRecord.getModelYear());
        assertEquals("TOYOTA", carRentalRecord.getMaker());

        CarRentalRecord carRentalRecord1 = carRentalRecords.get(8);
        assertEquals("Y5RTGM", carRentalRecord1.getPlateNumber());
        assertEquals("2022", carRentalRecord1.getModelYear());
        assertEquals("HYUNDAI", carRentalRecord1.getMaker());

        List<CarRentalTotal> carRentalTotals = carRentalWorkbook.getCarRentalRevenue().getCarRentalTotals();
        CarRentalTotal carRentalTotal = carRentalTotals.get(0);
        assertEquals("17,819.04", carRentalTotal.getTotalRevenue());

        List<CarRentalAgentInfo> carRentalAgentInfos = carRentalWorkbook.getCarRentalRevenue().getCarRentalAgents();
        CarRentalAgentInfo carRentalAgentInfo = carRentalAgentInfos.get(0);
        assertEquals("Peter Pan", carRentalAgentInfo.getAgent());
        assertEquals("Toyota Everglades", carRentalAgentInfo.getDealer());
    }

//    @Test
//    void testMultiRowSectionRevenueReport() throws IOException {
//        CarRentalWorkbook carRentalWorkbook = Util.multiSectionCarRentalExample();
//
//        CarRentalHeader carRentalHeader = carRentalWorkbook.getCarRentalRevenue().getCarRentalHeaders().get(0);
//        assertEquals("Car rental revenue", carRentalHeader.getReportTitle());
//        assertEquals("9", carRentalHeader.getTotalRecords());
//
//        List<CarRentalRecord> carRentalRecords = carRentalWorkbook.getCarRentalRevenue().getCarRentalRecords();
//        CarRentalRecord carRentalRecord = carRentalRecords.get(0);
//        assertEquals("FG3425", carRentalRecord.getPlateNumber());
//        assertEquals("2023", carRentalRecord.getModelYear());
//        assertEquals("TOYOTA", carRentalRecord.getMaker());
//
//        CarRentalRecord carRentalRecord1 = carRentalRecords.get(8);
//        assertEquals("Y5RTGM", carRentalRecord1.getPlateNumber());
//        assertEquals("2022", carRentalRecord1.getModelYear());
//        assertEquals("HYUNDAI", carRentalRecord1.getMaker());
//
//        List<CarRentalTotal> carRentalTotals = carRentalWorkbook.getCarRentalRevenue().getCarRentalTotals();
//        CarRentalTotal carRentalTotal = carRentalTotals.get(0);
//        assertEquals("17,819.04", carRentalTotal.getTotalRevenue());
//
//        List<CarRentalAgentInfo> carRentalAgentInfos = carRentalWorkbook.getCarRentalRevenue().getCarRentalAgents();
//        CarRentalAgentInfo carRentalAgentInfo = carRentalAgentInfos.get(0);
//        assertEquals("Peter Pan", carRentalAgentInfo.getAgent());
//        assertEquals("Toyota Everglades", carRentalAgentInfo.getDealer());
//    }
}