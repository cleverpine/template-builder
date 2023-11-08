package com.cleverpine.templatebuilder.service.springboottemplate;

import com.cleverpine.templatebuilder.config.SpringTemplateProperties;
import com.cleverpine.templatebuilder.dto.OperationResult;
import com.cleverpine.templatebuilder.service.DockerComposeUpdater;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.GitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@RequiredArgsConstructor
@Service
public class TemplateService {

    private static final String GIT = ".git";
    private static final String GIT_MODULES = ".gitmodules";
    private final SpringTemplateProperties springTemplateProperties;
    private final TemplatePomHandler templatePomHandler;

    private final TemplateRenameHandler templateRenameHandler;

    private final TemplateModuleUtil templateModuleUtil;

    private final GitService gitService;

    private final FileService fileService;

    private final DockerComposeUpdater dockerComposeUpdater;

    private final DockerFileHandler dockerFileHandler;

    public OperationResult cloneSpringBootTemplate(String serviceName, String serviceDescription) {
        try {
            gitService.cloneRepository(
                    springTemplateProperties.getProjectRepo(),
                    springTemplateProperties.getProjectBranch(),
                    fileService.getFile(serviceName));
            handlePomXmlChanges(serviceName, serviceDescription);
            renameFolders(serviceName);
            dockerFileHandler.renameServiceNameInDockerfile(serviceName);
            dockerComposeUpdater.addSpringServiceToDockerComposeFile(serviceName);
            return new OperationResult(
                    String.format("Cloned Spring Boot service template as %s\n", serviceName),
                    true);
        } catch (Exception e) {
            e.printStackTrace();
            deleteService(serviceName);
            return new OperationResult(
                    String.format("Failed to clone Spring Boot service template as %s\n", serviceName),
                    false);
        }
    }

    public OperationResult addSpringBootTemplateToGit(String serviceName, String apiRepo, String serviceRepo) {
        var templateFolder = fileService.getFile(serviceName);
        var templateSubmoduleFolder = fileService
                .getFile(serviceName, templateModuleUtil.getSubmodulePath(serviceName));

        removeServiceGitConfiguration(templateFolder, templateSubmoduleFolder);

        try {
            gitService.initRepository(templateSubmoduleFolder);
            gitService.addRemoteOrigin(templateSubmoduleFolder, apiRepo);
            gitService.initialCommitAndPush(templateSubmoduleFolder);

            gitService.initRepository(templateFolder);
            gitService.addRemoteOrigin(templateFolder, serviceRepo);
            fileService.deleteFolderAndContents(templateSubmoduleFolder);
            gitService.addSubmodule(templateFolder, apiRepo, templateModuleUtil.getSubmodulePath(serviceName));
            gitService.initialCommitAndPush(templateFolder);
            return new OperationResult(
                    String.format("Successfully added Spring Boot service %s to git!\n", serviceName),
                    true);
        } catch (Exception e) {
            e.printStackTrace();
            removeServiceGitConfiguration(templateFolder, templateSubmoduleFolder);
            return new OperationResult(
                    String.format("Failed to add Spring Boot service %s to git\n", serviceName),
                    false);
        }
    }

    public OperationResult deleteService(String serviceName) {
        try {
            fileService.deleteFolderAndContents(fileService.getFile(serviceName));
            return new OperationResult(
                    String.format("Successfully deleted service %s\n", serviceName),
                    true);
        } catch (Exception e) {
            e.printStackTrace();
            return new OperationResult(
                    String.format("Failed to delete service %s\n", serviceName),
                    false);
        }
    }

    public OperationResult removeServiceGitConfiguration(String serviceName) {
        try {
            var templateFolder = fileService.getFile(serviceName);
            var templateSubmoduleFolder = fileService
                    .getFile(serviceName, templateModuleUtil.getSubmodulePath(serviceName));
            removeServiceGitConfiguration(templateFolder, templateSubmoduleFolder);
            return new OperationResult(
                    String.format("Successfully deleted git configurations for service %s\n", serviceName),
                    true);
        } catch (Exception e) {
            e.printStackTrace();
            return new OperationResult(
                    String.format("Failed to delete git configurations for service %s\n", serviceName),
                    false);
        }
    }

    private void removeServiceGitConfiguration(File templateFolder, File templateSubmoduleFolder) {
        fileService.deleteFolderAndContents(new File(templateFolder.getPath(), GIT));
        fileService.deleteFolderAndContents(new File(templateFolder.getPath(), GIT_MODULES));
        fileService.deleteFolderAndContents(new File(templateSubmoduleFolder.getPath(), GIT));
    }

    private void renameFolders(String serviceName) {
        templateRenameHandler.renameTemplate(serviceName);
        templateRenameHandler.renameYmlFile(serviceName);
        templateRenameHandler.fixImports(serviceName);
    }

    private void handlePomXmlChanges(String serviceName, String serviceDescription) {
        templatePomHandler.renameInRootPom(serviceName, serviceDescription);
        templatePomHandler.renameInApiProjectPom(serviceName);
        templatePomHandler.renameInServiceProjectPom(serviceName);
    }

}
