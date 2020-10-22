package com.neverpile.fusion.jpa;

import java.util.Collections;
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
    if (stringList == null || stringList.isEmpty()) {
      return "";
    }
    
    String text = stringList.stream() //
        .map(s -> s.replace(DELIMITER, "\\" + DELIMITER)) //
        .collect(Collectors.joining(DELIMITER, "", DELIMITER));
    return text;
  }

  @Override
  public Set<String> convertToEntityAttribute(final String string) {
    if(string.isEmpty()) {
      return Collections.emptySet();
    }
    
    // due to the way split() below works, we need one special case treatment for a single, empty string
    if(string.equals(DELIMITER)) {
      return Collections.singleton("");
    }
    
    return Stream //
        .of(string.split("(?<!\\\\)" + DELIMITER)) //
        .map(s -> s.replace("\\" + DELIMITER, DELIMITER)) //
        .collect(Collectors.toSet());
  }
}