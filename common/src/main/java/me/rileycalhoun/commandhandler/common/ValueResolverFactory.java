package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.rileycalhoun.commandhandler.common.ParameterResolver.ValueResolver;

public interface ValueResolverFactory extends ResolverFactory<ValueResolver<?>> {

    @Nullable ValueResolver<?> create(@NotNull CommandParameter parameter,
                                      @NotNull HandledCommand command,
                                      @NotNull CommandHandler handler);

    static <T> @NotNull ValueResolverFactory forType(Class<T> type, ValueResolver<T> resolver) {
        return (parameter, command, handler) -> parameter.getType() == type ? resolver : null;
    }

    static <T> @NotNull ValueResolverFactory forHierarchyType(Class<T> type, ValueResolver<T> resolver) {
        return (parameter, command, handler) -> parameter.getType() == type
                || parameter.getType().isAssignableFrom(type) ? resolver : null;
    }

}
