package com.cleverpine.templatebuilder.config.service.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReactServiceConfig extends FrontendServiceConfig {

    public ReactServiceConfig(@Value("${react.template.repo}") String projectRepo,
                              @Value("${react.template.branch}") String projectBranch,
                              @Value("${react.template.name}") String projectName,
                              @Value("${react.template.module-names.ui}") String projectModuleUiName,
                              @Value("${react.template.module-names.api}") String projectModuleApiName) {
        super(projectRepo, projectBranch, projectName, projectModuleUiName, projectModuleApiName);
    }

}
