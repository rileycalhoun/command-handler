package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;

public interface CommandDispatcher {

    void execute(@NotNull CommandSubject subject, @NotNull CommandContext context, @NotNull String[] argsArray);
    void invokeCommand(HandledCommand command, ArgumentStack args, CommandSubject sender, CommandContext context) throws Throwable;

}
