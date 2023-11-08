package com.cleverpine.templatebuilder.service.backend.spring;

import com.cleverpine.templatebuilder.config.service.backend.SpringServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.backend.RenameHandler;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateModuleUtil;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SpringRenameHandler extends RenameHandler {
    protected SpringRenameHandler(SpringServiceConfig config,
                                  TemplateModuleUtil templateModuleUtil,
                                  FileService fileService) {
        super(config, templateModuleUtil, fileService);
    }

    @Override
    public void renameTemplateFiles(BackendServiceInstructions instructions) {
        Path targetDir = Paths.get(fileService.getTargetDirectory(), instructions.getName());

        renameDirectory(targetDir.resolve(config.getProjectModuleServiceName()),
                targetDir.resolve(templateModuleUtil.getServiceModuleName(instructions.getName())));

        renameDirectory(targetDir.resolve(config.getProjectModuleApiName()),
                targetDir.resolve(templateModuleUtil.getApiModuleName(instructions.getName())));

        Path oldServiceRootDir = targetDir
                .resolve(templateModuleUtil.getServiceModuleName(instructions.getName()))
                .resolve(TemplateModuleUtil.CLEVERPINE_PROJ_PATH)
                .resolve(config.getProjectName());
        Path newServiceRootDir = targetDir
                .resolve(templateModuleUtil.getServiceModuleName(instructions.getName()))
                .resolve(TemplateModuleUtil.CLEVERPINE_PROJ_PATH)
                .resolve(instructions.getName());
        renameDirectory(oldServiceRootDir, newServiceRootDir);

        Path oldServiceStaticApiDir = targetDir
                .resolve(templateModuleUtil.getServiceModuleName(instructions.getName()))
                .resolve(TemplateModuleUtil.SERVICE_STATIC_PATH)
                .resolve(config.getProjectModuleApiName());
        Path newServiceStaticApiDir = targetDir
                .resolve(templateModuleUtil.getServiceModuleName(instructions.getName()))
                .resolve(TemplateModuleUtil.SERVICE_STATIC_PATH)
                .resolve(templateModuleUtil.getApiModuleName(instructions.getName()));
        renameDirectory(oldServiceStaticApiDir, newServiceStaticApiDir);
    }
}
