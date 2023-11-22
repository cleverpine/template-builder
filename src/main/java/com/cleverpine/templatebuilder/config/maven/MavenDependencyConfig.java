package com.cleverpine.templatebuilder.config.maven;

import com.cleverpine.templatebuilder.dto.dependency.MavenDependency;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "dependencies")
public class MavenDependencyConfig {

    private List<MavenDependency> spring;
    private List<MavenDependency> quarkus;

}
