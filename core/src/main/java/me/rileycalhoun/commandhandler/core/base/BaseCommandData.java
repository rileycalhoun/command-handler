package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandResolver;
import me.rileycalhoun.commandhandler.core.annotation.Command;
import me.rileycalhoun.commandhandler.core.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class BaseCommandData implements CommandData {

    private final String name, description;
    private final boolean isRootCommand;
    private final String[] aliases;
    private final Method method;
    private final List<CommandData> subCommands;
    private final CommandData parent;
    private final Object instance;
    private final CommandHandler handler;

    public BaseCommandData (Command info, boolean isRootCommand, Object instance, Method method, CommandData parent, CommandHandler handler)
    {
        this.name = info.name();
        this.description = info.description();
        this.isRootCommand = isRootCommand;
        this.aliases = info.aliases();
        this.method = method;
        this.subCommands = new ArrayList<>();
        this.parent = parent;
        this.instance = instance;
        this.handler = handler;
    }

    public BaseCommandData (@NotNull SubCommand info, boolean isRootCommand, @Nullable Object instance, @Nullable Method method, @Nullable CommandData parent, CommandHandler handler)
    {
        this.name = info.name();
        this.description = info.description();
        this.isRootCommand = isRootCommand;
        this.aliases = info.aliases();
        this.method = method;
        this.subCommands = new ArrayList<>();
        this.parent = parent;
        this.instance = instance;
        this.handler = handler;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public @Nullable String[] getAliases() {
        return aliases;
    }

    @Override
    public boolean isRootCommand() {
        return isRootCommand;
    }

    @Override
    public @Nullable CommandData getParent() {
        return parent;
    }

    @Override
    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public @Nullable Object getInstance() {
        return instance;
    }

    @Override
    public @Nullable List<CommandData> getSubCommands() {
        return subCommands;
    }

    @Override
    public void execute(CommandContext context, String[] args) throws InvocationTargetException, IllegalAccessException {
        if(this.getMethod() != null) {
            if(this.getInstance() == null && this.getParent() != null)
                this.getMethod().invoke(this.getParent().getInstance(), context, args);
            else this.getMethod().invoke(this.getInstance(), context, args);
        } else {
            if(args.length == 0) context.getSubject().reply(new BaseCommandHelpWriter().write(getSubCommands()));

            String name = args[0];
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            CommandData data = handler.getCommandResolver().find(name, getSubCommands());
            data.execute(context, newArgs);
        }
    }

    public void addSubCommand(@NotNull CommandData subCommand) {
        this.subCommands.add(subCommand);
    }

}
