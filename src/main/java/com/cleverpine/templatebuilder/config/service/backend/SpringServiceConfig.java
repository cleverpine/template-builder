package com.cleverpine.templatebuilder.config.service.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringServiceConfig extends BackendServiceConfig {
    public SpringServiceConfig(@Value("${spring.template.repo}") String projectRepo,
                               @Value("${spring.template.branch}") String projectBranch,
                               @Value("${spring.template.name}") String projectName,
                               @Value("${spring.template.module-names.service}") String projectModuleServiceName,
                               @Value("${spring.template.module-names.api}") String projectModuleApiName) {
        super(projectRepo, projectBranch, projectName, projectModuleServiceName, projectModuleApiName);
    }
}
