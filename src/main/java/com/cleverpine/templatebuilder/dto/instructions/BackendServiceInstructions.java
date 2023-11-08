package com.cleverpine.templatebuilder.dto.instructions;

import com.cleverpine.templatebuilder.dto.instructions.enums.BackendTemplateType;
import com.cleverpine.templatebuilder.dto.instructions.enums.DatabaseType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BackendServiceInstructions extends BaseServiceInstructions {

    public BackendTemplateType templateType;

    public DatabaseType databaseType;

    public RepoInstructions apiRepository;

}
