package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandHelpWriter;
import me.rileycalhoun.commandhandler.core.CommandResolver;
import me.rileycalhoun.commandhandler.core.annotation.Command;
import me.rileycalhoun.commandhandler.core.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BaseCommandHandler implements CommandHandler {

    private final List<CommandData> commands = new ArrayList<>();
    private CommandHelpWriter writer;
    private CommandResolver resolver;

    public BaseCommandHandler () {
        this.writer = new BaseCommandHelpWriter();
        this.resolver = new BaseCommandResolver(this);
    }

    @Override
    public void registerCommands(Object obj) {
        Class<?> clazz = obj.getClass();
        // Check if the class has the command annotation attached
        if(clazz.isAnnotationPresent(Command.class)) {
            Command info = clazz.getAnnotation(Command.class);
            BaseCommandData command = new BaseCommandData(info, true, obj, null, null, this);
            for(Method m : Arrays.stream(clazz.getMethods()).filter(m -> m.isAnnotationPresent(SubCommand.class)).toList()) {
                SubCommand subCommandInfo = m.getAnnotation(SubCommand.class);
                CommandData subCommand = new BaseCommandData(subCommandInfo, false, null, m, command, this);
                command.addSubCommand(subCommand);
                commands.add(command);
            }
        } else {
            // Check methods inside class
            for(Method m : Arrays.stream(clazz.getMethods()).filter(m -> m.isAnnotationPresent(Command.class)).toList()) {
                Command info = m.getAnnotation(Command.class);
                CommandData command = new BaseCommandData(info, true, obj, m, null, this);
                commands.add(command);
            }
        }
    }

    @Override
    public @NotNull CommandHelpWriter getHelpWriter() {
        return writer;
    }

    @Override
    public void setHelpWriter(@NotNull CommandHelpWriter writer) {
        this.writer = writer;
    }

    @Override
    public @NotNull CommandResolver getCommandResolver() {
        return resolver;
    }

    @Override
    public void setCommandResolver(@NotNull CommandResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public @NotNull @UnmodifiableView List<CommandData> getCommands() {
        return commands;
    }

}
