package me.rileycalhoun.commandhandler.core;

import me.rileycalhoun.commandhandler.core.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface CommandData {

    String getName();
    @Nullable String getDescription();
    @Nullable String[] getAliases();

    boolean isRootCommand();
    @Nullable CommandData getParent();
    @Nullable Method getMethod();
    @Nullable Object getInstance();
    @NotNull Map<String, CommandData> getSubCommands();

}
