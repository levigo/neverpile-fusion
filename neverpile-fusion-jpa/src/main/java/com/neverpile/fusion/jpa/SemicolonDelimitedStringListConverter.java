package com.neverpile.fusion.jpa;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  private static final String DELIMITER = ";";

  @Override
  public String convertToDatabaseColumn(final List<String> stringList) {
    if (stringList == null) {
      return "";
    }

    return stringList.stream() //
        .map(s -> s.replace(DELIMITER, "\\" + DELIMITER)) //
        .collect(Collectors.joining(DELIMITER));
  }

  @Override
  public List<String> convertToEntityAttribute(final String string) {
    return Stream //
        .of(string.split("(?<!\\\\)" + DELIMITER)) //
        .map(s -> s.replace("\\" + DELIMITER, DELIMITER)) //
        .collect(Collectors.toList());
  }

}