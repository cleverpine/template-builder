package com.cleverpine.templatebuilder.dto.dependency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MavenDependency {

    private String groupId;
    private String artifactId;
    private String version;
    private String branch;
}
