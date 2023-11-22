package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.dto.instructions.enums.BackendTemplateType;
import com.cleverpine.templatebuilder.dto.dependency.MavenDependency;

import java.util.Map;

public interface BackendService {
    void createService(BackendServiceInstructions instructions);

    Map<String, MavenDependency> getDependenciesMap(BackendTemplateType templateType);
}
