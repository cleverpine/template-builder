package com.cleverpine.templatebuilder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CloudConfigService {

    private final DockerComposeUpdater dockerComposeUpdater;

    public void addCloudConfig() {
        //TODO add folder for cloud config
        //TODO add local config for each service
        //TODO push to git cloud config
        dockerComposeUpdater.addCloudConfigToDockerComposeFile();
    }


}
