package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.dto.instructions.enums.BackendTemplateType;
import com.cleverpine.templatebuilder.dto.dependency.MavenDependency;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.GitService;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateModuleUtil;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BackendServiceImpl implements BackendService {

    private static final String GIT = ".git";
    private static final String GIT_MODULES = ".gitmodules";

    private final GitService gitService;

    private final FileService fileService;

    private final BackendServiceHandlerFactory backendServiceHandlerFactory;

    private final MavenDependencyHandler mavenDependencyHandler;
    private final TemplateModuleUtil templateModuleUtil;

    @Override
    public void createService(BackendServiceInstructions instructions) {
        var handlers = backendServiceHandlerFactory.getHandlers(instructions);
        var serviceConfig = handlers.config();
        var git = gitService.cloneRepository(
                serviceConfig.getProjectRepo(),
                serviceConfig.getProjectBranch(),
                fileService.getFile(instructions.getName()));
        mergeDependencyBranches(git, instructions.getDependencies());
        handlers.pomHandler().updatePom(instructions);
        handlers.renameHandler().renameTemplateFiles(instructions);
        applyGitConfigurations(instructions);
    }

    @Override
    public Map<String, MavenDependency> getDependenciesMap(BackendTemplateType templateType) {
        return mavenDependencyHandler.getDependencies(templateType)
                .stream()
                .collect(Collectors.toMap(MavenDependency::getArtifactId, Function.identity()));
    }

    private void mergeDependencyBranches(Git git, List<MavenDependency> dependencies) {
        var branchesToMerge = dependencies
                .stream()
                .map(MavenDependency::getBranch)
                .toList();
        gitService.mergeBranches(git, branchesToMerge);
    }

    private void applyGitConfigurations(BackendServiceInstructions instructions) {
        var serviceName = instructions.getName();
        var templateFolder = fileService.getFile(serviceName);
        var templateSubmoduleFolder = fileService
                .getFile(serviceName, templateModuleUtil.getSubmodulePath(serviceName));
        fileService.deleteFolderAndContents(new File(templateFolder.getPath(), GIT));
        fileService.deleteFolderAndContents(new File(templateFolder.getPath(), GIT_MODULES));
        fileService.deleteFolderAndContents(new File(templateSubmoduleFolder.getPath(), GIT));

        var repository = instructions.getRepository();
        var apiRepository = instructions.getApiRepository();
        boolean pushToRemote = repository != null && apiRepository != null;
        if (pushToRemote) {
            var apiRepositoryUrl = apiRepository.getRepoUrl();
            var repositoryUrl = repository.getRepoUrl();

            gitService.initRepository(templateSubmoduleFolder);
            gitService.addRemoteOrigin(templateSubmoduleFolder, apiRepositoryUrl);
            gitService.initialCommitAndPush(templateSubmoduleFolder);

            gitService.initRepository(templateFolder);
            gitService.addRemoteOrigin(templateFolder, repositoryUrl);
            fileService.deleteFolderAndContents(templateSubmoduleFolder);
            gitService.addSubmodule(templateFolder, apiRepositoryUrl, templateModuleUtil.getSubmodulePath(serviceName));
            gitService.initialCommitAndPush(templateFolder);
        }
    }
}
