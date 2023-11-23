package com.cleverpine.templatebuilder.service;

import com.cleverpine.templatebuilder.dto.instructions.*;
import com.cleverpine.templatebuilder.dto.instructions.enums.BackendTemplateType;
import com.cleverpine.templatebuilder.dto.instructions.enums.DatabaseType;
import com.cleverpine.templatebuilder.dto.instructions.enums.FrontendTemplateType;
import com.cleverpine.templatebuilder.dto.dependency.MavenDependency;
import com.cleverpine.templatebuilder.service.backend.BackendService;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CmdService {

    //TODO modify pattern to handle bitbucket ssh links
    private static final String PATTERN_GIT = "^git@([\\w\\.-]+):(\\S+)$";
    private static final String PATTERN_NAME = "^[a-z_]+$";
    private static final String PATTERN_DESCRIPTION = "^[a-zA-Z0-9\\s]+$";
    private static final Map<String, String> REGEX_EXAMPLES = Map.of(
            PATTERN_NAME, "my_project",
            PATTERN_DESCRIPTION, "This is a project description.",
            PATTERN_GIT, "git@ssh.dev.azure.com:v3/cleverpine/Java%20Test%20Project/template"
    );
    private static final Map<String, String> REGEX_DESCRIPTIONS = Map.of(
            PATTERN_NAME, "Allowed characters: lowercase letters and underscores",
            PATTERN_DESCRIPTION, "Allowed characters: alphanumeric and spaces",
            PATTERN_GIT, "Format: git@host:repository"
    );

    @Lazy
    @Autowired
    private LineReader lineReader;

    @Autowired
    private BackendService backendService;

    public String configureProject() {
        var projectInstructions = new ProjectInstructions();
        var sharedInstructions = projectInstructions.getSharedInstructions();

        sharedInstructions.setProjectName(
                promptForString("Enter project name: ", PATTERN_NAME, false));
        sharedInstructions.setProjectDescription(
                promptForString("Enter project description (optional): ", PATTERN_DESCRIPTION, true));
        sharedInstructions.setKeycloakEnabled(promptForBoolean("Include Keycloak "));
        sharedInstructions.setCloudConfigEnabled(promptForBoolean("Include CloudConfig"));

        boolean gitSetup = promptForBoolean("Do you want to push the project to a git repository? ");
        projectInstructions.setGitSetup(gitSetup);
        sharedInstructions.setLocalSetupRepository(promptRepoInstructions("local setup", gitSetup));

        while (promptForBoolean("Do you want to include a backend service? ")) {
            projectInstructions.addBackendServiceInstructions(
                    promptForBackendServiceInstructions(gitSetup, projectInstructions.getBackendServiceNames()));
        }
        while (promptForBoolean("Do you want to include a frontend service? ")) {
            projectInstructions.addFrontendServiceInstructions(
                    promptForFrontendServiceInstructions(projectInstructions));
        }

        if (promptForBoolean(String.format("\n Are you sure you want to create the %s project?",
                sharedInstructions.getProjectName()))) {
            createLocalSetup(projectInstructions);
            return projectInstructions.toString();
        } else {
            return "Project creation cancelled.";
        }
    }

    public String configureBackendService() {
        boolean gitSetup = promptForBoolean("Do you want to push the project to a git repository? ");
        var instructions = promptForBackendServiceInstructions(gitSetup, Collections.emptySet());

        if (promptForBoolean(String.format("\n Are you sure you want to create the %s project?", instructions.getName()))) {
            backendService.createService(instructions);
            return instructions.toString();
        } else {
            return "Project creation cancelled.";
        }
    }

    private void createLocalSetup(ProjectInstructions projectInstructions) {
        projectInstructions.getBackendServiceInstructions().values().forEach(s -> backendService.createService(s));
    }

    private <T extends BaseServiceInstructions> void promptForBaseInstructions(
            T instructions, boolean gitSetup, Set<String> usedNames) {
        String name = null;
        do {
            if (name != null) {
                System.out.println("\nThis service name is already in use!");
            }
            name = promptForString("\nEnter service name: ", PATTERN_NAME, false);
        } while (usedNames.contains(name));
        instructions.setName(name);
        instructions.setDescription(
                promptForString("Enter service description (optional): ", PATTERN_DESCRIPTION, true));
        instructions.setRepository(
                promptRepoInstructions(instructions.getName(), gitSetup)
        );
    }

    private RepoInstructions promptRepoInstructions(String repoName, boolean gitSetup) {
        if (gitSetup) {
            return new RepoInstructions(
                    promptForString(String.format("Enter %s repository url: ", repoName), PATTERN_GIT, false));
        }
        return null;
    }

    private BackendServiceInstructions promptForBackendServiceInstructions(boolean gitSetup, Set<String> usedNames) {
        BackendServiceInstructions instructions = new BackendServiceInstructions();

        promptForBaseInstructions(instructions, gitSetup, usedNames);

        var backEndTemplateType = promptForEnum("Enter Backend Template Type", BackendTemplateType.class);
        instructions.setTemplateType(backEndTemplateType);

        var dependencies = promptForDependencies(backEndTemplateType);
        instructions.setDependencies(dependencies);

        instructions.setDatabaseType(promptForEnum("Enter Database Type", DatabaseType.class));
        instructions.setRepository(promptRepoInstructions(String.format("%s api", instructions.getName()), gitSetup));
        System.out.println("Backend instructions complete.\n");
        return instructions;
    }

    private FrontendServiceInstructions promptForFrontendServiceInstructions(ProjectInstructions projectInstructions) {
        FrontendServiceInstructions instructions = new FrontendServiceInstructions();
        promptForBaseInstructions(
                instructions,
                projectInstructions.isGitSetup(),
                projectInstructions.getFrontendServiceNames());
        instructions.setTemplateType(
                promptForEnum("Enter Frontend Template Type", FrontendTemplateType.class));
        while (promptForBoolean("Do you want to include an API connection ")) {
            instructions.addApi(promptFromSet("API", projectInstructions.getBackendServiceNames()));
        }
        System.out.println("Frontend instructions complete.\n");

        return instructions;
    }

    private String promptForString(String prompt, String regexPattern, boolean optional) {
        String description = REGEX_DESCRIPTIONS.getOrDefault(regexPattern, "Please enter a string.");
        String result = lineReader.readLine(prompt + " (" + description + "): ");

        if (result == null || result.isEmpty()) {
            if (optional) {
                return null;
            } else {
                System.out.println(prompt + " cannot be empty");
                return promptForString(prompt, regexPattern, optional);
            }
        } else if (!result.matches(regexPattern)) {
            String example = REGEX_EXAMPLES.getOrDefault(regexPattern, "No example available");
            System.out.println("Input doesn't match the expected format. " + example);
            return promptForString(prompt, regexPattern, optional);
        } else {
            return result;
        }
    }

    private boolean promptForBoolean(String prompt) {
        String result = lineReader.readLine(prompt + " (y/n): ").toLowerCase();
        switch (result) {
            case "y":
                return true;
            case "n":
                return false;
            default:
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
                return promptForBoolean(prompt);
        }
    }

    private String promptFromSet(String promptBase, Set<String> selection) {
        List<String> selectionList = selection.stream().toList();
        return promptFromSelection(promptBase, selectionList);
    }

    private <E extends Enum<E>> E promptForEnum(String promptBase, Class<E> enumClass) {
        Map<String, E> eMap = Arrays.stream(enumClass.getEnumConstants())
                .collect(Collectors.toMap(Enum::name, e -> e));
        List<String> enumValues = Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList();
        var key = promptFromSelection(promptBase, enumValues);
        return eMap.get(key);
    }

    private String promptFromSelection(String promptBase, List<String> selection) {
        String prompt = generateSelectionPrompt(promptBase, selection);
        System.out.println(prompt);
        String result = lineReader.readLine("Please enter the number of your choice: ");
        try {
            int index = Integer.parseInt(result);
            return selection.get(index);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid input. Please enter a number from the list.");
            return promptFromSelection(promptBase, selection);
        }
    }

    private <E extends Enum<E>> String generateSelectionPrompt(String promptBase, List<?> enumValues) {
        StringBuilder builder = new StringBuilder(promptBase);
        builder.append(":");
        for (int i = 0; i < enumValues.size(); i++) {
            builder.append("\n");
            builder.append(i);
            builder.append(" - ");
            builder.append(enumValues.get(i));
        }
        return builder.toString();
    }

    private List<MavenDependency> promptForDependencies(BackendTemplateType templateType) {
        var promptBase = "Please enter the number of your choice";
        var availableDependencies = backendService.getDependenciesMap(templateType);
        var selectedDependencies = new ArrayList<MavenDependency>();
        while (promptForBoolean("Do you want to add external libraries to the project?")) {
            if (availableDependencies.isEmpty()) {
                System.out.println("No more dependencies available.");
                break;
            }
            var selectedDependency = promptFromSet(promptBase, availableDependencies.keySet());
            selectedDependencies.add(availableDependencies.get(selectedDependency));
            availableDependencies.remove(selectedDependency);
        }
        return selectedDependencies;
    }

}
