package com.cleverpine.templatebuilder.service.backend.quarkus;

import com.cleverpine.templatebuilder.config.service.backend.QuarkusServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.backend.TemplatePomHandler;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateModuleUtil;
import org.springframework.stereotype.Service;

@Service
public class QuarkusTemplatePomHandler extends TemplatePomHandler {
    public QuarkusTemplatePomHandler(FileService fileService,
                                     QuarkusServiceConfig config,
                                     TemplateModuleUtil templateModuleUtil) {
        super(fileService, config, templateModuleUtil);
    }

    @Override
    public void updatePom(BackendServiceInstructions instructions) {
        //TODO implement for Quarkus
    }
}
