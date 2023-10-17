package com.pcistudio.poi.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class SectionParserManagerBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SectionParserManagerBuilder.class);

    private final SectionParserManager sectionParserManager = new SectionParserManager();

    public <T> SectionParserManagerBuilder table(Consumer<TableSectionParserBuilder<T>> config) {
        TableSectionParserBuilder<T> builder = new TableSectionParserBuilder<>();
        config.accept(builder);
        sectionParserManager.register(builder.build());
        return this;
    }

    public <T> SectionParserManagerBuilder pivot(Consumer<PivotSectionParserBuilder<T>> config) {
        PivotSectionParserBuilder<T> builder = new PivotSectionParserBuilder<>();
        config.accept(builder);
        sectionParserManager.register(builder.build());
        return this;
    }

    public SectionParserManager build() {
        return sectionParserManager;
    }

}
