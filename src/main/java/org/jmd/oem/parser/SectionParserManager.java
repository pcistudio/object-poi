package org.jmd.oem.parser;

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SectionParserManager implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(SectionParserManager.class);
    private List<SectionParser<?>> list = new ArrayList<>();

    private SectionParser<?> currentSectionParser = null;

    public SectionParserManager register(SectionParser<?> sectionParser) {
        list.add(sectionParser);
        return this;
    }

    public SectionParserManager register(List<SectionParser<?>> sectionParserList) {
        list.addAll(sectionParserList);
        return this;
    }

    public SectionParser<?> get(Row row, int rowIndex) {
        if (list.isEmpty()) {
            throw new IllegalStateException("There is none SectionParser register ");
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            SectionParser<?> sectionParser = list.get(i);
            if (sectionParser.isActive(row, rowIndex)) {
                notifyCompletion(sectionParser);
                return currentSectionParser = sectionParser;
            }
        }
        throw new IllegalStateException("SectionParser not found");
    }


    private void notifyCompletion(SectionParser<?> sectionParser) {
        if (currentSectionParser!=null && currentSectionParser != sectionParser){
            currentSectionParser.notifyCompletion();
        }
    }

    @Override
    public void close() {
        if (currentSectionParser != null) {
            currentSectionParser.notifyCompletion();
            currentSectionParser = null;
        }
    }




}
