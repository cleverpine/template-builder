package com.cleverpine.templatebuilder.service;

import com.cleverpine.templatebuilder.config.CloudConfigProperties;
import com.cleverpine.templatebuilder.config.DockerComposeProperties;
import com.cleverpine.templatebuilder.dto.yml.dockercompose.DockerCompose;
import com.cleverpine.templatebuilder.dto.yml.dockercompose.DockerLocalNetwork;
import com.cleverpine.templatebuilder.dto.yml.dockercompose.DockerNetworks;
import com.cleverpine.templatebuilder.dto.yml.dockercompose.DockerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class DockerComposeUpdater {

    public static final Set<Integer> RESERVED_PORTS = Set.of(8888);

    public static final String SERVICE_BUILD_FORMAT = "./%s";
    public static final String SERVICE_PORTS_FORMAT = "%d:%d";
    public static final String SERVICE_PORTS_SPRING_PROFILE = "SPRING_PROFILES_ACTIVE=%s";
    private final FileService fileService;
    private final DockerComposeProperties dockerComposeProperties;

    private final CloudConfigProperties cloudConfigProperties;

    public void addCloudConfigToDockerComposeFile() {
        addServiceToDockerCompose(
                cloudConfigProperties.getContainerName(), createCloudConfigService(), cloudConfigProperties.getPort());
    }

    public void addSpringServiceToDockerComposeFile(String newServiceName) {
        addServiceToDockerCompose(
                newServiceName, createNewSpringService(newServiceName), dockerComposeProperties.getSpringDefaultPort());
    }

    private void addServiceToDockerCompose(String serviceName, DockerService dockerService, Integer internalPort) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        var dockerComposeFile = fileService.getFile(dockerComposeProperties.getFileName());
        DockerCompose dockerCompose = getDockerCompose(mapper, dockerComposeFile);

        dockerService.setNetworks(List.of(DockerNetworks.NETWORK_NAME));
        if ((dockerService.getPorts() == null || dockerService.getPorts().isEmpty()) && internalPort != null) {
            var newServicePort = geNewServicePort(dockerCompose);
            dockerService.setPorts(List.of(String.format(SERVICE_PORTS_FORMAT, newServicePort, internalPort)));
        }

        dockerCompose.addService(serviceName, dockerService);

        updateDockerComposeFile(mapper, dockerComposeFile, dockerCompose);
    }

    private void updateDockerComposeFile(ObjectMapper mapper, File dcokerComposeFile, DockerCompose dockerCompose) {
        try {
            mapper.writeValue(dcokerComposeFile, dockerCompose);
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Failed to save %s file", dockerComposeProperties.getFileName()), e);
        }
    }

    private DockerCompose getDockerCompose(ObjectMapper mapper, File dcokerComposeFile) {
        DockerCompose dockerCompose;
        if (dcokerComposeFile.exists() && dcokerComposeFile.isFile()) {
            try {
                dockerCompose = mapper.readValue(dcokerComposeFile, DockerCompose.class);
            } catch (IOException e) {
                throw new RuntimeException(
                        String.format("Failed to load %s file", dockerComposeProperties.getFileName()), e);
            }
        } else {
            dockerCompose = createNewDockerCompose();
        }
        return dockerCompose;
    }

    private int geNewServicePort(DockerCompose compose) {
        int port = dockerComposeProperties.getDefaultServicePort();
        if (compose == null || compose.getServices() == null || compose.getServices().isEmpty()) {
            return port;
        }

        for (DockerService service : compose.getServices().values()) {
            for (String portMapping : service.getPorts()) {
                if (portMapping == null || portMapping.isEmpty()) {
                    continue;
                }
                String[] splitPortMapping = portMapping.replace("\"", "").split(":");
                if (splitPortMapping.length != 2) {
                    continue;
                }

                try {
                    Integer externalPort = Integer.parseInt(splitPortMapping[0]);
                    if (RESERVED_PORTS.contains(externalPort)) {
                        continue;
                    }
                    port = Math.max(port, externalPort);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid port mapping: " + portMapping);
                }
            }
        }
        port += 1;

        return port;
    }

    private DockerCompose createNewDockerCompose() {
        DockerCompose dockerCompose = new DockerCompose();
        dockerCompose.setVersion(dockerComposeProperties.getVersion());
        dockerCompose.setServices(new HashMap<>());
        dockerCompose.setNetworks(new DockerNetworks(new DockerLocalNetwork(DockerLocalNetwork.NETWORK_TYPE)));
        return dockerCompose;
    }

    private DockerService createCloudConfigService() {
        DockerService cloudConfigService = new DockerService();
        cloudConfigService.setImage(cloudConfigProperties.getImage());
        cloudConfigService.setContainerName(cloudConfigProperties.getContainerName());
        cloudConfigService.setEnvironment(List.of(
                String.format("ENCRYPT_KEY=%s", cloudConfigProperties.getEncryptKey()),
                String.format("SPRING_PROFILES_ACTIVE=%s", "docker, native"),
                String.format("SPRING_SECURITY_USER_NAME=%s", cloudConfigProperties.getUser()),
                String.format("SPRING_SECURITY_USER_PASSWORD=%s", cloudConfigProperties.getPassword()),
                String.format("SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCHLOCATIONS=file:/%s",
                        cloudConfigProperties.getFolder())
        ));
        cloudConfigService.setVolumes(List.of(
                String.format("./%s:/%s", cloudConfigProperties.getFolder(), cloudConfigProperties.getFolder())
        ));
        cloudConfigService.setPorts(List.of(String.format(SERVICE_PORTS_FORMAT,
                cloudConfigProperties.getPort(), cloudConfigProperties.getPort())));
        cloudConfigService.setNetworks(List.of(DockerNetworks.NETWORK_NAME));
        return cloudConfigService;
    }

    private DockerService createNewSpringService(String serviceName) {
        DockerService newService = new DockerService();
        newService.setBuild(
                String.format(SERVICE_BUILD_FORMAT, serviceName));
        newService.setEnvironment(List.of(
                String.format(SERVICE_PORTS_SPRING_PROFILE, dockerComposeProperties.getSpringProfile())));
        return newService;
    }

}
