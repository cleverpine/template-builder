package com.cleverpine.templatebuilder.config.service.frontend;

import lombok.Getter;

@Getter
public abstract class FrontendServiceConfig {
    private final String projectRepo;
    private final String projectBranch;
    private final String projectName;
    private final String projectModuleUiName;
    private final String projectModuleApiName;
    protected FrontendServiceConfig(String projectRepo,
                                    String projectBranch,
                                    String projectName,
                                    String projectModuleUiName,
                                    String projectModuleApiName) {
        this.projectRepo = projectRepo;
        this.projectBranch = projectBranch;
        this.projectName = projectName;
        this.projectModuleUiName = projectModuleUiName;
        this.projectModuleApiName = projectModuleApiName;
    }
}
