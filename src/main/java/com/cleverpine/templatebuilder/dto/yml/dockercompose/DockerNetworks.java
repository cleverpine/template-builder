package com.cleverpine.templatebuilder.dto.yml.dockercompose;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DockerNetworks {
    public static final String NETWORK_NAME = "local";
    private DockerLocalNetwork local;

}
