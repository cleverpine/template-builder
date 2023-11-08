# CleverPine Template Builder

## Overview

CleverPine Template Builder is a Spring Shell application designed to streamline the development of new applications,
particularly in a microservice environment. It automates the process of cloning a template project from a repository,
renaming it, and adding it to the services in a docker-compose file for easy local development.

## Getting Started

### Prerequisites

You will need the following installed on your system:

1. Java 11 or higher
2. Docker if you want to run the services locally

### Installation

Download the JAR file from the release page and save it to a convenient location on your local machine.

### Running the Application

```sh
java -jar template-builder.jar
```

## Usage

The application provides a command-line interface for managing Spring Boot service templates.
The available commands are:

1. **new**: Create a new working environment with all needed services.
2. **dir-config**: Configure target directory for the template builder.
3. **git-config**: Configure Git with SSH key location and password.
4. **clone**: Clone and add Spring Boot service template to Git. This clones a template project from a repository,
   renames it with the service name provided and adds it to the services in a docker-compose file. It then pushes the
   new
   service and its API to their git repositories
5. **clone-only**: Clone Spring Boot service template without adding to Git.
6. **add-to-git**: Add existing Spring Boot service to Git.
7. **delete**: Delete Spring Boot service.
8. **delete-git-config**: Remove Git configuration from a Spring Boot service.

To execute a command, simply type the command name and press Enter. For some commands, you will be prompted for
additional information, such as directory paths, SSH key details, repositoryURLs, or service names and descriptions.
Follow the on-screen prompts to complete the desired operation.

The most commonly used command sequence will likely be `dir-config`, `git-config`, and `clone`.
Here's what each of them does:

- `new`: This command can create an environment with a backend & frontend services in it. It can also add a database and
  keycloak configurations. All selected services will be packed in a docker-compose file ready for local use. You will
  be prompted to configure each service separately. You can also choose to push the services to their git repositories.

- `dir-config`: This command sets the target directory where your services will be cloned to. You will be prompted
  to enter a directory relative to the location of the script. If target directory is explicitly set using this command,
  the application will use the default directory '/tmp'.

- `git-config`: This command sets up Git SSH access for cloning repositories and pushing changes. You will be prompted
  to enter the location of your SSH key (relative to the user home directory) and its password. If SSH key location is
  not explicitly set using this command, the application will use the default location '~/.ssh/'.

- `clone`: This command clones a Spring Boot service template from a Git repository, renames it with the service name
  you provide, and adds it to the services in a docker-compose file for easy local development. You will be prompted to
  enter a service name, a service description, and API and Service repo URLs.

## Troubleshooting

If a command is not available, it may be due to unmet prerequisites. For example, the `clone`, `clone-only`, and
`add-to-git` commands require Git access and a configured directory. If these prerequisites are not met, you will see
a message indicating that "git access and directory must be configured". Similarly, the `delete` and `delete-git-config`
commands require a configured directory. If this prerequisite is not met, you will see a message indicating
that "directory must be configured".