package com.neverpile.fusion.jpa.seen;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.neverpile.fusion.jpa.JPAConfiguration;

@Configuration
@ComponentScan
@EnableJpaRepositories
@EntityScan(basePackageClasses = SeenUnseenInfoEntity.class)
@Import(JPAConfiguration.class)
public class JPASeenUnseenServiceConfiguration {

}
