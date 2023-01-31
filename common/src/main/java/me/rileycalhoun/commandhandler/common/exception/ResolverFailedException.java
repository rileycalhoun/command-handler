package me.rileycalhoun.commandhandler.common.exception;

import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.ParameterResolver;

public class ResolverFailedException extends CommandException {
    private final ParameterResolver<?, ?> resolver;
    private final Object input;
    private final CommandParameter parameter;
    private final Throwable parentCause;

    public ResolverFailedException(ParameterResolver<?, ?> resolver,
                                   Object input,
                                   CommandParameter parameter,
                                   Throwable parentCause) {
        this.resolver = resolver;
        this.input = input;
        this.parameter = parameter;
        this.parentCause = parentCause;
        initCause(parentCause);
    }

    public <T> ParameterResolver<?, T> getResolver() {
        return (ParameterResolver<?, T>) resolver;
    }

    public <T> T getInput() {
        return (T) input;
    }

    public CommandParameter getParameter() {
        return parameter;
    }

    public Throwable getParentCause() {
        return parentCause;
    }
}
