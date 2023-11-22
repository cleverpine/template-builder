package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.dto.instructions.enums.BackendTemplateType;
import com.cleverpine.templatebuilder.dto.dependency.MavenDependency;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.GitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BackendServiceImpl implements BackendService {

    private final GitService gitService;

    private final FileService fileService;

    private final BackendServiceHandlerFactory backendServiceHandlerFactory;

    private final MavenDependencyHandler mavenDependencyHandler;

    @Override
    public void createService(BackendServiceInstructions instructions) {
        var handlers = backendServiceHandlerFactory.getHandlers(instructions);
        var serviceConfig = handlers.config();
        var git = gitService.cloneRepository(
                serviceConfig.getProjectRepo(),
                serviceConfig.getProjectBranch(),
                fileService.getFile(instructions.getName()));
        var branchesToMerge = instructions.getDependencies()
                .stream()
                .map(MavenDependency::getBranch)
                .toList();
        gitService.mergeBranches(git, branchesToMerge);
        handlers.pomHandler().updatePom(instructions);
        handlers.renameHandler().renameTemplateFiles(instructions);
    }

    @Override
    public Map<String, MavenDependency> getDependenciesMap(BackendTemplateType templateType) {
        return mavenDependencyHandler.getDependencies(templateType)
                .stream()
                .collect(Collectors.toMap(MavenDependency::getArtifactId, Function.identity()));
    }
}
