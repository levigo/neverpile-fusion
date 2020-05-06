package com.neverpile.fusion.jpa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.stereotype.Component;

/**
 * An {@link AttributeConverter} used to map lists of strings to a single character column which
 * contains semicolon delimited values.
 */
@Converter(autoApply = true)
@Component
public class SemicolonDelimitedStringListConverter implements AttributeConverter<List<String>, String> {

  @Override
  public String convertToDatabaseColumn(final List<String> meta) {
    return meta.stream().map(String::trim).map(s -> s.replaceAll(";", "_")).collect(Collectors.joining(";"));
  }

  @Override
  public List<String> convertToEntityAttribute(final String dbData) {
    return dbData.isEmpty() ? Collections.emptyList() : Arrays.asList(dbData.split(";"));
  }

}