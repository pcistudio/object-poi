package com.pcistudio.poi.processor;

import com.pcistudio.poi.parser.impl.FullRevenueColumnSheetParser;
import com.pcistudio.poi.parser.impl.FullRevenueColumnSheetParser2;
import com.pcistudio.poi.parser.impl.RevenueColumnSheetParser;
import com.pcistudio.poi.parser.impl.RevenueMultiRowSectionColumnSheetParser;
import com.pcistudio.poi.report.CarRentalAgentInfo;
import com.pcistudio.poi.report.CarRentalWorkbook;
import com.pcistudio.poi.util.PoiUtil;
import com.pcistudio.poi.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import static com.pcistudio.poi.util.Util.jsonCompare;
import static org.junit.jupiter.api.Assertions.*;

class DefaultWorkbookWriterTest {
    private static CarRentalWorkbook fullCarRentalWorkbook;

    private static CarRentalWorkbook multiRowSectionCarRentalWorkbook;

    private static CarRentalWorkbook fullCarRentalWithStartIndexWorkbook;

    @BeforeAll
    public static void  setUp() throws IOException {
        fullCarRentalWorkbook = Util.fullCarRentalExample();
        fullCarRentalWithStartIndexWorkbook = Util.fullCarRentalWithStartIndexExample();
        multiRowSectionCarRentalWorkbook = Util.multiSectionCarRentalExample();
    }

    @Test
    void testWriteRevenue() throws IOException {
        assertNotNull(fullCarRentalWorkbook.getCarRentalRevenue().getCarRentalTotals());
        Path testFile = Paths.get(String.format("test-%s.xlsx", Instant.now().toEpochMilli()));
        try (OutputStream outputStream = Files.newOutputStream(testFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            WorkbookWriter workbookWriter = new DefaultWorkbookWriter(new RevenueColumnSheetParser());
            workbookWriter.write(outputStream, fullCarRentalWorkbook);

            PoiUtil.dumpSheet(Files.newInputStream(testFile), "Revenue");
        } finally {
            Files.delete(testFile);
            System.out.println("Not Removing file");
        }

    }

    @Test
    void testWriteFullRevenue() throws IOException {
        assertNotNull(fullCarRentalWorkbook.getCarRentalRevenue().getCarRentalTotals());
        Path testFile = Paths.get(String.format("test-full-%s.xlsx", Instant.now().toEpochMilli()));
        try (OutputStream outputStream = Files.newOutputStream(testFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            WorkbookWriter workbookWriter = new DefaultWorkbookWriter(new FullRevenueColumnSheetParser());
            workbookWriter.write(outputStream, fullCarRentalWorkbook);

            PoiUtil.dumpSheet(Files.newInputStream(testFile), "Revenue");

            CarRentalWorkbook carRentalWorkbookfromResultFile = Util.fullCarRentalExample(testFile.getFileName().toString());
            assertTrue(jsonCompare(fullCarRentalWorkbook, carRentalWorkbookfromResultFile));
        } finally {
            Files.delete(testFile);
            System.out.println("Not Removing file");
        }

    }

    @Test
    void testFullCarRentalWithStartIndexWorkbook() throws IOException {
        assertNotNull(fullCarRentalWithStartIndexWorkbook.getCarRentalRevenue().getCarRentalTotals());
        assertEquals(9, fullCarRentalWithStartIndexWorkbook.getCarRentalRevenue().getCarRentalRecords().size());
        Path testFile = Paths.get(String.format("test-full-%s.xlsx", Instant.now().toEpochMilli()));
        try (OutputStream outputStream = Files.newOutputStream(testFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            WorkbookWriter workbookWriter = new DefaultWorkbookWriter(new FullRevenueColumnSheetParser2());
            workbookWriter.write(outputStream, fullCarRentalWithStartIndexWorkbook);

            PoiUtil.dumpSheet(Files.newInputStream(testFile), "Revenue");

            CarRentalWorkbook carRentalWorkbookfromResultFile = Util.fullCarRentalWithStartIndexExample(testFile.getFileName().toString());
            assertTrue(jsonCompare(fullCarRentalWithStartIndexWorkbook, carRentalWorkbookfromResultFile));
        } finally {
            Files.delete(testFile);
            System.out.println("Not Removing file");
        }
    }

    //todo make a test wit5h all start index set
    @Test
    void testWriteMultiSectionRevenue() throws IOException {
        assertEquals(1, multiRowSectionCarRentalWorkbook.getCarRentalRevenue().getCarRentalHeaders().size());
        assertNotNull(multiRowSectionCarRentalWorkbook.getCarRentalRevenue().getCarRentalTotals());
        assertFalse(multiRowSectionCarRentalWorkbook.getCarRentalRevenue().getCarRentalTotals().isEmpty());
        assertNotNull(multiRowSectionCarRentalWorkbook.getCarRentalRevenue().getCarRentalAgents());
        assertEquals(1, multiRowSectionCarRentalWorkbook.getCarRentalRevenue().getCarRentalAgents().size());

        assertFalse(multiRowSectionCarRentalWorkbook.getCarRentalRevenue().getCarRentalAgents().isEmpty());
        Path testFile = Paths.get(String.format("test-multi-%s.xlsx", Instant.now().toEpochMilli()));
        try (OutputStream outputStream = Files.newOutputStream(testFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            WorkbookWriter workbookWriter = new DefaultWorkbookWriter(new RevenueMultiRowSectionColumnSheetParser());
            workbookWriter.write(outputStream, multiRowSectionCarRentalWorkbook);

            PoiUtil.dumpSheet(Files.newInputStream(testFile), "Revenue");

            CarRentalWorkbook carRentalWorkbookfromResultFile = Util.multiSectionCarRentalExample(testFile.getFileName().toString());
            assertTrue(jsonCompare(multiRowSectionCarRentalWorkbook, carRentalWorkbookfromResultFile));
        } finally {
            Files.delete(testFile);
            System.out.println("Not Removing file");
        }
    }
}