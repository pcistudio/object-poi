package com.pcistudio.poi.parser;

import com.pcistudio.poi.util.Preconditions;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Interface for read Section Parser
 */
public interface ReadSectionParser extends NamedComponent {

    boolean isActive(Row row);

    void accept(Row row);
    void notifyCompletion();

    static ReadSectionParser compose(ReadSectionParser ... parsers) {
        return new ComposeReadSectionParser(List.of(parsers));
    }

    class ComposeReadSectionParser implements ReadSectionParser {
        private final List<ReadSectionParser> readSectionParsers;
        private final String name;

        private ComposeReadSectionParser(List<ReadSectionParser> readSectionParsers) {
            Preconditions.notEmpty(readSectionParsers, "Parser list cannot be empty");
            this.readSectionParsers = readSectionParsers.stream()
                    .flatMap(compose())
                    .collect(Collectors.toList());
            this.name = NamedComponent.generateComposeName(readSectionParsers);
        }

        private static Function<ReadSectionParser, Stream<ReadSectionParser>> compose() {
            return readSectionParser -> {
                if (readSectionParser instanceof ComposeReadSectionParser) {
                    return ((ComposeReadSectionParser) readSectionParser).getReadSectionParsers().stream();
                } else {
                    return Stream.of(readSectionParser);
                }
            };
        }

        @Override
        public String getName() {
            return name;
        }

        private List<ReadSectionParser> getReadSectionParsers() {
            return readSectionParsers;
        }

        @Override
        public boolean isActive(Row row) {
            return readSectionParsers.stream()
                    .anyMatch(readSectionParser -> readSectionParser.isActive(row));
        }

        @Override
        public void accept(Row row) {
            for (ReadSectionParser sectionParser: readSectionParsers) {
                if (sectionParser.isActive(row)) {
                    sectionParser.accept(row);
                }
            }
        }

        @Override
        public void notifyCompletion() {
            for (ReadSectionParser sectionParser: readSectionParsers) {
                sectionParser.notifyCompletion();
            }
        }
//
//        private static String generateComposeName(List<ReadSectionParser> readSectionParsers) {
//            String composeName = readSectionParsers.stream()
//                    .map(NamedComponent::getName)
//                    .collect(Collectors.joining("-", "", ""));
//            return String.format("compose[%s]", composeName.substring(0, Math.min(composeName.length(), 20)));
//        }
    }
}