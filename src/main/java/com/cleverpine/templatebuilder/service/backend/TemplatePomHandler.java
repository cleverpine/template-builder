package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.config.service.backend.BackendServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateModuleUtil;
import lombok.RequiredArgsConstructor;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class TemplatePomHandler {

    protected final FileService fileService;

    protected final BackendServiceConfig config;

    protected final TemplateModuleUtil templateModuleUtil;

    public abstract void updatePom(BackendServiceInstructions instructions);

    protected final void renameProperty(Document document, String xPathExpression, String newPropertyValue) {
        XPath xPath = document.createXPath(xPathExpression);

        Element propertyElement = (Element) xPath.selectSingleNode(document);
        if (propertyElement != null) {
            propertyElement.setText(newPropertyValue);
        }
    }

    protected final String createXPathExpression(String... elementNames) {
        return Arrays.stream(elementNames)
                .map(name -> "/*[local-name()='" + name + "']")
                .collect(Collectors.joining());
    }

    protected final void addDependencyProperties(Document document, String xPathExpression, List<Map<String, String>> properties) {
        XPath xPath = document.createXPath(xPathExpression);
        Element propertyElement = (Element) xPath.selectSingleNode(document);

        if (propertyElement != null) {
            properties.forEach(property -> {
                Element dependencyElement = propertyElement.addElement("dependency");
                property.forEach((key, value) -> dependencyElement.addElement(key).setText(value));
            });
        }
    }


    protected final void processPomFile(String pomFilePath, Consumer<Document> documentProcessor) {
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
