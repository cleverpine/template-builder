package com.cleverpine.templatebuilder.service;

import com.cleverpine.templatebuilder.config.GitProperties;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.util.FS;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GitService {

    private boolean gitConfigured = false;

    private final GitProperties gitProperties;

    public boolean gitConfigured() {
        return gitConfigured;
    }

    public boolean configureGit(String sshKeyLocation, String sshKeyPassword) {
        gitProperties.setSshKeyLocation(sshKeyLocation);
        gitProperties.setSshKeyPassphrase(sshKeyPassword);
        checkConnection();
        return gitConfigured;
    }

    public void checkConnection() {
        //TODO for no internet testing
//        gitConfigured = checkSshConnection(gitProperties.getCheckUrl());
    }

    public boolean checkSshConnection(String repositoryUrl) {
        File sshDir = new File(FS.DETECTED.userHome(), gitProperties.getSshKeyLocation());

        var sshSessionFactory = new SshdSessionFactory();
        sshSessionFactory.setSshDirectory(sshDir);
        SshSessionFactory.setInstance(sshSessionFactory);

        try {
            var result = Git.lsRemoteRepository()
                    .setRemote(repositoryUrl)
                    .setCredentialsProvider(getCredentials())
                    .call();
            return !result.isEmpty();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Git cloneRepository(String repositoryUrl, String branch, File targetDirectory) {
        File sshDir = new File(FS.DETECTED.userHome(), gitProperties.getSshKeyLocation());

        var sshSessionFactory = new SshdSessionFactory();
        sshSessionFactory.setSshDirectory(sshDir);
        SshSessionFactory.setInstance(sshSessionFactory);

        try (Git git = Git.cloneRepository()
                .setURI(repositoryUrl)
                .setBranch(branch)
                .setCloneSubmodules(true)
                .setCredentialsProvider(getCredentials())
                .setDirectory(targetDirectory)
                .setCloneAllBranches(true)
                .call()) {
            // Cloning process completed successfully
            return git;
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public void initRepository(File targetDirectory) {
        try (Git git = Git.init()
                .setDirectory(targetDirectory)
                .call()) {
            // Cloning process completed successfully
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialCommitAndPush(File targetDirectory) {
        addAll(targetDirectory);
        commit(targetDirectory, "Initial commit");
        push(targetDirectory);
    }

    public void addAll(File targetDirectory) {
        try (Git git = Git.open(targetDirectory)) {
            git.add().addFilepattern(".").call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void commit(File targetDirectory, String message) {
        try (Git git = Git.open(targetDirectory)) {
            git.commit().setMessage(message).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void push(File targetDirectory) {
        try (Git git = Git.open(targetDirectory)) {
            git.push().setCredentialsProvider(getCredentials()).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addRemoteOrigin(File targetDirectory, String repositoryUrl) {
        try (Git git = Git.open(targetDirectory)) {
            git.remoteAdd().setName("origin").setUri(new URIish(repositoryUrl)).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addSubmodule(File targetDirectory, String repositoryUrl, String path) {
        try (Git git = Git.open(targetDirectory)) {
            git.submoduleAdd().setURI(repositoryUrl).setPath(path)
                    .setCredentialsProvider(getCredentials()).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void mergeBranches(Git git, List<String> branchesToMerge) {
        try {
            var mergeCommand = git.merge();
            var repository = git.getRepository();
            branchesToMerge.forEach(branch -> {
                Ref ref;
                try {
                    branch = "refs/remotes/origin/" + branch;
                    ref = repository.findRef(branch);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mergeCommand.include(ref);
            });
            mergeCommand.call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private CredentialsProvider getCredentials() {
        return new UsernamePasswordCredentialsProvider("", gitProperties.getSshKeyPassphrase());
    }

}
