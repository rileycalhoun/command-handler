package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Supplier;

public interface ParameterResolver<A, R> {

    @Deprecated
    R resolve(@NotNull @Unmodifiable A args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable;

    interface ContextResolver<T> extends ParameterResolver<List<String>, T> {

        T resolve(@NotNull @Unmodifiable List<String> args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable;

        static <T> ContextResolver<T> of(@NotNull T value) {
            return (args, subject, parameter) -> value;
        }

        static <T> ContextResolver<T> of(@NotNull Supplier<T> value) {
            return (args, subject, parameter) -> value.get();
        }

    }

    interface ValueResolver<T> extends ParameterResolver<ArgumentStack, T> {

        @Contract(mutates = "param1")
        T resolve(@NotNull ArgumentStack args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter) throws Throwable;

    }

}
