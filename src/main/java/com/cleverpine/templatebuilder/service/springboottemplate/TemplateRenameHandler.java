package com.cleverpine.templatebuilder.service.springboottemplate;

import com.cleverpine.templatebuilder.config.SpringTemplateProperties;
import com.cleverpine.templatebuilder.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class TemplateRenameHandler {

    private final FileService fileService;

    private final SpringTemplateProperties springTemplateProperties;

    private final TemplateModuleUtil templateModuleUtil;

    public void renameTemplate(String serviceName) {
        Path targetDir = Paths.get(fileService.getTargetDirectory(), serviceName);

        renameDirectory(targetDir.resolve(springTemplateProperties.getProjectModuleServiceName()),
                targetDir.resolve(templateModuleUtil.getServiceModuleName(serviceName)));

        renameDirectory(targetDir.resolve(springTemplateProperties.getProjectModuleApiName()),
                targetDir.resolve(templateModuleUtil.getApiModuleName(serviceName)));

        Path oldServiceRootDir = targetDir.resolve(templateModuleUtil.getServiceModuleName(serviceName))
                .resolve(TemplateModuleUtil.CLEVERPINE_PROJ_PATH)
                .resolve(springTemplateProperties.getProjectName());
        Path newServiceRootDir = targetDir.resolve(templateModuleUtil.getServiceModuleName(serviceName))
                .resolve(TemplateModuleUtil.CLEVERPINE_PROJ_PATH)
                .resolve(serviceName);
        renameDirectory(oldServiceRootDir, newServiceRootDir);

        Path oldServiceStaticApiDir = targetDir.resolve(templateModuleUtil.getServiceModuleName(serviceName))
                .resolve(TemplateModuleUtil.SERVICE_STATIC_PATH)
                .resolve(springTemplateProperties.getProjectModuleApiName());
        Path newServiceStaticApiDir = targetDir.resolve(templateModuleUtil.getServiceModuleName(serviceName))
                .resolve(TemplateModuleUtil.SERVICE_STATIC_PATH)
                .resolve(templateModuleUtil.getApiModuleName(serviceName));
        renameDirectory(oldServiceStaticApiDir, newServiceStaticApiDir);
    }

    public void fixImports(String serviceName) {
        var importReplacements = Map.of(
                "package com.cleverpine." + springTemplateProperties.getProjectName(),
                "package com.cleverpine." + serviceName,
                "import com.cleverpine." + springTemplateProperties.getProjectName(),
                "import com.cleverpine." + serviceName
        );
        try {
            replaceWordsInDirectory(Paths.get(fileService.getTargetDirectory(), serviceName).toString(), importReplacements);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void renameYmlFile(String serviceName) {
        Path targetDir = Paths.get(fileService.getTargetDirectory(),
                serviceName,
                templateModuleUtil.getServiceModuleName(serviceName),
                TemplateModuleUtil.SERVICE_STATIC_PATH,
                templateModuleUtil.getApiModuleName(serviceName));
        File oldYmlFile = targetDir
                .resolve(springTemplateProperties.getProjectModuleApiName() + ".yml").toFile();
        File newYmlFile = targetDir
                .resolve(templateModuleUtil.getApiModuleName(serviceName) + ".yml").toFile();
        boolean success = oldYmlFile.renameTo(newYmlFile);
        if (!success) {
            System.out.println("Failed to rename yml file");
        }
    }

    private void renameDirectory(Path oldDir, Path newDir) {
        try {
            Files.move(oldDir, newDir);
        } catch (IOException e) {
            if (Files.isDirectory(oldDir)) {
                throw new RuntimeException("Unable to move directory: " + oldDir, e);
            } else {
                throw new IllegalArgumentException("Old path is not a directory: " + oldDir);
            }
        }
    }

    private void replaceWordsInDirectory(String directoryPath, Map<String, String> wordReplacements) throws IOException {
        Path dir = Paths.get(directoryPath);

        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Path is not a directory");
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            replaceWordsInFile(path, wordReplacements);
                        } catch (IOException e) {
                            System.err.println("Failed to replace words in file: " + path + " - " + e.getMessage());
                        }
                    });
        }
    }

    private void replaceWordsInFile(Path filePath, Map<String, String> wordReplacements) throws IOException {
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        List<String> modifiedLines = lines.stream().map(line -> {
            String modifiedLine = line;
            for (Map.Entry<String, String> entry : wordReplacements.entrySet()) {
                Pattern pattern = Pattern.compile(entry.getKey());
                Matcher matcher = pattern.matcher(modifiedLine);
                modifiedLine = matcher.replaceAll(entry.getValue());
            }
            return modifiedLine;
        }).collect(Collectors.toList());

        Files.write(filePath, modifiedLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
