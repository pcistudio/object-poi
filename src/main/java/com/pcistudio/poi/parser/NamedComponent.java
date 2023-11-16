package com.pcistudio.poi.parser;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Named Component Interface
 */
public interface NamedComponent {
    String getName();

    static String generateComposeName(List<? extends NamedComponent> SectionParsers) {
        String composeName = SectionParsers.stream()
                .map(NamedComponent::getName)
                .collect(Collectors.joining("-", "", ""));
        return String.format("compose[%s]", composeName.substring(0, Math.min(composeName.length(), 20)));
    }
}