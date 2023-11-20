package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.Preconditions;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface for write Section Parser
 *
 * @param <T>
 */
//FIXME this is not a Parser name should be change
public interface SectionWriter<T> extends NamedComponent {
    //    SectionDescriptor<T> getSectionDescriptor();
    SectionBox getSectionBox();

    void write(Sheet sheet, SheetCursor cursor);

    int objectToBuildSize();


    static SectionWriter<?> compose(SectionWriter<?>... parsers) {
        return new ComposeSectionWriter<>(List.of(parsers));
    }

    class ComposeSectionWriter<T> extends AbstractSectionWriter<T> {
        private final List<SectionWriter<?>> sectionWriters;
        private final String name;

        private ComposeSectionBox sectionBox;

        private ComposeSectionWriter(List<SectionWriter<?>> sectionWriters) {
            Preconditions.notEmpty(sectionWriters, "Parser list cannot be empty");
            this.sectionWriters = sectionWriters.stream()
                    .flatMap(compose())
                    .collect(Collectors.toList());
            this.name = NamedComponent.generateComposeName(sectionWriters);
            this.sectionBox = new ComposeSectionBox(
                    sectionWriters.stream()
                            .map(SectionWriter::getSectionBox)
                            .collect(Collectors.toList())
            );
        }

        private static Function<SectionWriter<?>, Stream<SectionWriter<?>>> compose() {
            return sectionWriter -> {
                if (sectionWriter instanceof SectionWriter.ComposeSectionWriter) {
                    return ((ComposeSectionWriter<?>) sectionWriter).getWriteSectionParsers().stream();
                } else {
                    return Stream.of(sectionWriter);
                }
            };
        }

        @Override
        public String getName() {
            return name;
        }

        private List<SectionWriter<?>> getWriteSectionParsers() {
            return sectionWriters;
        }

        @Override
        public SectionBox getSectionBox() {
            return sectionBox;
        }

        @Override
        public void write(Sheet sheet, SheetCursor cursor) {
            for (SectionWriter<?> sectionWriter : sectionWriters) {

//                cursor.beginSection(writeSectionParser.getName(), writeSectionParser.getSectionBox(), writeSectionParser.objectToBuildSize());
                sectionWriter.write(sheet, cursor);
            }
        }

        @Override
        public int objectToBuildSize() {
            return sectionWriters.stream()
                    .mapToInt(SectionWriter::objectToBuildSize)
                    .max()
                    .getAsInt();
        }
    }

    class ComposeSectionBox implements SectionBox {
        private int rowCount;
        private short columnCount;

        private int rowStart;

        private int columnStart;

        public ComposeSectionBox(List<SectionBox> sectionBoxes) {
            Preconditions.notEmpty(sectionBoxes, "sectionBoxes cannot be empty");

            rowCount = 0;
            columnCount = 0;
            for (SectionBox box : sectionBoxes) {
                if (box.getRowCount() > rowCount) {
                    rowCount = box.getRowCount();
                }

            }
            SectionBox lastSection = sectionBoxes.get(sectionBoxes.size() - 1);
            columnCount = (short)(lastSection.getColumnStartIndex() + lastSection.getColumnCount());
            rowStart = sectionBoxes.get(0).getRowStartIndex();
            columnStart = sectionBoxes.get(0).getColumnStartIndex();
        }

        @Override
        public int getRowStartIndex() {
            return rowStart;
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public short getColumnCount() {
            return columnCount;
        }

        @Override
        public int getColumnStartIndex() {
            return columnStart;
        }

        @Override
        public boolean isDisplayNextRow() {
            return true;
        }

        @Override
        public boolean isStartIndexSet() {
            return getRowStartIndex() != -1;
        }
    }

}