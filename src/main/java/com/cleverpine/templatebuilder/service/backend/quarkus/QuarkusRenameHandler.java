package com.cleverpine.templatebuilder.service.backend.quarkus;

import com.cleverpine.templatebuilder.config.service.backend.QuarkusServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.backend.RenameHandler;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateModuleUtil;
import org.springframework.stereotype.Service;

@Service
public class QuarkusRenameHandler extends RenameHandler {

    protected QuarkusRenameHandler(QuarkusServiceConfig config,
                                   TemplateModuleUtil templateModuleUtil,
                                   FileService fileService) {
        super(config, templateModuleUtil, fileService);
    }

    @Override
    public void renameTemplateFiles(BackendServiceInstructions instructions) {
        //TODO implement for Quarkus
    }
}
