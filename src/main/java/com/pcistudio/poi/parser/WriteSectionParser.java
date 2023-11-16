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
public interface WriteSectionParser<T> extends NamedComponent {
    //    SectionDescriptor<T> getSectionDescriptor();
    SectionLocation getSectionLocation();

    void write(Sheet sheet, SheetCursor cursor);

    int objectToBuildSize();

    static WriteSectionParser<?> compose(WriteSectionParser<?>... parsers) {
        return new WriteSectionParser.ComposeWriteSectionParser<>(List.of(parsers));
    }

    class ComposeWriteSectionParser<T> implements WriteSectionParser<T> {
        private final List<WriteSectionParser<?>> writeSectionParsers;
        private final String name;

        private ComposeSectionLocation sectionLocation;

        private ComposeWriteSectionParser(List<WriteSectionParser<?>> writeSectionParsers) {
            Preconditions.notEmpty(writeSectionParsers, "Parser list cannot be empty");
            this.writeSectionParsers = writeSectionParsers.stream()
                    .flatMap(compose())
                    .collect(Collectors.toList());
            this.name = NamedComponent.generateComposeName(writeSectionParsers);
            this.sectionLocation = new ComposeSectionLocation(
                    writeSectionParsers.stream()
                            .map(WriteSectionParser::getSectionLocation)
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
        public SectionDescriptor<T> getSectionLocation() {
            return null;
        }

        @Override
        public void write(Sheet sheet, SheetCursor cursor) {
            for (WriteSectionParser<?> writeSectionParser : writeSectionParsers) {
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

    class ComposeSectionLocation implements SectionLocation {

        List<SectionLocation> sectionLocations;

        public ComposeSectionLocation(List<SectionLocation> sectionLocations) {
            this.sectionLocations = sectionLocations;
        }

        @Override
        public int getRowStartIndex() {
            return 0;
        }

        @Override
        public int getDescriptorMapSize() {
            return 0;
        }

        @Override
        public Short getColumnCount() {
            return null;
        }

        @Override
        public int getColumnStartIndex() {
            return 0;
        }

        @Override
        public boolean isDisplayNextRow() {
            return false;
        }
    }

}