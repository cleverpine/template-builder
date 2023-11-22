package com.cleverpine.templatebuilder.config.maven;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(MavenDependencyConfig.class)
@PropertySource(name = "maven-property-source",value = "classpath:maven-dependencies.yml", factory = MavenPropertySourceFactory.class)
public class MavenPropertySourceConfig {
}
