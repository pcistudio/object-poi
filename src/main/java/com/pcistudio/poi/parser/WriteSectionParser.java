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
public interface WriteSectionParser<T> extends NamedComponent {
    //    SectionDescriptor<T> getSectionDescriptor();
    SectionBox getSectionBox();

    void write(Sheet sheet, SheetCursor cursor);

    int objectToBuildSize();


    static WriteSectionParser<?> compose(WriteSectionParser<?>... parsers) {
        return new WriteSectionParser.ComposeWriteSectionParser<>(List.of(parsers));
    }

    class ComposeWriteSectionParser<T> extends AbstractWriteSectionParser<T> {
        private final List<WriteSectionParser<?>> writeSectionParsers;
        private final String name;

        private ComposeSectionBox sectionBox;

        private ComposeWriteSectionParser(List<WriteSectionParser<?>> writeSectionParsers) {
            Preconditions.notEmpty(writeSectionParsers, "Parser list cannot be empty");
            this.writeSectionParsers = writeSectionParsers.stream()
                    .flatMap(compose())
                    .collect(Collectors.toList());
            this.name = NamedComponent.generateComposeName(writeSectionParsers);
            this.sectionBox = new ComposeSectionBox(
                    writeSectionParsers.stream()
                            .map(WriteSectionParser::getSectionBox)
                            .collect(Collectors.toList())
            );
        }

        private static Function<WriteSectionParser<?>, Stream<WriteSectionParser<?>>> compose() {
            return writeSectionParser -> {
                if (writeSectionParser instanceof ComposeWriteSectionParser) {
                    return ((ComposeWriteSectionParser<?>) writeSectionParser).getWriteSectionParsers().stream();
                } else {
                    return Stream.of(writeSectionParser);
                }
            };
        }

        @Override
        public String getName() {
            return name;
        }

        private List<WriteSectionParser<?>> getWriteSectionParsers() {
            return writeSectionParsers;
        }

        @Override
        public SectionBox getSectionBox() {
            return sectionBox;
        }

        @Override
        public void write(Sheet sheet, SheetCursor cursor) {
            for (WriteSectionParser<?> writeSectionParser : writeSectionParsers) {

//                cursor.beginSection(writeSectionParser.getName(), writeSectionParser.getSectionBox(), writeSectionParser.objectToBuildSize());
                writeSectionParser.write(sheet, cursor);
            }
        }

        @Override
        public int objectToBuildSize() {
            return writeSectionParsers.stream()
                    .mapToInt(WriteSectionParser::objectToBuildSize)
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