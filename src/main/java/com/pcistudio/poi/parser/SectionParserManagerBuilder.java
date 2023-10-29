package com.pcistudio.poi.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class SectionParserManagerBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SectionParserManagerBuilder.class);

    private final SectionParserManager sectionParserManager = new SectionParserManager();

    public <T> TableSectionParserManagerBuilder<T> table(Class<T> recordClass) {
        return new TableSectionParserManagerBuilder<>(recordClass);
    }

    public <T> TableSectionParserManagerBuilder<T> table() {
        return table(null);
    }

    public <T> PivotSectionParserManagerBuilder<T> pivot(Class<T> recordClass) {
        return new PivotSectionParserManagerBuilder<>(recordClass);
    }

    public <T> PivotSectionParserManagerBuilder<T> pivot() {
        return pivot(null);
    }

    public SectionParserManager build() {
        return sectionParserManager;
    }


    public class TableSectionParserManagerBuilder<T> {
        private final Class<T> recordClass;

        public TableSectionParserManagerBuilder(Class<T> recordClass) {
            this.recordClass = recordClass;
        }

        public void describe(Consumer<TableSectionParserBuilder<T>> config) {
            TableSectionParserBuilder<T> builder = new TableSectionParserBuilder<>();
            config.accept(builder);
            builder.withRecordClass(recordClass);
            if (recordClass == null) {
                builder.withObjectToBuild(null);
            }
            sectionParserManager.register(builder.build());
        }
    }

    public class PivotSectionParserManagerBuilder<T> {
        private final Class<T> recordClass;

        public PivotSectionParserManagerBuilder(Class<T> recordClass) {
            this.recordClass = recordClass;
        }

        public void describe(Consumer<PivotSectionParserBuilder<T>> config) {
            PivotSectionParserBuilder<T> builder = new PivotSectionParserBuilder<>();
            config.accept(builder);
            builder.withRecordClass(recordClass);
            if (recordClass == null) {
                builder.withObjectToBuild(null);
            }
            sectionParserManager.register(builder.build());
        }
    }
}
