package com.cleverpine.templatebuilder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class FileProperties {
    @Value("${file.location}")
    private String filesLocation;

}
