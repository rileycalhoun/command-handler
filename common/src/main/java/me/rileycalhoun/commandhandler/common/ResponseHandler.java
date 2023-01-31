package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;

public interface ResponseHandler<T> {

    ResponseHandler<?> VOID = (response, sender, command, context) -> {};

    void handleResponse(T response,
                        @NotNull CommandSubject subject,
                        @NotNull HandledCommand command,
                        @NotNull CommandContext context);

}
