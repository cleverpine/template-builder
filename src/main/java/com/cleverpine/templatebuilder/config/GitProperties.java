package com.cleverpine.templatebuilder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class GitProperties {
    @Value("${git.checkurl}")
    private String checkUrl;

    @Value("${git.sshkey.location}")
    private String sshKeyLocation;

    @Value("${git.sshkey.passphrase}")
    private String sshKeyPassphrase;

}
