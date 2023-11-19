package com.pcistudio.poi.parser;

import com.pcistudio.poi.report.CarRentalHeader;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldDescriptorTest {

    @Test
    void testLoadFrom() {
        Map<String, FieldDescriptor> stringFieldDescriptorMap = FieldDescriptor.loadFrom(CarRentalHeader.class);

        int index = 1;
        for (Map.Entry<String, FieldDescriptor> entry : stringFieldDescriptorMap.entrySet()) {
            assertEquals(index, entry.getValue().getOrder());
            if (index == 1) {
                assertEquals("Report Title:", entry.getValue().getName());
            } else if (index == 2) {
                assertEquals("Number of Clients", entry.getValue().getName());
            } else if (index == 3) {
                assertEquals("Report Date", entry.getValue().getName());
            } else if (index == 4) {
                assertEquals("Total Records", entry.getValue().getName());
            }
            index++;
        }
    }
}