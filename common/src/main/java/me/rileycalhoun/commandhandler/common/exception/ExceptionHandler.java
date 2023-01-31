package me.rileycalhoun.commandhandler.common.exception;

import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.core.BaseHandledCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ExceptionHandler {

    void handleException(CommandSubject subject,
                         CommandHandler handler,
                         HandledCommand command,
                         List<String> asImmutableList,
                         CommandContext context,
                         Throwable sanitizeStackTrace,
                         boolean b);

}
