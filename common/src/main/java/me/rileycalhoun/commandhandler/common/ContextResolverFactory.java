package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.rileycalhoun.commandhandler.common.ParameterResolver.ContextResolver;

public interface ContextResolverFactory extends ResolverFactory<ContextResolver<?>> {

    @Nullable ContextResolver<?> create(@NotNull CommandParameter parameter, @NotNull HandledCommand command, @NotNull CommandHandler handler);

    static <T> @NotNull ContextResolverFactory forType(Class<T> type, ContextResolver<T> resolver) {
        return (parameter, command, handler) -> parameter.getType() == type ? resolver : null;
    }

    static <T> @NotNull ContextResolverFactory forHierarchyType(Class<T> type, ContextResolver<T> resolver) {
        return (parameter, command, handler) -> parameter.getType() == type
                || parameter.getType().isAssignableFrom(type) ? resolver : null;
    }

}
