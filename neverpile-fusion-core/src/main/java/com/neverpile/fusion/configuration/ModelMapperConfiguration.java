package com.neverpile.fusion.configuration;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {
  public interface ModelMapperConfigurer {
    void configure(ModelMapper mapper);
  }

  @Autowired(required = false)
  List<ModelMapperConfigurer> configurers;

  @Bean
  ModelMapper documentMapper() {
    ModelMapper m = new ModelMapper();
    
    if(null != configurers)
      configurers.forEach(c -> c.configure(m));

    return m;
  }
}
