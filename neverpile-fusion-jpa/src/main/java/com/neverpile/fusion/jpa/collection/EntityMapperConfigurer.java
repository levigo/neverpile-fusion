package com.neverpile.fusion.jpa.collection;

import java.util.Arrays;

import org.modelmapper.Converter;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.fusion.configuration.FusionModelMapperConfiguration.ModelMapperConfigurer;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.spec.Specification;

/**
 * ModelMapper configuration facilitating the mapping between {@link Collection} and
 * {@link CollectionEntity} (and dependent objects).
 */
@Component
public class EntityMapperConfigurer implements ModelMapperConfigurer {

  private final ObjectMapper objectMapper;

  @Autowired
  public EntityMapperConfigurer(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void configure(final ModelMapper mapper) {
    mapper.addConverter(new Converter<Specification, JsonNode>() {
      @Override
      public JsonNode convert(final MappingContext<Specification, JsonNode> context) {
        return objectMapper.valueToTree(context.getSource());
      }
    });
    mapper.addConverter(new Converter<JsonNode, Specification>() {
      @Override
      public Specification convert(final MappingContext<JsonNode, Specification> context) {
        try {
          return objectMapper.treeToValue(context.getSource(), Specification.class);
        } catch (JsonProcessingException e) {
          throw new MappingException(Arrays.asList(new ErrorMessage("Can't convert tree to Specification", e)));
        }
      }
    });
  }

}
