package com.cleverpine.templatebuilder.dto.instructions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SharedInstructions {

    private String projectName;

    private String projectDescription;

    private boolean keycloakEnabled;

    private boolean cloudConfigEnabled;

    private RepoInstructions localSetupRepository;

}
