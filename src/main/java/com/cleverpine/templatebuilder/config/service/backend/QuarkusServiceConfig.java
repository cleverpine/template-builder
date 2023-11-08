package com.cleverpine.templatebuilder.config.service.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QuarkusServiceConfig extends BackendServiceConfig {
    public QuarkusServiceConfig(@Value("${quarkus.template.repo}") String projectRepo,
                                @Value("${quarkus.template.branch}") String projectBranch,
                                @Value("${quarkus.template.name}") String projectName,
                                @Value("${quarkus.template.module-names.service}") String projectModuleServiceName,
                                @Value("${quarkus.template.module-names.api}") String projectModuleApiName) {
        super(projectRepo, projectBranch, projectName, projectModuleServiceName, projectModuleApiName);
    }
}
