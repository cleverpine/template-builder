package com.cleverpine.templatebuilder.service.springboottemplate;

import com.cleverpine.templatebuilder.config.SpringTemplateProperties;
import com.cleverpine.templatebuilder.service.FileService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TemplatePomHandler {
    private final SpringTemplateProperties springTemplateProperties;

    private final FileService fileService;

    private final TemplateModuleUtil templateModuleUtil;

    public TemplatePomHandler(SpringTemplateProperties springTemplateProperties,
                              FileService fileService,
                              TemplateModuleUtil templateModuleUtil) {
        this.springTemplateProperties = springTemplateProperties;
        this.fileService = fileService;
        this.templateModuleUtil = templateModuleUtil;
    }

    public void renameInServiceProjectPom(String serviceName) {
        var serviceProjectPath = String.format("/%s/%s/pom.xml",
                serviceName,
                springTemplateProperties.getProjectModuleServiceName());
        processPomFile(serviceProjectPath, document ->
                renameProperty(document,
                        createXPathExpression("project", "parent", "artifactId"), serviceName));
    }

    public void renameInApiProjectPom(String serviceName) {
        var apiProjectPath = String.format("/%s/%s/pom.xml",
                serviceName,
                springTemplateProperties.getProjectModuleApiName());
        processPomFile(apiProjectPath, document ->
                renameProperty(document,
                        createXPathExpression("project", "parent", "artifactId"), serviceName));
    }

    public void renameInRootPom(String name, String description) {
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

    private static void renameProperty(Document document, String xPathExpression, String newPropertyValue) {
        XPath xPath = document.createXPath(xPathExpression);

        Element propertyElement = (Element) xPath.selectSingleNode(document);
        if (propertyElement != null) {
            propertyElement.setText(newPropertyValue);
        }
    }

    private String createXPathExpression(String... elementNames) {
        return Arrays.stream(elementNames)
                .map(name -> "/*[local-name()='" + name + "']")
                .collect(Collectors.joining());
    }

    private void processPomFile(String pomFilePath, Consumer<Document> documentProcessor) {
        File pomFile = fileService.getFile(pomFilePath);

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(pomFile);

            documentProcessor.accept(document);

            try (FileWriter fileWriter = new FileWriter(pomFile)) {
                OutputFormat format = OutputFormat.createPrettyPrint();
                XMLWriter writer = new XMLWriter(fileWriter, format);
                writer.write(document);
            }
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
