package com.cleverpine.templatebuilder.dto.yml.dockercompose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DockerCompose {
    private String version;
    private Map<String, DockerService> services;
    private DockerNetworks networks;

    public DockerCompose addService(String serviceName, DockerService service) {
        this.services.put(serviceName, service);
        return this;
    }
}
