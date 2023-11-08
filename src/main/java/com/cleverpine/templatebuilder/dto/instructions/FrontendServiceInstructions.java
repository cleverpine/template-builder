package com.cleverpine.templatebuilder.dto.instructions;

import com.cleverpine.templatebuilder.dto.instructions.enums.FrontendTemplateType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class FrontendServiceInstructions extends BaseServiceInstructions {

    public final Set<String> apiNames;
    public FrontendTemplateType templateType;

    public FrontendServiceInstructions() {
        this.apiNames = new HashSet<>();
    }

    public FrontendServiceInstructions addApi(String string) {
        apiNames.add(string);
        return this;
    }
}
