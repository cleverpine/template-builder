package com.cleverpine.templatebuilder.service;

import com.cleverpine.templatebuilder.dto.instructions.ProjectInstructions;
import com.cleverpine.templatebuilder.dto.yml.dockercompose.DockerService;

import java.util.Set;

public interface BaseServiceHandler {
    Set<DockerService> createService(ProjectInstructions projectInstructions);

}
