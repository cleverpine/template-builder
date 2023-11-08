package com.cleverpine.templatebuilder.service;

import com.cleverpine.templatebuilder.config.FileProperties;
import com.cleverpine.templatebuilder.exceptions.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class FileService {

    public static final String DIR_DELEMITER = "/";
    public static final String TMP_DIRECTORY = "/tmp";
    private final FileProperties fileProperties;

    private boolean directoryConfigured = false;

    public boolean directoryConfigured() {
        return directoryConfigured;
    }

    public boolean setTargetDirectory(String targetDirectory) {
        fileProperties.setFilesLocation(targetDirectory);
        checkDirectory();
        return directoryConfigured;
    }

    public void checkDirectory() {
        var targetDirectory = getTargetDirectory();
        var targetDirectoryFile = new File(targetDirectory);
        if (!targetDirectoryFile.exists()) {
            directoryConfigured = targetDirectoryFile.mkdirs();
        } else if (!targetDirectoryFile.isDirectory()) {
            directoryConfigured = false;
            System.out.println("Target directory is not a directory: " + targetDirectory);
        } else if (!targetDirectoryFile.canRead() || !targetDirectoryFile.canWrite()) {
            directoryConfigured = false;
            System.out.println("Target directory is not readable or writable: " + targetDirectory);
        } else {
            directoryConfigured = true;
        }
    }

    public String getTargetDirectory() {
        var directory = fileProperties.getFilesLocation();
        if (directory == null || directory.isBlank()) {
            directory = TMP_DIRECTORY;
        } else if (!directory.startsWith(DIR_DELEMITER)) {
            directory = DIR_DELEMITER + directory;
        }
        return System.getProperty("user.dir") + directory;
    }

    public void createDirectory(String directoryPath) {
        Path path = getPath(directoryPath);
        try {
            Files.createDirectories(path);
            setTargetDirectory(path.toString());
        } catch (IOException e) {
            throw new ApplicationError(e.getMessage());
        }
    }

    public File getFile(String... relativePath) {
        return getPath(relativePath).toFile();
    }

    public Path getPath(String... relativePath) {
        return Paths.get(getTargetDirectory(), relativePath);
    }

    public void deleteFolderAndContents(File folder) {
        try (Stream<Path> paths = Files.walk(folder.toPath())) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (NoSuchFileException e) {
            // ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
