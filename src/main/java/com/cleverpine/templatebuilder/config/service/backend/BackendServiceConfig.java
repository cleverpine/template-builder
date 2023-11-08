package com.cleverpine.templatebuilder.config.service.backend;

import lombok.Getter;

@Getter
public abstract class BackendServiceConfig {
    private final String projectRepo;
    private final String projectBranch;
    private final String projectName;
    private final String projectModuleServiceName;
    private final String projectModuleApiName;

    protected BackendServiceConfig(String projectRepo,
                                   String projectBranch,
                                   String projectName,
                                   String projectModuleServiceName,
                                   String projectModuleApiName) {
        this.projectRepo = projectRepo;
        this.projectBranch = projectBranch;
        this.projectName = projectName;
        this.projectModuleServiceName = projectModuleServiceName;
        this.projectModuleApiName = projectModuleApiName;
    }
}
