package com.cleverpine.templatebuilder.service;

import com.cleverpine.templatebuilder.dto.instructions.ProjectInstructions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectSetupServiceImpl implements  ProjectSetupService {

    private final FileService fileService;
    @Override
    public boolean setupProject(ProjectInstructions projectInstructions) {
        //TODO create dir
        fileService.createDirectory(projectInstructions.getSharedInstructions().getProjectName());
        //TODO clone each service
        //TODO add keycloak
        //TODO add cloud config
        //TODO create readme
        //TODO create compose
        return false;
    }
}
