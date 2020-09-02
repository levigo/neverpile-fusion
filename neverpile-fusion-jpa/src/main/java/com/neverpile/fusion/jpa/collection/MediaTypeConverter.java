package com.neverpile.fusion.jpa.collection;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * An {@link AttributeConverter} used to {@link MediaType}s to character columns in the database.
 */
@Converter(autoApply = true)
@Component
public class MediaTypeConverter implements AttributeConverter<MediaType, String> {

  @Override
  public String convertToDatabaseColumn(final MediaType meta) {
    return meta.toString();
  }

  @Override
  public MediaType convertToEntityAttribute(final String dbData) {
    return MediaType.valueOf(dbData);
  }

}