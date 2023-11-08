package com.cleverpine.templatebuilder.service.backend.spring;

import com.cleverpine.templatebuilder.config.service.backend.SpringServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.backend.TemplatePomHandler;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateModuleUtil;
import org.springframework.stereotype.Service;

@Service
public class SpringTemplatePomHandler extends TemplatePomHandler {

    public SpringTemplatePomHandler(FileService fileService,
                                    SpringServiceConfig config,
                                    TemplateModuleUtil templateModuleUtil) {
        super(fileService, config, templateModuleUtil);
    }

    @Override
    public void updatePom(BackendServiceInstructions instructions) {
        renameInRootPom(instructions.getName(), instructions.getDescription());
        renameInApiProjectPom(instructions.getName());
        renameInServiceProjectPom(instructions.getName());
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
