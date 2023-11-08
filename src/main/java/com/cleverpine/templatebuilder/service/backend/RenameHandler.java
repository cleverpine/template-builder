package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.config.service.backend.BackendServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateModuleUtil;

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

public abstract class RenameHandler {

    protected final BackendServiceConfig config;

    protected final TemplateModuleUtil templateModuleUtil;

    protected final FileService fileService;

    protected RenameHandler(BackendServiceConfig config,
                            TemplateModuleUtil templateModuleUtil,
                            FileService fileService) {
        this.config = config;
        this.templateModuleUtil = templateModuleUtil;
        this.fileService = fileService;
    }

    public abstract void renameTemplateFiles(BackendServiceInstructions instructions);

    protected final void fixImports(String serviceName) {
        var importReplacements = Map.of(
                "package com.cleverpine." + config.getProjectName(),
                "package com.cleverpine." + serviceName,
                "import com.cleverpine." + config.getProjectName(),
                "import com.cleverpine." + serviceName
        );
        try {
            replaceWordsInDirectory(Paths.get(fileService.getTargetDirectory(), serviceName).toString(), importReplacements);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected final void renameYmlFile(String serviceName) {
        Path targetDir = Paths.get(fileService.getTargetDirectory(),
                serviceName,
                templateModuleUtil.getServiceModuleName(serviceName),
                TemplateModuleUtil.SERVICE_STATIC_PATH,
                templateModuleUtil.getApiModuleName(serviceName));
        File oldYmlFile = targetDir
                .resolve(config.getProjectModuleApiName() + ".yml").toFile();
        File newYmlFile = targetDir
                .resolve(templateModuleUtil.getApiModuleName(serviceName) + ".yml").toFile();
        boolean success = oldYmlFile.renameTo(newYmlFile);
        if (!success) {
            System.out.println("Failed to rename yml file");
        }
    }

    protected final void renameDirectory(Path oldDir, Path newDir) {
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

    protected final void replaceWordsInDirectory(String directoryPath, Map<String, String> wordReplacements) throws IOException {
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

    protected final void replaceWordsInFile(Path filePath, Map<String, String> wordReplacements) throws IOException {
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
