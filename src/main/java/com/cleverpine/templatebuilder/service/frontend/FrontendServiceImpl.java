package com.cleverpine.templatebuilder.service.frontend;

import com.cleverpine.templatebuilder.config.service.frontend.AngularServiceConfig;
import com.cleverpine.templatebuilder.config.service.frontend.FrontendServiceConfig;
import com.cleverpine.templatebuilder.config.service.frontend.ReactServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.FrontendServiceInstructions;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.GitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FrontendServiceImpl implements FrontendService {

    private final GitService gitService;

    private final FileService fileService;

    private final ReactServiceConfig reactServiceConfig;

    private final AngularServiceConfig angularServiceConfig;

    @Override
    public void createService(FrontendServiceInstructions instructions) {
        var serviceConfig = getConfig(instructions);
        gitService.cloneRepository(
                serviceConfig.getProjectRepo(),
                serviceConfig.getProjectBranch(),
                fileService.getFile(instructions.getName()));
    }

    private FrontendServiceConfig getConfig(FrontendServiceInstructions instructions) {
        switch (instructions.getTemplateType()) {
            case REACT -> {
                return reactServiceConfig;
            }
            case ANGULAR -> {
                return angularServiceConfig;
            }
            default -> throw new IllegalStateException("Invalid template type in frontend instructions");
        }
    }
}
