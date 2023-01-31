package me.rileycalhoun.commandhandler.common.core;

import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.ParameterValidator;
import org.jetbrains.annotations.NotNull;

public class NumberRangeValidator implements ParameterValidator<Number> {

    public static ParameterValidator<Number> INSTANCE;

    @Override
    public void validate(Number value, @NotNull CommandParameter parameter, @NotNull CommandSubject subject) throws Throwable {

    }
}
