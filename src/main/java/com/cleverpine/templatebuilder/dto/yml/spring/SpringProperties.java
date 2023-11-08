package com.cleverpine.templatebuilder.dto.yml.spring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpringProperties {

    private ApplicationProperty application;

    private ConfigProperty config;

}
