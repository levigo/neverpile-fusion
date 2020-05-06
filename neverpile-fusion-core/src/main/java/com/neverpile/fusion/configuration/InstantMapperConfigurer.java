package com.neverpile.fusion.configuration;

import java.time.Instant;
import java.util.Date;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import com.neverpile.fusion.configuration.ModelMapperConfiguration.ModelMapperConfigurer;

@Component
public class InstantMapperConfigurer implements ModelMapperConfigurer {

  @Override
  public void configure(final ModelMapper mapper) {
    mapper.addConverter(new Converter<Instant, Date>() {
      @Override
      public Date convert(final MappingContext<Instant, Date> context) {
        return Date.from(context.getSource());
      }
    });
    mapper.addConverter(new Converter<Date, Instant>() {
      @Override
      public Instant convert(final MappingContext<Date, Instant> context) {
        return context.getSource().toInstant();
      }
    });
    
  }

}
