package com.neverpile.fusion.jpa;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SemicolonDelimitedStringSetConverter implements AttributeConverter<Set<String>, String> {
  private static final String DELIMITER = ";";

  @Override
  public String convertToDatabaseColumn(final Set<String> stringList) {
    if (stringList == null) {
      return "";
    }

    return stringList.stream() //
        .map(s -> s.replace(DELIMITER, "\\" + DELIMITER)) //
        .collect(Collectors.joining(DELIMITER));
  }

  @Override
  public Set<String> convertToEntityAttribute(final String string) {
    return Stream //
        .of(string.split("(?<!\\\\)" + DELIMITER)) //
        .map(s -> s.replace("\\" + DELIMITER, DELIMITER)) //
        .collect(Collectors.toSet());
  }
}