package com.cleverpine.templatebuilder.service.springboottemplate;

import org.springframework.stereotype.Service;

@Service
public class TemplateModuleUtil {

    public static final String CLEVERPINE_PROJ_PATH = "src/main/java/com/cleverpine";
    public static final String SERVICE_STATIC_PATH = "src/main/resources/static";

    public String getSubmodulePath(String serviceName) {
        return String.format("%s/%s/%s",
                getServiceModuleName(serviceName),
                SERVICE_STATIC_PATH,
                getApiModuleName(serviceName));
    }

    public String getApiModuleName(String serviceName) {
        return String.format("%s-api", serviceName);
    }

    public String getServiceModuleName(String serviceName) {
        return String.format("%s-service", serviceName);
    }
}
