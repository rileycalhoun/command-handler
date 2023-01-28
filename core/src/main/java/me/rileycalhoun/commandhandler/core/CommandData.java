package me.rileycalhoun.commandhandler.core;

import me.rileycalhoun.commandhandler.core.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public interface CommandData {

    @NotNull String getName();
    @Nullable String getDescription();
    @Nullable String[] getAliases();

    boolean isRootCommand();
    @Nullable CommandData getParent();
    @Nullable Method getMethod();
    @Nullable Object getInstance();
    @Nullable List<CommandData> getSubCommands();
    void execute(CommandContext context, String[] args) throws InvocationTargetException, IllegalAccessException;

}
