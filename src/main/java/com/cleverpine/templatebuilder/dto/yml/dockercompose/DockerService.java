package com.cleverpine.templatebuilder.dto.yml.dockercompose;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class DockerService {

    private String image;
    @JsonProperty("container_name")
    private String containerName;
    private String build;
    private List<String> ports;
    private List<String> environment;
    private List<String> networks;
    private List<String> volumes;
}
