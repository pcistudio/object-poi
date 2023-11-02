package com.pcistudio.poi.parser;

public class SectionCursor {
    private SectionDescriptor sectionDescriptor;

    private int rowIndex = 0;

    private int colIndex = 0;

    public SectionCursor(SectionDescriptor sectionDescriptor) {
        this.sectionDescriptor = sectionDescriptor;
    }


}
