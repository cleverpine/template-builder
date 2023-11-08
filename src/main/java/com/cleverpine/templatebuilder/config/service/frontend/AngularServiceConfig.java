package com.cleverpine.templatebuilder.config.service.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AngularServiceConfig extends FrontendServiceConfig {

    public AngularServiceConfig(@Value("${angular.template.repo}") String projectRepo,
                                @Value("${angular.template.branch}") String projectBranch,
                                @Value("${angular.template.name}") String projectName,
                                @Value("${angular.template.module-names.ui}") String projectModuleUiName,
                                @Value("${angular.template.module-names.api}") String projectModuleApiName) {
        super(projectRepo, projectBranch, projectName, projectModuleUiName, projectModuleApiName);
    }

}

