package com.neverpile.fusion.jpa.collection;

import java.io.IOException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An {@link AttributeConverter} used to map fields typed {@link JsonNode} to LOB database columns.
 */
@Converter(autoApply = true)
@Component
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

  private final ObjectMapper objectMapper;
  
  @Autowired
  public JsonNodeConverter(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public String convertToDatabaseColumn(final JsonNode meta) {
    try {
      return objectMapper.writeValueAsString(meta);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Can't convert to JSON string", e);
    }
  }

  @Override
  public JsonNode convertToEntityAttribute(final String dbData) {
    try {
      return objectMapper.readTree(dbData);
    } catch (IOException e) {
      throw new IllegalArgumentException("Can't convert from JSON string", e);
    }
  }

}