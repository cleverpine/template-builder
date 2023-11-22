package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.config.maven.MavenDependencyConfig;
import com.cleverpine.templatebuilder.dto.instructions.enums.BackendTemplateType;
import com.cleverpine.templatebuilder.dto.dependency.MavenDependency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MavenDependencyHandler {

    private final MavenDependencyConfig mavenDependencyConfig;

    public List<MavenDependency> getDependencies(BackendTemplateType templateType) {
        switch (templateType) {
            case SPRING -> {
                return mavenDependencyConfig.getSpring();
            }
            case QUARKUS -> {
                return mavenDependencyConfig.getQuarkus();
            }
            default -> {
                return Collections.emptyList();
            }
        }
    }
}
