package me.rileycalhoun.commandhandler.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.List;

public interface CommandHandler {

    void registerCommands(@NotNull Object object);

    @NotNull CommandHelpWriter getHelpWriter();
    void setHelpWriter(@NotNull CommandHelpWriter writer);

    @NotNull CommandResolver getCommandResolver();
    void setCommandResolver(@NotNull CommandResolver resolver);

    @NotNull @UnmodifiableView List<CommandData> getCommands();

    default void registerCommands(@NotNull Object... objects) {
        Arrays.stream(objects).forEach(this::registerCommands);
    }

}
