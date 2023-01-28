package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.*;
import me.rileycalhoun.commandhandler.core.exception.CommandException;
import me.rileycalhoun.commandhandler.core.exception.InvalidCommandException;
import me.rileycalhoun.commandhandler.core.exception.MissingSubCommandException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class BaseCommandDispatcher implements CommandDispatcher {

    protected final CommandHandler commandHandler;

    public BaseCommandDispatcher(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(CommandContext context, String[] argsArray) {
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
                    t
            );
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void invokeCommand(CommandData command, ArgumentStack args, CommandContext context) {
        try {
            if (command.getInstance() == null && command.getParent() != null)
                command.getMethod().invoke(command.getParent().getInstance(), context, args);
            else command.getMethod().invoke(command.getInstance(), context, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
