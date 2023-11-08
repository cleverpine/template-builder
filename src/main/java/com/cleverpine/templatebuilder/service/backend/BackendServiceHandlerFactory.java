package com.cleverpine.templatebuilder.service.backend;

import com.cleverpine.templatebuilder.config.service.backend.BackendServiceConfig;
import com.cleverpine.templatebuilder.config.service.backend.QuarkusServiceConfig;
import com.cleverpine.templatebuilder.config.service.backend.SpringServiceConfig;
import com.cleverpine.templatebuilder.dto.instructions.BackendServiceInstructions;
import com.cleverpine.templatebuilder.service.backend.quarkus.QuarkusRenameHandler;
import com.cleverpine.templatebuilder.service.backend.quarkus.QuarkusTemplatePomHandler;
import com.cleverpine.templatebuilder.service.backend.spring.SpringRenameHandler;
import com.cleverpine.templatebuilder.service.backend.spring.SpringTemplatePomHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BackendServiceHandlerFactory {

    private final QuarkusServiceConfig quarkusServiceConfig;

    private final SpringServiceConfig springServiceConfig;

    private final QuarkusTemplatePomHandler quarkusTemplatePomHandler;

    private final SpringTemplatePomHandler springTemplatePomHandler;

    private final QuarkusRenameHandler quarkusRenameHandler;

    private final SpringRenameHandler springRenameHandler;

    public Holder getHandlers(BackendServiceInstructions instructions) {
        switch (instructions.templateType) {
            case SPRING -> {
                return new Holder(
                        springServiceConfig,
                        springTemplatePomHandler,
                        springRenameHandler);
            }
            case QUARKUS -> {
                return new Holder(
                        quarkusServiceConfig,
                        quarkusTemplatePomHandler,
                        quarkusRenameHandler);
            }
            default -> throw new IllegalStateException("Invalid template type in backend instructions");
        }
    }

    public record Holder(BackendServiceConfig config,
                         TemplatePomHandler pomHandler,
                         RenameHandler renameHandler) {
    }

}
