package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonExtendable
public interface ResolverFactory<R extends ParameterResolver<?, ?>> {

    @Nullable R create(@NotNull CommandParameter parameter,
                       @NotNull HandledCommand command,
                       @NotNull CommandHandler handler);

}
