package com.pcistudio.poi.parser;

/**
 * Interface that combine the reader and the writer for section parser
 *
 * @param <T>
 */
public interface SectionParser<T> extends ReadSectionParser, WriteSectionParser<T> {


}