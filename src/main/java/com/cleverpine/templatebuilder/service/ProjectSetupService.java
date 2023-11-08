package com.cleverpine.templatebuilder.service;

import com.cleverpine.templatebuilder.dto.instructions.ProjectInstructions;

public interface ProjectSetupService {
    boolean setupProject(ProjectInstructions projectInstructions);

}
