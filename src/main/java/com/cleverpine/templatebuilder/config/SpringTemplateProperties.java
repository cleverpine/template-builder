package com.cleverpine.templatebuilder.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SpringTemplateProperties {

    @Value("${spring.template.repo}")
    private String projectRepo;

    @Value("${spring.template.branch}")
    private String projectBranch;

    @Value("${spring.template.name}")
    private String projectName;

    @Value("${spring.template.module-names.service}")
    private String projectModuleServiceName;

    @Value("${spring.template.module-names.api}")
    private String projectModuleApiName;

}
