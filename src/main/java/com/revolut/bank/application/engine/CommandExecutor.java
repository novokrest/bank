package com.revolut.bank.application.engine;

import com.revolut.bank.application.engine.error.ValidationError;
import com.revolut.bank.application.engine.error.factory.ApplicationErrorFactory;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.container.AsyncResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executor of commands
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Service
public class CommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    private static final CommandResult<?> TECHNICAL_ERROR = CommandResult.applicationError(ApplicationErrorFactory.technicalError());

    private final ExecutorService executorService;

    public CommandExecutor(int threadsCount) {
        this.executorService = Executors.newFixedThreadPool(threadsCount);
    }

    /**
     * Executes command asynchronously
     *
     * @param command command to execute
     * @param request request
     * @param asyncResponse object to set command execution result
     * @param <RequestT> request's type
     * @param <ResponseT> response's type
     */
    public <RequestT, ResponseT> void execute(
            @Nonnull Command<RequestT, ResponseT> command,
            @Nonnull RequestT request,
            @Nonnull AsyncResponse asyncResponse
    ) {
        CompletableFuture.supplyAsync(() -> executeCommand(command, request), executorService)
                .thenApply(asyncResponse::resume)
                .exceptionally(error -> {
                    log.error("Error occurred during command execution", error);
                    return asyncResponse.resume(TECHNICAL_ERROR);
                });
    }

    @Nonnull
    private <RequestT, ResponseT> CommandResult<ResponseT> executeCommand(
            @Nonnull Command<RequestT, ResponseT> command,
            @Nonnull RequestT request
    ) {
        List<ValidationError> validationErrors = command.getValidationRules().validate(request);
        return validationErrors.isEmpty()
                ? command.execute(request)
                : CommandResult.validationErrors(validationErrors);
    }

}
