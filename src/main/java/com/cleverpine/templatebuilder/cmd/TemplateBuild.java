package com.cleverpine.templatebuilder.cmd;

import com.cleverpine.templatebuilder.dto.OperationResult;
import com.cleverpine.templatebuilder.service.CloudConfigService;
import com.cleverpine.templatebuilder.service.CmdService;
import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.GitService;
import com.cleverpine.templatebuilder.service.springboottemplate.TemplateService;
import lombok.RequiredArgsConstructor;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@ShellComponent
public class TemplateBuild extends AbstractShellComponent {

    public static final String CMD_NEW_PROJECT = "new";
    public static final String CMD_NEW_BACKEND_SERVICE = "new-backend";
    public static final String CMD_ADD_CLOUD_CONFIG = "add-cloud-config";
    public static final String CMD_DIR_CONFIG = "dir-config";
    public static final String CMD_GIT_CONFIG = "git-config";
    public static final String CMD_CLONE = "clone";
    public static final String CMD_CLONE_ONLY = "clone-only";
    public static final String CMD_ADD_TO_GIT = "add-to-git";
    public static final String CMD_DELETE = "delete";
    public static final String CMD_DELETE_GIT_CONFIG = "delete-git-config";

    private final TemplateService templateService;
    private final CloudConfigService cloudConfigService;
    private final GitService gitService;
    private final FileService fileService;
    private final CmdService cmdService;

    @Lazy
    @Autowired
    private LineReader lineReader;

//TODO    @ShellMethodAvailability({CMD_CLONE, CMD_CLONE_ONLY, CMD_ADD_TO_GIT, CMD_ADD_CLOUD_CONFIG, CMD_NEW_PROJECT, CMD_NEW_BACKEND_SERVICE})
    @ShellMethodAvailability({CMD_CLONE, CMD_CLONE_ONLY, CMD_ADD_TO_GIT, CMD_ADD_CLOUD_CONFIG})
    public Availability generalAvailability() {
        return gitService.gitConfigured() && fileService.directoryConfigured() ?
                Availability.available() : Availability.unavailable("git access and directory must be configured");
    }

    public Availability gitAvailability() {
        return gitService.gitConfigured() ?
                Availability.available() : Availability.unavailable("git access must be configured");
    }

    @ShellMethodAvailability({CMD_DELETE, CMD_DELETE_GIT_CONFIG})
    public Availability directoryAvailability() {
        return fileService.directoryConfigured() ?
                Availability.available() : Availability.unavailable("directory must be configured");
    }

    @ShellMethod(value = "Configure new Project", key = CMD_NEW_PROJECT)
    public String newProject() {
        return cmdService.configureProject();
    }

    @ShellMethod(value = "Configure new Backend Service", key = CMD_NEW_BACKEND_SERVICE)
    public String newBackendService() {
        return cmdService.configureBackendService();
    }

    @ShellMethod(value = "Add cloud config service", key = CMD_ADD_CLOUD_CONFIG)
    public String addCloudConfig() {
        cloudConfigService.addCloudConfig();
        return "Cloud config added";
    }

    @ShellMethod(value = "Configure target dir", key = CMD_DIR_CONFIG)
    public String configureTargetDir() {
        String targetDir = promptTargetDir();
        var configured = fileService.setTargetDirectory(targetDir);
        if (configured) {
            return "Target dir configured to " + targetDir;
        } else {
            return "Target dir configuration failed";
        }
    }

    @ShellMethod(value = "Configure Git", key = CMD_GIT_CONFIG)
    public String configureGit() {
        String sshKeyLocation = promptSshKeyLocation();
        String sshKeyPassword = promptSshKeyPassword();
        var configured = gitService.configureGit(sshKeyLocation, sshKeyPassword);
        if (configured) {
            return "Git configured";
        } else {
            return "Git configuration failed";
        }
    }

    private String promptSshKeyLocation() {
        String prompt = "Enter SSH key directory relative to the user home directory: ";
        String sshKeyLocation = lineReader.readLine(prompt);
        if (sshKeyLocation == null || sshKeyLocation.isEmpty()) {
            System.out.println("SSH key location cannot be empty");
            return promptSshKeyLocation();
        } else {
            return sshKeyLocation;
        }
    }


    private String promptSshKeyPassword() {
        String prompt = "Enter SSH key password: ";
        String sshKeyPassword = lineReader.readLine(prompt, '*');
        return sshKeyPassword;
    }

    private String promptTargetDir() {
        String prompt = "Enter target directory relative to the script: ";
        String targetDir = lineReader.readLine(prompt);
        if (targetDir == null || targetDir.isEmpty()) {
            System.out.println("Target dir cannot be empty");
            return promptTargetDir();
        } else {
            return targetDir;
        }
    }

    @ShellMethod(value = "Clone and add to Git Spring Boot service template", key = CMD_CLONE)
    public String cloneAndAddToGitSpringBootTemplate() {
        String serviceName = promptServiceName();
        OperationResult cloneResult = getCloneResult(serviceName);
        if (cloneResult.success()) {
            OperationResult addToGitResult = getAddToGitResult(serviceName);
            return cloneResult.text() + addToGitResult.text();
        } else {
            return cloneResult.text();
        }
    }

    @ShellMethod(value = "Clone Spring Boot service template", key = CMD_CLONE_ONLY)
    public String cloneSpringBootTemplate() {
        String serviceName = promptServiceName();
        OperationResult cloneResult = getCloneResult(serviceName);

        return cloneResult.text();
    }

    @ShellMethod(value = "Add Spring Boot service to Git", key = CMD_ADD_TO_GIT)
    public String addSpringBootTemplateToGit() {
        String serviceName = promptServiceName();
        OperationResult addToGitResult = getAddToGitResult(serviceName);

        return addToGitResult.text();
    }

    @ShellMethod(value = "Delete Spring Boot service", key = CMD_DELETE)
    public String deleteService() {
        String serviceName = promptServiceName();
        String promptMessage = String.format("Are you sure you want to delete %s? (y/n): ", serviceName);
        String input = lineReader.readLine(promptMessage);
        if (input.equalsIgnoreCase("y")) {
            templateService.deleteService(serviceName);
            return String.format("Deleted %s\n", serviceName);
        } else {
            return "Did not delete service\n";
        }
    }

    @ShellMethod(value = "Delete Spring Boot service's git configuration", key = CMD_DELETE_GIT_CONFIG)
    public String removeServiceGitConfiguration() {
        String serviceName = promptServiceName();
        String promptMessage = String.format("Are you sure you want to remove git configuration from %s? (y/n): ", serviceName);
        String input = lineReader.readLine(promptMessage);
        if (input.equalsIgnoreCase("y")) {
            templateService.removeServiceGitConfiguration(serviceName);
            return String.format("Removed git configuration from %s\n", serviceName);
        } else {
            return "Did not remove git configuration from service\n";
        }
    }

    private OperationResult getCloneResult(String serviceName) {
        String serviceDescription = promptServiceDescription();

        return templateService.cloneSpringBootTemplate(serviceName, serviceDescription);
    }

    private OperationResult getAddToGitResult(String serviceName) {
        String apiRepo = promptRepoUrl("API");
        String serviceRepo = promptRepoUrl("Service");

        return templateService.addSpringBootTemplateToGit(serviceName, apiRepo, serviceRepo);
    }

    private String promptRepoUrl(String repoType) {
        Pattern repoUrlPattern = Pattern.compile("^git@([\\w\\.-]+):(\\S+)$");

        String promptMessage = String.format("""
                Enter %s repo.
                The repo should be completely empty and the url should be in the format git@<user>:<url>
                and the user should have write access to the repo with their ssh key:
                input:
                """, repoType);

        System.out.print(promptMessage);
        String repoUrl = lineReader.readLine();
        Matcher repoUrlMatcher = repoUrlPattern.matcher(repoUrl);
        while (!repoUrlMatcher.matches()) {
            System.out.println("Invalid repo URL. Please enter a valid repo URL:");
            repoUrl = lineReader.readLine();
            repoUrlMatcher = repoUrlPattern.matcher(repoUrl);
        }

        return repoUrl;
    }

    private String promptServiceDescription() {
        System.out.print("""
                Enter service description. It can be left blank.
                input:
                """);
        String serviceDescription = lineReader.readLine();
        return serviceDescription;
    }

    private String promptServiceName() {
        Pattern serviceNamePattern = Pattern.compile("^[a-z]+$");

        System.out.print("""
                Enter service name.
                The name should be all lowercase latin letters and contain no special characters:
                input:
                """);
        String serviceName = lineReader.readLine();
        Matcher serviceNameMatcher = serviceNamePattern.matcher(serviceName);
        while (!serviceNameMatcher.matches()) {
            System.out.println("Invalid service name. Please enter a valid service name:");
            serviceName = lineReader.readLine();
            serviceNameMatcher = serviceNamePattern.matcher(serviceName);
        }
        return serviceName;
    }

}
