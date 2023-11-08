package com.cleverpine.templatebuilder.service.springboottemplate;

import com.cleverpine.templatebuilder.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class DockerFileHandler {

    private final String DOCKERFILE_NAME = "Dockerfile";

    private final FileService fileService;

    public void renameServiceNameInDockerfile(String serviceName) {
        var dockerFile = fileService.getFile(serviceName, DOCKERFILE_NAME);
        if (!dockerFile.exists() || !dockerFile.isFile()) {
            throw new RuntimeException(String
                    .format("Dockerfile not found for service %s", serviceName));
        }
        try {
            var dockerfileContent = Files.readString(dockerFile.toPath());
            Pattern pattern = Pattern.compile("^ENV SERVICE_NAME=(.*)$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(dockerfileContent);
            if (!matcher.find()) {
                throw new RuntimeException(String
                        .format("Failed to find ENV variable in Dockerfile for service %s", serviceName));
            }
            String newDockerfileContent = matcher.replaceFirst("ENV SERVICE_NAME=" + serviceName);
            Files.writeString(dockerFile.toPath(), newDockerfileContent);
        } catch (IOException e) {
            throw new RuntimeException(String
                    .format("Failed to modify Dockerfile for service %s", serviceName), e);
        }

    }
}
