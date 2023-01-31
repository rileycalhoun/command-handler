package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public interface HandledCommand {

    String getName();
    @Nullable String getDescription();
    @NotNull String getUsage();
    @NotNull List<String> getAliases();

    @Nullable HandledCommand getParent();
    @NotNull CommandPermission getPermission();
    boolean isRootCommand();
    @NotNull @Unmodifiable List<CommandParameter> getParameters();
    @NotNull List<CommandCondition> getConditions();
    @NotNull @Unmodifiable Map<String, HandledCommand> getSubcommands();
    boolean isAsync();
    boolean isPrivate();
    <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation);
    boolean hasAnnotation(@NotNull Class<? extends Annotation> annotation);
    @NotNull CommandHandler getCommandHandler();
    @Nullable Executor getExecutor();

}
