package me.rileycalhoun.commandhandler.common.core;

import me.rileycalhoun.commandhandler.common.*;
import me.rileycalhoun.commandhandler.common.exception.*;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import static me.rileycalhoun.commandhandler.common.core.Utils.n;

public abstract class BaseDispatcher implements CommandDispatcher {

    public static final Pattern SPLIT = Pattern.compile(" ");
    protected final BaseCommandHandler handler;
    private final List<String> sanitizedPaths = new ArrayList<>();

    public BaseDispatcher(BaseCommandHandler handler) {
        this.handler = handler;
        sanitizePath(getClass());
        sanitizePath(handler.getClass());
    }

    @Override
    public void execute(@NotNull CommandSubject subject, @NotNull CommandContext context, @NotNull String[] argsArray) {
        ArgumentStack args = new LinkedArgumentStack(splitWithoutQuotes(argsArray), handler);
        BaseHandledCommand command = null;
        try {
            final String commandName = args.pop();
            command = (BaseHandledCommand) handler.getCommands().get(commandName);
            if (command == null)
                throw new InvalidCommandException(commandName);
            if (!command.getSubcommands().isEmpty() || command.getMethodHandle() == null) {
                command = (BaseHandledCommand) getCommand(command, args);
            }

            invokeCommand(command, args, subject, context);
        } catch (Throwable t) {
            if (t instanceof InvalidCommandException && command != null && command.fallback != null) {
                List<Object> fallbackArgs = new ArrayList<>();
                for (Parameter parameter : command.fallbackParameters) {

                    Class<?> type = parameter.getType();
                    if (ArgumentStack.class.isAssignableFrom(type))
                        fallbackArgs.add(args);
                    else if (CommandSubject.class.isAssignableFrom(type))
                        fallbackArgs.add(subject);
                    else if (type == String[].class)
                        fallbackArgs.add(argsArray);
                    else if (HandledCommand.class.isAssignableFrom(type))
                        fallbackArgs.add(command);
                    else if (CommandHandler.class.isAssignableFrom(type))
                        fallbackArgs.add(handler);
                    else if (parameter.getType() == List.class)
                        fallbackArgs.add(args.asImmutableList());
                }
                try {
                    command.fallback.invokeWithArguments(fallbackArgs);
                    return;
                } catch (Throwable throwable) {
                    throw new IllegalStateException("Cannot invoke @CatchInvalid method", throwable);
                }
            }

            handler.getExceptionHandler().handleException(
                    subject,
                    handler,
                    command,
                    args.asImmutableList(),
                    context,
                    sanitizeStackTrace(t),
                    false);
        }
    }

    @Override
    public void invokeCommand(HandledCommand command, ArgumentStack args, CommandSubject sender, CommandContext context) throws Throwable {
        for (CommandCondition condition : command.getConditions()) {
            try {
                condition.test(sender, args.asImmutableList(), command, context);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        List<Object> invokedArgs = new ArrayList<>();
        for (CommandParameter parameter : command.getParameters()) {
            if (parameter.isSwitch()) {
                boolean provided = args.remove(handler.switchPrefix + parameter.getSwitchName());
                if (!provided) invokedArgs.add(parameter.getDefaultSwitch());
                else {
                    invokedArgs.add(true);
                }
                continue;
            }
            if (parameter.isFlag()) {
                String look = handler.flagPrefix + parameter.getFlagName();
                int index = args.indexOf(look);
                args.remove(look);
                if (index == -1) {
                    if (parameter.isOptional()) {
                        if (parameter.getDefaultValue() != null) {
                            args.add(parameter.getDefaultValue());
                            index = args.size() - 2;
                        } else {
                            invokedArgs.add(null);
                            continue;
                        }
                    } else {
                        throw new MissingParameterException(parameter, parameter.getResolver());
                    }
                }

                ParameterResolver.ValueResolver<?> resolver = (ParameterResolver.ValueResolver<?>) parameter.getResolver();
                if (args.size() <= 0) {
                    throw new MissingParameterException(parameter, resolver);
                }

                ArgumentStack newStack = args.subList(index, index + 1);
                args.removeAll(newStack);

                try {
                    invokedArgs.add(parameter.getMethodIndex(), resolver.resolve(newStack, sender, parameter));
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            Object result = null;
            ParameterResolver<?, ?> parameterResolver = parameter.getResolver();
            try {
                if (parameter.getMethodIndex() == 0) {
                    if (CommandSubject.class.isAssignableFrom(parameter.getType())) {
                        invokedArgs.add(sender);
                        continue;
                    } else if (isPossibleSender(parameter.getType())) {
                        invokedArgs.add(handlePossibleSender(parameter.getType(), context));
                        continue;
                    }
                }
                if (parameterResolver instanceof ParameterResolver.ValueResolver) {
                    ParameterResolver.ValueResolver<?> resolver = (ParameterResolver.ValueResolver<?>) parameterResolver;
                    if (args.size() == 0) {
                        if (parameter.getDefaultValue() == null && parameter.isOptional()) {
                            invokedArgs.add(parameter.getMethodIndex(), null);
                            continue;
                        } else {
                            if (parameter.getDefaultValue() != null) {
                                args.add(parameter.getDefaultValue());
                            } else
                                throw new MissingParameterException(parameter, parameterResolver);
                        }
                    }
                    result = resolver.resolve(args, sender, parameter);
                } else {
                    ParameterResolver.ContextResolver<?> resolver = (ParameterResolver.ContextResolver<?>) parameterResolver;
                    result = resolver.resolve(args.asImmutableList(), sender, parameter);
                }
                invokedArgs.add(parameter.getMethodIndex(), result);
            } catch (Throwable throwable) {
                if (throwable instanceof CommandException) throw sanitizeStackTrace(throwable);
                throw sanitizeStackTrace(new ResolverFailedException(
                        parameterResolver,
                        result,
                        parameter,
                        throwable
                ));
            }
        }
        command.getExecutor().execute(() -> {
            try {
                Object result = n(((BaseHandledCommand) command).getMethodHandle()).invokeWithArguments(invokedArgs);
                try {
                    ((BaseHandledCommand) command).responseHandler.handleResponse(result, sender, command, context);
                } catch (Throwable t) {
                    throw sanitizeStackTrace(new ResponseFailedException(t, ((BaseHandledCommand) command).responseHandler, result));
                }
            } catch (Throwable throwable) {
                if (command.isAsync())
                    handler.getExceptionHandler().handleException(
                            sender, handler, command, args.asImmutableList(), context, sanitizeStackTrace(throwable),
                            true);
                else
                    throw sneakyThrow(sanitizeStackTrace(throwable)); // delegate to the synchronous handler
            }
        });
    }

    private HandledCommand getCommand(HandledCommand parent, ArgumentStack stack) {
        String name = null;
        try {
            name = stack.pop();
            HandledCommand command = parent.getSubcommands().get(name);
            if (command == null) {
                throw new InvalidCommandException(name);
            } if (((BaseHandledCommand) command).getMethodHandle() != null)
                return command;
            return getCommand(command, stack);
        } catch (Throwable t) {
            throw new InvalidCommandException(name);
        }
    }

    protected Throwable sanitizeStackTrace(Throwable throwable) {
        List<StackTraceElement> elements = new ArrayList<>();
        Collections.addAll(elements, throwable.getStackTrace());
        elements.removeIf(t -> t.getClassName().equals(getClass().getName()));
        elements.removeIf(t -> t.getClassName().equals(BaseDispatcher.class.getName()));
        elements.removeIf(t -> t.getClassName().equals(MethodHandle.class.getName()));
        elements.removeIf(t -> t.getClassName().equals(BaseCommandHandler.class.getName()));
        elements.removeIf(t -> sanitizedPaths.contains(t.getClassName()));
        throwable.setStackTrace(elements.toArray(new StackTraceElement[0]));
        return throwable;
    }

    protected void sanitizePath(@NotNull Class<?> type) {
        sanitizedPaths.add(type.getName());
    }

    protected abstract boolean isPossibleSender(@NotNull Class<?> v);

    protected abstract Object handlePossibleSender(Class<?> type, @NotNull CommandContext context);

    protected static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) throw new NullPointerException("t");
        return sneakyThrow0(t);
    }

    private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }

    public static String[] splitWithoutQuotes(String[] split) {
        List<String> result = new ArrayList<>();
        boolean close = false;
        int index = -1;
        for (int i = 0; i < split.length; i++) {
            String t = split[i];
            if (t.equals("\"\"")) {
                result.add("");
                continue;
            }
            if (t.length() >= 2 && t.indexOf('"') == 0) {
                close = true;
                index = i;
//                continue;
            }
            if (t.lastIndexOf('"') == t.length() - 1 && close && !t.endsWith("\\\"")) {
                StringJoiner joiner = new StringJoiner(" ");
                for (int j = 0; j < split.length; j++) {
                    if (j >= index && j <= i)
                        joiner.add(split[j]);
                }
                result.add((String) joiner.toString().subSequence(1, joiner.length() - 1));
                close = false;
                continue;
            }
            if (!close) {
                result.add(t);
                close = false;
            }
        }
        result.replaceAll(t -> t.replace("\\\"", "\""));
        return result.toArray(new String[0]);
    }

}
