package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.*;
import me.rileycalhoun.commandhandler.core.exception.CommandException;
import me.rileycalhoun.commandhandler.core.exception.InvalidCommandException;
import me.rileycalhoun.commandhandler.core.exception.MissingSubCommandException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseCommandDispatcher implements CommandDispatcher {

    protected final CommandHandler commandHandler;
    private final List<String> sanitizedPaths = new ArrayList<>();

    public BaseCommandDispatcher(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        sanitizePath(getClass());
        sanitizePath(commandHandler.getClass());
    }

    @Override
    public void execute(CommandContext context, String... argsArray) {
        ArgumentStack args = new LinkedArgumentStack(argsArray);
        CommandData command = null;
        try {
            String commandName = args.pop();
            command = commandHandler.getCommands().get(commandName);
            if(command == null)
                throw new InvalidCommandException(commandName);

            if(!command.getSubCommands().isEmpty() || command.getMethod() == null)
                command = getCommand(command, args);

            invokeCommand(command, args, context);
        } catch (Throwable t) {
            commandHandler.getExceptionHandler().handleException(
                    context.getSubject(),
                    commandHandler,
                    command,
                    context,
                    t.getCause() != null ? t.getCause() : t
            );
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void invokeCommand(CommandData command, ArgumentStack args, CommandContext context) throws Throwable {
        try {
            command.getMethod().invoke(command.getInstance(), context, args);
        } catch (Throwable throwable) {
            throw sanitizeStackTrace(throwable);
        }
    }

    protected Throwable sanitizeStackTrace(Throwable throwable) {
        List<StackTraceElement> elements = new ArrayList<>();
        Collections.addAll(elements, throwable.getStackTrace());
        elements.removeIf(t -> t.getClassName().equals(getClass().getName()));
        elements.removeIf(t -> t.getClassName().equals(BaseCommandDispatcher.class.getName()));
        elements.removeIf(t -> t.getClassName().equals(Method.class.getName()));
        elements.removeIf(t -> t.getClassName().equals(BaseCommandHandler.class.getName()));
        elements.removeIf(t -> sanitizedPaths.contains(t.getClassName()));
        throwable.setStackTrace(elements.toArray(new StackTraceElement[0]));
        return throwable;
    }

    protected void sanitizePath(@NotNull Class<?> type) {
        sanitizedPaths.add(type.getName());
    }

    @SuppressWarnings("ConstantConditions")
    private CommandData getCommand(CommandData parent, ArgumentStack args) {
        String name = null;
        try {
            name = args.pop();
            CommandData command = parent.getSubCommands().get(name);
            if(command == null) throw new InvalidCommandException(name);
            if(((BaseCommandData) command).getMethod() != null) return command;
            return getCommand(command, args);
        } catch (Throwable t) {
            if(name == null)
                throw new MissingSubCommandException();

            throw new InvalidCommandException(name);
        }
    }

}
