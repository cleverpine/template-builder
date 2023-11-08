package com.cleverpine.templatebuilder.dto.yml.dockercompose;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DockerLocalNetwork {
    public static final String NETWORK_TYPE = "bridge";
    private String driver;

}
