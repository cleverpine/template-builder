package com.cleverpine.templatebuilder.service.backend.spring;

import com.cleverpine.templatebuilder.config.service.backend.SpringServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.dto.dependency.MavenDependency;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.backend.TemplatePomHandler;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateModuleUtil;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpringTemplatePomHandler extends TemplatePomHandler {

    public SpringTemplatePomHandler(FileService fileService,
                                    SpringServiceConfig config,
                                    TemplateModuleUtil templateModuleUtil) {
        super(fileService, config, templateModuleUtil);
    }

    @Override
    public void updatePom(BackendServiceInstructions instructions) {
        addExternalLibrariesInRootPom(instructions.getName(), instructions.getDependencies());
        addExternalLibrariesInServiceProjectPom(instructions.getName(), instructions.getDependencies());
        renameInRootPom(instructions.getName(), instructions.getDescription());
        renameInApiProjectPom(instructions.getName());
        renameInServiceProjectPom(instructions.getName());
    }

    private void addExternalLibrariesInRootPom(String name, List<MavenDependency> dependencies) {
        var rootPomPath = String.format("/%s/pom.xml", name);
        var dependencyProperties = dependencies.stream()
                .map(dependency -> {
                    Map<String, String> orderedMap = new LinkedHashMap<>();
                    orderedMap.put("groupId", dependency.getGroupId());
                    orderedMap.put("artifactId", dependency.getArtifactId());
                    orderedMap.put("version", dependency.getVersion());
                    return orderedMap;
                })
                .toList();
        processPomFile(rootPomPath, document ->
                addDependencyProperties(document,
                        createXPathExpression("project", "dependencyManagement", "dependencies"), dependencyProperties));
    }

    private void addExternalLibrariesInServiceProjectPom(String serviceName, List<MavenDependency> dependencies) {
        var serviceProjectPath = String.format("/%s/%s/pom.xml",
                serviceName,
                config.getProjectModuleServiceName());
        var dependencyProperties = dependencies.stream()
                .map(dependency -> {
                    Map<String, String> orderedMap = new LinkedHashMap<>();
                    orderedMap.put("groupId", dependency.getGroupId());
                    orderedMap.put("artifactId", dependency.getArtifactId());
                    return orderedMap;
                })
                .toList();
        processPomFile(serviceProjectPath, document ->
                addDependencyProperties(document, createXPathExpression("project", "dependencies"), dependencyProperties)
        );
    }

    private void renameInServiceProjectPom(String serviceName) {
        var serviceProjectPath = String.format("/%s/%s/pom.xml",
                serviceName,
                config.getProjectModuleServiceName());
        processPomFile(serviceProjectPath, document ->
                renameProperty(document,
                        createXPathExpression("project", "parent", "artifactId"), serviceName));
    }

    private void renameInApiProjectPom(String serviceName) {
        var apiProjectPath = String.format("/%s/%s/pom.xml",
                serviceName,
                config.getProjectModuleApiName());
        processPomFile(apiProjectPath, document ->
                renameProperty(document,
                        createXPathExpression("project", "parent", "artifactId"), serviceName));
    }

    private void renameInRootPom(String name, String description) {
        var rootPomPath = String.format("/%s/pom.xml", name);
        processPomFile(rootPomPath, document -> {
            renameProperty(document,
                    createXPathExpression("project", "artifactId"),
                    name);
            renameProperty(document,
                    createXPathExpression("project", "name"),
                    name);
            renameProperty(document,
                    createXPathExpression("project", "description"),
                    description);
            renameProperty(document,
                    createXPathExpression("project", "properties", "parent-project.name"),
                    name);
            renameProperty(document,
                    createXPathExpression("project", "properties", "service-project.name"),
                    templateModuleUtil.getServiceModuleName(name));
            renameProperty(document,
                    createXPathExpression("project", "properties", "api-project.name"),
                    templateModuleUtil.getApiModuleName(name));
        });
    }
}
