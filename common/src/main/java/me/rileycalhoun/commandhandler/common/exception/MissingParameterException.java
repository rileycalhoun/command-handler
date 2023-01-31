package me.rileycalhoun.commandhandler.common.exception;

import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.ParameterResolver;

public class MissingParameterException extends CommandException {

    private final CommandParameter parameter;
    private final ParameterResolver<?, ?> resolver;

    public MissingParameterException(CommandParameter parameter, ParameterResolver<?, ?> resolver) {
        this.parameter = parameter;
        this.resolver = resolver;
    }

    public CommandParameter getParameter() {
        return parameter;
    }

    public ParameterResolver<?, ?> getResolver() {
        return resolver;
    }
}
