package com.cleverpine.templatebuilder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class CloudConfigProperties {

    @Value("${cloudconfig.image}")
    private String image;

    @Value("${cloudconfig.container-name}")
    private String containerName;

    @Value("${cloudconfig.port}")
    private int port;

    @Value("${cloudconfig.encrypt-key}")
    private String encryptKey;

    @Value("${cloudconfig.user}")
    private String user;

    @Value("${cloudconfig.password}")
    private String password;

    @Value("${cloudconfig.folder}")
    private String folder;

}