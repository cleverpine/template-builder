package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.dto.OperationResult;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.GitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BackendServiceImpl implements BackendService {

    private final GitService gitService;

    private final FileService fileService;

    private final BackendServiceHandlerFactory backendServiceHandlerFactory;

    @Override
    public void createService(BackendServiceInstructions instructions) {
        var handlers = backendServiceHandlerFactory.getHandlers(instructions);
        var serviceConfig = handlers.config();
        gitService.cloneRepository(
                serviceConfig.getProjectRepo(),
                serviceConfig.getProjectBranch(),
                fileService.getFile(instructions.getName()));
        handlers.pomHandler().updatePom(instructions);
        handlers.renameHandler().renameTemplateFiles(instructions);
    }
}
