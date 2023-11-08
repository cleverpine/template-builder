package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.dto.OperationResult;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;

public interface BackendService {
    void createService(BackendServiceInstructions instructions);
}
