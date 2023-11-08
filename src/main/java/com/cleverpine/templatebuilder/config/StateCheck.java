package com.cleverpine.templatebuilder.config;

import com.cleverpine.templatebuilder.service.FileService;
import com.cleverpine.templatebuilder.service.GitService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StateCheck {

    private final FileService fileService;

    private final GitService gitService;

    @PostConstruct
    public void onApplicationEvent() {
        gitService.checkConnection();
        fileService.checkDirectory();
    }
}
