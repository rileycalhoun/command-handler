package me.rileycalhoun.commandhandler.core;

import me.rileycalhoun.commandhandler.core.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Map;

public interface CommandHandler {

    void registerCommands(@NotNull Object object);

    @NotNull CommandHelpWriter getHelpWriter();
    void setHelpWriter(@NotNull CommandHelpWriter writer);

    @NotNull ExceptionHandler getExceptionHandler();
    void setExceptionHandler(@NotNull ExceptionHandler exceptionHandler);

    @NotNull @UnmodifiableView Map<String, CommandData> getCommands();

    default void registerCommands(@NotNull Object... objects) {
        Arrays.stream(objects).forEach(this::registerCommands);
    }

}
