package com.neverpile.fusion.configuration;

import java.util.List;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FusionModelMapperConfiguration {
  public interface ModelMapperConfigurer {
    void configure(ModelMapper mapper);
  }

  @Autowired(required = false)
  List<ModelMapperConfigurer> configurers;

  @Autowired
  ModelMapper modelMapper;

  @Bean
  @ConditionalOnMissingBean(ModelMapper.class)
  ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @PostConstruct
  public void configureModelMapper() {
    if (null != configurers)
      configurers.forEach(c -> c.configure(modelMapper));
  }
}
