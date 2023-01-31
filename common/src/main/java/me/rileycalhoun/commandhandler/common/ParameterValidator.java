package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;

public interface ParameterValidator<T> {

    void validate(T value, @NotNull CommandParameter parameter, @NotNull CommandSubject subject) throws Throwable;

}
