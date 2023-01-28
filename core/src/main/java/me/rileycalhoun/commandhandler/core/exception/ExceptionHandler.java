package me.rileycalhoun.commandhandler.core.exception;

import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandSubject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ExceptionHandler {

    void handleException(@NotNull CommandSubject sender,
                         @NotNull CommandHandler commandHandler,
                         @Nullable CommandData command,
                         @NotNull CommandContext context,
                         @NotNull Throwable throwable);

}
