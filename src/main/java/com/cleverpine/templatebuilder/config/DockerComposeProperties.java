package com.cleverpine.templatebuilder.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class DockerComposeProperties {

    @Value("${docker.compose.fileName}")
    private String fileName;

    @Value("${docker.compose.version}")
    private String version;

    @Value("${docker.compose.defaultServicePort}")
    private int defaultServicePort;

    @Value("${docker.compose.springProfile}")
    private String springProfile;

    @Value("${docker.compose.springDefaultPort}")
    private int springDefaultPort;
}
