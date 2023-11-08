package com.cleverpine.templatebuilder.dto.instructions;


import com.cleverpine.templatebuilder.exceptions.IllegalStateException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Set;

@Getter
@Setter
@ToString
public class ProjectInstructions {

    private final HashMap<String, BackendServiceInstructions> backendServiceInstructions;

    private final HashMap<String, FrontendServiceInstructions> frontendServiceInstructions;

    private final SharedInstructions sharedInstructions;

    private boolean gitSetup;

    public ProjectInstructions() {
        sharedInstructions = new SharedInstructions();
        backendServiceInstructions = new HashMap<>();
        frontendServiceInstructions = new HashMap<>();
    }

    public ProjectInstructions addBackendServiceInstructions(BackendServiceInstructions instructions) {
        if (instructions == null || instructions.getName() == null) {
            throw new IllegalStateException("Instructions ot service name missing");
        }
        backendServiceInstructions.put(instructions.getName(), instructions);
        return this;
    }

    public ProjectInstructions addFrontendServiceInstructions(FrontendServiceInstructions instructions) {
        if (instructions == null || instructions.getName() == null) {
            throw new IllegalStateException("Instructions ot service name missing");
        }
        frontendServiceInstructions.put(instructions.getName(), instructions);
        return this;
    }

    public Set<String> getBackendServiceNames() {
        return this.backendServiceInstructions.keySet();
    }

    public Set<String> getFrontendServiceNames() {
        return this.frontendServiceInstructions.keySet();
    }

}
