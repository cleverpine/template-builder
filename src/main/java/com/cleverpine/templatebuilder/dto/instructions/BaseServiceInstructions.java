package com.cleverpine.templatebuilder.dto.instructions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseServiceInstructions {

    private String name;

    private String description;

    private RepoInstructions repository;

}
